package com.bookstore.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bookstore.domain.Order;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {
	
	Optional<Order> findById(Long id);

}
