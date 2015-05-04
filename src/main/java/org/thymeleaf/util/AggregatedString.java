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


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class AggregatedString implements Serializable, CharSequence {


    protected static final long serialVersionUID = 823987612L;


    private String[] values;
    private int[] offsets;
    private int size;

    private String aggregated = null;



    public AggregatedString(final int initialCapacityInItems) {

        super();

        if (initialCapacityInItems <= 0) {
            throw new IllegalArgumentException("Initial capacity (items) must be greater than zero");
        }

        this.values = new String[initialCapacityInItems];
        this.offsets = new int[initialCapacityInItems];
        this.size = 0;

    }



    public void append(final String str) {

        // no need to check nullity - will raise a NullPointerException

        ensureCapacity();

        this.values[this.size] = str;
        this.offsets[this.size] =
                (this.size == 0? 0 : this.offsets[this.size - 1] + this.values[this.size - 1].length());

        this.size++;

    }




    public int length() {
        if (this.size == 0) {
            return 0;
        }
        return this.offsets[this.size - 1] + this.values[this.size - 1].length();
    }




    public char charAt(final int index) {
        int n = this.size;
        while (n-- != 0 && this.offsets[n] > index);
        return this.values[++n].charAt((index - this.offsets[n]));
    }




    public CharSequence subSequence(final int start, final int end) {
        if (start > end) {
            throw new IllegalArgumentException("Bad (start,end) indexes specified: " + start + "," + end);
        }
        int n1 = this.size;
        while (n1-- != 0 && this.offsets[n1] > end);
        int n0 = ++n1;
        while (n0-- != 0 && this.offsets[n0] > start);
        if (++n0 == n1) {
            return this.values[n0].substring((start - this.offsets[n0]), (end - this.offsets[n0]));
        }
        final char[] subseqchars = new char[end - start];
        int subseqcharsOffset = 0;
        int nx = n0;
        while (nx < n1) {
            final int nstart = Math.max(start, this.offsets[nx]) - this.offsets[nx];
            final int nend = Math.min(end, this.offsets[nx] + this.values[nx].length()) - this.offsets[nx];
            this.values[nx].getChars(nstart, nend, subseqchars, subseqcharsOffset);
            subseqcharsOffset += (nend - nstart);
            nx++;
        }
        return new String(subseqchars);
    }



    private void ensureCapacity() {
        if (this.size == this.values.length) {
            final String[] newData = new String[this.values.length + Math.max(5, this.values.length / 3)];
            final int[] newDataoffset = new int[this.offsets.length + Math.max(5, this.offsets.length / 3)];
            System.arraycopy(this.values, 0, newData, 0, this.values.length);
            System.arraycopy(this.offsets, 0, newDataoffset, 0, this.offsets.length);
            this.values = newData;
            this.offsets = newDataoffset;
        }
    }


    @Override
    public String toString() {
        if (this.aggregated != null) {
            return this.aggregated;
        }
        if (this.size == 0) {
            this.aggregated = "";
        } else if (this.size == 1) {
            this.aggregated = this.values[0];
        } else {
            final char[] seqchars = new char[length()];
            for (int i = 0; i < this.size; i++) {
                this.values[i].getChars(0, this.values[i].length(), seqchars, this.offsets[i]);
            }
            this.aggregated = new String(seqchars);
        }
        return this.aggregated;
    }



    // TODO Turn this into a test when thymeleaf-tests compiles again
    public static void main(String[] args) {

        try {

            final String text = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            final int textLen = text.length();

            for (int initialCapacity = 1; initialCapacity < 10; initialCapacity++) {

                final AggregatedString as = new AggregatedString(initialCapacity);

                for (int textx = 0; textx <= textLen; textx++) {

                    for (int texty = 0; textx + texty <= textLen; texty++) {

                        final String textStr0 = new String(text.substring(0, textx));
                        final String textStr1 = new String(text.substring(textx, (textx + texty)));
                        final String textStr2 = new String(text.substring((textx + texty), textLen));

                        as.append(textStr0);
                        as.append(textStr1);
                        as.append(textStr2);

                        final String asText = as.toString();
                        assertEquals(text, asText);
                        assertSame(asText, as.toString());

                    }

                }

            }

        } catch (final Throwable e) {
            e.printStackTrace(System.err);
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


    private static void assertSame(final String expected, final String actual) {
        if (expected == null) {
            if (actual != null) {
                throw new RuntimeException("Expected '" + expected + "' is not the same as actual '" + actual + "'");
            }
        } else if (actual == null) {
            throw new RuntimeException("Expected '" + expected + "' is not the same as actual '" + actual + "'");
        } else {
            if (!expected.equals(actual)) {
                throw new RuntimeException("Expected '" + expected + "' is not the same as actual '" + actual + "'");
            }
        }
    }


}
