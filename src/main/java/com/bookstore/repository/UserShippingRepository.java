package com.bookstore.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bookstore.domain.UserShipping;

@Repository
public interface UserShippingRepository extends CrudRepository<UserShipping, Long> {
	
	UserShipping findByUserId(Long userId);

	void removeById(Long id);
}
