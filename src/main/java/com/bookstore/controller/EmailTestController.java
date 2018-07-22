//package com.bookstore.controller;
//
//import com.bookstore.domain.Order;
//import com.bookstore.domain.User;
//import com.bookstore.utility.MailConstructor;
//import com.bookstore.utility.MailSenderUtilityService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//@Controller
//public class EmailTestController {
//
//    @Autowired
//    private MailConstructor mailContructor;
//
//    @Autowired
//    MailSenderUtilityService mailSenderUtilityService;
//
//    @RequestMapping("/orderEmail")
//    public String sendOrderConfirmationEmail(){
//
//        Order order = new Order();
//        User user = new User();
//
//        mailSenderUtilityService.sendOrderSubmittedEmail();
//
//
//        return "orderConfirmationEmailTemplate";
//    }
//
//}
