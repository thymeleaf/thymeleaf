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
package org.thymeleaf.aurora.engine;

import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class ElementTagActionHandler implements IElementTagActionHandler {


    boolean setBodyText;
    String setBodyTextValue;
    boolean setBodyTextShouldEscape;

    boolean setBodyQueue;
    ITemplateHandlerEventQueue setBodyQueueValue;

    boolean replaceWithText;
    String replaceWithTextValue;
    boolean replaceWithTextShouldEscape;

    boolean replaceWithQueue;
    ITemplateHandlerEventQueue replaceWithQueueValue;



    ElementTagActionHandler() {
        super();
        reset();
    }



    public void setBody(final String text, final boolean shouldEscape) {
        reset();
        Validate.notNull(text, "Text cannot be null");
        this.setBodyText = true;
        this.setBodyTextValue = text;
        this.setBodyTextShouldEscape = shouldEscape;
    }


    public void setBody(final ITemplateHandlerEventQueue eventQueue) {
        reset();
        Validate.notNull(eventQueue, "Event Queue cannot be null");
        this.setBodyQueue = true;
        this.setBodyQueueValue = eventQueue;
    }


    public void replaceWith(final String text, final boolean shouldEscape) {
        reset();
        Validate.notNull(text, "Text cannot be null");
        this.replaceWithText = true;
        this.replaceWithTextValue = text;
        this.replaceWithTextShouldEscape = shouldEscape;
    }


    public void replaceWith(final ITemplateHandlerEventQueue eventQueue) {
        reset();
        Validate.notNull(eventQueue, "Event Queue cannot be null");
        this.replaceWithQueue = true;
        this.replaceWithQueueValue = eventQueue;
    }


    public void reset() {

        this.setBodyText = false;
        this.setBodyTextValue = null;
        this.setBodyTextShouldEscape = false;

        this.setBodyQueue = false;
        this.setBodyQueueValue = null;

        this.replaceWithText = false;
        this.replaceWithTextValue = null;
        this.replaceWithTextShouldEscape = false;

        this.replaceWithQueue = false;
        this.replaceWithQueueValue = null;

    }


}
