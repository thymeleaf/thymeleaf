/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.templateparser.xmldom;

import javax.xml.parsers.DocumentBuilder;

import org.thymeleaf.util.ResourcePool;

/**
 * <p>
 *   Parses XML documents, using a standard validating DOM parser.
 * </p>
 * 
 * <p>
 *   This implementation first builds a DOM tree using the
 *   standard DOM API, and then translates this tree into a
 *   Thymeleaf-specific one. It also populates tree nodes with 
 *   basic location information (document name only).
 * </p>
 * 
 * @since 2.0.0
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 */
public abstract class AbstractValidatingDOMTemplateParser extends AbstractNonValidatingDOMTemplateParser {
    
    
    
    private final ResourcePool<DocumentBuilder> pool;

    
    
    protected AbstractValidatingDOMTemplateParser(final int poolSize) {
        super(poolSize);
        this.pool = createDocumentBuilders(poolSize, true);
    }
    
    


    @Override
    protected ResourcePool<DocumentBuilder> getPool() {
        return this.pool;
    }




    @Override
    protected boolean shouldAddThymeleafRootToParser() {
        return false;
    }



}
