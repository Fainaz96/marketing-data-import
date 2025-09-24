package com.flaregames.tech.appsflyer;

import com.flaregames.tech.fbinsights.DateUtil;
import com.flaregames.tech.fbinsights.S3JsonFile;
import com.flaregames.tech.lib.TimeSlice;
import com.flaregames.tech.lib.TimeSliceTableWithS3Files;

import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStream;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class TestAFDataLockerLoader {

    @BeforeClass
    public static void assertUTC() {
        DateUtil.AssertUTC();
    }

    @Test
    public void testDateKey() {
        DateTime dt = new DateTime().withDate(2019, 2, 7).withTime(0, 0, 0, 0);
        assertThat(DateUtil.dateKey(dt, 10)).isEqualTo(2019020710);
        dt = new DateTime().withDate(2019, 11, 30).withTime(0, 0, 0, 0);
        assertThat(DateUtil.dateKey(dt, 25)).isEqualTo(2019113025);
    }

}
