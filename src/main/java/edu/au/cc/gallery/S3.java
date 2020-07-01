package edu.au.cc.gallery;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketConfiguration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

public class S3 {
    private static final Region region = Region.US_WEST_1;
    private static S3Client client;

    public static void connect() {
        client = S3Client.builder().region(region).build();
    }

    public void createBucket(String bucketName) {
	CreateBucketRequest createBucketRequest = CreateBucketRequest
	    .builder()
	    .bucket(bucketName)
	    .createBucketConfiguration(CreateBucketConfiguration.builder()
				       .locationConstraint(region.id())
				       .build())
	    .build();
	client.createBucket(createBucketRequest);
    }

    public void putObject(String bucketName, String key, String value) {
	PutObjectRequest por = PutObjectRequest.builder()
	    .bucket(bucketName)
	    .key(key)
	    .build();
	client.putObject(por, RequestBody.fromString(value));
    }
	
	public static boolean deleteObject(String username, String objectName) {
	    String bucketName = "edu.au.cc.arg0055.image-gallery/images/" + username;
	    DeleteObjectRequest dor = DeleteObjectRequest.builder()
		.bucket(bucketName)
		.key(objectName)
		.build();
	    try {
		client.deleteObject(dor);
			return true;
		} catch (Exception e) {
		System.out.println(e);
		      return false;
		}
	}
}
