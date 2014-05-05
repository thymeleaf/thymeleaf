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
package org.thymeleaf.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;
import org.thymeleaf.standard.expression.LiteralValue;


public final class EvaluationUtilTest {



    @Test
    public void convertToBooleanTest() {

        Assert.assertFalse(EvaluationUtil.evaluateAsBoolean(null));

        Assert.assertTrue(EvaluationUtil.evaluateAsBoolean(Boolean.TRUE));
        Assert.assertFalse(EvaluationUtil.evaluateAsBoolean(Boolean.FALSE));

        Assert.assertFalse(EvaluationUtil.evaluateAsBoolean(BigDecimal.ZERO));
        Assert.assertTrue(EvaluationUtil.evaluateAsBoolean(BigDecimal.ONE));
        Assert.assertTrue(EvaluationUtil.evaluateAsBoolean(BigDecimal.TEN));

        Assert.assertFalse(EvaluationUtil.evaluateAsBoolean(BigInteger.ZERO));
        Assert.assertTrue(EvaluationUtil.evaluateAsBoolean(BigInteger.ONE));
        Assert.assertTrue(EvaluationUtil.evaluateAsBoolean(BigInteger.TEN));

        Assert.assertFalse(EvaluationUtil.evaluateAsBoolean(Double.valueOf(0.0d)));
        Assert.assertFalse(EvaluationUtil.evaluateAsBoolean(Float.valueOf(0.0f)));
        Assert.assertTrue(EvaluationUtil.evaluateAsBoolean(Double.valueOf(0.1d)));
        Assert.assertTrue(EvaluationUtil.evaluateAsBoolean(Float.valueOf(0.1f)));
        Assert.assertTrue(EvaluationUtil.evaluateAsBoolean(Double.valueOf(-0.1d)));
        Assert.assertTrue(EvaluationUtil.evaluateAsBoolean(Float.valueOf(-0.1f)));
        Assert.assertTrue(EvaluationUtil.evaluateAsBoolean(Double.valueOf(Double.MAX_VALUE)));
        Assert.assertTrue(EvaluationUtil.evaluateAsBoolean(Float.valueOf(Float.MAX_VALUE)));
        Assert.assertTrue(EvaluationUtil.evaluateAsBoolean(Double.valueOf(Double.MIN_VALUE)));
        Assert.assertTrue(EvaluationUtil.evaluateAsBoolean(Float.valueOf(Float.MIN_VALUE)));

        Assert.assertFalse(EvaluationUtil.evaluateAsBoolean(Character.valueOf((char) 0)));
        Assert.assertTrue(EvaluationUtil.evaluateAsBoolean(Character.valueOf('x')));
        Assert.assertTrue(EvaluationUtil.evaluateAsBoolean(Character.valueOf('0')));
        Assert.assertTrue(EvaluationUtil.evaluateAsBoolean(Character.valueOf('1')));

        Assert.assertTrue(EvaluationUtil.evaluateAsBoolean("true"));
        Assert.assertFalse(EvaluationUtil.evaluateAsBoolean("false"));
        Assert.assertTrue(EvaluationUtil.evaluateAsBoolean("yes"));
        Assert.assertFalse(EvaluationUtil.evaluateAsBoolean("no"));
        Assert.assertTrue(EvaluationUtil.evaluateAsBoolean("on"));
        Assert.assertFalse(EvaluationUtil.evaluateAsBoolean("off"));
        Assert.assertTrue(EvaluationUtil.evaluateAsBoolean("sky"));
        Assert.assertTrue(EvaluationUtil.evaluateAsBoolean("high above"));

        Assert.assertTrue(EvaluationUtil.evaluateAsBoolean(new LiteralValue("true")));
        Assert.assertFalse(EvaluationUtil.evaluateAsBoolean(new LiteralValue("false")));
        Assert.assertTrue(EvaluationUtil.evaluateAsBoolean(new LiteralValue("yes")));
        Assert.assertFalse(EvaluationUtil.evaluateAsBoolean(new LiteralValue("no")));
        Assert.assertTrue(EvaluationUtil.evaluateAsBoolean(new LiteralValue("on")));
        Assert.assertFalse(EvaluationUtil.evaluateAsBoolean(new LiteralValue("off")));
        Assert.assertTrue(EvaluationUtil.evaluateAsBoolean(new LiteralValue("sky")));
        Assert.assertTrue(EvaluationUtil.evaluateAsBoolean(new LiteralValue("high above")));

        Assert.assertTrue(EvaluationUtil.evaluateAsBoolean(EvaluationUtil.class));

    }



