package com.flaregames.imports.importlog;

import org.junit.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MissingListElementFinderTest {

    private final MissingListElementFinder<Instant> finder = MissingListElementFinder.getForInstance();

    @Test
    public void emptyList_allFilled() {
        List<Instant> missing = finder.getMissingElements(Collections.emptyList(), Instant.parse("2020-01-01T00:00:00Z"),
                Instant.parse("2020-01-05T00:00:00Z"));

        assertThat(missing).hasSize(5);
    }

    @Test
    public void gaps_gapsFilled() {
        List<Instant> list = Arrays.asList(Instant.parse("2020-01-03T00:00:00Z"), Instant.parse("2020-01-05T00:00:00Z"),
                Instant.parse("2020-01-08T00:00:00Z"), Instant.parse("2020-01-10T00:00:00Z"));

        List<Instant> missing = finder.getMissingElements(list, Instant.parse("2020-01-03T00:00:00Z"),
                Instant.parse("2020-01-10T00:00:00Z"));

        assertThat(missing).hasSize(4);
    }

    @Test
    public void single_beforeAfterFilled() {
        List<Instant> list = Arrays.asList(Instant.parse("2020-01-02T00:00:00Z"));

        List<Instant> missing = finder.getMissingElements(list, Instant.parse("2020-01-01T00:00:00Z"),
                Instant.parse("2020-01-03T00:00:00Z"));

        assertThat(missing).hasSize(2);
    }

    @Test
    public void gaps_allFilledUp() {
        List<Instant> list = Arrays.asList(Instant.parse("2020-01-03T00:00:00Z"), Instant.parse("2020-01-04T00:00:00Z"),
                Instant.parse("2020-01-06T00:00:00Z"));

        List<Instant> missing = finder.getMissingElements(list, Instant.parse("2020-01-01T00:00:00Z"),
                Instant.parse("2020-01-07T00:00:00Z"));

        assertThat(missing).hasSize(4);
    }

    @Test
    public void complete_noneFilled() {
        List<Instant> list = Arrays.asList(Instant.parse("2020-01-03T00:00:00Z"), Instant.parse("2020-01-04T00:00:00Z"),
                Instant.parse("2020-01-05T00:00:00Z"));

        List<Instant> missing = finder.getMissingElements(list, Instant.parse("2020-01-03T00:00:00Z"),
                Instant.parse("2020-01-05T00:00:00Z"));

        assertThat(missing).hasSize(0);
    }

    @Test
    public void leading_leadingFilled() {
        List<Instant> list = Arrays.asList(Instant.parse("2020-01-03T00:00:00Z"));

        List<Instant> missing = finder.getMissingElements(list, Instant.parse("2020-01-01T00:00:00Z"),
                Instant.parse("2020-01-03T00:00:00Z"));

        assertThat(missing).hasSize(2);
    }

    @Test
    public void trailing_trailingFilled() {
        List<Instant> list = Arrays.asList(Instant.parse("2020-01-03T00:00:00Z"));

        List<Instant> missing = finder.getMissingElements(list, Instant.parse("2020-01-03T00:00:00Z"),
                Instant.parse("2020-01-05T00:00:00Z"));

        assertThat(missing).hasSize(2);
    }

}