package com.flaregames.imports.currency;

import com.google.inject.Inject;

import com.flaregames.imports.integration.AbstractIntegrationTest;
import com.flaregames.imports.integration.TestDao;
import com.flaregames.imports.schema.DBSchemaName;
import com.flaregames.imports.schema.SchemaService;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.config.RegisterFieldMapper;
import org.jdbi.v3.sqlobject.customizer.Define;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class CurrencyDaoIntegrationTest extends AbstractIntegrationTest {
    @Inject
    CurrencyDao currencyDao;

    @Inject
    Jdbi jdbi;

    @Inject
    TestDao testDao;

    @Inject
    DBSchemaName dbSchemaName;

    @Inject
    SchemaService schemaService;

    @Before
    public void before() {
        testDao.dropTable(dbSchemaName.toString(), "currencies");
        schemaService.syncTable(CurrencyRecord.class, "currencies");
    }

    @Test
    public void testInsert() {
        CurrencyRecord record = CurrencyRecordTest.buildRecord();

        currencyDao.insert(dbSchemaName.toString(), record);

        assertThat(jdbi.onDemand(CurrencyTestDao.class).selectAll(dbSchemaName.toString()))
                .extracting("end_stamp", "day_stamp", "source", "target", "rate")
                .containsExactly(tuple(CurrencyRecordTest.end_stamp, CurrencyRecordTest.day_stamp, CurrencyRecordTest.source,
                        CurrencyRecordTest.target, CurrencyRecordTest.rate));
    }

    @RegisterFieldMapper(CurrencyRecord.class)
    private interface CurrencyTestDao {

        @SqlQuery("SELECT * FROM <schemaName>.currencies")
        List<CurrencyRecord> selectAll(@Define("schemaName") String schemaName);

    }

}
