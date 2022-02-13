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
package org.thymeleaf.templatemode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public enum TemplateMode {


    HTML(true, false, false), XML(false, true, false),
    TEXT(false, false, true), JAVASCRIPT(false, false, true), CSS(false, false, true),
    RAW(false, false, false),


    /**
     * Provided only for legacy compatibility reasons for old XML-based configurations (e.g. Spring).
     * <strong>Never</strong> use this value directly. Only to be used internally at
     * {@link AbstractConfigurableTemplateResolver} implementations.
     *
     * @deprecated Deprecated in 3.0.0. Use {@link #HTML} instead. Will be REMOVED in 3.1
     */
    @Deprecated
    HTML5(true, false, false),

    /**
     * Provided only for legacy compatibility reasons for old XML-based configurations (e.g. Spring).
     * <strong>Never</strong> use this value directly. Only to be used internally at
     * {@link AbstractConfigurableTemplateResolver} implementations.
     *
     * @deprecated Deprecated in 3.0.0. Use {@link #HTML} instead. Will be REMOVED in 3.1
     */
    @Deprecated
    LEGACYHTML5(true, false, false),

    /**
     * Provided only for legacy compatibility reasons for old XML-based configurations (e.g. Spring).
     * <strong>Never</strong> use this value directly. Only to be used internally at
     * {@link AbstractConfigurableTemplateResolver} implementations.
     *
     * @deprecated Deprecated in 3.0.0. Use {@link #HTML} instead. Will be REMOVED in 3.1
     */
    @Deprecated
    XHTML(true, false, false),

    /**
     * Provided only for legacy compatibility reasons for old XML-based configurations (e.g. Spring).
     * <strong>Never</strong> use this value directly. Only to be used internally at
     * {@link AbstractConfigurableTemplateResolver} implementations.
     *
     * @deprecated Deprecated in 3.0.0. Use {@link #HTML} instead. Will be REMOVED in 3.1
     */
    @Deprecated
    VALIDXHTML(true, false, false),

    /**
     * Provided only for legacy compatibility reasons for old XML-based configurations (e.g. Spring).
     * <strong>Never</strong> use this value directly. Only to be used internally at
     * {@link AbstractConfigurableTemplateResolver} implementations.
     *
     * @deprecated Deprecated in 3.0.0. Use {@link #XML} instead. Will be REMOVED in 3.1
     */
    @Deprecated
    VALIDXML(false, true, false);





    private static Logger logger = LoggerFactory.getLogger(TemplateMode.class);


    private final boolean html;
    private final boolean xml;
    private final boolean text;
    private final boolean caseSensitive;

    TemplateMode(final boolean html, final boolean xml, final boolean text) {
        this.html = html;
        this.xml = xml;
        this.text = text;
        this.caseSensitive = !this.html;
    }

    public boolean isMarkup() {
        return this.html || this.xml;
    }

    public boolean isText() {
        return this.text;
    }

    public boolean isCaseSensitive() {
        return this.caseSensitive;
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
        if ("JAVASCRIPT".equalsIgnoreCase(mode)) {
            return JAVASCRIPT;
        }
        if ("CSS".equalsIgnoreCase(mode)) {
            return CSS;
        }
        if ("RAW".equalsIgnoreCase(mode)) {
            return RAW;
        }
        // Legacy template modes are automatically converted here
        // This code should probably be removed at some point in the distant future after Thymeleaf v3
        if ("HTML5".equalsIgnoreCase(mode) || "XHTML".equalsIgnoreCase(mode) ||
                "VALIDXHTML".equalsIgnoreCase(mode) || "LEGACYHTML5".equalsIgnoreCase(mode)) {
            logger.warn(
                    "[THYMELEAF][{}] Template Mode '{}' is deprecated. Using Template Mode '{}' instead.",
                    new Object[]{TemplateEngine.threadIndex(), mode, HTML});
            return HTML;
        }
        if ("VALIDXML".equalsIgnoreCase(mode)) {
            logger.warn(
                    "[THYMELEAF][{}] Template Mode '{}' is deprecated. Using Template Mode '{}' instead.",
                    new Object[]{TemplateEngine.threadIndex(), mode, XML});
            return XML;
        }
        logger.warn(
                "[THYMELEAF][{}] Unknown Template Mode '{}'. Must be one of: 'HTML', 'XML', 'TEXT', 'JAVASCRIPT', 'CSS', 'RAW'. " +
                "Using default Template Mode '{}'.",
                new Object[]{TemplateEngine.threadIndex(), mode, HTML});
        return HTML;
    }

}
