package com.bookstore.controller;

import com.bookstore.constants.Constants;
import com.bookstore.constants.ReferralMode;
import com.bookstore.domain.ReferAFriend;
import com.bookstore.domain.StorePoint;
import com.bookstore.domain.User;
import com.bookstore.domain.security.PasswordResetToken;
import com.bookstore.domain.security.Role;
import com.bookstore.domain.security.UserRole;
import com.bookstore.service.AsyncEmailService;
import com.bookstore.service.ReferAFriendService;
import com.bookstore.service.StorePointService;
import com.bookstore.service.UserService;
import com.bookstore.service.impl.UserSecurityService;
import com.bookstore.utility.MailConstructor;
import com.bookstore.utility.SecurityUtility;
import com.bookstore.utility.StorePointUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
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

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.*;

@Controller
public class MyAccountController {

    private static final Logger logger = LoggerFactory.getLogger(MyAccountController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ReferAFriendService referAFriendService;

    @Autowired
    private AsyncEmailService asyncEmailService;

    @Autowired
    private MailConstructor mailContructor;

    @Autowired
    private MailSender mailSender;

    @Autowired
    private UserSecurityService userSecurityService;

    @Autowired
    private StorePointService storePointService;

    @RequestMapping("/myAccount/referAFriend")
    public String referAFriendGet(Model model, Principal principal){

        User user = userService.findByUsername(principal.getName());
        // Fetching the list of refer a friend users:

        renderListofReferredFriends(model, user);
        model.addAttribute("user", user);

        return "refer_a_Friend";
    }

    private void renderListofReferredFriends(Model model, User user) {
        try {
            List<ReferAFriend> allReferredFriendList =  referAFriendService.findAllReferredFriendsByUserId(user.getId());
            model.addAttribute("referAFriendList", allReferredFriendList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @RequestMapping(value = "/myAccount/referAFriend", method = RequestMethod.POST)
    public String referAFriendPost(@ModelAttribute("friendEmail") String friendEmail, Model model, Principal principal, HttpServletRequest request){
        User user = userService.findByUsername(principal.getName());

        if(referAFriendService.checkIfEmailIsRegistered(friendEmail)){
//            model.addAttribute("alreadyRegistered", true);
            return "redirect:/myAccount/referAFriend";
        }

        // Check if this user is already invited or not
        // If the friend is invited already, increase the invitationCount by 1 else just mark one additional.

        // We shouldn't have any condition as of now to "do not" invite friends who are already registered.
        // Simply invite everyone.

        ReferAFriend alreadyInvited = referAFriendService.searchInvitedEmailId(user.getId(), friendEmail);
        ReferAFriend referAFriend = new ReferAFriend();

        if(alreadyInvited != null){
            alreadyInvited.setInvitationCount(alreadyInvited.getInvitationCount()+1);
            referAFriend=alreadyInvited;
        }else{
            // Create a ReferAFriend Entity and start adding values with this post methods.
            logger.info("Not already invited");
            referAFriend.setEmailId(friendEmail);
            referAFriend.setInvitationDate(new Date());
            referAFriend.setReferralModel(ReferralMode.EMAIL.name());
            referAFriend.setJoined(false);
            referAFriend.setInvitationCount(1);
            referAFriend.setUser(user);
        }

        try {
            // Create temp user, generate token and send email with token ID
            sendInvitationRegistrationLinkToFriend(user,referAFriend,friendEmail,request, alreadyInvited);
        } catch (Exception e) {
            e.printStackTrace();
        }

        referAFriendService.save(referAFriend);

        return "redirect:/myAccount/referAFriend";
    }


    private void sendInvitationRegistrationLinkToFriend(User accountUser, ReferAFriend referAFriend, String friendEmail, HttpServletRequest request, ReferAFriend alreadyInvited) throws Exception {
        User newUser = null;
        String password ="";

        if(alreadyInvited == null){

            newUser = new User();
            newUser.setEmail(friendEmail);

            password = SecurityUtility.randomPassword();
            String encryptedPassword = SecurityUtility.passwordEncoder().encode(password);
            newUser.setPassword(encryptedPassword);

            Role role = new Role();
            role.setRoleId(1);
            role.setName("ROLE_USER");

            Set<UserRole> userRoles = new HashSet<>();
            userRoles.add(new UserRole(newUser, role));

            userService.createUser(newUser, userRoles);
        }else{
//            newUser = alreadyInvited.getUser();
            newUser = userService.findByEmail(alreadyInvited.getEmailId());
            logger.info("ALreday invited new user::: "+ newUser.getEmail());
            password = SecurityUtility.randomPassword();
            String encryptedPassword = SecurityUtility.passwordEncoder().encode(password);
            newUser.setPassword(encryptedPassword);
            userService.save(newUser);
        }
        logger.info("New user has been created: email:: " + friendEmail);

        String token = UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(newUser, token);

        String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        String referralLink = appUrl + "/referral-registration?token="+token+"&userId="+accountUser.getId()+"&tempwd="+password;


        logger.info("request.getServerName() ::" + request.getServerName());
        logger.info("request.getServerPort() ::" + request.getServerPort());
        logger.info("request.getContextPath() :: " + request.getContextPath());

        logger.info("referral Link: "+ referralLink);

//        SimpleMailMessage simpleEmail = mailContructor.constructSimpleResetTokenEmail(appUrl, request.getLocale(), token,
//                user, password);
//        mailSender.send(simpleEmail);
//        logger.info("Email has been sent");


        // Now send the email to friend with joining Link from User's registered email ID.
        Map<String, Object> contextRAF = new HashMap<String, Object>();
        contextRAF.put("user", accountUser);
        contextRAF.put("referAFriend", referAFriend);
        contextRAF.put("invitationURL", referralLink);
        contextRAF.put("token", token);

        asyncEmailService.sendWithContext(newUser.getEmail(), friendEmail, Constants.REFER_A_FRIEND_REPLY_TO, Constants.REFER_A_FRIEND_EMAIl_SUBJECT, Constants.REFER_A_FRIEND_EMAIl_TEMPLATE ,contextRAF);
        logger.info(":::: EMAIL HAS BEEN FIRED THROUGH SENDGRID ::::");

    }

    @RequestMapping("/referral-registration")
    public String referAFriendRegistration(Model model,
                                           @RequestParam("token") String token,
                                           @RequestParam("userId") String userId,
                                           @RequestParam("tempwd") String tempwd
                                           ){

        logger.info("TOKEN:::::: "+ token);
        logger.info("userId:::::: "+ userId);
        logger.info("tempwd:::::: "+ tempwd);

        PasswordResetToken passToken = userService.getPasswordResetToken(token);
        String userEmail = passToken.getUser().getEmail();
        logger.info("Email Fetched from token------------   "+ userEmail + " user: "+ passToken.getUser().getId());

        model.addAttribute("token", token);
        model.addAttribute("userId", userId);
        model.addAttribute("tempwd", tempwd);
        model.addAttribute("userEmail", userEmail);

        return "RAFRegistration";
    }

    @RequestMapping(value = "/referral-registration", method = RequestMethod.POST)
    public String referAFriendRegistrationPost(
            @ModelAttribute("token") String token,
            @ModelAttribute("userId") String userId,
            @ModelAttribute("tempwd") String tempwd,
            @ModelAttribute("firstName") String firstName,
            @ModelAttribute("lastName") String lastName,
            @ModelAttribute("contactNo") String contactNo,
            @ModelAttribute("username") String username,
            @ModelAttribute("email") String email,
            @ModelAttribute("password") String password
    ){


        PasswordResetToken passToken = userService.getPasswordResetToken(token);

        if (passToken == null || passToken.getExpiryDate().compareTo(new Date()) < 0 ) {
            String message = "Invalid Token";
//            model.addAttribute("message", message);
            return "redirect:/badRequest";
        }

        User r_user = passToken.getUser();
        r_user.setPassword(SecurityUtility.passwordEncoder().encode(password));

        // Check if username is not already present
        if (userService.findByUsername(username) != null) {
            return "redirect:/badRequest";
        }
        r_user.setUsername(username);
        r_user.setFirstName(firstName);
        r_user.setLastName(lastName);
        r_user.setPhone(contactNo);
        r_user.setImageUrl(Constants.DEFAULT_USER_PROFILE_PIC);

        ReferAFriend referAFriend = referAFriendService.findByEmailId(r_user.getEmail());
        referAFriend.setJoined(true);
        referAFriend.setJoiningDate(new Date());

        // Persisting user with details
        userService.save(r_user);

        UserDetails userDetails = userSecurityService.loadUserByUsername(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, tempwd,
                userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        logger.info("token:::::::: "+ token);
        logger.info("userId:::::::: "+ userId);
        logger.info("tempwd:::::::: "+ tempwd);
        logger.info("username:::::::: "+ username);
        logger.info("password:::::::: "+ password);


        User user = userService.findById(Long.valueOf(userId));
        if(null == user){
            return "redirect:/RAFRegistration";
        }
        logger.info("Referrer username is: "+ user.getUsername() + " and email: "+ user.getEmail());

        AddStorePointToReferrerAndReferred(r_user, user);

        return "redirect:/myProfile";
    }

    private void AddStorePointToReferrerAndReferred(User r_user, User user) {
        StorePoint storePoint = new StorePoint();

        storePoint.setReferralBonus(true);
        storePoint.setReferralBonusPoint(Constants.REFER_A_FRIEND_BONUS_POINT);
        storePoint.setPoints(Constants.REFER_A_FRIEND_BONUS_POINT);
        storePoint.setConvertedAmount(StorePointUtility.convertStorePointToCashAmount(Constants.REFER_A_FRIEND_BONUS_POINT));
        storePoint.setUser(user);
        storePoint.setPointEarningMode(ReferralMode.EMAIL.toString());
        user.addStorePoint(storePoint);
        if(null != storePoint){
            storePointService.save(storePoint);
        }

        // Adding Referral bonus to newly registered user also.

        StorePoint r_storePoint = new StorePoint();

        r_storePoint.setReferralBonus(true);
        r_storePoint.setReferralBonusPoint(Constants.REFER_A_FRIEND_BONUS_POINT);
        r_storePoint.setPoints(Constants.REFER_A_FRIEND_BONUS_POINT);
        r_storePoint.setConvertedAmount(StorePointUtility.convertStorePointToCashAmount(Constants.REFER_A_FRIEND_BONUS_POINT));
        r_storePoint.setUser(r_user);
        r_storePoint.setPointEarningMode(ReferralMode.EMAIL.toString());
        r_user.addStorePoint(r_storePoint);

        if(null != r_storePoint){
            storePointService.save(r_storePoint);
        }
    }

}
