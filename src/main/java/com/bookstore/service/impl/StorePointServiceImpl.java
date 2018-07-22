package com.bookstore.service.impl;

import com.bookstore.domain.*;
import com.bookstore.repository.StorePointRepository;
import com.bookstore.service.StorePointService;
import com.bookstore.service.UserService;
import com.bookstore.utility.StorePointUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.Store;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StorePointServiceImpl implements StorePointService {

    private static final Logger logger = LoggerFactory.getLogger(StorePointServiceImpl.class);

    @Autowired
    private StorePointRepository storePointRepository;

    @Autowired
    private UserService userService;


    /**
     * Receives @{@link Order} object and calculates points as per cartItem.
     * Sets the values to the SP  table.
     *
     * @param order
     */
    @Override
    public void addPointsWithOrderDetails(Order order) {
        StorePoint storePoint = new StorePoint();
        Long totalPointsEarnedInOrder = 0L;

        if (order == null){
            logger.info("*********** Order is null ************");
        }
        // Fetch the price of each Book from orders and calculate the Points earned by them.
        List<CartItem> orderedCartItemList = order.getCartItemList();
        for(CartItem cartItem : orderedCartItemList){

            Long pointsPerBook = StorePointUtility.
                                    calculateAndFetchStorePointForTheBook(cartItem.getBook());
            logger.info("::::: pointsPerBook :::::: "+ pointsPerBook);

            totalPointsEarnedInOrder += cartItem.getQty()*pointsPerBook;
            logger.info("::::: totalPointsEarnedInOrder :::::: "+ totalPointsEarnedInOrder);
        }

        storePoint.setOrder(order);
        storePoint.setPoints(totalPointsEarnedInOrder);
        storePoint.setReferralBonus(false);
        storePoint.setConvertedAmount(StorePointUtility.convertStorePointToCashAmount(totalPointsEarnedInOrder));
        storePoint.setUser(order.getUser());
        storePoint.setReferralBonusPoint(0L);
        order.getUser().addStorePoint(storePoint);
        logger.info("::::: StorePoint :::::: "+ storePoint);


        if(null != storePoint){
            storePointRepository.save(storePoint);
        }
        logger.info(" order.getUser().getStorePointList().size() =>" + order.getUser().getStorePointList().size());
        logger.info("findCompleteStorePointByUser => "+ findCompleteStorePointByUser(order.getUser()));
    }

    @Override
    public Long findCompleteStorePointByUser(User user) {

        Long points =0L;

        if(userService.findById(user.getId()) == null){
            return null;
        }
        List<StorePoint> storePointList =  storePointRepository.findByUserId(user.getId());
        if(storePointList.size() >0){
            for(StorePoint storePoint : storePointList){
                points += storePoint.getPoints();
            }
            return points;
        }
        return 0L;
    }

    /**
     * Map of OrderId and StorePoint
     *
     * @param user
     * @return
     */
    @Override
    public Map<Integer, StorePoint> fetchStorePointListByUser(User user) {

        Map<Integer, StorePoint> spOrderMap = new HashMap<>();

        List<StorePoint> storePointList = storePointRepository.findByUserId(user.getId());
        for(StorePoint storePoint : storePointList){
            logger.info("::::: Saving key as :: "+ Integer.parseInt(storePoint.getOrder().getId().toString()) + " ==> Value ::: "+ storePoint.toString());
            spOrderMap.put(Integer.parseInt(storePoint.getOrder().getId().toString()), storePoint);
        }
        return spOrderMap;
    }

    @Override
    public StorePoint findPointByOrder(Order order) {

        if(null == order){
            return null;
        }
        return storePointRepository.findByOrderId(order.getId());
    }

    // TODO: Please add method security for admin use only.
    /**
     * This is for admin use only
     *
     * @return Map<User, StorePoint>
     */
    @Override
    public Map<User, List<StorePoint>> fetchAllUserStorePoint() {

        // Find list of users from store_point table
        // Fetch all StorePoint wrt to that user and add it to the map.
        // Key would be user and value would be list of rows returns.

        Map<User, List<StorePoint>> userStorePointMap = new HashMap<>();

        List<User> userIdList = storePointRepository.findAllUsers();
        for(User user : userIdList){
            System.out.println("::::::::::::::::::: "+ user.getUsername());
            List<StorePoint> storePointList = storePointRepository.findByUserId(user.getId());
            userStorePointMap.put(user, storePointList);
        }
        logger.info("***************** userStorePointMap ************"  + userStorePointMap.size());

        return userStorePointMap;
    }

    @Override
    public Long getStorePointsByCartItemList(List<CartItem> cartItemList) {
        Long myPoints = 0L;
        for(CartItem cartItem : cartItemList){
            myPoints += cartItem.getQty() * StorePointUtility.calculateAndFetchStorePointForTheBook(cartItem.getBook());
        }
        return myPoints;
    }
}
