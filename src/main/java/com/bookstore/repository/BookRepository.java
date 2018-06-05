package com.bookstore.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bookstore.domain.Book;

@Repository
public interface BookRepository extends CrudRepository<Book, Long> {
	
	List<Book> findAll();

	Book findOne(Long id);

	List<Book> findByTitleContaining(String title);

	List<Book> findByCategory(String category);

}
