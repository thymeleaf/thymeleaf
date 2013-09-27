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
package org.thymeleaf.standard.expression;


import java.math.BigDecimal;
import java.util.List;

import org.thymeleaf.util.Validate;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.1.0
 *
 */
public class StandardConversionService implements IStandardConversionService {



    public StandardConversionService() {
        // Should only be instanced from StandardDialect
        super();
    }


    public <S, T> boolean canConvert(final Class<S> sourceClass, final Class<T> targetClass) {
        Validate.notNull(targetClass, "Target class cannot be null");
        if (targetClass.equals(String.class)) {
            return true;
        }
        if (targetClass.equals(Boolean.class) || targetClass.equals(boolean.class)) {
            return true;
        }
        if (targetClass.equals(Number.class) || targetClass.equals(BigDecimal.class)) {
            // Our to-number conversions only return BigDecimals, we cannot convert to any other
            // subclasses of java.lang.Number
            return true;
        }
        if (targetClass.equals(Iterable.class) || targetClass.equals(List.class)) {
            // Our to-iterable conversions only return List, we cannot convert to any other
            // implementations of java.lang.Iterable
            return true;
        }
        if (targetClass.equals(Object[].class)) {
            return true;
        }
        return false;
    }



    public <S, T> T convert(final S object, final Class<? super S> sourceClass, final Class<T> targetClass) {
        Validate.notNull(targetClass, "Target class cannot be null");
        if (targetClass.equals(String.class)) {
            return (T) StandardConversionServiceUtil.convertToString(object);
        }
        if (targetClass.equals(Boolean.class)) {
            return (T) Boolean.valueOf(StandardConversionServiceUtil.convertToBoolean(object));
        }
        if (targetClass.equals(boolean.class)) {
            return (T) (Boolean) StandardConversionServiceUtil.convertToBoolean(object);
        }
        if (targetClass.equals(Number.class) || targetClass.equals(BigDecimal.class)) {
            return (T) StandardConversionServiceUtil.convertToNumber(object);
        }
        if (targetClass.equals(Iterable.class) || targetClass.equals(List.class)) {
            return (T) StandardConversionServiceUtil.convertToIterable(object);
        }
        if (targetClass.equals(Object[].class)) {
            return (T) StandardConversionServiceUtil.convertToArray(object);
        }
        throw new IllegalArgumentException("No available conversion for target class \"" + targetClass.getName() + "\"");
    }

}
