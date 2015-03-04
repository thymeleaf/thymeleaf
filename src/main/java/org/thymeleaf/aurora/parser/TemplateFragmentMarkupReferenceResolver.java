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
package org.thymeleaf.aurora.parser;

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

    private static TemplateFragmentMarkupReferenceResolver HTML_INSTANCE_NO_PREFIX;
    private static ConcurrentHashMap<String,TemplateFragmentMarkupReferenceResolver> HTML_INSTANCES_BY_PREFIX;
    private static TemplateFragmentMarkupReferenceResolver XML_INSTANCE_NO_PREFIX;
    private static ConcurrentHashMap<String,TemplateFragmentMarkupReferenceResolver> XML_INSTANCES_BY_PREFIX;

    private static String HTML_FORMAT_WITHOUT_PREFIX = "/[fragment='%s' or data-fragment='%s']";
    private static String HTML_FORMAT_WITH_PREFIX = "/[%s:fragment='%%s' or data-%s-fragment='%%s']";

    private static String XML_FORMAT_WITHOUT_PREFIX = "/[fragment='%s']";
    private static String XML_FORMAT_WITH_PREFIX = "/[%s:fragment='%%s']";


    private final ConcurrentHashMap<String,String> selectorsByReference = new ConcurrentHashMap<String, String>(20);
    private final String resolverFormat;



    static{

        HTML_INSTANCE_NO_PREFIX = new TemplateFragmentMarkupReferenceResolver(true, null);
        XML_INSTANCE_NO_PREFIX = new TemplateFragmentMarkupReferenceResolver(false, null);
        HTML_INSTANCES_BY_PREFIX = new ConcurrentHashMap<String, TemplateFragmentMarkupReferenceResolver>(2,1.0f);
        XML_INSTANCES_BY_PREFIX = new ConcurrentHashMap<String, TemplateFragmentMarkupReferenceResolver>(2,1.0f);

    }


    static TemplateFragmentMarkupReferenceResolver forPrefix(final boolean html, final String prefix) {
        if (prefix == null) {
            return (html? HTML_INSTANCE_NO_PREFIX : XML_INSTANCE_NO_PREFIX);
        }
        final TemplateFragmentMarkupReferenceResolver resolver =
                (html? HTML_INSTANCES_BY_PREFIX.get(prefix) : XML_INSTANCES_BY_PREFIX.get(prefix));
        if (resolver != null) {
            return resolver;
        }
        final TemplateFragmentMarkupReferenceResolver newResolver = new TemplateFragmentMarkupReferenceResolver(html, prefix);
        if (html) {
            HTML_INSTANCES_BY_PREFIX.putIfAbsent(prefix, newResolver);
            return HTML_INSTANCES_BY_PREFIX.get(prefix);
        } else {
            XML_INSTANCES_BY_PREFIX.putIfAbsent(prefix, newResolver);
            return XML_INSTANCES_BY_PREFIX.get(prefix);
        }
    }



    private TemplateFragmentMarkupReferenceResolver(final boolean html, final String prefix) {
        super();
        if (prefix == null) {
            this.resolverFormat = (html? HTML_FORMAT_WITHOUT_PREFIX : XML_FORMAT_WITHOUT_PREFIX);
        } else {
            this.resolverFormat = (html? String.format(HTML_FORMAT_WITH_PREFIX, prefix, prefix) : String.format(XML_FORMAT_WITH_PREFIX, prefix, prefix));
        }
    }


    public String resolveSelectorFromReference(final String reference) {
        Validate.notNull(reference, "Reference cannot be null");
        final String selector = this.selectorsByReference.get(reference);
        if (selector != null) {
            return selector;
        }
        final String newSelector = String.format(this.resolverFormat, reference, reference);
        this.selectorsByReference.put(reference, newSelector);
        return newSelector;
    }

}
