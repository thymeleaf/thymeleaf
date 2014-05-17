/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.util;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class DateUtils {
    
    
    private static final Map<DateFormatKey,DateFormat> dateFormats = new ConcurrentHashMap<DateFormatKey, DateFormat>();
    

    /*
     * This SimpleDateFormat defines an almost-ISO8601 formatter.
     *
     * The correct ISO8601 format would be "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", but the "X" pattern (which outputs the
     * timezone as "+02:00" or "Z" instead of "+0200") was not added until Java SE 7. So the use of this
     * SimpleDateFormat object requires additional post-processing.
     *
     * Note SimpleDateFormat objects are NOT thread-safe, so use of this object must be synchronized.
     */
    private static final SimpleDateFormat ISO8601_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZ");

    
    
    

    
    /**
     * 
     * @since 1.1.2
     */
    public static Calendar create(final Object year, final Object month, final Object day) {
        return create(year, month, day, null, null, null, null);
    }

    
    /**
     * 
     * @since 1.1.2
     */
    public static Calendar create(final Object year, final Object month, final Object day, 
            final Object hour, final Object minute) {
        return create(year, month, day, hour, minute, null, null);
    }

    
    /**
     * 
     * @since 1.1.2
     */
    public static Calendar create(final Object year, final Object month, final Object day, 
            final Object hour, final Object minute, final Object second) {
        return create(year, month, day, hour, minute, second, null);
    }
    
    
    /**
     * 
     * @since 1.1.2
     */
    public static Calendar create(final Object year, final Object month, final Object day, 
            final Object hour, final Object minute, final Object second, final Object millisecond) {
        
        final BigDecimal nYear = ObjectUtils.evaluateAsNumber(year);
        final BigDecimal nMonth = ObjectUtils.evaluateAsNumber(month);
        final BigDecimal nDay = ObjectUtils.evaluateAsNumber(day);
        final BigDecimal nHour = ObjectUtils.evaluateAsNumber(hour);
        final BigDecimal nMinute = ObjectUtils.evaluateAsNumber(minute);
        final BigDecimal nSecond = ObjectUtils.evaluateAsNumber(second);
        final BigDecimal nMillisecond = ObjectUtils.evaluateAsNumber(millisecond);
        
        final Calendar cal = Calendar.getInstance();
        
        if (nYear == null || nMonth == null || nDay == null) {
            throw new IllegalArgumentException(
                    "Cannot create Calendar/Date object with null year (" + nYear + "), " +
                    "month (" + nMonth + ") or day (" + nDay + ")");
        }
        
        cal.set(Calendar.YEAR, nYear.intValue());
        cal.set(Calendar.MONTH, nMonth.intValue() - 1);
        cal.set(Calendar.DAY_OF_MONTH, nDay.intValue());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        if (nHour != null && nMinute != null) {
            
            cal.set(Calendar.HOUR_OF_DAY, nHour.intValue());
            cal.set(Calendar.MINUTE, nMinute.intValue());
            
            if (nSecond != null) {
                
                cal.set(Calendar.SECOND, nSecond.intValue());
                
                if (nMillisecond != null) {
                    
                    cal.set(Calendar.MILLISECOND, nMillisecond.intValue());
                    
                }
                
            } else if (nMillisecond != null){
                
                throw new IllegalArgumentException(
                        "Calendar/Date object cannot be correctly created from a null second " +
                        "but non-null millisecond.");
                
            }
            
        } else if (nHour != null || nMinute != null) {
            
            throw new IllegalArgumentException(
                    "Calendar/Date object can only be correctly created if hour (" + nHour + ") " +
                    "and minute (" + nMinute + ") are either both null or non-null.");
            
        } else if (nSecond != null || nMillisecond != null) {
            
            throw new IllegalArgumentException(
                    "Calendar/Date object cannot be correctly created from a null hour and " +
                    "minute but non-null second and/or millisecond.");
            
        }
        
        
        return cal;
        
    }
    
    
    /**
     * 
     * @since 1.1.2
     */
    public static Calendar createNow() {
        return Calendar.getInstance();
    }
    

    /**
     * 
     * @since 1.1.2
     */
    public static Calendar createToday() {
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        return cal;
    }

    

    
    public static String format(final Object target, final Locale locale) {
        Validate.notNull(target, "Cannot apply format on null");
        return formatDate(target, locale);
    }
    
    public static String format(final Object target, final String pattern, final Locale locale) {
        Validate.notNull(target, "Cannot apply format on null");
        Validate.notEmpty(pattern, "Pattern cannot be null or empty");
        return formatDate(target, pattern, locale);
    }
    

    public static Integer day(final Object target) {
        Validate.notNull(target, "Cannot retrieve day from null");
        final Calendar cal = normalizeDate(target);
        return Integer.valueOf(cal.get(Calendar.DAY_OF_MONTH));
    }
    

    public static Integer month(final Object target) {
        Validate.notNull(target, "Cannot retrieve month from null");
        final Calendar cal = normalizeDate(target);
        return Integer.valueOf(cal.get(Calendar.MONTH) + 1);
    }
    

    public static String monthName(final Object target, final Locale locale) {
        Validate.notNull(target, "Cannot retrieve month name from null");
        return format(target, "MMMM", locale);
    }
    

    public static String monthNameShort(final Object target, final Locale locale) {
        Validate.notNull(target, "Cannot retrieve month name short from null");
        return format(target, "MMM", locale);
    }
    

    public static Integer year(final Object target) {
        Validate.notNull(target, "Cannot retrieve year from null");
        final Calendar cal = normalizeDate(target);
        return Integer.valueOf(cal.get(Calendar.YEAR));
    }
    

    public static Integer dayOfWeek(final Object target) {
        Validate.notNull(target, "Cannot retrieve day of week from null");
        final Calendar cal = normalizeDate(target);
        return Integer.valueOf(cal.get(Calendar.DAY_OF_WEEK));
    }
    

    public static String dayOfWeekName(final Object target, final Locale locale) {
        Validate.notNull(target, "Cannot retrieve day of week name from null");
        return format(target, "EEEE", locale);
    }
    

    public static String dayOfWeekNameShort(final Object target, final Locale locale) {
        Validate.notNull(target, "Cannot retrieve day of week name shortfrom null");
        return format(target, "EEE", locale);
    }
    

    public static Integer hour(final Object target) {
        Validate.notNull(target, "Cannot retrieve hour from null");
        final Calendar cal = normalizeDate(target);
        return Integer.valueOf(cal.get(Calendar.HOUR_OF_DAY));
    }
    

    public static Integer minute(final Object target) {
        Validate.notNull(target, "Cannot retrieve hour from null");
        final Calendar cal = normalizeDate(target);
        return Integer.valueOf(cal.get(Calendar.MINUTE));
    }
    

    public static Integer second(final Object target) {
        Validate.notNull(target, "Cannot retrieve hour from null");
        final Calendar cal = normalizeDate(target);
        return Integer.valueOf(cal.get(Calendar.SECOND));
    }
    

    public static Integer millisecond(final Object target) {
        Validate.notNull(target, "Cannot retrieve hour from null");
        final Calendar cal = normalizeDate(target);
        return Integer.valueOf(cal.get(Calendar.MILLISECOND));
    }
    
    
    
    
    
    
    private static Calendar normalizeDate(final Object target) {
        if (target instanceof Calendar) {
            return (Calendar) target;
        } else if (target instanceof java.util.Date) {
            final Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(((java.util.Date)target).getTime());
            return cal;
        } else {
            throw new IllegalArgumentException(
                    "Cannot normalize class \"" + target.getClass().getName() + "\" as a date");
        }
    }
    
    
    
    
    
    private static String formatDate(final Object target, final Locale locale) {
        return formatDate(target, null, locale);
    }
    
    
    private static String formatDate(final Object target, final String pattern, final Locale locale) {

        Validate.notNull(target, "Cannot apply format on null");
        Validate.notNull(locale, "Locale cannot be null");
        

        final DateFormatKey key = new DateFormatKey(pattern, locale);
        
        DateFormat dateFormat = dateFormats.get(key);
        if (dateFormat == null) {
            if (StringUtils.isEmptyOrWhitespace(pattern)) {
                dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
            } else {
                dateFormat = new SimpleDateFormat(pattern, locale);
            }
            dateFormats.put(key, dateFormat);
        }
        
        if (target instanceof Calendar) {
            synchronized (dateFormat) {
                return dateFormat.format(((Calendar) target).getTime());
            }
        } else if (target instanceof java.util.Date) {
            synchronized (dateFormat) {
                return dateFormat.format((java.util.Date)target);
            }
        } else {
            throw new IllegalArgumentException(
                    "Cannot format object of class \"" + target.getClass().getName() + "\" as a date");
        }
        
    }





    /**
     *
     * @since 2.1.4
     */
    public static String formatISO(final Object target) {

        Validate.notNull(target, "Cannot apply format on null");

        final java.util.Date targetDate;
        if (target instanceof Calendar) {
            targetDate = ((Calendar)target).getTime();
        } else if (target instanceof java.util.Date) {
            targetDate = (java.util.Date)target;
        } else {
            throw new IllegalArgumentException(
                    "Cannot format object of class \"" + target.getClass().getName() + "\" as a date");
        }

        final String formatted;
        synchronized (ISO8601_DATE_FORMAT) {
            formatted = ISO8601_DATE_FORMAT.format(targetDate);
        }

        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(formatted, 0, 23);

        final String formattedTimeZone = formatted.substring(23);
        if (formattedTimeZone.equals("+0000")) {
            strBuilder.append("Z");
        } else {
            strBuilder.append(formattedTimeZone,0,3);
            strBuilder.append(':');
            strBuilder.append(formattedTimeZone,3,5);
        }

        return strBuilder.toString();

    }


    
    private DateUtils() {
        super();
    }
 
    
    
    
    
    
    
    
    
    private static final class DateFormatKey {
        
        private final String format;
        private final Locale locale;
        
        private DateFormatKey(final String format, final Locale locale) {
            super();
            Validate.notNull(locale, "Locale cannot be null");
            this.format = format;
            this.locale = locale;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((this.format == null) ? 0 : this.format.hashCode());
            result = prime * result + this.locale.hashCode();
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final DateFormatKey other = (DateFormatKey) obj;
            if (this.format == null) {
                if (other.format != null) {
                    return false;
                }
            } else if (!this.format.equals(other.format)) {
                return false;
            }
            return this.locale.equals(other.locale);
        }
        
    }


    
}
