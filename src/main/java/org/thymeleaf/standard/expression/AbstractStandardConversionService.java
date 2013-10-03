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
public abstract class AbstractStandardConversionService implements IStandardConversionService {



    protected AbstractStandardConversionService() {
        super();
    }



    public final <T> T convert(final Object object, final Class<T> targetClass) {
        Validate.notNull(targetClass, "Target class cannot be null");
        if (targetClass.equals(String.class)) {
            return (T) convertToString(object);
        }
        if (targetClass.equals(Boolean.class)) {
            return (T) (Boolean) convertToBoolean(object);
        }
        if (targetClass.equals(boolean.class)) {
            return (T) (Boolean) convertToBoolean(object);
        }
        if (targetClass.equals(Number.class) || targetClass.equals(BigDecimal.class)) {
            return (T) convertToBigDecimal(object);
        }
        if (targetClass.equals(Iterable.class) || targetClass.equals(List.class)) {
            return (T) convertToList(object);
        }
        if (targetClass.equals(Object[].class)) {
            return (T) convertToArray(object);
        }
        return convertOther(object, targetClass);
    }



    protected String convertToString(final Object object) {
        return StandardConversionServiceUtil.convertToString(object);
    }


    protected boolean convertToBoolean(final Object object) {
        return StandardConversionServiceUtil.convertToBoolean(object);
    }


    protected BigDecimal convertToBigDecimal(final Object object) {
        return StandardConversionServiceUtil.convertToBigDecimal(object);
    }


    protected List<?> convertToList(final Object object) {
        return StandardConversionServiceUtil.convertToList(object);
    }


    protected Object[] convertToArray(final Object object) {
        return StandardConversionServiceUtil.convertToArray(object);
    }


    protected <T> T convertOther(final Object object, final Class<T> targetClass) {
        throw new IllegalArgumentException("No available conversion for target class \"" + targetClass.getName() + "\"");
    }


}
