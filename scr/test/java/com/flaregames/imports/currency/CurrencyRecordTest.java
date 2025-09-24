package com.flaregames.imports.currency;
import java.time.Instant;

public class CurrencyRecordTest{
        public static final String source = "USD";
        public static final String target = "EUR";
        public static final Instant day_stamp = Instant.parse("2020-01-01T00:00:00Z");
        public static final Instant end_stamp = Instant.parse("2020-01-01T00:01:00Z");
        public static final Instant date_updated = Instant.parse("2020-01-01T00:03:00Z");
        public static final double rate = 0.892398;

        public static CurrencyRecord buildRecord(){
                CurrencyRecord record = new CurrencyRecord();
                record.end_stamp = end_stamp;
                record.day_stamp = day_stamp;
                record.date_updated = date_updated;
                record.rate = rate;
                record.target = target;
                record.source = source;
                return record;
        }
}
