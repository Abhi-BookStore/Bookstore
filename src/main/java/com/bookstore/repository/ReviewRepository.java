package com.bookstore.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bookstore.domain.Review;

@Repository
public interface ReviewRepository extends CrudRepository<Review, Long> {

	
}
