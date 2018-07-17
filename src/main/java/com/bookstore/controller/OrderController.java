package com.bookstore.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.bookstore.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.amazonaws.services.s3.AmazonS3;
import com.bookstore.domain.BillingAddress;
import com.bookstore.domain.CartItem;
import com.bookstore.domain.Order;
import com.bookstore.domain.Payment;
import com.bookstore.domain.ShippingAddress;
import com.bookstore.domain.ShoppingCart;
import com.bookstore.domain.User;
import com.bookstore.domain.UserPayment;
import com.bookstore.domain.UserShipping;
import com.bookstore.s3.service.S3Services;
import com.bookstore.utility.IndiaConstants;
import com.bookstore.utility.MailConstructor;
import com.bookstore.utility.MailSenderUtilityService;
import com.bookstore.utility.PDFGenerator;

@Controller
public class OrderController {

	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private ShoppingCartService shoppingCartService;

	@Autowired
	private ShippingAddressService shippingAddressService;

	@Autowired
	private CartItemService cartItemService;

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private BillingAddressService billingAddressService;

	@Autowired
	private UserShippingService userShippingService;

	@Autowired
	private UserPaymentService userPaymentService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private MailSenderUtilityService mailSenderUtilityService;

	@Autowired
	private PDFGenerator pdfGenerator;

	@Autowired
	private S3Services s3Services;

	@Autowired
	private StorePointService storePointService;

	@Value("${jsa.s3.uploadfile}")
	private String uploadFilePath;

	private ShippingAddress shippingAddress = new ShippingAddress();
	private BillingAddress billingAddress = new BillingAddress();
	private Payment payment = new Payment();

	@RequestMapping("/checkout")
	private String checkoutPage(Model model,
			@RequestParam(value = "missingRequiredField", required = false) boolean missingRequiredField,
			Principal principal, @RequestParam("id") Long cartId) {

		User user = userService.findByUsername(principal.getName());
		if (cartId != user.getShoppingCart().getId()) {
			return "badRequestPage";
		}

		// List<CartItem> cartItemList = user.getShoppingCart().getCartItemList();
		List<CartItem> cartItemList = cartItemService.findByShoppingCart(user.getShoppingCart());

		if (cartItemList.size() == 0) {
			model.addAttribute("emptyCart", true);
			return "forward:/shoppingCart/cart";
		}

		// Checking if cartItem has enough in stock to order get completed.
		for (CartItem cartItem : cartItemList) {
			if (cartItem.getBook().getInStockNumber() < cartItem.getQty()) {
				model.addAttribute("notEnoughStock", true);
				return "forward:/shoppingCart/cart";
			}
		}

		List<UserShipping> userShippingList = user.getUserShippingList();
		if (userShippingList.size() == 0) {
			model.addAttribute("emptyShippingList", true);
		} else {
			model.addAttribute("emptyShippingList", false);
		}

		// Adding currentShiping to ShippingAddress table
		for (UserShipping userShipping : userShippingList) {
			if (userShipping.isUserShippingDefault()) {
				shippingAddressService.setByUserShipping(userShipping, shippingAddress);
			}
		}
		model.addAttribute("userShippingList", userShippingList);

		List<UserPayment> userPaymentList = user.getUserPaymentList();
		if (userPaymentList.size() == 0) {
			model.addAttribute("emptyPaymentList", true);
		} else {
			model.addAttribute("emptyPaymentList", false);
		}

		// Adding currentPayment to Payment table
		for (UserPayment userPayment : userPaymentList) {
			if (userPayment.isDefaultPayment()) {
				paymentService.setByUserPayment(userPayment, payment);
				billingAddressService.setByBillingAddressService(userPayment.getUserBilling(), billingAddress);
			}
		}
		model.addAttribute("userPaymentList", userPaymentList);

		// ShoppingCart shoppingCart = shoppingCartService.findOne(cartId);
		/**
		 * Below one worked very well but we are following another way to do the same
		 * just to avoid extra DB call.
		 */
		// ShippingAddress shippingAddress =
		// shippingAddressService.findByUserId(user.getId());
		// System.out.println("*************************************************** "+
		// shippingAddress.getShippingAddressStreet1());

		ShoppingCart shoppingCart = user.getShoppingCart();

		List<String> stateList = IndiaConstants.listOfIndiaStateCodes;
		Collections.sort(stateList);
		model.addAttribute("stateList", stateList);

		model.addAttribute("user", user);
		model.addAttribute("cartItemList", cartItemList);
		model.addAttribute("shoppingCart", shoppingCart);

		model.addAttribute("payment", payment);
		model.addAttribute("shippingAddress", shippingAddress);
		model.addAttribute("billingAddress", billingAddress);

		model.addAttribute("classActiveShipping", true);

		if (missingRequiredField) {
			model.addAttribute("missingRequiredField", true);
		}

		return "checkout";
	}

