package com.bookstore.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bookstore.domain.BookToCartItem;
import com.bookstore.domain.CartItem;

@Repository
@Transactional
public interface BookToCartItemRepository extends CrudRepository<BookToCartItem, Long> {

	void deleteByCartItem(CartItem cartItem);
	
}
