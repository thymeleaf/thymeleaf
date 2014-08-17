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
public final class TemplatePreprocessingReader extends Reader {

    
    private static final Logger readerLogger = LoggerFactory.getLogger(TemplatePreprocessingReader.class);

    private static final int BUFFER_BLOCK_SIZE = 1024;
    private static final int OVERFLOW_BLOCK_SIZE = 2048;

    public static final char CHAR_ENTITY_START_SUBSTITUTE = '\uFFF8';

    
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
    private static final int[] ENTITY = convertToIndexes(('&' + String.valueOf(CHAR_ALPHANUMERIC_WILDCARD) + ';').toCharArray());
    private static final int[] DOCTYPE =
            convertToIndexes(("<!DOCTYPE" + String.valueOf(CHAR_WHITESPACE_WILDCARD)  + String.valueOf(CHAR_ANY_WILDCARD) + ">").toCharArray());
    private static final int[] XML_PROLOG =
            convertToIndexes(("<?xml" + String.valueOf(CHAR_WHITESPACE_WILDCARD)  + String.valueOf(CHAR_ANY_WILDCARD) + "?>").toCharArray());

    private static final int[] PROTOTYPE_ONLY_COMMENT_START = convertToIndexes("<!--/*/".toCharArray());
    private static final int[] PROTOTYPE_ONLY_COMMENT_END = convertToIndexes("/*/-->".toCharArray());

    private static final int[] PARSER_LEVEL_COMMENT_START = convertToIndexes("<!--/*".toCharArray());
    private static final int[] PARSER_LEVEL_COMMENT_END = convertToIndexes("*/-->".toCharArray());


    private static final char[] NORMALIZED_DOCTYPE_PREFIX = "<!DOCTYPE ".toCharArray();
    private static final char[] NORMALIZED_DOCTYPE_PUBLIC = "PUBLIC ".toCharArray();
    private static final char[] NORMALIZED_DOCTYPE_SYSTEM = "SYSTEM ".toCharArray();
    
    
    private static final char[] ENTITY_START_SUBSTITUTE_CHAR_ARRAY = new char[] { CHAR_ENTITY_START_SUBSTITUTE };

    
    public static final String SYNTHETIC_ROOT_ELEMENT_NAME = "THYMELEAF_ROOT"; 
    private static final char[] SYNTHETIC_ROOT_ELEMENT_START_CHARS = ("<" + SYNTHETIC_ROOT_ELEMENT_NAME + ">").toCharArray();
    private static final char[] SYNTHETIC_ROOT_ELEMENT_END_CHARS = ("</" + SYNTHETIC_ROOT_ELEMENT_NAME + ">").toCharArray(); 
    

    private final Reader innerReader;
    private final BufferedReader bufferedReader;
    private final boolean addSyntheticRootElement;
    
    private char[] buffer;
    private char[] overflow;
    private int overflowIndex;
    
    private boolean inComment = false;
    private boolean inParserLevelComment = false;
    private boolean docTypeClauseRead = false;
    private boolean xmlPrologRead = false;
    private int xmlPrologRemaining = -1;
    private boolean syntheticRootElementOpeningProcessed = false;
    private boolean syntheticRootElementClosingSent = false;
    private int rootElementClosingOffset = 0;
    
    private boolean noMoreToRead = false;
    
    private String docTypeClause = null;
    


    
    
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


    
    public TemplatePreprocessingReader(final Reader in, final int bufferSize) {
        this(in, bufferSize, true);
    }


