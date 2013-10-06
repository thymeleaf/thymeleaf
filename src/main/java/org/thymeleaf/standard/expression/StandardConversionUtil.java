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

import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.Configuration;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.util.Validate;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.1.0
 *
 */
public final class StandardConversionUtil {


    private static final Map<Class<?>, Class<?>> OBJECT_TO_PRIMITIVE_CLASS = new HashMap<Class<?>, Class<?>>(9, 1.0f);
    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_OBJECT_CLASS = new HashMap<Class<?>, Class<?>>(9, 1.0f);



    static {

        OBJECT_TO_PRIMITIVE_CLASS.put(Byte.class, byte.class);
        OBJECT_TO_PRIMITIVE_CLASS.put(Short.class, short.class);
        OBJECT_TO_PRIMITIVE_CLASS.put(Integer.class, int.class);
        OBJECT_TO_PRIMITIVE_CLASS.put(Long.class, long.class);
        OBJECT_TO_PRIMITIVE_CLASS.put(Float.class, float.class);
        OBJECT_TO_PRIMITIVE_CLASS.put(Double.class, double.class);
        OBJECT_TO_PRIMITIVE_CLASS.put(Character.class, char.class);
        OBJECT_TO_PRIMITIVE_CLASS.put(Boolean.class, boolean.class);

        for (final Map.Entry<Class<?>, Class<?>> entry : OBJECT_TO_PRIMITIVE_CLASS.entrySet()) {
            PRIMITIVE_TO_OBJECT_CLASS.put(entry.getValue(), entry.getKey());
        }

    }




    public static <T> T convertIfNeeded(
            final Configuration configuration, final IProcessingContext processingContext,
            final Object object, final Class<T> targetClass) {

        Validate.notNull(configuration, "Configuration cannot be null");
        Validate.notNull(targetClass, "Target class cannot be null");

        if (object == null) {
            // Booleans are special, and a null should always be false!
            if (targetClass.equals(boolean.class) || targetClass.equals(Boolean.class)) {
                return (T) Boolean.FALSE;
            }
            return (T) object;
        }

        if (isAssignable(targetClass, object.getClass())) {
            return (T) object;
        }

        final IStandardConversionService conversionService = StandardExpressions.getConversionService(configuration);
        return conversionService.convert(configuration, processingContext, object, targetClass);

    }



    private static boolean isAssignable(final Class<?> targetClass, final Class<?> valueClass) {

        if (targetClass.isAssignableFrom(valueClass)) {
            return true;
        }

        if (targetClass.isPrimitive()) {
            final Class primitiveClass = OBJECT_TO_PRIMITIVE_CLASS.get(valueClass);
            return (primitiveClass != null && targetClass.equals(primitiveClass));
        } else {
            final Class objectClass = PRIMITIVE_TO_OBJECT_CLASS.get(valueClass);
            return (objectClass != null && targetClass.isAssignableFrom(objectClass));
        }

    }



    private StandardConversionUtil() {
        super();
    }
    
    

}
