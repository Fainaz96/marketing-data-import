package com.flaregames.imports.utils;

import com.flaregames.microservice.SystemTimeRule;

import org.junit.Rule;
import org.junit.Test;

import java.time.Instant;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class InstantSeriesTest {

    @Rule
    public SystemTimeRule systemTimeRule = new SystemTimeRule().useFixed(Instant.parse("2020-10-15T00:00:00Z"));

    @Test
    public void daysUntilYesterday_asExpected() {

        assertThat(InstantSeries.daysUntilYesterday(5).stream().collect(Collectors.toList())).containsExactly(
                Instant.parse("2020-10-10T00:00:00Z"),
                Instant.parse("2020-10-11T00:00:00Z"),
                Instant.parse("2020-10-12T00:00:00Z"),
                Instant.parse("2020-10-13T00:00:00Z"),
                Instant.parse("2020-10-14T00:00:00Z")
        );

    }

    @Test
    public void parsedDayRange_asExpected() {

        assertThat(InstantSeries.parsedDayRange("2020-10-10", "2020-10-14").stream().collect(Collectors.toList())).containsExactly(
                Instant.parse("2020-10-10T00:00:00Z"),
                Instant.parse("2020-10-11T00:00:00Z"),
                Instant.parse("2020-10-12T00:00:00Z"),
                Instant.parse("2020-10-13T00:00:00Z"),
                Instant.parse("2020-10-14T00:00:00Z")
        );

    }

}