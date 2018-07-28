package com.bookstore.controller;

import com.bookstore.constants.Constants;
import com.bookstore.constants.ReferralMode;
import com.bookstore.domain.ReferAFriend;
import com.bookstore.domain.User;
import com.bookstore.service.AsyncEmailService;
import com.bookstore.service.ReferAFriendService;
import com.bookstore.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MyAccountController {

    private static final Logger logger = LoggerFactory.getLogger(MyAccountController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ReferAFriendService referAFriendService;

    @Autowired
    private AsyncEmailService asyncEmailService;

    @RequestMapping("/myAccount/referAFriend")
    public String referAFriendGet(Model model, Principal principal){

        User user = userService.findByUsername(principal.getName());
        // Fetching the list of refer a friend users:

        try {
            List<ReferAFriend> allReferredFriendList =  referAFriendService.findAllReferredFriendsByUserId(user.getId());
            model.addAttribute("referAFriendList", allReferredFriendList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        model.addAttribute("user", user);

        return "refer_a_Friend";
    }


    @RequestMapping(value = "/myAccount/referAFriend", method = RequestMethod.POST)
    public String referAFriendPost(@ModelAttribute("friendEmail") String friendEmail, Principal principal){

        User user = userService.findByUsername(principal.getName());
        // Create a ReferAFriend Entity and start adding values with this post methods.
        ReferAFriend referAFriend = new ReferAFriend();
        referAFriend.setEmailId(friendEmail);
        referAFriend.setInvitationDate(new Date());
        referAFriend.setReferralModel(ReferralMode.EMAIL.name());
        referAFriend.setUser(user);

        // Check if this user is already invited or not
        // If the friend is invited already, increase the invitationCount by 1 else just mark one additional.

        //TODO : Add different logic here
//        int invitationCount = referAFriendService.fetchTheInvitationCount(friendEmail);
//        referAFriend.setInvitationCount(invitationCount+1);

        // We shouldn't have any condition as of now to "do not" invite friends who are already registered.
        // Simply invite everyone.

        // Now send the email to friend with joining Link from User's registered email ID.
        Map<String, Object> contextRAF = new HashMap<String, Object>();
        contextRAF.put("user", user);
        contextRAF.put("referAFriend", referAFriend);
        logger.info(":::: EMAIL HAS BEEN FIRED THROUGH SENDGRID ::::");
        asyncEmailService.sendWithContext(user.getEmail(), friendEmail, Constants.REFER_A_FRIEND_REPLY_TO, Constants.REFER_A_FRIEND_EMAIl_SUBJECT, Constants.REFER_A_FRIEND_EMAIl_TEMPLATE ,contextRAF);

        referAFriendService.save(referAFriend);

        return "refer_a_Friend";
    }


}
