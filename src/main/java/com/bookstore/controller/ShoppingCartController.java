package com.bookstore.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.bookstore.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bookstore.domain.Book;
import com.bookstore.domain.CartItem;
import com.bookstore.domain.InStockNotification;
import com.bookstore.domain.ShoppingCart;
import com.bookstore.domain.User;

@Controller
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

	@Autowired
	private UserService userService;

	@Autowired
	private ShoppingCartService shoppingCartService;

	@Autowired
	private CartItemService cartItemService;
	
	@Autowired
	private BookService bookService;
	
	@Autowired
	private InStockNotificationService inStockNotificationService;

	@Autowired
    private StorePointService storePointService;

	@RequestMapping("/cart")
	private String cart(Model model, Principal principal) {

		User user = userService.findByUsername(principal.getName());
		// ShoppingCart shoppingCart = shoppingCartService.findByUserId(user.getId());
		// //TODO: Test this code whether it works or not.

		ShoppingCart shoppingCart = user.getShoppingCart();
		// List<CartItem> cartItemList = shoppingCart.getCartItemList();

		List<CartItem> cartItemList = cartItemService.findByShoppingCart(shoppingCart);

		shoppingCartService.updateShoppingCart(shoppingCart);

		model.addAttribute("user", user);
		model.addAttribute("shoppingCart", shoppingCart);
		model.addAttribute("cartItemList", cartItemList);

		return "shoppingCart";
	}

	@RequestMapping("/updateCartItem")
	private String updateCartItem(
			Model model,
			@ModelAttribute("qty") String qty,
			@ModelAttribute("id") Long cartItemId
			) {
		
		CartItem cartItem = cartItemService.findById(cartItemId);
		if(cartItem.getBook().getInStockNumber() < Integer.parseInt(qty)) {
			model.addAttribute("notEnoughStock", true);
			return "forward:/shoppingCart/cart";
		}
		cartItem.setQty(Integer.parseInt(qty));
		
		//TODO: Add logic to rollback the stock numbers of book
		// Added back those updated book quantity to stock
		// cartItem.getBook().setInStockNumber(cartItem.getBook().getInStockNumber() - cartItem.getQty());
		
		cartItemService.updateCartItem(cartItem);
		
		return "forward:/shoppingCart/cart";
	}
	
	
	@RequestMapping("/removeItem")
	private String removeItemFromCart(
			@RequestParam("id") Long cartItemId
			) {
		System.out.println("****** CartItem id ****** "+ cartItemId);
		CartItem cartItem = cartItemService.findById(cartItemId);
		System.out.println("****** CartItem  ****** "+ cartItem);
		// Added back those deleted book quantity to stock
		cartItem.getBook().setInStockNumber(cartItem.getBook().getInStockNumber() + cartItem.getQty());
		System.out.println("****** CartItem totalQty  ****** "+ cartItem.getBook().getInStockNumber() + cartItem.getQty());
		
		cartItemService.removeCartItem(cartItem);
		
		return "forward:/shoppingCart/cart";
	}
	
	
	/**
	 * Add items to shopping Cart and go to cart page.
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/addItem")
	private String addItemToCart(
			Model model,
			@ModelAttribute("book") Book book,
			@ModelAttribute("qty") String qty,
			Principal principal	
			) {
		
		User user = userService.findByUsername(principal.getName());
		book = bookService.findOne(book.getId());
	
		if (Integer.parseInt(qty) > book.getInStockNumber()) {
			model.addAttribute("notEnoughStock", true);
			return "forward:/bookDetail?id=" + book.getId();
		}
		
		CartItem cartItem = cartItemService.addBookToCartItem(book, Integer.parseInt(qty), user);
		
		// Added code to reduce the book stock from repository when user adds book to the cart.
		//TODO: But this logic should be implemented for update/delete book quantities.
		book.setInStockNumber(bookService.findOne(book.getId()).getInStockNumber()-(Integer.parseInt(qty)));
		shoppingCartService.updateShoppingCart(user.getShoppingCart());
		
		model.addAttribute("user",user);
		model.addAttribute("shoppingCart", user.getShoppingCart());
		model.addAttribute("cartItemList", user.getShoppingCart().getCartItemList());		
		
		return "shoppingCart";
	}
	
	@RequestMapping("/notifyInStock")
	private String notifyInStock(Model model, 
					@ModelAttribute("bookId") Long bookId,
					@ModelAttribute("notifyMeEmail") String notifyMeEmail
					) {
		
		if(null == notifyMeEmail || notifyMeEmail.equalsIgnoreCase("")) {
			model.addAttribute("errorMessage", true);
			return "redirect:/bookDetails?id=1"+bookId;
		}
		
		Book book = bookService.findOne(bookId);
		// Check if the subscription is already applied ?
		// Create object of InStockNotification class
		// Set all the attributes and save it.
		
		InStockNotification inStockNotification = new InStockNotification();
		
		InStockNotification isInStockNotification = inStockNotificationService.findByEmail(notifyMeEmail);
		
		if(null != isInStockNotification && isInStockNotification.isNotified()==false) {
			model.addAttribute("alreadySubscribed", true);
			model.addAttribute("email", notifyMeEmail);
			model.addAttribute("book", book);
			
			return  "notificationSubscribed";
		}
		
		inStockNotification.setBookId(bookId);
		inStockNotification.setEmail(notifyMeEmail);
		inStockNotification.setNotified(false);

		inStockNotification.setSubscriptionTime(new Date());
		
		inStockNotificationService.save(inStockNotification);
		
		model.addAttribute("alreadySubscribed", false);
		model.addAttribute("email", notifyMeEmail);
		model.addAttribute("book", book);
		
		return "notificationSubscribed";
	}
	
	
}
