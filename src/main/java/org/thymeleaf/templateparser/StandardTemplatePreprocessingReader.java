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
package org.thymeleaf.templateparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.exceptions.TemplateInputException;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.7
 *
 */
public final class StandardTemplatePreprocessingReader extends Reader {


    private static final Logger readerLogger = LoggerFactory.getLogger(StandardTemplatePreprocessingReader.class);

    private static final int BUFFER_BLOCK_SIZE = 1024;
    private static final int OVERFLOW_BLOCK_SIZE = 2048;


    private static final char CHAR_OPTIONAL_WHITESPACE_WILDCARD = '\u0358';
    private static final char CHAR_WHITESPACE_WILDCARD = '\u0359';
    private static final char CHAR_ALPHANUMERIC_WILDCARD = '\u0360';
    private static final char CHAR_ANY_WILDCARD = '\u0361';


    private static final char[] LOWER_CHARS =
            ("[]<>!?=-_.,:;+*()&/%$\"'@#~^ \t\n\rabcdefghijklmnopqrstuvwxyz" +
            String.valueOf(CHAR_OPTIONAL_WHITESPACE_WILDCARD) + String.valueOf(CHAR_WHITESPACE_WILDCARD) +
            String.valueOf(CHAR_ALPHANUMERIC_WILDCARD) + String.valueOf(CHAR_ANY_WILDCARD)).toCharArray();
    private static final char[] UPPER_CHARS =
            ("[]<>!?=-_.,:;+*()&/%$\"'@#~^ \t\n\rABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            String.valueOf(CHAR_OPTIONAL_WHITESPACE_WILDCARD) + String.valueOf(CHAR_WHITESPACE_WILDCARD) +
            String.valueOf(CHAR_ALPHANUMERIC_WILDCARD) + String.valueOf(CHAR_ANY_WILDCARD)).toCharArray();



    private static final int[] COMMENT_START = convertToIndexes("<!--".toCharArray());
    private static final int[] COMMENT_END = convertToIndexes("-->".toCharArray());

    private static final int[] PROTOTYPE_ONLY_COMMENT_START = convertToIndexes("<!--/*/".toCharArray());
    private static final int[] PROTOTYPE_ONLY_COMMENT_END = convertToIndexes("/*/-->".toCharArray());

    private static final int[] PARSER_LEVEL_COMMENT_START = convertToIndexes("<!--/*".toCharArray());
    private static final int[] PARSER_LEVEL_COMMENT_END = convertToIndexes("*/-->".toCharArray());


    private final Reader innerReader;
    private final BufferedReader bufferedReader;

    private char[] buffer;
    private char[] overflow;
    private int overflowIndex;

    private boolean inComment = false;
    private boolean inParserLevelComment = false;

    private boolean noMoreToRead = false;





    private static int[] convertToIndexes(final char[] chars) {

        final int charsLen = chars.length;
        final int[] result = new int[charsLen];
        for (int i = 0; i < charsLen; i++) {
            final char c = chars[i];
            boolean found = false;
            for (int j = 0; !found && j < UPPER_CHARS.length; j++) {
                if (UPPER_CHARS[j] == c) {
                    result[i] = j;
                    found = true;
                }
            }
            for (int j = 0; !found && j < LOWER_CHARS.length; j++) {
                if (LOWER_CHARS[j] == c) {
                    result[i] = j;
                    found = true;
                }
            }
            if (!found) {
                throw new RuntimeException(
                        "Cannot convert to index character: '" + c + "' (value: " + ((int)c) + ")");
            }
        }
        return result;

    }



    /*
     *
     * TODO Add exceptions for not substituting anything inside [[...]]
     *
     */



    public StandardTemplatePreprocessingReader(final Reader in, final int bufferSize) {
        super();
        this.innerReader = in;
        this.bufferedReader = new BufferedReader(this.innerReader, bufferSize);
        this.buffer = new char[bufferSize + BUFFER_BLOCK_SIZE];
        this.overflow = new char[bufferSize + OVERFLOW_BLOCK_SIZE];
        this.overflowIndex = 0;
    }

    
    

