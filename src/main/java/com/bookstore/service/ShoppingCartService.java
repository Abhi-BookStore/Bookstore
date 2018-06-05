package com.bookstore.service;

import com.bookstore.domain.ShoppingCart;

public interface ShoppingCartService {
	
	ShoppingCart findByUserId(Long userId);

	ShoppingCart updateShoppingCart(ShoppingCart shoppingCart);

	ShoppingCart findOne(Long id);

	void clearShoppingCart(ShoppingCart shoppingCart);
	
}
