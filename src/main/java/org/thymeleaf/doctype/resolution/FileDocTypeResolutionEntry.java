/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2013, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.doctype.resolution;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.thymeleaf.doctype.DocTypeIdentifier;
import org.thymeleaf.util.Validate;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class FileDocTypeResolutionEntry implements IDocTypeResolutionEntry {
    
    private final DocTypeIdentifier systemID;
    private final DocTypeIdentifier publicID;
    private final File file;
    
    
    public FileDocTypeResolutionEntry(
            final DocTypeIdentifier publicID, final DocTypeIdentifier systemID, final File file) {
        super();
        Validate.notNull(file, "File cannot be null");
        this.publicID = publicID;
        this.systemID = systemID;
        this.file = file;
    }
    
    
    
    public DocTypeIdentifier getSystemID() {
        return this.systemID;
    }

    public DocTypeIdentifier getPublicID() {
        return this.publicID;
    }

    public InputSource createInputSource() throws SAXException, IOException {
        return new InputSource(new FileInputStream(this.file));
    }



    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.file == null) ? 0 : this.file.hashCode());
        result = prime * result
                + ((this.publicID == null) ? 0 : this.publicID.hashCode());
        result = prime * result
                + ((this.systemID == null) ? 0 : this.systemID.hashCode());
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
        final FileDocTypeResolutionEntry other = (FileDocTypeResolutionEntry) obj;
        if (this.file == null) {
            if (other.file != null) {
                return false;
            }
        } else if (!this.file.equals(other.file)) {
            return false;
        }
        if (this.publicID == null) {
            if (other.publicID != null) {
                return false;
            }
        } else if (!this.publicID.equals(other.publicID)) {
            return false;
        }
        if (this.systemID == null) {
            if (other.systemID != null) {
                return false;
            }
        } else if (!this.systemID.equals(other.systemID)) {
            return false;
        }
        return true;
    }
    
    
}
