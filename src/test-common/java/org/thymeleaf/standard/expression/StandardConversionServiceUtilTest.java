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


public final class StandardConversionServiceUtilTest {



    @Test
    public void convertToBooleanTest() {

        Assert.assertFalse(StandardConversionServiceUtil.convertToBoolean(null));

        Assert.assertTrue(StandardConversionServiceUtil.convertToBoolean(Boolean.TRUE));
        Assert.assertFalse(StandardConversionServiceUtil.convertToBoolean(Boolean.FALSE));

        Assert.assertFalse(StandardConversionServiceUtil.convertToBoolean(BigDecimal.ZERO));
        Assert.assertTrue(StandardConversionServiceUtil.convertToBoolean(BigDecimal.ONE));
        Assert.assertTrue(StandardConversionServiceUtil.convertToBoolean(BigDecimal.TEN));

        Assert.assertFalse(StandardConversionServiceUtil.convertToBoolean(BigInteger.ZERO));
        Assert.assertTrue(StandardConversionServiceUtil.convertToBoolean(BigInteger.ONE));
        Assert.assertTrue(StandardConversionServiceUtil.convertToBoolean(BigInteger.TEN));

        Assert.assertFalse(StandardConversionServiceUtil.convertToBoolean(Double.valueOf(0.0d)));
        Assert.assertFalse(StandardConversionServiceUtil.convertToBoolean(Float.valueOf(0.0f)));
        Assert.assertTrue(StandardConversionServiceUtil.convertToBoolean(Double.valueOf(0.1d)));
        Assert.assertTrue(StandardConversionServiceUtil.convertToBoolean(Float.valueOf(0.1f)));
        Assert.assertTrue(StandardConversionServiceUtil.convertToBoolean(Double.valueOf(-0.1d)));
        Assert.assertTrue(StandardConversionServiceUtil.convertToBoolean(Float.valueOf(-0.1f)));
        Assert.assertTrue(StandardConversionServiceUtil.convertToBoolean(Double.valueOf(Double.MAX_VALUE)));
        Assert.assertTrue(StandardConversionServiceUtil.convertToBoolean(Float.valueOf(Float.MAX_VALUE)));
        Assert.assertTrue(StandardConversionServiceUtil.convertToBoolean(Double.valueOf(Double.MIN_VALUE)));
        Assert.assertTrue(StandardConversionServiceUtil.convertToBoolean(Float.valueOf(Float.MIN_VALUE)));

        Assert.assertFalse(StandardConversionServiceUtil.convertToBoolean(Character.valueOf((char) 0)));
        Assert.assertTrue(StandardConversionServiceUtil.convertToBoolean(Character.valueOf('x')));
        Assert.assertTrue(StandardConversionServiceUtil.convertToBoolean(Character.valueOf('0')));
        Assert.assertTrue(StandardConversionServiceUtil.convertToBoolean(Character.valueOf('1')));

        Assert.assertTrue(StandardConversionServiceUtil.convertToBoolean("true"));
        Assert.assertFalse(StandardConversionServiceUtil.convertToBoolean("false"));
        Assert.assertTrue(StandardConversionServiceUtil.convertToBoolean("yes"));
        Assert.assertFalse(StandardConversionServiceUtil.convertToBoolean("no"));
        Assert.assertTrue(StandardConversionServiceUtil.convertToBoolean("on"));
        Assert.assertFalse(StandardConversionServiceUtil.convertToBoolean("off"));
        Assert.assertTrue(StandardConversionServiceUtil.convertToBoolean("sky"));
        Assert.assertTrue(StandardConversionServiceUtil.convertToBoolean("high above"));

        Assert.assertTrue(StandardConversionServiceUtil.convertToBoolean(new LiteralValue("true")));
        Assert.assertFalse(StandardConversionServiceUtil.convertToBoolean(new LiteralValue("false")));
        Assert.assertTrue(StandardConversionServiceUtil.convertToBoolean(new LiteralValue("yes")));
        Assert.assertFalse(StandardConversionServiceUtil.convertToBoolean(new LiteralValue("no")));
        Assert.assertTrue(StandardConversionServiceUtil.convertToBoolean(new LiteralValue("on")));
        Assert.assertFalse(StandardConversionServiceUtil.convertToBoolean(new LiteralValue("off")));
        Assert.assertTrue(StandardConversionServiceUtil.convertToBoolean(new LiteralValue("sky")));
        Assert.assertTrue(StandardConversionServiceUtil.convertToBoolean(new LiteralValue("high above")));

        Assert.assertTrue(StandardConversionServiceUtil.convertToBoolean(StandardConversionServiceUtil.class));

    }



