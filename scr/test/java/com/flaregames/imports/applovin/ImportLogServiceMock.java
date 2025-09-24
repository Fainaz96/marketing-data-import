package com.flaregames.imports.applovin;

import com.flaregames.imports.importlog.ImportLogEntry;
import com.flaregames.imports.importlog.ImportLogId;
import com.flaregames.imports.importlog.ImportLogJob;
import com.flaregames.imports.importlog.ImportLogService;
import com.flaregames.imports.importlog.TransactioDao;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ImportLogServiceMock implements ImportLogService {

    private final TransactioDao dao;

    public Instant checkDay = null;

    public ImportLogServiceMock(TransactioDao dao) {
        this.dao = dao;
    }

    private class ImportLogJobMock implements ImportLogJob {
        @Override
        public void execute(Runnable runnable) {
            dao.runInTransaction(runnable);
        }

        @Override
        public void executeUnchecked(Runnable runnable) {
            dao.runInTransaction(runnable);
        }

        @Override
        public void execute(Consumer<ImportLogId> consumer) {
            dao.runInTransaction(() -> consumer.accept(new ImportLogId()));
        }

        @Override
        public void cancel() { dao.rollback(); }
    }

    @Override
    public ImportLogJob checkForDailyJob(String jobName, String logKey, Instant day) {
        checkDay = day;
        return new ImportLogJobMock();
    }

    @Override
    public List<ImportLogEntry> getFailedJobs() {
        return new ArrayList<>();
    }

    @Override
    public Optional<ImportLogEntry> getLogEntry(String jobName, String logKey, Instant day) {
        return Optional.empty();
    }

    @Override
    public int getExitStatus() {
        return 0;
    }

    @Override
    public boolean markDailyJobSuccess(String jobName, String logKey, Instant day) {
        return false;
    }

    @Override
    public ImportLogJob checkForHourlyJob(String jobName, String logKey, Instant day, int hour) {
        return null;
    }

    @Override
    public List<ImportLogEntry> loadSucceeded(String jobName, String logKey, Instant fromDay, Instant toDay) {
        return null;
    }

    @Override
    public void exitWithStatus() {}

}
