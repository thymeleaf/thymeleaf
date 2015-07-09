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

    private boolean inputClosed = false;

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
            this.inputClosed = (read < 0);

        } else {

            if (this.overflowBufferLen >= len) {
                // At first sight, it seems we can extract everything we need from the overflow buffer. Let's try

                // TODO What to do in this case?

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
                this.inputClosed = (delegateRead < 0);
                this.overflowBufferLen = 0;

            } else {

                System.arraycopy(this.overflowBuffer, 0, cbuf, off, len);
                read = len;
                this.overflowBufferLen -= len;
                // Nothing to check - if we are here we know there's nothing to remove in the overflow buffer
                return read;

            }

        }


        if (read == 0) {
            return 0;
        }


        /*
         * Once we have checked whether we could get our results from the overflow buffer, and we have completed
         * the cbuf buffer with results coming from overflow and from a read to the delegate, we can process what
         * we have got into cbuf
         */
        read = processReadBuffer(cbuf, off, read);
        if (this.index == 0 && (!this.insideComment || this.discardFrom < 0)) {
            return read;
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
            // Oops, the buffer ended in something that could be a structure to be removed -- will need some
            // more processing, using the overflow buffer this time

            // First step is to copy the contents we doubt about to the overflow buffer and subtract them from cbuf
            if (this.overflowBuffer == null) {
                // Using only the prototype-only comment structures will do, as they are longer
                this.overflowBuffer = new char[STRUCTURE_MAX_SIZE];
            }
            this.overflowBufferLen = this.index;
            System.arraycopy(cbuf, (off + read) - this.overflowBufferLen, this.overflowBuffer, 0, this.overflowBufferLen);
            read -= this.index;

            // Second step is trying to complete the overflow buffer in order to make a decision on whether we are
            // really looking at a removable structure here or not...
            fillUpOverflow();

            // Now we process the overflow buffer just as we processed the original buffer. This will modify the
            // state flags accordingly (index, insideComment, discardFrom...)
            final int overflowRead = processReadBuffer(this.overflowBuffer, 0, this.overflowBufferLen);


            /*
             * Possibilities:
             *    - The unfinished candidate was a structure -> has been removed (overflowRead < this.overflowBufferLen)
             *       - index == 0 -> just go, overflow prepared
             *       - index > 0 -> fillUpOverflow, then check again starting from off = (overflowRead - this.index)
             *    - The unfinished candidate was not a structure -> has not been removed (overflowRead == this.overflowBufferlen)
             *       - index == 0 -> just go, overflow prepared
             *       - index > 0 -> there are some characters that can be consumed by next read, but we have no more place for more characters and check if the new unfinished structure is such thing!!
             */



            if (overflowRead < this.overflowBufferLen) {
                // The index actually signaled to an unfinished structure, that has been just removed

                if (this.index > 0) {



                } else if (this.discardFrom >= 0) {

                }


            }


            this.index = 0;

        }

        // Once the overflow has been worked out, we need to check whether the amount of returned characters should
        // be affected by the fact that we might be in a parser-level comment that has not been closed yet
        if (shouldDiscardFrom >= 0) {
            read -= ((off + read) - shouldDiscardFrom);
        }

        return read;

    }




    private int processReadBuffer(final char[] buffer, final int off, final int len) {

        if (len == 0) {
            return 0;
        }

        int read = len;
        int maxi = off + len;

        boolean foundPrototypeOnlyPrefix = false;
        boolean foundParserLevelPrefix = false;

        char c;
        int i = off;
        while (i < maxi) {

            c = buffer[i++];

            if (this.index == 0 && c != '<' && c != '/' && c != '*') {
                // Shortcut for most characters in a template: no further tests to be done if the character coming
                // is not one of those that can start a recognizable sequence
                continue;
            }

            if (!this.insideComment) {

                if (c == PROTOTYPE_ONLY_COMMENT_PREFIX[this.index]) {
                    this.index++;
                    if (this.index == PROTOTYPE_ONLY_COMMENT_PREFIX.length) {
                        foundPrototypeOnlyPrefix = true;
                    } else if (this.index == PARSER_LEVEL_COMMENT_PREFIX.length && i >= maxi && this.inputClosed) {
                        // it's the last character in the whole input, so we should not have hope for a prototype-only comment prefix
                        foundParserLevelPrefix = true;
                    }
                } else if (this.index == PARSER_LEVEL_COMMENT_PREFIX.length) {
                    foundParserLevelPrefix = true;
                    i--; // whatever we do, we will need to process this character again, so better back the index one position
                }

                if (foundPrototypeOnlyPrefix) {
                    // It's a full prototype-only comment prefix, so remove the prefix, as if it was never there...
                    if (i < maxi) {
                        System.arraycopy(buffer, i, buffer, i - PROTOTYPE_ONLY_COMMENT_PREFIX.length, (maxi - i));
                    }
                    this.insideComment = true;
                    this.index = 0;
                    read -= PROTOTYPE_ONLY_COMMENT_PREFIX.length;
                    maxi -= PROTOTYPE_ONLY_COMMENT_PREFIX.length;
                    i -= PROTOTYPE_ONLY_COMMENT_PREFIX.length;
                    foundPrototypeOnlyPrefix = false;
                } else if (foundParserLevelPrefix) {
                    // Given we know the parser-level comment prefix matches almost exactly the prototype only
                    // comment prefix (except the last char), we know that in this case what we have is a parser-level
                    // prefix, so we will simply have to remove the entire block until we find the suffix. But first,
                    // we will be removing the prefix itself in order to make room (every identified structure should
                    // be immediately removed)
                    System.arraycopy(buffer, i, buffer, i - PARSER_LEVEL_COMMENT_PREFIX.length, (maxi - i));
                    this.insideComment = true;
                    this.index = 0;
                    read -= PARSER_LEVEL_COMMENT_PREFIX.length;
                    maxi -= PARSER_LEVEL_COMMENT_PREFIX.length;
                    i -= PARSER_LEVEL_COMMENT_PREFIX.length ;
                    this.discardFrom = i - PARSER_LEVEL_COMMENT_PREFIX.length;
                    foundParserLevelPrefix = false;
                } else {
                    this.index = 0;
                }

            } else {

                if (this.discardFrom < 0 && c == PROTOTYPE_ONLY_COMMENT_SUFFIX[this.index]) {
                    this.index++;
                    if (this.index == PROTOTYPE_ONLY_COMMENT_SUFFIX.length) {
                        // This is a suffix for a prototype-only block. Remove the suffix, as if it was never there...
                        if (i < maxi) {
                            System.arraycopy(buffer, i, buffer, i - PROTOTYPE_ONLY_COMMENT_SUFFIX.length, (maxi - i));
                        }
                        this.insideComment = false;
                        this.index = 0;
                        read -= PROTOTYPE_ONLY_COMMENT_SUFFIX.length;
                        maxi -= PROTOTYPE_ONLY_COMMENT_SUFFIX.length;
                        i -= PROTOTYPE_ONLY_COMMENT_SUFFIX.length;
                    }
                } else if (this.discardFrom >= 0 && c == PARSER_LEVEL_COMMENT_SUFFIX[this.index]) {
                    if (this.index == 0 && (i - 2 >= off) && (i - 2 >= this.discardFrom)) {
                        // Special case just in order to avoid trying to close a parser-level comment with a prototype-only suffix
                        if (buffer[i - 2] == PROTOTYPE_ONLY_COMMENT_SUFFIX[0]) {
                            // It's a prototype-only comment suffix, we should not allow it to close our comment
                            continue;
                        }
                    }
                    this.index++;
                    if (this.index == PARSER_LEVEL_COMMENT_SUFFIX.length) {
                        // We have just closed a parser-level comment block
                        if (i < maxi) {
                            System.arraycopy(buffer, i, buffer, this.discardFrom, (maxi - i));
                        }
                        this.insideComment = false;
                        this.index = 0;
                        read -= (i - this.discardFrom);
                        maxi -= (i - this.discardFrom);
                        i = this.discardFrom;
                        this.discardFrom = -1;
                    }
                } else {
                    this.index = 0;
                }

            }

        }

        return read;

    }



    private void fillUpOverflow() throws IOException {
        while (this.overflowBufferLen < this.overflowBuffer.length) {
            final int overflowRead =
                    this.reader.read(this.overflowBuffer, this.overflowBufferLen, (this.overflowBuffer.length - this.overflowBufferLen));
            if (overflowRead < 0) {
                // we reached the end of the stream!
                this.inputClosed = true;
                break;
            } else {
                this.overflowBufferLen += overflowRead;
            }
        }
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
