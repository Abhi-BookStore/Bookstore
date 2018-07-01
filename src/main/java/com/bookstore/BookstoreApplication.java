package com.bookstore;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import com.bookstore.domain.User;
import com.bookstore.domain.security.Role;
import com.bookstore.domain.security.UserRole;
import com.bookstore.repository.BookRepository;
import com.bookstore.service.BookService;
import com.bookstore.service.UserService;
import com.bookstore.utility.SecurityUtility;

@SpringBootApplication
@EnableAsync
public class BookstoreApplication implements CommandLineRunner {

	@Autowired
	private UserService userService;

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private BookService bookService;

	public static void main(String[] args) {
		SpringApplication.run(BookstoreApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		 User user = new User();
		
		 user.setFirstName("Abhinav");
		 user.setLastName("Singh");
		 user.setEmail("singh09.abhinav+user@gmail.com");
		 user.setEnabled(true);
		 user.setPassword(SecurityUtility.passwordEncoder().encode("user"));
		 user.setPhone("8087102325");
		 user.setUsername("user");
		 Set<UserRole> userRoles = new HashSet<>();
		 Role role1 = new Role();
		 role1.setRoleId(1);
		 role1.setName("ROLE_USER");
		 userRoles.add(new UserRole(user, role1));
		
		 userService.createUser(user, userRoles);

		/*
		 * Book book1 = bookRepository.findById(2L).get();
		 * 
		 * System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + book1.getId());
		 * 
		 * Review review1 = new Review(); review1.setComment("First Comment.");
		 * review1.setRatingStars(3L);
		 * 
		 * book1.addReview(review1);
		 * 
		 * bookService.save(book1);
		 */
	}

}
