package com.bookstore.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.bookstore.domain.User;
import com.bookstore.domain.UserBilling;
import com.bookstore.domain.UserPayment;
import com.bookstore.domain.UserShipping;
import com.bookstore.service.UserPaymentService;
import com.bookstore.service.UserService;
import com.bookstore.utility.IndiaConstants;
import com.bookstore.utility.MailConstructor;

@Controller
public class BillingController {
	
	private static final Logger LOG = LoggerFactory.getLogger(BillingController.class);

	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserPaymentService userPaymentService;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private MailConstructor mailContructor;

	
	@RequestMapping("/listOfCreditCards")
	private String listOfCreditCards(Model model, Principal principal, HttpRequest request) {
		
		User user = userService.findByUsername(principal.getName());
		
		List<UserPayment> userPaymentList = user.getUserPaymentList();
		List<UserShipping> userShippingList = user.getUserShippingList();
		
		model.addAttribute("user", user);
		model.addAttribute("userPaymentList", userPaymentList);
		model.addAttribute("userShippingList", userShippingList);
		model.addAttribute("userOrderList", user.getOrderList());
		
		model.addAttribute("listOfCreditCards", true);
		model.addAttribute("classActiveBilling", true);
		model.addAttribute("listOfShippingAddresses", true);
		model.addAttribute("orderList", user.getOrderList());

		return "myProfile";
		
	}
	
	@RequestMapping("/addNewCreditCard")
	private String addaNewCreditCard(Model model, Principal principal) {
		
		User user = userService.findByUsername(principal.getName());
		
		model.addAttribute("user", user);
		model.addAttribute("addNewCreditCard", true);
		model.addAttribute("classActiveBilling", true);
		model.addAttribute("listOfShippingAddresses", true);

		
		UserPayment userPayment = new UserPayment();
		UserBilling userBilling = new UserBilling();
		
		model.addAttribute("userPayment", userPayment);
		model.addAttribute("userBilling", userBilling);
		
		List<String> stateList = IndiaConstants.listOfIndiaStateCodes;
		Collections.sort(stateList);
		model.addAttribute("stateList", stateList);
		model.addAttribute("userPaymentList", user.getUserPaymentList());
		model.addAttribute("userShippingList", user.getUserShippingList());
		model.addAttribute("orderList", user.getOrderList());
				
		return  "myProfile";
	}

	@RequestMapping(value="/addNewCreditCard", method=RequestMethod.POST)
	private String addNewCreditCard(
			@ModelAttribute("userPayment") UserPayment userPayment,
			@ModelAttribute("userBilling") UserBilling userBilling,
			Model model, 
			Principal principal,
			HttpRequest request
			) {
		
		User user = userService.findByUsername(principal.getName());
		
		userService.updateUserBillingInfo(userBilling, userPayment, user);
		
		model.addAttribute("user", user);
		model.addAttribute("userShippingList", user.getUserShippingList());
		model.addAttribute("userPaymentList", user.getUserPaymentList());
		model.addAttribute("listOfShippingAddresses", true);
		model.addAttribute("classActiveBilling", true);
		model.addAttribute("listOfCreditCards", true);
		model.addAttribute("orderList", user.getOrderList());
		
		return "myProfile";
		
	}
	
	@RequestMapping("/updateCreditCard")
	private String updateCreditCard(@RequestParam("id") Long creditCardId, Model model, Principal principal) {
		
		User user = userService.findByUsername(principal.getName());
		
		UserPayment userPayment = userPaymentService.findById(creditCardId);
//		userBillingService.findById(id);
		
		if(user.getId() != userPayment.getUser().getId()) {
			return "badRequestPage";
		
		}else {
			model.addAttribute("user", user);
			UserBilling userBilling = userPayment.getUserBilling();
			
			model.addAttribute("userPayment", userPayment);
			model.addAttribute("userBilling", userBilling);
			
			List<String> stateList = IndiaConstants.listOfIndiaStateCodes;
			Collections.sort(stateList);
			model.addAttribute("stateList", stateList);
			
			model.addAttribute("addNewCreditCard", true);
			model.addAttribute("classActiveBilling", true);
			model.addAttribute("listOfShippingAddresses", true);

			model.addAttribute("userPaymentList", user.getUserPaymentList());
			model.addAttribute("userShippingList", user.getUserShippingList());	
			model.addAttribute("orderList", user.getOrderList());

			return "myProfile";
		}
	}
	
	@RequestMapping("/removeCreditCard")
	private String removeCreditCard(@RequestParam("id") Long creditCardId, 
			Model model, 
			Principal principal) {
		
		User user = userService.findByUsername(principal.getName());
		
		if(user.getId() != userPaymentService.findById(creditCardId).getUser().getId()) {
			return "badRequestPage";
		}else {
			
			model.addAttribute("user", user);
//			user.getUserPaymentList().remove(creditCardId);
			
			userPaymentService.removeById(creditCardId);

			model.addAttribute("listOfCreditCards", true);
			model.addAttribute("classActiveBilling", true);
			model.addAttribute("listOfShippingAddresses", true);
			
			model.addAttribute("userPaymentList", user.getUserPaymentList());
			model.addAttribute("userShippingList", user.getUserShippingList());		
			model.addAttribute("orderList", user.getOrderList());
			
			return "myProfile";
		}
		
	}
	
	@RequestMapping(value="/setDefaultPayment", method=RequestMethod.POST)
	private String setDefaultPayment(Model model, 
			@ModelAttribute("defaultUserPaymentId") Long defaultPaymentId, 
			Principal principal) {
		
			User user = userService.findByUsername(principal.getName());
		
			userService.setUserDefaultPaymentId(defaultPaymentId, user);
			
			model.addAttribute("user", user);
			model.addAttribute("userPaymentList", user.getUserPaymentList());
			model.addAttribute("userShippingList", user.getUserShippingList());			

			model.addAttribute("listOfCreditCards", true);
			model.addAttribute("classActiveBilling", true);
			model.addAttribute("listOfShippingAddresses", true);
			model.addAttribute("orderList", user.getOrderList());
			
			UserPayment theUserPayment=null;
			List<UserPayment> userPaymentList = user.getUserPaymentList();
			for(UserPayment userPayment : userPaymentList) {
				if(userPayment.getId()==defaultPaymentId) {
					theUserPayment=userPayment;
				}
			}

			SimpleMailMessage email =  mailContructor.constructSimpleSetDefaultCreditCard(user, theUserPayment);
			mailSender.send(email);
			
			LOG.info("************** Email sent for setDefaultPayment *************");
			return "myProfile";
		
	}
	
}
