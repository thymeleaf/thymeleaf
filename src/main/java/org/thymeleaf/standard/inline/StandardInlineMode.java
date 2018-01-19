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
package org.thymeleaf.standard.inline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;

/**
 * Enum specifying all the available inline modes (note the really available ones depend on the host template mode).
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public enum StandardInlineMode {

    // Note there is no "RAW" inlining. There is no need, because inlining applies on text nodes
    // appearing as body of elements with a different template mode, and therefore we wouldn't be able to avoid
    // the execution of attributes in tags of an HTML template, nested inside a tag with th:inline="raw". So
    // in the end, RAW inlining would be exactly the same as NONE inlining (but more confusing).

    NONE, HTML, XML, TEXT, JAVASCRIPT, CSS;


    private static final Logger LOGGER = LoggerFactory.getLogger(StandardInlineMode.class);


    public static StandardInlineMode parse(final String mode) {
        if (mode == null || mode.trim().length() == 0) {
            throw new IllegalArgumentException("Inline mode cannot be null or empty");
        }
        if ("NONE".equalsIgnoreCase(mode)) {
            return NONE;
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
        if (checkDartInline(mode)) {
            return JAVASCRIPT;
        }
        throw new IllegalArgumentException("Unrecognized inline mode: " + mode);
    }




    /**
     * Check for the pre-3.0 "dart" inlining mode, which was removed when inlining modes were matched to template modes,
     * due to the inexistence of a (unneeded) "DART" template mode.
     *
     * NOTE this method is expressed here separately only in order to mark it as DEPRECATED and make it easily locatable
     *      as such.
     *
     * @param inliner the name of the inliner we are asking for
     * @return StandardJavaScriptInliner.INSTANCE if the inliner being asked for is "dart", null otherwise
     * @deprecated in 3.0.0, "javascript" inlining should be used instead (since 3.0.0 all inlining modes refer to
     *             template modes, and there is no "DART" template mode). Support for this default operation will be
     *             removed in 3.1
     */
    @Deprecated
    private static boolean checkDartInline(final String inliner) {
        if ("DART".equalsIgnoreCase(inliner)) {
            LOGGER.warn(
                    "[THYMELEAF][{}] Found inline call with value \"dart\", which has been deprecated as no " +
                    "corresponding template mode exists for it. Inline will be redirected to \"javascript\", which " +
                    "should now be used instead. This redirection will be removed in future versions of Thymeleaf.",
                    TemplateEngine.threadIndex());
            return true;
        }
        return false;
    }

}
