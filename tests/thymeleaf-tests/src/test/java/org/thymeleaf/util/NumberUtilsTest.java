package org.thymeleaf.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NumberUtilsTest {



    @Test
    public void testSequence() {

        Assertions.assertArrayEquals(new Integer[] {new Integer(1),new Integer(2),new Integer(3)}, NumberUtils.sequence(new Integer(1),new Integer(3)));
        Assertions.assertArrayEquals(new Integer[] {new Integer(1),new Integer(2),new Integer(3)}, NumberUtils.sequence(new Integer(1),new Integer(3), new Integer(1)));
        Assertions.assertArrayEquals(new Integer[] {new Integer(1),new Integer(3)}, NumberUtils.sequence(new Integer(1),new Integer(3), new Integer(2)));
        Assertions.assertArrayEquals(new Integer[] {new Integer(3)}, NumberUtils.sequence(new Integer(3),new Integer(3), new Integer(1)));
        Assertions.assertArrayEquals(new Integer[] {new Integer(3)}, NumberUtils.sequence(new Integer(3),new Integer(3), new Integer(2)));
        Assertions.assertArrayEquals(new Integer[] {new Integer(3)}, NumberUtils.sequence(new Integer(3),new Integer(3)));

        Assertions.assertArrayEquals(new Integer[] {new Integer(-1),new Integer(-2),new Integer(-3)}, NumberUtils.sequence(new Integer(-1),new Integer(-3)));
        Assertions.assertArrayEquals(new Integer[] {new Integer(-1),new Integer(-2),new Integer(-3)}, NumberUtils.sequence(new Integer(-1),new Integer(-3), new Integer(-1)));
        Assertions.assertArrayEquals(new Integer[] {new Integer(-1),new Integer(-3)}, NumberUtils.sequence(new Integer(-1),new Integer(-3), new Integer(-2)));
        Assertions.assertArrayEquals(new Integer[] {new Integer(-3)}, NumberUtils.sequence(new Integer(-3),new Integer(-3), new Integer(-1)));
        Assertions.assertArrayEquals(new Integer[] {new Integer(-3)}, NumberUtils.sequence(new Integer(-3),new Integer(-3), new Integer(-2)));
        Assertions.assertArrayEquals(new Integer[] {new Integer(-3)}, NumberUtils.sequence(new Integer(-3),new Integer(-3)));

        Assertions.assertArrayEquals(new Integer[0], NumberUtils.sequence(new Integer(1),new Integer(3), new Integer(-1)));
        Assertions.assertArrayEquals(new Integer[0], NumberUtils.sequence(new Integer(-1),new Integer(-3), new Integer(1)));
        Assertions.assertArrayEquals(new Integer[0], NumberUtils.sequence(new Integer(1),new Integer(3), new Integer(-2)));
        Assertions.assertArrayEquals(new Integer[0], NumberUtils.sequence(new Integer(-1),new Integer(-3), new Integer(2)));
        Assertions.assertArrayEquals(new Integer[0], NumberUtils.sequence(new Integer(3),new Integer(1), new Integer(1)));
        Assertions.assertArrayEquals(new Integer[0], NumberUtils.sequence(new Integer(-3),new Integer(-1), new Integer(-1)));
        Assertions.assertArrayEquals(new Integer[0], NumberUtils.sequence(new Integer(3),new Integer(1), new Integer(2)));
        Assertions.assertArrayEquals(new Integer[0], NumberUtils.sequence(new Integer(-3),new Integer(-1), new Integer(-2)));

    }


}
