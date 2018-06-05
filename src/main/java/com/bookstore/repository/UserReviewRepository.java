package com.bookstore.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bookstore.domain.UserReview;

@Repository
public interface UserReviewRepository extends CrudRepository<UserReview, Long>{

}
