package com.flaregames.imports.importlog;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ImportLogDaoMock implements ImportLogDao {

    private List<ImportLogEntry> logEntries = new ArrayList<>();
    private boolean insertCalled;
    private boolean selectCalled;
    private Instant selectSucceededDayMin;
    private Instant selectSucceededDayMax;

    private Stream<ImportLogEntry> filterEntries(String jobName, String logKey) {
        return logEntries.stream().filter(entry -> entry.job_name.equals(jobName) && entry.log_key.equals(logKey));
    }

    @Override
    public ImportLogEntry selectLogStatus(String schemaName, String jobName, String logKey, Instant logStamp, Integer logHour) {
        selectCalled = true;
        return filterEntries(jobName, logKey).filter(entry -> entry.log_stamp.equals(logStamp))
                .filter(entry -> entry.log_hour.equals(logHour))
                .findFirst()
                .orElse(null);
    }

    @Override
    public ImportLogEntry selectLogStatus(String schemaName, String jobName, String logKey, Instant logStamp) {
        selectCalled = true;
        return filterEntries(jobName, logKey).filter(entry -> entry.log_stamp.equals(logStamp)).findFirst().orElse(null);
    }

    @Override
    public List<ImportLogEntry> selectSucceeded(String schemaName, String jobName, String logKey, Instant dayMin, Instant dayMax) {
        selectSucceededDayMin = dayMin;
        selectSucceededDayMax = dayMax;
        return logEntries;
    }

    @Override
    public int insertLogEntry(String schemaName, ImportLogEntry logEntry) {
        this.logEntries.add(logEntry);
        insertCalled = true;
        return 1;
    }

    public void addLogEntry(ImportLogEntry logEntry) {
        this.logEntries.add(logEntry);
    }

    public ImportLogEntry getLogEntry() {
        return logEntries.stream().findFirst().orElse(null);
    }

    public boolean isInsertCalled() {
        return insertCalled;
    }

    public boolean isSelectCalled() {
        return selectCalled;
    }

    public Instant getSelectSucceededDayMin() {
        return selectSucceededDayMin;
    }

    public Instant getSelectSucceededDayMax() {
        return selectSucceededDayMax;
    }

    @Override
    public void lockStatusTable(String schemaName) {

    }
}
