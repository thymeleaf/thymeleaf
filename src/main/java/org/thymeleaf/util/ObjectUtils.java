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
package org.thymeleaf.util;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class ObjectUtils {
    
    
    
    public static <T> T nullSafe(final T target, final T defaultValue) {
        return (target != null? target : defaultValue);
    }


    /**
     * @deprecated since 2.1, you should use the
     *            {@link org.thymeleaf.standard.expression.IStandardConversionService} implementation
     *            corresponding to the dialect you are using or the methods in
     *            {@link EvaluationUtil}.
     *            Will be removed in 3.0.
     */
    @Deprecated
    public static boolean evaluateAsBoolean(final Object condition) {
        return EvaluationUtil.evaluateAsBoolean(condition);
    }




    /**
     * @deprecated since 2.1, you should use the
     *            {@link org.thymeleaf.standard.expression.IStandardConversionService} implementation
     *            corresponding to the dialect you are using or the methods in
     *            {@link EvaluationUtil}.
     *            Will be removed in 3.0.
     */
    @Deprecated
    public static BigDecimal evaluateAsNumber(final Object object) {
        return EvaluationUtil.evaluateAsNumber(object);
    }



    /**
     * @deprecated since 2.1, you should use the
     *            {@link org.thymeleaf.standard.expression.IStandardConversionService} implementation
     *            corresponding to the dialect you are using or the methods in
     *            {@link EvaluationUtil}.
     *            Will be removed in 3.0.
     */
    @Deprecated
    public static List<Object> convertToIterable(final Object value) {
        return EvaluationUtil.evaluateAsIterable(value);
    }



    /**
     * @deprecated since 2.1, you should use the
     *            {@link org.thymeleaf.standard.expression.IStandardConversionService} implementation
     *            corresponding to the dialect you are using or the methods in
     *            {@link EvaluationUtil}.
     *            Will be removed in 3.0.
     */
    @Deprecated
    public static List<Object> convertToList(final Object value) {
        if (value == null) {
            // This mimics the old behaviour of the deprecated convertToList() method, which is not the same
            // as the current EvaluationUtil.convertToList() method.
            return Collections.singletonList(null);
        }
        return EvaluationUtil.evaluateAsIterable(value);
    }




    /**
     * @deprecated since 2.1, you should use the
     *            {@link org.thymeleaf.standard.expression.IStandardConversionService} implementation
     *            corresponding to the dialect you are using or the methods in
     *            {@link EvaluationUtil}.
     *            Will be removed in 3.0.
     */
    @Deprecated
    public static Object[] convertToArray(final Object value) {
        return EvaluationUtil.evaluateAsArray(value);
    }
    
    
    
    
    
    private ObjectUtils() {
        super();
    }
    
    

    
}
