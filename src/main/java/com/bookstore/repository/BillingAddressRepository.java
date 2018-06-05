package com.bookstore.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bookstore.domain.BillingAddress;

@Repository
public interface BillingAddressRepository extends CrudRepository<BillingAddress, Long> {

	
}