    @Test
    public void convertToNumberTest() {

        Assert.assertEquals(null, StandardConversionServiceUtil.convertToNumber(null));

        Assert.assertEquals(
                BigDecimal.valueOf(23.0f),
                StandardConversionServiceUtil.convertToNumber(BigDecimal.valueOf(23.0f)));
        Assert.assertEquals(
                BigDecimal.valueOf(23.89754f),
                StandardConversionServiceUtil.convertToNumber(BigDecimal.valueOf(23.89754f)));
        Assert.assertEquals(
                BigDecimal.valueOf(23.89754d),
                StandardConversionServiceUtil.convertToNumber(BigDecimal.valueOf(23.89754d)));
        Assert.assertEquals(
                BigDecimal.ZERO,
                StandardConversionServiceUtil.convertToNumber(BigDecimal.valueOf(0)));
        Assert.assertEquals(
                BigDecimal.valueOf(0.0d),
                StandardConversionServiceUtil.convertToNumber(BigDecimal.valueOf(0.0d)));
        Assert.assertNotEquals(
                BigDecimal.valueOf(23.1f),
                StandardConversionServiceUtil.convertToNumber(BigDecimal.valueOf(23.0f)));

        Assert.assertEquals(
                BigDecimal.valueOf(23),
                StandardConversionServiceUtil.convertToNumber(BigInteger.valueOf(23)));
        Assert.assertEquals(
                BigDecimal.valueOf(0),
                StandardConversionServiceUtil.convertToNumber(BigInteger.valueOf(0)));
        Assert.assertEquals(
                BigDecimal.valueOf(-2323232),
                StandardConversionServiceUtil.convertToNumber(BigInteger.valueOf(-2323232)));

        Assert.assertEquals(
                BigDecimal.valueOf(-232),
                StandardConversionServiceUtil.convertToNumber(Short.valueOf("-232")));
        Assert.assertEquals(
                BigDecimal.valueOf(232),
                StandardConversionServiceUtil.convertToNumber(Short.valueOf("232")));
        Assert.assertEquals(
                BigDecimal.valueOf(0),
                StandardConversionServiceUtil.convertToNumber(Short.valueOf("0")));

        Assert.assertEquals(
                BigDecimal.valueOf(-232232),
                StandardConversionServiceUtil.convertToNumber(Integer.valueOf("-232232")));
        Assert.assertEquals(
                BigDecimal.valueOf(232232),
                StandardConversionServiceUtil.convertToNumber(Integer.valueOf("232232")));
        Assert.assertEquals(
                BigDecimal.valueOf(0),
                StandardConversionServiceUtil.convertToNumber(Integer.valueOf("0")));

        Assert.assertEquals(
                BigDecimal.valueOf(-23223212121L),
                StandardConversionServiceUtil.convertToNumber(Long.valueOf("-23223212121")));
        Assert.assertEquals(
                BigDecimal.valueOf(23223212121L),
                StandardConversionServiceUtil.convertToNumber(Long.valueOf("23223212121")));
        Assert.assertEquals(
                BigDecimal.valueOf(0),
                StandardConversionServiceUtil.convertToNumber(Long.valueOf("0")));

        Assert.assertTrue(
                BigDecimal.valueOf(23.0f).compareTo(
                    StandardConversionServiceUtil.convertToNumber(Float.valueOf(23.0f))) == 0);
        {
            BigDecimal num = BigDecimal.valueOf(23.89754f);
            num = num.setScale(5, RoundingMode.HALF_UP);
            BigDecimal result = StandardConversionServiceUtil.convertToNumber(Float.valueOf(23.89754f));
            result = result.setScale(5, RoundingMode.HALF_UP);
            Assert.assertTrue(num.compareTo(result) == 0);
        }
        Assert.assertTrue(
                BigDecimal.ZERO.compareTo(
                    StandardConversionServiceUtil.convertToNumber(Float.valueOf(0))) == 0);
        Assert.assertTrue(
                BigDecimal.valueOf(0.0d).compareTo(
                    StandardConversionServiceUtil.convertToNumber(Float.valueOf(0.0f))) == 0);
        {
            BigDecimal num = BigDecimal.valueOf(23.1f);
            num = num.setScale(1, RoundingMode.HALF_UP);
            BigDecimal result = StandardConversionServiceUtil.convertToNumber(Float.valueOf(23.0f));
            result = result.setScale(1, RoundingMode.HALF_UP);
            Assert.assertTrue(num.compareTo(result) != 0);
        }

        Assert.assertTrue(
                BigDecimal.valueOf(23.0d).compareTo(
                        StandardConversionServiceUtil.convertToNumber(Double.valueOf(23.0d))) == 0);
        {
            BigDecimal num = BigDecimal.valueOf(23.89754d);
            num = num.setScale(5, RoundingMode.HALF_UP);
            BigDecimal result = StandardConversionServiceUtil.convertToNumber(Double.valueOf(23.89754d));
            result = result.setScale(5, RoundingMode.HALF_UP);
            Assert.assertTrue(num.compareTo(result) == 0);
        }
        Assert.assertTrue(
                BigDecimal.ZERO.compareTo(
                        StandardConversionServiceUtil.convertToNumber(Double.valueOf(0))) == 0);
        Assert.assertTrue(
                BigDecimal.valueOf(0.0d).compareTo(
                        StandardConversionServiceUtil.convertToNumber(Double.valueOf(0.0d))) == 0);
        {
            BigDecimal num = BigDecimal.valueOf(23.1d);
            num = num.setScale(1, RoundingMode.HALF_UP);
            BigDecimal result = StandardConversionServiceUtil.convertToNumber(Double.valueOf(23.0d));
            result = result.setScale(1, RoundingMode.HALF_UP);
            Assert.assertTrue(num.compareTo(result) != 0);
        }

        Assert.assertTrue(
                BigDecimal.valueOf(23.0d).compareTo(
                        StandardConversionServiceUtil.convertToNumber("23.0")) == 0);
        {
            BigDecimal num = BigDecimal.valueOf(23.89754d);
            num = num.setScale(5, RoundingMode.HALF_UP);
            BigDecimal result = StandardConversionServiceUtil.convertToNumber("23.89754");
            result = result.setScale(5, RoundingMode.HALF_UP);
            Assert.assertTrue(num.compareTo(result) == 0);
        }
        Assert.assertTrue(
                BigDecimal.ZERO.compareTo(
                        StandardConversionServiceUtil.convertToNumber("0")) == 0);
        Assert.assertTrue(
                BigDecimal.valueOf(0.0d).compareTo(
                        StandardConversionServiceUtil.convertToNumber("0.0")) == 0);
        {
            BigDecimal num = BigDecimal.valueOf(23.1d);
            num = num.setScale(1, RoundingMode.HALF_UP);
            BigDecimal result = StandardConversionServiceUtil.convertToNumber("23.0");
            result = result.setScale(1, RoundingMode.HALF_UP);
            Assert.assertTrue(num.compareTo(result) != 0);
        }

        Assert.assertNull(StandardConversionServiceUtil.convertToNumber("something"));
        Assert.assertNull(StandardConversionServiceUtil.convertToNumber(StandardConversionServiceUtil.class));

    }




