/*
 * =============================================================================
 * 
 *   Copyright (c) 2011, The THYMELEAF team (http://www.thymeleaf.org)
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.thymeleaf.standard.expression.LiteralValue;

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

    
    
    public static boolean evaluateAsBoolean(final Object condition) {

        boolean result = true;
        if (condition == null) {
            result = false;
        } else {
            if (condition instanceof Boolean) {
                result = ((Boolean)condition).booleanValue();
            } else if (condition instanceof Number) {
                if (condition instanceof BigDecimal) {
                    result = !((BigDecimal)condition).equals(BigDecimal.ZERO);
                } else if (condition instanceof BigInteger) {
                    result = !((BigInteger)condition).equals(BigInteger.ZERO);
                } else {
                    result = ((Number)condition).doubleValue() != 0.0;
                }
            } else if (condition instanceof Character) {
                result = ((Character) condition).charValue() != 0;
            } else if (condition instanceof String) {
                final String condStr = ((String)condition).trim().toLowerCase();
                result = !(condStr.equals("false") || condStr.equals("off") || condStr.equals("no"));
            } else if (condition instanceof LiteralValue) {
                final String condStr = ((LiteralValue)condition).getValue().trim().toLowerCase();
                result = !(condStr.equals("false") || condStr.equals("off") || condStr.equals("no"));
            } else {
                result = true;
            }
        }
        return result;
        
    }

    

    
    public static BigDecimal evaluateAsNumber(final Object object) {
        
        if (object == null) {
            return null;
        }
        
        if (object instanceof Number) {
            if (object instanceof BigDecimal) {
                return (BigDecimal)object;
            } else if (object instanceof BigInteger) {
                return new BigDecimal((BigInteger)object);
            } else if (object instanceof Short) {
                return new BigDecimal(((Short)object).intValue());
            } else if (object instanceof Integer) {
                return new BigDecimal(((Integer)object).intValue());
            } else if (object instanceof Long) {
                return new BigDecimal(((Long)object).longValue());
            } else if (object instanceof Float) {
                return new BigDecimal(((Float)object).doubleValue());
            } else if (object instanceof Double) {
                return new BigDecimal(((Double)object).doubleValue());
            }
        } else if (object instanceof String) {
            try {
                return new BigDecimal(((String)object).trim());
            } catch (final NumberFormatException e) {
                return null;
            }
        }
        
        return null;
        
    }
    
    
    
    public static List<Object> convertToList(final Object value) {
        
        final List<Object> result = new ArrayList<Object>();
        if (value == null) {
            result.add(null);
            return result;
        }
        if (value instanceof Iterable<?>) {
            for (final Object obj : (Iterable<?>) value) {
                result.add(obj);
            }
        } else if (value instanceof Map<?,?>) {
            for (final Object obj : ((Map<?,?>) value).entrySet()) {
                result.add(obj);
            }
        } else if (value.getClass().isArray()){
            if (value instanceof byte[]) {
                for (final byte obj : (byte[]) value) {
                    result.add(Byte.valueOf(obj));
                }
            } else if (value instanceof short[]) {
                for (final short obj : (short[]) value) {
                    result.add(Short.valueOf(obj));
                }
            } else if (value instanceof int[]) {
                for (final int obj : (int[]) value) {
                    result.add(Integer.valueOf(obj));
                }
            } else if (value instanceof long[]) {
                for (final long obj : (long[]) value) {
                    result.add(Long.valueOf(obj));
                }
            } else if (value instanceof float[]) {
                for (final float obj : (float[]) value) {
                    result.add(Float.valueOf(obj));
                }
            } else if (value instanceof double[]) {
                for (final double obj : (double[]) value) {
                    result.add(Double.valueOf(obj));
                }
            } else if (value instanceof boolean[]) {
                for (final boolean obj : (boolean[]) value) {
                    result.add(Boolean.valueOf(obj));
                }
            } else if (value instanceof char[]) {
                for (final char obj : (char[]) value) {
                    result.add(Character.valueOf(obj));
                }
            } else {
                for (final Object obj : (Object[]) value) {
                    result.add(obj);
                }
            }
        } else{
            result.add(value);
        }
        
        return Collections.unmodifiableList(result);
        
    }
    

    
    
    public static Object[] convertToArray(final Object value) {
        
        final List<Object> result = new ArrayList<Object>();
        if (value == null) {
            return new Object[] { null };
        }
        if (value instanceof Iterable<?>) {
            for (final Object obj : (Iterable<?>) value) {
                result.add(obj);
            }
        } else if (value instanceof Map<?,?>) {
            for (final Object obj : ((Map<?,?>) value).entrySet()) {
                result.add(obj);
            }
        } else if (value.getClass().isArray()){
            return (Object[]) value;
        } else{
            result.add(value);
        }
        return result.toArray(new Object[result.size()]);
        
    }
    
    
    
    
    
    private ObjectUtils() {
        super();
    }
    
    
    
}
