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
package org.thymeleaf.templateparser.markup;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class ThymeleafMarkupTemplateReader extends Reader {

    private final static char[] PROTOTYPE_ONLY_COMMENT_PREFIX = "<!--/*/".toCharArray();
    private final static char[] PROTOTYPE_ONLY_COMMENT_SUFFIX = "/*/-->".toCharArray();

    private final Reader reader;

    private char[] overflowBuffer = null;
    private int overflowBufferLen = 0;

    private boolean insideComment = false;
    private int index = 0;


    public ThymeleafMarkupTemplateReader(final Reader reader) {
        super();
        this.reader = reader;
    }





    @Override
    public int read(final char[] cbuf, final int off, final int len) throws IOException {

        int read;

        if (this.overflowBufferLen == 0) {

            // Base case: we don't have overflow, so we just delegate on the delegate reader
            read = this.reader.read(cbuf, off, len);

        } else {

            if (this.overflowBufferLen < len) {

                // Our overflow fits in the cbuf len, so we copy and ask the delegate reader to write from there
                System.arraycopy(this.overflowBuffer, 0, cbuf, off, this.overflowBufferLen);
                read = this.overflowBufferLen;
                final int delegateRead =
                        this.reader.read(cbuf, (off + this.overflowBufferLen), (len - this.overflowBufferLen));
                if (delegateRead > 0) {
                    read += delegateRead;
                }
                this.overflowBufferLen = 0;

            } else {

                System.arraycopy(this.overflowBuffer, 0, cbuf, off, len);
                read = len;
                this.overflowBufferLen -= len;
                // Nothing to check - if we are here we know there's nothing to remove in the overflow buffer
                return read;

            }

        }


        if (read > 0) {

            int maxi = off + read;

            char c;
            int i = 0;
            while (i < maxi) { // we'll go backwards because that way removals will not affect iteration

                c = cbuf[i++];

                if (this.index == 0 && c != '<' && c != '/') {
                    // Shortcut for most characters in a template: no further tests to be done
                    continue;
                }

                if (!this.insideComment) {

                    if (c == PROTOTYPE_ONLY_COMMENT_PREFIX[this.index]) {
                        this.index++;
                        if (this.index == PROTOTYPE_ONLY_COMMENT_PREFIX.length) {
                            // Remove the prefix, as if it was never there...
                            if (i < maxi) {
                                System.arraycopy(cbuf, i, cbuf, i - PROTOTYPE_ONLY_COMMENT_PREFIX.length, (maxi - i));
                            }
                            this.insideComment = true;
                            this.index = 0;
                            read -= PROTOTYPE_ONLY_COMMENT_PREFIX.length;
                            maxi -= PROTOTYPE_ONLY_COMMENT_PREFIX.length;
                            i -= PROTOTYPE_ONLY_COMMENT_PREFIX.length;
                        }
                    } else {
                        this.index = 0;
                    }

                } else {

                    if (c == PROTOTYPE_ONLY_COMMENT_SUFFIX[this.index]) {
                        this.index++;
                        if (this.index == PROTOTYPE_ONLY_COMMENT_SUFFIX.length) {
                            // Remove the suffix, as if it was never there...
                            if (i < maxi) {
                                System.arraycopy(cbuf, i, cbuf, i - PROTOTYPE_ONLY_COMMENT_SUFFIX.length, (maxi - i));
                            }
                            this.insideComment = false;
                            this.index = 0;
                            read -= PROTOTYPE_ONLY_COMMENT_SUFFIX.length;
                            maxi -= PROTOTYPE_ONLY_COMMENT_SUFFIX.length;
                            i -= PROTOTYPE_ONLY_COMMENT_SUFFIX.length;
                        }
                    } else {
                        this.index = 0;
                    }

                }

            }

            if (this.index > 0) {
                // Oops, the buffer ended in something that could be a structure to be removed -- will need some more processing

                // First step is to copy the contents we doubt about to the overflow buffer and subtract them from cbuf
                if (this.overflowBuffer == null) {
                    this.overflowBuffer =
                            new char[Math.max(PROTOTYPE_ONLY_COMMENT_PREFIX.length, PROTOTYPE_ONLY_COMMENT_SUFFIX.length)];
                }
                this.overflowBufferLen = this.index;
                System.arraycopy(cbuf, maxi - this.overflowBufferLen, this.overflowBuffer, 0, this.overflowBufferLen);
                read -= this.overflowBufferLen;

                // Second step is trying to complete the overflow buffer in order to make a decision on whether we are
                // really looking at a removable structure here or not...
                final int requiredLen = this.insideComment? PROTOTYPE_ONLY_COMMENT_SUFFIX.length : PROTOTYPE_ONLY_COMMENT_PREFIX.length;
                while (this.overflowBufferLen < requiredLen) {
                    final int overflowRead =
                            this.reader.read(this.overflowBuffer, this.overflowBufferLen, (requiredLen - this.overflowBufferLen));
                    if (overflowRead < 0) {
                        // we reached the end of the stream!
                        break;
                    } else {
                        this.overflowBufferLen += overflowRead;
                    }
                }

                // Third step is check whether what comes after is a removable structure or not. If it is, just ignore it
                boolean matches =
                    this.insideComment?
                        isArrayEquals(PROTOTYPE_ONLY_COMMENT_SUFFIX, this.overflowBuffer, 0, this.overflowBufferLen) :
                        isArrayEquals(PROTOTYPE_ONLY_COMMENT_PREFIX, this.overflowBuffer, 0, this.overflowBufferLen);
                if (matches) {
                    this.insideComment = !this.insideComment;
                    this.overflowBufferLen = 0;
                }
                // If it doesn't match, we just leave it in the overflow buffer for processing it afterwards

                this.index = 0;

            }

        }

        return read;

    }


    private static boolean isArrayEquals(final char[] target, final char[] array, final int offset, final int len) {
        if (array.length == len && offset == 0) {
            return Arrays.equals(target, array);
        }
        if (target.length != len) {
            return false;
        }
        int n = target.length;
        while (n-- != 0) {
            if (target[n] != array[offset + n]) {
                return false;
            }
        }
        return true;
    }



    @Override
    public void close() throws IOException {
        this.reader.close();
    }




}