    @Test
    public void convertToIterableTest() {

        {
            final List<Object> result = StandardConversionServiceUtil.convertToIterable(null);
            Assert.assertTrue(result != null && result.size() == 0);
        }

        {
            final Set<Object> set = new LinkedHashSet<Object>();
            set.add(Integer.valueOf(2));
            set.add(Integer.valueOf(43));
            final List<Object> list = new ArrayList<Object>();
            list.add(Integer.valueOf(2));
            list.add(Integer.valueOf(43));

            final List<Object> result = StandardConversionServiceUtil.convertToIterable(set);
            Assert.assertTrue(result != null && result instanceof List && list.equals(result));
        }

        {
            final Map<Object,Object> map = new LinkedHashMap<Object,Object>();
            map.put("a", Integer.valueOf(2));
            map.put("b", Integer.valueOf(43));
            final List<Object> list = new ArrayList<Object>();
            list.add(new StandardConversionServiceUtil.MapEntry<Object,Object>("a", Integer.valueOf(2)));
            list.add(new StandardConversionServiceUtil.MapEntry<Object,Object>("b", Integer.valueOf(43)));

            final List<Object> result = StandardConversionServiceUtil.convertToIterable(map);
            Assert.assertTrue(result != null && result instanceof List && list.equals(result));
        }

        {
            final byte[] arr0 = new byte[0];
            final List<Object> list0 = new ArrayList<Object>();
            final List<Object> result0 = StandardConversionServiceUtil.convertToIterable(arr0);
            Assert.assertTrue(result0 != null && result0 instanceof List && list0.equals(result0));


            final byte[] arr = new byte[2];
            arr[0] = (byte)23;
            arr[1] = (byte)-127;
            final List<Object> list = new ArrayList<Object>();
            list.add(Byte.valueOf((byte)23));
            list.add(Byte.valueOf((byte)-127));

            final List<Object> result = StandardConversionServiceUtil.convertToIterable(arr);
            Assert.assertTrue(result != null && result instanceof List && list.equals(result));
        }

        {
            final short[] arr0 = new short[0];
            final List<Object> list0 = new ArrayList<Object>();
            final List<Object> result0 = StandardConversionServiceUtil.convertToIterable(arr0);
            Assert.assertTrue(result0 != null && result0 instanceof List && list0.equals(result0));


            final short[] arr = new short[2];
            arr[0] = (short)23;
            arr[1] = (short)-127;
            final List<Object> list = new ArrayList<Object>();
            list.add(Short.valueOf((short)23));
            list.add(Short.valueOf((short)-127));

            final List<Object> result = StandardConversionServiceUtil.convertToIterable(arr);
            Assert.assertTrue(result != null && result instanceof List && list.equals(result));
        }

        {
            final int[] arr0 = new int[0];
            final List<Object> list0 = new ArrayList<Object>();
            final List<Object> result0 = StandardConversionServiceUtil.convertToIterable(arr0);
            Assert.assertTrue(result0 != null && result0 instanceof List && list0.equals(result0));


            final int[] arr = new int[2];
            arr[0] = 23;
            arr[1] = -127;
            final List<Object> list = new ArrayList<Object>();
            list.add(Integer.valueOf(23));
            list.add(Integer.valueOf(-127));

            final List<Object> result = StandardConversionServiceUtil.convertToIterable(arr);
            Assert.assertTrue(result != null && result instanceof List && list.equals(result));
        }

        {
            final long[] arr0 = new long[0];
            final List<Object> list0 = new ArrayList<Object>();
            final List<Object> result0 = StandardConversionServiceUtil.convertToIterable(arr0);
            Assert.assertTrue(result0 != null && result0 instanceof List && list0.equals(result0));


            final long[] arr = new long[2];
            arr[0] = 23L;
            arr[1] = -127L;
            final List<Object> list = new ArrayList<Object>();
            list.add(Long.valueOf(23L));
            list.add(Long.valueOf(-127L));

            final List<Object> result = StandardConversionServiceUtil.convertToIterable(arr);
            Assert.assertTrue(result != null && result instanceof List && list.equals(result));
        }

        {
            final float[] arr0 = new float[0];
            final List<Object> list0 = new ArrayList<Object>();
            final List<Object> result0 = StandardConversionServiceUtil.convertToIterable(arr0);
            Assert.assertTrue(result0 != null && result0 instanceof List && list0.equals(result0));


            final float[] arr = new float[2];
            arr[0] = 23.0f;
            arr[1] = -127.1f;
            final List<Object> list = new ArrayList<Object>();
            list.add(Float.valueOf(23.0f));
            list.add(Float.valueOf(-127.1f));

            final List<Object> result = StandardConversionServiceUtil.convertToIterable(arr);
            Assert.assertTrue(result != null && result instanceof List && result.size() == list.size());
            for (int i = 0; i < result.size(); i++) {
                Assert.assertTrue(result.get(i) != null && result.get(i) instanceof Float &&
                        (((Float)result.get(i)).compareTo((Float)list.get(i)) == 0));
            }
        }

        {
            final double[] arr0 = new double[0];
            final List<Object> list0 = new ArrayList<Object>();
            final List<Object> result0 = StandardConversionServiceUtil.convertToIterable(arr0);
            Assert.assertTrue(result0 != null && result0 instanceof List && list0.equals(result0));


            final double[] arr = new double[2];
            arr[0] = 23.0d;
            arr[1] = -127.1d;
            final List<Object> list = new ArrayList<Object>();
            list.add(Double.valueOf(23.0d));
            list.add(Double.valueOf(-127.1d));

            final List<Object> result = StandardConversionServiceUtil.convertToIterable(arr);
            Assert.assertTrue(result != null && result instanceof List && result.size() == list.size());
            for (int i = 0; i < result.size(); i++) {
                Assert.assertTrue(result.get(i) != null && result.get(i) instanceof Double &&
                        (((Double)result.get(i)).compareTo((Double)list.get(i)) == 0));
            }
        }

        {
            final boolean[] arr0 = new boolean[0];
            final List<Object> list0 = new ArrayList<Object>();
            final List<Object> result0 = StandardConversionServiceUtil.convertToIterable(arr0);
            Assert.assertTrue(result0 != null && result0 instanceof List && list0.equals(result0));


            final boolean[] arr = new boolean[2];
            arr[0] = true;
            arr[1] = false;
            final List<Object> list = new ArrayList<Object>();
            list.add(Boolean.TRUE);
            list.add(Boolean.FALSE);

            final List<Object> result = StandardConversionServiceUtil.convertToIterable(arr);
            Assert.assertTrue(result != null && result instanceof List && list.equals(result));
        }

        {
            final char[] arr0 = new char[0];
            final List<Object> list0 = new ArrayList<Object>();
            final List<Object> result0 = StandardConversionServiceUtil.convertToIterable(arr0);
            Assert.assertTrue(result0 != null && result0 instanceof List && list0.equals(result0));


            final char[] arr = new char[3];
            arr[0] = 'a';
            arr[1] = 'x';
            arr[2] = (char)0;
            final List<Object> list = new ArrayList<Object>();
            list.add(Character.valueOf('a'));
            list.add(Character.valueOf('x'));
            list.add(Character.valueOf((char)0));

            final List<Object> result = StandardConversionServiceUtil.convertToIterable(arr);
            Assert.assertTrue(result != null && result instanceof List && list.equals(result));
        }

        {
            final Class<?>[] arr0 = new Class<?>[0];
            final List<Object> list0 = new ArrayList<Object>();
            final List<Object> result0 = StandardConversionServiceUtil.convertToIterable(arr0);
            Assert.assertTrue(result0 != null && result0 instanceof List && list0.equals(result0));


            final Class<?>[] arr = new Class<?>[2];
            arr[0] = StandardConversionServiceUtil.class;
            arr[1] = StandardConversionServiceUtilTest.class;
            final List<Object> list = new ArrayList<Object>();
            list.add(StandardConversionServiceUtil.class);
            list.add(StandardConversionServiceUtilTest.class);

            final List<Object> result = StandardConversionServiceUtil.convertToIterable(arr);
            Assert.assertTrue(result != null && result instanceof List && list.equals(result));
        }

        {
            final List<Object> list = new ArrayList<Object>();
            list.add(StandardConversionServiceUtil.class);

            final List<Object> result = StandardConversionServiceUtil.convertToIterable(StandardConversionServiceUtil.class);
            Assert.assertTrue(result != null && result instanceof List && list.equals(result));
        }


    }


