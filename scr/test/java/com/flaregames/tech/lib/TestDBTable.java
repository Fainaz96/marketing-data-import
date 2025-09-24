package com.flaregames.tech.lib;

import com.flaregames.tech.fbinsights.ResourceProvider;
import com.flaregames.tech.lib.db.DB;
import com.flaregames.tech.lib.db.DBTable;
import com.flaregames.tech.lib.db.DBTableSlice;
import com.flaregames.tech.lib.db.DBTempTable;
import com.flaregames.tech.lib.guessql.SQLMeta;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TestDBTable {

    private static String SCHEMA_NAME = "";

    public static class Bla {

        @SQLMeta
        public String name = "name";
        public long id = 4723984379l;

        public static String columns = "id INTEGER, name VARCHAR(128)";

        public Bla(int k) {
            id = k;
        }

    }

    Connection con;

    @Before
    public void setup() throws SQLException {
        PartnerDBConf.setTestMode();
        con = ResourceProvider.getConnection();
    }

    @Test
    public void testTempTable() throws Exception {

        DBTempTable tempTable = DB.createTempTable("test_table_temp").withColumns(Bla.columns).withS3File("test_temp_table");

        assertThat(tempTable.getFQTableName()).isEqualTo("test_table_temp");

        Statement stm = con.createStatement();
        for (int k = 0; k < 100; k++) {
            tempTable.addObject(new Bla(k));
        }
        tempTable.create(stm);
        tempTable.uploadS3File();
        tempTable.copyFromS3File(stm);
        stm.executeBatch();

        ResultSet rs = stm.executeQuery("select count(*) as count from " + tempTable.getFQTableName());
        assertThat(rs.next()).isTrue();
        assertThat(rs.getInt("count")).isEqualTo(100);

        con.commit();

    }

    @Test
    public void testSlicedTable() throws Exception {

        long stamp1 = new DateTime(2019, 05, 06, 0, 0, 0).getMillis();
        DBTableSlice tableSlice1 = DB.createTableSlice("integration_tests", "test_table_slice", stamp1)
                .withColumns(Bla.columns)
                .withS3File("test_temp_table");

        assertThat(tableSlice1.getFQTableName()).isEqualTo("integration_tests.test_table_slice_m201905");

        long stamp2 = new DateTime(2019, 06, 21, 0, 0, 0).getMillis();
        DBTableSlice tableSlice2 = DB.createTableSlice("integration_tests", "test_table_slice", stamp2)
                .withColumns(Bla.columns)
                .withS3File("test_temp_table");

        assertThat(tableSlice2.getFQTableName()).isEqualTo("integration_tests.test_table_slice_m201906");

        Statement stm = con.createStatement();
        stm.execute("DROP VIEW IF EXISTS " + tableSlice2.getFQViewName());
        stm.execute("DROP TABLE IF EXISTS " + tableSlice1.getFQTableName());
        stm.execute("DROP TABLE IF EXISTS " + tableSlice2.getFQTableName());

        for (int k = 0; k < 100; k++) {
            tableSlice1.addObject(new Bla(k));
        }
        tableSlice1.create(stm);
        tableSlice1.uploadS3File();
        tableSlice1.copyFromS3File(stm);

        for (int k = 100; k < 200; k++) {
            tableSlice2.addObject(new Bla(k));
        }
        tableSlice2.create(stm);
        tableSlice2.uploadS3File();
        tableSlice2.copyFromS3File(stm);

        stm.executeBatch();

        List<String> existing = tableSlice1.getExistingTableSlices(con);

        tableSlice1.createView(stm, existing, -1);

        stm.executeBatch();

        ResultSet rs = stm.executeQuery("select count(*) as count from " + tableSlice1.getFQViewName());
        assertThat(rs.next()).isTrue();
        assertThat(rs.getInt("count")).isEqualTo(200);

        con.commit();

    }

    @Test
    public void testTable() throws Exception {

        DBTable tempTable = DB.createTable("integration_tests", "test_table").withColumns(Bla.columns).withS3File("test_table");

        assertThat(tempTable.getFQTableName()).isEqualTo("integration_tests.test_table");

        Statement stm = con.createStatement();

        stm.execute("DROP TABLE IF EXISTS " + tempTable.getFQTableName());

        for (int k = 0; k < 100; k++) {
            tempTable.addObject(new Bla(k));
        }
        tempTable.create(stm);
        tempTable.uploadS3File();
        tempTable.copyFromS3File(stm);
        stm.executeBatch();

        ResultSet rs = stm.executeQuery("select count(*) as count from " + tempTable.getFQTableName());
        assertThat(rs.next()).isTrue();
        assertThat(rs.getInt("count")).isEqualTo(100);

        con.commit();

    }

}
