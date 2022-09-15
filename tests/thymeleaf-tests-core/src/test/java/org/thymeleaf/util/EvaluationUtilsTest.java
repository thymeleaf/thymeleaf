/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thymeleaf.standard.expression.LiteralValue;


public final class EvaluationUtilsTest {



    @Test
    public void convertToBooleanTest() {

        Assertions.assertFalse(EvaluationUtils.evaluateAsBoolean(null));

        Assertions.assertTrue(EvaluationUtils.evaluateAsBoolean(Boolean.TRUE));
        Assertions.assertFalse(EvaluationUtils.evaluateAsBoolean(Boolean.FALSE));

        Assertions.assertFalse(EvaluationUtils.evaluateAsBoolean(BigDecimal.ZERO));
        Assertions.assertTrue(EvaluationUtils.evaluateAsBoolean(BigDecimal.ONE));
        Assertions.assertTrue(EvaluationUtils.evaluateAsBoolean(BigDecimal.TEN));

        Assertions.assertFalse(EvaluationUtils.evaluateAsBoolean(BigInteger.ZERO));
        Assertions.assertTrue(EvaluationUtils.evaluateAsBoolean(BigInteger.ONE));
        Assertions.assertTrue(EvaluationUtils.evaluateAsBoolean(BigInteger.TEN));

        Assertions.assertFalse(EvaluationUtils.evaluateAsBoolean(Double.valueOf(0.0d)));
        Assertions.assertFalse(EvaluationUtils.evaluateAsBoolean(Float.valueOf(0.0f)));
        Assertions.assertTrue(EvaluationUtils.evaluateAsBoolean(Double.valueOf(0.1d)));
        Assertions.assertTrue(EvaluationUtils.evaluateAsBoolean(Float.valueOf(0.1f)));
        Assertions.assertTrue(EvaluationUtils.evaluateAsBoolean(Double.valueOf(-0.1d)));
        Assertions.assertTrue(EvaluationUtils.evaluateAsBoolean(Float.valueOf(-0.1f)));
        Assertions.assertTrue(EvaluationUtils.evaluateAsBoolean(Double.valueOf(Double.MAX_VALUE)));
        Assertions.assertTrue(EvaluationUtils.evaluateAsBoolean(Float.valueOf(Float.MAX_VALUE)));
        Assertions.assertTrue(EvaluationUtils.evaluateAsBoolean(Double.valueOf(Double.MIN_VALUE)));
        Assertions.assertTrue(EvaluationUtils.evaluateAsBoolean(Float.valueOf(Float.MIN_VALUE)));

        Assertions.assertFalse(EvaluationUtils.evaluateAsBoolean(Character.valueOf((char) 0)));
        Assertions.assertTrue(EvaluationUtils.evaluateAsBoolean(Character.valueOf('x')));
        Assertions.assertTrue(EvaluationUtils.evaluateAsBoolean(Character.valueOf('0')));
        Assertions.assertTrue(EvaluationUtils.evaluateAsBoolean(Character.valueOf('1')));

        Assertions.assertTrue(EvaluationUtils.evaluateAsBoolean("true"));
        Assertions.assertFalse(EvaluationUtils.evaluateAsBoolean("false"));
        Assertions.assertTrue(EvaluationUtils.evaluateAsBoolean("yes"));
        Assertions.assertFalse(EvaluationUtils.evaluateAsBoolean("no"));
        Assertions.assertTrue(EvaluationUtils.evaluateAsBoolean("on"));
        Assertions.assertFalse(EvaluationUtils.evaluateAsBoolean("off"));
        Assertions.assertTrue(EvaluationUtils.evaluateAsBoolean("sky"));
        Assertions.assertTrue(EvaluationUtils.evaluateAsBoolean("high above"));

        Assertions.assertTrue(EvaluationUtils.evaluateAsBoolean(new LiteralValue("true")));
        Assertions.assertFalse(EvaluationUtils.evaluateAsBoolean(new LiteralValue("false")));
        Assertions.assertTrue(EvaluationUtils.evaluateAsBoolean(new LiteralValue("yes")));
        Assertions.assertFalse(EvaluationUtils.evaluateAsBoolean(new LiteralValue("no")));
        Assertions.assertTrue(EvaluationUtils.evaluateAsBoolean(new LiteralValue("on")));
        Assertions.assertFalse(EvaluationUtils.evaluateAsBoolean(new LiteralValue("off")));
        Assertions.assertTrue(EvaluationUtils.evaluateAsBoolean(new LiteralValue("sky")));
        Assertions.assertTrue(EvaluationUtils.evaluateAsBoolean(new LiteralValue("high above")));

        Assertions.assertTrue(EvaluationUtils.evaluateAsBoolean(EvaluationUtils.class));

    }



