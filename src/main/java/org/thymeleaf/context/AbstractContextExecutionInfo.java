/*
 * =============================================================================
 * 
 *   Copyright (c) 2011, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.context;

import java.util.Calendar;


/**
 * <p>
 *   Abstract class for {@link IContextExecutionInfo} implementations, providing some of the
 *   features required to implement this interface.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public abstract class AbstractContextExecutionInfo implements IContextExecutionInfo {
    
    private final String templateName;
    private final Calendar now;

    
    protected AbstractContextExecutionInfo(final String templateName, final Calendar now) {
        super();
        this.templateName = templateName;
        this.now = now;
    }
    
    
    /**
     * <p>
     *   Returns the template name.
     * </p>
     * <p>
     *   Note that the same template can be resolved with different names due to
     *   aliases, links, etc. This template name refers to the one used to call
     *   the {@link org.thymeleaf.TemplateEngine#process(String, IContext)} method.
     * </p>
     * 
     * @return the template name
     */
    public String getTemplateName() {
        return this.templateName;
    }
    

    /**
     * <p>
     *   Returns the current date and time (from the moment of template execution).
     * </p>
     *
     * @return the current date and time, as a Calendar
     */
    public Calendar getNow() {
        return this.now;
    }

    
}
