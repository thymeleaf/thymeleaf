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

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.List;


/**
 * <p>
 *   Character sequence that aggregates one or several {@link CharSequence} objects, without the need to clone them
 *   or convert them to String.
 * </p>
 * <p>
 *   Special implementation of the {@link CharSequence} interface that can replace {@link String} objects
 *   wherever a specific text literal is composed of several parts and we want to avoid creating new
 *   {@link String} objects for them, using instead objects of this class that simply keep an array
 *   of references to the original CharSequences.
 * </p>
 * <p>
 *   Note that any mutable {@link CharSequence} implementations used to build objects of this class should
 *   <strong>never</strong> be modified after the creation of the aggregated object.
 * </p>
 *
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
public final class AggregateCharSequence implements Serializable, IWritableCharSequence {


    protected static final long serialVersionUID = 823987612L;

    private static final int[] UNIQUE_ZERO_OFFSET = new int[] { 0 };

    private final CharSequence[] values;
    private final int[] offsets;
    private final int length;

    // This variable will mimic the hashCode cache mechanism in java.lang.String
    private int hash; // defaults to 0




    public AggregateCharSequence(final CharSequence component) {

        super();

        if (component == null) {
            throw new IllegalArgumentException("Component argument is null, which is forbidden");
        }

        this.values = new CharSequence[] { component };
        this.offsets = UNIQUE_ZERO_OFFSET;
        this.length = component.length();

    }


    public AggregateCharSequence(final CharSequence component0, final CharSequence component1) {

        super();

        if (component0 == null || component1 == null) {
            throw new IllegalArgumentException("At least one component argument is null, which is forbidden");
        }

        this.values = new CharSequence[] { component0, component1 };
        this.offsets = new int[] { 0, component0.length() };
        this.length = this.offsets[1] + component1.length();

    }


    public AggregateCharSequence(final CharSequence component0, final CharSequence component1, final CharSequence component2) {

        super();

        if (component0 == null || component1 == null || component2 == null) {
            throw new IllegalArgumentException("At least one component argument is null, which is forbidden");
        }

        this.values = new CharSequence[] { component0, component1, component2 };
        this.offsets = new int[] { 0, component0.length(), component0.length() + component1.length() };
        this.length = this.offsets[2] + component2.length();

    }


    public AggregateCharSequence(final CharSequence component0, final CharSequence component1, final CharSequence component2, final CharSequence component3) {

        super();

        if (component0 == null || component1 == null || component2 == null || component3 == null) {
            throw new IllegalArgumentException("At least one component argument is null, which is forbidden");
        }

        this.values = new CharSequence[] { component0, component1, component2, component3 };
        this.offsets = new int[] { 0, component0.length(), component0.length() + component1.length(), component0.length() + component1.length() + component2.length() };
        this.length = this.offsets[3] + component3.length();

    }


    public AggregateCharSequence(final CharSequence component0, final CharSequence component1, final CharSequence component2, final CharSequence component3, final CharSequence component4) {

        super();

        if (component0 == null || component1 == null || component2 == null || component3 == null || component4 == null) {
            throw new IllegalArgumentException("At least one component argument is null, which is forbidden");
        }

        this.values = new CharSequence[] { component0, component1, component2, component3, component4 };
        this.offsets = new int[] { 0, component0.length(), component0.length() + component1.length(), component0.length() + component1.length() + component2.length(), component0.length() + component1.length() + component2.length() + component3.length() };
        this.length = this.offsets[4] + component3.length();

    }



    public AggregateCharSequence(final CharSequence[] components) {

        // NOTE: We have this set of constructors instead of only one with a varargs argument in order to
        // avoid unnecessary creation of String[] objects

        super();

        if (components == null) {
            throw new IllegalArgumentException("Components argument array cannot be null");
        }

        if (components.length == 0) {

            // We want always at least one String
            this.values = new CharSequence[]{""};
            this.offsets = UNIQUE_ZERO_OFFSET;
            this.length = 0;

        } else {

            this.values = new CharSequence[components.length];
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



    public AggregateCharSequence(final List<? extends CharSequence> components) {

        super();

        if (components == null) {
            throw new IllegalArgumentException("Components argument array cannot be null");
        }

        final int componentsSize = components.size();

        if (componentsSize == 0) {

            // We want always at least one String
            this.values = new CharSequence[]{""};
            this.offsets = UNIQUE_ZERO_OFFSET;
            this.length = 0;

        } else {

            this.values = new CharSequence[componentsSize];
            this.offsets = new int[componentsSize];

            int totalLength = 0;
            int i = 0;
            while (i < componentsSize) {
                final CharSequence element = components.get(i);
                if (element == null) {
                    throw new IllegalArgumentException("Components argument contains at least a null, which is forbidden");
                }
                final int componentLen = element.length();
                this.values[i] = element;
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
            // Shortcut: let the CharSequence#subSequence method do the job...
            return this.values[n0].subSequence((beginIndex - this.offsets[n0]), (endIndex - this.offsets[n0]));
        }
        final char[] chars = new char[endIndex - beginIndex];
        int charsOffset = 0;
        int nx = n0;
        while (nx <= n1) {
            final int nstart = Math.max(beginIndex, this.offsets[nx]) - this.offsets[nx];
            final int nend = Math.min(endIndex, this.offsets[nx] + this.values[nx].length()) - this.offsets[nx];
            copyChars(this.values[nx], nstart, nend, chars, charsOffset);
            charsOffset += (nend - nstart);
            nx++;
        }
        return new String(chars);

    }



    public void write(final Writer writer) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Writer cannot be null");
        }
        for (int i = 0; i < this.values.length; i++) {
            writer.write(this.values[i].toString());
        }
    }




    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof AggregateCharSequence)) {
            return false;
        }

        final AggregateCharSequence that = (AggregateCharSequence) o;

        if (this.values.length == 1 && that.values.length == 1) {
            if (this.values[0] instanceof String && that.values[0] instanceof String) {
                return this.values[0].equals(that.values[0]);
            }
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

            // Shortcut, in case we have to identical Strings ready to be compared with one another
            if (n1 == 0 && n2 == 0 && len1 == len2 && this.values[m1] instanceof String && that.values[m2] instanceof String) {
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
                final CharSequence[] vals = this.values;
                CharSequence val;
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
            if (this.values.length == 1 && this.values[0] instanceof String) {
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
            return this.values[0].toString();
        }
        final char[] chars = new char[this.length];
        for (int i = 0; i < this.values.length; i++) {
            copyChars(this.values[i], 0, this.values[i].length(), chars, this.offsets[i]);
        }
        return new String(chars);
    }





    private static void copyChars(
            final CharSequence src, final int srcBegin, final int srcEnd, final char[] dst, final int dstBegin) {

        if (src instanceof String) {
            ((String)src).getChars(srcBegin, srcEnd, dst, dstBegin);
            return;
        }

        int i = srcBegin;
        while (i < srcEnd) {
            dst[dstBegin + (i - srcBegin)] = src.charAt(i);
            i++;
        }

    }



}
