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
final class CDATASectionStructureHandler implements ICDATASectionStructureHandler {


    boolean replaceWithQueue;
    ITemplateHandlerEventQueue replaceWithQueueValue;
    boolean replaceWithQueueProcessable;

    boolean removeCDATASection;




    CDATASectionStructureHandler() {
        super();
        reset();
    }



    public void replaceWith(final ITemplateHandlerEventQueue eventQueue, final boolean processable) {
        reset();
        Validate.notNull(eventQueue, "Event Queue cannot be null");
        this.replaceWithQueue = true;
        this.replaceWithQueueValue = eventQueue;
        this.replaceWithQueueProcessable = processable;
    }


    public void removeCDATASection() {
        reset();
        this.removeCDATASection = true;
    }




    public void reset() {

        this.replaceWithQueue = false;
        this.replaceWithQueueValue = null;
        this.replaceWithQueueProcessable = false;

        this.removeCDATASection = false;

    }


}
