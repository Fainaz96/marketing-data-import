package com.flaregames.imports.s3;

import com.google.inject.Inject;

import com.flaregames.imports.integration.AbstractIntegrationTest;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class S3ClientIntegrationTest extends AbstractIntegrationTest {

    @Inject
    S3Client s3Client;

    @Inject
    S3TestClient s3TestClient;

    private final static String TEST_FILE_NAME = "s3client_integration_test/testfile.txt";

    private File createTempFile(String content) {
        try {
            File file = File.createTempFile("test_", "");
            try (FileOutputStream out = new FileOutputStream(file)) {
                out.write(content.getBytes());
            }
            return file;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Before
    public void before() {
        s3TestClient.delete(TEST_FILE_NAME);
    }

    @Test
    public void upload_fileUploaded() {
        File file = createTempFile("some content");
        s3Client.upload(TEST_FILE_NAME, file);
        assertThat(s3TestClient.readS3File(TEST_FILE_NAME)).isEqualTo("some content");
    }

}
