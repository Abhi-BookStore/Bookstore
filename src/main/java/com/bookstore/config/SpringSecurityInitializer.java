package com.bookstore.config;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

public class SpringSecurityInitializer extends AbstractSecurityWebApplicationInitializer {

	@Override
	public boolean enableHttpSessionEventPublisher() {
		return true;
	}

}
