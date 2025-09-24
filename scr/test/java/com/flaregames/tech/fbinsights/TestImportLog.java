package com.flaregames.tech.fbinsights;

import com.flaregames.tech.lib.PartnerDBConf;

import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TestImportLog {

    @BeforeClass
    public static void assertUTC() {
        DateUtil.AssertUTC();
    }

    @Test
    public void testGetMissing() {

        DateTime from = new DateTime().withDate(2019, 4, 30).withTime(4, 0, 0, 0);
        DateTime to = new DateTime().withDate(2019, 5, 2).withTime(17, 0, 0, 0);

        List<ImportLogDao.LogEntry> list = new ArrayList<>();

        ImportLogDao dao = new ImportLogDao("testing").withSchemaName("unit_tests").withTableName("import_log");
        List<ImportLogDao.LogEntry> result = dao.getMissingHours(list, "bla", from, to, true);

        for (ImportLogDao.LogEntry ent : result) {
            System.out.println(ent);
        }

    }

    @Test
    public void testLog() throws SQLException {

        PartnerDBConf.setTestMode();

        ImportLogDao dao = new ImportLogDao("testing").withSchemaName("integration_tests").withTableName("import_log");
        Connection con = ResourceProvider.getConnection();

        dao.createTable(con);

        long today = DateUtil.todayStamp();

        ImportLogDao.LogEntry entry = dao.readLogStatus(con, "testing", today);
        assertThat(!entry.isSuccess());

        dao.logSuccess(con, entry);
        entry = dao.readLogStatus(con, "testing", today);
        assertThat(entry.isSuccess());
        System.out.println(entry);

        con.rollback();

    }

}
