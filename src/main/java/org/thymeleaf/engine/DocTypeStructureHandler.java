/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
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

import org.thymeleaf.model.IModel;
import org.thymeleaf.processor.doctype.IDocTypeStructureHandler;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Structure handler implementation, internally used by the engine.
 * </p>
 * <p>
 *   This class should not be directly used from outside the engine.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class DocTypeStructureHandler implements IDocTypeStructureHandler {

    boolean setDocType;
    String setDocTypeKeyword;
    String setDocTypeElementName;
    String setDocTypePublicId;
    String setDocTypeSystemId;
    String setDocTypeInternalSubset;

    boolean replaceWithModel;
    IModel replaceWithModelValue;
    boolean replaceWithModelProcessable;

    boolean removeDocType;




    DocTypeStructureHandler() {
        super();
        reset();
    }




    public void setDocType(
            final String keyword, final String elementName,
            final String publicId, final String systemId, final String internalSubset) {
        reset();
        Validate.notNull(keyword, "Keyword cannot be null");
        Validate.notNull(elementName, "Element name cannot be null");
        this.setDocType = true;
        this.setDocTypeKeyword = keyword;
        this.setDocTypeElementName = elementName;
        this.setDocTypePublicId = publicId;
        this.setDocTypeSystemId = systemId;
        this.setDocTypeInternalSubset = internalSubset;
    }


    public void replaceWith(final IModel model, final boolean processable) {
        reset();
        Validate.notNull(model, "Model cannot be null");
        this.replaceWithModel = true;
        this.replaceWithModelValue = model;
        this.replaceWithModelProcessable = processable;
    }


    public void removeDocType() {
        reset();
        this.removeDocType = true;
    }




    public void reset() {

        this.setDocType = false;
        this.setDocTypeKeyword = null;
        this.setDocTypeElementName = null;
        this.setDocTypePublicId = null;
        this.setDocTypeSystemId = null;
        this.setDocTypeInternalSubset = null;

        this.replaceWithModel = false;
        this.replaceWithModelValue = null;
        this.replaceWithModelProcessable = false;

        this.removeDocType = false;

    }


}
