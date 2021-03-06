package com.bookstore.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookstore.domain.Book;
import com.bookstore.repository.BookRepository;
import com.bookstore.service.BookService;

@Service
public class BookServiceImpl implements BookService {

	@Autowired
	private BookRepository bookRepository;

	@Override
	public List<Book> findAll() {
		return bookRepository.findAll();
	}

	@Override
	public Book findById(Long id) {
		return bookRepository.findById(id).get();
	}

	@Override
	public List<Book> findByTitleContaining(String title) {
		return bookRepository.findByTitleContaining(title);
	}

	@Override
	public List<Book> findByCategory(String category) {

		List<Book> bookList = bookRepository.findByCategory(category);

		List<Book> activeBookList = new ArrayList<>();

		for (Book book : bookList) {
			activeBookList.add(book);
		}
		return activeBookList;
	}

	@Override
	public void save(Book book) {
		bookRepository.save(book);
	}

	@Override
	public List<Book> findByActiveStatus() {
		return bookRepository.findByActiveStatus();
	}

	@Override
	public Book findOne(Long id) {
		return bookRepository.findById(id).get();
	}



}
