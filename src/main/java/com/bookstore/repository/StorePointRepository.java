package com.bookstore.repository;

import com.bookstore.domain.StorePoint;
import com.bookstore.domain.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StorePointRepository extends CrudRepository<StorePoint, Long> {

    public List<StorePoint> findByUserId(Long userId);

    public StorePoint findByOrderId(Long orderId);

    @Query("select distinct sp.user from StorePoint sp where sp.user is not null")
    public List<User> findAllUsers();

}
