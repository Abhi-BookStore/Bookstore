package com.bookstore.service.impl;

import com.bookstore.service.EmailTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
public class ThymeleafTemplateServiceImpl implements EmailTemplateService {

    @Autowired
    private TemplateEngine engine;

    /**
     * Auto generates a string representation for the email body
     *
     * @param mailContext
     * @param template    The template filename. Default location is resources/templates
     * @return
     */
    @Override
    public String generateFromContext(Map<String, Object> mailContext, String template) {

        Context context = new Context();
        context.setVariables(mailContext);
        return engine.process(template, context);
    }
}
