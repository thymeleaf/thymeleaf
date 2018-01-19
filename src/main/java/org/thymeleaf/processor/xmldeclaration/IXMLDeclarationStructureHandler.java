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
package org.thymeleaf.processor.xmldeclaration;

import org.thymeleaf.model.IModel;

/**
 * <p>
 *   Structure handler class meant to be used by {@link IXMLDeclarationProcessor} implementations.
 * </p>
 * <p>
 *   Structure handlers allow processors to instruct the engine to perform a series of actions that cannot
 *   be done directly from the processors themselves, usually because these actions are applied or have effects
 *   on scopes broader than the processed events themselves.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @see IXMLDeclarationProcessor
 * @since 3.0.0
 * 
 */
public interface IXMLDeclarationStructureHandler {


    /**
     * <p>
     *   Resets all actions specified so far for the current processor execution.
     * </p>
     */
    public void reset();

    /**
     * <p>
     *   Instructs the engine to set new values into the properties of the XMLDeclaration event being processed.
     * </p>
     *
     * @param keyword the new keyword value
     * @param version the new version value (might be null)
     * @param encoding the new encoding value (might be null)
     * @param standalone the new standalone value (might be null)
     */
    public void setXMLDeclaration(
            final String keyword, final String version, final String encoding, final String standalone);

    /**
     * <p>
     *   Instructs the engine to replace the current event with the specified model (a {@link IModel}).
     * </p>
     *
     * @param model the model to be used as a replacement.
     * @param processable whether the model should be considered <em>processable</em> or not.
     */
    public void replaceWith(final IModel model, final boolean processable);

    /**
     * <p>
     *   Instructs the engine to remove the entire event that is being processed.
     * </p>
     */
    public void removeXMLDeclaration();

}

