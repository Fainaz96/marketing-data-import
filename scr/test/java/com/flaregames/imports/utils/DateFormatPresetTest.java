package com.flaregames.imports.utils;

import org.junit.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class DateFormatPresetTest {

    @Test
    public void parseDashedDay() {
        assertThat(DateFormatPreset.DASHED_DAY.parse("2010-10-11")).isEqualTo(Instant.parse("2010-10-11T00:00:00Z"));
    }

    @Test
    public void formatDashedDay() {
        assertThat(DateFormatPreset.DASHED_DAY.format(Instant.parse("2010-10-11T00:00:00Z"))).isEqualTo("2010-10-11");
    }

    @Test
    public void parseIntegerDay() {
        assertThat(DateFormatPreset.INTEGER_DAY.parse("20101011")).isEqualTo(Instant.parse("2010-10-11T00:00:00Z"));
    }

    @Test
    public void formatIntegerDay() {
        assertThat(DateFormatPreset.INTEGER_DAY.format(Instant.parse("2010-10-11T00:00:00Z"))).isEqualTo("20101011");
    }

    @Test
    public void parseIntegerMonth() {
        assertThat(DateFormatPreset.INTEGER_MONTH.parse("201010")).isEqualTo(Instant.parse("2010-10-01T00:00:00Z"));
    }

    @Test
    public void formatIntegerMonth() {
        assertThat(DateFormatPreset.INTEGER_MONTH.format(Instant.parse("2010-10-11T00:00:00Z"))).isEqualTo("201010");
    }

    @Test
    public void parsePathDay() {
        assertThat(DateFormatPreset.PATH_DAY.parse("2010/10/02")).isEqualTo(Instant.parse("2010-10-02T00:00:00Z"));
    }

    @Test
    public void formatPathDay() {
        assertThat(DateFormatPreset.PATH_DAY.format(Instant.parse("2010-10-11T00:00:00Z"))).isEqualTo("2010/10/11");
    }

}