/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 * =============================================================================
 */
package org.thymeleaf.expression;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.DateUtils;
import org.thymeleaf.util.Validate;


/**
 * <p>
 *   Expression Object for performing calendar ({@link Calendar}) operations inside Thymeleaf Standard Expressions.
 * </p>
 * <p>
 *   An object of this class is usually available in variable evaluation expressions with the name
 *   {@code #calendars}.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class Calendars {



    private final Locale locale;




    public Calendars(final Locale locale) {
        super();
        Validate.notNull(locale, "Locale cannot be null");
        this.locale = locale;
    }

    
  
    /**
     *
     * @param year year
     * @param month month
     * @param day day
     * @return the result
     * @since 1.1.2
     */
    public Calendar create(final Object year, final Object month, final Object day) {
        return DateUtils.create(year, month, day, null, null, null, null, null, this.locale);
    }

    
    /**
     *
     * @param year year
     * @param month month
     * @param day day
     * @param hour hour
     * @param minute minute
     * @return the result
     * @since 1.1.2
     */
    public Calendar create(final Object year, final Object month, final Object day,
            final Object hour, final Object minute) {
        return DateUtils.create(year, month, day, hour, minute, null, null, null, this.locale);
    }

    
    /**
     *
     * @param year year
     * @param month month
     * @param day day
     * @param hour hour
     * @param minute minute
     * @param second second
     * @return the result
     * @since 1.1.2
     */
    public Calendar create(final Object year, final Object month, final Object day,
            final Object hour, final Object minute, final Object second) {
        return DateUtils.create(year, month, day, hour, minute, second, null, null, this.locale);
    }


    /**
     *
     * @param year year
     * @param month month
     * @param day day
     * @param hour hour
     * @param minute minute
     * @param second second
     * @param millisecond millisecond
     * @return the result
     * @since 1.1.2
     */
    public Calendar create(final Object year, final Object month, final Object day,
            final Object hour, final Object minute, final Object second, final Object millisecond) {
        return DateUtils.create(year, month, day, hour, minute, second, millisecond, null, this.locale);
    }


    /**
     *
     * @param year year
     * @param month month
     * @param day day
     * @param timeZone timeZone
     * @return the result
     * @since 2.1.0
     */
    public Calendar createForTimeZone(final Object year, final Object month, final Object day, final Object timeZone) {
        return DateUtils.create(year, month, day, null, null, null, null, timeZone, this.locale);
    }


    /**
     *
     * @param year year
     * @param month month
     * @param day day
     * @param hour hour
     * @param minute minute
     * @param timeZone timeZone
     * @return the result
     * @since 2.1.0
     */
    public Calendar createForTimeZone(final Object year, final Object month, final Object day,
            final Object hour, final Object minute, final Object timeZone) {
        return DateUtils.create(year, month, day, hour, minute, null, null, timeZone, this.locale);
    }


    /**
     *
     * @param year year
     * @param month month
     * @param day day
     * @param hour hour
     * @param minute minute
     * @param second second
     * @param timeZone timeZone
     * @return the result
     * @since 2.1.0
     */
    public Calendar createForTimeZone(final Object year, final Object month, final Object day,
            final Object hour, final Object minute, final Object second, final Object timeZone) {
        return DateUtils.create(year, month, day, hour, minute, second, null, timeZone, this.locale);
    }


    /**
     *
     * @param year year
     * @param month month
     * @param day day
     * @param hour hour
     * @param minute minute
     * @param second second
     * @param millisecond millisecond
     * @param timeZone timeZone
     * @return the result
     * @since 2.1.0
     */
    public Calendar createForTimeZone(final Object year, final Object month, final Object day,
            final Object hour, final Object minute, final Object second, final Object millisecond,
            final Object timeZone) {
        return DateUtils.create(year, month, day, hour, minute, second, millisecond, timeZone, this.locale);
    }


    /**
     *
     * @return the result
     * @since 1.1.2
     */
    public Calendar createNow() {
        return DateUtils.createNow(null, this.locale);
    }


    /**
     *
     * @param timeZone timeZone
     * @return the result
     * @since 2.1.0
     */
    public Calendar createNowForTimeZone(final Object timeZone) {
        return DateUtils.createNow(timeZone, this.locale);
    }


    /**
     *
     * @return the result
     * @since 1.1.2
     */
    public Calendar createToday() {
        return DateUtils.createToday(null, this.locale);
    }


    /**
     *
     * @param timeZone timeZone
     * @return the result
     * @since 2.1.0
     */
    public Calendar createTodayForTimeZone(final Object timeZone) {
        return DateUtils.createToday(timeZone, this.locale);
    }

    
    
    
    public String format(final Calendar target) {
        if (target == null) {
            return null;
        }
        try {
            return DateUtils.format(target, this.locale);
        } catch (final Exception e) {
            throw new TemplateProcessingException(
                    "Error formatting calendar with standard format for locale " + this.locale, e);
        }
    }
    
    public String[] arrayFormat(final Object[] target) {
        if (target == null) {
            return null;
        }
        final String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = format((Calendar)target[i]);
        }
        return result;
    }
    
    public List<String> listFormat(final List<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        final List<String> result = new ArrayList<String>(target.size() + 2);
        for (final Calendar element : target) {
            result.add(format(element));
        }
        return result;
    }
    
    public Set<String> setFormat(final Set<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        final Set<String> result = new LinkedHashSet<String>(target.size() + 2);
        for (final Calendar element : target) {
            result.add(format(element));
        }
        return result;
    }

    
    
    
    public String format(final Calendar target, final String pattern) {
        if (target == null) {
            return null;
        }
        try {
            return DateUtils.format(target, pattern, this.locale);
        } catch (final Exception e) {
            throw new TemplateProcessingException(
                    "Error formatting calendar with format pattern \"" + pattern + "\"", e);
        }
    }
    
    public String[] arrayFormat(final Object[] target, final String pattern) {
        if (target == null) {
            return null;
        }
        final String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = format((Calendar)target[i], pattern);
        }
        return result;
    }
    
    public List<String> listFormat(final List<? extends Calendar> target, final String pattern) {
        if (target == null) {
            return null;
        }
        final List<String> result = new ArrayList<String>(target.size() + 2);
        for (final Calendar element : target) {
            result.add(format(element, pattern));
        }
        return result;
    }
    
    public Set<String> setFormat(final Set<? extends Calendar> target, final String pattern) {
        if (target == null) {
            return null;
        }
        final Set<String> result = new LinkedHashSet<String>(target.size() + 2);
        for (final Calendar element : target) {
            result.add(format(element, pattern));
        }
        return result;
    }

    
    
    

    public Integer day(final Calendar target) {
        if (target == null) {
            return null;
        }
        return DateUtils.day(target);
    }
    
    public Integer[] arrayDay(final Object[] target) {
        if (target == null) {
            return null;
        }
        final Integer[] result = new Integer[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = day((Calendar)target[i]);
        }
        return result;
    }
    
    public List<Integer> listDay(final List<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        final List<Integer> result = new ArrayList<Integer>(target.size() + 2);
        for (final Calendar element : target) {
            result.add(day(element));
        }
        return result;
    }
    
    public Set<Integer> setDay(final Set<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        final Set<Integer> result = new LinkedHashSet<Integer>(target.size() + 2);
        for (final Calendar element : target) {
            result.add(day(element));
        }
        return result;
    }

    
    
    

    public Integer month(final Calendar target) {
        if (target == null) {
            return null;
        }
        return DateUtils.month(target);
    }
    
    public Integer[] arrayMonth(final Object[] target) {
        if (target == null) {
            return null;
        }
        final Integer[] result = new Integer[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = month((Calendar)target[i]);
        }
        return result;
    }
    
    public List<Integer> listMonth(final List<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        final List<Integer> result = new ArrayList<Integer>(target.size() + 2);
        for (final Calendar element : target) {
            result.add(month(element));
        }
        return result;
    }
    
    public Set<Integer> setMonth(final Set<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        final Set<Integer> result = new LinkedHashSet<Integer>(target.size() + 2);
        for (final Calendar element : target) {
            result.add(month(element));
        }
        return result;
    }


    
    

    public String monthName(final Calendar target) {
        if (target == null) {
            return null;
        }
        return DateUtils.monthName(target, this.locale);
    }
    
    public String[] arrayMonthName(final Object[] target) {
        if (target == null) {
            return null;
        }
        final String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = monthName((Calendar)target[i]);
        }
        return result;
    }
    
    public List<String> listMonthName(final List<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        final List<String> result = new ArrayList<String>(target.size() + 2);
        for (final Calendar element : target) {
            result.add(monthName(element));
        }
        return result;
    }
    
    public Set<String> setMonthName(final Set<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        final Set<String> result = new LinkedHashSet<String>(target.size() + 2);
        for (final Calendar element : target) {
            result.add(monthName(element));
        }
        return result;
    }

    
    

    public String monthNameShort(final Calendar target) {
        if (target == null) {
            return null;
        }
        return DateUtils.monthNameShort(target, this.locale);
    }
    
    public String[] arrayMonthNameShort(final Object[] target) {
        if (target == null) {
            return null;
        }
        final String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = monthNameShort((Calendar)target[i]);
        }
        return result;
    }
    
    public List<String> listMonthNameShort(final List<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        final List<String> result = new ArrayList<String>(target.size() + 2);
        for (final Calendar element : target) {
            result.add(monthNameShort(element));
        }
        return result;
    }
    
    public Set<String> setMonthNameShort(final Set<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        final Set<String> result = new LinkedHashSet<String>(target.size() + 2);
        for (final Calendar element : target) {
            result.add(monthNameShort(element));
        }
        return result;
    }

    
    
    
    

    public Integer year(final Calendar target) {
        if (target == null) {
            return null;
        }
        return DateUtils.year(target);
    }
    
    public Integer[] arrayYear(final Object[] target) {
        if (target == null) {
            return null;
        }
        final Integer[] result = new Integer[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = year((Calendar)target[i]);
        }
        return result;
    }
    
    public List<Integer> listYear(final List<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        final List<Integer> result = new ArrayList<Integer>(target.size() + 2);
        for (final Calendar element : target) {
            result.add(year(element));
        }
        return result;
    }
    
    public Set<Integer> setYear(final Set<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        final Set<Integer> result = new LinkedHashSet<Integer>(target.size() + 2);
        for (final Calendar element : target) {
            result.add(year(element));
        }
        return result;
    }

    
    
    

    public Integer dayOfWeek(final Calendar target) {
        if (target == null) {
            return null;
        }
        return DateUtils.dayOfWeek(target);
    }
    
    public Integer[] arrayDayOfWeek(final Object[] target) {
        if (target == null) {
            return null;
        }
        final Integer[] result = new Integer[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = dayOfWeek((Calendar)target[i]);
        }
        return result;
    }
    
    public List<Integer> listDayOfWeek(final List<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        final List<Integer> result = new ArrayList<Integer>(target.size() + 2);
        for (final Calendar element : target) {
            result.add(dayOfWeek(element));
        }
        return result;
    }
    
    public Set<Integer> setDayOfWeek(final Set<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        final Set<Integer> result = new LinkedHashSet<Integer>(target.size() + 2);
        for (final Calendar element : target) {
            result.add(dayOfWeek(element));
        }
        return result;
    }
    

    
    
    

    public String dayOfWeekName(final Calendar target) {
        if (target == null) {
            return null;
        }
        return DateUtils.dayOfWeekName(target, this.locale);
    }
    
    public String[] arrayDayOfWeekName(final Object[] target) {
        if (target == null) {
            return null;
        }
        final String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = dayOfWeekName((Calendar)target[i]);
        }
        return result;
    }
    
    public List<String> listDayOfWeekName(final List<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        final List<String> result = new ArrayList<String>(target.size() + 2);
        for (final Calendar element : target) {
            result.add(dayOfWeekName(element));
        }
        return result;
    }
    
    public Set<String> setDayOfWeekName(final Set<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        final Set<String> result = new LinkedHashSet<String>(target.size() + 2);
        for (final Calendar element : target) {
            result.add(dayOfWeekName(element));
        }
        return result;
    }

    
    
    
    

    public String dayOfWeekNameShort(final Calendar target) {
        if (target == null) {
            return null;
        }
        return DateUtils.dayOfWeekNameShort(target, this.locale);
    }
    
    public String[] arrayDayOfWeekNameShort(final Object[] target) {
        if (target == null) {
            return null;
        }
        final String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = dayOfWeekNameShort((Calendar)target[i]);
        }
        return result;
    }
    
    public List<String> listDayOfWeekNameShort(final List<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        final List<String> result = new ArrayList<String>(target.size() + 2);
        for (final Calendar element : target) {
            result.add(dayOfWeekNameShort(element));
        }
        return result;
    }
    
    public Set<String> setDayOfWeekNameShort(final Set<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        final Set<String> result = new LinkedHashSet<String>(target.size() + 2);
        for (final Calendar element : target) {
            result.add(dayOfWeekNameShort(element));
        }
        return result;
    }

    

    
    
    

    public Integer hour(final Calendar target) {
        if (target == null) {
            return null;
        }
        return DateUtils.hour(target);
    }
    
    public Integer[] arrayHour(final Object[] target) {
        if (target == null) {
            return null;
        }
        final Integer[] result = new Integer[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = hour((Calendar)target[i]);
        }
        return result;
    }
    
    public List<Integer> listHour(final List<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        final List<Integer> result = new ArrayList<Integer>(target.size() + 2);
        for (final Calendar element : target) {
            result.add(hour(element));
        }
        return result;
    }
    
    public Set<Integer> setHour(final Set<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        final Set<Integer> result = new LinkedHashSet<Integer>(target.size() + 2);
        for (final Calendar element : target) {
            result.add(hour(element));
        }
        return result;
    }

    

    
    
    

    public Integer minute(final Calendar target) {
        if (target == null) {
            return null;
        }
        return DateUtils.minute(target);
    }
    
    public Integer[] arrayMinute(final Object[] target) {
        if (target == null) {
            return null;
        }
        final Integer[] result = new Integer[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = minute((Calendar)target[i]);
        }
        return result;
    }
    
    public List<Integer> listMinute(final List<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        final List<Integer> result = new ArrayList<Integer>(target.size() + 2);
        for (final Calendar element : target) {
            result.add(minute(element));
        }
        return result;
    }
    
    public Set<Integer> setMinute(final Set<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        final Set<Integer> result = new LinkedHashSet<Integer>(target.size() + 2);
        for (final Calendar element : target) {
            result.add(minute(element));
        }
        return result;
    }

    

    
    
    

    public Integer second(final Calendar target) {
        if (target == null) {
            return null;
        }
        return DateUtils.second(target);
    }
    
    public Integer[] arraySecond(final Object[] target) {
        if (target == null) {
            return null;
        }
        final Integer[] result = new Integer[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = second((Calendar)target[i]);
        }
        return result;
    }
    
    public List<Integer> listSecond(final List<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        final List<Integer> result = new ArrayList<Integer>(target.size() + 2);
        for (final Calendar element : target) {
            result.add(second(element));
        }
        return result;
    }
    
    public Set<Integer> setSecond(final Set<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        final Set<Integer> result = new LinkedHashSet<Integer>(target.size() + 2);
        for (final Calendar element : target) {
            result.add(second(element));
        }
        return result;
    }

    

    
    
    

    public Integer millisecond(final Calendar target) {
        if (target == null) {
            return null;
        }
        return DateUtils.millisecond(target);
    }
    
    public Integer[] arrayMillisecond(final Object[] target) {
        if (target == null) {
            return null;
        }
        final Integer[] result = new Integer[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = millisecond((Calendar)target[i]);
        }
        return result;
    }
    
    public List<Integer> listMillisecond(final List<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        final List<Integer> result = new ArrayList<Integer>(target.size() + 2);
        for (final Calendar element : target) {
            result.add(millisecond(element));
        }
        return result;
    }
    
    public Set<Integer> setMillisecond(final Set<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        final Set<Integer> result = new LinkedHashSet<Integer>(target.size() + 2);
        for (final Calendar element : target) {
            result.add(millisecond(element));
        }
        return result;
    }



    /**
     * 
     * @param target target
     * @return the result
     * @since 2.1.4
     */
    public String formatISO(final Calendar target) {
        if (target == null) {
            return null;
        }
        try {
            return DateUtils.formatISO(target);
        } catch (final Exception e) {
            throw new TemplateProcessingException("Error formatting calendar as ISO8601", e);
        }
    }

    /**
     * 
     * @param target target
     * @return the result
     * @since 2.1.4
     */
    public String[] arrayFormatISO(final Object[] target) {
        if (target == null) {
            return null;
        }
        final String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = formatISO((Calendar) target[i]);
        }
        return result;
    }

    /**
     * 
     * @param target target
     * @return the result
     * @since 2.1.4
     */
    public List<String> listFormatISO(final List<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        final List<String> result = new ArrayList<String>(target.size() + 2);
        for (final Calendar element : target) {
            result.add(formatISO(element));
        }
        return result;
    }

    /**
     * 
     * @param target target
     * @return the result
     * @since 2.1.4
     */
    public Set<String> setFormatISO(final Set<? extends Calendar> target) {
        if (target == null) {
            return null;
        }
        final Set<String> result = new LinkedHashSet<String>(target.size() + 2);
        for (final Calendar element : target) {
            result.add(formatISO(element));
        }
        return result;
    }


}
