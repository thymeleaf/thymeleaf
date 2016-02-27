/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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
import org.thymeleaf.engine.ElementAttributes;
import org.thymeleaf.model.IElementAttributes;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class StandardProcessorUtils {


    public static void setAttribute(
            final IElementAttributes attributes,
            final AttributeDefinition attributeDefinition, final String completeName,
            final String value) {

        if (attributes instanceof ElementAttributes) {
            ((ElementAttributes) attributes).setAttribute(attributeDefinition, completeName, value, null);
        } else {
            attributes.setAttribute(completeName, value);
        }

    }


    public static void replaceAttribute(
            final IElementAttributes attributes,
            final AttributeName oldName,
            final AttributeDefinition newAttributeDefinition, final String completeNewName,
            final String value) {

        if (attributes instanceof ElementAttributes) {
            ((ElementAttributes) attributes).replaceAttribute(oldName, newAttributeDefinition, completeNewName, value, null);
        } else {
            attributes.replaceAttribute(oldName, completeNewName, value);
        }

    }






    private StandardProcessorUtils() {
        super();
    }



}
