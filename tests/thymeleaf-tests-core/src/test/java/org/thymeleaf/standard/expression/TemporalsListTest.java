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
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.thymeleaf.expression.Temporals;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests regarding formatting of lists of temporal objects.
 */
public class TemporalsListTest {
    
    private final Temporals temporals = new Temporals(Locale.US, ZoneOffset.UTC);

    @Test
    public void testListFormat() {
        List<Temporal> list = Arrays.asList(LocalDate.of(2015, 1, 1), LocalDate.of(2015, 12, 31));
        List<String> expected = Arrays.asList("January 1, 2015", "December 31, 2015");
        assertEquals(expected, temporals.listFormat(list));
    }

    @Test
    public void testListFormatWithLocale() {
        List<Temporal> list = Arrays.asList(LocalDate.of(2015, 1, 1), LocalDate.of(2015, 12, 31));
        List<String> expected = Arrays.asList("1. Januar 2015", "31. Dezember 2015");
        assertEquals(expected, temporals.listFormat(list, Locale.GERMANY));
    }

    @Test
    public void testListFormatWithPattern() {
        List<Temporal> list = Arrays.asList(LocalDate.of(2015, 1, 1), LocalDate.of(2015, 12, 31));
        String pattern = "yyyy-MM-dd";
        List<String> expected = Arrays.asList("2015-01-01", "2015-12-31");
        assertEquals(expected, temporals.listFormat(list, pattern));
    }

    @Test
    public void testListFormatWithPatternAndLocale() {
        List<Temporal> list = Arrays.asList(LocalDate.of(2015, 1, 1), LocalDate.of(2015, 12, 31));
        String pattern = "EEEE, d MMMM, yyyy";
        List<String> expected = Arrays.asList("Donnerstag, 1 Januar, 2015", "Donnerstag, 31 Dezember, 2015");
        assertEquals(expected, temporals.listFormat(list, pattern, Locale.GERMANY));
    }

    @Test
    public void testListDay() {
        List<Temporal> list = Arrays.asList(LocalDate.of(2015, 1, 1), LocalDate.of(2015, 12, 31));
        List<Integer> expected = Arrays.asList(1, 31);
        assertEquals(expected, temporals.listDay(list));
    }
    
    @Test
    public void testListMonth() {
        List<Temporal> list = Arrays.asList(LocalDate.of(2015, 1, 1), LocalDate.of(2015, 12, 31));
        List<Integer> expected = Arrays.asList(1, 12);
        assertEquals(expected, temporals.listMonth(list));
    }
    
    @Test
    public void testListMonthName() {
        List<Temporal> list = Arrays.asList(LocalDate.of(2015, 1, 1), LocalDate.of(2015, 12, 31));
        List<String> expected = Arrays.asList("January", "December");
        assertEquals(expected, temporals.listMonthName(list));
    }
    
    @Test
    public void testListMonthNameShort() {
        List<Temporal> list = Arrays.asList(LocalDate.of(2015, 1, 1), LocalDate.of(2015, 12, 31));
        List<String> expected = Arrays.asList("Jan", "Dec");
        assertEquals(expected, temporals.listMonthNameShort(list));
    }
    
    @Test
    public void testListYear() {
        List<Temporal> list = Arrays.asList(LocalDate.of(2014, 1, 1), LocalDate.of(2015, 12, 31));
        List<Integer> expected = Arrays.asList(2014, 2015);
        assertEquals(expected, temporals.listYear(list));
    }

    @Test
    public void testListDayOfWeek() {
        List<Temporal> list = Arrays.asList(LocalDate.of(2014, 1, 1), LocalDate.of(2015, 12, 31));
        List<Integer> expected = Arrays.asList(3, 4);
        assertEquals(expected, temporals.listDayOfWeek(list));
    }

    @Test
    public void testListDayOfWeekName() {
        List<Temporal> list = Arrays.asList(LocalDate.of(2014, 1, 1), LocalDate.of(2015, 12, 31));
        List<String> expected = Arrays.asList("Wednesday", "Thursday");
        assertEquals(expected, temporals.listDayOfWeekName(list));
    }

    @Test
    public void testListDayOfWeekNameShort() {
        List<Temporal> list = Arrays.asList(LocalDate.of(2014, 1, 1), LocalDate.of(2015, 12, 31));
        List<String> expected = Arrays.asList("Wed", "Thu");
        assertEquals(expected, temporals.listDayOfWeekNameShort(list));
    }

    @Test
    public void testListHour() {
        List<Temporal> list = Arrays.asList(LocalDateTime.of(2015, 1, 1, 1, 1, 1, 1), LocalDateTime.of(2015, 12, 31, 23, 59, 10, 9));
        List<Integer> expected = Arrays.asList(1, 23);
        assertEquals(expected, temporals.listHour(list));
    }

    @Test
    public void testListMinute() {
        List<Temporal> list = Arrays.asList(LocalDateTime.of(2015, 1, 1, 1, 1, 1, 1), LocalDateTime.of(2015, 12, 31, 23, 59, 10, 9));
        List<Integer> expected = Arrays.asList(1, 59);
        assertEquals(expected, temporals.listMinute(list));
    }

    @Test
    public void testListSecond() {
        List<Temporal> list = Arrays.asList(LocalDateTime.of(2015, 1, 1, 1, 1, 1, 1), LocalDateTime.of(2015, 12, 31, 23, 59, 10, 9));
        List<Integer> expected = Arrays.asList(1, 10);
        assertEquals(expected, temporals.listSecond(list));
    }

    @Test
    public void testListNanosecond() {
        List<Temporal> list = Arrays.asList(LocalDateTime.of(2015, 1, 1, 1, 1, 1, 1), LocalDateTime.of(2015, 12, 31, 23, 59, 10, 9));
        List<Integer> expected = Arrays.asList(1, 9);
        assertEquals(expected, temporals.listNanosecond(list));
    }

    @Test
    public void testListFormatISO() {
        List<Temporal> list = Arrays.asList(LocalDateTime.of(2015, 1, 1, 1, 1, 1, 1), LocalDateTime.of(2015, 12, 31, 23, 59, 10, 9));
        List<String> expected = Arrays.asList("2015-01-01T01:01:01.000+0000", "2015-12-31T23:59:10.000+0000");
        assertEquals(expected, temporals.listFormatISO(list));
    }
    
}
