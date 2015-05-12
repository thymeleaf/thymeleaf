/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 *   Character sequence that aggregates one or several {@link String} objects, without the need to clone them.
 * </p>
 * <p>
 *   Special implementation of the {@link CharSequence} interface that replaces {@link String} objects in many
 *   parts of the framework where a specific text literal is composed of several parts and we want to avoid
 *   creating new {@link String} objects for them, using instead objects of this class that simply keep an array
 *   of references to the original Strings.
 * </p>
 *
 * <p>
 *   This class is <strong>thread-safe</strong>
 * </p>
 *
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class AggregateString implements Serializable, CharSequence {


    protected static final long serialVersionUID = 823987612L;

    private static final int[] UNIQUE_ZERO_OFFSET = new int[] { 0 };

    private final String[] values;
    private final int[] offsets;
    private final int length;

    // This variable will mimic the hashCode cache mechanism in java.lang.String
    private int hash; // defaults to 0




    public AggregateString(final String component) {

        super();

        if (component == null) {
            throw new IllegalArgumentException("Component argument is null, which is forbidden");
        }

        this.values = new String[] { component };
        this.offsets = UNIQUE_ZERO_OFFSET;
        this.length = component.length();

    }


    public AggregateString(final String component0, final String component1) {

        super();

        if (component0 == null || component1 == null) {
            throw new IllegalArgumentException("At least one component argument is null, which is forbidden");
        }

        this.values = new String[] { component0, component1 };
        this.offsets = new int[] { 0, component0.length() };
        this.length = this.offsets[1] + component1.length();

    }


    public AggregateString(final String component0, final String component1, final String component2) {

        super();

        if (component0 == null || component1 == null || component2 == null) {
            throw new IllegalArgumentException("At least one component argument is null, which is forbidden");
        }

        this.values = new String[] { component0, component1, component2 };
        this.offsets = new int[] { 0, component0.length(), component0.length() + component1.length() };
        this.length = this.offsets[2] + component2.length();

    }


    public AggregateString(final String component0, final String component1, final String component2, final String component3) {

        super();

        if (component0 == null || component1 == null || component2 == null || component3 == null) {
            throw new IllegalArgumentException("At least one component argument is null, which is forbidden");
        }

        this.values = new String[] { component0, component1, component2, component3 };
        this.offsets = new int[] { 0, component0.length(), component0.length() + component1.length(), component0.length() + component1.length() + component2.length() };
        this.length = this.offsets[3] + component3.length();

    }



    public AggregateString(final String[] components) {

        // NOTE: We have this set of constructors instead of only one with a varargs argument in order to
        // avoid unnecessary creation of String[] objects

        super();

        if (components == null) {
            throw new IllegalArgumentException("Components argument array cannot be null");
        }

        if (components.length == 0) {

            // We want always at least one String
            this.values = new String[]{""};
            this.offsets = UNIQUE_ZERO_OFFSET;
            this.length = 0;

        } else {

            this.values = new String[components.length];
            this.offsets = new int[components.length];

            int totalLength = 0;
            int i = 0;
            while (i < components.length) {
                if (components[i] == null) {
                    throw new IllegalArgumentException("Components argument contains at least a null, which is forbidden");
                }
                final int componentLen = components[i].length();
                this.values[i] = components[i];
                this.offsets[i] = (i == 0 ? 0 : this.offsets[i - 1] + this.values[i - 1].length());
                totalLength += componentLen;
                i++;
            }

            this.length = totalLength;

        }

    }




    public int length() {
        return this.length;
    }




    public char charAt(final int index) {
        if ((index < 0) || (index >= this.length)) {
            throw new StringIndexOutOfBoundsException(index);
        }
        int n = this.values.length;
        while (n-- != 0) {
            if (this.offsets[n] <= index) {
                return this.values[n].charAt(index - this.offsets[n]);
            }
        }
        // Should never reach here!
        throw new IllegalStateException("Bad computing of charAt at AggregatedString");
    }




    public CharSequence subSequence(final int beginIndex, final int endIndex) {

        if (beginIndex < 0) {
            throw new StringIndexOutOfBoundsException(beginIndex);
        }
        if (endIndex > this.length) {
            throw new StringIndexOutOfBoundsException(endIndex);
        }
        int subLen = endIndex - beginIndex;
        if (subLen < 0) {
            throw new StringIndexOutOfBoundsException(subLen);
        }

        if (subLen == 0) {
            return "";
        }

        int n1 = this.values.length;
        while (n1-- != 0) {
            if (this.offsets[n1] < endIndex) { // Will always happen eventually, as the first offset is 0
                break;
            }
        }
        int n0 = n1 + 1;
        while (n0-- != 0) {
            if (this.offsets[n0] <= beginIndex) { // Will always happen eventually, as the first offset is 0
                break;
            }
        }
        if (n0 == n1) {
            // Shortcut: let the String#substring method do the job...
            return this.values[n0].substring((beginIndex - this.offsets[n0]), (endIndex - this.offsets[n0]));
        }
        final char[] chars = new char[endIndex - beginIndex];
        int charsOffset = 0;
        int nx = n0;
        while (nx <= n1) {
            final int nstart = Math.max(beginIndex, this.offsets[nx]) - this.offsets[nx];
            final int nend = Math.min(endIndex, this.offsets[nx] + this.values[nx].length()) - this.offsets[nx];
            this.values[nx].getChars(nstart, nend, chars, charsOffset);
            charsOffset += (nend - nstart);
            nx++;
        }
        return new String(chars);

    }




    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof AggregateString)) {
            return false;
        }

        final AggregateString that = (AggregateString) o;

        if (this.values.length == 1 && that.values.length == 1) {
            return this.values[0].equals(that.values[0]);
        }

        if (this.length != that.length) {
            return false;
        }

        if (this.length == 0) {
            return true;
        }

        if (this.hash != 0 && that.hash != 0 && this.hash != that.hash) {
            return false;
        }

        int i = 0;

        int m1 = 0;
        int n1 = 0;
        int len1 = this.values[m1].length();

        int m2 = 0;
        int n2 = 0;
        int len2 = that.values[m2].length();

        while (i < this.length) {

            // Move to the next value array if needed, including skipping those with len == 0
            while (n1 >= len1 && (m1 + 1) < this.values.length) {
                m1++; n1 = 0;
                len1 = this.values[m1].length();
            }
            while (n2 >= len2 && (m2 + 1) < that.values.length) {
                m2++; n2 = 0;
                len2 = that.values[m2].length();
            }

            // Shortcut, in case we have to identical strings ready to be compared with one another
            if (n1 == 0 && n2 == 0 && len1 == len2) {
                if (!this.values[m1].equals(that.values[m2])) {
                    return false;
                }
                n1 = len1; // Force skipping this value position
                n2 = len2; // Force skipping this value position
                i += len1;
                continue;
            }

            // Character-by-character matching
            if (this.values[m1].charAt(n1) != that.values[m2].charAt(n2)) {
                return false;
            }

            n1++;
            n2++;
            i++;

        }

        return true;

    }




    public int hashCode() {
        // This method mimics the local-variable cache mechanism from java.lang.String
        // ---------------------------------------
        // NOTE: Even if relying on the specific implementation of String.hashCode() might seem
        //       a potential issue for cross-platform compatibility, the fact is that the
        //       implementation of String.hashCode() is actually a part of the Java Specification
        //       since Java 1.2, and its internal workings are explained in the JavaDoc for the
        //       String.hashCode() method.
        // ---------------------------------------
        int h = this.hash;
        if (h == 0 && this.length > 0) {
            if (this.values.length == 1) {
                h = this.values[0].hashCode(); // Might be cached at the String object, let's benefit from that
            } else {
                final String vals[] = this.values;
                String val;
                int valLen;
                for (int x = 0; x < vals.length; x++) {
                    val = vals[x];
                    valLen = val.length();
                    for (int i = 0; i < valLen; i++) {
                        h = 31 * h + val.charAt(i);
                    }
                }
            }
            this.hash = h;
        }
        return h;
    }





    public boolean contentEquals(final StringBuffer sb) {
        synchronized (sb) {
            return contentEquals((CharSequence) sb);
        }
    }


    public boolean contentEquals(final CharSequence cs) {

        if (this.length != cs.length()) {
            return false;
        }

        if (this.length == 0) {
            return true;
        }

        // Shortcut in case argument is another AggregatedString
        if (cs.equals(this)) {
            return true;
        }

        if (cs instanceof String) {
            if (this.values.length == 1) {
                return this.values[0].equals(cs);
            }
            if (this.hash != 0 && this.hash != cs.hashCode()) {
                return false;
            }
        }

        // Deal with argument as a generic CharSequence
        int i = 0;

        int m1 = 0;
        int n1 = 0;
        int len1 = this.values[m1].length();

        while (i < this.length) {

            // Move to the next value array if needed, including skipping those with len == 0
            while (n1 >= len1 && (m1 + 1) < this.values.length) {
                m1++; n1 = 0;
                len1 = this.values[m1].length();
            }

            // Character-by-character matching
            if (this.values[m1].charAt(n1) != cs.charAt(i)) {
                return false;
            }

            n1++;
            i++;

        }
        return true;

    }




    @Override
    public String toString() {
        if (this.length == 0) {
            return "";
        }
        if (this.values.length == 1) {
            // Hooray, no need to create a new String object!
            return this.values[0];
        }
        final char[] chars = new char[this.length];
        for (int i = 0; i < this.values.length; i++) {
            this.values[i].getChars(0, this.values[i].length(), chars, this.offsets[i]);
        }
        return new String(chars);
    }





    // TODO Turn this into a test when thymeleaf-tests compiles again
    public static void main(String[] args) {

        try {

            final String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

            String oldText = null;

            for (int textLen = 0; textLen < base.length(); textLen++) {

                final String text = base.substring(0, textLen);

                final List<AggregateString> allAS = new ArrayList<AggregateString>();

                for (int initialCapacity = 1; initialCapacity < 10; initialCapacity++) {

                    for (int textx = 0; textx <= textLen; textx++) {

                        for (int texty = 0; textx + texty <= textLen; texty++) {

                            final String textStr0 = new String(text.substring(0, textx));
                            final String textStr1 = new String(text.substring(textx, (textx + texty)));
                            final String textStr2 = new String(text.substring((textx + texty), textLen));

                            final AggregateString as =
                                    new AggregateString(textStr0, textStr1, textStr2);

                            allAS.add(as);

                            final String asText = as.toString();
                            assertEquals(text, asText);
                            assertTrue(text.hashCode() == as.hashCode());
                            assertTrue(text.hashCode() == TextUtil.hashCode(textStr0, textStr1, textStr2));
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

                for (final AggregateString as1 : allAS) {
                    for (final AggregateString as2 : allAS) {
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