    @Test
    public void convertToNumberTest() {

        Assertions.assertEquals(null, EvaluationUtils.evaluateAsNumber(null));

        Assertions.assertEquals(
                BigDecimal.valueOf(23.0f),
                EvaluationUtils.evaluateAsNumber(BigDecimal.valueOf(23.0f)));
        Assertions.assertEquals(
                BigDecimal.valueOf(23.89754f),
                EvaluationUtils.evaluateAsNumber(BigDecimal.valueOf(23.89754f)));
        Assertions.assertEquals(
                BigDecimal.valueOf(23.89754d),
                EvaluationUtils.evaluateAsNumber(BigDecimal.valueOf(23.89754d)));
        Assertions.assertEquals(
                BigDecimal.ZERO,
                EvaluationUtils.evaluateAsNumber(BigDecimal.valueOf(0)));
        Assertions.assertEquals(
                BigDecimal.valueOf(0.0d),
                EvaluationUtils.evaluateAsNumber(BigDecimal.valueOf(0.0d)));
        Assertions.assertNotEquals(
                BigDecimal.valueOf(23.1f),
                EvaluationUtils.evaluateAsNumber(BigDecimal.valueOf(23.0f)));

        Assertions.assertEquals(
                BigDecimal.valueOf(23),
                EvaluationUtils.evaluateAsNumber(BigInteger.valueOf(23)));
        Assertions.assertEquals(
                BigDecimal.valueOf(0),
                EvaluationUtils.evaluateAsNumber(BigInteger.valueOf(0)));
        Assertions.assertEquals(
                BigDecimal.valueOf(-2323232),
                EvaluationUtils.evaluateAsNumber(BigInteger.valueOf(-2323232)));

        Assertions.assertEquals(
                BigDecimal.valueOf(-232),
                EvaluationUtils.evaluateAsNumber(Short.valueOf("-232")));
        Assertions.assertEquals(
                BigDecimal.valueOf(232),
                EvaluationUtils.evaluateAsNumber(Short.valueOf("232")));
        Assertions.assertEquals(
                BigDecimal.valueOf(0),
                EvaluationUtils.evaluateAsNumber(Short.valueOf("0")));

        Assertions.assertEquals(
                BigDecimal.valueOf(-232232),
                EvaluationUtils.evaluateAsNumber(Integer.valueOf("-232232")));
        Assertions.assertEquals(
                BigDecimal.valueOf(232232),
                EvaluationUtils.evaluateAsNumber(Integer.valueOf("232232")));
        Assertions.assertEquals(
                BigDecimal.valueOf(0),
                EvaluationUtils.evaluateAsNumber(Integer.valueOf("0")));

        Assertions.assertEquals(
                BigDecimal.valueOf(-23223212121L),
                EvaluationUtils.evaluateAsNumber(Long.valueOf("-23223212121")));
        Assertions.assertEquals(
                BigDecimal.valueOf(23223212121L),
                EvaluationUtils.evaluateAsNumber(Long.valueOf("23223212121")));
        Assertions.assertEquals(
                BigDecimal.valueOf(0),
                EvaluationUtils.evaluateAsNumber(Long.valueOf("0")));

        Assertions.assertTrue(
                BigDecimal.valueOf(23.0f).compareTo(
                    EvaluationUtils.evaluateAsNumber(Float.valueOf(23.0f))) == 0);
        {
            BigDecimal num = BigDecimal.valueOf(23.89754f);
            num = num.setScale(5, RoundingMode.HALF_UP);
            BigDecimal result = EvaluationUtils.evaluateAsNumber(Float.valueOf(23.89754f));
            result = result.setScale(5, RoundingMode.HALF_UP);
            Assertions.assertTrue(num.compareTo(result) == 0);
        }
        Assertions.assertTrue(
                BigDecimal.ZERO.compareTo(
                    EvaluationUtils.evaluateAsNumber(Float.valueOf(0))) == 0);
        Assertions.assertTrue(
                BigDecimal.valueOf(0.0d).compareTo(
                    EvaluationUtils.evaluateAsNumber(Float.valueOf(0.0f))) == 0);
        {
            BigDecimal num = BigDecimal.valueOf(23.1f);
            num = num.setScale(1, RoundingMode.HALF_UP);
            BigDecimal result = EvaluationUtils.evaluateAsNumber(Float.valueOf(23.0f));
            result = result.setScale(1, RoundingMode.HALF_UP);
            Assertions.assertTrue(num.compareTo(result) != 0);
        }

        Assertions.assertTrue(
                BigDecimal.valueOf(23.0d).compareTo(
                        EvaluationUtils.evaluateAsNumber(Double.valueOf(23.0d))) == 0);
        {
            BigDecimal num = BigDecimal.valueOf(23.89754d);
            num = num.setScale(5, RoundingMode.HALF_UP);
            BigDecimal result = EvaluationUtils.evaluateAsNumber(Double.valueOf(23.89754d));
            result = result.setScale(5, RoundingMode.HALF_UP);
            Assertions.assertTrue(num.compareTo(result) == 0);
        }
        Assertions.assertTrue(
                BigDecimal.ZERO.compareTo(
                        EvaluationUtils.evaluateAsNumber(Double.valueOf(0))) == 0);
        Assertions.assertTrue(
                BigDecimal.valueOf(0.0d).compareTo(
                        EvaluationUtils.evaluateAsNumber(Double.valueOf(0.0d))) == 0);
        {
            BigDecimal num = BigDecimal.valueOf(23.1d);
            num = num.setScale(1, RoundingMode.HALF_UP);
            BigDecimal result = EvaluationUtils.evaluateAsNumber(Double.valueOf(23.0d));
            result = result.setScale(1, RoundingMode.HALF_UP);
            Assertions.assertTrue(num.compareTo(result) != 0);
        }

        Assertions.assertTrue(
                BigDecimal.valueOf(23.0d).compareTo(
                        EvaluationUtils.evaluateAsNumber("23.0")) == 0);
        {
            BigDecimal num = BigDecimal.valueOf(23.89754d);
            num = num.setScale(5, RoundingMode.HALF_UP);
            BigDecimal result = EvaluationUtils.evaluateAsNumber("23.89754");
            result = result.setScale(5, RoundingMode.HALF_UP);
            Assertions.assertTrue(num.compareTo(result) == 0);
        }
        Assertions.assertTrue(
                BigDecimal.ZERO.compareTo(
                        EvaluationUtils.evaluateAsNumber("0")) == 0);
        Assertions.assertTrue(
                BigDecimal.valueOf(0.0d).compareTo(
                        EvaluationUtils.evaluateAsNumber("0.0")) == 0);
        {
            BigDecimal num = BigDecimal.valueOf(23.1d);
            num = num.setScale(1, RoundingMode.HALF_UP);
            BigDecimal result = EvaluationUtils.evaluateAsNumber("23.0");
            result = result.setScale(1, RoundingMode.HALF_UP);
            Assertions.assertTrue(num.compareTo(result) != 0);
        }

        Assertions.assertNull(EvaluationUtils.evaluateAsNumber("something"));
        Assertions.assertNull(EvaluationUtils.evaluateAsNumber(EvaluationUtils.class));

    }





