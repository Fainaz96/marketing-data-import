package com.flaregames.imports.tableslice;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class TableSliceDaoTest {

    @Test
    public void upsert_asExpected() {

        TableSliceDaoMock daoMock = new TableSliceDaoMock();

        daoMock.upsert("schema", "table", "temp_table", "key1", "key2");

        assertThat(daoMock.getDeleteArgs()).isEqualTo(tuple("schema", "table",
                "USING temp_table AS source WHERE source.key1 = schema.table.key1 AND source.key2 = schema.table.key2"));

        assertThat(daoMock.getInsertFromTempTableArgs()).isEqualTo(tuple("schema", "table", "temp_table"));
    }

    @Test
    public void upsert_noPKs_illegalSql() {

        TableSliceDaoMock daoMock = new TableSliceDaoMock();

        daoMock.upsert("schema", "table", "temp_table");

        assertThat(daoMock.getDeleteArgs()).isEqualTo(tuple("schema", "table", "USING temp_table AS source WHERE "));

        assertThat(daoMock.getInsertFromTempTableArgs()).isEqualTo(tuple("schema", "table", "temp_table"));
    }

}
