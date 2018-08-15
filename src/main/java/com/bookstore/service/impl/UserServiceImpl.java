package com.bookstore.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookstore.domain.ShoppingCart;
import com.bookstore.domain.User;
import com.bookstore.domain.UserBilling;
import com.bookstore.domain.UserPayment;
import com.bookstore.domain.UserShipping;
import com.bookstore.domain.security.PasswordResetToken;
import com.bookstore.domain.security.UserRole;
import com.bookstore.repository.PasswordResetTokenRepository;
import com.bookstore.repository.RoleRepository;
import com.bookstore.repository.UserPaymentRepository;
import com.bookstore.repository.UserRepository;
import com.bookstore.repository.UserShippingRepository;
import com.bookstore.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private PasswordResetTokenRepository passwordResetTokenRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserPaymentRepository userPaymentRepository;

	@Autowired
	private UserShippingRepository userShippingRepository;

	@Autowired
	private RoleRepository roleRepository;
	

	@Override
	public PasswordResetToken getPasswordResetToken(String token) {
		return passwordResetTokenRepository.findByToken(token);
	}

	@Override
	public void createPasswordResetTokenForUser(User user, String token) {
		final PasswordResetToken myToken = new PasswordResetToken(token, user);
		passwordResetTokenRepository.save(myToken);
	}

	@Override
	public User findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	@Override
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	@Transactional
	public User createUser(User user, Set<UserRole> userRoles) throws Exception {
		User localUser = null;
		if(null == user.getUsername()){
			localUser = userRepository.findByEmail(user.getEmail());
		}else{
			localUser = userRepository.findByUsername(user.getUsername());
		}
		if (localUser != null) {
			// throw new Exception("User already exists. Nothing will be done here.");
			LOG.info("User {} already exists. Nothiong will be done here." + user.getUsername());
		} else {
			for (UserRole uRole : userRoles) {
				roleRepository.save(uRole.getRole());
			}
			user.getUserRoles().addAll(userRoles);
			
			// Adding shoppingCart to user and user to shoppingCart
			ShoppingCart shoppingCart = new ShoppingCart();
			shoppingCart.setUser(user);
			user.setShoppingCart(shoppingCart);
			
			/// initializing the user shippingList
			user.setUserShippingList(new ArrayList<UserShipping>());
			user.setUserPaymentList(new ArrayList<UserPayment>());
			
			localUser = userRepository.save(user);
		}
		return localUser;
	}

	@Override
	public User save(User user) {
		if (null == user) {
			LOG.info("No user to save.");
			return null;
		}
		userRepository.save(user);

		return user;
	}

	@Override
	public void updateUserBillingInfo(UserBilling userBilling, UserPayment userPayment, User user) {

		userPayment.setUser(user);
		userPayment.setUserBilling(userBilling);
		userPayment.setDefaultPayment(true);

		userBilling.setUserPayment(userPayment);
		user.getUserPaymentList().add(userPayment);
		save(user);
	}

	@Override
	public void setUserDefaultPaymentId(Long userPaymentId, User user) {

		List<UserPayment> userPaymentList = (List<UserPayment>) userPaymentRepository.findAll();

		for (UserPayment uPaymnet : userPaymentList) {
			if (uPaymnet.getId() == userPaymentId) {
				uPaymnet.setDefaultPayment(true);
				userPaymentRepository.save(uPaymnet);
			} else {
				uPaymnet.setDefaultPayment(false);
				userPaymentRepository.save(uPaymnet);

			}
		}
	}

	@Override
	public void updateUserShippingInfo(UserShipping userShipping, User user) {

		userShipping.setUser(user);
		userShipping.setUserShippingDefault(true);
		user.getUserShippingList().add(userShipping);
		save(user);

	}

	@Override
	public void setUserDefaultShippingAddress(Long userShippingId, User user) {

		List<UserShipping> userShippings = (List<UserShipping>) userShippingRepository.findAll();

		for (UserShipping userShipping : userShippings) {
			if (userShippingId == userShipping.getId()) {
				userShipping.setUserShippingDefault(true);
				userShippingRepository.save(userShipping);
			} else {
				userShipping.setUserShippingDefault(false);
				userShippingRepository.save(userShipping);
			}
		}

	}

	@Override
	public User findById(Long id) {
		return userRepository.findById(id).get();
	}

	@Override
	public List<User> findAll() { 
		return userRepository.findAll();
	}

}
