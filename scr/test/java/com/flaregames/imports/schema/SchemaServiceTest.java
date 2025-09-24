package com.flaregames.imports.schema;

import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class SchemaServiceTest {

    private static final DBSchemaName SCHEMA_NAME = DBSchemaName.of("test_schema");
    private final SchemaDaoMock schemaDaoMock = new SchemaDaoMock();
    private final SchemaService service = new SchemaService(SCHEMA_NAME, schemaDaoMock, new ClassToTableMapperMock());

    @Test
    public void syncSchema_daoCalled() {

        service.syncTable(TestDbRecord.class, "test_table");

        assertThat(schemaDaoMock.getCreatedTable()).isEqualTo(tuple("test_schema", "test_table", "columns-for-TestDbRecord"));
    }

    private static class TestDbRecord {
        public long id;
        public String name;
    }

    private static class ClassToTableMapperMock implements ClassToTableMapper {
        @Override
        public String getTableDefinition(Class<?> forClass) {
            return String.format("columns-for-%s", forClass.getSimpleName());
        }

        @Override
        public String getTableDefinition(Class<?> forClass, List<String> sortKeys, String distKey) {
            return null;
        }

        @Override
        public List<String[]> getColumnDefinitions(Class<?> forClass) {
            return null;
        }
    }

}
