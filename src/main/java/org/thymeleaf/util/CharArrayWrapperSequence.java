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





/**
 * <p>
 *   Wrapper class that allows the use of char[] objects as {@link CharSequence}s, 
 *   without the need to duplicate the char[] contents in memory (as would happen
 *   if a String was created from the char[]).
 * </p>
 * <p>
 *   Note that a reference to the original char[] is kept, so modifying this char[]
 *   outside this object will result in this object's contents being modified too.
 * </p>
 * <p>
 *   Objects of this class are <b>thread-safe</b>.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.15
 *
 */
public final class CharArrayWrapperSequence implements CharSequence, Cloneable {

    private final char[] buffer;
    private final int offset;
    private final int len;
    

    
    public CharArrayWrapperSequence(final char[] array) {
        this(array, 0, (array != null? array.length : -1));
    }
    
    
    public CharArrayWrapperSequence(final char[] buffer, final int offset, final int len) {
        
        super();
        
        if (buffer == null) {
            throw new IllegalArgumentException("Buffer cannot be null");
        }
        
        if (offset < 0 || offset >= buffer.length) {
            throw new IllegalArgumentException(offset + " is not a valid offset for buffer (size: " + buffer.length + ")");
        }
        
        if ((offset + len) > buffer.length) {
            throw new IllegalArgumentException(len + " is not a valid length for buffer using offset " + offset + " (size: " + buffer.length + ")");
        }
        
        this.buffer = buffer;
        this.offset = offset;
        this.len = len;
        
    }
    

    
    public char charAt(final int index) {
        if (index < 0 || index >= this.len) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return this.buffer[index + this.offset];
    }

    
    
    public int length() {
        return this.len;
    }

    
    
    public CharSequence subSequence(final int start, final int end) {
        if (start < 0 || start >= this.len) {
            throw new ArrayIndexOutOfBoundsException(start);
        }
        if (end > this.len) {
            throw new ArrayIndexOutOfBoundsException(end);
        }
        return new CharArrayWrapperSequence(this.buffer, (this.offset + start), (end - start));
    }

    
    
    @Override
    protected CharArrayWrapperSequence clone() throws CloneNotSupportedException {
        return (CharArrayWrapperSequence) super.clone();
    }

    

    @Override
    public int hashCode() {
        /*
         * This implementation is compatible with java.lang.String#hashCode(),
         * even if equals(obj) cannot be because java.lang.String requires
         * the obj to be an instance of String.
         */
        if (this.len == 0) {
            return 0;
        }
        final int prime = 31;
        int result = 0;
        final int maxi = this.offset + this.len;
        for (int i = this.offset; i < maxi; i++) {
            result = prime * result + (int) this.buffer[i];
        }
        return result;
    }



    @Override
    public boolean equals(final Object obj) {
        /*
         * This implementation works in the same way as java.lang.String#equals(obj),
         * but is not compatible because java.lang.String#equals(obj) requires obj
         * to be an instance of String.
         */
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof CharArrayWrapperSequence) {
            final CharArrayWrapperSequence other = (CharArrayWrapperSequence) obj;
            if (this.len != other.len) {
                return false;
            }
            for (int i = 0; i < this.len; i++) {
                if (this.buffer[i + this.offset] != other.buffer[i + other.offset]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }


    
    @Override
    public String toString() {
        return new String(this.buffer, this.offset, this.len);
    }
    
    
    
}