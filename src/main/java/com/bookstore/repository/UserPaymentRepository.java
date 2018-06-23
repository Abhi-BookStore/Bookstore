package com.bookstore.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bookstore.domain.UserPayment;

@Repository
public interface UserPaymentRepository extends CrudRepository<UserPayment, Long> {

	void removeById(Long id);
		

}
