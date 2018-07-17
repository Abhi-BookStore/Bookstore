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

            totalPointsEarnedInOrder = cartItem.getQty()*pointsPerBook;
            logger.info("::::: totalPointsEarnedInOrder :::::: "+ totalPointsEarnedInOrder);


        }

        storePoint.setOrder(order);
        storePoint.setPoints(totalPointsEarnedInOrder);
        storePoint.setReferralBonus(false);
        storePoint.setConvertedAmount(StorePointUtility.convertStorePointToCashAmount(totalPointsEarnedInOrder));
        storePoint.setUser(order.getUser());
        storePoint.setReferralBonusPoint(0L);
        logger.info("::::: StorePoint :::::: "+ storePoint);


        if(null != storePoint){
            storePointRepository.save(storePoint);
        }

    }

    @Override
    public Long findCompleteStorePointByUser(User user) {

        if(userService.findById(user.getId()) == null){
            return null;
        }
        StorePoint storePoint =  storePointRepository.findByUserId(user.getId());
        if(storePoint != null){
            return storePoint.getPoints();
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
        return null;
    }

    @Override
    public Long findPointByOrder(Order order) {
        return null;
    }

    @Override
    public Map<User, StorePoint> fetchAllUserStorePoint() {
        return null;
    }
}
