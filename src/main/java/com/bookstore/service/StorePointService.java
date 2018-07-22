package com.bookstore.service;

import com.bookstore.domain.CartItem;
import com.bookstore.domain.Order;
import com.bookstore.domain.StorePoint;
import com.bookstore.domain.User;

import java.util.List;
import java.util.Map;

public interface StorePointService {

    public void addPointsWithOrderDetails(Order order);
    public Long findCompleteStorePointByUser(User user);

    /**
     * Map of OrderId and StorePoint
     * @param user
     * @return
     */
    public Map<Integer,StorePoint> fetchStorePointListByUser(User user);
    public StorePoint findPointByOrder(Order order);
    public Map<User, List<StorePoint>> fetchAllUserStorePoint();
    Long getStorePointsByCartItemList(List<CartItem> cartItemList);
}
