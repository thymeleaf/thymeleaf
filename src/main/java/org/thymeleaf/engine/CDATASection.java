/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.engine;

import java.io.IOException;
import java.io.Writer;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.model.IModelVisitor;
import org.thymeleaf.text.ITextRepository;
import org.thymeleaf.text.IWritableCharSequence;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
final class CDATASection extends AbstractTemplateEvent implements ICDATASection, IEngineTemplateEvent {

    // CDATA Section nodes do not exist in text parsing, so we are safe expliciting markup structures here
    private static final String CDATA_PREFIX = "<![CDATA[";
    private static final String CDATA_SUFFIX = "]]>";

    private final ITextRepository textRepository;

    private char[] buffer;
    private int offset;

    private String cdataSection;
    private CharSequence content;

    private int cdataSectionLength;
    private int contentLength;

    private Boolean whitespace;
    private Boolean inlineable;


    /*
     * Object of this class can contain their data both as a String and as a char[] buffer. The buffer will only
     * be used internally to the 'engine' package, in order to avoid the creation of unnecessary String objects
     * (most times the parsing buffer itself will be used). Computation of the String form will be performed lazily
     * and only if specifically required.
     *
     * Objects of this class are meant to both be reused by the engine and also created fresh by the processors. This
     * should allow reducing the number of instances of this class to the minimum.
     */


    // Meant to be called only from the template handler adapter
    CDATASection(final ITextRepository textRepository) {
        super();
        this.textRepository = textRepository;
    }



    CDATASection(final ITextRepository textRepository, final CharSequence content) {
        super();
        this.textRepository = textRepository;
        setContent(content);
    }






    public String getCDATASection() {

        // Either we have a non-null cdata and/or content, or a non-null buffer specification (char[],offset,len)

        if (this.cdataSection == null) {
            if (this.content == null) {
                this.cdataSection = this.textRepository.getText(this.buffer, this.offset, this.cdataSectionLength);
            } else {
                this.cdataSection = this.textRepository.getText(CDATA_PREFIX, this.content, CDATA_SUFFIX);
            }
        }

        return this.cdataSection;

    }


    public String getContent() {

        if (this.content == null) {

            // By calling getCDATASection() we will compute a String for the entire CDATA Section, so we will
            // save some bytes in memory if we just return a substring of it (substrings don't duplicate the
            // underlying char[])

            getCDATASection(); // this will initialize this.cdataSection if not initialized yet
            this.content =
                    this.textRepository.getText(
                            this.cdataSection, CDATA_PREFIX.length(), (this.cdataSection.length() - CDATA_SUFFIX.length()));

        }

        return this.content.toString();

    }




    public int length() {
        if (this.cdataSectionLength == -1 && this.content != null) {
            this.contentLength = this.content.length();
            this.cdataSectionLength = CDATA_PREFIX.length() + this.contentLength + CDATA_SUFFIX.length();
        }
        return this.cdataSectionLength;
    }


    public char charAt(final int index) {

        // no need to perform index bounds checking: it would slow down traversing operations a lot, and
        // it would be exactly the same exception we'd obtain by basically trying to access that index, so let's do
        // it directly instead

        if (this.buffer != null) {
            return this.buffer[this.offset + index];
        }

        if (this.cdataSection != null) {
            return this.cdataSection.charAt(index);
        }
        return getCDATASection().charAt(index); // Force the computation of the cdataSection property

    }


    public CharSequence subSequence(final int start, final int end) {

        // no need to perform index bounds checking: it would slow down traversing operations a lot, and
        // it would be exactly the same exception we'd obtain by basically trying to access that index, so let's do
        // it directly instead

        int subLen = end - start;

        if (this.cdataSection != null || this.content != null) {
            if (start == 0 && subLen == length()) {
                return getCDATASection();
            }
            return getCDATASection().subSequence(start, end);
        }

        return this.textRepository.getText(this.buffer, this.offset + start, subLen);

    }




    void reset(final char[] buffer,
               final int outerOffset, final int outerLen,
               final String templateName, final int line, final int col) {

        // This is only meant to be called internally, so no need to perform a lot of checks on the input validity

        super.resetTemplateEvent(templateName, line, col);

        this.buffer = buffer;
        this.offset = outerOffset;

        this.cdataSectionLength = outerLen;
        this.contentLength = this.cdataSectionLength - CDATA_PREFIX.length() - CDATA_SUFFIX.length();

        this.cdataSection = null;
        this.content = null;

        this.whitespace = null;
        this.inlineable = null;

    }




    public void setContent(final CharSequence content) {

        if (content == null) {
            throw new IllegalArgumentException("CDATA Section content cannot be null");
        }

        super.resetTemplateEvent(null, -1, -1);

        this.content = content;
        this.cdataSection = null;

        this.contentLength = -1;
        this.cdataSectionLength = -1;

        this.buffer = null;
        this.offset = -1;

        this.whitespace = null;
        this.inlineable = null;

    }




    public void accept(final IModelVisitor visitor) {
        visitor.visit(this);
    }


    public void write(final Writer writer) throws IOException {
        Validate.notNull(writer, "Writer cannot be null");
        if (this.buffer != null) {
            // Using the 'buffer write' is the first option because it is normally faster and requires less
            // resources than writing String objects
            writer.write(this.buffer, this.offset, this.cdataSectionLength);
        } else if (this.cdataSection != null) {
            writer.write(this.cdataSection);
        } else { // this.content != null
            writer.write(CDATA_PREFIX);
            if (this.content instanceof IWritableCharSequence) {
                // In the special case we are using a writable CharSequence, we will avoid creating a String
                // for the whole content
                ((IWritableCharSequence) this.content).write(writer);
            } else {
                writer.write(this.content.toString());
            }
            writer.write(CDATA_SUFFIX);
        }
    }