    @Override
    public int read(final char[] cbuf, final int off, final int len) throws IOException {

        if (readerLogger.isTraceEnabled()) {
            readerLogger.trace("[THYMELEAF][TEMPLATEPREPROCESSINGREADER][{}] CALLING read(char[], {}, {})", 
                    new Object[] {TemplateEngine.threadIndex(),Integer.valueOf(off), Integer.valueOf(len)});
        }
        
        if ((len * 2) > this.overflow.length) {
            // Resize buffer and overflow
            
            this.buffer = new char[len + BUFFER_BLOCK_SIZE];
            final char[] newOverflow = new char[len + OVERFLOW_BLOCK_SIZE];
            System.arraycopy(this.overflow, 0, newOverflow, 0, this.overflowIndex);
            this.overflow = newOverflow;
        }
        
        
        int bufferSize = 0;
        if (this.overflowIndex > 0) {
            
            final int copied =
                copyToResult(
                        this.overflow, 0, this.overflowIndex, 
                        this.buffer, 0, this.buffer.length);
            
            bufferSize += copied;
            
            if (readerLogger.isTraceEnabled()) {
                readerLogger.trace("[THYMELEAF][TEMPLATEPREPROCESSINGREADER][{}] READ FROM OVERFLOW BUFFER {} Some content from the overflow buffer has been copied into results.", 
                        new Object[] {TemplateEngine.threadIndex(), Integer.valueOf(copied)});
            }
            
        }
        
        
        
        char[] overflowOverflow = null;
        if (this.overflowIndex > 0) {
            // Overflow did not entirely fit into buffer, and some overflow
            // had to be relocated. This overflow will have to be placed at the end of the
            // overflow generated in the current call (if any).
            
            overflowOverflow = new char[this.overflowIndex];
            System.arraycopy(this.overflow, 0, overflowOverflow, 0, this.overflowIndex);
            
            if (readerLogger.isTraceEnabled()) {
                readerLogger.trace("[THYMELEAF][TEMPLATEPREPROCESSINGREADER][{}] RELLOCATED SOME OVERFLOW CONTENTS, WAITING TO BE ADDED TO RESULT/NEW OVERFLOW {} Some content was remaining at the overflow buffer and will have to be rellocated.", 
                        new Object[] {TemplateEngine.threadIndex(), Integer.valueOf(this.overflowIndex)});
            }
            
            this.overflowIndex = 0;
            
        }

        
        
        if (!this.noMoreToRead && bufferSize < this.buffer.length) {
            // Buffer was not filled up with content from overflow, so ask for more content
            
            final int toBeRead = this.buffer.length - bufferSize;
            int reallyRead = this.bufferedReader.read(this.buffer, bufferSize, toBeRead);

            if (readerLogger.isTraceEnabled()) {
                readerLogger.trace("[THYMELEAF][TEMPLATEPREPROCESSINGREADER][{}] READ FROM SOURCE {} A read operation was executed on the source reader (max chars requested: {}).", 
                        new Object[] {TemplateEngine.threadIndex(), Integer.valueOf(reallyRead), Integer.valueOf(toBeRead)});
            }
            
            if (reallyRead < 0) {

                
                if (bufferSize == 0) {

                    
                    if (readerLogger.isTraceEnabled()) {
                        readerLogger.trace("[THYMELEAF][TEMPLATEPREPROCESSINGREADER][{}] RETURN {} After trying to read from input: No input left, no buffer left.", 
                                new Object[] {TemplateEngine.threadIndex(), Integer.valueOf(reallyRead)});
                    }
                    
                    return reallyRead;
                    
                }
                
                this.noMoreToRead = true;
                
            } else {
                
                bufferSize += reallyRead;
                
            }
            
        }

        
        
        if (this.noMoreToRead && bufferSize == 0) {
            // Nothing left to do. Just return -1

            if (readerLogger.isTraceEnabled()) {
                readerLogger.trace("[THYMELEAF][TEMPLATEPREPROCESSINGREADER][{}] RETURN -1 Reader was already marked to be finished. No more input, no more buffer.", 
                        new Object[] {TemplateEngine.threadIndex()});
            }
            
            return -1;
        }
        
        

        int totalRead = 0;
        int cbufi = off;
        final int last = off + len;
        
        int buffi = 0;
        while (cbufi < last && buffi < bufferSize) {


//            /*
//             * ------------------
//             * CHECK FOR PROTOTYPE-ONLY COMMENT BLOCKS
//             * ------------------
//             */
//
//            // Completely ignored if inside comment
//            final int matchedStartOfPrototypeOnlyComment =
//                    (this.inComment || this.inParserLevelComment?
//                            -2 : match(PROTOTYPE_ONLY_COMMENT_START, 0, PROTOTYPE_ONLY_COMMENT_START.length, this.buffer, buffi, bufferSize));
//
//            if (matchedStartOfPrototypeOnlyComment > 0) {
//
//                // no changes to "cbufi", as nothing is copied into result
//                // no changes to "totalRead", as nothing is copied into result
//                buffi += matchedStartOfPrototypeOnlyComment; // We just skip all characters in comment start
//                continue;
//
//            }
//
//
//            // Completely ignored if inside comment, in contrast with "normal" comment end
//            final int matchedEndOfPrototypeOnlyComment =
//                    (this.inComment || this.inParserLevelComment?
//                            -2 : match(PROTOTYPE_ONLY_COMMENT_END, 0, PROTOTYPE_ONLY_COMMENT_END.length, this.buffer, buffi, bufferSize));
//
//            if (matchedEndOfPrototypeOnlyComment > 0) {
//
//                // no changes to "cbufi", as nothing is copied into result
//                // no changes to "totalRead", as nothing is copied into result
//                buffi += matchedEndOfPrototypeOnlyComment; // We just skip all characters in comment end
//                continue;
//
//            }



//            /*
//             * ------------------
//             * CHECK FOR PARSER-LEVEL COMMENT BLOCKS
//             * ------------------
//             */
//
//            // Completely ignored if inside comment
//            final int matchedStartOfParserLevelComment =
//                    (this.inComment || this.inParserLevelComment?
//                            -2 : match(PARSER_LEVEL_COMMENT_START, 0, PARSER_LEVEL_COMMENT_START.length, this.buffer, buffi, bufferSize));
//
//            if (matchedStartOfParserLevelComment > 0) {
//
//                // no changes to "cbufi", as nothing is copied into result
//                // no changes to "totalRead", as nothing is copied into result
//                buffi += matchedStartOfParserLevelComment; // We skip all characters in comment start
//                this.inParserLevelComment = true;
//                continue;
//
//            }
//
//
//            // Completely ignored if inside comment, in contrast with "normal" comment end
//            final int matchedEndOfParserLevelComment =
//                    (this.inParserLevelComment ?
//                            match(PARSER_LEVEL_COMMENT_END, 0, PARSER_LEVEL_COMMENT_END.length, this.buffer, buffi, bufferSize) : -2);
//
//            if (matchedEndOfParserLevelComment > 0) {
//
//                // no changes to "cbufi", as nothing is copied into result
//                // no changes to "totalRead", as nothing is copied into result
//                buffi += matchedEndOfParserLevelComment; // We skip all characters in comment end
//                this.inParserLevelComment = false;
//                continue;
//
//            }



            /*
             * ------------------
             * CHECK FOR NORMAL COMMENT BLOCKS (which should change the "inComment" flag)
             * ------------------
             */

            final int matchedStartOfComment =
                    (this.inComment || this.inParserLevelComment?
                            -2 : match(COMMENT_START, 0, COMMENT_START.length, this.buffer, buffi, bufferSize));

            if (matchedStartOfComment > 0) {

                this.inComment = true;
                final int copied =
                        copyToResult(
                                this.buffer, buffi, matchedStartOfComment,
                                cbuf, cbufi, last);
                cbufi += copied;
                totalRead += copied;
                buffi += matchedStartOfComment;
                continue;

            }

            
            final int matchedEndOfComment = 
                    (this.inComment?
                            match(COMMENT_END, 0, COMMENT_END.length, this.buffer, buffi, bufferSize) : -2);
            
            if (matchedEndOfComment > 0) {
                
                this.inComment = false;
                final int copied =
                    copyToResult(
                            this.buffer, buffi, matchedEndOfComment, 
                            cbuf, cbufi, last);
                cbufi += copied;
                totalRead += copied;
                buffi += matchedEndOfComment;
                continue;
                
            }



            /*
             * ------------------
             * NOTHING ELSE TO CHECK, JUST COPY OUTPUT (if not in parser-level comment)
             * ------------------
             */

            if (!this.inParserLevelComment) {
                cbuf[cbufi++] = this.buffer[buffi++];
                totalRead++;
            } else {
                buffi++;
            }


        }

        
        if (buffi < bufferSize) {
            // Copy remaining buffer to overflow
            final int toBeOverFlowed = bufferSize - buffi;
            final int copied =
                copyToResult(
                        this.buffer, buffi, toBeOverFlowed, 
                        cbuf, cbufi, last);
            // copied must be zero
            if (copied != 0) {
                throw new TemplateInputException("Overflow was not correctly computed!");
            }
        }
        
        
        if (overflowOverflow != null) {
            
            final int copied =
                copyToResult(
                        overflowOverflow, 0, overflowOverflow.length, 
                        cbuf, cbufi, last);
            
            // copied must be zero
            if (copied != 0) {
                throw new TemplateInputException("Overflow-overflow was not correctly computed!");
            }
            
        }


        if (readerLogger.isTraceEnabled()) {
            final char[] result = new char[totalRead];
            System.arraycopy(cbuf, off, result, 0, totalRead);
            readerLogger.trace("[THYMELEAF][TEMPLATEPREPROCESSINGREADER][{}] RETURN {} Input was read and processed. Returning content: [[{}]]", 
                    new Object[] {TemplateEngine.threadIndex(), Integer.valueOf(totalRead), new String(result)});
        }
        

        return totalRead;
        
    }


    
    
