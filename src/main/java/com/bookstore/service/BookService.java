package com.bookstore.service;

import java.util.List;


import com.bookstore.domain.Book;

public interface BookService {
	
	List<Book> findAll();
	
	Book findOne(Long id);

	List<Book> findByTitleContaining(String bookName);
	
	List<Book> findByCategory(String category);

	void save(Book book);

	List<Book> findByActiveStatus();

	Book findById(Long id);
}


