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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.TimeZone;

import org.thymeleaf.util.EvaluationUtils;
import org.thymeleaf.util.Validate;

/**
 * This class instances implementations of Temporal ({@link Temporal}) according to different sets
 * of parameters.
 *
 * @author Jos&eacute; Miguel Samper
 *
 * @since 3.1.0
 */
public final class TemporalCreationUtils {

    public TemporalCreationUtils() {
        super();
    }
    
    /**
     *
     * @return a instance of java.time.LocalDate
     * @since 2.1.0
     */
    public Temporal create(final Object year, final Object month, final Object day) {
        return LocalDate.of(integer(year), integer(month), integer(day));
    }

    /**
     *
     * @return a instance of java.time.LocalDateTime
     * @since 2.1.0
     */
    public Temporal create(final Object year, final Object month, final Object day,
            final Object hour, final Object minute) {
        return LocalDateTime.of(integer(year), integer(month), integer(day), integer(hour), integer(minute));
    }

    /**
     *
     * @return a instance of java.time.LocalDateTime
     * @since 2.1.0
     */
    public Temporal create(final Object year, final Object month, final Object day,
            final Object hour, final Object minute, final Object second) {
        return LocalDateTime.of(integer(year), integer(month), integer(day),
            integer(hour), integer(minute), integer(second));
    }

    /**
     *
     * @return a instance of java.time.LocalDateTime
     * @since 2.1.0
     */
    public Temporal create(final Object year, final Object month, final Object day,
            final Object hour, final Object minute, final Object second, final Object nanosecond) {
        return LocalDateTime.of(integer(year), integer(month), integer(day),
            integer(hour), integer(minute), integer(second), integer(nanosecond));
    }

    /**
     *
     * @return a instance of java.time.LocalDateTime
     * @since 2.1.0
     */
    public Temporal createNow() {
        return LocalDateTime.now();
    }

    /**
     *
     * @return a instance of java.time.ZonedDateTime
     * @since 2.1.0
     */
    public Temporal createNowForTimeZone(final Object zoneId) {
        return ZonedDateTime.now(zoneId(zoneId));
    }

    /**
     *
     * @return a instance of java.time.LocalDate
     * @since 2.1.0
     */
    public Temporal createToday() {
        return LocalDate.now();
    }

    /**
     *
     * @return a instance of java.time.ZonedDateTime with 00:00:00.000 for the time part
     * @since 2.1.0
     */
    public Temporal createTodayForTimeZone(final Object zoneId) {
        return ZonedDateTime.now(zoneId(zoneId))
            .withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    /**
     *
     * @return a instance of java.time.LocalDate
     * @since 2.1.0
     */
    public Temporal createDate(String isoDate) {
        return LocalDate.parse(isoDate);
    }

    /**
     *
     * @return a instance of java.time.LocalDateTime
     * @since 2.1.0
     */
    public Temporal createDateTime(String isoDate) {
        return LocalDateTime.parse(isoDate);
    }

    /**
     *
     * @return a instance of java.time.LocalDate
     * @since 2.1.0
     */
    public Temporal createDate(String isoDate, String pattern) {
        return LocalDate.parse(isoDate, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     *
     * @return a instance of java.time.LocalDateTime
     * @since 2.1.0
     */
    public Temporal createDateTime(String isoDate, String pattern) {
        return LocalDateTime.parse(isoDate, DateTimeFormatter.ofPattern(pattern));
    }

    private int integer(final Object number) {
        Validate.notNull(number, "Argument cannot be null");
        return EvaluationUtils.evaluateAsNumber(number).intValue();
    }

    private ZoneId zoneId(final Object zoneId) {
        Validate.notNull(zoneId, "ZoneId cannot be null");
        if (zoneId instanceof ZoneId) {
            return (ZoneId) zoneId;
        } else if (zoneId instanceof TimeZone) {
            TimeZone timeZone = (TimeZone) zoneId;
            return timeZone.toZoneId();
        } else {
            return ZoneId.of(zoneId.toString());
        }
    }
}
