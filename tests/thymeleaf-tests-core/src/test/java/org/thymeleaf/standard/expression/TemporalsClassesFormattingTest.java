/*
 * Copyright (c) 2011-2026 Thymeleaf (http://www.thymeleaf.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thymeleaf.standard.expression;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.Locale;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thymeleaf.expression.Temporals;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Tests regarding formatting of all temporal classes.
 */
public class TemporalsClassesFormattingTest {
    
    private final Temporals temporals = new Temporals(Locale.US, ZoneOffset.UTC);

    @Test
    public void localDate() {
        Temporal time = LocalDate.of(2015, 12, 31);
        Assertions.assertEquals("December 31, 2015", temporals.format(time));
    }
    
    @Test
    public void localDateTime() {
        Temporal time = LocalDateTime.of(2015, 12, 31, 23, 59, 45);
        assertEqualsNormalized("December 31, 2015, 11:59:45 PM", temporals.format(time));
    }

    @Test
    public void zonedDateTime() {
        ZonedDateTime time = ZonedDateTime.of(2015, 12, 31, 23, 59, 45, 0, ZoneOffset.UTC);
        String expected = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG).withLocale(Locale.US).format(time);
        assertEqualsNormalized(expected, temporals.format(time));
    }
    
    @Test
    public void instant() {
        Temporal time = Instant.ofEpochSecond(1);
        // Default formatting for Instant
        Assertions.assertEquals("1970-01-01T00:00:01Z", temporals.format(time));
    }
    
    @Test
    public void localTime() {
        Temporal time = LocalTime.of(23, 59, 45);
        assertEqualsNormalized("11:59:45 PM", temporals.format(time));
    }
    
    @Test
    public void offsetTime() {
        Temporal time = OffsetTime.of(23, 59, 45, 0, ZoneOffset.MAX);
        Assertions.assertEquals("23:59:45GMT+18:00", temporals.format(time));
    }
    
    @Test
    public void offsetDateTime() {
        Temporal time = OffsetDateTime.of(2015, 12, 31, 23, 59, 45, 0, ZoneOffset.MAX);
        assertEqualsNormalized("December 31, 2015, 11:59:45 PMGMT+18:00", temporals.format(time, Locale.US));
        assertEqualsNormalized("31. Dezember 2015, 23:59:45GMT+18:00", temporals.format(time, Locale.GERMANY));
    }

    @Test
    public void year() {
        Temporal time = Year.of(2015);
        Assertions.assertEquals("2015", temporals.format(time));
    }
    
    @Test
    public void yearMonth() {
        Temporal time = YearMonth.of(2015, 12);
        Assertions.assertEquals("December 2015", temporals.format(time, Locale.US));
    }
    
    @Test
    public void yearMonthForYMDLocales() {
        Temporal time = YearMonth.of(2015, 12);
        Assertions.assertEquals("2015 December", temporals.format(time, Locale.CANADA));
    }

    private static void assertEqualsNormalized(final String expected, final String actual) {
        assertEquals(
                expected == null ? null : expected.replace('\u202F', ' '),
                actual == null ? null : actual.replace('\u202F', ' '));
    }

}
