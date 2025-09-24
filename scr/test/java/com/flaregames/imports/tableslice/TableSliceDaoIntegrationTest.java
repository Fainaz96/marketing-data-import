package com.flaregames.imports.tableslice;

import com.google.inject.Inject;

import com.flaregames.imports.config.ImportConfiguration;
import com.flaregames.imports.integration.AbstractIntegrationTest;
import com.flaregames.imports.schema.DBSchemaName;
import com.flaregames.imports.schema.IamRole;

import org.junit.Before;
import org.junit.Test;

public class TableSliceDaoIntegrationTest extends AbstractIntegrationTest {

    @Inject
    TableSliceDao dao;

    @Inject
    ImportConfiguration config;

    @Inject
    DBSchemaName schemaName;

    @Inject
    IamRole iamRole;

    private String quotePattern = "'%s'";
    private String urlPattern = "'s3://%s/%s'";
    private String bucket;

    @Before
    public void setUp() {

    }

    @Test
    public void copy() {
        //dao.copyToTempTable("some_table", String.format(urlPattern, bucket, "some/file.txt"), iamRole.toQuotedString());
    }

}
