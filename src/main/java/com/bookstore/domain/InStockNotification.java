package com.bookstore.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class InStockNotification {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private Long bookId;
	private String email;
	private boolean notified;
	private Date subscriptionTime;

	public InStockNotification() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isNotified() {
		return notified;
	}

	public void setNotified(boolean notified) {
		this.notified = notified;
	}

	public Date getSubscriptionTime() {
		return subscriptionTime;
	}

	public void setSubscriptionTime(Date subscriptionTime) {
		this.subscriptionTime = subscriptionTime;
	}

	public Long getBookId() {
		return bookId;
	}

	public void setBookId(Long bookId) {
		this.bookId = bookId;
	}

	@Override
	public String toString() {
		return "InStockNotification [id=" + id + ", bookId=" + bookId + ", email=" + email + ", notified=" + notified
				+ ", subscriptionTime=" + subscriptionTime + "]";
	}

}
