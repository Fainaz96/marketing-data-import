package com.flaregames.imports.s3;

import com.google.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flaregames.imports.integration.AbstractIntegrationTest;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class JsonFileTest extends AbstractIntegrationTest {

    @Inject
    ObjectMapper objectMapper;

    @Test
    public void addObject() {
        File file;
        try (JsonFile json = new JsonFile(objectMapper)) {
            json.addObject(new TestObject());
            json.addObject(new TestObject());
            file = json.getFile();
        }
        assertThatCode(() -> {
            assertThat(IOUtils.toString(new GZIPInputStream(new FileInputStream(file)), StandardCharsets.UTF_8)).isEqualTo(
                    "{\"hello\":\"hello\"," +
                            "\"world\":123}{\"hello\":\"hello\",\"world\":123}");
        }).doesNotThrowAnyException();
    }

    private static class TestObject {
        public String hello = "hello";
        public int world = 123;
    }

}