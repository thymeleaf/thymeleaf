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
package org.thymeleaf.templateparser;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.doctype.resolution.IDocTypeResolutionEntry;
import org.thymeleaf.exceptions.EntityResolutionException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class EntityResolver implements org.xml.sax.EntityResolver {
    
    private static final Logger logger = LoggerFactory.getLogger(EntityResolver.class);
    

    private final Set<IDocTypeResolutionEntry> docTypeResolutionEntries;

    
    public EntityResolver(final Configuration configuration) {
        super();
        this.docTypeResolutionEntries = configuration.getDocTypeResolutionEntries();
    }
    
    
    public InputSource resolveEntity(final String publicID, final String systemID)
            throws SAXException, IOException {

        if (logger.isTraceEnabled()) {
            if (publicID == null) {
                logger.trace("[THYMELEAF][{}] Resolving entity SYSTEM \"{}\"", TemplateEngine.threadIndex(), systemID);
            } else {
                logger.trace("[THYMELEAF][{}] Resolving entity PUBLIC \"{}\" \"{}\"", new Object[] {TemplateEngine.threadIndex(), publicID, systemID});
            }
        }
        
        for (final IDocTypeResolutionEntry entry : this.docTypeResolutionEntries) {
            if (entry.getPublicID().matches(publicID) && entry.getSystemID().matches(systemID)) {
                return entry.createInputSource();
            }
        }
        
        throw new EntityResolutionException(
                "Unsupported entity requested with PUBLICID \"" + publicID + "\" and " +
        		"SYSTEMID \"" + systemID + "\". Make sure a corresponding " + 
				IDocTypeResolutionEntry.class.getName() + " implementation is provided " +
				"by you dialect");
    }
    
    
}
