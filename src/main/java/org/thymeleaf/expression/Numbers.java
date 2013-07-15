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
package org.thymeleaf.expression;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.NumberPointType;
import org.thymeleaf.util.NumberUtils;
import org.thymeleaf.util.Validate;


/**
 * <p>
 *   Utility class for performing numeric operations (esp. number formatting).
 * </p>
 * <p>
 *   An object of this class is usually available in variable evaluation expressions with the name
 *   <tt>#numbers</tt>.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class Numbers {

    
    private final Locale locale;
    
    
    public Numbers(final Locale locale) {
        super();
        this.locale = locale;
    }


    
    
    public String formatInteger(final Number target, final Integer minIntegerDigits) {
        try {
            return NumberUtils.format(target, minIntegerDigits, this.locale);
        } catch (final Exception e) {
            throw new TemplateProcessingException(
                    "Error formatting integer with minimum integer digits = " + minIntegerDigits, e);
        }
    }

    public String[] arrayFormatInteger(final Object[] target, final Integer minIntegerDigits) {
        Validate.notNull(target, "Target cannot be null");
        final String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = formatInteger((Number)target[i], minIntegerDigits);
        }
        return result;
    }

    public List<String> listFormatInteger(final List<? extends Number> target, final Integer minIntegerDigits) {
        Validate.notNull(target, "Target cannot be null");
        final List<String> result = new ArrayList<String>(target.size() + 2);
        for (final Number element : target) {
            result.add(formatInteger(element, minIntegerDigits));
        }
        return result;
    }

    public Set<String> setFormatInteger(final Set<? extends Number> target, final Integer minIntegerDigits) {
        Validate.notNull(target, "Target cannot be null");
        final Set<String> result = new LinkedHashSet<String>(target.size() + 2);
        for (final Number element : target) {
            result.add(formatInteger(element, minIntegerDigits));
        }
        return result;
    }

    
    
    
    public String formatInteger(final Number target, final Integer minIntegerDigits, final String thousandsPointType) {
        final NumberPointType thousandsNumberPointType = NumberPointType.match(thousandsPointType);
        if (thousandsNumberPointType == null) {
            throw new TemplateProcessingException(
                    "Unrecognized point format \"" + thousandsPointType + "\"");
        }
        try {
            return NumberUtils.format(target, minIntegerDigits, thousandsNumberPointType, this.locale);
        } catch (final Exception e) {
            throw new TemplateProcessingException(
                    "Error formatting integer with minimum integer digits = " + 
                    minIntegerDigits + " and thousands point type = " + thousandsPointType, e);
        }
    }

    public String[] arrayFormatInteger(final Object[] target, final Integer minIntegerDigits, final String thousandsPointType) {
        Validate.notNull(target, "Target cannot be null");
        final String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = formatInteger((Number)target[i], minIntegerDigits, thousandsPointType);
        }
        return result;
    }

    public List<String> listFormatInteger(final List<? extends Number> target, final Integer minIntegerDigits, final String thousandsPointType) {
        Validate.notNull(target, "Target cannot be null");
        final List<String> result = new ArrayList<String>(target.size() + 2);
        for (final Number element : target) {
            result.add(formatInteger(element, minIntegerDigits, thousandsPointType));
        }
        return result;
    }

    public Set<String> setFormatInteger(final Set<? extends Number> target, final Integer minIntegerDigits, final String thousandsPointType) {
        Validate.notNull(target, "Target cannot be null");
        final Set<String> result = new LinkedHashSet<String>(target.size() + 2);
        for (final Number element : target) {
            result.add(formatInteger(element, minIntegerDigits, thousandsPointType));
        }
        return result;
    }
    

    
    
    
    
    public String formatDecimal(final Number target, final Integer minIntegerDigits, final Integer decimalDigits) {
        try {
            return NumberUtils.format(target, minIntegerDigits, decimalDigits, this.locale);
        } catch (final Exception e) {
            throw new TemplateProcessingException(
                    "Error formatting decimal with minimum integer digits = " + minIntegerDigits + 
                    " and decimal digits " + decimalDigits, e);
        }
    }

    public String[] arrayFormatDecimal(final Object[] target, final Integer minIntegerDigits, final Integer decimalDigits) {
        Validate.notNull(target, "Target cannot be null");
        final String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = formatDecimal((Number)target[i], minIntegerDigits, decimalDigits);
        }
        return result;
    }

    public List<String> listFormatDecimal(final List<? extends Number> target, final Integer minIntegerDigits, final Integer decimalDigits) {
        Validate.notNull(target, "Target cannot be null");
        final List<String> result = new ArrayList<String>(target.size() + 2);
        for (final Number element : target) {
            result.add(formatDecimal(element, minIntegerDigits, decimalDigits));
        }
        return result;
    }

    public Set<String> setFormatDecimal(final Set<? extends Number> target, final Integer minIntegerDigits, final Integer decimalDigits) {
        Validate.notNull(target, "Target cannot be null");
        final Set<String> result = new LinkedHashSet<String>(target.size() + 2);
        for (final Number element : target) {
            result.add(formatDecimal(element, minIntegerDigits, decimalDigits));
        }
        return result;
    }

    
    

    
    
    
    public String formatDecimal(final Number target, final Integer minIntegerDigits, final Integer decimalDigits, final String decimalPointType) {
        final NumberPointType decimalNumberPointType = NumberPointType.match(decimalPointType);
        if (decimalNumberPointType == null) {
            throw new TemplateProcessingException(
                    "Unrecognized point format \"" + decimalPointType + "\"");
        }
        try {
            return NumberUtils.format(target, minIntegerDigits, decimalDigits, decimalNumberPointType, this.locale);
        } catch (final Exception e) {
            throw new TemplateProcessingException(
                    "Error formatting decimal with minimum integer digits = " + minIntegerDigits +
                    ", decimal digits = " + decimalDigits + " and decimal point type = " + decimalPointType, e);
        }
    }

    public String[] arrayFormatDecimal(final Object[] target, final Integer minIntegerDigits, final Integer decimalDigits, final String decimalPointType) {
        Validate.notNull(target, "Target cannot be null");
        final String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = formatDecimal((Number)target[i], minIntegerDigits, decimalDigits, decimalPointType);
        }
        return result;
    }

    public List<String> listFormatDecimal(final List<? extends Number> target, final Integer minIntegerDigits, final Integer decimalDigits, final String decimalPointType) {
        Validate.notNull(target, "Target cannot be null");
        final List<String> result = new ArrayList<String>(target.size() + 2);
        for (final Number element : target) {
            result.add(formatDecimal(element, minIntegerDigits, decimalDigits, decimalPointType));
        }
        return result;
    }

    public Set<String> setFormatDecimal(final Set<? extends Number> target, final Integer minIntegerDigits, final Integer decimalDigits, final String decimalPointType) {
        Validate.notNull(target, "Target cannot be null");
        final Set<String> result = new LinkedHashSet<String>(target.size() + 2);
        for (final Number element : target) {
            result.add(formatDecimal(element, minIntegerDigits, decimalDigits, decimalPointType));
        }
        return result;
    }

    
    

    
    
    
    public String formatDecimal(final Number target, final Integer minIntegerDigits, final String thousandsPointType, final Integer decimalDigits, final String decimalPointType) {
        final NumberPointType decimalNumberPointType = NumberPointType.match(decimalPointType);
        if (decimalNumberPointType == null) {
            throw new TemplateProcessingException(
                    "Unrecognized point format \"" + decimalPointType + "\"");
        }
        final NumberPointType thousandsNumberPointType = NumberPointType.match(thousandsPointType);
        if (thousandsNumberPointType == null) {
            throw new TemplateProcessingException(
                    "Unrecognized point format \"" + thousandsPointType + "\"");
        }
        try {
            return NumberUtils.format(target, minIntegerDigits, thousandsNumberPointType, decimalDigits, decimalNumberPointType, this.locale);
        } catch (final Exception e) {
            throw new TemplateProcessingException(
                    "Error formatting decimal with minimum integer digits = " + minIntegerDigits + 
                    ", thousands point type = " + thousandsPointType + ", decimal digits = " + decimalDigits +
                    " and decimal point type = " + decimalPointType, e);
        }
    }

    public String[] arrayFormatDecimal(final Object[] target, final Integer minIntegerDigits, final String thousandsPointType, final Integer decimalDigits, final String decimalPointType) {
        Validate.notNull(target, "Target cannot be null");
        final String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = formatDecimal((Number)target[i], minIntegerDigits, thousandsPointType, decimalDigits, decimalPointType);
        }
        return result;
    }

    public List<String> listFormatDecimal(final List<? extends Number> target, final Integer minIntegerDigits, final String thousandsPointType, final Integer decimalDigits, final String decimalPointType) {
        Validate.notNull(target, "Target cannot be null");
        final List<String> result = new ArrayList<String>(target.size() + 2);
        for (final Number element : target) {
            result.add(formatDecimal(element, minIntegerDigits, thousandsPointType, decimalDigits, decimalPointType));
        }
        return result;
    }

    public Set<String> setFormatDecimal(final Set<? extends Number> target, final Integer minIntegerDigits, final String thousandsPointType, final Integer decimalDigits, final String decimalPointType) {
        Validate.notNull(target, "Target cannot be null");
        final Set<String> result = new LinkedHashSet<String>(target.size() + 2);
        for (final Number element : target) {
            result.add(formatDecimal(element, minIntegerDigits, thousandsPointType, decimalDigits, decimalPointType));
        }
        return result;
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
    public Integer[] sequence(final Integer from, final Integer to) {
        return NumberUtils.sequence(from, to);
    }

    
    
    /**
     * <p>
     *   Produces an array with a sequence of integer numbers, using the 
     *   specified step.
     * </p>
     * 
     * @param from value to start the sequence from
     * @param to value to produce the sequence to
     * @param step the step to be used
     * @return the Integer[] sequence
     * 
     * @since 2.0.9
     */
    public Integer[] sequence(final Integer from, final Integer to, final Integer step) {
        return NumberUtils.sequence(from, to, step);
    }
        
    

    
    
    
    
}