	@RequestMapping(value = "/checkout", method = RequestMethod.POST)
	private String checkoutTheShoppingCart(Model model, Principal principal, @ModelAttribute("payment") Payment payment,
			@ModelAttribute("shippingAddress") ShippingAddress shippingAddress,
			@ModelAttribute("billingAddress") BillingAddress billingAddress,
			@ModelAttribute("billingSameAsShipping") String billingSameAsShipping,
			@ModelAttribute("shippingMethod") String shippingmethod) {

		User user = userService.findByUsername(principal.getName());

		ShoppingCart shoppingCart = user.getShoppingCart();
		// List<CartItem> cartItemList = shoppingCart.getCartItemList();

		List<CartItem> cartItemList = cartItemService.findByShoppingCart(shoppingCart);
		model.addAttribute("cartItemList", cartItemList);

		if (billingSameAsShipping.equals("true")) {
			billingAddress.setBillingAddressName(shippingAddress.getShippingAddressName());
			billingAddress.setBillingAddressStreet1(shippingAddress.getShippingAddressStreet1());
			billingAddress.setBillingAddressStreet2(shippingAddress.getShippingAddressStreet2());
			billingAddress.setBillingAddressCity(shippingAddress.getShippingAddressCity());
			billingAddress.setBillingAddressState(shippingAddress.getShippingAddressState());
			billingAddress.setBillingAddressCountry(shippingAddress.getShippingAddressCountry());
			billingAddress.setBillingAddressZipcode(shippingAddress.getShippingAddressZipcode());
		}

		if (validateOrderDetails(payment, shippingAddress, billingAddress)) {
			return "redirect:/checkout/?id=" + shoppingCart.getId() + "&missingRequiredField=true";
		}

		Order order = orderService.createOrder(shoppingCart, shippingAddress, billingAddress, payment, shippingmethod,
				user);

		storePointService.addPointsWithOrderDetails(order);

		/** Generating PDF for the order summary */
		pdfGenerator.generateItenerary(order, uploadFilePath + order.getId() + ".pdf");

		/** Upload the PDFs to S3 Bucket */
		s3Services.uploadObjectWithPublicAccess(order.getId()+".pdf", uploadFilePath + order.getId()+".pdf");

		/** Sending Async email */
		mailSenderUtilityService.sendOrderSubmittedEmail(order, user, Locale.ENGLISH);
		logger.info("Async Email Has been sent");

		/** Clearing shopping cart after placing the order */
		shoppingCartService.clearShoppingCart(shoppingCart);

		LocalDate today = LocalDate.now();
		LocalDate estimatedDeliveryDate = null;

		if (shippingmethod.equals("groundShipping")) {
			estimatedDeliveryDate = today.plusDays(5);
		} else if (shippingmethod.equals("premiumShipping")) {
			estimatedDeliveryDate = today.plusDays(2);
		}

		model.addAttribute("estimatedDeliveryDate", estimatedDeliveryDate);

		return "orderSubmittedPage";
	}