    @Test
    public void convertToNumberTest() {

        Assert.assertEquals(null, EvaluationUtil.evaluateAsNumber(null));

        Assert.assertEquals(
                BigDecimal.valueOf(23.0f),
                EvaluationUtil.evaluateAsNumber(BigDecimal.valueOf(23.0f)));
        Assert.assertEquals(
                BigDecimal.valueOf(23.89754f),
                EvaluationUtil.evaluateAsNumber(BigDecimal.valueOf(23.89754f)));
        Assert.assertEquals(
                BigDecimal.valueOf(23.89754d),
                EvaluationUtil.evaluateAsNumber(BigDecimal.valueOf(23.89754d)));
        Assert.assertEquals(
                BigDecimal.ZERO,
                EvaluationUtil.evaluateAsNumber(BigDecimal.valueOf(0)));
        Assert.assertEquals(
                BigDecimal.valueOf(0.0d),
                EvaluationUtil.evaluateAsNumber(BigDecimal.valueOf(0.0d)));
        Assert.assertNotEquals(
                BigDecimal.valueOf(23.1f),
                EvaluationUtil.evaluateAsNumber(BigDecimal.valueOf(23.0f)));

        Assert.assertEquals(
                BigDecimal.valueOf(23),
                EvaluationUtil.evaluateAsNumber(BigInteger.valueOf(23)));
        Assert.assertEquals(
                BigDecimal.valueOf(0),
                EvaluationUtil.evaluateAsNumber(BigInteger.valueOf(0)));
        Assert.assertEquals(
                BigDecimal.valueOf(-2323232),
                EvaluationUtil.evaluateAsNumber(BigInteger.valueOf(-2323232)));

        Assert.assertEquals(
                BigDecimal.valueOf(-232),
                EvaluationUtil.evaluateAsNumber(Short.valueOf("-232")));
        Assert.assertEquals(
                BigDecimal.valueOf(232),
                EvaluationUtil.evaluateAsNumber(Short.valueOf("232")));
        Assert.assertEquals(
                BigDecimal.valueOf(0),
                EvaluationUtil.evaluateAsNumber(Short.valueOf("0")));

        Assert.assertEquals(
                BigDecimal.valueOf(-232232),
                EvaluationUtil.evaluateAsNumber(Integer.valueOf("-232232")));
        Assert.assertEquals(
                BigDecimal.valueOf(232232),
                EvaluationUtil.evaluateAsNumber(Integer.valueOf("232232")));
        Assert.assertEquals(
                BigDecimal.valueOf(0),
                EvaluationUtil.evaluateAsNumber(Integer.valueOf("0")));

        Assert.assertEquals(
                BigDecimal.valueOf(-23223212121L),
                EvaluationUtil.evaluateAsNumber(Long.valueOf("-23223212121")));
        Assert.assertEquals(
                BigDecimal.valueOf(23223212121L),
                EvaluationUtil.evaluateAsNumber(Long.valueOf("23223212121")));
        Assert.assertEquals(
                BigDecimal.valueOf(0),
                EvaluationUtil.evaluateAsNumber(Long.valueOf("0")));

        Assert.assertTrue(
                BigDecimal.valueOf(23.0f).compareTo(
                    EvaluationUtil.evaluateAsNumber(Float.valueOf(23.0f))) == 0);
        {
            BigDecimal num = BigDecimal.valueOf(23.89754f);
            num = num.setScale(5, RoundingMode.HALF_UP);
            BigDecimal result = EvaluationUtil.evaluateAsNumber(Float.valueOf(23.89754f));
            result = result.setScale(5, RoundingMode.HALF_UP);
            Assert.assertTrue(num.compareTo(result) == 0);
        }
        Assert.assertTrue(
                BigDecimal.ZERO.compareTo(
                    EvaluationUtil.evaluateAsNumber(Float.valueOf(0))) == 0);
        Assert.assertTrue(
                BigDecimal.valueOf(0.0d).compareTo(
                    EvaluationUtil.evaluateAsNumber(Float.valueOf(0.0f))) == 0);
        {
            BigDecimal num = BigDecimal.valueOf(23.1f);
            num = num.setScale(1, RoundingMode.HALF_UP);
            BigDecimal result = EvaluationUtil.evaluateAsNumber(Float.valueOf(23.0f));
            result = result.setScale(1, RoundingMode.HALF_UP);
            Assert.assertTrue(num.compareTo(result) != 0);
        }

        Assert.assertTrue(
                BigDecimal.valueOf(23.0d).compareTo(
                        EvaluationUtil.evaluateAsNumber(Double.valueOf(23.0d))) == 0);
        {
            BigDecimal num = BigDecimal.valueOf(23.89754d);
            num = num.setScale(5, RoundingMode.HALF_UP);
            BigDecimal result = EvaluationUtil.evaluateAsNumber(Double.valueOf(23.89754d));
            result = result.setScale(5, RoundingMode.HALF_UP);
            Assert.assertTrue(num.compareTo(result) == 0);
        }
        Assert.assertTrue(
                BigDecimal.ZERO.compareTo(
                        EvaluationUtil.evaluateAsNumber(Double.valueOf(0))) == 0);
        Assert.assertTrue(
                BigDecimal.valueOf(0.0d).compareTo(
                        EvaluationUtil.evaluateAsNumber(Double.valueOf(0.0d))) == 0);
        {
            BigDecimal num = BigDecimal.valueOf(23.1d);
            num = num.setScale(1, RoundingMode.HALF_UP);
            BigDecimal result = EvaluationUtil.evaluateAsNumber(Double.valueOf(23.0d));
            result = result.setScale(1, RoundingMode.HALF_UP);
            Assert.assertTrue(num.compareTo(result) != 0);
        }

        Assert.assertTrue(
                BigDecimal.valueOf(23.0d).compareTo(
                        EvaluationUtil.evaluateAsNumber("23.0")) == 0);
        {
            BigDecimal num = BigDecimal.valueOf(23.89754d);
            num = num.setScale(5, RoundingMode.HALF_UP);
            BigDecimal result = EvaluationUtil.evaluateAsNumber("23.89754");
            result = result.setScale(5, RoundingMode.HALF_UP);
            Assert.assertTrue(num.compareTo(result) == 0);
        }
        Assert.assertTrue(
                BigDecimal.ZERO.compareTo(
                        EvaluationUtil.evaluateAsNumber("0")) == 0);
        Assert.assertTrue(
                BigDecimal.valueOf(0.0d).compareTo(
                        EvaluationUtil.evaluateAsNumber("0.0")) == 0);
        {
            BigDecimal num = BigDecimal.valueOf(23.1d);
            num = num.setScale(1, RoundingMode.HALF_UP);
            BigDecimal result = EvaluationUtil.evaluateAsNumber("23.0");
            result = result.setScale(1, RoundingMode.HALF_UP);
            Assert.assertTrue(num.compareTo(result) != 0);
        }

        Assert.assertNull(EvaluationUtil.evaluateAsNumber("something"));
        Assert.assertNull(EvaluationUtil.evaluateAsNumber(EvaluationUtil.class));

    }





