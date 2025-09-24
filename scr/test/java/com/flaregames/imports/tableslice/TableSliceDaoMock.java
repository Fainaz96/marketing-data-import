package com.flaregames.imports.tableslice;

import org.assertj.core.groups.Tuple;

import java.util.List;

import static org.assertj.core.api.Assertions.tuple;

class TableSliceDaoMock implements TableSliceDao {

    private Tuple insertFromTempTableArgs;
    private Tuple deleteArgs;
    private Tuple createViewArgs;
    private List<String> existingNames;

    public void setExistingNames(List<String> existingNames) {
        this.existingNames = existingNames;
    }

    @Override
    public void copyToTempTable(String tempTableName, String s3Url, String iamRole) {

    }

    @Override
    public void copyToTable(String schemaName, String tableName, String s3Url, String iamRole) {

    }

    @Override
    public List<String> getExistingSliceNames(String schemaName, String baseTableName) {
        return existingNames;
    }

    @Override
    public void dropView(String schemaName, String tableName) {
    }

    @Override
    public void createOrReplaceView(String schemaName, String tableName, String viewDefintion) {
        createViewArgs = tuple(schemaName, tableName, viewDefintion);
    }

    @Override
    public int insertFromTempTable(String schemaName, String tableName, String tempTableName) {
        insertFromTempTableArgs = tuple(schemaName, tableName, tempTableName);
        return 0;
    }

    @Override
    public int delete(String schemaName, String tableName, String deleteClause) {
        deleteArgs = tuple(schemaName, tableName, deleteClause);
        return 0;
    }

    public Tuple getInsertFromTempTableArgs() {
        return insertFromTempTableArgs;
    }

    public Tuple getDeleteArgs() {
        return deleteArgs;
    }

    public Tuple getCreateViewArgs() {
        return createViewArgs;
    }
}
