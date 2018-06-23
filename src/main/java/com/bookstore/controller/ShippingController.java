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
import com.bookstore.domain.UserShipping;
import com.bookstore.service.UserService;
import com.bookstore.service.UserShippingService;
import com.bookstore.utility.IndiaConstants;
import com.bookstore.utility.MailConstructor;

@Controller
public class ShippingController {
	
	private static final Logger LOG = LoggerFactory.getLogger(ShippingController.class);

	
	@Autowired
	private UserService userService;
		
	@Autowired
	private UserShippingService userShippingService;
	
	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private MailConstructor mailContructor;

	
	@RequestMapping("/addNewShippingAddress")
	private String addNewShippingAddress(Model model, Principal principal) {
		
		User user = userService.findByUsername(principal.getName());
		
		model.addAttribute("user", user);
		model.addAttribute("addNewShippingAddress", true);
		model.addAttribute("classActiveShipping", true);
		model.addAttribute("listOfCreditCards", true);

		UserShipping userShipping = new UserShipping();
		model.addAttribute("userShipping", userShipping);
				
		List<String> stateList = IndiaConstants.listOfIndiaStateCodes;
		Collections.sort(stateList);
		model.addAttribute("stateList", stateList);
		model.addAttribute("userPaymentList", user.getUserPaymentList());
		model.addAttribute("userShippingList", user.getUserShippingList());	
		model.addAttribute("orderList", user.getOrderList());
		
		return  "myProfile";
	}

	
	
	@RequestMapping(value="/addNewShippingAddress", method=RequestMethod.POST)
	private String addNewShippingAddressPost(Model model,
			@ModelAttribute("userShipping") UserShipping userShipping,
			Principal principal
			) {
		
		User user = userService.findByUsername(principal.getName());
		userService.updateUserShippingInfo(userShipping, user);
		
		model.addAttribute("user", user);
		model.addAttribute("userShippingList", user.getUserShippingList());
		model.addAttribute("listOfShippingAddresses", true);
		model.addAttribute("classActiveShipping", true);
		model.addAttribute("listOfCreditCards", true);
		model.addAttribute("userPaymentList", user.getUserPaymentList());
		model.addAttribute("orderList", user.getOrderList());
		
		return "myProfile";
		
	}
	
	@RequestMapping(value="/setDefaultShippingAddress", method=RequestMethod.POST)
	private String setDefaultShippingAddress(Model model,
			@ModelAttribute("defaultShippingAddressId") Long userShippingId,
			Principal principal
			) {
		User user = userService.findByUsername(principal.getName());
		
		userService.setUserDefaultShippingAddress(userShippingId, user);
		
		model.addAttribute("user", user);
		model.addAttribute("userShippingList", user.getUserShippingList());
		model.addAttribute("listOfCreditCards", true);
		model.addAttribute("userPaymentList", user.getUserPaymentList());
		model.addAttribute("userOrderList", user.getOrderList());
		
		model.addAttribute("classActiveShipping", true);
		model.addAttribute("listOfShippingAddresses", true);
		
		UserShipping userShipping = userShippingService.findById(userShippingId);
		
		SimpleMailMessage email = mailContructor.constructSimpleSetDefaultShippingAddressEmail(user, userShipping);
		mailSender.send(email);
		LOG.info("************** Email sent for setDefaultShippingAddress *************");
		return "myProfile";
		
	}
	
	@RequestMapping("/listOfShippingAddresses")
	private String listOfShipppingAddresses(Model model, Principal principal, HttpRequest request) {
		
		User user = userService.findByUsername(principal.getName());
		
		List<UserShipping> userShippingList = user.getUserShippingList();
		
		model.addAttribute("user", user);
		model.addAttribute("userShippingList", userShippingList);
		model.addAttribute("userPaymentList", user.getUserPaymentList());

		model.addAttribute("classActiveShipping", true);
		model.addAttribute("listOfShippingAddresses", true);
		model.addAttribute("userOrderList", user.getOrderList());		
		model.addAttribute("listOfCreditCards", true);
		model.addAttribute("orderList", user.getOrderList());
		
		return "myProfile";
	}
	
	@RequestMapping("/updateUserShipping")
	private String updateUserShipping(
			Model model, 
			@RequestParam("id") Long shippingId,
			Principal principal
			) {
		User user = userService.findByUsername(principal.getName());
		UserShipping userShipping = userShippingService.findById(shippingId);
		
		if(user.getId() != userShipping.getUser().getId()) {
			model.addAttribute("user", user);
			return "badRequestPage";
		}
			
		model.addAttribute("user", user);
		model.addAttribute("addNewShippingAddress", true);
		model.addAttribute("userShipping", userShipping);
				
		List<String> stateList = IndiaConstants.listOfIndiaStateCodes;
		Collections.sort(stateList);
		model.addAttribute("stateList", stateList);
		
		model.addAttribute("classActiveShipping", true);
		model.addAttribute("listOfCreditCards", true);

		model.addAttribute("userPaymentList", user.getUserPaymentList());
		model.addAttribute("userShippingList", user.getUserShippingList());
		model.addAttribute("orderList", user.getOrderList());
		
		return "myProfile";
	}
	
	@RequestMapping("/removeUserShipping")
	private String deleteShippingAddress(
			Model model, 
			@RequestParam("id")Long shippingId, 
			Principal principal) {
		
		User user = userService.findByUsername(principal.getName());
		UserShipping userShipping = userShippingService.findById(shippingId);
		
		if(user.getId() != userShipping.getUser().getId()) {
			model.addAttribute("user", user);
			return "badRequestPage";
		}
		
		userShippingService.removeById(shippingId);

		model.addAttribute("user", user);
		model.addAttribute("listOfCreditCards", true);
		model.addAttribute("classActiveShipping", true);
		model.addAttribute("listOfShippingAddresses", true);
		
		model.addAttribute("userPaymentList", user.getUserPaymentList());
		model.addAttribute("userShippingList", user.getUserShippingList());
		model.addAttribute("orderList", user.getOrderList());
		
		return "myProfile";
		
	}


}
