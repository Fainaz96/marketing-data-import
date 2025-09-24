package com.flaregames.imports.schema;

import com.flaregames.tech.lib.guessql.SQLMeta;

import org.junit.Test;

import java.sql.Timestamp;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class GuessQLTableMapperTest {

    private final GuessQLTableMapper tableMapper = new GuessQLTableMapper();

    @Test
    public void getTableDefinition_asExpected() {

        assertThat(tableMapper.getTableDefinition(TestDbRecord.class)).isEqualTo(
                // @formatter:off
                "(long_int8 INT8,\n" +
                        "int_int4 INT4,\n" +
                        "boolean_bool BOOL,\n" +
                        "str_default VARCHAR(128),\n" +
                        "str_var_32 VARCHAR(32),\n" +
                        "str_fix_3 CHAR(3),\n" +
                        "instant_timestamp TIMESTAMP,\n" +
                        "timestamp_timestamp TIMESTAMP,\n" +
                        "float_float4 FLOAT4,\n" +
                        "double_float8 FLOAT8,\n" +
                        "enum_var_32 VARCHAR(32),\n" +
                        "int_optional INT4,\n" +
                        "long_optional INT8,\n" +
                        "float_optional FLOAT4,\n" +
                        "double_optional FLOAT8,\n" +
                        "the_dist_key INT8,\n" +
                        "sort_key_1 VARCHAR(128),\n" +
                        "sort_key_2 INT4)\n" +
                        "DISTKEY(the_dist_key)\n" +
                        "SORTKEY(sort_key_1,sort_key_2)"
                // @formatter:on
        );

    }

    private enum TestEnum {
        SOME_VALUE, OTHER_VALUE
    }

    private static class TestDbRecord {
        public long long_int8;
        public int int_int4;
        public boolean boolean_bool;
        public String str_default;
        @SQLMeta(size = 32)
        public String str_var_32;
        @SQLMeta(size = 3, varying = false)
        public String str_fix_3;
        public Instant instant_timestamp;
        public Timestamp timestamp_timestamp;
        public float float_float4;
        public double double_float8;
        public TestEnum enum_var_32;

        public Integer int_optional;
        public Long long_optional;
        public Float float_optional;
        public Double double_optional;

        @SQLMeta(distkey = true)
        public long the_dist_key;
        @SQLMeta(sortkey = true)
        public String sort_key_1;
        @SQLMeta(sortkey = true)
        public int sort_key_2;

    }

}