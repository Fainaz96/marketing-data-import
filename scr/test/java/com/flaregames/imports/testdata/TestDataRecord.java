package com.flaregames.imports.testdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.flaregames.imports.utils.JsonInstantDeserializers;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TestDataRecord {
    public int id;
    public Instant stamp;
    public String name;

    @JsonDeserialize(using = JsonInstantDeserializers.DashedDayDeserializer.class)
    public Instant formatted;

    public TestDataRecord() {
    }

    public TestDataRecord(int id, Instant stamp, String name) {
        this.id = id;
        this.stamp = stamp;
        this.name = name;
    }
}
