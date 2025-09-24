package com.flaregames.imports.integration;

import org.assertj.core.groups.Tuple;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Define;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.IntStream;

public interface TestDao {

    public class TupleMapper implements RowMapper<Tuple> {
        @Override
        public Tuple map(ResultSet rs, StatementContext ctx) throws SQLException {
            Object[] list = IntStream.range(1, rs.getMetaData().getColumnCount() + 1).mapToObj(index -> {
                try {
                    return rs.getObject(index);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }).toArray(Object[]::new);
            return Tuple.tuple(list);
        }
    }

    @SqlUpdate("CREATE TABLE IF NOT EXISTS <schemaName>.<tableName> <tableDefiniton>")
    void createTable(@Define("schemaName") String schemaName, @Define("tableName") String tableName,
            @Define("tableDefiniton") String tableDefinition);

    @SqlUpdate("DROP TABLE IF EXISTS <schemaName>.<tableName>")
    void dropTable(@Define("schemaName") String schemaName, @Define("tableName") String tableName);

    @SqlUpdate("DROP VIEW IF EXISTS <schemaName>.<viewName>")
    void dropView(@Define("schemaName") String schemaName, @Define("viewName") String viewName);

    @SqlUpdate("INSERT INTO <schemaName>.<tableName> VALUES (<values>)")
    void insertValues(@Define("schemaName") String schemaName, @Define("tableName") String tableName, @Define("values") String values);

    @SqlQuery("SELECT COUNT(*) FROM <schemaName>.<tableName>")
    int countRows(@Define("schemaName") String schemaName, @Define("tableName") String tableName);

    @RegisterRowMapper(TupleMapper.class)
    @SqlQuery("SELECT <columns> FROM <schemaName>.<tableName>")
    List<Tuple> queryTuples(@Define("columns") String columns, @Define("schemaName") String schemaName,
            @Define("tableName") String tableName);

}
