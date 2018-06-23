package com.bookstore.service;

import com.bookstore.domain.UserShipping;

public interface UserShippingService {

	UserShipping findById(Long shippingId);

	UserShipping findByUserId(Long id);

	void removeById(Long shippingId);

}
