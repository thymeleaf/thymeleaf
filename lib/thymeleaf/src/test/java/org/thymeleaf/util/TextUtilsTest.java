package org.thymeleaf.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TextUtilsTest {



    @Test
    public void testContains() {

        testContains("This is a test", "is  a", false, false);
        testContains("This is a test", "is a", true, false);
        testContains("This iS a test", "is a", false, true);
        testContains("This iS a test", "b", false, false);
        testContains("This iS a test", "This iS a test", true, false);
        testContains("This iS a test", "This iS a test", true, true);
        testContains("This iS a test", "This iS a testa", false, false);
        testContains("This iS a test", "This iS a testa", false, true);
        testContains("aab", "ab", true, false);
        testContains("ababac", "abac", true, false);
        testContains("execution", "exec", true, false);
        testContains("exexecution", "exec", true, false);
        testContains("exeecution", "exec", false, false);

    }


    private void testContains(final String text, final String fragment, final boolean expected, final boolean caseSensitiveOnly)  {

        final char[] text01 = text.toCharArray();
        final char[] text02 = ("..." + text + "...").toCharArray();
        final String text03 = "..." + text + "...";
        final char[] fragment01 = fragment.toCharArray();
        final char[] fragment02 = ("..." + fragment + "...").toCharArray();
        final String fragment03 = "..." + fragment + "...";

        Assertions.assertTrue(expected == TextUtils.contains(caseSensitiveOnly || true, text, fragment));
        Assertions.assertTrue(expected == TextUtils.contains(caseSensitiveOnly || false, text, fragment));
        Assertions.assertTrue(expected == TextUtils.contains(caseSensitiveOnly || true, text01, fragment01));
        Assertions.assertTrue(expected == TextUtils.contains(caseSensitiveOnly || false, text01, fragment01));
        Assertions.assertTrue(expected == TextUtils.contains(caseSensitiveOnly || true, text, fragment01));
        Assertions.assertTrue(expected == TextUtils.contains(caseSensitiveOnly || false, text, fragment01));
        Assertions.assertTrue(expected == TextUtils.contains(caseSensitiveOnly || true, text01, 0, text01.length, fragment01, 0, fragment01.length));
        Assertions.assertTrue(expected == TextUtils.contains(caseSensitiveOnly || false, text01, 0, text01.length, fragment01, 0, fragment01.length));
        Assertions.assertTrue(expected == TextUtils.contains(caseSensitiveOnly || true, text01, 0, text01.length, fragment02, 3, fragment01.length));
        Assertions.assertTrue(expected == TextUtils.contains(caseSensitiveOnly || false, text01, 0, text01.length, fragment02, 3, fragment01.length));
        Assertions.assertTrue(expected == TextUtils.contains(caseSensitiveOnly || true, text02, 3, text01.length, fragment01, 0, fragment01.length));
        Assertions.assertTrue(expected == TextUtils.contains(caseSensitiveOnly || false, text02, 3, text01.length, fragment01, 0, fragment01.length));
        Assertions.assertTrue(expected == TextUtils.contains(caseSensitiveOnly || true, text02, 3, text01.length, fragment02, 3, fragment01.length));
        Assertions.assertTrue(expected == TextUtils.contains(caseSensitiveOnly || false, text02, 3, text01.length, fragment02, 3, fragment01.length));
        Assertions.assertTrue(expected == TextUtils.contains(caseSensitiveOnly || true, text, 0, text01.length, fragment01, 0, fragment01.length));
        Assertions.assertTrue(expected == TextUtils.contains(caseSensitiveOnly || false, text, 0, text01.length, fragment01, 0, fragment01.length));
        Assertions.assertTrue(expected == TextUtils.contains(caseSensitiveOnly || true, text03, 3, text01.length, fragment01, 0, fragment01.length));
        Assertions.assertTrue(expected == TextUtils.contains(caseSensitiveOnly || false, text03, 3, text01.length, fragment01, 0, fragment01.length));
        Assertions.assertTrue(expected == TextUtils.contains(caseSensitiveOnly || true, text01, 0, text01.length, fragment, 0, fragment01.length));
        Assertions.assertTrue(expected == TextUtils.contains(caseSensitiveOnly || false, text01, 0, text01.length, fragment, 0, fragment01.length));
        Assertions.assertTrue(expected == TextUtils.contains(caseSensitiveOnly || true, text01, 0, text01.length, fragment03, 3, fragment01.length));
        Assertions.assertTrue(expected == TextUtils.contains(caseSensitiveOnly || false, text01, 0, text01.length, fragment03, 3, fragment01.length));
        Assertions.assertTrue(expected == TextUtils.contains(caseSensitiveOnly || true, text02, 3, text01.length, fragment03, 3, fragment01.length));
        Assertions.assertTrue(expected == TextUtils.contains(caseSensitiveOnly || false, text02, 3, text01.length, fragment03, 3, fragment01.length));
        Assertions.assertTrue(expected == TextUtils.contains(caseSensitiveOnly || true, text03, 3, text01.length, fragment03, 3, fragment01.length));
        Assertions.assertTrue(expected == TextUtils.contains(caseSensitiveOnly || false, text03, 3, text01.length, fragment03, 3, fragment01.length));

    }


}
