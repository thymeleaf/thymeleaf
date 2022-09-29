/*
 * Copyright 2014 The THYMELEAF team.
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

import org.junit.jupiter.api.Test;
import org.thymeleaf.expression.Temporals;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests regarding formatting of temporal objects.
 */
public class TemporalsFormattingTest {
    
    private final Temporals temporals = new Temporals(Locale.US, ZoneOffset.UTC);

    @Test
    public void testFormat() {
        Temporal time = ZonedDateTime.of(2015, 12, 31, 23, 59, 45, 0, ZoneOffset.UTC);
        assertEquals("December 31, 2015 at 11:59:45 PM Z", temporals.format(time));
    }
    
    @Test
    public void testFormatWithNullTemporal() {
        assertNull(temporals.format(null));
    }

    @Test
    public void testFormatWithLocale() {
        Temporal time = ZonedDateTime.of(2015, 12, 31, 23, 59, 45, 0, ZoneOffset.UTC);
        assertEquals("31. Dezember 2015 um 23:59:45 Z", temporals.format(time, Locale.GERMANY));
    }

    @Test
    public void testFormatWithLocaleAndNullTemporal() {
        assertNull(temporals.format(null, Locale.GERMANY));
    }

    @Test
    public void testFormatWithPattern() {
        Temporal time = LocalDateTime.of(2015, 12, 31, 23, 59);
        String pattern = "yyyy-MM-dd HH:mm:ss";
        String expectd = "2015-12-31 23:59:00";
        assertEquals(expectd, temporals.format(time, pattern));
    }

    @Test
    public void testFormatWithPatternAndZone() {
        Temporal time = ZonedDateTime.of(2015, 12, 31, 23, 59, 0, 1, ZoneOffset.UTC);
        String pattern = "yyyy-MM-dd HH:mm:ss";
        String expectd = "2015-12-31 18:59:00";
        assertEquals(expectd, temporals.format(time, pattern, "Etc/GMT+5"));
    }

    @Test
    public void testFormatStandardPattern() {
        Temporal time = LocalDateTime.of(2015, 12, 31, 23, 59);
        assertEquals("12/31/15, 11:59 PM", temporals.format(time, "SHORT", Locale.US));
        assertEquals("Dec 31, 2015, 11:59:00 PM", temporals.format(time, "MEDIUM", Locale.US));
        assertEquals("December 31, 2015 at 11:59:00 PM Z", temporals.format(time, "LONG", Locale.US));
        assertEquals("Thursday, December 31, 2015 at 11:59:00 PM Z", temporals.format(time, "FULL", Locale.US));
    }

    @Test
    public void testFormatWithPatternAndNullTemporal() {
        assertNull(temporals.format(null, "y"));
    }

    @Test
    public void testFormatWithPatternAndLocale() {
        Temporal time = LocalDateTime.of(2015, 12, 31, 23, 59);
        String pattern = "EEEE, d MMMM, yyyy";
        String expectd = "Donnerstag, 31 Dezember, 2015";
        assertEquals(expectd, temporals.format(time, pattern, Locale.GERMANY));
    }

    @Test
    public void testFormatWithPatternAndLocaleAndNullTemporal() {
        assertNull(temporals.format(null, "y", Locale.GERMANY));
    }
    
    @Test
    public void localTimeWithPattern() {
        Temporal time = LocalTime.of(23, 59, 45);
        assertEquals("23:59:45", temporals.format(time, "HH:mm:ss"));
    }
    
    @Test
    public void offsetDateTimeWithPattern() {
        OffsetDateTime time = OffsetDateTime.of(LocalDateTime.of(2015, 12, 31, 23, 59, 45), ZoneOffset.UTC);
        assertEquals("12/31/2015 23:59:45", temporals.format(time, "MM/dd/yyyy HH:mm:ss"));
    }

    @Test
    public void offsetTimeWithPattern() {
        OffsetTime time = OffsetTime.of(LocalTime.of(23, 59, 45), ZoneOffset.UTC);
        assertEquals("23:59:45", temporals.format(time, "HH:mm:ss"));
    }

    @Test
    public void yearWithPattern() {
        Year time = Year.of(2015);
        assertEquals("2015", temporals.format(time, "yyyy"));
    }

    @Test
    public void yearMonthWithPattern() {
        YearMonth time = YearMonth.of(2015, 12);
        assertEquals("12/2015", temporals.format(time, "MM/yyyy"));
    }

