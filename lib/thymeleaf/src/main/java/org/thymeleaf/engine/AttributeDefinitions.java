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
package org.thymeleaf.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.processor.element.IElementProcessor;
import org.thymeleaf.processor.element.MatchingAttributeName;
import org.thymeleaf.processor.element.MatchingElementName;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.TextUtils;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class AttributeDefinitions {


    // Set containing all the standard element names, for possible external reference
    public static final Set<String> ALL_STANDARD_HTML_ATTRIBUTE_NAMES;

    // Set containing all the names of the standard HTML attributes that are considered "boolean"
    private static final Set<String> ALL_STANDARD_BOOLEAN_HTML_ATTRIBUTE_NAMES;


    // We need a different repository for each template mode
    private final AttributeDefinitionRepository htmlAttributeRepository;
    private final AttributeDefinitionRepository xmlAttributeRepository;
    private final AttributeDefinitionRepository textAttributeRepository;
    private final AttributeDefinitionRepository javascriptAttributeRepository;
    private final AttributeDefinitionRepository cssAttributeRepository;



    static {

        final List<String> htmlAttributeNameListAux =
                new ArrayList<String>(Arrays.asList(new String[]{
                        "abbr", "accept", "accept-charset", "accesskey", "action", "align", "alt", "archive", "async",
                        "autocomplete", "autofocus", "autoplay", "axis", "border", "cellpadding", "cellspacing",
                        "challenge", "char", "charoff", "charset", "checked", "cite", "class", "classid",
                        "codebase", "codetype", "cols", "colspan", "command", "content", "contenteditable",
                        "contextmenu", "controls", "coords", "data", "datetime", "declare", "default",
                        "defer", "dir", "disabled", "draggable", "dropzone", "enctype", "for", "form",
                        "formaction", "formenctype", "formmethod", "formnovalidate", "formtarget",
                        "frame", "headers", "height", "hidden", "high", "href", "hreflang", "http-equiv",
                        "icon", "id", "ismap", "keytype", "kind", "label", "lang", "list", "longdesc",
                        "loop", "low", "max", "maxlength", "media", "method", "min", "multiple", "muted",
                        "name", "nohref", "novalidate", "nowrap", "onabort", "onafterprint", "onbeforeprint",
                        "onbeforeunload", "onblur", "oncanplay", "oncanplaythrough", "onchange",
                        "onclick", "oncontextmenu", "oncuechange", "ondblclick", "ondrag", "ondragend",
                        "ondragenter", "ondragleave", "ondragover", "ondragstart", "ondrop",
                        "ondurationchange", "onemptied", "onended", "onerror", "onfocus",
                        "onformchange", "onforminput", "onhaschange", "oninput", "oninvalid", "onkeydown",
                        "onkeypress", "onkeyup", "onload", "onloadeddata", "onloadedmetadata",
                        "onloadstart", "onmessage", "onmousedown", "onmousemove", "onmouseout", "onmouseover",
                        "onmouseup", "onmousewheel", "onoffline", "ononline", "onpagehide", "onpageshow",
                        "onpause", "onplay", "onplaying", "onpopstate", "onprogress", "onratechange",
                        "onredo", "onreset", "onresize", "onscroll", "onseeked", "onseeking",
                        "onselect", "onstalled", "onstorage", "onsubmit", "onsuspend", "ontimeupdate",
                        "onundo", "onunload", "onvolumechange", "onwaiting", "open", "optimum", "pattern",
                        "placeholder", "poster", "preload", "profile", "pubdate", "radiogroup", "readonly", "rel",
                        "required", "rev", "reversed", "rows", "rowspan", "rules", "scheme", "scope", "scoped",
                        "seamless", "selected", "shape", "size", "span", "spellcheck", "src", "srclang", "standby",
                        "style", "summary", "tabindex", "title", "translate", "type", "usemap", "valign", "value",
                        "valuetype", "width", "xml:lang", "xml:space", "xmlns"
                }));

        Collections.sort(htmlAttributeNameListAux);


        ALL_STANDARD_HTML_ATTRIBUTE_NAMES =
                Collections.unmodifiableSet(new LinkedHashSet<String>(htmlAttributeNameListAux));


        final Set<String> htmlBooleanAttributeNameSetAux =
                new HashSet<String>(Arrays.asList(new String[]{
                        "async", "autofocus", "autoplay", "checked", "controls",
                        "declare", "default", "defer", "disabled", "formnovalidate",
                        "hidden", "ismap", "loop", "multiple", "novalidate",
                        "nowrap", "open", "pubdate", "readonly", "required",
                        "reversed", "selected", "scoped", "seamless"
                }));

        ALL_STANDARD_BOOLEAN_HTML_ATTRIBUTE_NAMES =
                Collections.unmodifiableSet(new LinkedHashSet<String>(htmlBooleanAttributeNameSetAux));

    }






    /**
     * <strong>ONLY FOR INTERNAL USE</strong>. This constructor is meant to be called only from inside the engine.
     * It should never be called directly from any other classes.
     *
     * @param elementProcessorsByTemplateMode the processors (element and node), already ordered by precedence, which
     *                                        might be of application to the attributes which definition is to be stored
     *                                        here.
     */
    public AttributeDefinitions(final Map<TemplateMode, Set<IElementProcessor>> elementProcessorsByTemplateMode) {

        super();


        /*
         * Build the list of all Standard HTML attribute definitions
         */

        final List<HTMLAttributeDefinition> standardHTMLAttributeDefinitions =
                new ArrayList<HTMLAttributeDefinition>(ALL_STANDARD_HTML_ATTRIBUTE_NAMES.size() + 1);
        for (final String attributeNameStr : ALL_STANDARD_HTML_ATTRIBUTE_NAMES) {
            standardHTMLAttributeDefinitions.add(
                    buildHTMLAttributeDefinition(
                            AttributeNames.forHTMLName(attributeNameStr),
                            elementProcessorsByTemplateMode.get(TemplateMode.HTML)));
        }


        /*
         * Initialize the repositories
         */
        this.htmlAttributeRepository = new AttributeDefinitionRepository(TemplateMode.HTML, elementProcessorsByTemplateMode);
        this.xmlAttributeRepository = new AttributeDefinitionRepository(TemplateMode.XML, elementProcessorsByTemplateMode);
        this.textAttributeRepository = new AttributeDefinitionRepository(TemplateMode.TEXT, elementProcessorsByTemplateMode);
        this.javascriptAttributeRepository = new AttributeDefinitionRepository(TemplateMode.JAVASCRIPT, elementProcessorsByTemplateMode);
        this.cssAttributeRepository = new AttributeDefinitionRepository(TemplateMode.CSS, elementProcessorsByTemplateMode);


        /*
         * Register the standard elements at the element repository, in order to initialize it
         */
        for (final AttributeDefinition attributeDefinition : standardHTMLAttributeDefinitions) {
            this.htmlAttributeRepository.storeStandardAttribute(attributeDefinition);
        }

    }










    private static HTMLAttributeDefinition buildHTMLAttributeDefinition(
            final HTMLAttributeName name, final Set<IElementProcessor> elementProcessors) {

        // No need to use a list for sorting - the elementProcessors set has already been ordered
        final Set<IElementProcessor> associatedProcessors = new LinkedHashSet<IElementProcessor>(2);

        if (elementProcessors != null) {
            for (final IElementProcessor processor : elementProcessors) {

                // Cannot be null -- has been previously validated
                final TemplateMode templateMode = processor.getTemplateMode();

                if (templateMode != TemplateMode.HTML) {
                    // We are creating an HTML element definition, therefore we are only interested on HTML processors
                    continue;
                }

                final MatchingElementName matchingElementName = processor.getMatchingElementName();
                final MatchingAttributeName matchingAttributeName = processor.getMatchingAttributeName();

                if ((matchingElementName != null && matchingElementName.getTemplateMode() != TemplateMode.HTML) ||
                        (matchingAttributeName != null && matchingAttributeName.getTemplateMode() != TemplateMode.HTML)) {
                    throw new ConfigurationException("HTML processors must return HTML element names and HTML attribute names (processor: " + processor.getClass().getName() + ")");
                }

                if (matchingAttributeName == null || matchingAttributeName.isMatchingAllAttributes()) {
                    // This processor does not relate to a specific attribute - surely an element processor
                    continue;
                }

                if (!matchingAttributeName.matches(name)) {
                    // Doesn't match. This processor is not associated with this attribute
                    continue;
                }

                associatedProcessors.add(processor);

            }
        }

        // Compute whether this attribute is to be considered boolean or not
        boolean booleanAttribute = false;
        for (final String completeAttributeName : name.getCompleteAttributeNames()) {
            if (ALL_STANDARD_BOOLEAN_HTML_ATTRIBUTE_NAMES.contains(completeAttributeName)) {
                booleanAttribute = true;
            }
        }

        // Build the final instance
        return new HTMLAttributeDefinition(name, booleanAttribute, associatedProcessors);

    }




    private static XMLAttributeDefinition buildXMLAttributeDefinition(
            final XMLAttributeName name, final Set<IElementProcessor> elementProcessors) {

        // No need to use a list for sorting - the elementProcessors set has already been ordered
        final Set<IElementProcessor> associatedProcessors = new LinkedHashSet<IElementProcessor>(2);

        if (elementProcessors != null) {
            for (final IElementProcessor processor : elementProcessors) {

                // Cannot be null -- has been previously validated
                final TemplateMode templateMode = processor.getTemplateMode();

                if (templateMode != TemplateMode.XML) {
                    // We are creating an XML element definition, therefore we are only interested on XML processors
                    continue;
                }

                final MatchingElementName matchingElementName = processor.getMatchingElementName();
                final MatchingAttributeName matchingAttributeName = processor.getMatchingAttributeName();

                if ((matchingElementName != null && matchingElementName.getTemplateMode() != TemplateMode.XML) ||
                        (matchingAttributeName != null && matchingAttributeName.getTemplateMode() != TemplateMode.XML)) {
                    throw new ConfigurationException("XML processors must return XML element names and XML attribute names (processor: " + processor.getClass().getName() + ")");
                }

                if (matchingAttributeName == null || matchingAttributeName.isMatchingAllAttributes()) {
                    // This processor does not relate to a specific attribute - surely an element processor
                    continue;
                }

                if (!matchingAttributeName.matches(name)) {
                    // Doesn't match. This processor is not associated with this attribute
                    continue;
                }

                associatedProcessors.add(processor);

            }
        }

        // Build the final instance
        return new XMLAttributeDefinition(name, associatedProcessors);

    }




    private static TextAttributeDefinition buildTextAttributeDefinition(
            final TemplateMode templateMode, final TextAttributeName name, final Set<IElementProcessor> elementProcessors) {

        // No need to use a list for sorting - the elementProcessors set has already been ordered
        final Set<IElementProcessor> associatedProcessors = new LinkedHashSet<IElementProcessor>(2);

        if (elementProcessors != null) {
            for (final IElementProcessor processor : elementProcessors) {

                if (processor.getTemplateMode() != templateMode) {
                    // We are creating a text element definition, therefore we are only interested on XML processors
                    continue;
                }

                final MatchingElementName matchingElementName = processor.getMatchingElementName();
                final MatchingAttributeName matchingAttributeName = processor.getMatchingAttributeName();

                if ((matchingElementName != null && matchingElementName.getTemplateMode() != templateMode) ||
                        (matchingAttributeName != null && matchingAttributeName.getTemplateMode() != templateMode)) {
                    throw new ConfigurationException(templateMode + " processors must return " + templateMode + "element names and " + templateMode + " attribute names (processor: " + processor.getClass().getName() + ")");
                }

                if (matchingAttributeName == null || matchingAttributeName.isMatchingAllAttributes()) {
                    // This processor does not relate to a specific attribute - surely an element processor
                    continue;
                }

                if (!matchingAttributeName.matches(name)) {
                    // Doesn't match. This processor is not associated with this attribute
                    continue;
                }

                associatedProcessors.add(processor);

            }
        }

        // Build the final instance
        return new TextAttributeDefinition(name, associatedProcessors);

    }




    public AttributeDefinition forName(final TemplateMode templateMode, final String attributeName) {
        if (templateMode == null) {
            throw new IllegalArgumentException("Template Mode cannot be null");
        }
        switch (templateMode) {
            case HTML:
                return forHTMLName(attributeName);
            case XML:
                return forXMLName(attributeName);
            case TEXT:
                return forTextName(attributeName);
            case JAVASCRIPT:
                return forJavaScriptName(attributeName);
            case CSS:
                return forCSSName(attributeName);
            case RAW:
                throw new IllegalArgumentException("Attribute Definitions cannot be obtained for " + templateMode + " template mode ");
            default:
                throw new IllegalArgumentException("Unknown template mode " + templateMode);
        }
    }


    public AttributeDefinition forName(final TemplateMode templateMode, final String prefix, final String attributeName) {
        if (templateMode == null) {
            throw new IllegalArgumentException("Template Mode cannot be null");
        }
        switch (templateMode) {
            case HTML:
                return forHTMLName(prefix, attributeName);
            case XML:
                return forXMLName(prefix, attributeName);
            case TEXT:
                return forTextName(prefix, attributeName);
            case JAVASCRIPT:
                return forJavaScriptName(prefix, attributeName);
            case CSS:
                return forCSSName(prefix, attributeName);
            case RAW:
                throw new IllegalArgumentException("Attribute Definitions cannot be obtained for " + templateMode + " template mode ");
            default:
                throw new IllegalArgumentException("Unknown template mode " + templateMode);
        }
    }


    public AttributeDefinition forName(final TemplateMode templateMode, final char[] attributeName, final int attributeNameOffset, final int attributeNameLen) {
        if (templateMode == null) {
            throw new IllegalArgumentException("Template Mode cannot be null");
        }
        switch (templateMode) {
            case HTML:
                return forHTMLName(attributeName, attributeNameOffset, attributeNameLen);
            case XML:
                return forXMLName(attributeName, attributeNameOffset, attributeNameLen);
            case TEXT:
                return forTextName(attributeName, attributeNameOffset, attributeNameLen);
            case JAVASCRIPT:
                return forJavaScriptName(attributeName, attributeNameOffset, attributeNameLen);
            case CSS:
                return forCSSName(attributeName, attributeNameOffset, attributeNameLen);
            case RAW:
                throw new IllegalArgumentException("Attribute Definitions cannot be obtained for " + templateMode + " template mode ");
            default:
                throw new IllegalArgumentException("Unknown template mode " + templateMode);
        }
    }




    public HTMLAttributeDefinition forHTMLName(final String attributeName) {
        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (HTMLAttributeDefinition) this.htmlAttributeRepository.getAttribute(attributeName);
    }


    public HTMLAttributeDefinition forHTMLName(final String prefix, final String attributeName) {
        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (HTMLAttributeDefinition) this.htmlAttributeRepository.getAttribute(prefix, attributeName);
    }


    public HTMLAttributeDefinition forHTMLName(final char[] attributeName, final int attributeNameOffset, final int attributeNameLen) {
        if (attributeName == null || attributeNameLen == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (attributeNameOffset < 0 || attributeNameLen < 0) {
            throw new IllegalArgumentException("Both name offset and length must be equal to or greater than zero");
        }
        return (HTMLAttributeDefinition) this.htmlAttributeRepository.getAttribute(attributeName, attributeNameOffset, attributeNameLen);
    }



    public XMLAttributeDefinition forXMLName(final String attributeName) {
        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (XMLAttributeDefinition) this.xmlAttributeRepository.getAttribute(attributeName);
    }


    public XMLAttributeDefinition forXMLName(final String prefix, final String attributeName) {
        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (XMLAttributeDefinition) this.xmlAttributeRepository.getAttribute(prefix, attributeName);
    }


    public XMLAttributeDefinition forXMLName(final char[] attributeName, final int attributeNameOffset, final int attributeNameLen) {
        if (attributeName == null || attributeNameLen == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (attributeNameOffset < 0 || attributeNameLen < 0) {
            throw new IllegalArgumentException("Both name offset and length must be equal to or greater than zero");
        }
        return (XMLAttributeDefinition) this.xmlAttributeRepository.getAttribute(attributeName, attributeNameOffset, attributeNameLen);
    }



    public TextAttributeDefinition forTextName(final String attributeName) {
        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (TextAttributeDefinition) this.textAttributeRepository.getAttribute(attributeName);
    }


    public TextAttributeDefinition forTextName(final String prefix, final String attributeName) {
        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (TextAttributeDefinition) this.textAttributeRepository.getAttribute(prefix, attributeName);
    }


    public TextAttributeDefinition forTextName(final char[] attributeName, final int attributeNameOffset, final int attributeNameLen) {
        if (attributeName == null || attributeNameLen == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (attributeNameOffset < 0 || attributeNameLen < 0) {
            throw new IllegalArgumentException("Both name offset and length must be equal to or greater than zero");
        }
        return (TextAttributeDefinition) this.textAttributeRepository.getAttribute(attributeName, attributeNameOffset, attributeNameLen);
    }



    public TextAttributeDefinition forJavaScriptName(final String attributeName) {
        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (TextAttributeDefinition) this.javascriptAttributeRepository.getAttribute(attributeName);
    }


    public TextAttributeDefinition forJavaScriptName(final String prefix, final String attributeName) {
        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (TextAttributeDefinition) this.javascriptAttributeRepository.getAttribute(prefix, attributeName);
    }


    public TextAttributeDefinition forJavaScriptName(final char[] attributeName, final int attributeNameOffset, final int attributeNameLen) {
        if (attributeName == null || attributeNameLen == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (attributeNameOffset < 0 || attributeNameLen < 0) {
            throw new IllegalArgumentException("Both name offset and length must be equal to or greater than zero");
        }
        return (TextAttributeDefinition) this.javascriptAttributeRepository.getAttribute(attributeName, attributeNameOffset, attributeNameLen);
    }



    public TextAttributeDefinition forCSSName(final String attributeName) {
        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (TextAttributeDefinition) this.cssAttributeRepository.getAttribute(attributeName);
    }


    public TextAttributeDefinition forCSSName(final String prefix, final String attributeName) {
        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (TextAttributeDefinition) this.cssAttributeRepository.getAttribute(prefix, attributeName);
    }


    public TextAttributeDefinition forCSSName(final char[] attributeName, final int attributeNameOffset, final int attributeNameLen) {
        if (attributeName == null || attributeNameLen == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (attributeNameOffset < 0 || attributeNameLen < 0) {
            throw new IllegalArgumentException("Both name offset and length must be equal to or greater than zero");
        }
        return (TextAttributeDefinition) this.cssAttributeRepository.getAttribute(attributeName, attributeNameOffset, attributeNameLen);
    }





    /*
     * This repository class is thread-safe. The reason for this is that it not only contains the
     * standard attributes, but will also contain new instances of AttributeDefinition created during processing (created
     * when asking the repository for them when they do not exist yet). As any thread can create a new attribute,
     * this has to be lock-protected.
     */
    static final class AttributeDefinitionRepository {

        private final TemplateMode templateMode;

        // These have already been filtered previously - only element-oriented processors will be here
        private final Map<TemplateMode, Set<IElementProcessor>> elementProcessorsByTemplateMode;

        private final List<String> standardRepositoryNames; // read-only, no sync needed
        private final List<AttributeDefinition> standardRepository; // read-only, no sync needed

        private final List<String> repositoryNames;  // read-write, sync will be needed
        private final List<AttributeDefinition> repository;  // read-write, sync will be needed

        private final ReadWriteLock lock = new ReentrantReadWriteLock(true);
        private final Lock readLock = this.lock.readLock();
        private final Lock writeLock = this.lock.writeLock();


        AttributeDefinitionRepository(final TemplateMode templateMode, final Map<TemplateMode, Set<IElementProcessor>> elementProcessorsByTemplateMode) {

            super();

            this.templateMode = templateMode;
            this.elementProcessorsByTemplateMode = elementProcessorsByTemplateMode;

            this.standardRepositoryNames = (templateMode == TemplateMode.HTML ? new ArrayList<String>(150) : null);
            this.standardRepository = (templateMode == TemplateMode.HTML ? new ArrayList<AttributeDefinition>(150) : null);

            this.repositoryNames = new ArrayList<String>(500);
            this.repository = new ArrayList<AttributeDefinition>(500);

        }


        AttributeDefinition getAttribute(final char[] text, final int offset, final int len) {

            int index;

            if (this.standardRepository != null) {
                /*
                 * We first try to find it in the repository containing the standard elements, which does not need
                 * any synchronization.
                 */
                index = binarySearch(this.templateMode.isCaseSensitive(), this.standardRepositoryNames, text, offset, len);

                if (index >= 0) {
                    return this.standardRepository.get(index);
                }
            }

            /*
             * We did not find it in the repository of standard elements, so let's try in the read+write one,
             * which does require synchronization through a readwrite lock.
             */

            this.readLock.lock();
            try {

                /*
                 * First look for the element in the namespaced repository
                 */
                index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, text, offset, len);

                if (index >= 0) {
                    return this.repository.get(index);
                }

            } finally {
                this.readLock.unlock();
            }


            /*
             * NOT FOUND. We need to obtain a write lock and store the text
             */
            this.writeLock.lock();
            try {
                return storeAttribute(text, offset, len);
            } finally {
                this.writeLock.unlock();
            }

        }


        AttributeDefinition getAttribute(final String completeAttributeName) {

            int index;

            if (this.standardRepository != null) {
                /*
                 * We first try to find it in the repository containing the standard elements, which does not need
                 * any synchronization.
                 */
                index = binarySearch(this.templateMode.isCaseSensitive(), this.standardRepositoryNames, completeAttributeName);

                if (index >= 0) {
                    return this.standardRepository.get(index);
                }
            }

            /*
             * We did not find it in the repository of standard elements, so let's try in the read+write one,
             * which does require synchronization through a readwrite lock.
             */

            this.readLock.lock();
            try {

                /*
                 * First look for the element in the namespaced repository
                 */
                index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, completeAttributeName);

                if (index >= 0) {
                    return this.repository.get(index);
                }

            } finally {
                this.readLock.unlock();
            }


            /*
             * NOT FOUND. We need to obtain a write lock and store the text
             */
            this.writeLock.lock();
            try {
                return storeAttribute(completeAttributeName);
            } finally {
                this.writeLock.unlock();
            }

        }


        AttributeDefinition getAttribute(final String prefix, final String attributeName) {

            int index;

            if (this.standardRepository != null) {
                /*
                 * We first try to find it in the repository containing the standard elements, which does not need
                 * any synchronization.
                 */
                index = binarySearch(this.templateMode.isCaseSensitive(), this.standardRepositoryNames, prefix, attributeName);

                if (index >= 0) {
                    return this.standardRepository.get(index);
                }
            }

            /*
             * We did not find it in the repository of standard elements, so let's try in the read+write one,
             * which does require synchronization through a readwrite lock.
             */

            this.readLock.lock();
            try {

                /*
                 * First look for the element in the namespaced repository
                 */
                index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, prefix, attributeName);

                if (index >= 0) {
                    return this.repository.get(index);
                }

            } finally {
                this.readLock.unlock();
            }


            /*
             * NOT FOUND. We need to obtain a write lock and store the text
             */
            this.writeLock.lock();
            try {
                return storeAttribute(prefix, attributeName);
            } finally {
                this.writeLock.unlock();
            }

        }


        private AttributeDefinition storeAttribute(final char[] text, final int offset, final int len) {

            int index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, text, offset, len);
            if (index >= 0) {
                // It was already added while we were waiting for the lock!
                return this.repository.get(index);
            }

            final Set<IElementProcessor> elementProcessors = this.elementProcessorsByTemplateMode.get(this.templateMode);

            final AttributeDefinition attributeDefinition;
            if (this.templateMode == TemplateMode.HTML) {
                attributeDefinition =
                        buildHTMLAttributeDefinition(AttributeNames.forHTMLName(text, offset, len), elementProcessors);
            } else if (this.templateMode == TemplateMode.XML) {
                attributeDefinition =
                        buildXMLAttributeDefinition(AttributeNames.forXMLName(text, offset, len), elementProcessors);
            } else { // this.templateMode.isText()
                attributeDefinition =
                        buildTextAttributeDefinition(this.templateMode, AttributeNames.forTextName(text, offset, len), elementProcessors);
            }

            final String[] completeAttributeNames = attributeDefinition.attributeName.completeAttributeNames;

            for (final String completeAttributeName : completeAttributeNames) {

                index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, completeAttributeName);

                // binary Search returned (-(insertion point) - 1)
                this.repositoryNames.add(((index + 1) * -1), completeAttributeName);
                this.repository.add(((index + 1) * -1), attributeDefinition);

            }

            return attributeDefinition;

        }


        private AttributeDefinition storeAttribute(final String attributeName) {

            int index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, attributeName);
            if (index >= 0) {
                // It was already added while we were waiting for the lock!
                return this.repository.get(index);
            }

            final Set<IElementProcessor> elementProcessors = this.elementProcessorsByTemplateMode.get(this.templateMode);

            final AttributeDefinition attributeDefinition;
            if (this.templateMode == TemplateMode.HTML) {
                attributeDefinition =
                        buildHTMLAttributeDefinition(AttributeNames.forHTMLName(attributeName), elementProcessors);
            } else if (this.templateMode == TemplateMode.XML) {
                attributeDefinition =
                        buildXMLAttributeDefinition(AttributeNames.forXMLName(attributeName), elementProcessors);
            } else { // this.templateMode.isText()
                attributeDefinition =
                        buildTextAttributeDefinition(this.templateMode, AttributeNames.forTextName(attributeName), elementProcessors);
            }

            final String[] completeAttributeNames = attributeDefinition.attributeName.completeAttributeNames;

            for (final String completeAttributeName : completeAttributeNames) {

                index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, completeAttributeName);

                // binary Search returned (-(insertion point) - 1)
                this.repositoryNames.add(((index + 1) * -1), completeAttributeName);
                this.repository.add(((index + 1) * -1), attributeDefinition);

            }

            return attributeDefinition;

        }


        private AttributeDefinition storeAttribute(final String prefix, final String attributeName) {

            int index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, prefix, attributeName);
            if (index >= 0) {
                // It was already added while we were waiting for the lock!
                return this.repository.get(index);
            }

            final Set<IElementProcessor> elementProcessors = this.elementProcessorsByTemplateMode.get(this.templateMode);

            final AttributeDefinition attributeDefinition;
            if (this.templateMode == TemplateMode.HTML) {
                attributeDefinition =
                        buildHTMLAttributeDefinition(AttributeNames.forHTMLName(prefix, attributeName), elementProcessors);
            } else if (this.templateMode == TemplateMode.XML) {
                attributeDefinition =
                        buildXMLAttributeDefinition(AttributeNames.forXMLName(prefix, attributeName), elementProcessors);
            } else { // this.templateMode.isText()
                attributeDefinition =
                        buildTextAttributeDefinition(this.templateMode, AttributeNames.forTextName(prefix, attributeName), elementProcessors);
            }

            final String[] completeAttributeNames = attributeDefinition.attributeName.completeAttributeNames;

            for (final String completeAttributeName : completeAttributeNames) {

                index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, completeAttributeName);

                // binary Search returned (-(insertion point) - 1)
                this.repositoryNames.add(((index + 1) * -1), completeAttributeName);
                this.repository.add(((index + 1) * -1), attributeDefinition);

            }

            return attributeDefinition;

        }


        private AttributeDefinition storeStandardAttribute(final AttributeDefinition attributeDefinition) {

            // This method will only be called from within the AttributeDefinitions class itself, during initialization of
            // standard elements.

            final String[] completeAttributeNames = attributeDefinition.attributeName.completeAttributeNames;

            int index;
            for (final String completeAttributeName : completeAttributeNames) {

                index = binarySearch(this.templateMode.isCaseSensitive(), this.standardRepositoryNames, completeAttributeName);

                // binary Search returned (-(insertion point) - 1)
                this.standardRepositoryNames.add(((index + 1) * -1), completeAttributeName);
                this.standardRepository.add(((index + 1) * -1), attributeDefinition);

                index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, completeAttributeName);

                // binary Search returned (-(insertion point) - 1)
                this.repositoryNames.add(((index + 1) * -1), completeAttributeName);
                this.repository.add(((index + 1) * -1), attributeDefinition);

            }

            return attributeDefinition;

        }


        private static int binarySearch(
                final boolean caseSensitive, final List<String> values, final char[] text, final int offset, final int len) {

            int low = 0;
            int high = values.size() - 1;

            int mid, cmp;
            String midVal;

            while (low <= high) {

                mid = (low + high) >>> 1;
                midVal = values.get(mid);

                cmp = TextUtils.compareTo(caseSensitive, midVal, 0, midVal.length(), text, offset, len);

                if (cmp < 0) {
                    low = mid + 1;
                } else if (cmp > 0) {
                    high = mid - 1;
                } else {
                    // Found!!
                    return mid;
                }

            }

            return -(low + 1);  // Not Found!! We return (-(insertion point) - 1), to guarantee all non-founds are < 0

        }


        private static int binarySearch(final boolean caseSensitive, final List<String> values, final String text) {

            int low = 0;
            int high = values.size() - 1;

            int mid, cmp;
            String midVal;

            while (low <= high) {

                mid = (low + high) >>> 1;
                midVal = values.get(mid);

                cmp = TextUtils.compareTo(caseSensitive, midVal, text);

                if (cmp < 0) {
                    low = mid + 1;
                } else if (cmp > 0) {
                    high = mid - 1;
                } else {
                    // Found!!
                    return mid;
                }

            }

            return -(low + 1);  // Not Found!! We return (-(insertion point) - 1), to guarantee all non-founds are < 0

        }


        private static int binarySearch(final boolean caseSensitive,
                                        final List<String> values, final String prefix, final String attributeName) {

            // This method will be specialized in finding prefixed attribute names (in the prefix:name form)

            if (prefix == null || prefix.trim().length() == 0) {
                return binarySearch(caseSensitive, values, attributeName);
            }

            final int prefixLen = prefix.length();
            final int attributeNameLen = attributeName.length();

            int low = 0;
            int high = values.size() - 1;

            int mid, cmp;
            String midVal;
            int midValLen;

            while (low <= high) {

                mid = (low + high) >>> 1;
                midVal = values.get(mid);
                midValLen = midVal.length();

                if (TextUtils.startsWith(caseSensitive, midVal, prefix)) {

                    // Prefix matched, but it could be a mere coincidence if the text being evaluated doesn't have
                    // a ':' after the prefix letters, so we will make sure by comparing the next char manually

                    if (midValLen <= prefixLen) {
                        // midVal is exactly as prefix, therefore it goes first

                        low = mid + 1;

                    } else {

                        // Compare the next char
                        cmp = midVal.charAt(prefixLen) - ':';

                        if (cmp < 0) {
                            low = mid + 1;
                        } else if (cmp > 0) {
                            high = mid - 1;
                        } else {

                            // Prefix matches and we made sure midVal has a ':', so let's try the attributeName
                            cmp = TextUtils.compareTo(caseSensitive, midVal, prefixLen + 1, (midValLen - (prefixLen + 1)), attributeName, 0, attributeNameLen);

                            if (cmp < 0) {
                                low = mid + 1;
                            } else if (cmp > 0) {
                                high = mid - 1;
                            } else {
                                // Found!!
                                return mid;
                            }

                        }

                    }

                } else {

                    // midVal does not start with prefix, so comparing midVal and prefix should be enough

                    cmp = TextUtils.compareTo(caseSensitive, midVal, prefix);

                    if (cmp < 0) {
                        low = mid + 1;
                    } else if (cmp > 0) {
                        high = mid - 1;
                    } else {
                        // This is impossible - if they were the same, we'd have detected it already!
                        throw new IllegalStateException("Bad comparison of midVal \"" + midVal + "\" and prefix \"" + prefix + "\"");
                    }

                }

            }

            return -(low + 1);  // Not Found!! We return (-(insertion point) - 1), to guarantee all non-founds are < 0

        }


    }



}
