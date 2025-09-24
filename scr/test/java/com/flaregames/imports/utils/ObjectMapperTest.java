package com.flaregames.imports.utils;

import com.google.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flaregames.imports.integration.AbstractIntegrationTest;
import com.flaregames.imports.testdata.TestDataRecord;

import org.junit.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class ObjectMapperTest extends AbstractIntegrationTest {

    @Inject
    ObjectMapper objectMapper;

    @Test
    public void serialize_asExpected() {
        TestDataRecord data = new TestDataRecord(10, Instant.parse("2010-11-12T01:02:03.456Z"), "hello");
        assertThatCode(() -> assertThat(objectMapper.writeValueAsString(data)).isEqualTo(
                "{\"id\":10,\"stamp\":\"2010-11-12 01:02:03" + ".456\",\"name\":\"hello\",\"formatted\":null}")).doesNotThrowAnyException();
    }

    @Test
    public void deserialize_asExpected() {
        assertThatCode(
                () -> assertThat(objectMapper.readValue("{ \"formatted\" : \"2010-11-12\" }", TestDataRecord.class)).extracting("formatted")
                        .containsExactly(Instant.parse("2010-11-12T00:00:00Z"))).doesNotThrowAnyException();
    }

}
