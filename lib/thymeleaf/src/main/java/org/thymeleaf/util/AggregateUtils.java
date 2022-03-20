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
import java.math.BigInteger;
import java.math.RoundingMode;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class AggregateUtils {
    
    

    /**
     * <p>
     *   Returns the sum of all the numbers contained in the provided 
     *   iterable (e.g. a collection).
     * </p>
     * 
     * @param target the iterable containing the number objects
     * @return the sum, as a BigDecimal
     */
    public static BigDecimal sum(final Iterable<? extends Number> target) {
        Validate.notNull(target, "Cannot aggregate on null");
        Validate.containsNoNulls(target, "Cannot aggregate on iterable containing nulls");
        BigDecimal total = BigDecimal.ZERO;
        int size = 0;
        for (final Number element : target) {
            total = total.add(toBigDecimal(element));
            size++;
        }
        if (size == 0) {
            return null;
        }
        return total;
    }
    
    
    /**
     * <p>
     *   Returns the sum of all the numbers contained in the provided array.
     * </p>
     * 
     * @param target the array of numbers
     * @return the sum, as a BigDecimal
     */
    public static BigDecimal sum(final Object[] target) {
        Validate.notNull(target, "Cannot aggregate on null");
        Validate.containsNoNulls(target, "Cannot aggregate on array containing nulls");
        if (target.length == 0) {
            return null;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (final Object element : target) {
            total = total.add(toBigDecimal((Number)element));
        }
        return total;
    }

    
    /**
     * <p>
     *   Returns the sum of all the numbers contained in the provided array.
     * </p>
     * 
     * @param target the array of numbers
     * @return the sum, as a BigDecimal
     */
    public static BigDecimal sum(final byte[] target) {
        Validate.notNull(target, "Cannot aggregate on null");
        if (target.length == 0) {
            return null;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (final byte element : target) {
            total = total.add(toBigDecimal(element));
        }
        return total;
    }
    
    
    /**
     * <p>
     *   Returns the sum of all the numbers contained in the provided array.
     * </p>
     * 
     * @param target the array of numbers
     * @return the sum, as a BigDecimal
     */
    public static BigDecimal sum(final short[] target) {
        Validate.notNull(target, "Cannot aggregate on null");
        if (target.length == 0) {
            return null;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (final short element : target) {
            total = total.add(toBigDecimal(element));
        }
        return total;
    }
    
    
    /**
     * <p>
     *   Returns the sum of all the numbers contained in the provided array.
     * </p>
     * 
     * @param target the array of numbers
     * @return the sum, as a BigDecimal
     */
    public static BigDecimal sum(final int[] target) {
        Validate.notNull(target, "Cannot aggregate on null");
        if (target.length == 0) {
            return null;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (final int element : target) {
            total = total.add(toBigDecimal(element));
        }
        return total;
    }
    
    
    /**
     * <p>
     *   Returns the sum of all the numbers contained in the provided array.
     * </p>
     * 
     * @param target the array of numbers
     * @return the sum, as a BigDecimal
     */
    public static BigDecimal sum(final long[] target) {
        Validate.notNull(target, "Cannot aggregate on null");
        if (target.length == 0) {
            return null;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (final long element : target) {
            total = total.add(toBigDecimal(element));
        }
        return total;
    }
    
    
    /**
     * <p>
     *   Returns the sum of all the numbers contained in the provided array.
     * </p>
     * 
     * @param target the array of numbers
     * @return the sum, as a BigDecimal
     */
    public static BigDecimal sum(final float[] target) {
        Validate.notNull(target, "Cannot aggregate on null");
        if (target.length == 0) {
            return null;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (final float element : target) {
            total = total.add(toBigDecimal(element));
        }
        return total;
    }
    
    
    /**
     * <p>
     *   Returns the sum of all the numbers contained in the provided array.
     * </p>
     * 
     * @param target the array of numbers
     * @return the sum, as a BigDecimal
     */
    public static BigDecimal sum(final double[] target) {
        Validate.notNull(target, "Cannot aggregate on null");
        if (target.length == 0) {
            return null;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (final double element : target) {
            total = total.add(toBigDecimal(element));
        }
        return total;
    }
    

    
    
    
    

    
    /**
     * <p>
     *   Returns the average of all the numbers contained in the provided 
     *   iterable (e.g. a collection).
     * </p>
     * 
     * @param target the iterable containing the number objects
     * @return the average, as a BigDecimal
     */
    public static BigDecimal avg(final Iterable<? extends Number> target) {
        Validate.notNull(target, "Cannot aggregate on null");
        Validate.containsNoNulls(target, "Cannot aggregate on array containing nulls");
        BigDecimal total = BigDecimal.ZERO;
        int size = 0;
        for (final Number element : target) {
            total = total.add(toBigDecimal(element));
            size++;
        }
        if (size == 0) {
            return null;
        }
        final BigDecimal divisor = BigDecimal.valueOf(size);
        try {
            return total.divide(divisor);
        } catch (final ArithmeticException e) {
            // We will get an arithmetic exception if: 1. Divisor is zero, which is impossible; or 2. Division
            // returns a number with a non-terminating decimal expansion. In the latter case, we will set the
            // scale manually.
            return total.divide(divisor, Math.max(total.scale(), 10), RoundingMode.HALF_UP);
        }
    }
    
    
    /**
     * <p>
     *   Returns the average of all the numbers contained in the provided array.
     * </p>
     * 
     * @param target the array of numbers
     * @return the average, as a BigDecimal
     */
    public static BigDecimal avg(final Object[] target) {
        Validate.notNull(target, "Cannot aggregate on null");
        Validate.containsNoNulls(target, "Cannot aggregate on array containing nulls");
        if (target.length == 0) {
            return null;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (final Object element : target) {
            total = total.add(toBigDecimal((Number)element));
        }
        final BigDecimal divisor = BigDecimal.valueOf(target.length);
        try {
            return total.divide(divisor);
        } catch (final ArithmeticException e) {
            // We will get an arithmetic exception if: 1. Divisor is zero, which is impossible; or 2. Division
            // returns a number with a non-terminating decimal expansion. In the latter case, we will set the
            // scale manually.
            return total.divide(divisor, Math.max(total.scale(), 10), RoundingMode.HALF_UP);
        }
    }

    
    /**
     * <p>
     *   Returns the average of all the numbers contained in the provided array.
     * </p>
     * 
     * @param target the array of numbers
     * @return the average, as a BigDecimal
     */
    public static BigDecimal avg(final byte[] target) {
        Validate.notNull(target, "Cannot aggregate on null");
        if (target.length == 0) {
            return null;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (final byte element : target) {
            total = total.add(toBigDecimal(element));
        }
        final BigDecimal divisor = BigDecimal.valueOf(target.length);
        try {
            return total.divide(divisor);
        } catch (final ArithmeticException e) {
            // We will get an arithmetic exception if: 1. Divisor is zero, which is impossible; or 2. Division
            // returns a number with a non-terminating decimal expansion. In the latter case, we will set the
            // scale manually.
            return total.divide(divisor, Math.max(total.scale(), 10), RoundingMode.HALF_UP);
        }
    }
    
    
    /**
     * <p>
     *   Returns the average of all the numbers contained in the provided array.
     * </p>
     * 
     * @param target the array of numbers
     * @return the average, as a BigDecimal
     */
    public static BigDecimal avg(final short[] target) {
        Validate.notNull(target, "Cannot aggregate on null");
        if (target.length == 0) {
            return null;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (final short element : target) {
            total = total.add(toBigDecimal(element));
        }
        final BigDecimal divisor = BigDecimal.valueOf(target.length);
        try {
            return total.divide(divisor);
        } catch (final ArithmeticException e) {
            // We will get an arithmetic exception if: 1. Divisor is zero, which is impossible; or 2. Division
            // returns a number with a non-terminating decimal expansion. In the latter case, we will set the
            // scale manually.
            return total.divide(divisor, Math.max(total.scale(), 10), RoundingMode.HALF_UP);
        }
    }
    
    
    /**
     * <p>
     *   Returns the average of all the numbers contained in the provided array.
     * </p>
     * 
     * @param target the array of numbers
     * @return the average, as a BigDecimal
     */
    public static BigDecimal avg(final int[] target) {
        Validate.notNull(target, "Cannot aggregate on null");
        if (target.length == 0) {
            return null;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (final int element : target) {
            total = total.add(toBigDecimal(element));
        }
        final BigDecimal divisor = BigDecimal.valueOf(target.length);
        try {
            return total.divide(divisor);
        } catch (final ArithmeticException e) {
            // We will get an arithmetic exception if: 1. Divisor is zero, which is impossible; or 2. Division
            // returns a number with a non-terminating decimal expansion. In the latter case, we will set the
            // scale manually.
            return total.divide(divisor, Math.max(total.scale(), 10), RoundingMode.HALF_UP);
        }
    }
    
    
    /**
     * <p>
     *   Returns the average of all the numbers contained in the provided array.
     * </p>
     * 
     * @param target the array of numbers
     * @return the average, as a BigDecimal
     */
    public static BigDecimal avg(final long[] target) {
        Validate.notNull(target, "Cannot aggregate on null");
        if (target.length == 0) {
            return null;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (final long element : target) {
            total = total.add(toBigDecimal(element));
        }
        final BigDecimal divisor = BigDecimal.valueOf(target.length);
        try {
            return total.divide(divisor);
        } catch (final ArithmeticException e) {
            // We will get an arithmetic exception if: 1. Divisor is zero, which is impossible; or 2. Division
            // returns a number with a non-terminating decimal expansion. In the latter case, we will set the
            // scale manually.
            return total.divide(divisor, Math.max(total.scale(), 10), RoundingMode.HALF_UP);
        }
    }
    
    
    /**
     * <p>
     *   Returns the average of all the numbers contained in the provided array.
     * </p>
     * 
     * @param target the array of numbers
     * @return the average, as a BigDecimal
     */
    public static BigDecimal avg(final float[] target) {
        Validate.notNull(target, "Cannot aggregate on null");
        if (target.length == 0) {
            return null;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (final float element : target) {
            total = total.add(toBigDecimal(element));
        }
        final BigDecimal divisor = BigDecimal.valueOf(target.length);
        try {
            return total.divide(divisor);
        } catch (final ArithmeticException e) {
            // We will get an arithmetic exception if: 1. Divisor is zero, which is impossible; or 2. Division
            // returns a number with a non-terminating decimal expansion. In the latter case, we will set the
            // scale manually.
            return total.divide(divisor, Math.max(total.scale(), 10), RoundingMode.HALF_UP);
        }
    }
    
    
    /**
     * <p>
     *   Returns the average of all the numbers contained in the provided array.
     * </p>
     * 
     * @param target the array of numbers
     * @return the average, as a BigDecimal
     */
    public static BigDecimal avg(final double[] target) {
        Validate.notNull(target, "Cannot aggregate on null");
        if (target.length == 0) {
            return null;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (final double element : target) {
            total = total.add(toBigDecimal(element));
        }
        final BigDecimal divisor = BigDecimal.valueOf(target.length);
        try {
            return total.divide(divisor);
        } catch (final ArithmeticException e) {
            // We will get an arithmetic exception if: 1. Divisor is zero, which is impossible; or 2. Division
            // returns a number with a non-terminating decimal expansion. In the latter case, we will set the
            // scale manually.
            return total.divide(divisor, Math.max(total.scale(), 10), RoundingMode.HALF_UP);
        }
    }
    
    
    
    
    
    
    
    
    
    
    private static BigDecimal toBigDecimal(final Number number) {
        
        Validate.notNull(number, "Cannot convert null to BigDecimal");
        
        if (number instanceof BigDecimal) {
            return (BigDecimal) number;
        }
        
        if (number instanceof BigInteger) {
            return new BigDecimal((BigInteger)number);
        }
        
        if (number instanceof Byte ||
            number instanceof Short ||
            number instanceof Integer ||
            number instanceof Long) {
            return BigDecimal.valueOf(number.longValue());
        }
        return BigDecimal.valueOf(number.doubleValue());
        
    }
    
    private static BigDecimal toBigDecimal(final byte number) {
        return BigDecimal.valueOf(number);
    }
    
    private static BigDecimal toBigDecimal(final short number) {
        return BigDecimal.valueOf(number);
    }
    
    private static BigDecimal toBigDecimal(final int number) {
        return BigDecimal.valueOf(number);
    }
    
    private static BigDecimal toBigDecimal(final long number) {
        return BigDecimal.valueOf(number);
    }
    
    private static BigDecimal toBigDecimal(final float number) {
        return BigDecimal.valueOf(number);
    }
    
    private static BigDecimal toBigDecimal(final double number) {
        return BigDecimal.valueOf(number);
    }


    
    
    
    
    private AggregateUtils() {
        super();
    }
    
    
}
