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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class NumberUtils {

    
    
    public static String format(final Number target, final Integer minIntegerDigits, final Locale locale) {
        if (target == null) {
            return null;
        }
        Validate.notNull(minIntegerDigits, "Minimum integer digits cannot be null");
        return formatNumber(target, minIntegerDigits, NumberPointType.NONE, Integer.valueOf(0), NumberPointType.NONE, locale);
    }
    
    public static String format(final Number target, final Integer minIntegerDigits, final NumberPointType thousandsPointType, final Locale locale) {
        if (target == null) {
            return null;
        }
        Validate.notNull(minIntegerDigits, "Minimum integer digits cannot be null");
        Validate.notNull(thousandsPointType, "Thousands point type cannot be null");
        return formatNumber(target, minIntegerDigits, thousandsPointType, Integer.valueOf(0), NumberPointType.NONE, locale);
    }
    
    
    
    public static String format(final Number target, final Integer minIntegerDigits, final Integer decimalDigits, final Locale locale) {
        if (target == null) {
            return null;
        }
        Validate.notNull(minIntegerDigits, "Minimum integer digits cannot be null");
        Validate.notNull(decimalDigits, "Decimal digits cannot be null");
        return formatNumber(target, minIntegerDigits, NumberPointType.NONE, decimalDigits, NumberPointType.DEFAULT, locale);
    }

    
    
    public static String format(final Number target, final Integer minIntegerDigits, final Integer decimalDigits, final NumberPointType decimalPointType, final Locale locale) {
        if (target == null) {
            return null;
        }
        Validate.notNull(minIntegerDigits, "Minimum integer digits cannot be null");
        Validate.notNull(decimalDigits, "Decimal digits cannot be null");
        Validate.notNull(decimalPointType, "Decimal point type cannot be null");
        return formatNumber(target, minIntegerDigits, NumberPointType.NONE, decimalDigits, decimalPointType, locale);
    }

    
    
    public static String format(final Number target, final Integer minIntegerDigits, final NumberPointType thousandsPointType, final Integer decimalDigits, final Locale locale) {
        if (target == null) {
            return null;
        }
        Validate.notNull(minIntegerDigits, "Minimum integer digits cannot be null");
        Validate.notNull(thousandsPointType, "Thousands point type cannot be null");
        Validate.notNull(decimalDigits, "Decimal digits cannot be null");
        return formatNumber(target, minIntegerDigits, thousandsPointType, decimalDigits, NumberPointType.DEFAULT, locale);
    }

    
    
    public static String format(final Number target, final Integer minIntegerDigits, final NumberPointType thousandsPointType, final Integer decimalDigits, final NumberPointType decimalPointType, final Locale locale) {
        if (target == null) {
            return null;
        }
        Validate.notNull(minIntegerDigits, "Minimum integer digits cannot be null");
        Validate.notNull(thousandsPointType, "Thousands point type cannot be null");
        Validate.notNull(decimalDigits, "Decimal digits cannot be null");
        Validate.notNull(decimalPointType, "Decimal point type cannot be null");
        return formatNumber(target, minIntegerDigits, thousandsPointType, decimalDigits, decimalPointType, locale);
    }
    

    
    
    
    /**
     * <p>
     *   Produces an array with a sequence of integer numbers.
     * </p>
     * 
     * @param from value to start the sequence from
     * @param to value to produce the sequence to
     * @return the Integer[] sequence
     * 
     * @since 1.1.2
     */
    public static Integer[] sequence(final Integer from, final Integer to) {
        return sequence(from, to, Integer.valueOf(from <= to? 1 : -1));
    }
    

    
    
    
    /**
     * <p>
     *   Produces an array with a sequence of integer numbers, using a step.
     * </p>
     * 
     * @param from value to start the sequence from
     * @param to value to produce the sequence to
     * @param step the step to be used
     * @return the Integer[] sequence
     * 
     * @since 2.0.9
     */
    public static Integer[] sequence(final Integer from, final Integer to, final Integer step) {
        
        Validate.notNull(from, "Value to start the sequence from cannot be null");
        Validate.notNull(to, "Value to generate the sequence up to cannot be null");
        Validate.notNull(step, "Step to generate the sequence cannot be null");
        
        final int iFrom = from.intValue();
        final int iTo = to.intValue();
        final int iStep = step.intValue();

        if (iFrom == iTo) {
            return new Integer[] {Integer.valueOf(iFrom)};
        }

        if (iStep == 0) {
            // with iStep == 0, this would only be valid if iFrom == iTo, which it isn't - the rest are impossible
            throw new IllegalArgumentException("Cannot create sequence from " + iFrom + " to " + iTo + " with step " + iStep);
        }

        final List<Integer> values = new ArrayList<Integer>(10);
        if (iFrom < iTo && iStep > 0) {
            int i = iFrom;
            while (i <= iTo) {
                values.add(Integer.valueOf(i));
                i += iStep;
            }
        } else if (iFrom > iTo && iStep < 0){
            // iFrom > iTo
            int i = iFrom;
            while (i >= iTo) {
                values.add(Integer.valueOf(i));
                i += iStep;
            }
        }

        return values.toArray(new Integer[values.size()]);
        
    }

    /**
     * Formats a number as per the given values.
     * 
     * @param target             The number to format.
     * @param minIntegerDigits   Minimum number digits to return (0 padding).
     * @param thousandsPointType Character to use for separating number groups.
     * @param fractionDigits     Minimum number of fraction digits to format to
     *                           (0 padding).
     * @param decimalPointType   Character to use for separating decimals.
     * @param locale             Locale to draw more information from.
     * @return The number formatted as specified, or {@code null} if the number
     *         given is {@code null}.
     */
    private static String formatNumber(final Number target, final Integer minIntegerDigits,
        final NumberPointType thousandsPointType, final Integer fractionDigits,
        final NumberPointType decimalPointType, final Locale locale) {

        Validate.notNull(fractionDigits, "Fraction digits cannot be null");
        Validate.notNull(decimalPointType, "Decimal point type cannot be null");
        Validate.notNull(thousandsPointType, "Thousands point type cannot be null");
        Validate.notNull(locale, "Locale cannot be null");

        if (target == null) {
            return null;
        }

        DecimalFormat format = (DecimalFormat)NumberFormat.getNumberInstance(locale);
        format.setMinimumFractionDigits(fractionDigits.intValue());
        format.setMaximumFractionDigits(fractionDigits.intValue());
        if (minIntegerDigits != null) {
            format.setMinimumIntegerDigits(minIntegerDigits.intValue());
        }
        format.setDecimalSeparatorAlwaysShown(decimalPointType != NumberPointType.NONE && fractionDigits.intValue() > 0);
        format.setGroupingUsed(thousandsPointType != NumberPointType.NONE);
        format.setDecimalFormatSymbols(computeDecimalFormatSymbols(decimalPointType, thousandsPointType, locale));
        
        return format.format(target);
    }


    private static DecimalFormatSymbols computeDecimalFormatSymbols(
            final NumberPointType decimalPointType, final NumberPointType thousandsPointType, final Locale locale) {

        Validate.notNull(decimalPointType, "Decimal point type cannot be null");
        Validate.notNull(thousandsPointType, "Thousands point type cannot be null");
        Validate.notNull(locale, "Locale cannot be null");
        
        final DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        
        switch (decimalPointType) {
            case POINT :
                symbols.setDecimalSeparator('.');
                break;
            case COMMA :
                symbols.setDecimalSeparator(',');
                break;
            case WHITESPACE :
                symbols.setDecimalSeparator(' ');
                break;
            case DEFAULT :
                final DecimalFormatSymbols dfs = new DecimalFormatSymbols(locale);
                symbols.setDecimalSeparator(dfs.getDecimalSeparator());
                break;
            case NONE :
                // This should never happen
                symbols.setDecimalSeparator('?');
                break;
        }
        switch (thousandsPointType) {
            case POINT :
                symbols.setGroupingSeparator('.');
                break;
            case COMMA :
                symbols.setGroupingSeparator(',');
                break;
            case WHITESPACE :
                symbols.setGroupingSeparator(' ');
                break;
            case DEFAULT :
                final DecimalFormatSymbols dfs = new DecimalFormatSymbols(locale);
                symbols.setGroupingSeparator(dfs.getGroupingSeparator());
                break;
            case NONE :
                // This should never be shown
                symbols.setGroupingSeparator('?');
                break;
        }
        
        return symbols;
        
    }

    /**
     * Formats a number as a currency value according to the specified locale.
     * 
     * @param target The number to format.
     * @param locale Locale to use for formatting.
     * @return The number formatted as a currency, or {@code null} if the number
     *         given is {@code null}.
     */
    public static String formatCurrency(final Number target, final Locale locale) {

        Validate.notNull(locale, "Locale cannot be null");

        if (target == null) {
            return null;
        }

        NumberFormat format = NumberFormat.getCurrencyInstance(locale);

        return format.format(target);
    }

    /**
     * Formats a number as a percentage value.
     * 
     * @param target           The number to format.
     * @param minIntegerDigits Minimum number of digits to return (0 padding).
     * @param fractionDigits   Minimum number of fraction digits to return (0
     *                         padding).
     * @param locale           Locale to use for formatting.
     * @return The number formatted as a percentage, or {@code null} if the
     *         number given is {@code null}.
     */
    public static String formatPercent(final Number target, final Integer minIntegerDigits,
        final Integer fractionDigits, final Locale locale) {

        Validate.notNull(fractionDigits, "Fraction digits cannot be null");
        Validate.notNull(locale, "Locale cannot be null");

        if (target == null) {
            return null;
        }

        NumberFormat format = NumberFormat.getPercentInstance(locale);
        format.setMinimumFractionDigits(fractionDigits.intValue());
        format.setMaximumFractionDigits(fractionDigits.intValue());
        if (minIntegerDigits != null) {
            format.setMinimumIntegerDigits(minIntegerDigits.intValue());
        }

        return format.format(target);
    }


    private NumberUtils() {
        super();
    }
}