    @Test
    public void convertToListTest() {

        {
            final List<Object> result = EvaluationUtil.evaluateAsIterable(null);
            Assert.assertTrue(result != null && result.size() == 0);
        }

        {
            final Set<Object> set = new LinkedHashSet<Object>();
            set.add(Integer.valueOf(2));
            set.add(Integer.valueOf(43));
            final List<Object> list = new ArrayList<Object>();
            list.add(Integer.valueOf(2));
            list.add(Integer.valueOf(43));

            final List<Object> result = EvaluationUtil.evaluateAsIterable(set);
            Assert.assertTrue(result != null && result instanceof List && list.equals(result));
        }

        {
            final Map<Object,Object> map = new LinkedHashMap<Object,Object>();
            map.put("a", Integer.valueOf(2));
            map.put("b", Integer.valueOf(43));
            final List<Object> list = new ArrayList<Object>();
            list.add(new EvaluationUtil.MapEntry<Object,Object>("a", Integer.valueOf(2)));
            list.add(new EvaluationUtil.MapEntry<Object,Object>("b", Integer.valueOf(43)));

            final List<Object> result = EvaluationUtil.evaluateAsIterable(map);
            Assert.assertTrue(result != null && result instanceof List && list.equals(result));
        }

        {
            final byte[] arr0 = new byte[0];
            final List<Object> list0 = new ArrayList<Object>();
            final List<Object> result0 = EvaluationUtil.evaluateAsIterable(arr0);
            Assert.assertTrue(result0 != null && result0 instanceof List && list0.equals(result0));


            final byte[] arr = new byte[2];
            arr[0] = (byte)23;
            arr[1] = (byte)-127;
            final List<Object> list = new ArrayList<Object>();
            list.add(Byte.valueOf((byte)23));
            list.add(Byte.valueOf((byte)-127));

            final List<Object> result = EvaluationUtil.evaluateAsIterable(arr);
            Assert.assertTrue(result != null && result instanceof List && list.equals(result));
        }

        {
            final short[] arr0 = new short[0];
            final List<Object> list0 = new ArrayList<Object>();
            final List<Object> result0 = EvaluationUtil.evaluateAsIterable(arr0);
            Assert.assertTrue(result0 != null && result0 instanceof List && list0.equals(result0));


            final short[] arr = new short[2];
            arr[0] = (short)23;
            arr[1] = (short)-127;
            final List<Object> list = new ArrayList<Object>();
            list.add(Short.valueOf((short)23));
            list.add(Short.valueOf((short)-127));

            final List<Object> result = EvaluationUtil.evaluateAsIterable(arr);
            Assert.assertTrue(result != null && result instanceof List && list.equals(result));
        }

        {
            final int[] arr0 = new int[0];
            final List<Object> list0 = new ArrayList<Object>();
            final List<Object> result0 = EvaluationUtil.evaluateAsIterable(arr0);
            Assert.assertTrue(result0 != null && result0 instanceof List && list0.equals(result0));


            final int[] arr = new int[2];
            arr[0] = 23;
            arr[1] = -127;
            final List<Object> list = new ArrayList<Object>();
            list.add(Integer.valueOf(23));
            list.add(Integer.valueOf(-127));

            final List<Object> result = EvaluationUtil.evaluateAsIterable(arr);
            Assert.assertTrue(result != null && result instanceof List && list.equals(result));
        }

        {
            final long[] arr0 = new long[0];
            final List<Object> list0 = new ArrayList<Object>();
            final List<Object> result0 = EvaluationUtil.evaluateAsIterable(arr0);
            Assert.assertTrue(result0 != null && result0 instanceof List && list0.equals(result0));


            final long[] arr = new long[2];
            arr[0] = 23L;
            arr[1] = -127L;
            final List<Object> list = new ArrayList<Object>();
            list.add(Long.valueOf(23L));
            list.add(Long.valueOf(-127L));

            final List<Object> result = EvaluationUtil.evaluateAsIterable(arr);
            Assert.assertTrue(result != null && result instanceof List && list.equals(result));
        }

        {
            final float[] arr0 = new float[0];
            final List<Object> list0 = new ArrayList<Object>();
            final List<Object> result0 = EvaluationUtil.evaluateAsIterable(arr0);
            Assert.assertTrue(result0 != null && result0 instanceof List && list0.equals(result0));


            final float[] arr = new float[2];
            arr[0] = 23.0f;
            arr[1] = -127.1f;
            final List<Object> list = new ArrayList<Object>();
            list.add(Float.valueOf(23.0f));
            list.add(Float.valueOf(-127.1f));

            final List<Object> result = EvaluationUtil.evaluateAsIterable(arr);
            Assert.assertTrue(result != null && result instanceof List && result.size() == list.size());
            for (int i = 0; i < result.size(); i++) {
                Assert.assertTrue(result.get(i) != null && result.get(i) instanceof Float &&
                        (((Float)result.get(i)).compareTo((Float)list.get(i)) == 0));
            }
        }

        {
            final double[] arr0 = new double[0];
            final List<Object> list0 = new ArrayList<Object>();
            final List<Object> result0 = EvaluationUtil.evaluateAsIterable(arr0);
            Assert.assertTrue(result0 != null && result0 instanceof List && list0.equals(result0));


            final double[] arr = new double[2];
            arr[0] = 23.0d;
            arr[1] = -127.1d;
            final List<Object> list = new ArrayList<Object>();
            list.add(Double.valueOf(23.0d));
            list.add(Double.valueOf(-127.1d));

            final List<Object> result = EvaluationUtil.evaluateAsIterable(arr);
            Assert.assertTrue(result != null && result instanceof List && result.size() == list.size());
            for (int i = 0; i < result.size(); i++) {
                Assert.assertTrue(result.get(i) != null && result.get(i) instanceof Double &&
                        (((Double)result.get(i)).compareTo((Double)list.get(i)) == 0));
            }
        }

        {
            final boolean[] arr0 = new boolean[0];
            final List<Object> list0 = new ArrayList<Object>();
            final List<Object> result0 = EvaluationUtil.evaluateAsIterable(arr0);
            Assert.assertTrue(result0 != null && result0 instanceof List && list0.equals(result0));


            final boolean[] arr = new boolean[2];
            arr[0] = true;
            arr[1] = false;
            final List<Object> list = new ArrayList<Object>();
            list.add(Boolean.TRUE);
            list.add(Boolean.FALSE);

            final List<Object> result = EvaluationUtil.evaluateAsIterable(arr);
            Assert.assertTrue(result != null && result instanceof List && list.equals(result));
        }

        {
            final char[] arr0 = new char[0];
            final List<Object> list0 = new ArrayList<Object>();
            final List<Object> result0 = EvaluationUtil.evaluateAsIterable(arr0);
            Assert.assertTrue(result0 != null && result0 instanceof List && list0.equals(result0));


            final char[] arr = new char[3];
            arr[0] = 'a';
            arr[1] = 'x';
            arr[2] = (char)0;
            final List<Object> list = new ArrayList<Object>();
            list.add(Character.valueOf('a'));
            list.add(Character.valueOf('x'));
            list.add(Character.valueOf((char)0));

            final List<Object> result = EvaluationUtil.evaluateAsIterable(arr);
            Assert.assertTrue(result != null && result instanceof List && list.equals(result));
        }

        {
            final Class<?>[] arr0 = new Class<?>[0];
            final List<Object> list0 = new ArrayList<Object>();
            final List<Object> result0 = EvaluationUtil.evaluateAsIterable(arr0);
            Assert.assertTrue(result0 != null && result0 instanceof List && list0.equals(result0));


            final Class<?>[] arr = new Class<?>[2];
            arr[0] = EvaluationUtil.class;
            arr[1] = EvaluationUtilTest.class;
            final List<Object> list = new ArrayList<Object>();
            list.add(EvaluationUtil.class);
            list.add(EvaluationUtilTest.class);

            final List<Object> result = EvaluationUtil.evaluateAsIterable(arr);
            Assert.assertTrue(result != null && result instanceof List && list.equals(result));
        }

        {
            final List<Object> list = new ArrayList<Object>();
            list.add(EvaluationUtil.class);

            final List<Object> result = EvaluationUtil.evaluateAsIterable(EvaluationUtil.class);
            Assert.assertTrue(result != null && result instanceof List && list.equals(result));
        }

    }







