package com.bookstore.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bookstore.domain.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
	
	public User findByUsername(String username);
	
	public User findByEmail(String email);
	
	public List<User> findAll();

}
