package org.thymeleaf.dom;

import java.io.IOException;
import java.io.Writer;

import org.thymeleaf.Configuration;
import org.thymeleaf.doctype.DocTypeIdentifier;
import org.thymeleaf.doctype.translation.IDocTypeTranslation;
import org.thymeleaf.util.Validate;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.2
 *
 */
public final class DocType {
    

    private final String rootElementName;
    private final String publicId;
    private final String systemId;
    
    private boolean processed;
    private DocTypeIdentifier processedPublicId;
    private DocTypeIdentifier processedSystemId;

    
    public DocType(final String rootElementName, final String publicId, final String systemId) {
        super();
        Validate.notNull(rootElementName, "Root element name cannot be null");
        this.rootElementName = rootElementName;
        this.publicId = publicId;
        this.systemId = systemId;
        this.processed = false;
        this.processedPublicId = null;
        this.processedSystemId = null;
    }

    
    public String getRootElementName() {
        return this.rootElementName;
    }
    
    
    public String getPublicId() {
        return this.publicId;
    }

    
    public String getSystemId() {
        return this.systemId;
    }
    
    
    public void process(final Configuration configuration) {
        if (!this.processed) {
            final IDocTypeTranslation translation =
                    configuration.getDocTypeTranslationBySource(this.publicId, this.systemId);
            if (translation != null) {
                this.processedPublicId = translation.getTargetPublicID();
                this.processedSystemId = translation.getTargetSystemID();
                this.processed = true;
            }
        }
    }

    
    public void write(final Writer writer) throws IOException {
        
        DocTypeIdentifier writablePublicId = this.processedPublicId;
        DocTypeIdentifier writableSystemId = this.processedSystemId;
        
        if (!this.processed) {
            writablePublicId = DocTypeIdentifier.forValue(this.publicId);
            writableSystemId = DocTypeIdentifier.forValue(this.systemId);
        }
        
        writer.write("<!DOCTYPE ");
        writer.write(this.rootElementName);
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
    
    


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.rootElementName.hashCode();
        result = prime * result
                + ((this.publicId == null) ? 0 : this.publicId.hashCode());
        result = prime * result
                + ((this.systemId == null) ? 0 : this.systemId.hashCode());
        return result;
    }


    
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DocType other = (DocType) obj;
        if (!this.rootElementName.equals(other.rootElementName)) {
            return false;
        }
        if (this.publicId == null) {
            if (other.publicId != null) {
                return false;
            }
        } else if (!this.publicId.equals(other.publicId)) {
            return false;
        }
        if (this.systemId == null) {
            if (other.systemId != null) {
                return false;
            }
        } else if (!this.systemId.equals(other.systemId)) {
            return false;
        }
        return true;
    }


    
    
    
    
    
    public static final DocType translateDOMDocumentType(final org.w3c.dom.DocumentType domDocumentType) {
        
        if (domDocumentType == null) {
            return null;
        }
        final String rootElementName = domDocumentType.getName();
        return new DocType(rootElementName, domDocumentType.getPublicId(), domDocumentType.getSystemId());
        
    }
    
    
}
