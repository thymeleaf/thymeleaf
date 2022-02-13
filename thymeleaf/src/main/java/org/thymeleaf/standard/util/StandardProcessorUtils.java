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
package org.thymeleaf.standard.util;

import org.thymeleaf.engine.AttributeDefinition;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.ElementTagStructureHandler;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

/**
 * <p>
 *   Internal utility methods for modifying the state of events at the StandarDialects.
 * </p>
 * <p>
 *   This class should only be used <strong>internally</strong>.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class StandardProcessorUtils {



    public static void replaceAttribute(
            final IElementTagStructureHandler structureHandler,
            final AttributeName oldAttributeName,
            final AttributeDefinition attributeDefinition, final String attributeName, final String attributeValue) {

        if (structureHandler instanceof ElementTagStructureHandler) {
            ((ElementTagStructureHandler) structureHandler).replaceAttribute(oldAttributeName, attributeDefinition, attributeName, attributeValue, null);
        } else {
            structureHandler.replaceAttribute(oldAttributeName, attributeName, attributeValue);
        }

    }


    public static void setAttribute(
            final IElementTagStructureHandler structureHandler,
            final AttributeDefinition attributeDefinition, final String attributeName, final String attributeValue) {

        if (structureHandler instanceof ElementTagStructureHandler) {
            ((ElementTagStructureHandler) structureHandler).setAttribute(attributeDefinition, attributeName, attributeValue, null);
        } else {
            structureHandler.setAttribute(attributeName, attributeValue);
        }

    }




    private StandardProcessorUtils() {
        super();
    }



}
