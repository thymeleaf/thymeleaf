/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.util.temporal;

import java.lang.reflect.Array;
import java.time.ZoneId;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;

import org.thymeleaf.util.Validate;


/**
 * Formatting utilities for Java 8 Time object arrays.
 *
 * @author Jos&eacute; Miguel Samper
 *
 * @since 3.1.0
 */
public final class TemporalArrayUtils {

    private final TemporalFormattingUtils temporalFormattingUtils;
    
    public TemporalArrayUtils(final Locale locale, final ZoneId defaultZoneId) {
        super();
        Validate.notNull(locale, "Locale cannot be null");
        Validate.notNull(defaultZoneId, "ZoneId cannot be null");
        temporalFormattingUtils = new TemporalFormattingUtils(locale, defaultZoneId);
    }

    public String[] arrayFormat(final Object[] target) {
        return arrayFormat(target, temporalFormattingUtils::format, String.class);
    }

    public String[] arrayFormat(final Object[] target, final Locale locale) {
        return arrayFormat(target, time -> temporalFormattingUtils.format(time, locale), String.class);
    }

    public String[] arrayFormat(final Object[] target, final String pattern) {
        return arrayFormat(target, time -> temporalFormattingUtils.format(time, pattern, null), String.class);
    }

    public String[] arrayFormat(final Object[] target, final String pattern, final Locale locale) {
        return arrayFormat(target, time -> temporalFormattingUtils.format(time, pattern, locale, null), String.class);
    }

    public Integer[] arrayDay(final Object[] target) {
        return arrayFormat(target, temporalFormattingUtils::day, Integer.class);
    }

    public Integer[] arrayMonth(final Object[] target) {
        return arrayFormat(target, temporalFormattingUtils::month, Integer.class);
    }

    public String[] arrayMonthName(final Object[] target) {
        return arrayFormat(target, temporalFormattingUtils::monthName, String.class);
    }

    public String[] arrayMonthNameShort(final Object[] target) {
        return arrayFormat(target, temporalFormattingUtils::monthNameShort, String.class);
    }
    
    public Integer[] arrayYear(final Object[] target) {
        return arrayFormat(target, temporalFormattingUtils::year, Integer.class);
    }
    
    public Integer[] arrayDayOfWeek(final Object[] target) {
        return arrayFormat(target, temporalFormattingUtils::dayOfWeek, Integer.class);
    }
    
    public String[] arrayDayOfWeekName(final Object[] target) {
        return arrayFormat(target, temporalFormattingUtils::dayOfWeekName, String.class);
    }

    public String[] arrayDayOfWeekNameShort(final Object[] target) {
        return arrayFormat(target, temporalFormattingUtils::dayOfWeekNameShort, String.class);
    }

    public Integer[] arrayHour(final Object[] target) {
        return arrayFormat(target, temporalFormattingUtils::hour, Integer.class);
    }

    public Integer[] arrayMinute(final Object[] target) {
        return arrayFormat(target, temporalFormattingUtils::minute, Integer.class);
    }

    public Integer[] arraySecond(final Object[] target) {
        return arrayFormat(target, temporalFormattingUtils::second, Integer.class);
    }

    public Integer[] arrayNanosecond(final Object[] target) {
        return arrayFormat(target, temporalFormattingUtils::nanosecond, Integer.class);
    }

    public String[] arrayFormatISO(final Object[] target) {
        return arrayFormat(target, temporalFormattingUtils::formatISO, String.class);
    }

    private <R extends Object> R[] arrayFormat(
            final Object[] target, final Function<Object, R> mapFunction, final Class<R> returnType) {
        Validate.notNull(target, "Target cannot be null");
        return Stream.of(target)
            .map(time -> mapFunction.apply(time))
            .toArray(length -> (R[]) Array.newInstance(returnType, length));
    }
}
