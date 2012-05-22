/*
 * =============================================================================
 * 
 *   Copyright (c) 2011, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.doctype.DocTypeIdentifier;
import org.thymeleaf.exceptions.OutputCreationException;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.thymeleaf.util.Validate;
import org.w3c.dom.Document;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class OutputHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(OutputHandler.class);

    public static final String TRANSFORMER_OUTPUT_METHOD_HTML = "html";
    public static final String TRANSFORMER_OUTPUT_METHOD_XML = "xml";
    
    public static final String DOCTYPE_HTML5 = "<!DOCTYPE html>";
    public static final String DOCTYPE_HTML5_LEGACY_COMPAT = "<!DOCTYPE html SYSTEM \"" + Standards.HTML_5_LEGACY_WILDCARD_SYSTEMID + "\">";
    public static final String DOCTYPE_PUBLICID_PREFIX = "<!DOCTYPE html PUBLIC ";
    public static final String DOCTYPE_SYSTEMID_PREFIX = "<!DOCTYPE html SYSTEM ";
    
    private static final Pattern XML_DECLARATION_PATTERN = Pattern.compile("^\\s*(\\<\\?xml(.*?)\\?\\>)(.*?)$", Pattern.DOTALL);

   
    
    public static final String output(
            final Arguments arguments, final TemplateResolution templateResolution, 
            final Document document, final boolean outputDocType, 
            final DocTypeIdentifier publicId, final DocTypeIdentifier systemId) {
        
        Validate.notNull(arguments, "Arguments cannot be null");
        Validate.notNull(templateResolution, "Template resolution cannot be null");
        Validate.notNull(document, "Document cannot be null");
        Validate.isTrue(!outputDocType || (outputDocType && publicId != null), "DOCTYPE PUBLIC specification cannot be null");
        Validate.isTrue(!outputDocType || (outputDocType && systemId != null), "DOCTYPE SYSTEM specification cannot be null");
        
        try {
            
            final TemplateMode templateMode = templateResolution.getTemplateMode();
            
            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            
            final Transformer transformer = transformerFactory.newTransformer();

            transformer.setOutputProperty(OutputKeys.METHOD, TRANSFORMER_OUTPUT_METHOD_XML);
            if (templateMode.isXHTML() || templateMode.isHTML5()) {
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            } else {
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            }
            
            final DOMSource source = new DOMSource(document);
            
            final StringWriter stringWriter = new StringWriter();
            /*
             * If template is XHTML or HTML5, these filterWriters automatically detect <textarea/>
             * <script/> and <style/> tags and substitute them by <textarea></textarea>,
             * <script></script> and <style></style> because that is what browsers expect 
             * and they will not display the page correctly if those tags have no closing 
             * correspondent.
             * 
             * A pattern on the result String could be used instead of building these
             * filtering writers, but that is a slightly (only slightly) slower option
             * and can potentially use a little more memory than acting on the writer
             * stream directly.
             */
            final StreamResult streamResult =
                (templateMode.isXHTML() || templateMode.isHTML5()?
                        new StreamResult(new HTMLFilterWriter(stringWriter)):
                        new StreamResult(new XMLFilterWriter(stringWriter)));
            
            transformer.transform(source, streamResult);
            
            String result = stringWriter.getBuffer().toString();
            
            if (templateMode.isHTML5() &&
                (systemId.isNone() || systemId.matches(Standards.HTML_5_LEGACY_WILDCARD_SYSTEMID) || systemId.matches(""))) {
                
                if (systemId.matches(Standards.HTML_5_LEGACY_WILDCARD_SYSTEMID)) {
                    
                    result = DOCTYPE_HTML5_LEGACY_COMPAT + "\n" + result;
                    logger.debug("[THYMELEAF][{}] Output DOCTYPE: {}", TemplateEngine.threadIndex(), DOCTYPE_HTML5_LEGACY_COMPAT);
                    
                } else {
                    // systemId.isNone || systemId.equals("")
                
                    result = DOCTYPE_HTML5 + "\n" + result;
                    logger.debug("[THYMELEAF][{}] Output DOCTYPE: {}", TemplateEngine.threadIndex(), DOCTYPE_HTML5);
                    
                }
                
            } else if (outputDocType) {

                String doctype = null;
                if (!publicId.isNone()) {
                    if (!systemId.isNone()) {
                        doctype = createPublicIdAndSystemIdDoctype(publicId.getValue(), systemId.getValue());
                    } else {
                        doctype = createPublicIdDoctype(publicId.getValue());
                    }
                } else {
                    if (!systemId.isNone()) {
                        doctype = createSystemIdDoctype(systemId.getValue());
                    }
                }
                
                final Matcher xmlDeclarationMatcher = XML_DECLARATION_PATTERN.matcher(result);
                if (xmlDeclarationMatcher.matches()) {
                
                    result = xmlDeclarationMatcher.group(1) + "\n" + doctype + "\n" + xmlDeclarationMatcher.group(3);
                    
                } else {
                    
                    result = doctype + "\n" + result;
                    
                }
                
                logger.debug("[THYMELEAF][{}] Output DOCTYPE: {}", TemplateEngine.threadIndex(), doctype);
                
            }
            
            return result;
            
        } catch (final TransformerConfigurationException e) {
            throw new OutputCreationException("Error while producing template output", e);
        } catch (final TransformerException e) {
            throw new OutputCreationException("Error while producing template output", e);
        }
        
    }
    
    
    
    
    private static String createPublicIdAndSystemIdDoctype(final String publicId, final String systemId) {
        return DOCTYPE_PUBLICID_PREFIX + "\"" + publicId + "\" \"" + systemId + "\">";
    }
    
    
    private static String createPublicIdDoctype(final String publicId) {
        return DOCTYPE_PUBLICID_PREFIX + "\"" + publicId + "\" \"\">";
    }
    
    
    private static String createSystemIdDoctype(final String systemId) {
        return DOCTYPE_SYSTEMID_PREFIX + "\"" + systemId + "\">";
    }

    
    
    
    private OutputHandler() {
        super();
    }
    
    
    
    
    
    
    
    /*
     * Performs XHTML-specific transformations to XML output, as defined by
     * http://www.w3.org/TR/xslt-xquery-serialization/#xhtml-output
     * and aided by the DOMDocumentProcessor (which adds text elements
     * with \uFFFC chars to empty elements which should not be minimized).
     * 
     * This fine-tuning operations have to be performed this way because of
     * the incomplete way in which XML Transformers (esp. xalan) handle xhtml output,
     * which has to be set as method "xml" and then rely on the initial characters of
     * a PUBLIC doctype declaration for being considered XHTML... and even so they
     * do not perform all the transformations they should (in fact, many
     * implementations do absolutely none).
     */
    static final class HTMLFilterWriter extends FilterWriter {

        private static final char CHAR_ENTITY_START = '&';
        
        private static final char CHAR_REMOVABLE = '\uFFFC';
        private static final char CHAR_SOLIDUS = '/';
        private static final char CHAR_GT = '>';
        private static final char CHAR_SPACE = ' ';
        
        private boolean afterSolidus = false;
        
        
        HTMLFilterWriter(final Writer out) {
            super(out);
        }

        
        @Override
        public final void write(final int c) throws IOException {

            boolean writeChar = true;
            
            if (this.afterSolidus) {
                if (c == CHAR_GT) {
                    super.write(CHAR_SPACE);
                }
                super.write(CHAR_SOLIDUS);
                this.afterSolidus = false;
            }
                
            if (c == CHAR_SOLIDUS) {
                this.afterSolidus = true;
                writeChar = false;
            }
            
            if (c == CHAR_REMOVABLE) {
                writeChar = false;
            }
            
            if (c == TemplateEngine.CHAR_ENTITY_START_SUBSTITUTE) {
                super.write(CHAR_ENTITY_START);
                writeChar = false;
            }
            
            if (writeChar) {
                super.write(c);
            }
            
        }
        
        

        @Override
        public void close() throws IOException {
            if (this.afterSolidus) {
                super.write(CHAR_SOLIDUS);
            }
            super.close();
        }

        

        @Override
        public final void write(final char[] cbuf, final int off, final int len) throws IOException {

            boolean delegate = true;
            for (int i = off; delegate && i < off+len; i++) {
                if (cbuf[i] == CHAR_REMOVABLE ||
                    cbuf[i] == CHAR_SOLIDUS ||
                    cbuf[i] == CHAR_GT ||
                    cbuf[i] == TemplateEngine.CHAR_ENTITY_START_SUBSTITUTE) {
                    delegate = false;
                }
            }
            if (delegate) {
                if (this.afterSolidus) {
                    super.write(CHAR_SOLIDUS);
                    this.afterSolidus = false;
                }
                super.write(cbuf, off, len);
            } else {
                for (int i = off; i < off+len; i++) {
                    write(cbuf[i]);
                }
            }
            
        }
        
        

        @Override
        public final void write(final String str, final int off, final int len) throws IOException {

            boolean delegate = true;
            for (int i = off; delegate && i < off+len; i++) {
                if (str.charAt(i) == CHAR_REMOVABLE ||
                    str.charAt(i) == CHAR_SOLIDUS ||
                    str.charAt(i) == CHAR_GT ||
                    str.charAt(i) == TemplateEngine.CHAR_ENTITY_START_SUBSTITUTE) {
                    delegate = false;
                }
            }
            if (delegate) {
                if (this.afterSolidus) {
                    super.write(CHAR_SOLIDUS);
                    this.afterSolidus = false;
                }
                super.write(str, off, len);
            } else {
                for (int i = off; i < off+len; i++) {
                    write(str.charAt(i));
                }
            }
            
        }
        
        
        
    }

    
    
    
    /*
     * Converts &-symbol substitutes (from filtered entities) into
     * the original character.
     */
    static final class XMLFilterWriter extends FilterWriter {

        private static final char CHAR_ENTITY_START = '&';

        
        XMLFilterWriter(final Writer out) {
            super(out);
        }

        
        @Override
        public final void write(final int c) throws IOException {

            boolean writeChar = true;
            
            if (c == TemplateEngine.CHAR_ENTITY_START_SUBSTITUTE) {
                super.write(CHAR_ENTITY_START);
                writeChar = false;
            }
            
            if (writeChar) {
                super.write(c);
            }
            
        }
        
        

        @Override
        public void close() throws IOException {
            super.close();
        }

        

        @Override
        public final void write(final char[] cbuf, final int off, final int len) throws IOException {

            boolean delegate = true;
            for (int i = off; delegate && i < off+len; i++) {
                if (cbuf[i] == TemplateEngine.CHAR_ENTITY_START_SUBSTITUTE) {
                    delegate = false;
                }
            }
            if (delegate) {
                super.write(cbuf, off, len);
            } else {
                for (int i = off; i < off+len; i++) {
                    write(cbuf[i]);
                }
            }
            
        }
        
        

        @Override
        public final void write(final String str, final int off, final int len) throws IOException {

            boolean delegate = true;
            for (int i = off; delegate && i < off+len; i++) {
                if (str.charAt(i) == TemplateEngine.CHAR_ENTITY_START_SUBSTITUTE) {
                    delegate = false;
                }
            }
            if (delegate) {
                super.write(str, off, len);
            } else {
                for (int i = off; i < off+len; i++) {
                    write(str.charAt(i));
                }
            }
            
        }
        
        
        
    }
    
    
}
