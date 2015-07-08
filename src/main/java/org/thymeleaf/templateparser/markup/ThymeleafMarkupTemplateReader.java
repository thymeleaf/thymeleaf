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
    private final static char[] PARSER_LEVEL_COMMENT_PREFIX = "<!--/*".toCharArray();
    private final static char[] PARSER_LEVEL_COMMENT_SUFFIX = "*/-->".toCharArray();

    private final static int PREFIX_MAX_SIZE = Math.max(PROTOTYPE_ONLY_COMMENT_PREFIX.length, PARSER_LEVEL_COMMENT_PREFIX.length);
    private final static int SUFFIX_MAX_SIZE = Math.max(PROTOTYPE_ONLY_COMMENT_SUFFIX.length, PARSER_LEVEL_COMMENT_SUFFIX.length);
    private final static int STRUCTURE_MAX_SIZE = Math.max(PREFIX_MAX_SIZE, SUFFIX_MAX_SIZE);

    
    private final Reader reader;

    private char[] overflowBuffer = null;
    private int overflowBufferLen = 0;

    private boolean insideComment = false;
    private int discardFrom = -1;
    private int index = 0;


    public ThymeleafMarkupTemplateReader(final Reader reader) {
        super();
        this.reader = reader;
    }





    @Override
    public int read(final char[] cbuf, final int off, final int len) throws IOException {

        int read;

        /*
         * First step will be checking whether we can output all we need from the overflow
         */

        if (this.overflowBufferLen == 0) {

            // Base case: we don't have overflow, so we just delegate on the delegate reader
            read = this.reader.read(cbuf, off, len);

        } else {

            if (this.overflowBufferLen >= len) {
                // At first sight, it seems we can extract everything we need from the overflow buffer. Let's try

                if ()

            }

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


        /*
         * Once we have checked whether we could get our results from the overflow buffer, and we have completed
         * the cbuf buffer with results coming from overflow and from a read to the delegate, we can process what
         * we have got into cbuf
         */

        if (read > 0) {

            int maxi = off + read;

            char c;
            int i = off;
            while (i < maxi) {

                c = cbuf[i++];

                if (this.index == 0 && c != '<' && c != '/' && c != '*') {
                    // Shortcut for most characters in a template: no further tests to be done if the character coming
                    // is not one of those that can start a recognizable sequence
                    continue;
                }

                if (!this.insideComment) {

                    if (c == PROTOTYPE_ONLY_COMMENT_PREFIX[this.index]) {
                        this.index++;
                        if (this.index == PROTOTYPE_ONLY_COMMENT_PREFIX.length) {
                            // It's a full prototype-only comment prefix, so remove the prefix, as if it was never there...
                            if (i < maxi) {
                                System.arraycopy(cbuf, i, cbuf, i - PROTOTYPE_ONLY_COMMENT_PREFIX.length, (maxi - i));
                            }
                            this.insideComment = true;
                            this.index = 0;
                            read -= PROTOTYPE_ONLY_COMMENT_PREFIX.length;
                            maxi -= PROTOTYPE_ONLY_COMMENT_PREFIX.length;
                            i -= PROTOTYPE_ONLY_COMMENT_PREFIX.length;
                        }
                    } else if (this.index == PARSER_LEVEL_COMMENT_PREFIX.length){
                        // Given we know the parser-level comment prefix matches almost exactly the prototype only
                        // comment prefix (except the last char), we know that in this case what we have is a parser-level
                        // prefix, so we simply have to remove the entire block until we find the suffix
                        this.discardFrom = ((i - 1) - PARSER_LEVEL_COMMENT_PREFIX.length);
                        this.insideComment = true;
                        this.index = 0;
                        i--; // we need to back one position so that we process this character again knowing we are in a comment
                    } else {
                        this.index = 0;
                    }

                } else {

                    if (this.discardFrom < 0 && c == PROTOTYPE_ONLY_COMMENT_SUFFIX[this.index]) {
                        this.index++;
                        if (this.index == PROTOTYPE_ONLY_COMMENT_SUFFIX.length) {
                            // This is a suffix for a prototype-only block. Remove the suffix, as if it was never there...
                            if (i < maxi) {
                                System.arraycopy(cbuf, i, cbuf, i - PROTOTYPE_ONLY_COMMENT_SUFFIX.length, (maxi - i));
                            }
                            this.insideComment = false;
                            this.index = 0;
                            read -= PROTOTYPE_ONLY_COMMENT_SUFFIX.length;
                            maxi -= PROTOTYPE_ONLY_COMMENT_SUFFIX.length;
                            i -= PROTOTYPE_ONLY_COMMENT_SUFFIX.length;
                        }
                    } else if (this.discardFrom >= 0 && c == PARSER_LEVEL_COMMENT_SUFFIX[this.index]) {
                        if (this.index == 0 && (i - 2 >= off)) {
                            // Special case just in order to avoid trying to close a parser-level comment with a prototype-only suffix
                            final char cprev = cbuf[i - 2];
                            if (cprev == PROTOTYPE_ONLY_COMMENT_SUFFIX[0]) {
                                // It's a prototype-only comment suffix, we should not allow it to close our comment
                                continue;
                            }
                        }
                        this.index++;
                        if (this.index == PARSER_LEVEL_COMMENT_SUFFIX.length) {
                            // We have just closed a parser-level comment block
                            System.arraycopy(cbuf, i, cbuf, this.discardFrom, (maxi - i));
                            read -= (i - this.discardFrom);
                            maxi -= (i - this.discardFrom);
                            i = this.discardFrom;
                            this.discardFrom = -1;
                            this.insideComment = false;
                            this.index = 0;
                        }
                    } else {
                        this.index = 0;
                    }

                }

            }


            /*
             * Before handling possible overflow of unfinished structures, we need to save the value indicating whether
             * we actually need to trim the results before returning (because we are in a parser-level comment block
             * that has not been closed yet). This is done here because during overflow-handling, these values
             * might get changed in preparation for the next iteration to adequately use the overflow buffer.
             */
            final int shouldDiscardFrom = (this.insideComment && this.discardFrom >= 0 ? this.discardFrom : -1);


            /*
             * Now we should take care of the possibility that the buffer ended in an unfinished structure. If that
             * is the case, we will try to read the remaining part of the structure in order to determine what to
             * do about it before leaving (or not) the overflow buffer prepared for the next iteration.
             */
            if (this.index > 0) {
                // Oops, the buffer ended in something that could be a structure to be removed -- will need some more processing

                // First step is to copy the contents we doubt about to the overflow buffer and subtract them from cbuf
                if (this.overflowBuffer == null) {
                    // Using only the prototype-only comment structures will do, as they are longer
                    this.overflowBuffer = new char[STRUCTURE_MAX_SIZE];
                }


                this.overflowBufferLen = this.index;
                System.arraycopy(cbuf, maxi - this.overflowBufferLen, this.overflowBuffer, 0, this.overflowBufferLen);
                read -= this.overflowBufferLen;
                maxi -= this.overflowBufferLen;

                // Second step is trying to complete the overflow buffer in order to make a decision on whether we are
                // really looking at a removable structure here or not...
                while (this.overflowBufferLen < this.overflowBuffer.length) {
                    final int overflowRead =
                            this.reader.read(this.overflowBuffer, this.overflowBufferLen, (this.overflowBuffer.length - this.overflowBufferLen));
                    if (overflowRead < 0) {
                        // we reached the end of the stream!
                        break;
                    } else {
                        this.overflowBufferLen += overflowRead;
                    }
                }

                // Third step is check whether what comes after is a removable structure or not. If it is, just ignore it
                // NOTE we won't be modifying the "read" value here, as the actions taken here will affect the overflow buffer only
                if (!this.insideComment) {

                    if (isArrayEquals(PROTOTYPE_ONLY_COMMENT_PREFIX, this.overflowBuffer, 0, Math.min(this.overflowBufferLen, PROTOTYPE_ONLY_COMMENT_PREFIX.length))) {

                        // OK, this was a prototype-only comment prefix, so we just remove it and go on, setting the insideComment flag to true

                        this.insideComment = true;
                        this.overflowBufferLen -= PROTOTYPE_ONLY_COMMENT_PREFIX.length;
                        if (this.overflowBufferLen > 0) {
                            System.arraycopy(this.overflowBuffer, PROTOTYPE_ONLY_COMMENT_PREFIX.length, this.overflowBuffer, 0, this.overflowBufferLen);
                        }

                    } else if (isArrayEquals(PARSER_LEVEL_COMMENT_PREFIX, this.overflowBuffer, 0, Math.min(this.overflowBufferLen, PARSER_LEVEL_COMMENT_PREFIX.length))) {

                        // A parser-level comment block is just starting. We will remove the prefix and set the insideComment and
                        // discardFrom flags, letting normal processing handling the discarding of the content and also the finding
                        // of the suffix

                        this.insideComment = true;
                        this.overflowBufferLen -= PARSER_LEVEL_COMMENT_PREFIX.length;
                        if (this.overflowBufferLen > 0) {
                            System.arraycopy(this.overflowBuffer, PARSER_LEVEL_COMMENT_PREFIX.length, this.overflowBuffer, 0, this.overflowBufferLen);
                        }
                        this.discardFrom = 0;

                    }

                } else {

                    if (this.discardFrom < 0 && isArrayEquals(PROTOTYPE_ONLY_COMMENT_SUFFIX, this.overflowBuffer, 0, Math.min(this.overflowBufferLen, PROTOTYPE_ONLY_COMMENT_SUFFIX.length))) {

                        // We found a suffix for a prototype-only comment block, just remove it and go on, just the same as with the prefix

                        this.insideComment = false;
                        this.overflowBufferLen -= PROTOTYPE_ONLY_COMMENT_SUFFIX.length;
                        if (this.overflowBufferLen > 0) {
                            System.arraycopy(this.overflowBuffer, PROTOTYPE_ONLY_COMMENT_SUFFIX.length, this.overflowBuffer, 0, this.overflowBufferLen);
                        }

                    } else if (this.discardFrom >= 0 && isArrayEquals(PARSER_LEVEL_COMMENT_SUFFIX, this.overflowBuffer, 0, Math.min(this.overflowBufferLen, PARSER_LEVEL_COMMENT_SUFFIX.length))) {

                        // We found the suffix closing a parser-level comment block, so we will remove it and return
                        // the flags to their usual state

                        this.insideComment = false;
                        this.discardFrom = -1;
                        this.overflowBufferLen -= PARSER_LEVEL_COMMENT_SUFFIX.length;
                        if (this.overflowBufferLen > 0) {
                            System.arraycopy(this.overflowBuffer, PARSER_LEVEL_COMMENT_SUFFIX.length, this.overflowBuffer, 0, this.overflowBufferLen);
                        }

                    }

                }

                this.index = 0;

            }

            // Once the overflow has been worked out, we need to check whether the amount of returned characters should
            // be affected by the fact that we might be in a parser-level comment that has not been closed yet
            if (shouldDiscardFrom >= 0) {
                read -= (maxi - shouldDiscardFrom);
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
