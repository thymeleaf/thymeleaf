package org.thymeleaf.util;

import java.util.Currency;
import java.util.Locale;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NumberUtilsTest {



    @Test
    public void testFormatCurrencyWithCurrency_nullTargetReturnsNull() {
        Assertions.assertNull(NumberUtils.formatCurrency(null, Locale.US, Currency.getInstance("EUR")));
    }

    @Test
    public void testFormatCurrencyWithCurrency_nullLocaleThrows() {
        Assertions.assertThrows(Exception.class,
                () -> NumberUtils.formatCurrency(100.0, null, Currency.getInstance("EUR")));
    }

    @Test
    public void testFormatCurrencyWithCurrency_nullCurrencyThrows() {
        Assertions.assertThrows(Exception.class,
                () -> NumberUtils.formatCurrency(100.0, Locale.US, null));
    }

    @Test
    public void testFormatCurrencyWithCurrency_currencyOverridesLocaleDefault() {
        // Locale.US defaults to USD; formatting with EUR must not produce a USD symbol.
        String result = NumberUtils.formatCurrency(1234.56, Locale.US, Currency.getInstance("EUR"));
        Assertions.assertFalse(result.contains("$"),
                "Result should not contain the USD symbol '$': " + result);
        // EUR symbol or ISO code must appear
        Assertions.assertTrue(result.contains("\u20AC") || result.contains("EUR"),
                "Result should contain the EUR symbol or code: " + result);
    }

    @Test
    public void testFormatCurrencyWithCurrency_jpyZeroFractionDigits() {
        // JPY has 0 default fraction digits; the formatted value must have no decimal separator.
        String result = NumberUtils.formatCurrency(1234, Locale.US, Currency.getInstance("JPY"));
        Assertions.assertFalse(result.contains("."),
                "JPY formatting should produce no decimal separator: " + result);
    }

    @Test
    public void testFormatCurrencyWithCurrency_localeFormattingConventionsApplied() {
        // German locale uses ',' as decimal separator and '.' as grouping separator.
        String deResult = NumberUtils.formatCurrency(1234.56, Locale.GERMANY, Currency.getInstance("USD"));
        String usResult = NumberUtils.formatCurrency(1234.56, Locale.US, Currency.getInstance("USD"));
        Assertions.assertNotEquals(deResult, usResult,
                "German and US locale results should differ in formatting conventions");
        Assertions.assertTrue(deResult.contains(","),
                "German locale result should contain a ',' as decimal separator: " + deResult);
    }

    @Test
    public void testFormatCurrencyWithCurrency_matchesLocaleDefaultWhenCurrencyMatches() {
        // When the given currency matches the locale's default, results should be equal.
        String withCurrency = NumberUtils.formatCurrency(1234.56, Locale.US, Currency.getInstance("USD"));
        String withoutCurrency = NumberUtils.formatCurrency(1234.56, Locale.US);
        Assertions.assertEquals(withoutCurrency, withCurrency);
    }

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
