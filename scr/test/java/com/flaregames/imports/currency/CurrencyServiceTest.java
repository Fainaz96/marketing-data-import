package com.flaregames.imports.currency;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flaregames.imports.schema.DBSchemaName;
import com.flaregames.imports.schema.SchemaService;
import com.flaregames.microservice.SystemTimeRule;

import org.junit.Rule;
import org.junit.Test;

import java.io.InputStream;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class CurrencyServiceTest {

    private final static Instant TEST_DAY = Instant.parse("2019-07-09T00:00:00Z");

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CurrencyDaoMock daoMock = new CurrencyDaoMock();
    private final CurrencyClientMock clientMock = new CurrencyClientMock();
    private final SchemaService schemaService = null;

    @Rule
    public SystemTimeRule systemTimeRule = new SystemTimeRule();

    @Test
    public void parseDay_dataInserted() {
        systemTimeRule.useFixed(Instant.parse("2020-01-10T10:20:30.123Z"));

        CurrencyService service = new CurrencyService(DBSchemaName.of("test_schema"), daoMock, schemaService, clientMock,
                objectMapper);

        service.importDay(TEST_DAY,"USD");

        assertThat(clientMock.getQueryCurrencyDay()).isEqualTo(TEST_DAY);
        assertThat(daoMock.isInsertCalled());
        assertThat(daoMock.getRecords()).extracting("end_stamp", "day_stamp", "source", "target", "rate", "date_updated").contains(
                tuple(Instant.parse("2019-07-09T23:59:59.999Z"), TEST_DAY, "USD", "EUR", 0.892398, Instant.parse("2020-01-10T00:00:00Z")),
                tuple(Instant.parse("2019-07-09T23:59:59.999Z"), TEST_DAY, "EUR", "USD", 1.0 / 0.892398,
                        Instant.parse("2020-01-10T00:00:00Z")));
    }

    private static class CurrencyClientMock implements CurrencyClient {
        private Instant queryCurrencyDay;

        public Instant getQueryCurrencyDay() {
            return queryCurrencyDay;
        }

        @Override
        public InputStream queryCurrency(Instant day, String currency) {
            this.queryCurrencyDay = day;
            return CurrencyServiceTest.class.getResourceAsStream("/fixtures/currency/currencylayer_historical.json");
        }
    }
}
