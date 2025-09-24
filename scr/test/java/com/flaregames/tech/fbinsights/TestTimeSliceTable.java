package com.flaregames.tech.fbinsights;

import com.flaregames.tech.lib.PartnerDBConf;
import com.flaregames.tech.lib.TimeSlice;
import com.flaregames.tech.lib.TimeSliceTable;
import com.flaregames.tech.lib.TimeSliceTableWithS3Files;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class TestTimeSliceTable {

    @BeforeClass
    public static void assertUTC() {
        DateUtil.AssertUTC();
    }

    public static class TestObj {

        public Date ts;
        public String value;

        public TestObj(Date ts, String value) {
            this.ts = ts;
            this.value = value;
        }
    }

    @Test
    public void testCreate() throws SQLException {

        PartnerDBConf.setTestMode();

        TimeSlice slice = TimeSlice.createMonthSlice();
        TimeSliceTable tab = new TimeSliceTable(slice)
                .withTable("integration_tests", "time_slice")
                .withCreateSql("(ts TIMESTAMP, value VARCHAR(128))");

        tab.addSlice(DateUtil.getDay(2017, 1, 1).getTime().getTime());
        tab.addSlice(DateUtil.getDay(2017, 2, 1).getTime().getTime());

        Connection con = ResourceProvider.getConnection();
        Statement stm = con.createStatement();

        tab.executeSync(stm);

        stm.executeBatch();

        PreparedStatement sel = con.prepareStatement("SELECT * FROM information_schema.tables WHERE table_name LIKE 'time_slice%'");

        ResultSet rs = sel.executeQuery();
        Set<String> res = new HashSet<>();
        while (rs.next()) {
            res.add(rs.getString("table_name") + " " + rs.getString("table_type"));
        }

        assertThat(res).contains("time_slice VIEW", "time_slice_m201701 BASE TABLE", "time_slice_m201702 BASE TABLE");

        con.rollback();

    }

    @Test
    public void testCreateWithS3() throws IOException, SQLException {

        PartnerDBConf.setTestMode();

        Connection con = ResourceProvider.getConnection();

        TimeSlice slice = TimeSlice.createMonthSlice();
        TimeSliceTableWithS3Files table = new TimeSliceTableWithS3Files(slice);

        table.withTable("integration_tests", "time_slice_s3")
                .withS3File("unit_tests", null)
                .withCreateSql("(ts TIMESTAMP, value VARCHAR(128))");

        TestObj test[] = new TestObj[]{
                new TestObj(DateUtil.getDay(2018, 1, 1).getTime(), "value-1"),
                new TestObj(DateUtil.getDay(2018, 2, 1).getTime(), "value-2"),
        };
        table.addObject(test[0].ts.getTime(), test[0]);
        table.addObject(test[1].ts.getTime(), test[1]);

        table.upload();
        table.insert(con);

        assertThat(table.getExistingTableSlices(con)).hasSize(2);

        int idx = 0;
        ResultSet rs = con.prepareStatement("SELECT * FROM integration_tests.time_slice_s3 ORDER BY ts ASC").executeQuery();
        while (rs.next()) {
            System.out.println(rs.getTimestamp("ts") + " " + rs.getString("value"));
            System.out.println(test[idx].ts + " " + test[idx].value);
            assertThat(rs.getTimestamp("ts").getTime()).isEqualTo(test[idx].ts.getTime());
            assertThat(rs.getString("value")).isEqualTo(test[idx].value);
            idx++;
        }
        assertThat(idx).isEqualTo(2);

        con.rollback();
    }

}
