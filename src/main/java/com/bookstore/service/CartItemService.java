package com.bookstore.service;

import java.util.List;

import com.bookstore.domain.Book;
import com.bookstore.domain.CartItem;
import com.bookstore.domain.Order;
import com.bookstore.domain.ShoppingCart;
import com.bookstore.domain.User;

public interface CartItemService {

	List<CartItem> findByShoppingCart(ShoppingCart shoppingCart);

	CartItem updateCartItem(CartItem cartItem);

	CartItem addBookToCartItem(Book book, int qty, User user);

	CartItem findById(Long cartItemId);

	void removeCartItem(CartItem cartItem);

	void deleteById(Long id);

	CartItem save(CartItem cartItem);

	List<CartItem> findByOrder(Order order);
}
