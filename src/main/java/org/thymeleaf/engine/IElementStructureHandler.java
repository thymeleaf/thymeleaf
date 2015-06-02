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

import org.thymeleaf.inline.ITextInliner;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public interface IElementStructureHandler {


    public void reset();

    public void setLocalVariable(final String name, final Object value);
    public void removeLocalVariable(final String name);

    public void setSelectionTarget(final Object selectionTarget);

    public void setTextInliner(final ITextInliner textInliner);

    public void setBody(final String text, final boolean processable);
    public void setBody(final IMarkup markup, final boolean processable);

    public void insertBefore(final IMarkup markup); // cannot be processable
    public void insertAfter(final IMarkup markup, final boolean processable);

    public void replaceWith(final String text, final boolean processable);
    public void replaceWith(final IMarkup markup, final boolean processable);


    public void removeElement();
    public void removeTag();
    public void removeBody();
    public void removeAllButFirstChild();

    public void iterateElement(final String iterVariableName, final String iterStatusVariableName, final Object iteratedObject);

}

