package com.bookstore.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bookstore.domain.Payment;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, Long> {

}