    @Test
    public void convertToListTest() {

        {
            final List<Object> result = StandardConversionServiceUtil.convertToList(null);
            Assert.assertTrue(result != null && result.size() == 1 && result.get(0) == null);
        }

        {
            final Set<Object> set = new LinkedHashSet<Object>();
            set.add(Integer.valueOf(2));
            set.add(Integer.valueOf(43));
            final List<Object> list = new ArrayList<Object>();
            list.add(Integer.valueOf(2));
            list.add(Integer.valueOf(43));

            final List<Object> result = StandardConversionServiceUtil.convertToList(set);
            Assert.assertTrue(result != null && result instanceof List && list.equals(result));
        }

        {
            final Map<Object,Object> map = new LinkedHashMap<Object,Object>();
            map.put("a", Integer.valueOf(2));
            map.put("b", Integer.valueOf(43));
            final List<Object> list = new ArrayList<Object>();
            list.add(new StandardConversionServiceUtil.MapEntry<Object,Object>("a", Integer.valueOf(2)));
            list.add(new StandardConversionServiceUtil.MapEntry<Object,Object>("b", Integer.valueOf(43)));

            final List<Object> result = StandardConversionServiceUtil.convertToList(map);
            Assert.assertTrue(result != null && result instanceof List && list.equals(result));
        }

        {
            final byte[] arr0 = new byte[0];
            final List<Object> list0 = new ArrayList<Object>();
            final List<Object> result0 = StandardConversionServiceUtil.convertToList(arr0);
            Assert.assertTrue(result0 != null && result0 instanceof List && list0.equals(result0));


            final byte[] arr = new byte[2];
            arr[0] = (byte)23;
            arr[1] = (byte)-127;
            final List<Object> list = new ArrayList<Object>();
            list.add(Byte.valueOf((byte)23));
            list.add(Byte.valueOf((byte)-127));

            final List<Object> result = StandardConversionServiceUtil.convertToList(arr);
            Assert.assertTrue(result != null && result instanceof List && list.equals(result));
        }

        {
            final short[] arr0 = new short[0];
            final List<Object> list0 = new ArrayList<Object>();
            final List<Object> result0 = StandardConversionServiceUtil.convertToList(arr0);
            Assert.assertTrue(result0 != null && result0 instanceof List && list0.equals(result0));


            final short[] arr = new short[2];
            arr[0] = (short)23;
            arr[1] = (short)-127;
            final List<Object> list = new ArrayList<Object>();
            list.add(Short.valueOf((short)23));
            list.add(Short.valueOf((short)-127));

            final List<Object> result = StandardConversionServiceUtil.convertToList(arr);
            Assert.assertTrue(result != null && result instanceof List && list.equals(result));
        }

        {
            final int[] arr0 = new int[0];
            final List<Object> list0 = new ArrayList<Object>();
            final List<Object> result0 = StandardConversionServiceUtil.convertToList(arr0);
            Assert.assertTrue(result0 != null && result0 instanceof List && list0.equals(result0));


            final int[] arr = new int[2];
            arr[0] = 23;
            arr[1] = -127;
            final List<Object> list = new ArrayList<Object>();
            list.add(Integer.valueOf(23));
            list.add(Integer.valueOf(-127));

            final List<Object> result = StandardConversionServiceUtil.convertToList(arr);
            Assert.assertTrue(result != null && result instanceof List && list.equals(result));
        }

        {
            final long[] arr0 = new long[0];
            final List<Object> list0 = new ArrayList<Object>();
            final List<Object> result0 = StandardConversionServiceUtil.convertToList(arr0);
            Assert.assertTrue(result0 != null && result0 instanceof List && list0.equals(result0));


            final long[] arr = new long[2];
            arr[0] = 23L;
            arr[1] = -127L;
            final List<Object> list = new ArrayList<Object>();
            list.add(Long.valueOf(23L));
            list.add(Long.valueOf(-127L));

            final List<Object> result = StandardConversionServiceUtil.convertToList(arr);
            Assert.assertTrue(result != null && result instanceof List && list.equals(result));
        }

        {
            final float[] arr0 = new float[0];
            final List<Object> list0 = new ArrayList<Object>();
            final List<Object> result0 = StandardConversionServiceUtil.convertToList(arr0);
            Assert.assertTrue(result0 != null && result0 instanceof List && list0.equals(result0));


            final float[] arr = new float[2];
            arr[0] = 23.0f;
            arr[1] = -127.1f;
            final List<Object> list = new ArrayList<Object>();
            list.add(Float.valueOf(23.0f));
            list.add(Float.valueOf(-127.1f));

            final List<Object> result = StandardConversionServiceUtil.convertToList(arr);
            Assert.assertTrue(result != null && result instanceof List && result.size() == list.size());
            for (int i = 0; i < result.size(); i++) {
                Assert.assertTrue(result.get(i) != null && result.get(i) instanceof Float &&
                        (((Float)result.get(i)).compareTo((Float)list.get(i)) == 0));
            }
        }

        {
            final double[] arr0 = new double[0];
            final List<Object> list0 = new ArrayList<Object>();
            final List<Object> result0 = StandardConversionServiceUtil.convertToList(arr0);
            Assert.assertTrue(result0 != null && result0 instanceof List && list0.equals(result0));


            final double[] arr = new double[2];
            arr[0] = 23.0d;
            arr[1] = -127.1d;
            final List<Object> list = new ArrayList<Object>();
            list.add(Double.valueOf(23.0d));
            list.add(Double.valueOf(-127.1d));

            final List<Object> result = StandardConversionServiceUtil.convertToList(arr);
            Assert.assertTrue(result != null && result instanceof List && result.size() == list.size());
            for (int i = 0; i < result.size(); i++) {
                Assert.assertTrue(result.get(i) != null && result.get(i) instanceof Double &&
                        (((Double)result.get(i)).compareTo((Double)list.get(i)) == 0));
            }
        }

        {
            final boolean[] arr0 = new boolean[0];
            final List<Object> list0 = new ArrayList<Object>();
            final List<Object> result0 = StandardConversionServiceUtil.convertToList(arr0);
            Assert.assertTrue(result0 != null && result0 instanceof List && list0.equals(result0));


            final boolean[] arr = new boolean[2];
            arr[0] = true;
            arr[1] = false;
            final List<Object> list = new ArrayList<Object>();
            list.add(Boolean.TRUE);
            list.add(Boolean.FALSE);

            final List<Object> result = StandardConversionServiceUtil.convertToList(arr);
            Assert.assertTrue(result != null && result instanceof List && list.equals(result));
        }

        {
            final char[] arr0 = new char[0];
            final List<Object> list0 = new ArrayList<Object>();
            final List<Object> result0 = StandardConversionServiceUtil.convertToList(arr0);
            Assert.assertTrue(result0 != null && result0 instanceof List && list0.equals(result0));


            final char[] arr = new char[3];
            arr[0] = 'a';
            arr[1] = 'x';
            arr[2] = (char)0;
            final List<Object> list = new ArrayList<Object>();
            list.add(Character.valueOf('a'));
            list.add(Character.valueOf('x'));
            list.add(Character.valueOf((char)0));

            final List<Object> result = StandardConversionServiceUtil.convertToList(arr);
            Assert.assertTrue(result != null && result instanceof List && list.equals(result));
        }

        {
            final Class<?>[] arr0 = new Class<?>[0];
            final List<Object> list0 = new ArrayList<Object>();
            final List<Object> result0 = StandardConversionServiceUtil.convertToList(arr0);
            Assert.assertTrue(result0 != null && result0 instanceof List && list0.equals(result0));


            final Class<?>[] arr = new Class<?>[2];
            arr[0] = StandardConversionServiceUtil.class;
            arr[1] = StandardConversionServiceUtilTest.class;
            final List<Object> list = new ArrayList<Object>();
            list.add(StandardConversionServiceUtil.class);
            list.add(StandardConversionServiceUtilTest.class);

            final List<Object> result = StandardConversionServiceUtil.convertToList(arr);
            Assert.assertTrue(result != null && result instanceof List && list.equals(result));
        }

        {
            final List<Object> list = new ArrayList<Object>();
            list.add(StandardConversionServiceUtil.class);

            final List<Object> result = StandardConversionServiceUtil.convertToList(StandardConversionServiceUtil.class);
            Assert.assertTrue(result != null && result instanceof List && list.equals(result));
        }

    }







