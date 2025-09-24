package com.flaregames.imports.importlog;

import com.google.inject.Inject;

import com.flaregames.imports.integration.AbstractIntegrationTest;
import com.flaregames.imports.integration.TestDao;
import com.flaregames.imports.schema.DBSchemaName;
import com.flaregames.imports.schema.SchemaService;

import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ImportLogDaoIntegrationTest extends AbstractIntegrationTest {

    @Inject
    ImportLogDao importLogDao;

    @Inject
    TestDao testDao;

    @Inject
    SchemaService schemaService;

    @Inject
    DBSchemaName schemaName;

    @Before
    public void before() {
        testDao.dropTable(schemaName.toString(), "import_log");
        schemaService.syncTable(ImportLogEntry.class, "import_log");
    }

    @Test
    public void selectLogStatus_noRecords() {
        ImportLogEntry logEntry = importLogDao.selectLogStatus(schemaName.toString(), ImportLogEntryTest.JOB_NAME,
                ImportLogEntryTest.LOG_KEY, ImportLogEntryTest.INSTANT_1);

        assertThat(logEntry).isNull();
    }

    @Test
    public void selectSucceded_noRecords() {
        List<ImportLogEntry> logEntryList = importLogDao.selectSucceeded(schemaName.toString(), ImportLogEntryTest.JOB_NAME,
                ImportLogEntryTest.LOG_KEY, Instant.parse("2020-01-01T00:00:00Z"), Instant.parse("2020-01-05T00:00:00Z"));

        assertThat(logEntryList).isEmpty();
    }

    @Test
    public void selectSucceded_allInRange() {
        Arrays.asList(0, 1, 2, 3, 4).forEach((offset) ->
                importLogDao.insertLogEntry(schemaName.toString(),
                        ImportLogEntryTest.buildEntry(true, null)
                                .withLogStamp(Instant.parse("2020-01-05T00:00:00Z").minus(offset, ChronoUnit.DAYS))
                                .build())
        );

        List<ImportLogEntry> logEntryList = importLogDao.selectSucceeded(schemaName.toString(), ImportLogEntryTest.JOB_NAME,
                ImportLogEntryTest.LOG_KEY, Instant.parse("2020-01-02T00:00:00Z"), Instant.parse("2020-01-04T00:00:00Z"));

        assertThat(logEntryList).hasSize(3);
        assertThat(logEntryList).extracting("log_stamp")
                .containsExactly(Instant.parse("2020-01-02T00:00:00Z"), Instant.parse("2020-01-03T00:00:00Z"),
                        Instant.parse("2020-01-04T00:00:00Z"));
    }

    @Test
    public void insertSelect_asExpected() {
        ImportLogEntry entry = ImportLogEntryTest.buildEntry(false, null).build();
        importLogDao.insertLogEntry(schemaName.toString(), entry);
        ImportLogEntry selected = importLogDao.selectLogStatus(schemaName.toString(), entry.job_name, entry.log_key,
                entry.log_stamp);

        assertThat(entry).isNotSameAs(selected);
        ImportLogEntryTest.assertEquals(entry, selected);
    }

    @Test
    public void insertSelect_withHour_asExpected() {
        ImportLogEntry entry = ImportLogEntryTest.buildEntry(false, ImportLogEntryTest.HOUR).build();
        importLogDao.insertLogEntry(schemaName.toString(), entry);
        ImportLogEntry selected = importLogDao.selectLogStatus(schemaName.toString(), entry.job_name, entry.log_key,
                entry.log_stamp, entry.log_hour);

        assertThat(entry).isNotSameAs(selected);
        ImportLogEntryTest.assertEquals(entry, selected);
    }

}
