package com.bookstore.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookstore.domain.CartItem;
import com.bookstore.domain.ShoppingCart;
import com.bookstore.repository.CartItemRepository;
import com.bookstore.repository.ShoppingCartRepository;
import com.bookstore.service.CartItemService;
import com.bookstore.service.ShoppingCartService;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

	@Autowired
	private ShoppingCartRepository shoppingCartRepository;
	
	@Autowired
	private CartItemService cartItemService;
	
	@Autowired
	private CartItemRepository cartItemRepository;

	@Override
	public ShoppingCart findByUserId(Long id) {
		return shoppingCartRepository.findByUserId(id);
	}

	@Override
	public ShoppingCart updateShoppingCart(ShoppingCart shoppingCart) {

		BigDecimal cartTotal = new BigDecimal(0);
		List<CartItem> cartItemList = cartItemService.findByShoppingCart(shoppingCart);

		for (CartItem cartItem : cartItemList) {
			if (cartItem.getBook().getInStockNumber() > 0) {
				cartItemService.updateCartItem(cartItem);
				cartTotal = cartTotal.add(cartItem.getSubTotal());
			}
		}

		shoppingCart.setGrandTotal(cartTotal);
		shoppingCart.setCartItemList(cartItemList);
		shoppingCartRepository.save(shoppingCart);

		return shoppingCart;
	}

	@Override
	public ShoppingCart findOne(Long id) {
		
		return shoppingCartRepository.findOne(id);
	}

	@Override
	public void clearShoppingCart(ShoppingCart shoppingCart) {

		/*
		 * When some item is in cart that means we have cartItem entries in cartItem
		 * table and shoppingCart table. If order has been placed successfully then we will just
		 * delete the entries of cartItem table and shoppingCart table.
		 */
		
		for(CartItem cartItem : shoppingCart.getCartItemList()) {
			cartItem.setShoppingCart(null);
//			cartItemRepository.deleteById(cartItem.getId());
			cartItemService.save(cartItem);
		}
		shoppingCart.setGrandTotal(new BigDecimal(0));
		shoppingCartRepository.save(shoppingCart);
		
	}
	
}
