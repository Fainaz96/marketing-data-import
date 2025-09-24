package com.flaregames.imports.schema;

import org.assertj.core.groups.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.tuple;

public class SchemaDaoMock implements SchemaDao {

    private Tuple createdTable;
    private List<Tuple> addedColumns = new ArrayList<>();
    private List<Tuple> alteredColumns = new ArrayList<>();

    @Override
    public void createTable(String schemaName, String tableName, String tableDefinition) {
        createdTable = tuple(schemaName, tableName, tableDefinition);
    }

    @Override
    public void createTempTable(String tempTableName, String tableDefinition) {
        createdTable = tuple(tempTableName, tableDefinition);
    }

    public Tuple getCreatedTable() {
        return createdTable;
    }

    public List<Tuple> getAddedColumns() {
        return addedColumns;
    }

    public List<Tuple> getAlteredColumns() {
        return alteredColumns;
    }

    @Override
    public void addColumn(String schemaName, String tableName, String columnDefinition) {
        addedColumns.add(tuple(schemaName, tableName, columnDefinition));
    }

    @Override
    public void alterColumnType(String schemaName, String tableName, String columnName, String typeDefinition) {
        addedColumns.add(tuple(schemaName, tableName, columnName, typeDefinition));
    }

    @Override
    public void renameColumn(String schemaName, String tableName, String oldName, String newName) {

    }

    @Override
    public void dropTempTable(String tableName) {

    }

    @Override
    public void createOrReplaceView(String schemaName, String tableName, String viewDefintion) {

    }

    @Override
    public Optional<Boolean> tableExists(String schemaName, String tableName) {
        return Optional.empty();
    }

    @Override
    public List<String> getColumns(String schemaName, String tableName) {
        return null;
    }

    @Override
    public void setSearchPath(String schemaName) {

    }
    
}