    @Test
    public void convertToArrayTest() {

        {
            final Object[] result = EvaluationUtil.evaluateAsArray(null);
            Assert.assertTrue(result != null && result.length == 1 && result[0] == null);
        }

        {
            final Set<Object> set = new LinkedHashSet<Object>();
            set.add(Integer.valueOf(2));
            set.add(Integer.valueOf(43));
            final Object[] array = new Object[2];
            array[0] = Integer.valueOf(2);
            array[1] = Integer.valueOf(43);

            final Object[] result = EvaluationUtil.evaluateAsArray(set);
            Assert.assertTrue(result != null && ArrayUtils.isEquals(array, result));
        }

        {
            final Map<Object,Object> map = new LinkedHashMap<Object,Object>();
            map.put("a", Integer.valueOf(2));
            map.put("b", Integer.valueOf(43));
            final Object[] array = new Object[2];
            array[0] = new EvaluationUtil.MapEntry<Object,Object>("a", Integer.valueOf(2));
            array[1] = new EvaluationUtil.MapEntry<Object,Object>("b", Integer.valueOf(43));

            final Object[] result = EvaluationUtil.evaluateAsArray(map);
            Assert.assertTrue(result != null && ArrayUtils.isEquals(array, result));
        }

        {
            final Map<Object,Object> map = new LinkedHashMap<Object,Object>();
            map.put("a", Integer.valueOf(2));
            map.put("b", Integer.valueOf(43));
            final Object[] array = new Object[2];
            array[0] = new EvaluationUtil.MapEntry<Object,Object>("a", Integer.valueOf(2));
            array[1] = new EvaluationUtil.MapEntry<Object,Object>("b", Integer.valueOf(43));

            final Object[] result = EvaluationUtil.evaluateAsArray(map);
            Assert.assertTrue(result != null && ArrayUtils.isEquals(array, result));
        }

        {
            final Boolean[] arr0 = new Boolean[0];
            final Boolean[] array0 = new Boolean[0];
            final Object[] result0 = EvaluationUtil.evaluateAsArray(arr0);
            Assert.assertTrue(result0 != null && ArrayUtils.isEquals(array0, result0));


            final Boolean[] arr = new Boolean[2];
            arr[0] = Boolean.TRUE;
            arr[1] = Boolean.FALSE;
            final Boolean[] array = new Boolean[2];
            array[0] = Boolean.TRUE;
            array[1] = Boolean.FALSE;

            final Object[] result = EvaluationUtil.evaluateAsArray(arr);
            Assert.assertTrue(result != null && ArrayUtils.isEquals(array, result));
        }

        {
            final Object[] arr = new Object[1];
            arr[0] = EvaluationUtil.class;

            final Object[] result = EvaluationUtil.evaluateAsArray(EvaluationUtil.class);
            Assert.assertTrue(result != null && result.length == 1 && result[0] == EvaluationUtil.class);
        }

    }



    
}
