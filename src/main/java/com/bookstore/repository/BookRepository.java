package com.bookstore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bookstore.domain.Book;

@Repository
public interface BookRepository extends CrudRepository<Book, Long> {
	
	List<Book> findAll();

	Optional<Book> findById(Long id);

	List<Book> findByTitleContaining(String title);

	List<Book> findByCategory(String category);

	@Query("select b from Book b where b.active='1'")
	List<Book> findByActiveStatus();

}
