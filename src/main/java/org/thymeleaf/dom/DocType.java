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
package org.thymeleaf.dom;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;

import org.thymeleaf.Configuration;
import org.thymeleaf.doctype.DocTypeIdentifier;
import org.thymeleaf.doctype.translation.IDocTypeTranslation;
import org.thymeleaf.exceptions.TemplateInputException;
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

    private final String originalDocTypeClause;
    private final String rootElementName;
    private final String publicId;
    private final String systemId;
    
    private final char[] firstToken;
    private final char[] thirdToken;
    
    private boolean processed;
    private DocTypeIdentifier processedPublicId;
    private DocTypeIdentifier processedSystemId;

    
    public DocType(final String rootElementName, final String publicId, final String systemId) {
        this(rootElementName, publicId, systemId, null);
    }
    
    public DocType(final String rootElementName, final String publicId, final String systemId,
            final String originalDocTypeClause) {
        
        super();
        
        Validate.notNull(rootElementName, "Root element name cannot be null");
        this.originalDocTypeClause = originalDocTypeClause;
        this.rootElementName = rootElementName;
        this.publicId = publicId;
        this.systemId = systemId;
        this.processed = false;
        this.processedPublicId = null;
        this.processedSystemId = null;
        
        if (this.originalDocTypeClause != null) {
            final String[] docTypeClauseTokens = extractDocTypeTokens(this.originalDocTypeClause);
            this.firstToken = (docTypeClauseTokens[0] == null? null : docTypeClauseTokens[0].toCharArray());
            this.thirdToken = (docTypeClauseTokens[2] == null? null : docTypeClauseTokens[2].toCharArray());
        } else {
            this.firstToken = null;
            this.thirdToken = null;
        }
        
    }

    
    public String getOriginalDocTypeClause() {
        return this.originalDocTypeClause;
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


    
    
    
    private static String[] extractDocTypeTokens(final String docTypeClause) {
        
        try {
            final int docTypeClauseLen = docTypeClause.length();
            
            boolean inToken = true;
            int tokenDelim = 0;
            int currentToken = 0;
            
            final String[] result = new String[3];
            
            for (int i = 0; i < docTypeClauseLen; i++) {
                final char c = docTypeClause.charAt(i);
                if (c == ' ' || c == '\t' || c == '>') {
                    if (inToken) {
                        result[currentToken++] = docTypeClause.substring(tokenDelim,i).trim();
                        tokenDelim = i;
                        inToken = false;
                        if (currentToken > 2) {
                            return result;
                        }
                    }
                } else {
                    inToken = true;
                }
            }
                
            return result;
        
        } catch (final Exception ignored) {
            throw new TemplateInputException("DOCTYPE clause has bad format: \"" + docTypeClause + "\"");
        }
        
    }
    

    
    
    public void write(final Writer writer) throws IOException {
        
        DocTypeIdentifier writablePublicId = getProcessedPublicId();
        DocTypeIdentifier writableSystemId = getProcessedSystemId();
        
        if (!isProcessed()) {
            writablePublicId = DocTypeIdentifier.forValue(getPublicId());
            writableSystemId = DocTypeIdentifier.forValue(getSystemId());
        }
        
        writer.write(this.firstToken);
        writer.write(' ');
        writer.write(getRootElementName());
        if (!writablePublicId.isNone()) {
            writer.write(' ');
            if (!isProcessed()) {
                writer.write(this.thirdToken);
            } else {
                writer.write("PUBLIC");
            }
            writer.write(" \"");
            writablePublicId.write(writer);
            writer.write("\"");
        }
        if (!writableSystemId.isNone()) {
            if (writablePublicId.isNone()) {
                writer.write(' ');
                if (!isProcessed()) {
                    writer.write(this.thirdToken);
                } else {
                    writer.write("SYSTEM");
                }
            }
            writer.write(" \"");
            writableSystemId.write(writer);
            writer.write("\"");
        }
        writer.write(">");
    
    }
    
    
    
    
    
}