    @Override
    public int read() throws IOException {

        if (readerLogger.isTraceEnabled()) {
            readerLogger.trace("[THYMELEAF][TEMPLATEPREPROCESSINGREADER][{}] CALLING read(). Will be delegated to read(char[], 0, 1).", 
                    new Object[] {TemplateEngine.threadIndex()});
        }
        
        final char[] cbuf = new char[1];
        final int res = read(cbuf, 0, 1);
        if (res <= 0) {
            return res;
        }
        
        return cbuf[0];
        
    }

    

    
    
    @Override
    public int read(final CharBuffer target) throws IOException {

        if (readerLogger.isTraceEnabled()) {
            readerLogger.trace("[THYMELEAF][TEMPLATEPREPROCESSINGREADER][{}] CALLING read(CharBuffer). Will be delegated as several calls to read(char[], 0, 1024).", 
                    new Object[] {TemplateEngine.threadIndex()});
        }
        
        final char[] cbuf = new char[BUFFER_BLOCK_SIZE];
        int totalRead = -1;
        int read;
        while ((read = read(cbuf, 0, cbuf.length)) != -1) {
            target.put(cbuf, 0, read);
            if (totalRead == -1) {
                totalRead = 0;
            }
            totalRead += read;
        }
        return totalRead;
        
    }




    
    @Override
    public int read(final char[] cbuf) throws IOException {

        if (readerLogger.isTraceEnabled()) {
            readerLogger.trace("[THYMELEAF][TEMPLATEPREPROCESSINGREADER][{}] CALLING read(char[] cbuf). Will be delegated to read(cbuf, 0, cbuf.length).", 
                    new Object[] {TemplateEngine.threadIndex()});
        }
        
        return read(cbuf, 0, cbuf.length);
        
    }

    


