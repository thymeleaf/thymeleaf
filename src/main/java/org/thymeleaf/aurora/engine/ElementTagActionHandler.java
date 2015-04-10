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
final class ElementTagActionHandler implements IElementTagActionHandler {


    boolean setBodyText;
    String setBodyTextValue;
    boolean setBodyTextProcessable;

    boolean setBodyQueue;
    ITemplateHandlerEventQueue setBodyQueueValue;
    boolean setBodyQueueProcessable;

    boolean replaceWithText;
    String replaceWithTextValue;
    boolean replaceWithTextProcessable;

    boolean replaceWithQueue;
    ITemplateHandlerEventQueue replaceWithQueueValue;
    boolean replaceWithQueueProcessable;

    boolean removeElement;

    boolean removeTag;


    ElementTagActionHandler() {
        super();
        reset();
    }



    public void setBody(final String text, final boolean processable) {
        reset();
        Validate.notNull(text, "Text cannot be null");
        this.setBodyText = true;
        this.setBodyTextValue = text;
        this.setBodyTextProcessable = processable;
    }


    public void setBody(final ITemplateHandlerEventQueue eventQueue, final boolean processable) {
        reset();
        Validate.notNull(eventQueue, "Event Queue cannot be null");
        this.setBodyQueue = true;
        this.setBodyQueueValue = eventQueue;
        this.setBodyQueueProcessable = processable;
    }


    public void replaceWith(final String text, final boolean processable) {
        reset();
        Validate.notNull(text, "Text cannot be null");
        this.replaceWithText = true;
        this.replaceWithTextValue = text;
        this.replaceWithTextProcessable = processable;
    }


    public void replaceWith(final ITemplateHandlerEventQueue eventQueue, final boolean processable) {
        reset();
        Validate.notNull(eventQueue, "Event Queue cannot be null");
        this.replaceWithQueue = true;
        this.replaceWithQueueValue = eventQueue;
        this.replaceWithQueueProcessable = processable;
    }


    public void removeElement() {
        reset();
        this.removeElement = true;
    }


    public void removeTag() {
        reset();
        this.removeTag = true;
    }


    public void reset() {

        this.setBodyText = false;
        this.setBodyTextValue = null;
        this.setBodyTextProcessable = false;

        this.setBodyQueue = false;
        this.setBodyQueueValue = null;
        this.setBodyQueueProcessable = false;

        this.replaceWithText = false;
        this.replaceWithTextValue = null;
        this.replaceWithTextProcessable = false;

        this.replaceWithQueue = false;
        this.replaceWithQueueValue = null;
        this.replaceWithQueueProcessable = false;

        this.removeElement = false;

        this.removeTag = false;

    }


}
