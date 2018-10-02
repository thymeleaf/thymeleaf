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
package org.thymeleaf.spring5.webflow.view;

import org.springframework.js.ajax.AjaxHandler;
import org.springframework.web.servlet.View;


/**
 * <p>
 *   Interface defining getter and setter methods for an 
 *   {@code ajaxHandler} property in Views, so that they can
 *   be used in Spring AJAX environments.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.3
 *
 */
public interface AjaxEnabledView extends View {


    
    /**
     * <p>
     *   Return the AJAX handler (from Spring Javascript) used
     *   to determine whether a request is an AJAX request or not.
     * </p>
     * <p>
     *   Views implementing this interface should be used with an instance of
     *   {@link AjaxThymeleafViewResolver} or any of its subclasses,
     *   so that {@link #setAjaxHandler(AjaxHandler)} can be called by
     *   the resolver when resolving the view, setting the default
     *   AJAX handler being used.
     * </p>
     * 
     * @return the AJAX handler.
     */
    public AjaxHandler getAjaxHandler();

    
    /**
     * <p>
     *   Sets the AJAX handler (from Spring Javascript) used
     *   to determine whether a request is an AJAX request or not.
     * </p>
     * <p>
     *   Views implementing this interface should be used with an instance of
     *   {@link AjaxThymeleafViewResolver} or any of its subclasses,
     *   so that this method can be called by
     *   the resolver when resolving the view, setting the default
     *   AJAX handler being used.
     * </p>
     * 
     * @param ajaxHandler the AJAX handler.
     */
    public void setAjaxHandler(final AjaxHandler ajaxHandler);
    

}