    @Test
    public void testDay() {
        Temporal time = LocalDate.of(2015, 12, 31);
        assertEquals(31, temporals.day(time).intValue());
    }

    @Test
    public void testDayWithNullTemporal() {
        assertNull(temporals.day(null));
    }

    @Test
    public void testMonth() {
        Temporal time = LocalDate.of(2015, 12, 31);
        assertEquals(12, temporals.month(time).intValue());
    }
    
    @Test
    public void testMonthWithNullTemporal() {
        assertNull(temporals.month(null));
    }

    @Test
    public void testMonthName() {
        Temporal time = LocalDate.of(2015, 12, 31);
        assertEquals("December", temporals.monthName(time));
    }

    @Test
    public void testMonthNameWithNullTemporal() {
        assertNull(temporals.monthName(null));
    }

    @Test
    public void testMonthNameShort() {
        Temporal time = LocalDate.of(2015, 12, 31);
        assertEquals("Dec", temporals.monthNameShort(time));
    }

    @Test
    public void testMonthNameShortWithNullTemporal() {
        assertNull(temporals.monthNameShort(null));
    }

    @Test
    public void testYear() {
        Temporal time = LocalDate.of(2015, 12, 31);
        assertEquals(2015, temporals.year(time).intValue());
    }

    @Test
    public void testYearWithNullTemporal() {
        assertNull(temporals.year(null));
    }

    @Test
    public void testDayOfWeek() {
        Temporal time = LocalDate.of(2015, 12, 31);
        assertEquals(4, temporals.dayOfWeek(time).intValue());
    }
    
    @Test
    public void testDayOfWeekWithNullTemporal() {
        assertNull(temporals.dayOfWeek(null));
    }

    @Test
    public void testDayOfWeekName() {
        Temporal time = LocalDate.of(2015, 12, 31);
        assertEquals("Thursday", temporals.dayOfWeekName(time));
    }
    
    @Test
    public void testDayOfWeekNameWithNullTemporal() {
        assertNull(temporals.dayOfWeekName(null));
    }

    @Test
    public void testDayOfWeekNameShort() {
        Temporal time = LocalDate.of(2015, 12, 31);
        assertEquals("Thu", temporals.dayOfWeekNameShort(time));
    }
    
    @Test
    public void testDayOfWeekNameShortWithNullTemporal() {
        assertNull(temporals.dayOfWeekNameShort(null));
    }

    @Test
    public void testHour() {
        Temporal time = LocalDateTime.of(2015, 12, 31, 23, 59, 45, 1);
        assertEquals(23, temporals.hour(time).intValue());
    }

    @Test
    public void testHourWithNullTemporal() {
        assertNull(temporals.hour(null));
    }

    @Test
    public void testMinute() {
        Temporal time = LocalDateTime.of(2015, 12, 31, 23, 59, 45, 1);
        assertEquals(59, temporals.minute(time).intValue());
    }

    @Test
    public void testMinuteWithNullTemporal() {
        assertNull(temporals.minute(null));
    }

    @Test
    public void testSecond() {
        Temporal time = LocalDateTime.of(2015, 12, 31, 23, 59, 45, 1);
        assertEquals(45, temporals.second(time).intValue());
    }

    @Test
    public void testSecondWithNullTemporal() {
        assertNull(temporals.second(null));
    }

    @Test
    public void testNanosecond() {
        Temporal time = LocalDateTime.of(2015, 12, 31, 23, 59, 45, 1);
        assertEquals(1, temporals.nanosecond(time).intValue());
    }

    @Test
    public void testNanosecondWithNullTemporal() {
        assertNull(temporals.nanosecond(null));
    }

    @Test
    public void testFormatISO() {
        Temporal time = LocalDateTime.of(2015, 12, 31, 23, 59, 45, 1).atZone(ZoneOffset.MAX);
        assertEquals("2015-12-31T23:59:45.000+1800", temporals.formatISO(time));
    }

    @Test
    public void testFormatISOWithNullTemporal() {
        assertNull(temporals.formatISO(null));
    }

    @Test
    // https://github.com/thymeleaf/thymeleaf-extras-java8time/issues/17
    public void testIssue17() {
        Instant time = Instant.ofEpochSecond(1);
        assertEquals("1970-01-01", temporals.format(time, "yyyy-MM-dd", Locale.US));
    }

}
