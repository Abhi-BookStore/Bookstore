package com.bookstore.service;

import com.bookstore.domain.InStockNotification;

public interface InStockNotificationService {

	void save(InStockNotification inStockNotification);

	InStockNotification findByEmail(String notifyMeEmail);

}
