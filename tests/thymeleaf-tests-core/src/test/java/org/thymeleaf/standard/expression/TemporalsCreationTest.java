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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.thymeleaf.expression.Temporals;

import static org.junit.jupiter.api.Assertions.*;


public class TemporalsCreationTest {

    private final Temporals temporals = new Temporals(Locale.US);

    @Test
    public void testCreateLocalDate() {
        Temporal temporal = temporals.create(Integer.valueOf(2015), Integer.valueOf(12), Integer.valueOf(31));
        assertNotNull(temporal);
        assertTrue(temporal instanceof LocalDate);
        LocalDate date = (LocalDate) temporal;
        assertEquals(2015, date.getYear());
        assertEquals(  12, date.getMonthValue());
        assertEquals(  31, date.getDayOfMonth());
    }

    @Test
    public void testCreateLocalDateTime() {
        Temporal temporal = temporals.create(Integer.valueOf(2015), Integer.valueOf(12), Integer.valueOf(31), Integer.valueOf(23), Integer.valueOf(59));
        assertNotNull(temporal);
        assertTrue(temporal instanceof LocalDateTime);
        LocalDateTime time = (LocalDateTime) temporal;
        assertEquals(2015, time.getYear());
        assertEquals(  12, time.getMonthValue());
        assertEquals(  31, time.getDayOfMonth());
        assertEquals(  23, time.getHour());
        assertEquals(  59, time.getMinute());
        assertEquals(   0, time.getSecond());
        assertEquals(   0, time.getNano());
    }

    @Test
    public void testCreateLocalDateTimeWithSeconds() {
        Temporal temporal = temporals.create(Integer.valueOf(2015), Integer.valueOf(12), Integer.valueOf(31), Integer.valueOf(23), Integer.valueOf(59), Integer.valueOf(45));
        assertNotNull(temporal);
        assertTrue(temporal instanceof LocalDateTime);
        LocalDateTime time = (LocalDateTime) temporal;
        assertEquals(2015, time.getYear());
        assertEquals(  12, time.getMonthValue());
        assertEquals(  31, time.getDayOfMonth());
        assertEquals(  23, time.getHour());
        assertEquals(  59, time.getMinute());
        assertEquals(  45, time.getSecond());
        assertEquals(   0, time.getNano());
    }

    @Test
    public void testCreateLocalDateTimeWithMilliseconds() {
        Temporal temporal = temporals.create(Integer.valueOf(2015), Integer.valueOf(12), Integer.valueOf(31), Integer.valueOf(23), Integer.valueOf(59), Integer.valueOf(45), Integer.valueOf(999));
        assertNotNull(temporal);
        assertTrue(temporal instanceof LocalDateTime);
        LocalDateTime time = (LocalDateTime) temporal;
        assertEquals(2015, time.getYear());
        assertEquals(  12, time.getMonthValue());
        assertEquals(  31, time.getDayOfMonth());
        assertEquals(  23, time.getHour());
        assertEquals(  59, time.getMinute());
        assertEquals(  45, time.getSecond());
        assertEquals( 999, time.getNano());
    }

    @Test
    public void testCreateNow() {
        Temporal temporal = temporals.createNow();
        assertNotNull(temporal);
        assertTrue(temporal instanceof LocalDateTime);
    }

    @Test
    public void testCreateNowForTimeZone() {
        Temporal temporal = temporals.createNowForTimeZone(ZoneOffset.UTC);
        assertNotNull(temporal);
        assertTrue(temporal instanceof ZonedDateTime);
        ZonedDateTime time = (ZonedDateTime) temporal;
        assertEquals(ZoneOffset.UTC, time.getZone());
    }

    @Test
    public void testCreateToday() {
        Temporal temporal = temporals.createToday();
        assertNotNull(temporal);
        assertTrue(temporal instanceof LocalDate);
    }

    @Test
    public void testCreateTodayForTimeZone() {
        Temporal temporal = temporals.createTodayForTimeZone(ZoneOffset.UTC);
        assertNotNull(temporal);
        assertTrue(temporal instanceof ZonedDateTime);
        ZonedDateTime time = (ZonedDateTime) temporal;
        assertEquals(ZoneOffset.UTC, time.getZone());
        assertEquals(0, time.getHour());
        assertEquals(0, time.getMinute());
        assertEquals(0, time.getSecond());
        assertEquals(0, time.getNano());
    }

    @Test
    public void testCreateDate() {
        Temporal temporal = temporals.createDate("2015-12-31");
        assertNotNull(temporal);
        assertTrue(temporal instanceof LocalDate);
        LocalDate date = (LocalDate) temporal;
        assertEquals(2015, date.getYear());
        assertEquals(  12, date.getMonthValue());
        assertEquals(  31, date.getDayOfMonth());
    }
    
    @Test
    public void testCreateDateTime() {
        Temporal temporal = temporals.createDateTime("2015-12-31T23:59:00");
        assertNotNull(temporal);
        assertTrue(temporal instanceof LocalDateTime);
        LocalDateTime time = (LocalDateTime) temporal;
        assertEquals(2015, time.getYear());
        assertEquals(  12, time.getMonthValue());
        assertEquals(  31, time.getDayOfMonth());
        assertEquals(  23, time.getHour());
        assertEquals(  59, time.getMinute());
        assertEquals(   0, time.getSecond());
        assertEquals(   0, time.getNano());
    }
    
    @Test
    public void testCreateDateWithPattern() {
        Temporal temporal = temporals.createDate("31.12.2015", "dd.MM.yyyy");
        assertNotNull(temporal);
        assertTrue(temporal instanceof LocalDate);
        LocalDate date = (LocalDate) temporal;
        assertEquals(2015, date.getYear());
        assertEquals(  12, date.getMonthValue());
        assertEquals(  31, date.getDayOfMonth());
    }
    @Test
    public void testCreateDateTimeWithPattern() {
        Temporal temporal = temporals.createDateTime("31.12.2015 23:59", "dd.MM.yyyy HH:mm");
        assertNotNull(temporal);
        assertTrue(temporal instanceof LocalDateTime);
        LocalDateTime time = (LocalDateTime) temporal;
        assertEquals(2015, time.getYear());
        assertEquals(  12, time.getMonthValue());
        assertEquals(  31, time.getDayOfMonth());
        assertEquals(  23, time.getHour());
        assertEquals(  59, time.getMinute());
        assertEquals(   0, time.getSecond());
        assertEquals(   0, time.getNano());
    }

}