    public CDATASection cloneEvent() {
        // When cloning we will protect the buffer as only the instances used themselves as buffers in the 'engine'
        // package should reference a buffer.
        final CDATASection clone = new CDATASection(this.textRepository);
        clone.resetAsCloneOf(this);
        return clone;
    }


    // Meant to be called only from within the engine
    void resetAsCloneOf(final CDATASection original) {

        super.resetAsCloneOfTemplateEvent(original);
        this.buffer = null;
        this.offset = -1;
        this.cdataSection = original.cdataSection;
        this.content = (original.content == null? original.getContent() : original.content); // No buffer cloning
        this.cdataSectionLength = original.cdataSectionLength;
        this.contentLength = original.contentLength;
        this.whitespace = original.whitespace;
        this.inlineable = original.inlineable;

    }


    // Meant to be called only from within the engine
    static CDATASection asEngineCDATASection(
            final IEngineConfiguration configuration, final ICDATASection cdataSection, final boolean cloneAlways) {

        if (cdataSection instanceof CDATASection) {
            if (cloneAlways) {
                return ((CDATASection) cdataSection).cloneEvent();
            }
            return (CDATASection) cdataSection;
        }

        final CDATASection newInstance = new CDATASection(configuration.getTextRepository());
        newInstance.buffer = null;
        newInstance.offset = -1;
        newInstance.cdataSection = cdataSection.getCDATASection();
        newInstance.content = cdataSection.getContent();
        newInstance.cdataSectionLength = newInstance.cdataSection.length();
        newInstance.contentLength = newInstance.content.length();
        newInstance.whitespace = null;
        newInstance.inlineable = null;
        newInstance.resetTemplateEvent(cdataSection.getTemplateName(), cdataSection.getLine(), cdataSection.getCol());
        return newInstance;

    }





    boolean isWhitespace() {
        if (this.whitespace == null) {
            computeContentFlags();
        }
        return this.whitespace.booleanValue();
    }


    boolean isInlineable() {
        if (this.inlineable == null) {
            computeContentFlags();
        }
        return this.inlineable.booleanValue();
    }



    void computeContentFlags() {
        if (this.buffer != null) {
            computeContentFlags(this.buffer, this.offset + CDATA_PREFIX.length(), this.contentLength);
        } else {
            computeContentFlags(this.content);
        }
    }



    private void computeContentFlags(final CharSequence text) {

        this.whitespace = null;
        this.inlineable = null;

        int n = this.length() - (CDATA_PREFIX.length() + CDATA_SUFFIX.length());

        if (n == 0) {
            this.whitespace = Boolean.FALSE; // empty texts are NOT whitespace
            this.inlineable = Boolean.FALSE;
            return;
        }

        char c0, c1;
        c0 = 0x0;
        int inline = 0;
        while (n-- != 0 && this.inlineable == null) {
            c1 = text.charAt(n);
            if (c1 != ' ' && c1 != '\n') { // shortcut - most characters in many templates are just whitespace.
                if (this.whitespace == null && !Character.isWhitespace(c1)) {
                    this.whitespace = Boolean.FALSE;
                }
                if (c1 == ']' && c0 == ']') {
                    inline = 1;
                } else if (c1 == ')' && c0 == ']') {
                    inline = 2;
                } else if (inline == 1 && c1 == '[' && c0 == '[') {
                    this.inlineable = Boolean.TRUE;
                } else if (inline == 2 && c1 == '[' && c0 == '(') {
                    this.inlineable = Boolean.TRUE;
                }
            }
            c0 = c1;
        }

        // Not having the contrary been proved, apply the defaults
        this.whitespace = (this.whitespace == null? Boolean.TRUE : this.whitespace);
        this.inlineable = (this.inlineable == null? Boolean.FALSE : this.inlineable);

    }


    private void computeContentFlags(final char[] buffer, final int off, final int len) {

        this.whitespace = null;
        this.inlineable = null;

        int n = off + len;

        if (len == 0) {
            this.whitespace = Boolean.FALSE; // empty texts are NOT whitespace
            this.inlineable = Boolean.FALSE;
            return;
        }

        char c0, c1;
        c0 = 0x0;
        int inline = 0;
        while (n-- != off && this.inlineable == null) {
            c1 = buffer[n];
            if (c1 != ' ' && c1 != '\n') { // shortcut - most characters in many templates are just whitespace.
                if (this.whitespace == null && !Character.isWhitespace(c1)) {
                    this.whitespace = Boolean.FALSE;
                }
                if (c1 == ']' && c0 == ']') {
                    inline = 1;
                } else if (c1 == ')' && c0 == ']') {
                    inline = 2;
                } else if (inline == 1 && c1 == '[' && c0 == '[') {
                    this.inlineable = Boolean.TRUE;
                } else if (inline == 2 && c1 == '[' && c0 == '(') {
                    this.inlineable = Boolean.TRUE;
                }
            }
            c0 = c1;
        }

        // Not having the contrary been proved, apply the defaults
        this.whitespace = (this.whitespace == null? Boolean.TRUE : this.whitespace);
        this.inlineable = (this.inlineable == null? Boolean.FALSE : this.inlineable);

    }





    @Override
    public final String toString() {
        return getCDATASection();
    }


}
