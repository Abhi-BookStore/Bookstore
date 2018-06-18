package com.bookstore.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookstore.domain.InStockNotification;
import com.bookstore.repository.InStockNotificationRepository;
import com.bookstore.service.InStockNotificationService;

@Service
public class InStockNotificationServiceImpl implements InStockNotificationService {

	@Autowired
	private InStockNotificationRepository inStockNotificationRepository;
	
	@Override
	public void save(InStockNotification inStockNotification) {
		inStockNotificationRepository.save(inStockNotification);
	}

	@Override
	public InStockNotification findByEmail(String notifyMeEmail) {
		return inStockNotificationRepository.findByEmail(notifyMeEmail);
	}

}
