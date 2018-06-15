package com.bookstore.s3.service;

public interface S3Services {
	
	public void uploadObject(String keyName, String uploadFilePath);

}
