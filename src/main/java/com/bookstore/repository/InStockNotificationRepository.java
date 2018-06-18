package com.bookstore.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bookstore.domain.InStockNotification;

@Repository
public interface InStockNotificationRepository extends CrudRepository<InStockNotification, Long> {

	InStockNotification findByEmail(String email);

}
