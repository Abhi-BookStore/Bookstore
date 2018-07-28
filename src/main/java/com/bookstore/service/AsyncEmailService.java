package com.bookstore.service;

import java.io.IOException;
import java.util.Map;

public interface AsyncEmailService {

    void send(String from, String recipient, String replyTo, String subject, String text) throws IOException;

    /**
     * Sends email using variable automatically inserted into     *
     * @param from
     * @param recipient
     * @param replyTo
     * @param template
     * @param mailContext
     */
    void sendWithContext(String from, String recipient, String replyTo, String subject, String template, Map<String, Object> mailContext);

}
