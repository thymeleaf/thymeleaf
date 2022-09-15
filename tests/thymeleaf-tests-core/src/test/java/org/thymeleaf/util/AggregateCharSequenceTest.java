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

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;


/**
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class AggregateCharSequenceTest {



    @Test
    public void testAggregateString() throws Exception {

        try {

            final String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

            String oldText = null;

            for (int textLen = 0; textLen < base.length(); textLen++) {

                final String text = base.substring(0, textLen);

                final List<AggregateCharSequence> allAS = new ArrayList<AggregateCharSequence>();

                for (int initialCapacity = 1; initialCapacity < 10; initialCapacity++) {

                    for (int textx = 0; textx <= textLen; textx++) {

                        for (int texty = 0; textx + texty <= textLen; texty++) {

                            final String textStr0 = new String(text.substring(0, textx));
                            final String textStr1 = new String(text.substring(textx, (textx + texty)));
                            final String textStr2 = new String(text.substring((textx + texty), textLen));

                            final AggregateCharSequence as =
                                    new AggregateCharSequence(textStr0, textStr1, textStr2);

                            allAS.add(as);

                            final String asText = as.toString();
                            assertEquals(text, asText);
                            assertTrue(text.hashCode() == as.hashCode());
                            assertTrue(text.hashCode() == TextUtils.hashCode(textStr0, textStr1, textStr2));
                            assertTrue(textLen == as.length());

                            final int asLen = as.length();
                            for (int i = 0; i < asLen; i++) {
                                assertTrue(text.charAt(i) == as.charAt(i));
                            }

                            for (int subx = 0; subx <= textLen; subx++ ) {
                                for (int suby = 0; subx + suby <= textLen; suby++) {
                                    assertEquals(text.substring(subx, subx + suby), (String)as.subSequence(subx, subx + suby));
                                }
                            }

                        }

                    }

                }

                for (final AggregateCharSequence as1 : allAS) {
                    for (final AggregateCharSequence as2 : allAS) {
                        assertTrue(as1.equals(as2));
                        assertTrue(as1.contentEquals(as2));
                        assertTrue(as1.hashCode() == as2.hashCode());
                    }
                    assertTrue(!as1.equals(text));
                    assertTrue(as1.contentEquals(text));
                    assertTrue(!text.equals(as1));
                    assertTrue(text.contentEquals(as1));
                    if (oldText != null) {
                        assertTrue(!as1.equals(oldText));
                        assertTrue(!as1.contentEquals(oldText));
                        assertTrue(!oldText.equals(as1));
                        assertTrue(!oldText.contentEquals(as1));
                    }
                }

                oldText = text;

            }

        } catch (final Throwable e) {
            e.printStackTrace(System.err);
        }

    }



    private static void assertTrue(final boolean condition) {
        if (!condition) {
            throw new RuntimeException("Condition is not true");
        }
    }


    private static void assertEquals(final String expected, final String actual) {
        if (expected == null) {
            if (actual != null) {
                throw new RuntimeException("Expected '" + expected + "' does not match actual '" + actual + "'");
            }
        } else if (actual == null) {
            throw new RuntimeException("Expected '" + expected + "' does not match actual '" + actual + "'");
        } else {
            if (!expected.equals(actual)) {
                throw new RuntimeException("Expected '" + expected + "' does not match actual '" + actual + "'");
            }
        }
    }


}
