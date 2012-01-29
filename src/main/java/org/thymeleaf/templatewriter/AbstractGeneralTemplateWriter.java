package org.thymeleaf.templatewriter;

import java.io.IOException;
import java.io.Writer;

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.Standards;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.doctype.DocTypeIdentifier;
import org.thymeleaf.dom.Attribute;
import org.thymeleaf.dom.CDATASection;
import org.thymeleaf.dom.Comment;
import org.thymeleaf.dom.DocType;
import org.thymeleaf.dom.Document;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.dom.Text;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.Validate;


/**
 * 
 * @since 2.0.0
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 */
public abstract class AbstractGeneralTemplateWriter implements ITemplateWriter {


    private static final char[] COMMENT_PREFIX = "<!--".toCharArray();
    private static final char[] COMMENT_SUFFIX = "-->".toCharArray();

    private static final char[] CDATA_PREFIX = "<![CDATA[".toCharArray();
    private static final char[] CDATA_SUFFIX = "]]>".toCharArray();

    
    
    public void write(final Arguments arguments, final Writer writer, final Document document) 
                throws IOException {
        Validate.notNull(document, "Document cannot be null");
        writeDocument(arguments, writer, document);
    }

    
    protected abstract boolean writeXmlDeclaration();
    
    protected abstract boolean useXhtmlTagMinimizationRules();
    
    
    
    
    protected void writeDocument(final Arguments arguments, final Writer writer, final Document document) 
                throws IOException {
        
        if (writeXmlDeclaration()) {
            writer.write(Standards.XML_DECLARATION);
            writer.write('\n');
        }
        if (document.hasDocType()) {
            writeDocType(arguments, writer, document.getDocType());
            writer.write('\n');
        }
        if (document.hasChildren()) {
            final Node[] children = document.unsafeGetChildrenNodeArray();
            final int childrenLen = document.numChildren();
            for (int i = 0; i < childrenLen; i++) {
                writeNode(arguments, writer, children[i]);
            }
        }
        
    }
    
    
    @SuppressWarnings("unused")
    protected void writeDocType(final Arguments arguments, final Writer writer, final DocType docType) 
            throws IOException {
        
        DocTypeIdentifier writablePublicId = docType.getProcessedPublicId();
        DocTypeIdentifier writableSystemId = docType.getProcessedSystemId();
        
        if (!docType.isProcessed()) {
            writablePublicId = DocTypeIdentifier.forValue(docType.getPublicId());
            writableSystemId = DocTypeIdentifier.forValue(docType.getSystemId());
        }
        
        writer.write("<!DOCTYPE ");
        writer.write(docType.getRootElementName());
        if (!writablePublicId.isNone()) {
            writer.write(" PUBLIC \"");
            writablePublicId.write(writer);
            writer.write("\"");
        }
        if (!writableSystemId.isNone()) {
            if (writablePublicId.isNone()) {
                writer.write(" SYSTEM");
            }
            writer.write(" \"");
            writableSystemId.write(writer);
            writer.write("\"");
        }
        writer.write(">");
    
    }

    
    
    public void writeNode(final Arguments arguments, final Writer writer, final Node node) 
            throws IOException {
    
        Validate.notNull(arguments, "Arguments cannot be null");
        
        if (node instanceof Element) {
            writeElement(arguments, writer, (Element)node);
        } else if (node instanceof Text) {
            writeText(arguments, writer, (Text)node);
        } else if (node instanceof Comment) {
            writeComment(arguments, writer, (Comment)node);
        } else if (node instanceof CDATASection) {
            writeCDATASection(arguments, writer, (CDATASection)node);
        } else if (node instanceof Document) {
            writeDocument(arguments, writer, (Document)node);
        } else {
            throw new IllegalStateException("Cannot write node of class \"" + node.getClass().getName());
        }
    
    }

    
    
    protected void writeElement(final Arguments arguments, final Writer writer, final Element element) 
            throws IOException {
        
        writer.write('<');
        writer.write(element.getOriginalName());
        if (element.hasAttributes()) {
            
            final Configuration configuration = arguments.getConfiguration();
            
            final Attribute[] attributes = element.unsafeGetAttributes();
            final int attributesLen = element.numAttributes();
            
            for (int i = 0; i < attributesLen; i++) {
                
                final Attribute attribute = attributes[i];
                boolean writeAttribute = true;
                
                if (attribute.isXmlnsAttribute()) {
                    final String xmlnsPrefix = attribute.getXmlnsPrefix();
                    if (configuration.isPrefixManaged(xmlnsPrefix)) {
                        writeAttribute = configuration.isLenient(xmlnsPrefix);
                    }
                } else if (attribute.hasPrefix()) {
                    final String prefix = attribute.getNormalizedPrefix();
                    if (configuration.isPrefixManaged(prefix) && !configuration.isLenient(prefix)) {
                        throw new TemplateProcessingException(
                                "Error processing template: dialect prefix \"" + attribute.getNormalizedPrefix() + "\" " +
                                "is set as non-lenient but attribute \"" + attribute.getOriginalName() + "\" has not " +
                                "been removed during process", TemplateEngine.threadTemplateName(), element.getLineNumber());
                    }
                }
                if (writeAttribute) {
                    writer.write(' ');
                    writer.write(attribute.getOriginalName());
                    writer.write('=');
                    writer.write('\"');
                    writer.write(attribute.getValue());
                    writer.write('\"');
                }
            }
        }
        if (element.hasChildren()) {
            writer.write('>');
            final Node[] children = element.unsafeGetChildrenNodeArray();
            final int childrenLen = element.numChildren();
            for (int i = 0; i < childrenLen; i++) {
                writeNode(arguments, writer, children[i]);
            }
            writer.write('<');
            writer.write('/');
            writer.write(element.getOriginalName());
            writer.write('>');
        } else {
            if (useXhtmlTagMinimizationRules()) {
                if (element.isMinimizableIfWeb()) {
                    writer.write(' ');
                    writer.write('/');
                    writer.write('>');
                } else {
                    writer.write('>');
                    writer.write('<');
                    writer.write('/');
                    writer.write(element.getOriginalName());
                    writer.write('>');
                }
            } else {
                writer.write('/');
                writer.write('>');
            }
        }
    }

    
    
    @SuppressWarnings("unused")
    protected void writeCDATASection(final Arguments arguments, final Writer writer, final CDATASection cdataSection) 
            throws IOException {
        writer.write(CDATA_PREFIX);
        writer.write(cdataSection.unsafeGetContentCharArray());
        writer.write(CDATA_SUFFIX);
    }

    
    
    @SuppressWarnings("unused")
    protected void writeComment(final Arguments arguments, final Writer writer, final Comment comment) 
            throws IOException {
        writer.write(COMMENT_PREFIX);
        writer.write(comment.unsafeGetContentCharArray());
        writer.write(COMMENT_SUFFIX);
    }

    
    
    @SuppressWarnings("unused")
    protected void writeText(final Arguments arguments, final Writer writer, final Text text) 
            throws IOException {
        writer.write(text.unsafeGetContentCharArray());
    }
    

    
    
}