    @Test
    public void convertToListTest() {

        {
            final List<Object> result = EvaluationUtils.evaluateAsList(null);
            Assertions.assertTrue(result != null && result.size() == 0);
        }

        {
            final Set<Object> set = new LinkedHashSet<Object>();
            set.add(Integer.valueOf(2));
            set.add(Integer.valueOf(43));
            final List<Object> list = new ArrayList<Object>();
            list.add(Integer.valueOf(2));
            list.add(Integer.valueOf(43));

            final List<Object> result = EvaluationUtils.evaluateAsList(set);
            Assertions.assertTrue(result != null && result instanceof List && list.equals(result));
        }

        {
            final Map<Object,Object> map = new LinkedHashMap<Object,Object>();
            map.put("a", Integer.valueOf(2));
            map.put("b", Integer.valueOf(43));
            final List<Object> list = new ArrayList<Object>();
            list.add(new EvaluationUtils.MapEntry<Object,Object>("a", Integer.valueOf(2)));
            list.add(new EvaluationUtils.MapEntry<Object,Object>("b", Integer.valueOf(43)));

            final List<Object> result = EvaluationUtils.evaluateAsList(map);
            Assertions.assertTrue(result != null && result instanceof List && list.equals(result));
        }

        {
            final byte[] arr0 = new byte[0];
            final List<Object> list0 = new ArrayList<Object>();
            final List<Object> result0 = EvaluationUtils.evaluateAsList(arr0);
            Assertions.assertTrue(result0 != null && result0 instanceof List && list0.equals(result0));


            final byte[] arr = new byte[2];
            arr[0] = (byte)23;
            arr[1] = (byte)-127;
            final List<Object> list = new ArrayList<Object>();
            list.add(Byte.valueOf((byte)23));
            list.add(Byte.valueOf((byte)-127));

            final List<Object> result = EvaluationUtils.evaluateAsList(arr);
            Assertions.assertTrue(result != null && result instanceof List && list.equals(result));
        }

        {
            final short[] arr0 = new short[0];
            final List<Object> list0 = new ArrayList<Object>();
            final List<Object> result0 = EvaluationUtils.evaluateAsList(arr0);
            Assertions.assertTrue(result0 != null && result0 instanceof List && list0.equals(result0));


            final short[] arr = new short[2];
            arr[0] = (short)23;
            arr[1] = (short)-127;
            final List<Object> list = new ArrayList<Object>();
            list.add(Short.valueOf((short)23));
            list.add(Short.valueOf((short)-127));

            final List<Object> result = EvaluationUtils.evaluateAsList(arr);
            Assertions.assertTrue(result != null && result instanceof List && list.equals(result));
        }

        {
            final int[] arr0 = new int[0];
            final List<Object> list0 = new ArrayList<Object>();
            final List<Object> result0 = EvaluationUtils.evaluateAsList(arr0);
            Assertions.assertTrue(result0 != null && result0 instanceof List && list0.equals(result0));


            final int[] arr = new int[2];
            arr[0] = 23;
            arr[1] = -127;
            final List<Object> list = new ArrayList<Object>();
            list.add(Integer.valueOf(23));
            list.add(Integer.valueOf(-127));

            final List<Object> result = EvaluationUtils.evaluateAsList(arr);
            Assertions.assertTrue(result != null && result instanceof List && list.equals(result));
        }

        {
            final long[] arr0 = new long[0];
            final List<Object> list0 = new ArrayList<Object>();
            final List<Object> result0 = EvaluationUtils.evaluateAsList(arr0);
            Assertions.assertTrue(result0 != null && result0 instanceof List && list0.equals(result0));


            final long[] arr = new long[2];
            arr[0] = 23L;
            arr[1] = -127L;
            final List<Object> list = new ArrayList<Object>();
            list.add(Long.valueOf(23L));
            list.add(Long.valueOf(-127L));

            final List<Object> result = EvaluationUtils.evaluateAsList(arr);
            Assertions.assertTrue(result != null && result instanceof List && list.equals(result));
        }

        {
            final float[] arr0 = new float[0];
            final List<Object> list0 = new ArrayList<Object>();
            final List<Object> result0 = EvaluationUtils.evaluateAsList(arr0);
            Assertions.assertTrue(result0 != null && result0 instanceof List && list0.equals(result0));


            final float[] arr = new float[2];
            arr[0] = 23.0f;
            arr[1] = -127.1f;
            final List<Object> list = new ArrayList<Object>();
            list.add(Float.valueOf(23.0f));
            list.add(Float.valueOf(-127.1f));

            final List<Object> result = EvaluationUtils.evaluateAsList(arr);
            Assertions.assertTrue(result != null && result instanceof List && result.size() == list.size());
            for (int i = 0; i < result.size(); i++) {
                Assertions.assertTrue(result.get(i) != null && result.get(i) instanceof Float &&
                        (((Float)result.get(i)).compareTo((Float)list.get(i)) == 0));
            }
        }

        {
            final double[] arr0 = new double[0];
            final List<Object> list0 = new ArrayList<Object>();
            final List<Object> result0 = EvaluationUtils.evaluateAsList(arr0);
            Assertions.assertTrue(result0 != null && result0 instanceof List && list0.equals(result0));


            final double[] arr = new double[2];
            arr[0] = 23.0d;
            arr[1] = -127.1d;
            final List<Object> list = new ArrayList<Object>();
            list.add(Double.valueOf(23.0d));
            list.add(Double.valueOf(-127.1d));

            final List<Object> result = EvaluationUtils.evaluateAsList(arr);
            Assertions.assertTrue(result != null && result instanceof List && result.size() == list.size());
            for (int i = 0; i < result.size(); i++) {
                Assertions.assertTrue(result.get(i) != null && result.get(i) instanceof Double &&
                        (((Double)result.get(i)).compareTo((Double)list.get(i)) == 0));
            }
        }

        {
            final boolean[] arr0 = new boolean[0];
            final List<Object> list0 = new ArrayList<Object>();
            final List<Object> result0 = EvaluationUtils.evaluateAsList(arr0);
            Assertions.assertTrue(result0 != null && result0 instanceof List && list0.equals(result0));


            final boolean[] arr = new boolean[2];
            arr[0] = true;
            arr[1] = false;
            final List<Object> list = new ArrayList<Object>();
            list.add(Boolean.TRUE);
            list.add(Boolean.FALSE);

            final List<Object> result = EvaluationUtils.evaluateAsList(arr);
            Assertions.assertTrue(result != null && result instanceof List && list.equals(result));
        }

        {
            final char[] arr0 = new char[0];
            final List<Object> list0 = new ArrayList<Object>();
            final List<Object> result0 = EvaluationUtils.evaluateAsList(arr0);
            Assertions.assertTrue(result0 != null && result0 instanceof List && list0.equals(result0));


            final char[] arr = new char[3];
            arr[0] = 'a';
            arr[1] = 'x';
            arr[2] = (char)0;
            final List<Object> list = new ArrayList<Object>();
            list.add(Character.valueOf('a'));
            list.add(Character.valueOf('x'));
            list.add(Character.valueOf((char)0));

            final List<Object> result = EvaluationUtils.evaluateAsList(arr);
            Assertions.assertTrue(result != null && result instanceof List && list.equals(result));
        }

        {
            final Class<?>[] arr0 = new Class<?>[0];
            final List<Object> list0 = new ArrayList<Object>();
            final List<Object> result0 = EvaluationUtils.evaluateAsList(arr0);
            Assertions.assertTrue(result0 != null && result0 instanceof List && list0.equals(result0));


            final Class<?>[] arr = new Class<?>[2];
            arr[0] = EvaluationUtils.class;
            arr[1] = EvaluationUtilsTest.class;
            final List<Object> list = new ArrayList<Object>();
            list.add(EvaluationUtils.class);
            list.add(EvaluationUtilsTest.class);

            final List<Object> result = EvaluationUtils.evaluateAsList(arr);
            Assertions.assertTrue(result != null && result instanceof List && list.equals(result));
        }

        {
            final List<Object> list = new ArrayList<Object>();
            list.add(EvaluationUtils.class);

            final List<Object> result = EvaluationUtils.evaluateAsList(EvaluationUtils.class);
            Assertions.assertTrue(result != null && result instanceof List && list.equals(result));
        }

    }







