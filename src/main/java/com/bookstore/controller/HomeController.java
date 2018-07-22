package com.bookstore.controller;

import java.net.URL;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;

import com.bookstore.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.bookstore.domain.Book;
import com.bookstore.domain.Order;
import com.bookstore.domain.Review;
import com.bookstore.domain.User;
import com.bookstore.domain.UserShipping;
import com.bookstore.domain.security.PasswordResetToken;
import com.bookstore.domain.security.Role;
import com.bookstore.domain.security.UserRole;
import com.bookstore.repository.ReviewRepository;
import com.bookstore.s3.service.S3Services;
import com.bookstore.service.impl.UserSecurityService;
import com.bookstore.utility.IndiaConstants;
import com.bookstore.utility.MailConstructor;
import com.bookstore.utility.SecurityUtility;

@Controller
public class HomeController {
	private static final Logger LOG = LoggerFactory.getLogger(HomeController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private BookService bookService;

	@Autowired
	private UserSecurityService userSecurityService;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private MailConstructor mailContructor;

	@Autowired
	private OrderService orderService;

	@Autowired
	private CartItemService cartItemService;

	@Autowired
	private UserShippingService userShippingService;
	
	@Autowired
	private ReviewRepository reviewRepository;
	
	@Autowired
	private S3Services s3Services;

	@Value("${jsa.s3.profileUploadfile}")
	private String profileUploadfile;

	@Autowired
	private AmazonS3 s3client;

	@Autowired
	private StorePointService storePointService;

	@Value("${jsa.s3.bucket}")
	private String bucketName;
	
	public static final String DEFAULT_USER_IMAGE="https://s3.ap-south-1.amazonaws.com/bookstore-book-image/defaultuser.png";


	@RequestMapping("/")
	public String indexPage() {
		return "index";
	}
	
	@RequestMapping("/profile")
	public String getProfileInfo(){
		
		return "profile";
	}

	@RequestMapping("/myAccount")
	public String myAccount() {
		return "myAccount";
	}
	
	@RequestMapping("/login")
	public String login(Model model) {
		model.addAttribute("classActiveLogin", true);
		return "myAccount";
	}

	@RequestMapping(value = "/createAccount", method = RequestMethod.POST)
	public String createAccountFormSubmission(Model model, HttpServletRequest request,
			@ModelAttribute("username") String username, @ModelAttribute("email") String userEmail, Locale locale)
			throws Exception {

		// Fetch Username
		// Fetch EMailID

		// Search emailID and Username in DB => If found show a message telling User
		// already exist.

		// If not found =>

		// Generate Token
		// EMail the token to the user's email ID

		// Save the PasswordResetToken to the repository.
		LOG.info("::::::::::::::::::::: Inside createAccountFormSubmission :::::::::::::::::::::");

		model.addAttribute("classActiveNewAccount", true);
		model.addAttribute("email", userEmail);
		model.addAttribute("username", username);

		if (userService.findByUsername(username) != null) {
			model.addAttribute("userAlreadyExist", true);
			return "myAccount";
		}

		if (userService.findByEmail(userEmail) != null) {
			model.addAttribute("emailAlreadyExist", true);
			return "myAccount";
		}

		User user = new User();
		user.setUsername(username);
		user.setEmail(userEmail);

		String password = SecurityUtility.randomPassword();
		String encryptedPassword = SecurityUtility.passwordEncoder().encode(password);
		user.setPassword(encryptedPassword);

		Role role = new Role();
		role.setRoleId(1);
		role.setName("ROLE_USER");

		Set<UserRole> userRoles = new HashSet<>();
		userRoles.add(new UserRole(user, role));

		userService.createUser(user, userRoles);
		LOG.info("New user has been created: userName:: " + username);

		String token = UUID.randomUUID().toString();
		userService.createPasswordResetTokenForUser(user, token);

		String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

		LOG.info("request.getServerName() ::" + request.getServerName());
		LOG.info("request.getServerPort() ::" + request.getServerPort());
		LOG.info("request.getContextPath() :: " + request.getContextPath());

		SimpleMailMessage email = mailContructor.constructSimpleResetTokenEmail(appUrl, request.getLocale(), token,
				user, password);
		mailSender.send(email);
		LOG.info("Email has been sent");

		model.addAttribute("emailSent", "true");

		return "myACcount"; // return view name
	}

	@RequestMapping("/createAccount")
	public String createAccount(Model model, @RequestParam("token") String token, Locale locale) {

		PasswordResetToken passToken = userService.getPasswordResetToken(token);

		if (passToken == null) {
			LOG.info("<<<<<<<======================= passToken is null ======================>>>>>>");
			String message = "Invalid Token";
			model.addAttribute("message", message);
			return "redirect:/badRequest";
		}

		LOG.info("passToken ::::::::::: " + passToken.getToken());

		User user = passToken.getUser();
		String username = user.getUsername();
		String email = user.getEmail();

		LOG.info("DATA::::::::::::::: username: " + username + ", email: " + email);

		UserDetails userDetails = userSecurityService.loadUserByUsername(username);
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
				userDetails.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(authentication);
		model.addAttribute("user", user);
		model.addAttribute("classActiveEdit", true);
		return "myProfile";
	}

	@RequestMapping("/forgetPassword")
	public String forgetPassword(Model model, HttpServletRequest request, @ModelAttribute("email") String userEmail) {

		LOG.info("::::::::::: inside forgetPassword() method :::::::::::");

		model.addAttribute("classActiveForgetPassword", true);

		if (null == userEmail || userEmail.equals("")) {
			model.addAttribute("emailNotFound", true);
			LOG.info("::::::::::: emailNotFound :::::::::::");
			return "myAccount";
		}

		User user = userService.findByEmail(userEmail);
		if (null == user) {
			model.addAttribute("noUserFound", true);
			LOG.info("No user found with this email.");
			return "myAccount";
		}

		String password = SecurityUtility.randomPassword();
		String encryptedPassword = SecurityUtility.passwordEncoder().encode(password);
		user.setPassword(encryptedPassword);

		LOG.info(":::::::::: Username :: " + user.getUsername());

		userService.save(user);

		String token = UUID.randomUUID().toString();
		userService.createPasswordResetTokenForUser(user, token);

		String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

		SimpleMailMessage email = mailContructor.constructSimpleForgetPasswordEmail(appUrl, request.getLocale(), token,
				user, password);
		mailSender.send(email);
		LOG.info("An email has been sent");

		model.addAttribute("EmailSent", true);
		return "myAccount";
	}

	@RequestMapping(value = "/updateUserInfo", method = RequestMethod.POST)
	public String updateUserInfo(Model model, @ModelAttribute("user") User user,
			@ModelAttribute("newPasssword") String newPasssword) throws Exception {

		User currentUser = userService.findById(user.getId());

		if (null == currentUser) {
			model.addAttribute("classActiveEdit", true);
			throw new Exception("User not found.");

		}

		/* check if email already exist */

		if (userService.findByEmail(user.getEmail()) != null) {
			if (userService.findByEmail(user.getEmail()).getId() != currentUser.getId()) {
				model.addAttribute("emailExists", true);
				model.addAttribute("classActiveEdit", true);
				return "myProfile";
			}
		}

		/* check if username already exist */

		if (userService.findByUsername(user.getUsername()) != null) {
			if (userService.findByUsername(user.getUsername()).getId() != currentUser.getId()) {
				model.addAttribute("userExists", true);
				model.addAttribute("classActiveEdit", true);
				return "myProfile";
			}
		}

		// Update Password

		if (null != newPasssword && !newPasssword.isEmpty() && !newPasssword.equals("")) {
			BCryptPasswordEncoder passwordEncoder = SecurityUtility.passwordEncoder();
			String dbPasword = currentUser.getPassword();

			LOG.info("============checking if password matches-======= "
					+ passwordEncoder.matches(user.getPassword(), dbPasword));

			if (passwordEncoder.matches(user.getPassword(), dbPasword)) {
				currentUser.setPassword(passwordEncoder.encode(newPasssword));
			} else {
				model.addAttribute("passwordNotMatched", true);
				model.addAttribute("classActiveEdit", true);
				return "myProfile";
			}
		}

		LOG.info("========First Name: " + user.getFirstName());
		LOG.info("========Last Name: " + user.getLastName());
		LOG.info("========UserName: " + user.getUsername());
		LOG.info("========Email: " + user.getEmail());

		currentUser.setFirstName(user.getFirstName());
		currentUser.setLastName(user.getLastName());
		currentUser.setUsername(user.getUsername());
		currentUser.setEmail(user.getEmail());

		userService.save(currentUser);
	
		model.addAttribute("EditUser", true);
		model.addAttribute("updateUserInfo", true);
		model.addAttribute("user", currentUser);
		model.addAttribute("classActiveEdit", true);

		/* Set current Session for new user. */

		UserDetails userDetails = userSecurityService.loadUserByUsername(currentUser.getUsername());

		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
				userDetails.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(authentication);

		return "myProfile";

	}

	@RequestMapping("/bookShelf")
	public String bookShelf(Model model) {

		List<Book> bookList = bookService.findByActiveStatus();

		model.addAttribute("bookList", bookList);

		return "bookShelf";
	}

	@RequestMapping("/bookDetail")
	private String bookDetail(Model model, @PathParam("id") Long id, Principal principal) {

		Book book = bookService.findById(id);
		double averageRating = 0;

		if (book == null) {
			model.addAttribute("message", "Unable to fetch details right now.");
		}
		model.addAttribute("book", book);

		if (principal != null) {
			String username = principal.getName();
			User user = userService.findByUsername(username);
			model.addAttribute("user", user);
		}
		
		List<Review> reviewList = book.getReviewList();
		if(reviewList.size() !=0) {
			averageRating = getAverageRatingForTheBook(reviewList);
			model.addAttribute("addedReview", true);
		}

		List<Integer> qtyList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		model.addAttribute("qtyList", qtyList);
		model.addAttribute("qty", 1);
		model.addAttribute("reviewList", reviewList);
		model.addAttribute("averageRating",  averageRating);

		return "bookDetail";
	}

	private double getAverageRatingForTheBook(List<Review> reviewList) {
		
		double rating = 0;
		int reviewListSize = reviewList.size();
		
		if(reviewListSize==0) {
			return 0L;
		}
		
		for(Review review : reviewList) {
			rating += review.getRatingStars();
		}
		
		return (double) (rating/reviewListSize);
	}

	@RequestMapping("/myProfile")
	private String myProfile(Model model, Principal principal) {

		User user = userService.findByUsername(principal.getName());

		model.addAttribute("user", user);
		model.addAttribute("userPaymentList", user.getUserPaymentList());
		model.addAttribute("userShippingList", user.getUserShippingList());
		model.addAttribute("orderList", user.getOrderList());

		UserShipping userShipping = new UserShipping();
		model.addAttribute("userShipping", userShipping);

		model.addAttribute("listOfCreditCards", true);
		model.addAttribute("listOfShippingAddresses", true);

		List<String> stateList = IndiaConstants.listOfIndiaStateCodes;
		Collections.sort(stateList);
		model.addAttribute("stateList", stateList);
		model.addAttribute("classActiveEdit", true);

		return "myProfile";
	}

	@RequestMapping("/orderDetails")
	public String orderDetails(Model model, @RequestParam("id") Long orderId, Principal principal) throws Exception {

		User user = userService.findByUsername(principal.getName());

		Order order = orderService.findById(orderId);

		LOG.info("******************** ORDER *******************" + order.getId() + ", " + order.getOrderTotal());

		/* Check added for validating the order summary for current user only */
		if (order.getUser().getId() != user.getId()) {
			return "badRequestPage";
		} else {

			/* just avoiding a DB call here by fetching cartItems from user */
			// List<CartItem> cartItemList = cartItemService.findByOrder(order);

			model.addAttribute("order", order);
			model.addAttribute("cartItemList", order.getCartItemList());
			// model.addAttribute("cartItemList", cartItemList);

			model.addAttribute("user", user);
			model.addAttribute("addNewShippingAddress", true);
			model.addAttribute("orderList", user.getOrderList());

			UserShipping userShipping = new UserShipping();
			model.addAttribute("userShipping", userShipping);

			List<String> stateList = IndiaConstants.listOfIndiaStateCodes;
			Collections.sort(stateList);
			model.addAttribute("stateList", stateList);

			model.addAttribute("listOfShippingAddresses", true);
			model.addAttribute("classActiveOrders", true);
			model.addAttribute("listOfCreditCards", true);
			model.addAttribute("displayOrderDetails", true);

			model.addAttribute("userPaymentList", user.getUserPaymentList());
			model.addAttribute("userShippingList", user.getUserShippingList());

			return "myProfile";
		}
	}

	@RequestMapping("/searchByCategory")
	public String searchByCategory(Model model, @RequestParam("category") String category) {

		List<Book> bookList = bookService.findByCategory(category);
		model.addAttribute("bookList", bookList);

		return "bookShelf";
	}

	@RequestMapping("/search")
	public String fuzzySearch(Model model, @RequestParam("keyword") String keyword) {

		List<Book> bookList = bookService.findByTitleContaining(keyword);

		model.addAttribute("bookList", bookList);

		return "searchResults";
	}

	@RequestMapping(value="/bookDetail/addReview", method=RequestMethod.POST)
	public String addUserReview(Model model, 
			@ModelAttribute("userReview") String userReview,
			@ModelAttribute("bookId") Long bookId,
			Principal principal
			) {
		
		if(principal == null) {
			return "redirect:/login";
		}
		
		User user = userService.findByUsername(principal.getName());
				
		Review review = new Review();
		review.setComment(userReview);
		review.setRatingStars(4L);
		review.setUser(user);

		Book book = bookService.findById(bookId);
		book.addReview(review);
		bookService.save(book);
		
		List<Review> reviewList = book.getReviewList();
		
		model.addAttribute("reviewList" + reviewList);

		return "redirect:/bookDetail?id="+bookId;
	}
	
	@RequestMapping("/updatePic")
	public String updateProfilePic(Model model, Principal principal) {
		
		User user = userService.findByUsername(principal.getName());
		
//		URL imageUrl = s3client.getUrl(bucketName, user.getUsername()+"_"+user.getId()+".png");

        String imageUrl = user.getImageUrl();
		
		if(null == imageUrl) {
            user.setImageUrl(DEFAULT_USER_IMAGE);
            userService.save(user);
		}

		model.addAttribute("user", user);
//		model.addAttribute("redirected", false);
		
		return "uploadProfilePic";
	}
	
	@RequestMapping(value="/uploadPic", method=RequestMethod.POST)
	public String uploadImage(Model model, 
						Principal principal, 
						@RequestParam("profileImage") MultipartFile file
						) {
		
		User user = userService.findByUsername(principal.getName());
		String fileName = user.getUsername() + "_" + user.getId()+ ".png";
		
		s3Services.uploadProfileImageToS3(file, fileName);
		
		URL imageUrl = s3Services.getObjectAccessibleUrl(user.getUsername()+"_"+user.getId()+".png");

		System.out.println("*************** Setting profile Image url :"+ imageUrl.toString());

		user.setImageUrl(imageUrl.toString());

//		model.addAttribute("profileImage", user.getProfileImage());
//        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ This Should be Null:: user.getProfileImage() :::::  " + user.getProfileImage());


        model.addAttribute("stringImageUrl", user.getImageUrl());
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^ This Should not be null :: user.getImageUrl() :::::  " + user.getImageUrl());

        model.addAttribute("redirected", true);
//		model.addAttribute("imageUrl", imageUrl.toString());
		model.addAttribute("user", user);

		// Persisting the image URL
        userService.save(user);
		return "uploadProfilePic";

	}

	@RequestMapping("/delete")
	public String deleteProfilePic(Model model, Principal principal){

		if(principal == null){
			return "redirect:/login";
		}
		User user = userService.findByUsername(principal.getName());

		user.setImageUrl(DEFAULT_USER_IMAGE);
		userService.save(user);
		model.addAttribute("user", user);
		return "uploadProfilePic";
	}

}
