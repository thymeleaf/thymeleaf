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

import org.thymeleaf.model.ITemplateEvent;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
abstract class AbstractTemplateEvent implements ITemplateEvent {

    final String templateName;
    final int line;
    final int col;


    AbstractTemplateEvent() {
        this(null, -1, -1);
    }


    AbstractTemplateEvent(final String templateName, final int line, final int col) {
        super();
        this.templateName = templateName;
        this.line = line;
        this.col = col;
    }


    AbstractTemplateEvent(final AbstractTemplateEvent original) {
        super();
        this.templateName = original.templateName;
        this.line = original.line;
        this.col = original.col;
    }



    public final boolean hasLocation() {
        return (this.templateName != null && this.line != -1 && this.col != -1);
    }

    public final String getTemplateName() {
        return this.templateName;
    }

    public final int getLine() {
        return this.line;
    }

    public final int getCol() {
        return this.col;
    }



}
