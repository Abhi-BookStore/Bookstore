package com.bookstore.service;

import com.bookstore.domain.ShippingAddress;
import com.bookstore.domain.UserShipping;

public interface ShippingAddressService {

//	ShippingAddress findByUserId(Long id);

	ShippingAddress setByUserShipping(UserShipping userShipping, ShippingAddress shippingAddress);
	
}
