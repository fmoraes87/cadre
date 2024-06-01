package com.cadre.server.ext.amazon.s3;

import java.io.InputStream;
import java.util.Map;

import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.cadre.server.core.annotation.CustomService;
import com.cadre.server.core.boundary.MediaStore;
import com.cadre.server.core.entity.MMediaFolder;
import com.cadre.server.core.exception.CadreException;

@CustomService(serviceId = MediaStore.class )
public class AWSS3StoreImpl implements MediaStore {

	private static final Logger LOGGER = LoggerFactory.getLogger(AWSS3StoreImpl.class);

	private static final String AWS_REGION = "aws_region";
	private static final String SECRET_KEY_ID = "secret_key_id";
	private static final String ACCESS_KEY_ID = "access_key_id";

	private static final String AWS_S3_CONFIGURATION_ERROR = "AWS-S3 configuration error. Please check attributes";
	
	@Override
	public InputStream loadLOBData(MMediaFolder bucket, String fileName) {
		try {
			AmazonS3 s3Client = getAmasonS3Client(bucket.getAttributesAsMap());
			S3Object o = s3Client.getObject(bucket.getName(), fileName);
			S3ObjectInputStream s3is = o.getObjectContent();
			return s3is;
		} catch (AmazonServiceException e) {
			LOGGER.error("loadLOBData(fileName=" +fileName + ")", e);

			throw new CadreException(Status.INTERNAL_SERVER_ERROR.getStatusCode(),e.getErrorCode() ,e.getMessage());
		}
		
	}

	@Override
	public boolean delete(MMediaFolder bucket, String fileName) {
		try {
			AmazonS3 s3Client = getAmasonS3Client(bucket.getAttributesAsMap());
			s3Client.deleteObject(bucket.getName(), fileName);
			return true;
		} catch (AmazonServiceException e) {
			LOGGER.error("delete(fileName=" +fileName + ")", e);

			throw new CadreException(Status.INTERNAL_SERVER_ERROR.getStatusCode(),e.getErrorCode(),e.getMessage());
		}
		
		
	}

	@Override
	/**
	 * Upload object
	 */
	public boolean uploadFileContent(MMediaFolder bucket, String fileName, InputStream inputStream) {
		
        try {
        	AmazonS3 s3Client = getAmasonS3Client(bucket.getAttributesAsMap());
        	s3Client.putObject(bucket.getName(), fileName, inputStream,null);
        	return true;
        	
        } catch (AmazonServiceException e) {
			LOGGER.error("uploadFileContent(fileName=" +fileName + ")", e);

           throw new CadreException(Status.INTERNAL_SERVER_ERROR.getStatusCode(),e.getErrorCode(),e.getMessage());
        }

	}

	/**
	 * Get AmazonS3 client
	 * @param bucket
	 * @return
	 */
	private AmazonS3 getAmasonS3Client(Map<String,String> attributes) {
		if (attributes!=null && !attributes.isEmpty()) {
			//System.out.format("Uploading %s to S3 bucket %s...\n", file_path, bucket_name);
			
			String accessKeyId= attributes.get(ACCESS_KEY_ID);
			String secretKeyId= attributes.get(SECRET_KEY_ID);
			String awsRegion = attributes.get(AWS_REGION);
			
			if (StringUtils.isEmpty(accessKeyId) || StringUtils.isEmpty(secretKeyId) || StringUtils.isEmpty(awsRegion)) {
				throw new IllegalArgumentException(AWS_S3_CONFIGURATION_ERROR);
			}
			BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKeyId, secretKeyId);
			AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
					.withRegion(awsRegion)
					.withCredentials(new AWSStaticCredentialsProvider(awsCreds))
					.build();
			return s3Client;
		}else {
			throw new IllegalArgumentException("bucket==null");
		}
	}

}
