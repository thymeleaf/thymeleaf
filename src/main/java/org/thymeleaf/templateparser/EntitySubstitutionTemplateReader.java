/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
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
 * @since 1.1
 *
 */
public final class EntitySubstitutionTemplateReader extends Reader {

    
    private static final Logger readerLogger = LoggerFactory.getLogger(EntitySubstitutionTemplateReader.class);
    
    public static final char CHAR_ENTITY_START_SUBSTITUTE = '\uFFF8';
    
    private static final char CHAR_WHITESPACE_WILDCARD = '\u01F7';
    private static final char CHAR_ALPHANUMERIC_WILDCARD = '\u0234';
    
    
    private static final char[] COMMENT_START = "<!--".toCharArray(); 
    private static final char[] COMMENT_END = "-->".toCharArray(); 
    private static final char[] ENTITY = "&\u0234;".toCharArray();
    
    private static final char[] ENTITY_START_SUBSTITUTE = new char[] { CHAR_ENTITY_START_SUBSTITUTE };
    
    private final BufferedReader bufferedReader;
    
    private char[] buffer;
    private char[] overflow;
    private int overflowIndex;
    
    private boolean inComment = false;
    
    private boolean noMoreToRead = false;
    

    
    /*
     * 
     * TODO Add exceptions for not substituting anything inside [[...]]
     * 
     */


    
    public EntitySubstitutionTemplateReader(final Reader in, final int bufferSize) {
        super();
        this.bufferedReader = new BufferedReader(in, bufferSize);
        this.buffer = new char[bufferSize + 1024]; 
        this.overflow = new char[bufferSize + 2048];
        this.overflowIndex = 0;
    }

    
    

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {

        if (readerLogger.isTraceEnabled()) {
            readerLogger.trace("[THYMELEAF][HTMLTEMPLATEREADER][{}] CALLING read(char[], {}, {})", 
                    new Object[] {TemplateEngine.threadIndex(),Integer.valueOf(off), Integer.valueOf(len)});
        }
        
        if ((len * 2) > this.overflow.length) {
            // Resize buffer and overflow
            
            this.buffer = new char[len + 1024];
            final char[] newOverflow = new char[len + 2048];
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
                readerLogger.trace("[THYMELEAF][HTMLTEMPLATEREADER][{}] READ FROM OVERFLOW BUFFER {} Some content from the overflow buffer has been copied into results.", 
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
                readerLogger.trace("[THYMELEAF][HTMLTEMPLATEREADER][{}] RELLOCATED SOME OVERFLOW CONTENTS, WAITING TO BE ADDED TO RESULT/NEW OVERFLOW {} Some content was remaining at the overflow buffer and will have to be rellocated.", 
                        new Object[] {TemplateEngine.threadIndex(), Integer.valueOf(this.overflowIndex)});
            }
            
            this.overflowIndex = 0;
            
        }

        
        
        if (!this.noMoreToRead && bufferSize < this.buffer.length) {
            // Buffer was not filled up with content from overflow, so ask for more content
            
            final int toBeRead = this.buffer.length - bufferSize;
            final int reallyRead = this.bufferedReader.read(this.buffer, bufferSize, toBeRead);
            
            if (readerLogger.isTraceEnabled()) {
                readerLogger.trace("[THYMELEAF][HTMLTEMPLATEREADER][{}] READ FROM SOURCE {} A read operation was executed on the source reader (max chars requested: {}).", 
                        new Object[] {TemplateEngine.threadIndex(), Integer.valueOf(reallyRead), Integer.valueOf(toBeRead)});
            }
            
            if (reallyRead < 0) {
                
                if (bufferSize == 0) {

                    if (readerLogger.isTraceEnabled()) {
                        readerLogger.trace("[THYMELEAF][HTMLTEMPLATEREADER][{}] RETURN {} After trying to read from input: No input left, no buffer left.", 
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
                readerLogger.trace("[THYMELEAF][HTMLTEMPLATEREADER][{}] RETURN -1 Reader was already marked to be finished. No more input, no more buffer.", 
                        new Object[] {TemplateEngine.threadIndex()});
            }
            
            return -1;
        }
        
        

        int totalRead = 0;
        int cbufi = off;
        int last = off + len;
        
        int buffi = 0;
        while (cbufi < last && buffi < bufferSize) {

            final int matchedStartOfComment = 
                (this.inComment? 
                        -2 : match(COMMENT_START, 0, COMMENT_START.length, this.buffer, buffi, bufferSize));
            final int matchedEndOfComment = 
                (this.inComment? 
                        match(COMMENT_END, 0, COMMENT_END.length, this.buffer, buffi, bufferSize) : -2);

            final int matchedEntity = 
                (this.inComment? 
                        -2 : match(ENTITY, 0, ENTITY.length, this.buffer, buffi, bufferSize));
            
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
            
            if (matchedEntity > 0) {
                
                final int copied =
                    copyToResult(
                            ENTITY_START_SUBSTITUTE, 0, ENTITY_START_SUBSTITUTE.length, 
                            cbuf, cbufi, last);
                cbufi += copied;
                totalRead += copied;
                buffi += 1; // Only one character is substituted (&)
                continue;
                
            }
            
            cbuf[cbufi++] = this.buffer[buffi++];
            totalRead++;
            
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
            readerLogger.trace("[THYMELEAF][HTMLTEMPLATEREADER][{}] RETURN {} Input was read and processed. Returning content: [[{}]]", 
                    new Object[] {TemplateEngine.threadIndex(), Integer.valueOf(totalRead), new String(result)});
        }
        

        return totalRead;
        
    }


    
    
    @Override
    public int read() throws IOException {

        if (readerLogger.isTraceEnabled()) {
            readerLogger.trace("[THYMELEAF][HTMLTEMPLATEREADER][{}] CALLING read(). Will be delegated to read(char[], 0, 1).", 
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
            readerLogger.trace("[THYMELEAF][HTMLTEMPLATEREADER][{}] CALLING read(CharBuffer). Will be delegated as several calls to read(char[], 0, 1024).", 
                    new Object[] {TemplateEngine.threadIndex()});
        }
        
        final char[] cbuf = new char[1024];
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
            readerLogger.trace("[THYMELEAF][HTMLTEMPLATEREADER][{}] CALLING read(char[] cbuf). Will be delegated to read(cbuf, 0, cbuf.length).", 
                    new Object[] {TemplateEngine.threadIndex()});
        }
        
        return read(cbuf, 0, cbuf.length);
        
    }

    


    @Override
    public long skip(long n) throws IOException {
        throw new IOException("Skip not supported in reader");
    }




    @Override
    public boolean ready() throws IOException {
        if (this.bufferedReader.ready()) {
            return true;
        }
        return this.overflowIndex > 0;
    }




    @Override
    public boolean markSupported() {
        return false;
    }




    @Override
    public void mark(int readAheadLimit) throws IOException {
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
     *    -1 = does not start here
     *     0 = maybe (not enough buffer to know)
     *     1 = does start here
     */
    private static int match(
            final char[] fragment, final int fragmentOff, final int fragmentLen, 
            final char[] buffer, final int buffi, final int bufferLast) {
        
        /*
         * Trying to fail fast
         */
        final char f0 = fragment[fragmentOff];
        if (f0 != CHAR_WHITESPACE_WILDCARD && f0 != CHAR_ALPHANUMERIC_WILDCARD && f0 != buffer[buffi]) {
            return -1;
        }
        
        final int fragmentLast = fragmentOff + fragmentLen;  
        
        int buffj = buffi;
        int fragmenti = fragmentOff;
        while (buffj < bufferLast && fragmenti < fragmentLast) {

            final char f = fragment[fragmenti];
            
            if (f == CHAR_WHITESPACE_WILDCARD) {
                
                if (buffer[buffj] != ' ' && buffer[buffj] != '\t') {
                    fragmenti++;
                } else {
                    buffj++;
                }
                
            } else if (f == CHAR_ALPHANUMERIC_WILDCARD) {
                
                final char c = buffer[buffj]; 
                final boolean isUpper = (c >= 'A' && c <= 'Z'); 
                final boolean isLower = (c >= 'a' && c <= 'z'); 
                final boolean isDigit = (c >= '0' && c <= '9'); 
                final boolean isHash = (c == '#'); 
                if ((!isUpper && !isLower && !isDigit && !isHash) ||
                        (fragmenti + 1 < fragmentLast && fragment[fragmenti + 1] == buffer[buffj])) {
                    fragmenti++;
                } else {
                    buffj++;
                }
                
            } else {
                
                if (buffer[buffj++] != fragment[fragmenti++]) {
                    return -1;
                }
                
            }
            
        }
        if (fragmenti == fragmentLast) {
            return buffj - buffi;
        }
        return -1;
        
    }


    
    
    public static final String removeEntitySubstitutions(final String text) {

        if (text == null) {
            return null;
        }
        final int textLen = text.length();
        for (int i = 0; i < textLen; i++) {
            if (text.charAt(i) == EntitySubstitutionTemplateReader.CHAR_ENTITY_START_SUBSTITUTE) {
                final char[] textCharArray = text.toCharArray();
                for (int j = 0; j < textLen; j++) {
                    if (textCharArray[j] == EntitySubstitutionTemplateReader.CHAR_ENTITY_START_SUBSTITUTE) {
                        textCharArray[j] = '&';
                    }
                }
                return new String(textCharArray);
            }
        }
        return text;
        
    }
        

    
    
    public static final void removeEntitySubstitutions(final char[] text, final int off, final int len) {

        if (text == null) {
            return;
        }
        final int finalPos = off + len;
        for (int i = off; i < finalPos; i++) {
            if (text[i] == EntitySubstitutionTemplateReader.CHAR_ENTITY_START_SUBSTITUTE) {
                text[i] = '&';
            }
        }
        
    }
    
}
