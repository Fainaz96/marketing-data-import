package com.flaregames.imports.currency;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDaoMock implements CurrencyDao {

    private boolean insertCalled = false;

    private List<CurrencyRecord> records = new ArrayList<>();

    @Override
    public void insert(String schemaName, CurrencyRecord record) {
        insertCalled = true;
        this.records.add(record);
    }

    @Override
    public void insertAll(String schemaName, List<CurrencyRecord> records) {
        insertCalled = true;
        this.records.addAll(records);
    }

    @Override
    public List<CurrencyRecord> selectDay(String schemaName, String day) {
        return List.of();
    }

    public boolean isInsertCalled() {
        return insertCalled;
    }

    public List<CurrencyRecord> getRecords() {
        return records;
    }
}
