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
package org.thymeleaf.util;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class DateUtils {
    
    
    private static final Map<DateFormatKey,DateFormat> dateFormats = new ConcurrentHashMap<DateFormatKey, DateFormat>(4, 0.9f, 2);

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
     * @param year year
     * @param month month
     * @param day day
     * @return the result
     * @since 1.1.2
     */
    public static Calendar create(final Object year, final Object month, final Object day) {
        return create(year, month, day, null, null, null, null, null, null);
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
    public static Calendar create(final Object year, final Object month, final Object day, 
            final Object hour, final Object minute) {
        return create(year, month, day, hour, minute, null, null, null, null);
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
    public static Calendar create(final Object year, final Object month, final Object day, 
            final Object hour, final Object minute, final Object second) {
        return create(year, month, day, hour, minute, second, null, null, null);
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
    public static Calendar create(final Object year, final Object month, final Object day,
            final Object hour, final Object minute, final Object second, final Object millisecond) {
        return create(year, month, day, hour, minute, second, millisecond, null, null);
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
    public static Calendar create(final Object year, final Object month, final Object day,
            final Object hour, final Object minute, final Object second, final Object millisecond,
            final Object timeZone) {
        return create(year, month, day, hour, minute, second, millisecond, timeZone, null);
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
     * @param locale locale
     * @return the result
     * @since 2.1.0
     */
    public static Calendar create(final Object year, final Object month, final Object day,
            final Object hour, final Object minute, final Object second, final Object millisecond,
            final Object timeZone, final Locale locale) {

        final BigDecimal nYear =
                (year == null?
                        null : EvaluationUtils.evaluateAsNumber(year));
        final BigDecimal nMonth =
                (month == null?
                        null : EvaluationUtils.evaluateAsNumber(month));
        final BigDecimal nDay =
                (day == null?
                        null : EvaluationUtils.evaluateAsNumber(day));
        final BigDecimal nHour =
                (hour == null?
                        null : EvaluationUtils.evaluateAsNumber(hour));
        final BigDecimal nMinute =
                (minute == null?
                        null : EvaluationUtils.evaluateAsNumber(minute));
        final BigDecimal nSecond =
                (second == null?
                        null : EvaluationUtils.evaluateAsNumber(second));
        final BigDecimal nMillisecond =
                (millisecond == null?
                        null : EvaluationUtils.evaluateAsNumber(millisecond));

        final TimeZone tzTimeZone =
                (timeZone != null?
                        (timeZone instanceof TimeZone?
                                (TimeZone) timeZone : TimeZone.getTimeZone(timeZone.toString())) :
                        null);

        final Calendar cal;
        if (tzTimeZone != null && locale != null) {
            cal = Calendar.getInstance(tzTimeZone, locale);
        } else if (tzTimeZone != null) {
            cal = Calendar.getInstance(tzTimeZone);
        } else if (locale != null) {
            cal = Calendar.getInstance(locale);
        } else {
            cal = Calendar.getInstance();
        }

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
     * @return the result
     * @since 1.1.2
     */
    public static Calendar createNow() {
        return createNow(null, null);
    }


    /**
     * 
     * @param timeZone timeZone
     * @return the result
     * @since 2.1.0
     */
    public static Calendar createNow(final Object timeZone) {
        return createNow(timeZone, null);
    }


    /**
     * 
     * @param timeZone timeZone
     * @param locale locale
     * @return the result
     * @since 2.1.0
     */
    public static Calendar createNow(final Object timeZone, final Locale locale) {

        final TimeZone tzTimeZone =
                (timeZone != null?
                    (timeZone instanceof TimeZone?
                            (TimeZone) timeZone : TimeZone.getTimeZone(timeZone.toString())) :
                    null);

        if (tzTimeZone != null && locale != null) {
            return Calendar.getInstance(tzTimeZone, locale);
        }
        if (tzTimeZone != null) {
            return Calendar.getInstance(tzTimeZone);
        }
        if (locale != null) {
            return Calendar.getInstance(locale);
        }
        return Calendar.getInstance();

    }



    /**
     * 
     * @return the result
     * @since 1.1.2
     */
    public static Calendar createToday() {
        return createToday(null, null);
    }



    /**
     * 
     * @param timeZone timeZone
     * @return the result
     * @since 2.1.0
     */
    public static Calendar createToday(final Object timeZone) {
        return createToday(timeZone, null);
    }



    /**
     * 
     * @param timeZone timeZone
     * @param locale locale
     * @return the result
     * @since 2.1.0
     */
    public static Calendar createToday(final Object timeZone, final Locale locale) {
        final Calendar cal = createNow(timeZone, locale);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        return cal;
    }

    

    
    public static String format(final Object target, final Locale locale) {
        if (target == null) {
            return null;
        }
        return formatDate(target, locale);
    }
    
    public static String format(final Object target, final String pattern, final Locale locale) {
        Validate.notEmpty(pattern, "Pattern cannot be null or empty");
        if (target == null) {
            return null;
        }
        return formatDate(target, pattern, locale);
    }
    

    public static Integer day(final Object target) {
        if (target == null) {
            return null;
        }
        final Calendar cal = normalizeDate(target);
        return Integer.valueOf(cal.get(Calendar.DAY_OF_MONTH));
    }
    

    public static Integer month(final Object target) {
        if (target == null) {
            return null;
        }
        final Calendar cal = normalizeDate(target);
        return Integer.valueOf(cal.get(Calendar.MONTH) + 1);
    }
    

    public static String monthName(final Object target, final Locale locale) {
        if (target == null) {
            return null;
        }
        return format(target, "MMMM", locale);
    }
    

    public static String monthNameShort(final Object target, final Locale locale) {
        if (target == null) {
            return null;
        }
        return format(target, "MMM", locale);
    }
    

    public static Integer year(final Object target) {
        if (target == null) {
            return null;
        }
        final Calendar cal = normalizeDate(target);
        return Integer.valueOf(cal.get(Calendar.YEAR));
    }
    

    public static Integer dayOfWeek(final Object target) {
        if (target == null) {
            return null;
        }
        final Calendar cal = normalizeDate(target);
        return Integer.valueOf(cal.get(Calendar.DAY_OF_WEEK));
    }
    

    public static String dayOfWeekName(final Object target, final Locale locale) {
        if (target == null) {
            return null;
        }
        return format(target, "EEEE", locale);
    }
    

    public static String dayOfWeekNameShort(final Object target, final Locale locale) {
        if (target == null) {
            return null;
        }
        return format(target, "EEE", locale);
    }
    

    public static Integer hour(final Object target) {
        if (target == null) {
            return null;
        }
        final Calendar cal = normalizeDate(target);
        return Integer.valueOf(cal.get(Calendar.HOUR_OF_DAY));
    }
    

    public static Integer minute(final Object target) {
        if (target == null) {
            return null;
        }
        final Calendar cal = normalizeDate(target);
        return Integer.valueOf(cal.get(Calendar.MINUTE));
    }
    

    public static Integer second(final Object target) {
        if (target == null) {
            return null;
        }
        final Calendar cal = normalizeDate(target);
        return Integer.valueOf(cal.get(Calendar.SECOND));
    }
    

    public static Integer millisecond(final Object target) {
        if (target == null) {
            return null;
        }
        final Calendar cal = normalizeDate(target);
        return Integer.valueOf(cal.get(Calendar.MILLISECOND));
    }
    
    
    
    
    
    
    private static Calendar normalizeDate(final Object target) {
        if (target == null) {
            return null;
        }
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
        if (target == null) {
            return null;
        }
        return formatDate(target, null, locale);
    }
    
    
    private static String formatDate(final Object target, final String pattern, final Locale locale) {

        Validate.notNull(locale, "Locale cannot be null");

        if (target == null) {
            return null;
        }

        final DateFormatKey key = new DateFormatKey(target, pattern, locale);
        
        DateFormat dateFormat = dateFormats.get(key);
        if (dateFormat == null) {
            if (StringUtils.isEmptyOrWhitespace(pattern)) {
                dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
            } else {
                dateFormat = new SimpleDateFormat(pattern, locale);
            }
            if (key.timeZone != null) {
                dateFormat.setTimeZone(key.timeZone);
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
     * @param target target
     * @return the result
     * @since 2.1.4
     */
    public static String formatISO(final Object target) {

        if (target == null) {
            return null;
        }

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

        final StringBuilder strBuilder = new StringBuilder(formatted.length() + 1);
        strBuilder.append(formatted);
        strBuilder.insert(26, ':');

        return strBuilder.toString();

    }


    
    private DateUtils() {
        super();
    }
 
    
    
    
    
    
    
    
    
    private static final class DateFormatKey {
        
        final String format;
        final TimeZone timeZone;
        final Locale locale;
        
        DateFormatKey(final Object target, final String format, final Locale locale) {
            super();
            Validate.notNull(locale, "Locale cannot be null");
            this.format = format;
            this.locale = locale;
            if (target != null && target instanceof Calendar) {
                this.timeZone = ((Calendar)target).getTimeZone();
            } else {
                this.timeZone = null;
            }
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((this.format == null) ? 0 : this.format.hashCode());
            result = prime * result + this.locale.hashCode();
            result = prime * result + ((this.timeZone == null) ? 0 : this.timeZone.hashCode());
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
            if (this.timeZone == null) {
                if (other.timeZone != null) {
                    return false;
                }
            } else if (!this.timeZone.equals(other.timeZone)) {
                return false;
            }
            return this.locale.equals(other.locale);
        }
        
    }


    
}
