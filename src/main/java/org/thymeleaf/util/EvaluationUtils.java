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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.thymeleaf.standard.expression.LiteralValue;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0 (renamed from org.thymeleaf.util.EvaluationUtil)
 *
 */
public final class EvaluationUtils {




    public static boolean evaluateAsBoolean(final Object condition) {

        boolean result = true;
        if (condition == null) {
            result = false;
        } else {
            if (condition instanceof Boolean) {
                result = ((Boolean)condition).booleanValue();
            } else if (condition instanceof Number) {
                if (condition instanceof BigDecimal) {
                    result = (((BigDecimal) condition).compareTo(BigDecimal.ZERO) != 0);
                } else if (condition instanceof BigInteger) {
                    result = !condition.equals(BigInteger.ZERO);
                } else {
                    result = ((Number)condition).doubleValue() != 0.0;
                }
            } else if (condition instanceof Character) {
                result = ((Character) condition).charValue() != 0;
            } else if (condition instanceof String) {
                final String condStr = ((String)condition).trim().toLowerCase();
                result = !("false".equals(condStr) || "off".equals(condStr) || "no".equals(condStr));
            } else if (condition instanceof LiteralValue) {
                final String condStr = ((LiteralValue)condition).getValue().trim().toLowerCase();
                result = !("false".equals(condStr) || "off".equals(condStr) || "no".equals(condStr));
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
            } else if (object instanceof Byte) {
                return new BigDecimal(((Byte)object).intValue());
            } else if (object instanceof Short) {
                return new BigDecimal(((Short)object).intValue());
            } else if (object instanceof Integer) {
                return new BigDecimal(((Integer)object).intValue());
            } else if (object instanceof Long) {
                return new BigDecimal(((Long)object).longValue());
            } else if (object instanceof Float) {
                //noinspection UnpredictableBigDecimalConstructorCall
                return new BigDecimal(((Float)object).doubleValue());
            } else if (object instanceof Double) {
                //noinspection UnpredictableBigDecimalConstructorCall
                return new BigDecimal(((Double)object).doubleValue());
            }
        } else if (object instanceof String && ((String)object).length() > 0) {
            final char c0 = ((String)object).charAt(0);
            // This test will avoid trying to create the BigDecimal most of the times, which
            // will improve performance by avoiding lots of NumberFormatExceptions
            if ((c0 >= '0' && c0 <= '9') || c0 == '+' || c0 == '-') {
                try {
                    return new BigDecimal(((String)object).trim());
                } catch (final NumberFormatException ignored) {
                    return null;
                }
            }
        }

        return null;

    }



    public static List<Object> evaluateAsList(final Object value) {
        // Iterating on null should be the same as iterating an empty list
        // (for example, <c:forEach>)
        if (value == null) {
            return Collections.emptyList();
        }
        final List<Object> result = new ArrayList<Object>();
        if (value instanceof Iterable<?>) {
            for (final Object obj : (Iterable<?>) value) {
                result.add(obj);
            }
        } else if (value instanceof Map<?,?>) {
            for (final Map.Entry<Object,Object> obj : ((Map<Object,Object>) value).entrySet()) {
                // We should not directly use the Map.Entry<Object,Object> object used as an iteration
                // variable because some Map implementations like EnumMap reuse the same Map.Entry in their
                // iterator()'s, so we would be adding the same object to the list several times.
                result.add(new MapEntry<Object,Object>(obj.getKey(), obj.getValue()));
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
                final Object[] objValue = (Object[]) value;
                Collections.addAll(result, objValue);
            }
        } else{
            result.add(value);
        }

        return Collections.unmodifiableList(result);

    }




    public static Object[] evaluateAsArray(final Object value) {

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





    private EvaluationUtils() {
        super();
    }
    
    
    
    static final class MapEntry<K,V> implements Map.Entry<K,V> {

        private final K entryKey;
        private final V entryValue;
        
        MapEntry(final K key, final V value) {
            super();
            this.entryKey = key;
            this.entryValue = value;
        }
        
        public K getKey() {
            return this.entryKey;
        }

        public V getValue() {
            return this.entryValue;
        }

        public V setValue(final V value) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public String toString() {
            return this.entryKey + "=" + this.entryValue;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry mapEntry = (Map.Entry) o;
            if (this.entryKey != null ? !this.entryKey.equals(mapEntry.getKey()) : mapEntry.getKey() != null) {
                return false;
            }
            if (this.entryValue != null ? !this.entryValue.equals(mapEntry.getValue()) : mapEntry.getValue() != null) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int result = this.entryKey != null ? this.entryKey.hashCode() : 0;
            result = 31 * result + (this.entryValue != null ? this.entryValue.hashCode() : 0);
            return result;
        }

    }
    
    
}
