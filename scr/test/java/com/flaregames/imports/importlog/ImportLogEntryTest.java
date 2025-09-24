package com.flaregames.imports.importlog;

import org.junit.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class ImportLogEntryTest {

    public static final String JOB_NAME = "jobName";
    public static final String LOG_KEY = "logKey";
    public static final Instant INSTANT_1 = Instant.parse("2020-01-01T00:00:00Z");
    public static final Instant INSTANT_2 = Instant.parse("2020-01-01T00:01:02Z");
    public static final Instant INSTANT_3 = Instant.parse("2020-01-01T00:03:04Z");
    public static final int HOUR = 21;

    @Test
    public void builder_buildSucceeded() {
        ImportLogEntry entry = buildEntry(true, HOUR).build();

        assertThat(entry.job_name).isEqualTo(JOB_NAME);
        assertThat(entry.log_key).isEqualTo(LOG_KEY);
        assertThat(entry.log_stamp).isEqualTo(INSTANT_1);
        assertThat(entry.started).isEqualTo(INSTANT_2);
        assertThat(entry.ended).isEqualTo(INSTANT_3);
        assertThat(entry.status).isEqualTo(ImportLogStatus.SUCCESS);
        assertThat(entry.log_hour).isEqualTo(HOUR);
    }

    @Test
    public void builder_buildUnSucceeded() {
        ImportLogEntry entry = buildEntry(false, HOUR).build();

        assertThat(entry.job_name).isEqualTo(JOB_NAME);
        assertThat(entry.log_key).isEqualTo(LOG_KEY);
        assertThat(entry.log_stamp).isEqualTo(INSTANT_1);
        assertThat(entry.started).isEqualTo(INSTANT_2);
        assertThat(entry.ended).isNull();
        assertThat(entry.status).isNull();
        assertThat(entry.log_hour).isEqualTo(HOUR);
    }

    public static ImportLogEntry.Builder buildEntry(boolean succeeded, Integer logHour) {
        ImportLogEntry.Builder builder = ImportLogEntry.builder()
                .withJobName(JOB_NAME)
                .withLogKey(LOG_KEY)
                .withLogStamp(INSTANT_1)
                .withStarted(INSTANT_2)
                .withLogHour(logHour);
        if (succeeded) {
            builder.withEnded(INSTANT_3).withStatus(ImportLogStatus.SUCCESS);
        }
        return builder;
    }

    public static void assertEquals(ImportLogEntry expected, ImportLogEntry entry) {
        assertThat(entry.job_name).isEqualTo(expected.job_name);
        assertThat(entry.log_key).isEqualTo(expected.log_key);
        assertThat(entry.log_stamp).isEqualTo(expected.log_stamp);
        assertThat(entry.started).isEqualTo(expected.started);
        assertThat(entry.ended).isEqualTo(expected.ended);
        assertThat(entry.status).isEqualTo(expected.status);
        assertThat(entry.log_hour).isEqualTo(expected.log_hour);
    }

}
