package com.flaregames.imports.s3;

import org.apache.commons.io.IOUtils;

import java.nio.charset.StandardCharsets;

public class S3TestClient extends S3Client {

    public S3TestClient(String region, String bucketName, String awsAccessKey, String awsSecretKey) {
        super(region, bucketName, awsAccessKey, awsSecretKey);
    }

    public String readS3File(String s3FileName) {
        try {
            return IOUtils.toString(s3Client.getObject(bucketName, s3FileName).getObjectContent(), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