    @Test
    public void convertToArrayTest() {

        {
            final Object[] result = EvaluationUtils.evaluateAsArray(null);
            Assertions.assertTrue(result != null && result.length == 1 && result[0] == null);
        }

        {
            final Set<Object> set = new LinkedHashSet<Object>();
            set.add(Integer.valueOf(2));
            set.add(Integer.valueOf(43));
            final Object[] array = new Object[2];
            array[0] = Integer.valueOf(2);
            array[1] = Integer.valueOf(43);

            final Object[] result = EvaluationUtils.evaluateAsArray(set);
            Assertions.assertTrue(result != null && Objects.deepEquals(array, result));
        }

        {
            final Map<Object,Object> map = new LinkedHashMap<Object,Object>();
            map.put("a", Integer.valueOf(2));
            map.put("b", Integer.valueOf(43));
            final Object[] array = new Object[2];
            array[0] = new EvaluationUtils.MapEntry<Object,Object>("a", Integer.valueOf(2));
            array[1] = new EvaluationUtils.MapEntry<Object,Object>("b", Integer.valueOf(43));

            final Object[] result = EvaluationUtils.evaluateAsArray(map);
            Assertions.assertTrue(result != null && Objects.deepEquals(array, result));
        }

        {
            final Map<Object,Object> map = new LinkedHashMap<Object,Object>();
            map.put("a", Integer.valueOf(2));
            map.put("b", Integer.valueOf(43));
            final Object[] array = new Object[2];
            array[0] = new EvaluationUtils.MapEntry<Object,Object>("a", Integer.valueOf(2));
            array[1] = new EvaluationUtils.MapEntry<Object,Object>("b", Integer.valueOf(43));

            final Object[] result = EvaluationUtils.evaluateAsArray(map);
            Assertions.assertTrue(result != null && Objects.deepEquals(array, result));
        }

        {
            final Boolean[] arr0 = new Boolean[0];
            final Boolean[] array0 = new Boolean[0];
            final Object[] result0 = EvaluationUtils.evaluateAsArray(arr0);
            Assertions.assertTrue(result0 != null && Objects.deepEquals(array0, result0));


            final Boolean[] arr = new Boolean[2];
            arr[0] = Boolean.TRUE;
            arr[1] = Boolean.FALSE;
            final Boolean[] array = new Boolean[2];
            array[0] = Boolean.TRUE;
            array[1] = Boolean.FALSE;

            final Object[] result = EvaluationUtils.evaluateAsArray(arr);
            Assertions.assertTrue(result != null && Objects.deepEquals(array, result));
        }

        {
            final Object[] arr = new Object[1];
            arr[0] = EvaluationUtils.class;

            final Object[] result = EvaluationUtils.evaluateAsArray(EvaluationUtils.class);
            Assertions.assertTrue(result != null && result.length == 1 && result[0] == EvaluationUtils.class);
        }

    }



    
}
