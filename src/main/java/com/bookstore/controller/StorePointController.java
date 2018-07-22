package com.bookstore.controller;

import com.bookstore.domain.StorePoint;
import com.bookstore.domain.User;
import com.bookstore.service.StorePointService;
import com.bookstore.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/sp")
public class StorePointController {

    private static final Logger logger = LoggerFactory.getLogger(StorePointController.class);

    @Autowired
    private StorePointService storePointService;

    @Autowired
    private UserService userService;

    @RequestMapping("/storePoints")
    public String getStorePoints(Model model, Principal principal){

        User user = userService.findByUsername(principal.getName());
        Long allStorepoints = storePointService.findCompleteStorePointByUser(user);

        Map<Integer, StorePoint> orderStorePointMap = storePointService.fetchStorePointListByUser(user);
        logger.info(":::::::::::::: Size of orderStorePointMap ::: "+ orderStorePointMap.size());

        Map<User, List<StorePoint>> userStorePointMap = storePointService.fetchAllUserStorePoint();

        model.addAttribute("allStorepoints", allStorepoints);
        model.addAttribute("user", user);
        model.addAttribute("map", orderStorePointMap);
        model.addAttribute("amap", userStorePointMap);

        return "store-point";
    }

    @RequestMapping("/orderSpMap")
    public String fetchOrderStorePointMap(Model model, Principal principal){
        User user = userService.findByUsername(principal.getName());
        Map<Integer, StorePoint> orderStorePointMap = storePointService.fetchStorePointListByUser(user);
        logger.info(":::::::::::::: Size of orderStorePointMap ::: "+ orderStorePointMap.size());
        Set set = orderStorePointMap.entrySet();
        logger.info(":::::::::::::: Size of orderStorePointMap Keyset ::: "+ set.size());

        model.addAttribute("user", user);
        model.addAttribute("map", orderStorePointMap);

        return "testing";
    }

}
