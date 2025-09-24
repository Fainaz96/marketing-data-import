package com.flaregames.imports.utils;

import org.junit.Test;

import java.time.Instant;
import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

public class InstantSplitTest {
    @Test
    public void iterator_asExpected() {
        Iterator<InstantSplit.Range> it = InstantSplit.of(Instant.parse("2021-05-07T00:00:00Z"), Instant.parse("2021-05-20T00:00:00Z"), 5)
                .iterator();
        assertThat(it).extracting(r -> tuple(r.getFrom(), r.getTo())).containsExactly(
                // @formatter:off
                tuple(Instant.parse("2021-05-07T00:00:00Z"),Instant.parse("2021-05-11T00:00:00Z")),
                tuple(Instant.parse("2021-05-12T00:00:00Z"),Instant.parse("2021-05-16T00:00:00Z")),
                tuple(Instant.parse("2021-05-17T00:00:00Z"),Instant.parse("2021-05-20T00:00:00Z"))
                // @formatter:on
        );
    }

    @Test
    public void iterator_oneDay() {
        Iterator<InstantSplit.Range> it = InstantSplit.of(Instant.parse("2021-05-07T00:00:00Z"), Instant.parse("2021-05-07T00:00:00Z"), 1)
                .iterator();
        assertThat(it).extracting(r -> tuple(r.getFrom(), r.getTo())).containsExactly(
                // @formatter:off
                tuple(Instant.parse("2021-05-07T00:00:00Z"),Instant.parse("2021-05-07T00:00:00Z"))
                // @formatter:on
        );
    }

    @Test
    public void iterator_exactEnd() {
        Iterator<InstantSplit.Range> it = InstantSplit.of(Instant.parse("2021-05-10T00:00:00Z"), Instant.parse("2021-05-19T00:00:00Z"), 5)
                .iterator();
        assertThat(it).extracting(r -> tuple(r.getFrom(), r.getTo())).containsExactly(
                // @formatter:off
                tuple(Instant.parse("2021-05-10T00:00:00Z"),Instant.parse("2021-05-14T00:00:00Z")),
                tuple(Instant.parse("2021-05-15T00:00:00Z"),Instant.parse("2021-05-19T00:00:00Z"))
                // @formatter:on
        );
    }

    @Test
    public void iterator_endBeforeStart_none() {
        Iterator<InstantSplit.Range> it = InstantSplit.of(Instant.parse("2021-05-10T00:00:00Z"), Instant.parse("2021-05-09T00:00:00Z"), 1)
                .iterator();
        assertThat(it).extracting(r -> tuple(r.getFrom(), r.getTo())).isEmpty();
    }

    @Test
    public void iterator_wrongMaxDays() {
        assertThatThrownBy(
                () -> InstantSplit.of(Instant.parse("2021-05-10T00:00:00Z"), Instant.parse("2021-05-09T00:00:00Z"), 0)).isExactlyInstanceOf(
                IllegalArgumentException.class);
    }

}