    /**
     * 
     * @since 2.0.11
     */
    public TemplatePreprocessingReader(final Reader in, final int bufferSize, final boolean addSyntheticRootElement) {
        super();
        this.innerReader = in;
        this.bufferedReader = new BufferedReader(this.innerReader, bufferSize);
        this.buffer = new char[bufferSize + BUFFER_BLOCK_SIZE];
        this.overflow = new char[bufferSize + OVERFLOW_BLOCK_SIZE];
        this.overflowIndex = 0;
        this.addSyntheticRootElement = addSyntheticRootElement;
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

            if (this.addSyntheticRootElement && !this.syntheticRootElementClosingSent && (reallyRead < 0)) {
                // If there is no more content to be read from the source reader, close the synthetic
                // root element and make it look like it was read from source.

                final int closingSizeToInsert =
                    Math.min(
                            (SYNTHETIC_ROOT_ELEMENT_END_CHARS.length - this.rootElementClosingOffset),
                            (this.buffer.length - bufferSize)); 
                reallyRead =
                    copyToResult(
                            SYNTHETIC_ROOT_ELEMENT_END_CHARS, this.rootElementClosingOffset, closingSizeToInsert, 
                            this.buffer, bufferSize, this.buffer.length);
                
                this.rootElementClosingOffset += reallyRead;

                if (this.rootElementClosingOffset >= SYNTHETIC_ROOT_ELEMENT_END_CHARS.length) {
                    this.syntheticRootElementClosingSent = true;
                }
                
            }
            
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


            if (this.buffer[buffi] == '\uFEFF') {
                // This is a Byte Order Mark (BOM) symbol. We are working at a character level already (as opposed to
                // a byte level), so we should be fine just ignoring it.
                buffi++;
                continue;
            }

            
            /*
             * Process XML_PROLOG (if needed)
             * 
             * Processing it specifically before any other thing ensures the synthetic
             * root element will never be inserted before it.
             */
            if (!this.docTypeClauseRead && !this.xmlPrologRead && !this.inParserLevelComment) {

                if (this.xmlPrologRemaining >= 0) {
                    // We have still some bytes remaining from the XML PROLOG, and we 
                    // don't want the reader to mistake them for text and input the synthetic
                    // root element right now
                    
                    if (readerLogger.isTraceEnabled()) {
                        readerLogger.trace("[THYMELEAF][TEMPLATEPREPROCESSINGREADER][{}] Writing remaining byte from incompletely output XML PROLOG: {}", 
                                new Object[] {TemplateEngine.threadIndex(), String.valueOf(this.buffer[buffi])});
                    }
                    
                    cbuf[cbufi++] = this.buffer[buffi++];
                    totalRead++;
                    this.xmlPrologRemaining--;
                    continue;
        
                }
                
                final int matchedXmlProlog =
                        match(XML_PROLOG, 0, XML_PROLOG.length, this.buffer, buffi, bufferSize);
                
                if (matchedXmlProlog > 0) {

                    this.xmlPrologRemaining = matchedXmlProlog;
                    
                    final int copied =
                        copyToResult(
                                this.buffer, buffi, matchedXmlProlog, 
                                cbuf, cbufi, last);

                    this.xmlPrologRemaining -= copied;
                    if (this.xmlPrologRemaining <= 0) {
                        this.xmlPrologRead = true;
                    }

                    cbufi += copied;
                    totalRead += copied;
                    buffi += matchedXmlProlog;
                    continue;
                    
                }
                
            }

            
            /*
             * Process DOCTYPE (if needed)
             */
            if (!this.docTypeClauseRead && !this.inParserLevelComment) {
                
                final int matchedDocType =
                        match(DOCTYPE, 0, DOCTYPE.length, this.buffer, buffi, bufferSize);
                
                if (matchedDocType > 0) {
                    
                    this.docTypeClause = new String(this.buffer, buffi, matchedDocType);
                    this.docTypeClauseRead = true;
                    
                    final char[] normalizedDocType =
                            normalizeDocTypeClause(this.buffer, buffi, matchedDocType);
                    
                    if (readerLogger.isTraceEnabled()) {
                        readerLogger.trace("[THYMELEAF][TEMPLATEPREPROCESSINGREADER][{}] Normalized DOCTYPE clause: {}", 
                                new Object[] {TemplateEngine.threadIndex(), new String(normalizedDocType)});
                    }
                    
                    int copied = -1;
                    if (this.addSyntheticRootElement && !this.syntheticRootElementOpeningProcessed) {
                        // If DOCTYPE is processed, we will inject the synthetic root
                        // element just after the DOCTYPE so that we avoid problems with
                        // DOCTYPE clause being bigger than 'len' argument in the first 'read()' call.
                        
                        final char[] normalizedDocTypePlusSyntheticRootElement = 
                                new char[normalizedDocType.length + SYNTHETIC_ROOT_ELEMENT_START_CHARS.length];
                        System.arraycopy(normalizedDocType, 0, normalizedDocTypePlusSyntheticRootElement, 0, normalizedDocType.length);
                        System.arraycopy(SYNTHETIC_ROOT_ELEMENT_START_CHARS, 0, normalizedDocTypePlusSyntheticRootElement, normalizedDocType.length, SYNTHETIC_ROOT_ELEMENT_START_CHARS.length);
                        
                        if (readerLogger.isTraceEnabled()) {
                            readerLogger.trace("[THYMELEAF][TEMPLATEPREPROCESSINGREADER][{}] Synthetic root element will be output along with DOCTYPE clause: '{}'", 
                                    new Object[] {TemplateEngine.threadIndex(), new String(normalizedDocTypePlusSyntheticRootElement)});
                        }
                        
                        copied =
                                copyToResult(
                                        normalizedDocTypePlusSyntheticRootElement, 0, normalizedDocTypePlusSyntheticRootElement.length, 
                                        cbuf, cbufi, last);
                        
                        this.syntheticRootElementOpeningProcessed = true;
                        
                    } else {
                        
                        
                        if (readerLogger.isTraceEnabled()) {
                            readerLogger.trace("[THYMELEAF][TEMPLATEPREPROCESSINGREADER][{}] DOCTYPE clause will be output, without synthetic root element: '{}'", 
                                    new Object[] {TemplateEngine.threadIndex(), new String(normalizedDocType)});
                        }

                        copied =
                                copyToResult(
                                        normalizedDocType, 0, normalizedDocType.length, 
                                        cbuf, cbufi, last);
                        
                    }
                    
                    
                    cbufi += copied;
                    totalRead += copied;
                    buffi += matchedDocType;
                    continue;
                    
                }
                
            }

            /*
             * ------------------
             * CHECK FOR PROTOTYPE-ONLY COMMENT BLOCKS
             * ------------------
             */

            // Completely ignored if inside comment
            final int matchedStartOfPrototypeOnlyComment =
                    (this.inComment || this.inParserLevelComment?
                            -2 : match(PROTOTYPE_ONLY_COMMENT_START, 0, PROTOTYPE_ONLY_COMMENT_START.length, this.buffer, buffi, bufferSize));

            if (matchedStartOfPrototypeOnlyComment > 0) {

                // no changes to "cbufi", as nothing is copied into result
                // no changes to "totalRead", as nothing is copied into result
                buffi += matchedStartOfPrototypeOnlyComment; // We just skip all characters in comment start
                continue;

            }


            // Completely ignored if inside comment, in contrast with "normal" comment end
            final int matchedEndOfPrototypeOnlyComment =
                    (this.inComment || this.inParserLevelComment?
                            -2 : match(PROTOTYPE_ONLY_COMMENT_END, 0, PROTOTYPE_ONLY_COMMENT_END.length, this.buffer, buffi, bufferSize));

            if (matchedEndOfPrototypeOnlyComment > 0) {

                // no changes to "cbufi", as nothing is copied into result
                // no changes to "totalRead", as nothing is copied into result
                buffi += matchedEndOfPrototypeOnlyComment; // We just skip all characters in comment end
                continue;

            }



            /*
             * ------------------
             * CHECK FOR PARSER-LEVEL COMMENT BLOCKS
             * ------------------
             */

            // Completely ignored if inside comment
            final int matchedStartOfParserLevelComment =
                    (this.inComment || this.inParserLevelComment?
                            -2 : match(PARSER_LEVEL_COMMENT_START, 0, PARSER_LEVEL_COMMENT_START.length, this.buffer, buffi, bufferSize));

            if (matchedStartOfParserLevelComment > 0) {

                // no changes to "cbufi", as nothing is copied into result
                // no changes to "totalRead", as nothing is copied into result
                buffi += matchedStartOfParserLevelComment; // We skip all characters in comment start
                this.inParserLevelComment = true;
                continue;

            }


            // Completely ignored if inside comment, in contrast with "normal" comment end
            final int matchedEndOfParserLevelComment =
                    (this.inParserLevelComment ?
                            match(PARSER_LEVEL_COMMENT_END, 0, PARSER_LEVEL_COMMENT_END.length, this.buffer, buffi, bufferSize) : -2);

            if (matchedEndOfParserLevelComment > 0) {

                // no changes to "cbufi", as nothing is copied into result
                // no changes to "totalRead", as nothing is copied into result
                buffi += matchedEndOfParserLevelComment; // We skip all characters in comment end
                this.inParserLevelComment = false;
                continue;

            }



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
             * CHECK FOR ENTITIES(& sign will be replaced in order to avoid parser behaviours)
             * ------------------
             */

            final int matchedEntity = 
                (this.inComment || this.inParserLevelComment?
                        -2 : match(ENTITY, 0, ENTITY.length, this.buffer, buffi, bufferSize));
            
            if (matchedEntity > 0) {
                
                final int copied =
                    copyToResult(
                            ENTITY_START_SUBSTITUTE_CHAR_ARRAY, 0, ENTITY_START_SUBSTITUTE_CHAR_ARRAY.length, 
                            cbuf, cbufi, last);
                cbufi += copied;
                totalRead += copied;
                buffi += 1; // Only one character is substituted (&)
                continue;
                
            }



            /*
             * ------------------
             * ADD A SYNTHETIC ROOT ELEMENT IF NEEDED (e.g. in order to allow several-root-node documents)
             * ------------------
             */

            if (!Character.isWhitespace(this.buffer[buffi]) && this.addSyntheticRootElement && !this.syntheticRootElementOpeningProcessed && !this.inComment && !this.inParserLevelComment) {
                // This block will be reached if we did not have to process a
                // DOCTYPE clause (because the DOCTYPE would have
                // matched the previous block). And will not be affected by any whitespaces
                // or comments before DOCTYPE because of the !Character.isWhitespace condition.
                    
                final int copied =
                    copyToResult(
                            SYNTHETIC_ROOT_ELEMENT_START_CHARS, 0, SYNTHETIC_ROOT_ELEMENT_START_CHARS.length, 
                            cbuf, cbufi, last);
                
                cbufi += copied;
                totalRead += copied;
                
                this.syntheticRootElementOpeningProcessed = true;
                
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


    
    
    private static char[] normalizeDocTypeClause(final char[] buffer, final int offset, final int len) {
        
        try {
            
            boolean afterQuote = false;
            
            final char[] result = new char[len];
            System.arraycopy(NORMALIZED_DOCTYPE_PREFIX, 0, result, 0, NORMALIZED_DOCTYPE_PREFIX.length);
            
            for (int i = (offset + NORMALIZED_DOCTYPE_PREFIX.length); i < (offset + len); i++) {
                final char c = buffer[i];
                if (c == '\"') {
                    // Once we find a quote symbol, we stop worrying about normalizing and just copy verbatim
                    afterQuote = true;
                    result[i - offset] = '\"';
                } else if (!afterQuote && (c == 'P' || c == 'p')) {
                    final char c2 = buffer[i + 1];
                    if (c2 == 'U' || c2 == 'u') {
                        final char c3 = buffer[i + 2];
                        final char c4 = buffer[i + 3];
                        final char c5 = buffer[i + 4];
                        final char c6 = buffer[i + 5];
                        final char c7 = buffer[i + 6];
                        if ((c3 == 'B' || c3 == 'b') && 
                            (c4 == 'L' || c4 == 'l') &&
                            (c5 == 'I' || c5 == 'i') &&
                            (c6 == 'C' || c6 == 'c') &&
                            (c7 == ' ' || c7 == '\t')) {
                            System.arraycopy(NORMALIZED_DOCTYPE_PUBLIC, 0, result, (i - offset), NORMALIZED_DOCTYPE_PUBLIC.length);
                            i += NORMALIZED_DOCTYPE_PUBLIC.length - 1;
                            continue;
                        }
                    }
                    result[i - offset] = c;
                } else if (!afterQuote && (c == 'S' || c == 's')) {
                    final char c2 = buffer[i + 1];
                    if (c2 == 'Y' || c2 == 'y') {
                        final char c3 = buffer[i + 2];
                        final char c4 = buffer[i + 3];
                        final char c5 = buffer[i + 4];
                        final char c6 = buffer[i + 5];
                        final char c7 = buffer[i + 6];
                        if ((c3 == 'S' || c3 == 's') && 
                            (c4 == 'T' || c4 == 't') &&
                            (c5 == 'E' || c5 == 'e') &&
                            (c6 == 'M' || c6 == 'm') &&
                            (c7 == ' ' || c7 == '\t')) {
                            System.arraycopy(NORMALIZED_DOCTYPE_SYSTEM, 0, result, (i - offset), NORMALIZED_DOCTYPE_SYSTEM.length);
                            i += NORMALIZED_DOCTYPE_SYSTEM.length - 1;
                            continue;
                        }
                    }
                    result[i - offset] = c;
                } else {
                    result[i - offset] = c;
                }
            }
    
            
            return result;

        } catch (final Exception e) {
            throw new TemplateInputException("DOCTYPE clause has bad format: \"" + (new String(buffer, offset, len)) + "\"", e);
        }
        
    }

    
    
    public String getDocTypeClause() {
        return this.docTypeClause;
    }

    
    
    
    public static String removeEntitySubstitutions(final String text) {

        if (text == null) {
            return null;
        }
        final int textLen = text.length();
        for (int i = 0; i < textLen; i++) {
            if (text.charAt(i) == TemplatePreprocessingReader.CHAR_ENTITY_START_SUBSTITUTE) {
                final char[] textCharArray = text.toCharArray();
                for (int j = 0; j < textLen; j++) {
                    if (textCharArray[j] == TemplatePreprocessingReader.CHAR_ENTITY_START_SUBSTITUTE) {
                        textCharArray[j] = '&';
                    }
                }
                return new String(textCharArray);
            }
        }
        return text;
        
    }
        

    
    
    public static void removeEntitySubstitutions(final char[] text, final int off, final int len) {

        if (text == null) {
            return;
        }
        final int finalPos = off + len;
        for (int i = off; i < finalPos; i++) {
            if (text[i] == TemplatePreprocessingReader.CHAR_ENTITY_START_SUBSTITUTE) {
                text[i] = '&';
            }
        }
        
    }
    
    
    
    /**
     * @since 2.0.11
     */
    public Reader getInnerReader() {
        return this.innerReader;
    }
    
    
}
