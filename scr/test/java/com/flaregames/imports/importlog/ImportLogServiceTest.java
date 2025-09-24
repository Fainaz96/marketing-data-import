package com.flaregames.imports.importlog;

import com.flaregames.imports.schema.DBSchemaName;
import com.flaregames.microservice.SystemTimeRule;

import org.junit.Rule;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class ImportLogServiceTest {

    private final ImportLogDaoMock importLogDaoMock = new ImportLogDaoMock();
    private final TransactionDaoMock transactionDaoMock = new TransactionDaoMock();
    private final DBSchemaName dbSchemaName = DBSchemaName.of("test_schema");
    private final ImportLogServiceDefault service = new ImportLogServiceDefault(dbSchemaName, importLogDaoMock, transactionDaoMock,
            new ImportLogStatusService());

    @Rule
    public SystemTimeRule systemTimeRule = new SystemTimeRule();

    private boolean executedFlag = false;

    @Test
    public void createDailyJob_daosCalledAsExpected() {
        systemTimeRule.useFixed(ImportLogEntryTest.INSTANT_2);
        service.checkForDailyJob(ImportLogEntryTest.JOB_NAME, ImportLogEntryTest.LOG_KEY, ImportLogEntryTest.INSTANT_1).execute(() -> {
            systemTimeRule.useFixed(ImportLogEntryTest.INSTANT_3);
            executedFlag = true;
            //ImportLogEntryTest.assertEquals(ImportLogEntryTest.buildEntry(false, null).build(), log);
        });

        assertThat(executedFlag);
        assertThat(importLogDaoMock.isSelectCalled());
        assertThat(importLogDaoMock.isInsertCalled());
        assertThat(transactionDaoMock.isTransactionComplete());

        ImportLogEntry expected = ImportLogEntryTest.buildEntry(true, null).build();
        ImportLogEntryTest.assertEquals(expected, importLogDaoMock.getLogEntry());
    }

    @Test
    public void createDailyJob_executionException() {
        service.checkForDailyJob(ImportLogEntryTest.JOB_NAME, ImportLogEntryTest.LOG_KEY, ImportLogEntryTest.INSTANT_1).execute(() -> {
            throw new RuntimeException();
        });

        assertThat(importLogDaoMock.isSelectCalled()).isTrue();
        assertThat(importLogDaoMock.isInsertCalled()).isFalse();
        assertThat(transactionDaoMock.isTransactionComplete()).isFalse();

        assertThat(service.getFailedJobs()).hasSize(1);
        assertThat(service.getFailedJobs()).extracting("job_name", "log_key", "log_stamp", "status")
                .containsExactly(tuple(ImportLogEntryTest.JOB_NAME, ImportLogEntryTest.LOG_KEY, ImportLogEntryTest.INSTANT_1, null));
    }

    @Test
    public void createDailyJob_executionSkipped() {
        importLogDaoMock.addLogEntry(ImportLogEntryTest.buildEntry(true, null).build());
        service.checkForDailyJob(ImportLogEntryTest.JOB_NAME, ImportLogEntryTest.LOG_KEY, ImportLogEntryTest.INSTANT_1).execute(() -> {
            executedFlag = true;
        });

        assertThat(executedFlag).isFalse();
        assertThat(importLogDaoMock.isSelectCalled()).isTrue();
        assertThat(importLogDaoMock.isInsertCalled()).isFalse();
        assertThat(transactionDaoMock.isTransactionComplete()).isFalse();
    }

}
