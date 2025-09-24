package com.flaregames.imports.tableslice;

import org.junit.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class TableSliceMetaTest {

    @Test
    public void build() {

        TableSliceMeta meta = TableSliceMeta.builder()
                .withBaseTable("some_table")
                .withS3Path("some/path")
                .withCreateSQL("(some create)")
                .withUpsertColumns("col1", "col2")
                .build();

        assertThat(meta.getViewName()).isEqualTo("some_table");
        assertThat(meta.getCreateSQL()).isEqualTo("(some create)");
        assertThat(meta.getS3Path("some_file.json.gz")).isEqualTo("some/path/some_file.json.gz");
        assertThat(meta.getSliceName(Instant.parse("2020-01-10T10:20:30.123Z"))).isEqualTo("m202001");
        assertThat(meta.getTableNamePattern()).isEqualTo("some_table_m%");
        assertThat(meta.getUpsertColumns()).containsExactly("col1", "col2");
        assertThat(meta.matchesTableName("some_table_m202001"));
        assertThat(meta.matchesTableName("some_table_m20200")).isFalse();
        assertThat(meta.matchesTableName("some_table_m202001_m202001")).isFalse();
    }

}