	/**
	 * @param payment
	 * @param shippingAddress
	 * @param billingAddress
	 * @return
	 */
	private boolean validateOrderDetails(Payment payment, ShippingAddress shippingAddress,
			BillingAddress billingAddress) {
		return shippingAddress.getShippingAddressStreet1().isEmpty()
				|| shippingAddress.getShippingAddressName().isEmpty()
				|| shippingAddress.getShippingAddressCity().isEmpty()
				|| shippingAddress.getShippingAddressState().isEmpty()
				|| shippingAddress.getShippingAddressZipcode().isEmpty() || payment.getCardNumber().isEmpty()
				|| payment.getHolderName().isEmpty() || payment.getCvc() == 0
				|| billingAddress.getBillingAddressName().isEmpty()
				|| billingAddress.getBillingAddressStreet1().isEmpty()
				|| billingAddress.getBillingAddressCity().isEmpty() || billingAddress.getBillingAddressState().isEmpty()
				|| billingAddress.getBillingAddressZipcode().isEmpty();
	}

	@RequestMapping("/setShippingAddress")
	private String setShippingAddress(Model model, Principal principal,
			@RequestParam("userShippingId") Long userShippingId) {

		User user = userService.findByUsername(principal.getName());

		// Finding userSHipping as per the userShippingID
		UserShipping userShipping = userShippingService.findById(userShippingId);

		if (userShipping.getUser().getId() != user.getId()) {
			return "badRequestPage";
		} else {

			shippingAddressService.setByUserShipping(userShipping, shippingAddress);

			List<CartItem> cartItemList = cartItemService.findByShoppingCart(user.getShoppingCart());

			model.addAttribute("shippingAddress", shippingAddress);
			model.addAttribute("payment", payment);
			model.addAttribute("billingAddress", billingAddress);
			model.addAttribute("cartItemList", cartItemList);
			model.addAttribute("shoppingCart", user.getShoppingCart());

			List<String> stateList = IndiaConstants.listOfIndiaStateCodes;
			Collections.sort(stateList);
			model.addAttribute("stateList", stateList);

			List<UserShipping> userShippingList = user.getUserShippingList();
			List<UserPayment> userPaymentList = user.getUserPaymentList();

			model.addAttribute("userShippingList", userShippingList);
			model.addAttribute("userPaymentList", userPaymentList);

			model.addAttribute("shippingAddress", shippingAddress);

			model.addAttribute("classActiveShipping", true);

			if (userPaymentList.size() == 0) {
				model.addAttribute("emptyPaymentList", true);
			} else {
				model.addAttribute("emptyPaymentList", false);
			}

			model.addAttribute("emptyShippingList", false);

			return "checkout";
		}

	}

	@RequestMapping("/setPaymentMethod")
	private String setPaymentMethod(Model model, Principal principal,
			@RequestParam("userPaymentId") Long userPaymentId) {

		User user = userService.findByUsername(principal.getName());
		UserPayment userPayment = userPaymentService.findById(userPaymentId);

		if (userPayment.getUser().getId() != user.getId()) {
			return "badRequestPage";
		} else {
			paymentService.setByUserPayment(userPayment, payment);

			List<CartItem> cartItemList = cartItemService.findByShoppingCart(user.getShoppingCart());

			model.addAttribute("shippingAddress", shippingAddress);
			model.addAttribute("payment", payment);
			model.addAttribute("billingAddress", billingAddress);
			model.addAttribute("cartItemList", cartItemList);
			model.addAttribute("shoppingCart", user.getShoppingCart());

			List<String> stateList = IndiaConstants.listOfIndiaStateCodes;
			Collections.sort(stateList);
			model.addAttribute("stateList", stateList);

			List<UserShipping> userShippingList = user.getUserShippingList();
			List<UserPayment> userPaymentList = user.getUserPaymentList();

			model.addAttribute("userShippingList", userShippingList);
			model.addAttribute("userPaymentList", userPaymentList);

			model.addAttribute("shippingAddress", shippingAddress);

			model.addAttribute("classActivePayment", true);

			if (userPaymentList.size() == 0) {
				model.addAttribute("emptyPaymentList", true);
			} else {
				model.addAttribute("emptyPaymentList", false);
			}

			model.addAttribute("emptyShippingList", false);

			return "checkout";

		}
	}

}
