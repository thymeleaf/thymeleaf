package org.thymeleaf.dom;

import java.io.Serializable;

import org.thymeleaf.Configuration;
import org.thymeleaf.doctype.DocTypeIdentifier;
import org.thymeleaf.doctype.translation.IDocTypeTranslation;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Models a DOCTYPE declaration in an XML-based template document.
 * </p>
 * <p>
 *   Objects of this class contain both the original PUBLICID and SYSTEMID,
 *   and also a <i>processed</i> version of both, which can be the result
 *   of applying DOCTYPE translations. 
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public final class DocType implements Serializable {
    
    private static final long serialVersionUID = -5122946925754578948L;

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
    
    public DocTypeIdentifier getProcessedPublicId() {
        return this.processedPublicId;
    }


    public DocTypeIdentifier getProcessedSystemId() {
        return this.processedSystemId;
    }

    
    /**
     * <p>
     *   Returns whether this DOCTYPE has already been processed -and therefore
     *   translations have been applied- or not.
     * </p>
     * 
     * @return true if the DOCTYPE has already been processed, false if not.
     */
    public boolean isProcessed() {
        return this.processed;
    }

    

    /**
     * <p>
     *   Process this DOCTYPE, including any applicable translations.
     * </p>
     * 
     * @param configuration the configuration to be applied.
     */
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
