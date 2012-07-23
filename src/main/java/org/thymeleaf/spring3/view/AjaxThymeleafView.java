/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.spring3.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.js.ajax.AjaxHandler;
import org.springframework.js.ajax.SpringJavascriptAjaxHandler;
import org.springframework.util.StringUtils;
import org.thymeleaf.fragment.ElementAndAttributeNameFragmentSpec;


/**
*
* @author Daniel Fern&aacute;ndez
* 
* @since 2.0.9
*
*/
public class AjaxThymeleafView extends ThymeleafView {

    
    private static final Logger vlogger = LoggerFactory.getLogger(AjaxThymeleafView.class);

    
    private static final String FRAGMENTS_PARAM = "fragments";
    

    private AjaxHandler ajaxHandler = new SpringJavascriptAjaxHandler();



    
    public AjaxThymeleafView() {
        super();
    }
    
    
    

    public AjaxHandler getAjaxHandler() {
        return this.ajaxHandler;
    }

    public void setAjaxHandler(final AjaxHandler ajaxHandler) {
        this.ajaxHandler = ajaxHandler;
    }




    @Override
    public void render(
            final Map<String, ?> model, final HttpServletRequest request, final HttpServletResponse response) 
            throws Exception {

        if (this.ajaxHandler.isAjaxRequest(request, response)) {
            
            final String[] fragmentsToRender = getRenderFragments(model, request, response);
            if (fragmentsToRender.length == 0) {
                vlogger.warn("[THYMELEAF] An Ajax request was detected, but no fragments were specified to be re-rendered.  "
                        + "Falling back to full page render.  This can cause unpredictable results when processing "
                        + "the ajax response on the client.");
                super.render(model, request, response);
                return;
            }

            // TODO implement this using real fragment names
            super.renderFragment(
                    new ElementAndAttributeNameFragmentSpec(null, "th:fragment", fragmentsToRender[0]), 
                    model, request, response);
            
        } else {
            
            super.render(model, request, response);
            
        }
        
    }
    
    
    
    

    @SuppressWarnings({ "rawtypes", "unused" })
    protected String[] getRenderFragments(
            final Map model, final HttpServletRequest request, final HttpServletResponse response) {
        final String fragmentsParam = request.getParameter(FRAGMENTS_PARAM);
        final String[] renderFragments = StringUtils.commaDelimitedListToStringArray(fragmentsParam);
        return StringUtils.trimArrayElements(renderFragments);
    }
    
    

}
