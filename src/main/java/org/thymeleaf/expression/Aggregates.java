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

import java.math.BigDecimal;

import org.thymeleaf.util.AggregateUtils;


/**
 * <p>
 *   Expression Object for performing aggregation operations on numbers (collections or arrays)
 *   inside Thymeleaf Standard Expressions.
 * </p>
 * <p>
 *   An object of this class is usually available in variable evaluation expressions with the name
 *   {@code #aggregates}.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class Aggregates {
    
    

    /**
     * <p>
     *   Returns the sum of all the numbers contained in the provided 
     *   iterable (e.g. a collection).
     * </p>
     * <p>
     *   This method delegates on {@link AggregateUtils#sum(Iterable)}.
     * </p>
     * 
     * @param target the iterable containing the number objects
     * @return the sum, as a BigDecimal
     */
    public BigDecimal sum(final Iterable<? extends Number> target) {
        return AggregateUtils.sum(target);
    }
    

    /**
     * <p>
     *   Returns the sum of all the numbers contained in the provided array.
     * </p>
     * <p>
     *   This method delegates on {@link AggregateUtils#sum(Object[])}.
     * </p>
     * 
     * @param target the array of numbers
     * @return the sum, as a BigDecimal
     */
    public BigDecimal sum(final Number[] target) {
        return AggregateUtils.sum(target);
    }

    
    /**
     * <p>
     *   Returns the sum of all the numbers contained in the provided array.
     * </p>
     * <p>
     *   This method delegates on {@link AggregateUtils#sum(byte[])}.
     * </p>
     * 
     * @param target the array of numbers
     * @return the sum, as a BigDecimal
     */
    public BigDecimal sum(final byte[] target) {
        return AggregateUtils.sum(target);
    }
    
    
    /**
     * <p>
     *   Returns the sum of all the numbers contained in the provided array.
     * </p>
     * <p>
     *   This method delegates on {@link AggregateUtils#sum(short[])}.
     * </p>
     * 
     * @param target the array of numbers
     * @return the sum, as a BigDecimal
     */
    public BigDecimal sum(final short[] target) {
        return AggregateUtils.sum(target);
    }
    

    /**
     * <p>
     *   Returns the sum of all the numbers contained in the provided array.
     * </p>
     * <p>
     *   This method delegates on {@link AggregateUtils#sum(int[])}.
     * </p>
     * 
     * @param target the array of numbers
     * @return the sum, as a BigDecimal
     */
    public BigDecimal sum(final int[] target) {
        return AggregateUtils.sum(target);
    }
    
    
    /**
     * <p>
     *   Returns the sum of all the numbers contained in the provided array.
     * </p>
     * <p>
     *   This method delegates on {@link AggregateUtils#sum(long[])}.
     * </p>
     * 
     * @param target the array of numbers
     * @return the sum, as a BigDecimal
     */
    public BigDecimal sum(final long[] target) {
        return AggregateUtils.sum(target);
    }
    
    
    /**
     * <p>
     *   Returns the sum of all the numbers contained in the provided array.
     * </p>
     * <p>
     *   This method delegates on {@link AggregateUtils#sum(float[])}.
     * </p>
     * 
     * @param target the array of numbers
     * @return the sum, as a BigDecimal
     */
    public BigDecimal sum(final float[] target) {
        return AggregateUtils.sum(target);
    }

    
    /**
     * <p>
     *   Returns the sum of all the numbers contained in the provided array.
     * </p>
     * <p>
     *   This method delegates on {@link AggregateUtils#sum(double[])}.
     * </p>
     * 
     * @param target the array of numbers
     * @return the sum, as a BigDecimal
     */
    public BigDecimal sum(final double[] target) {
        return AggregateUtils.sum(target);
    }
    

    
    
    
    

    
    /**
     * <p>
     *   Returns the average of all the numbers contained in the provided 
     *   iterable (e.g. a collection).
     * </p>
     * <p>
     *   This method delegates on {@link AggregateUtils#avg(Iterable)}.
     * </p>
     * 
     * @param target the iterable containing the number objects
     * @return the average, as a BigDecimal
     */
    public BigDecimal avg(final Iterable<? extends Number> target) {
        return AggregateUtils.avg(target);
    }

    
    /**
     * <p>
     *   Returns the average of all the numbers contained in the provided array.
     * </p>
     * <p>
     *   This method delegates on {@link AggregateUtils#avg(Object[])}.
     * </p>
     * 
     * @param target the array of numbers
     * @return the average, as a BigDecimal
     */
    public BigDecimal avg(final Number[] target) {
        return AggregateUtils.avg(target);
    }

    
    /**
     * <p>
     *   Returns the average of all the numbers contained in the provided array.
     * </p>
     * <p>
     *   This method delegates on {@link AggregateUtils#avg(byte[])}.
     * </p>
     * 
     * @param target the array of numbers
     * @return the average, as a BigDecimal
     */
    public BigDecimal avg(final byte[] target) {
        return AggregateUtils.avg(target);
    }

    
    /**
     * <p>
     *   Returns the average of all the numbers contained in the provided array.
     * </p>
     * <p>
     *   This method delegates on {@link AggregateUtils#avg(short[])}.
     * </p>
     * 
     * @param target the array of numbers
     * @return the average, as a BigDecimal
     */
    public BigDecimal avg(final short[] target) {
        return AggregateUtils.avg(target);
    }
    
    
    /**
     * <p>
     *   Returns the average of all the numbers contained in the provided array.
     * </p>
     * <p>
     *   This method delegates on {@link AggregateUtils#avg(int[])}.
     * </p>
     * 
     * @param target the array of numbers
     * @return the average, as a BigDecimal
     */
    public BigDecimal avg(final int[] target) {
        return AggregateUtils.avg(target);
    }

    
    /**
     * <p>
     *   Returns the average of all the numbers contained in the provided array.
     * </p>
     * <p>
     *   This method delegates on {@link AggregateUtils#avg(long[])}.
     * </p>
     * 
     * @param target the array of numbers
     * @return the average, as a BigDecimal
     */
    public BigDecimal avg(final long[] target) {
        return AggregateUtils.avg(target);
    }
    
    
    /**
     * <p>
     *   Returns the average of all the numbers contained in the provided array.
     * </p>
     * <p>
     *   This method delegates on {@link AggregateUtils#avg(float[])}.
     * </p>
     * 
     * @param target the array of numbers
     * @return the average, as a BigDecimal
     */
    public BigDecimal avg(final float[] target) {
        return AggregateUtils.avg(target);
    }
    
    
    /**
     * <p>
     *   Returns the average of all the numbers contained in the provided array.
     * </p>
     * <p>
     *   This method delegates on {@link AggregateUtils#avg(double[])}.
     * </p>
     * 
     * @param target the array of numbers
     * @return the average, as a BigDecimal
     */
    public BigDecimal avg(final double[] target) {
        return AggregateUtils.avg(target);
    }

    
    /**
     * <p>
     *   Create an object of this class.
     * </p>
     * <p>
     *   Normally, this is only executed internally by the expression evaluation subsystems
     *   in dialects.
     * </p>
     */
    public Aggregates() {
        super();
    }
    
}
