package com.bookstore.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookstore.domain.Review;
import com.bookstore.repository.ReviewRepository;
import com.bookstore.service.ReviewService;

@Service
public class ReviewServiceImpl implements ReviewService {

	@Autowired
	private ReviewRepository reviewRepository;

	@Override
	public Review save(Review review) {
		return reviewRepository.save(review);
	}

}
