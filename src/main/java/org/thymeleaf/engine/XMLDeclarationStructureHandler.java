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
package org.thymeleaf.engine;

import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
final class XMLDeclarationStructureHandler implements IXMLDeclarationStructureHandler {


    boolean replaceWithMarkup;
    IMarkup replaceWithMarkupValue;
    boolean replaceWithMarkupProcessable;

    boolean removeXMLDeclaration;




    XMLDeclarationStructureHandler() {
        super();
        reset();
    }



    public void replaceWith(final IMarkup markup, final boolean processable) {
        reset();
        Validate.notNull(markup, "Markup cannot be null");
        this.replaceWithMarkup = true;
        this.replaceWithMarkupValue = markup;
        this.replaceWithMarkupProcessable = processable;
    }


    public void removeXMLDeclaration() {
        reset();
        this.removeXMLDeclaration = true;
    }




    public void reset() {

        this.replaceWithMarkup = false;
        this.replaceWithMarkupValue = null;
        this.replaceWithMarkupProcessable = false;

        this.removeXMLDeclaration = false;

    }


}
