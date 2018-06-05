package com.bookstore.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bookstore.domain.ShippingAddress;

@Repository
public interface ShippingAddressRepository extends CrudRepository<ShippingAddress, Long> {
	
//	ShippingAddress findByUserId(Long userId);

}
