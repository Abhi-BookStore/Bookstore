package com.bookstore.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookstore.domain.BillingAddress;
import com.bookstore.domain.Book;
import com.bookstore.domain.CartItem;
import com.bookstore.domain.Order;
import com.bookstore.domain.Payment;
import com.bookstore.domain.ShippingAddress;
import com.bookstore.domain.ShoppingCart;
import com.bookstore.domain.User;
import com.bookstore.repository.OrderRepository;
import com.bookstore.service.CartItemService;
import com.bookstore.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private CartItemService cartItemService;

	@Override
	public synchronized Order createOrder(ShoppingCart shoppingCart, 
			ShippingAddress shippingAddress, 
			BillingAddress billingAddress,
			Payment payment,
			String shippingMethod,
			User user
			) {
		
		Order order = new Order();
		
		order.setBillingAddress(billingAddress);
		order.setShippingAddress(shippingAddress);
		order.setPayment(payment);
		order.setUser(user);
//		order.setCartItemList(shoppingCart.getCartItemList());
		order.setOrderTotal(shoppingCart.getGrandTotal());
		order.setShippingMethod(shippingMethod);
		order.setShippingDate(new Date());
		order.setOrderStatus("created");
		
		List<CartItem> cartItemList = cartItemService.findByShoppingCart(shoppingCart);
		
		for(CartItem cartItem : cartItemList) {
			Book book = cartItem.getBook();
			cartItem.setOrder(order);
			book.setInStockNumber(book.getInStockNumber()-cartItem.getQty());			
		}
		order.setCartItemList(cartItemList);
		
		order.setOrderDate(Calendar.getInstance().getTime());
		order.setOrderTotal(shoppingCart.getGrandTotal());
		
		shippingAddress.setOrder(order);
		billingAddress.setOrder(order);
		payment.setOrder(order);
	
		order = orderRepository.save(order);
		return order;
	}

	@Override
	public Order findOne(Long id) {
		return orderRepository.findOne(id);
	}

}
