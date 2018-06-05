package com.bookstore.service.REST;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.domain.User;
import com.bookstore.service.UserService;

@RestController
@RequestMapping("/users")
public class UserRestController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping
	public List<User> getAllUsers(){
		
		return userService.findAll();
	}
	
	@GetMapping("/{id}")
	public User getUserById(@PathVariable("id") Long id) {
		User user = userService.findById(id);
		System.out.println(user.getFirstName());
		return user;
		
	}
	
}
