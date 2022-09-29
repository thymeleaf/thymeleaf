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
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;

import org.thymeleaf.util.Validate;

import static java.util.stream.Collectors.toSet;


/**
 * Formatting utilities for Java 8 Time object sets.
 *
 * @author Jos&eacute; Miguel Samper
 *
 * @since 3.1.0
 */
public final class TemporalSetUtils {

    private final TemporalFormattingUtils temporalFormattingUtils;
    
    public TemporalSetUtils(final Locale locale, final ZoneId defaultZoneId) {
        super();
        Validate.notNull(locale, "Locale cannot be null");
        Validate.notNull(defaultZoneId, "ZoneId cannot be null");
        temporalFormattingUtils = new TemporalFormattingUtils(locale, defaultZoneId);
    }

    public Set<String> setFormat(final Set<? extends Temporal> target) {
        return setFormat(target, temporalFormattingUtils::format);
    }

    public <T extends Temporal> Set<String> setFormat(final Set<T> target, final Locale locale) {
        return setFormat(target, time -> temporalFormattingUtils.format(time, locale));
    }

    public <T extends Temporal> Set<String> setFormat(final Set<T> target, final String pattern) {
        return setFormat(target, time -> temporalFormattingUtils.format(time, pattern, null));
    }

    public <T extends Temporal> Set<String> setFormat(final Set<T> target, final String pattern, final Locale locale) {
        return setFormat(target, time -> temporalFormattingUtils.format(time, pattern, locale, null));
    }

    public Set<Integer> setDay(final Set<? extends Temporal> target) {
        return setFormat(target, temporalFormattingUtils::day);
    }

    public Set<Integer> setMonth(final Set<? extends Temporal> target) {
        return setFormat(target, temporalFormattingUtils::month);
    }

    public Set<String> setMonthName(final Set<? extends Temporal> target) {
        return setFormat(target, temporalFormattingUtils::monthName);
    }

    public Set<String> setMonthNameShort(final Set<? extends Temporal> target) {
        return setFormat(target, temporalFormattingUtils::monthNameShort);
    }

    public Set<Integer> setYear(final Set<? extends Temporal> target) {
        return setFormat(target, temporalFormattingUtils::year);
    }
    
    public Set<Integer> setDayOfWeek(final Set<? extends Temporal> target) {
        return setFormat(target, temporalFormattingUtils::dayOfWeek);
    }

    public Set<String> setDayOfWeekName(final Set<? extends Temporal> target) {
        return setFormat(target, temporalFormattingUtils::dayOfWeekName);
    }
    
    public Set<String> setDayOfWeekNameShort(final Set<? extends Temporal> target) {
        return setFormat(target, temporalFormattingUtils::dayOfWeekNameShort);
    }
    
    public Set<Integer> setHour(final Set<? extends Temporal> target) {
        return setFormat(target, temporalFormattingUtils::hour);
    }
    
    public Set<Integer> setMinute(final Set<? extends Temporal> target) {
        return setFormat(target, temporalFormattingUtils::minute);
    }
    
    public Set<Integer> setSecond(final Set<? extends Temporal> target) {
        return setFormat(target, temporalFormattingUtils::second);
    }
    
    public Set<Integer> setNanosecond(final Set<? extends Temporal> target) {
        return setFormat(target, temporalFormattingUtils::nanosecond);
    }

    public Set<String> setFormatISO(final Set<? extends Temporal> target) {
        return setFormat(target, temporalFormattingUtils::formatISO);
    }

    private <R extends Object, T extends Temporal> Set<R> setFormat(
            final Set<T> target, final Function<T, R> mapFunction) {
        Validate.notNull(target, "Target cannot be null");
        return target.stream()
            .map(time -> mapFunction.apply(time))
            .collect(toSet());
    }

}
