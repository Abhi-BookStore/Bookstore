package com.bookstore.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bookstore.domain.Order;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {

}
