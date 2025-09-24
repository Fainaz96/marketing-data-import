package com.flaregames.microservice;

import org.junit.rules.ExternalResource;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.TimeZone;

public class SystemTimeRule extends ExternalResource {
    private static final ZoneId DEFAULT_ZONE_ID = TimeZone.getDefault().toZoneId();
    private static final int MILLIS_PER_SECOND = 1000;

    public SystemTimeRule() {
    }

    public SystemTimeRule useOffset(Duration duration) {
        ClockProvider.overrideClock(Clock.offset(ClockProvider.getClock(), duration));
        return this;
    }

    public SystemTimeRule useOffset(long millis) {
        this.useOffset(Duration.ofMillis(millis));
        return this;
    }

    public SystemTimeRule useFixed() {
        this.useFixed(ClockProvider.now());
        return this;
    }

    public SystemTimeRule useFixed(Instant instant) {
        ClockProvider.overrideClock(Clock.fixed(instant, DEFAULT_ZONE_ID));
        return this;
    }

    public SystemTimeRule useFixed(long millis) {
        this.useFixed(Instant.ofEpochMilli(millis));
        return this;
    }

    protected void after() {
        ClockProvider.resetClockToDefault();
    }

    public long getStampMillis() {
        return ClockProvider.currentTimeMillis();
    }

    public long getStampSeconds() {
        return this.getStampMillis() / 1000L;
    }
}
