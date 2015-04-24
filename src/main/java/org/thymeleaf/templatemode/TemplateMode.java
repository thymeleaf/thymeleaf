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
package org.thymeleaf.templatemode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public enum TemplateMode {


    HTML(true, false, false), XML(false, true, false), TEXT(false, false, true);



    private static Logger logger = LoggerFactory.getLogger(TemplateMode.class);


    private final boolean html;
    private final boolean xml;
    private final boolean text;

    TemplateMode(final boolean html, final boolean xml, final boolean text) {
        this.html = html;
        this.xml = xml;
        this.text = text;
    }

    public boolean isHTML() {
        return this.html;
    }

    public boolean isXML() {
        return this.xml;
    }

    public boolean isText() {
        return this.text;
    }


    public static TemplateMode parse(final String mode) {
        if (mode == null || mode.trim().length() == 0) {
            throw new IllegalArgumentException("Template mode cannot be null or empty");
        }
        if ("HTML".equalsIgnoreCase(mode)) {
            return HTML;
        }
        if ("XML".equalsIgnoreCase(mode)) {
            return XML;
        }
        if ("TEXT".equalsIgnoreCase(mode)) {
            return TEXT;
        }
        // Legacy template modes are automatically converted here
        // This code should probably be removed at some point in the distant future after Thymeleaf v3
        if ("HTML5".equalsIgnoreCase(mode) || "XHTML".equalsIgnoreCase(mode) ||
                "VALIDXHTML".equalsIgnoreCase(mode) || "LEGACYHTML5".equalsIgnoreCase(mode)) {
            logger.warn(String.format(
                    "[THYMELEAF][{}] Template Mode '{}' is deprecated. Using Template Mode '{}' instead.",
                    new Object[]{TemplateEngine.threadIndex(), mode, HTML}));
            return HTML;
        }
        if ("VALIDXML".equalsIgnoreCase(mode)) {
            logger.warn(String.format(
                    "[THYMELEAF][{}] Template Mode '{}' is deprecated. Using Template Mode '{}' instead.",
                    new Object[]{TemplateEngine.threadIndex(), mode, XML}));
            return XML;
        }
        logger.warn(String.format(
                "[THYMELEAF][{}] Unknown Template Mode '{}'. Using default Template Mode '{}'.",
                new Object[]{TemplateEngine.threadIndex(), mode, HTML}));
        return HTML;
    }

}
