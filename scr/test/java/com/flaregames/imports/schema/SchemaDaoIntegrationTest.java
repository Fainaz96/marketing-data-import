package com.flaregames.imports.schema;

import com.google.inject.Inject;

import com.flaregames.imports.importlog.TransactioDao;
import com.flaregames.imports.integration.AbstractIntegrationTest;
import com.flaregames.imports.integration.TestDao;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class SchemaDaoIntegrationTest extends AbstractIntegrationTest {

    private static final String TEST_TABLE = "test_table";

    @Inject
    SchemaDao schemaDao;

    @Inject
    TestDao testDao;

    @Inject
    TransactioDao transactioDao;

    @Inject
    DBSchemaName schemaName;

    @Before
    public void before() {
        testDao.dropTable(schemaName.toString(), TEST_TABLE);
    }

    @Test
    public void createTable_tableCreated() {
        schemaDao.createTable(schemaName.toString(), TEST_TABLE, "(id INT8, name VARCHAR(16))");

        testDao.insertValues(schemaName.toString(), TEST_TABLE, "1,'hello'");
        assertThat(testDao.countRows(schemaName.toString(), TEST_TABLE)).isEqualTo(1);
    }

    @Test
    public void tableExists_exists() {
        schemaDao.createTable(schemaName.toString(), TEST_TABLE, "(id INT8, name VARCHAR(16))");

        assertThat(schemaDao.tableExists(schemaName.toString(), TEST_TABLE)).hasValue(true);
    }

    @Test
    public void tableExists_existsNot() {
        assertThat(schemaDao.tableExists(schemaName.toString(), TEST_TABLE)).isNotPresent();
    }

    @Test
    public void addColumn_columnAdded() {
        schemaDao.createTable(schemaName.toString(), TEST_TABLE, "(id INT8, name VARCHAR(16))");

        schemaDao.addColumn(schemaName.toString(), TEST_TABLE, "added_column VARCHAR(128)");

        testDao.insertValues(schemaName.toString(), TEST_TABLE, "1,'hello','world'");
        assertThat(testDao.queryTuples("added_column", schemaName.toString(), TEST_TABLE)).containsExactly(tuple("world"));
    }

}