    @Override
    public long skip(final long n) throws IOException {
        throw new IOException("Skip not supported in reader");
    }




    @Override
    public boolean ready() throws IOException {
        return this.bufferedReader.ready() || this.overflowIndex > 0;
    }




    @Override
    public boolean markSupported() {
        return false;
    }




    @Override
    public void mark(final int readAheadLimit) throws IOException {
        throw new IOException("Mark not supported in reader");
    }




    @Override
    public void reset() throws IOException {
        throw new IOException("Reset not supported in reader");
    }




    @Override
    public void close() throws IOException {
        this.bufferedReader.close();
    }
    
    
    
    /*
     * fragment: the char[] containing the fragment to be copied
     * fragmentOff: the offset for the fragment char[]
     * fragmentLen: the length of the fragment to be copied
     * cbuf: the result buffer
     * cbufi: the current position in the result buffer (offset)
     * last: the last position (non-inclusive) that can be filled in cbuf
     * 
     * RETURNS: the amount of chars that have been actually copied to the cbuf result array
     */
    private int copyToResult(
            final char[] fragment, final int fragmentOff, final int fragmentLen, 
            final char[] cbuf, final int cbufi, final int last) {
        
        if (cbufi + fragmentLen < last) {
            
            System.arraycopy(fragment, fragmentOff, cbuf, cbufi, fragmentLen);

            //noinspection ArrayEquality
            if (fragment == this.overflow) {
                // Overflow has been cleaned
                this.overflowIndex = 0;
            }

            return fragmentLen;
            
        }
        
        // There is no space at the result buffer: we must overflow
        
        final int toBeCopied = last - cbufi;
        final int toBeOverflowed = fragmentLen - toBeCopied;
        
        if (toBeCopied > 0) {
            System.arraycopy(fragment, fragmentOff, cbuf, cbufi, toBeCopied);
        }
        //noinspection ArrayEquality
        if (fragment != this.overflow) {
            System.arraycopy(fragment, (fragmentOff + toBeCopied), this.overflow, this.overflowIndex, toBeOverflowed);
            this.overflowIndex += toBeOverflowed;
        } else {
            // Both source and target are the overflow array
            System.arraycopy(fragment, (fragmentOff + toBeCopied), this.overflow, 0, toBeOverflowed);
            this.overflowIndex = toBeOverflowed;
        }
        return toBeCopied;
        
    }
    
    

    
    /*
     * RETURNS:
     *     <0 = does not start here
     *     0  = maybe (not enough buffer to know)
     *     >0 = does start here
     */
    private static int match(
            final int[] fragment, final int fragmentOff, final int fragmentLen, 
            final char[] buffer, final int buffi, final int bufferLast) {
        
        /*
         * Trying to fail fast
         */
        final char f0Lower = LOWER_CHARS[fragment[fragmentOff]];
        final char f0Upper = UPPER_CHARS[fragment[fragmentOff]];
        if (f0Lower != CHAR_WHITESPACE_WILDCARD && f0Lower != CHAR_ALPHANUMERIC_WILDCARD && 
            f0Lower != buffer[buffi] && f0Upper != buffer[buffi]) {
            return -1;
        }
        
        final int fragmentLast = fragmentOff + fragmentLen;  
        
        int buffj = buffi;
        int fragmenti = fragmentOff;
        while (buffj < bufferLast && fragmenti < fragmentLast) {

            final int fragmentIndex = fragment[fragmenti];
            // lower will be enough for most checks
            final char fLower = LOWER_CHARS[fragmentIndex];
            
            // For wildcards, checking against lowercase will be enough (wildcards are at the same
            // position in lower and upper case).
            if (fLower == CHAR_WHITESPACE_WILDCARD) {
                
                if (buffer[buffj] != ' ' && buffer[buffj] != '\t') {
                    if (buffj > buffi && (buffer[buffj - 1] == ' ' || buffer[buffj - 1] == '\t')) {
                        fragmenti++;
                    } else {
                        // We did not find at least one whitespace
                        return -1;
                    }
                } else {
                    buffj++;
                }
                
            } else if (fLower == CHAR_OPTIONAL_WHITESPACE_WILDCARD) {
                    
                if (buffer[buffj] != ' ' && buffer[buffj] != '\t') {
                    fragmenti++;
                } else {
                    buffj++;
                }
                    
            } else if (fLower == CHAR_ALPHANUMERIC_WILDCARD) {
                
                final char c = buffer[buffj]; 
                final boolean isUpper = (c >= 'A' && c <= 'Z'); 
                final boolean isLower = (c >= 'a' && c <= 'z'); 
                final boolean isDigit = (c >= '0' && c <= '9'); 
                final boolean isHash = (c == '#'); 
                if ((!isUpper && !isLower && !isDigit && !isHash) ||
                        (fragmenti + 1 < fragmentLast && 
                                (UPPER_CHARS[fragment[fragmenti + 1]] == buffer[buffj]) ||
                                (LOWER_CHARS[fragment[fragmenti + 1]] == buffer[buffj]))) {
                    // Either we found a non-alphanumeric, or we simply found
                    // a character that matches next one in fragment
                    fragmenti++;
                } else {
                    buffj++;
                }
                
            } else if (fLower == CHAR_ANY_WILDCARD) {
                
                if ((fragmenti + 1 < fragmentLast && 
                        (UPPER_CHARS[fragment[fragmenti + 1]] == buffer[buffj]) ||
                        (LOWER_CHARS[fragment[fragmenti + 1]] == buffer[buffj]))) {
                    // We found a character that matches next one in fragment!
                    fragmenti++;
                } else {
                    buffj++;
                }
                
            } else {
                
                final char bufferChar = buffer[buffj];
                        
                if (bufferChar != UPPER_CHARS[fragmentIndex] && 
                    bufferChar != fLower) {
                    return -1;
                }

                buffj++;
                fragmenti++;
                
            }
            
        }
        if (fragmenti == fragmentLast) {
            // Matches! and we return the number of chars that matched
            return buffj - buffi;
        }
        // Was matching OK, but then we hit the end of the buffer...
        return 0;
        
    }

    
    
    /**
     * @since 2.0.11
     */
    public Reader getInnerReader() {
        return this.innerReader;
    }
    
    
}
