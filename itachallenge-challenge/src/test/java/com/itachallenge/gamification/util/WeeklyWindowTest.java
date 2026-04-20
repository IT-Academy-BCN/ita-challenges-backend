package com.itachallenge.gamification.util;

import com.itachallenge.gamification.service.WeeklyWindow;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WeeklyWindowTest {

    @Test
    void fromReferenceDateTime_whenMidWeek_thenReturnsMondayToSundayRange() {
        ZonedDateTime reference = ZonedDateTime.of(
                2026, 4, 8, 15, 30, 0, 0, ZoneId.of("Europe/Madrid"));

        WeeklyWindow window = WeeklyWindow.fromReferenceDateTime(reference);

        assertEquals(LocalDateTime.of(2026, 4, 6, 0, 0, 0, 0), window.getFromInclusive());
        assertEquals(LocalDateTime.of(2026, 4, 12, 23, 59, 59, 999_999_999), window.getToInclusive());
    }

    @Test
    void fromReferenceDateTime_whenBoundaryWeek_thenKeepsCorrectYearWeekRange() {
        ZonedDateTime reference = ZonedDateTime.of(
                2026, 1, 1, 8, 0, 0, 0, ZoneId.of("Europe/Madrid"));

        WeeklyWindow window = WeeklyWindow.fromReferenceDateTime(reference);

        assertEquals(LocalDateTime.of(2025, 12, 29, 0, 0, 0, 0), window.getFromInclusive());
        assertEquals(LocalDateTime.of(2026, 1, 4, 23, 59, 59, 999_999_999), window.getToInclusive());
    }

    @Test
    void fromReferenceDateTime_whenReferenceIsNull_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> WeeklyWindow.fromReferenceDateTime(null));
    }
}
