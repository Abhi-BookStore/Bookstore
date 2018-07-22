package com.bookstore.s3.service;

import java.io.ByteArrayInputStream;
import java.net.URL;

import org.springframework.web.multipart.MultipartFile;

public interface S3Services {
	
//	public String uploadObjectWithPublicAccess(String keyName, String uploadFilePath);

	public void uploadProfileImageToS3(MultipartFile file, String fileName);

	public URL getObjectAccessibleUrl(String string);

	String uploadStreamObjectToS3WithPublicAccess(String s, ByteArrayInputStream byteArrayInputStream);
}
