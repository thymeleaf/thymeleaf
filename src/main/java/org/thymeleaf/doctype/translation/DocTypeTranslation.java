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
package org.thymeleaf.doctype.translation;

import org.thymeleaf.doctype.DocTypeIdentifier;
import org.thymeleaf.util.Validate;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public class DocTypeTranslation implements IDocTypeTranslation {

    
    private final DocTypeIdentifier sourcePublicID;
    private final DocTypeIdentifier sourceSystemID;
    private final DocTypeIdentifier targetPublicID;
    private final DocTypeIdentifier targetSystemID;
    

    
    public DocTypeTranslation(
            final DocTypeIdentifier sourcePublicID, final DocTypeIdentifier sourceSystemID, 
            final DocTypeIdentifier targetPublicID, final DocTypeIdentifier targetSystemID) {
        super();
        Validate.notNull(sourcePublicID, "Source PUBLICID cannot be null");
        Validate.notNull(sourceSystemID, "Source SYSTEMID cannot be null");
        Validate.notNull(targetPublicID, "Target PUBLICID cannot be null");
        Validate.notNull(targetSystemID, "Target SYSTEMID cannot be null");
        this.sourcePublicID = sourcePublicID;
        this.sourceSystemID = sourceSystemID;
        this.targetPublicID = targetPublicID;
        this.targetSystemID = targetSystemID;
    }

    public DocTypeIdentifier getSourcePublicID() {
        return this.sourcePublicID;
    }

    public DocTypeIdentifier getSourceSystemID() {
        return this.sourceSystemID;
    }

    public DocTypeIdentifier getTargetPublicID() {
        return this.targetPublicID;
    }

    public DocTypeIdentifier getTargetSystemID() {
        return this.targetSystemID;
    }
    
}
