package com.bookstore.service;

import java.util.Map;

public interface EmailTemplateService {

    /**
     * Auto generates a string representation for the email body
     * @param mailContext
     * @param template The template filename. Default location is resources/templates
     * @return
     */

    public String generateFromContext(Map<String, Object> mailContext, String template);
}
