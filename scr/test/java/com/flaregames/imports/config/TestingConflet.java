package com.flaregames.imports.config;

import com.flaregames.imports.schema.DBSchemaName;
import com.flaregames.imports.schema.IamRole;

public class TestingConflet {

    public static ImportConfiguration getImportConfiguration(ConfigSecrets secrets) {

        DatabaseConfiguration targetDatabase = new DatabaseConfiguration();
        targetDatabase.jdbcUri = "jdbc:redshift://flaregames-etl.testing.bi.flarecloud.net:5439/marketing_partners_integration_tests";
        targetDatabase.userName = "marketing_partners_integration_tests";
        targetDatabase.password = secrets.testing_dbPassword;
        targetDatabase.iamRole = IamRole.of("arn:aws:iam::388090105529:role/Redshift-flaregames-etl-staging_s3_access");

        S3Configuration s3Configuration = new S3Configuration();
        s3Configuration.bucketName = "flaregames-marketing-partner-integration-tests";
        s3Configuration.region = "us-west-2";
        s3Configuration.s3AccessKey = secrets.testing_s3AccessKey;
        s3Configuration.s3AccessSecret = secrets.testing_s3AccessSecret;

        ApplovinImportConfiguration applovinConfiguration = new ApplovinImportConfiguration();
        applovinConfiguration.accessKey = secrets.applovin_accessKey;

        ImportConfiguration importConfiguration = ImportsConflet.getImportsConfiguration("TESTING", secrets);
        importConfiguration.targetDatabase = targetDatabase;
        importConfiguration.targetSchemaName = DBSchemaName.of("integration_tests");
        importConfiguration.s3Configuration = s3Configuration;
        importConfiguration.applovinImportConfiguration = applovinConfiguration;

        return importConfiguration;
    }

}
