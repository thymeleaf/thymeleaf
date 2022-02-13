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
package org.thymeleaf.spring5.util;

import java.beans.PropertyEditor;

import org.springframework.util.ObjectUtils;
import org.springframework.web.util.HtmlUtils;

/**
 * <p>
 *   This class mirrors the behaviour of the protected class
 *   {@code org.springframework.web.servlet.tags.form.ValueFormatter}, needed in order to format
 *   rendered values in a way compatible to other Spring view-layer technologies.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.3
 *
 */
public final class SpringValueFormatter {


    /*
     * NOTE This code is based on org.springframework.web.servlet.tags.form.ValueFormatter as of Spring 5.0.0
     *      Original license is Apache License 2.0, which is the same as the license for this file.
     *      Original copyright notice is "Copyright 2002-2012 the original author or authors".
     *      Original authors are Rob Harrop and Juergen Hoeller.
     */


    public static String getDisplayString(final Object value, final boolean htmlEscape) {
        final String displayValue = ObjectUtils.getDisplayString(value);
        return (htmlEscape ? HtmlUtils.htmlEscape(displayValue) : displayValue);
    }


    public static String getDisplayString(final Object value, final PropertyEditor propertyEditor, final boolean htmlEscape) {
        if (propertyEditor != null && !(value instanceof String)) {
            try {
                propertyEditor.setValue(value);
                final String text = propertyEditor.getAsText();
                if (text != null) {
                    return getDisplayString(text, htmlEscape);
                }
            } catch (final Throwable ex) {
                // The PropertyEditor might not support this value... pass through.
            }
        }
        return getDisplayString(value, htmlEscape);
    }


    private SpringValueFormatter() {
        super();
    }


}
