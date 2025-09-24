package com.flaregames.imports.tableslice;

import com.flaregames.imports.schema.DBSchemaName;
import com.flaregames.imports.schema.SchemaDaoMock;

import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class TableSliceSchemaProcessorTest {

    private static String SCHEMA_NAME = "test_schema";
    private final SchemaDaoMock schemaDaoMock = new SchemaDaoMock();
    private final TableSliceDaoMock tableSliceDaoMock = new TableSliceDaoMock();
    private final TableSliceSchemaProcessor processor = new TableSliceSchemaProcessor(DBSchemaName.of(SCHEMA_NAME), tableSliceDaoMock,
            schemaDaoMock);

    @Test
    public void addColumns() {
        TableSliceMeta meta = TableSliceMeta.builder().withBaseTable("table").withCreateSQL("dummy").build();
        tableSliceDaoMock.setExistingNames(Arrays.asList("table_m202001", "table_m202003", "table_m202004"));

        processor.addColumns(meta, Arrays.asList("column1 int", "column2 varchar(10)"));

        assertThat(schemaDaoMock.getAddedColumns()).containsExactly(
                // @formatter:off
                tuple(SCHEMA_NAME,"table_m202001","column1 int"),
                tuple(SCHEMA_NAME,"table_m202001","column2 varchar(10)"),
                tuple(SCHEMA_NAME,"table_m202003","column1 int"),
                tuple(SCHEMA_NAME,"table_m202003","column2 varchar(10)"),
                tuple(SCHEMA_NAME,"table_m202004","column1 int"),
                tuple(SCHEMA_NAME,"table_m202004","column2 varchar(10)")
                // @formatter:on
        );

    }

    @Test
    public void alterColumnTypes() {
        TableSliceMeta meta = TableSliceMeta.builder().withBaseTable("table").withCreateSQL("dummy").build();
        tableSliceDaoMock.setExistingNames(Arrays.asList("table_m202001", "table_m202003", "table_m202004"));

        processor.alterColumnType(meta, "column1", "varchar(10)");

        assertThat(schemaDaoMock.getAddedColumns()).containsExactly(
                // @formatter:off
                tuple(SCHEMA_NAME,"table_m202001","column1", "varchar(10)"),
                tuple(SCHEMA_NAME,"table_m202003","column1", "varchar(10)"),
                tuple(SCHEMA_NAME,"table_m202004","column1", "varchar(10)")
                // @formatter:on
        );

    }

}