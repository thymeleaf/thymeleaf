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

import org.thymeleaf.model.ITemplateEvent;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
abstract class AbstractTemplateEvent implements ITemplateEvent {

    private String templateName;
    private int line;
    private int col;


    /*
     * Object of this class can contain their data both as a String and as a char[] buffer. The buffer will only
     * be used internally to the 'engine' package, in order to avoid the creation of unnecessary String objects
     * (most times the parsing buffer itself will be used). Computation of the String form will be performed lazily
     * and only if specifically required.
     *
     * Objects of this class are meant to both be reused by the engine and also created fresh by the processors. This
     * should allow reducing the number of instances of this class to the minimum.
     */


    AbstractTemplateEvent() {
        super();
        resetTemplateEvent(null, -1, -1);
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




    protected void resetTemplateEvent(
            final String templateName, final int line, final int col) {
        this.templateName = templateName;
        this.line = line;
        this.col = col;
    }






    protected final void resetAsCloneOfTemplateEvent(final AbstractTemplateEvent original) {
        this.templateName = original.templateName;
        this.line = original.line;
        this.col = original.col;
    }


}
