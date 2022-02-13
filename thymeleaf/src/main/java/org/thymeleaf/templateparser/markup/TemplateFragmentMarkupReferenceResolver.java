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
package org.thymeleaf.templateparser.markup;

import java.util.concurrent.ConcurrentHashMap;

import org.attoparser.select.IMarkupSelectorReferenceResolver;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
final class TemplateFragmentMarkupReferenceResolver implements IMarkupSelectorReferenceResolver {

    private static final TemplateFragmentMarkupReferenceResolver HTML_INSTANCE_NO_PREFIX;
    private static final ConcurrentHashMap<String,TemplateFragmentMarkupReferenceResolver> HTML_INSTANCES_BY_PREFIX;
    private static final TemplateFragmentMarkupReferenceResolver XML_INSTANCE_NO_PREFIX;
    private static final ConcurrentHashMap<String,TemplateFragmentMarkupReferenceResolver> XML_INSTANCES_BY_PREFIX;

    private static final String HTML_FORMAT_WITHOUT_PREFIX =
            "/[ref='%1$s' or data-ref='%1$s' or fragment='%1$s' or data-fragment='%1$s' or fragment^='%1$s(' or data-fragment^='%1$s(' or fragment^='%1$s (' or data-fragment^='%1$s (']";
    private static final String HTML_FORMAT_WITH_PREFIX =
            "/[%1$s:ref='%%1$s' or data-%1$s-ref='%%1$s' or %1$s:fragment='%%1$s' or data-%1$s-fragment='%%1$s' or %1$s:fragment^='%%1$s(' or data-%1$s-fragment^='%%1$s(' or %1$s:fragment^='%%1$s (' or data-%1$s-fragment^='%%1$s (']";

    private static final String XML_FORMAT_WITHOUT_PREFIX =
            "/[ref='%1$s' or fragment='%1$s' or fragment^='%1$s(' or fragment^='%1$s (']";
    private static final String XML_FORMAT_WITH_PREFIX =
            "/[%1$s:ref='%%1$s' or %1$s:fragment='%%1$s' or %1$s:fragment^='%%1$s(' or %1$s:fragment^='%%1$s (']";


    private final ConcurrentHashMap<String,String> selectorsByReference = new ConcurrentHashMap<String, String>(20);
    private final String resolverFormat;



    static{

        HTML_INSTANCE_NO_PREFIX = new TemplateFragmentMarkupReferenceResolver(true, null);
        XML_INSTANCE_NO_PREFIX = new TemplateFragmentMarkupReferenceResolver(false, null);
        HTML_INSTANCES_BY_PREFIX = new ConcurrentHashMap<String, TemplateFragmentMarkupReferenceResolver>(3, 0.9f, 2);
        XML_INSTANCES_BY_PREFIX = new ConcurrentHashMap<String, TemplateFragmentMarkupReferenceResolver>(3, 0.9f, 2);

    }


    static TemplateFragmentMarkupReferenceResolver forPrefix(final boolean html, final String standardDialectPrefix) {
        return html? forHTMLPrefix(standardDialectPrefix) : forXMLPrefix(standardDialectPrefix);
    }


    private static TemplateFragmentMarkupReferenceResolver forHTMLPrefix(final String standardDialectPrefix) {
        if (standardDialectPrefix == null || standardDialectPrefix.length() == 0) {
            return HTML_INSTANCE_NO_PREFIX;
        }
        final String prefix = standardDialectPrefix.toLowerCase();
        final TemplateFragmentMarkupReferenceResolver resolver = HTML_INSTANCES_BY_PREFIX.get(prefix);
        if (resolver != null) {
            return resolver;
        }
        final TemplateFragmentMarkupReferenceResolver newResolver =
                new TemplateFragmentMarkupReferenceResolver(true, prefix);
        HTML_INSTANCES_BY_PREFIX.putIfAbsent(prefix, newResolver);
        return HTML_INSTANCES_BY_PREFIX.get(prefix);
    }


    private static TemplateFragmentMarkupReferenceResolver forXMLPrefix(final String standardDialectPrefix) {
        if (standardDialectPrefix == null || standardDialectPrefix.length() == 0) {
            return XML_INSTANCE_NO_PREFIX;
        }
        final TemplateFragmentMarkupReferenceResolver resolver = XML_INSTANCES_BY_PREFIX.get(standardDialectPrefix);
        if (resolver != null) {
            return resolver;
        }
        final TemplateFragmentMarkupReferenceResolver newResolver =
                new TemplateFragmentMarkupReferenceResolver(false, standardDialectPrefix);
        XML_INSTANCES_BY_PREFIX.putIfAbsent(standardDialectPrefix, newResolver);
        return XML_INSTANCES_BY_PREFIX.get(standardDialectPrefix);
    }



    private TemplateFragmentMarkupReferenceResolver(final boolean html, final String standardDialectPrefix) {
        super();
        if (standardDialectPrefix == null) {
            this.resolverFormat = (html? HTML_FORMAT_WITHOUT_PREFIX : XML_FORMAT_WITHOUT_PREFIX);
        } else {
            this.resolverFormat =
                    (html? String.format(HTML_FORMAT_WITH_PREFIX, standardDialectPrefix) :
                           String.format(XML_FORMAT_WITH_PREFIX, standardDialectPrefix));
        }
    }


    public String resolveSelectorFromReference(final String reference) {
        Validate.notNull(reference, "Reference cannot be null");
        final String selector = this.selectorsByReference.get(reference);
        if (selector != null) {
            return selector;
        }
        final String newSelector = String.format(this.resolverFormat, reference);
        this.selectorsByReference.put(reference, newSelector);
        return newSelector;
    }

}
