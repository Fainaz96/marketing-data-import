package com.flaregames.imports.integration;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import com.flaregames.imports.ImportsModule;
import com.flaregames.imports.config.ConfigSecrets;
import com.flaregames.imports.config.ImportConfiguration;
import com.flaregames.imports.config.S3Configuration;
import com.flaregames.imports.config.TestingConflet;
import com.flaregames.imports.s3.S3TestClient;
import com.flaregames.imports.schema.ClassToTableMapper;
import com.flaregames.imports.schema.GuessQLTableMapper;

import org.jdbi.v3.core.Jdbi;

public class IntegrationTestModule implements Module {

    @Provides
    @Singleton
    public ImportConfiguration getImportConfiguration(ConfigSecrets secrets) {
        return TestingConflet.getImportConfiguration(secrets);
    }

    @Provides
    @Singleton
    public S3TestClient getTestClient(ImportConfiguration config) {
        S3Configuration s3Configuration = config.getS3Configuration();
        return new S3TestClient(s3Configuration.getRegion(), s3Configuration.getBucketName(), s3Configuration.getS3AccessKey(),
                s3Configuration.getS3AccessSecret());

    }

    @Provides
    @Singleton
    public TestDao getTestDao(Jdbi jdbi) {
        return jdbi.onDemand(TestDao.class);
    }

    @Override
    public void configure(Binder binder) {
        binder.install(new ImportsModule());
    }
}
