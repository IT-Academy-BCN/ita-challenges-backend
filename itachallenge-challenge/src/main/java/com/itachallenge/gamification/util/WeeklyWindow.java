package com.itachallenge.gamification.util;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Objects;

public class WeeklyWindow {
    private final LocalDateTime fromInclusive;
    private final LocalDateTime toInclusive;

    public WeeklyWindow(LocalDateTime fromInclusive, LocalDateTime toInclusive) {
        this.fromInclusive = Objects.requireNonNull(fromInclusive, "fromInclusive must not be null");
        this.toInclusive = Objects.requireNonNull(toInclusive, "toInclusive must not be null");
    }

    public static WeeklyWindow fromReferenceDateTime(ZonedDateTime referenceDateTime) {
        Objects.requireNonNull(referenceDateTime, "referenceDateTime must not be null");
        ZonedDateTime startOfWeek = referenceDateTime
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .with(LocalTime.MIN);
        ZonedDateTime endOfWeek = startOfWeek
                .plusDays(6)
                .with(LocalTime.MAX);
        return new WeeklyWindow(startOfWeek.toLocalDateTime(), endOfWeek.toLocalDateTime());
    }

    public LocalDateTime getFromInclusive() {
        return fromInclusive;
    }

    public LocalDateTime getToInclusive() {
        return toInclusive;
    }
}