    @Test
    public void convertToArrayTest() {

        {
            final Object[] result = StandardConversionServiceUtil.convertToArray(null);
            Assert.assertTrue(result != null && result.length == 1 && result[0] == null);
        }

        {
            final Set<Object> set = new LinkedHashSet<Object>();
            set.add(Integer.valueOf(2));
            set.add(Integer.valueOf(43));
            final Object[] array = new Object[2];
            array[0] = Integer.valueOf(2);
            array[1] = Integer.valueOf(43);

            final Object[] result = StandardConversionServiceUtil.convertToArray(set);
            Assert.assertTrue(result != null && ArrayUtils.isEquals(array, result));
        }

        {
            final Map<Object,Object> map = new LinkedHashMap<Object,Object>();
            map.put("a", Integer.valueOf(2));
            map.put("b", Integer.valueOf(43));
            final Object[] array = new Object[2];
            array[0] = new StandardConversionServiceUtil.MapEntry<Object,Object>("a", Integer.valueOf(2));
            array[1] = new StandardConversionServiceUtil.MapEntry<Object,Object>("b", Integer.valueOf(43));

            final Object[] result = StandardConversionServiceUtil.convertToArray(map);
            Assert.assertTrue(result != null && ArrayUtils.isEquals(array, result));
        }

        {
            final Map<Object,Object> map = new LinkedHashMap<Object,Object>();
            map.put("a", Integer.valueOf(2));
            map.put("b", Integer.valueOf(43));
            final Object[] array = new Object[2];
            array[0] = new StandardConversionServiceUtil.MapEntry<Object,Object>("a", Integer.valueOf(2));
            array[1] = new StandardConversionServiceUtil.MapEntry<Object,Object>("b", Integer.valueOf(43));

            final Object[] result = StandardConversionServiceUtil.convertToArray(map);
            Assert.assertTrue(result != null && ArrayUtils.isEquals(array, result));
        }

        {
            final Boolean[] arr0 = new Boolean[0];
            final Boolean[] array0 = new Boolean[0];
            final Object[] result0 = StandardConversionServiceUtil.convertToArray(arr0);
            Assert.assertTrue(result0 != null && ArrayUtils.isEquals(array0, result0));


            final Boolean[] arr = new Boolean[2];
            arr[0] = Boolean.TRUE;
            arr[1] = Boolean.FALSE;
            final Boolean[] array = new Boolean[2];
            array[0] = Boolean.TRUE;
            array[1] = Boolean.FALSE;

            final Object[] result = StandardConversionServiceUtil.convertToArray(arr);
            Assert.assertTrue(result != null && ArrayUtils.isEquals(array, result));
        }

        {
            final Object[] arr = new Object[1];
            arr[0] = StandardConversionServiceUtil.class;

            final Object[] result = StandardConversionServiceUtil.convertToArray(StandardConversionServiceUtil.class);
            Assert.assertTrue(result != null && result.length == 1 && result[0] == StandardConversionServiceUtil.class);
        }

    }



    
}
