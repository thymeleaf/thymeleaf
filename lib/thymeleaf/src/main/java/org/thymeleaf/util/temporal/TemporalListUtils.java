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

import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import org.thymeleaf.util.Validate;

import static java.util.stream.Collectors.toList;


/**
 * Formatting utilities for Java 8 Time object lists.
 *
 * @author Jos&eacute; Miguel Samper
 *
 * @since 3.1.0
 */
public final class TemporalListUtils {
    
    private final TemporalFormattingUtils temporalFormattingUtils;
    
    public TemporalListUtils(final Locale locale, final ZoneId defaultZoneId) {
        super();
        Validate.notNull(locale, "Locale cannot be null");
        Validate.notNull(defaultZoneId, "ZoneId cannot be null");
        temporalFormattingUtils = new TemporalFormattingUtils(locale, defaultZoneId);
    }

    public List<String> listFormat(final List<? extends Temporal> target) {
        return listFormat(target, temporalFormattingUtils::format);
    }

    public <T extends Temporal> List<String> listFormat(final List<T> target, final Locale locale) {
        return listFormat(target, time -> temporalFormattingUtils.format(time, locale));
    }

    public <T extends Temporal> List<String> listFormat(final List<T> target, final String pattern) {
        return listFormat(target, time -> temporalFormattingUtils.format(time, pattern, null));
    }

    public <T extends Temporal> List<String> listFormat(final List<T> target, final String pattern, final Locale locale) {
        return listFormat(target, time -> temporalFormattingUtils.format(time, pattern, locale, null));
    }
    
    public List<Integer> listDay(final List<? extends Temporal> target) {
        return listFormat(target, temporalFormattingUtils::day);
    }
    
    public List<Integer> listMonth(final List<? extends Temporal> target) {
        return listFormat(target, temporalFormattingUtils::month);
    }

    public List<String> listMonthName(final List<? extends Temporal> target) {
        return listFormat(target, temporalFormattingUtils::monthName);
    }

    public List<String> listMonthNameShort(final List<? extends Temporal> target) {
        return listFormat(target, temporalFormattingUtils::monthNameShort);
    }

    public List<Integer> listYear(final List<? extends Temporal> target) {
        return listFormat(target, temporalFormattingUtils::year);
    }
    
    public List<Integer> listDayOfWeek(final List<? extends Temporal> target) {
        return listFormat(target, temporalFormattingUtils::dayOfWeek);
    }

    public List<String> listDayOfWeekName(final List<? extends Temporal> target) {
        return listFormat(target, temporalFormattingUtils::dayOfWeekName);
    }
    
    public List<String> listDayOfWeekNameShort(final List<? extends Temporal> target) {
        return listFormat(target, temporalFormattingUtils::dayOfWeekNameShort);
    }
    
    public List<Integer> listHour(final List<? extends Temporal> target) {
        return listFormat(target, temporalFormattingUtils::hour);
    }

    public List<Integer> listMinute(final List<? extends Temporal> target) {
        return listFormat(target, temporalFormattingUtils::minute);
    }

    public List<Integer> listSecond(final List<? extends Temporal> target) {
        return listFormat(target, temporalFormattingUtils::second);
    }

    public List<Integer> listNanosecond(final List<? extends Temporal> target) {
        return listFormat(target, temporalFormattingUtils::nanosecond);
    }

    public List<String> listFormatISO(final List<? extends Temporal> target) {
        return listFormat(target, temporalFormattingUtils::formatISO);
    }
    
    private <R extends Object, T extends Temporal> List<R> listFormat(
            final List<T> target, final Function<T, R> mapFunction) {
        Validate.notNull(target, "Target cannot be null");
        return target.stream()
            .map(time -> mapFunction.apply(time))
            .collect(toList());
    }
    
}
