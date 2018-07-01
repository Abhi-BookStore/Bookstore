package com.bookstore.s3.service;

import java.net.URL;

import org.springframework.web.multipart.MultipartFile;

public interface S3Services {
	
	public void uploadObjectWithPublicAccess(String keyName, String uploadFilePath);

	public void uploadProfileImageToS3(MultipartFile file, String fileName);

	public URL getObjectAccessibleUrl(String string);

}
