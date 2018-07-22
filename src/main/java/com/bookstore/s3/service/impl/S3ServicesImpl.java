package com.bookstore.s3.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import com.amazonaws.services.s3.model.ObjectMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.datapipeline.model.Field;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.bookstore.s3.service.S3Services;

@Service
public class S3ServicesImpl implements S3Services {

	private Logger logger = LoggerFactory.getLogger(S3ServicesImpl.class);

	@Autowired
	private AmazonS3 s3client;

	@Value("${jsa.s3.bucket}")
	private String bucketName;

/*
	@Override
	public String uploadObjectWithPublicAccess(String keyName, String uploadFilePath) {

		String orderPdfUrl = "";
		try {
			File file = new File(uploadFilePath);
			// s3client.putObject(bucketName, keyName, file);
			System.out.println("********* Uploading file to AWS with public FileName ****" + keyName);
			System.out.println("********* Uploading file to AWS with public access Location ****" + uploadFilePath);
			s3client.putObject(
					new PutObjectRequest(bucketName, keyName, file).withCannedAcl(CannedAccessControlList.PublicRead));
			System.out.println(" ************** file URL: " + s3client.getUrl(bucketName, keyName));
			orderPdfUrl = s3client.getUrl(bucketName, keyName).toString();
			logger.info("File upload done for file: " + keyName + " from location: " + uploadFilePath);

		} catch (AmazonServiceException ase) {
			logger.info("Caught an AmazonServiceException from PUT requests, rejected reasons:");
			logger.info("Error Message:    " + ase.getMessage());
			logger.info("HTTP Status Code: " + ase.getStatusCode());
			logger.info("AWS Error Code:   " + ase.getErrorCode());
			logger.info("Error Type:       " + ase.getErrorType());
			logger.info("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			logger.info("Caught an AmazonClientException: ");
			logger.info("Error Message: " + ace.getMessage());
		}
		return orderPdfUrl;
	}
*/

	@Override
	public void uploadProfileImageToS3(MultipartFile mFile, String fileName) {

		logger.info("**** Uploading profile Image to AWS ****** profileIamge Name: "+ fileName);
		try {
			File file = convertMultiPartToFile(mFile);
			uploadFileTos3bucket(fileName, file);
			file.delete();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	// S3 bucket uploading method requires File as a parameter,
	// but we have MultipartFile, so we need to add method which can make this
	// conversion.
	private File convertMultiPartToFile(MultipartFile file) throws IOException {

		File convFile = new File(file.getOriginalFilename());
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();
		return convFile;
	}

	private void uploadFileTos3bucket(String fileName, File file) {
		
		s3client.putObject(new PutObjectRequest(bucketName, fileName, file).withCannedAcl(CannedAccessControlList.PublicRead));
		
	}

	@Override
	public URL getObjectAccessibleUrl(String imageName) {
		return s3client.getUrl(bucketName, imageName);
	}


	/**
	 * Uploading {@link ByteArrayInputStream} to S3
	 *
	 * @param fileName
	 * @param byteArrayInputStream
	 * @return
	 */
	@Override
	public String uploadStreamObjectToS3WithPublicAccess(String fileName, ByteArrayInputStream byteArrayInputStream) {

		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, byteArrayInputStream, new ObjectMetadata()).withCannedAcl(CannedAccessControlList.PublicRead);
		s3client.putObject(putObjectRequest);
		String orderPdfUrl = s3client.getUrl(bucketName, fileName).toString();
		return orderPdfUrl;
	}
}