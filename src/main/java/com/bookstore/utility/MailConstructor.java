package com.bookstore.utility;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.bookstore.domain.Order;
import com.bookstore.domain.User;
import com.bookstore.domain.UserPayment;
import com.bookstore.domain.UserShipping;

@Component
public class MailConstructor {

	@Autowired
	private Environment env;
	
	@Autowired
	private TemplateEngine templateEngine;

	public SimpleMailMessage constructSimpleResetTokenEmail(String contextPath, Locale locale, String token, User user,
			String password) {

		String url = contextPath + "/createAccount?token=" + token;
		String message = "\n Please click on this link to verify your email address and update your personal information. Your password is: \n"
				+ password;

		SimpleMailMessage email = new SimpleMailMessage();
		email.setTo(user.getEmail());
		email.setFrom(env.getProperty("support.email"));
		email.setSubject("Abhi's bookstore - New User");
		email.setText(url + message);

		return email;
	}

	public SimpleMailMessage constructSimpleForgetPasswordEmail(String appUrl, Locale locale, String token, User user,
			String password) {

		String url = appUrl + "/createAccount?token=" + token;
		String message = "\n Please click on this link to Reset your password. Your temporary password is: \n"
				+ password;
		
		SimpleMailMessage email = new SimpleMailMessage();
		email.setTo(user.getEmail());
		email.setSubject("Abhi's bookstore - Password Reset");
		email.setFrom(env.getProperty("support.email"));
		email.setText(url + message);
		
		return email;
		
	}
	

	public SimpleMailMessage constructSimpleSetDefaultShippingAddressEmail(User user, UserShipping userShipping) {
		
		String message = "Hi "+user.getFirstName() + ",\n"
				+ "You have just changed your default shipping address. Your default shipping address has been changed to-\n\n"
				+ userShipping.getUserShippingAddress()
				+"\n"
				+userShipping.getUserShippingStreet1()
				+ ", "
				+ userShipping.getUserShippingStreet2()
				+"\n"
				+ userShipping.getUserShippingCity()
				+ ", "
				+ userShipping.getUserShippingState()
				+", "
				+userShipping.getUserShippingZipcode()
				+"."
				+ "\n\nIf you have not made this change please contact our customer service."
				+ "\n\nThank you for shopping with us," 
				+ "\nTeam AbhiBookStore.";
		
		SimpleMailMessage email = new SimpleMailMessage();
		email.setTo(user.getEmail());
		email.setFrom(env.getProperty("support.email"));
		email.setSubject("Your default shipping address has been changed.");
		email.setText(message);
		
		return email;		
	}
	
	
	public SimpleMailMessage constructSimpleSetDefaultCreditCard(User user, UserPayment userPayment) {

		String message = "Hi " + user.getFirstName() + ",\n"
				+ "You have just changed your default payment method. Default card details- .\n\n"
				+ "Card Name: "
				+ userPayment.getCardName() + "\nCard Holder Name: " + userPayment.getHolderName() + "\n"
				+ "Card's last 4 digit: "
				+ userPayment.getCardNumber().substring(userPayment.getCardNumber().length() - 4,
						userPayment.getCardNumber().length())
				+ "\n\nIf you have not made this change please contact our customer service."
				+ "\n\nThank you for shopping with us," 
				+ "\nTeam AbhiBookStore.";
		
		SimpleMailMessage email = new SimpleMailMessage();
		email.setTo(user.getEmail());
		email.setFrom(env.getProperty("support.email"));
		email.setSubject("Your default payment method has been changed.");
		email.setText(message);

		return email;
	}

	@Async
	public CompletableFuture<MimeMessagePreparator> constructSimpleOrderPlacedEmail(Order order, User user, Locale locale) {
		
		Context context = new Context();
		context.setVariable("order", order);
		context.setVariable("user", user);
		context.setVariable("cartItemList", order.getCartItemList());
		String text = templateEngine.process("orderConfirmationEmailTemplate", context);
		
		MimeMessagePreparator messagePreparator = new MimeMessagePreparator() {
			
			@Override
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper email = new MimeMessageHelper(mimeMessage);
				email.setTo(user.getEmail());
				email.setFrom(new InternetAddress("abhibookstore123@gmail.com"));
				email.setSubject("Order Confirmation - "+ order.getId());
				email.setText(text, true);				
			}
		};
		
		return CompletableFuture.completedFuture(messagePreparator);
	}
	

}
