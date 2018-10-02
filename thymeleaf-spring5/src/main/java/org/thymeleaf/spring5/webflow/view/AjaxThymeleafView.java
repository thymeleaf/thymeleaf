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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.js.ajax.AjaxHandler;
import org.springframework.util.StringUtils;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.spring5.view.ThymeleafView;


/**
 * <p>
 *   Subclass of {@link ThymeleafView} adding compatibility with AJAX events in
 *   Spring JavaScript (part of Spring WebFlow). This allows this View implementation
 *   to be able to return only <i>fragments</i> of the page.
 * </p>
 * <p>
 *   These rendering of fragments is used, for example, in Spring WebFlow's &lt;render&gt;
 *   instructions (though not only).
 * </p>
 * <p>
 *   This view searches for a comma-separated list of <i>markup selectors</i> in a request
 *   parameter called {@code fragments}.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
public class AjaxThymeleafView extends ThymeleafView implements AjaxEnabledView {

    private static final Logger vlogger = LoggerFactory.getLogger(AjaxThymeleafView.class);

    private static final String FRAGMENTS_PARAM = "fragments";


    private AjaxHandler ajaxHandler = null;



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
    public void render(final Map<String, ?> model, final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {


        final AjaxHandler templateAjaxHandler = getAjaxHandler();

        if (templateAjaxHandler == null) {
            throw new ConfigurationException("[THYMELEAF] AJAX Handler set into " +
                    AjaxThymeleafView.class.getSimpleName() + " instance for template " +
                    getTemplateName() + " is null.");
        }

        if (templateAjaxHandler.isAjaxRequest(request, response)) {

            final Set<String> fragmentsToRender = getRenderFragments(model, request, response);
            if (fragmentsToRender == null || fragmentsToRender.size() == 0) {
                vlogger.warn("[THYMELEAF] An Ajax request was detected, but no fragments were specified to be re-rendered.  "
                        + "Falling back to full page render.  This can cause unpredictable results when processing "
                        + "the ajax response on the client.");
                super.render(model, request, response);
                return;
            }

            super.renderFragment(fragmentsToRender, model, request, response);

        } else {

            super.render(model, request, response);

        }

    }




    @SuppressWarnings({ "rawtypes", "unused" })
    protected Set<String> getRenderFragments(
            final Map model, final HttpServletRequest request, final HttpServletResponse response) {
        final String fragmentsParam = request.getParameter(FRAGMENTS_PARAM);
        final String[] renderFragments = StringUtils.commaDelimitedListToStringArray(fragmentsParam);
        if (renderFragments.length == 0) {
            return null;
        }
        if (renderFragments.length == 1) {
            return Collections.singleton(renderFragments[0]);
        }
        return new HashSet<String>(Arrays.asList(renderFragments));
    }



}
