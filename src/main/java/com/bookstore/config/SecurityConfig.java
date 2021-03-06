package com.bookstore.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.bookstore.service.impl.UserSecurityService;
import com.bookstore.utility.SecurityUtility;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private Environment env;
	
	@Autowired
	private UserSecurityService userSecurityService;
	
	
	private BCryptPasswordEncoder passwordEncoder() {
		return SecurityUtility.passwordEncoder();
	}
	
	private static final String[] publicMatcher = {
			"/css/**",
			"/js/**",
			"/image/**",
			"/",
			"/myAccount",
			"/createAccount",
			"/forgetPassword",
			"/login",
			"/fonts/**",
			"/updateUserInfo",
			"/bookShelf",
			"/bookDetail",
			"/search",
			"/searchByCategory",
			"/users",
			"/users/*",
			"/bookDetail/addReview",
			"/shoppingCart/notifyInStock",
			"/referral-registration"
	};
	
	@Override
	protected void configure(HttpSecurity http) throws Exception { // @formatter:off
	 	
		http.
			authorizeRequests()
			.antMatchers(publicMatcher)
			.permitAll()
			.anyRequest()
			.authenticated();
		
//		http
//			.sessionManagement()
//			.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
//			.maximumSessions(1)
//			.expiredUrl("/?expired");
		
		http
			.csrf().disable().cors().disable()
			.formLogin().failureUrl("/login?error")
			.defaultSuccessUrl("/")
			.loginPage("/login")
			.permitAll()
			.and()
			.logout()
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
			.logoutSuccessUrl("/login")
			.deleteCookies("remember-me")
			.permitAll()
			.and()
			.rememberMe()
			.and()
			.exceptionHandling().accessDeniedPage("/access-denied");
				
		
		// @formatter:on

	}
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		
		auth.userDetailsService(userSecurityService).passwordEncoder(passwordEncoder());
	}
	
	

}
