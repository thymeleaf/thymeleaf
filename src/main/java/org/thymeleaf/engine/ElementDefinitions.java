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
package org.thymeleaf.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.attoparser.util.TextUtil;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.processor.element.IElementProcessor;
import org.thymeleaf.processor.element.MatchingAttributeName;
import org.thymeleaf.processor.element.MatchingElementName;
import org.thymeleaf.templatemode.TemplateMode;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class ElementDefinitions {


    // Set containing all the standard element names, for possible external reference
    public static final Set<String> ALL_STANDARD_HTML_ELEMENT_NAMES;


    // Root
    private static final HTMLElementDefinitionSpec HTML = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("html"), HTMLElementType.NORMAL);

    // Document metadata
    private static final HTMLElementDefinitionSpec HEAD = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("head"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec TITLE = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("title"), HTMLElementType.ESCAPABLE_RAW_TEXT);
    private static final HTMLElementDefinitionSpec BASE = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("base"), HTMLElementType.VOID);
    private static final HTMLElementDefinitionSpec LINK = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("link"), HTMLElementType.VOID);
    private static final HTMLElementDefinitionSpec META = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("meta"), HTMLElementType.VOID);
    private static final HTMLElementDefinitionSpec STYLE = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("style"), HTMLElementType.RAW_TEXT);

    // Scripting
    private static final HTMLElementDefinitionSpec SCRIPT = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("script"), HTMLElementType.RAW_TEXT);
    private static final HTMLElementDefinitionSpec NOSCRIPT = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("noscript"), HTMLElementType.NORMAL);

    // Sections
    private static final HTMLElementDefinitionSpec BODY = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("body"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec ARTICLE = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("article"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec SECTION = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("section"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec NAV = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("nav"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec ASIDE = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("aside"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec H1 = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("h1"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec H2 = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("h2"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec H3 = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("h3"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec H4 = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("h4"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec H5 = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("h5"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec H6 = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("h6"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec HGROUP = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("hgroup"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec HEADER = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("header"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec FOOTER = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("footer"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec ADDRESS = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("address"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec MAIN = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("main"), HTMLElementType.NORMAL);

    // Grouping content
    private static final HTMLElementDefinitionSpec P = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("p"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec HR = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("hr"), HTMLElementType.VOID);
    private static final HTMLElementDefinitionSpec PRE = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("pre"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec BLOCKQUOTE = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("blockquote"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec OL = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("ol"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec UL = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("ul"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec LI = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("li"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec DL = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("dl"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec DT = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("dt"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec DD = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("dd"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec FIGURE = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("figure"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec FIGCAPTION = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("figcaption"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec DIV = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("div"), HTMLElementType.NORMAL);

    // Text-level semantics
    private static final HTMLElementDefinitionSpec A = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("a"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec EM = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("em"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec STRONG = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("strong"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec SMALL = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("small"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec S = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("s"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec CITE = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("cite"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec G = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("g"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec DFN = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("dfn"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec ABBR = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("abbr"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec TIME = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("time"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec CODE = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("code"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec VAR = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("var"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec SAMP = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("samp"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec KBD = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("kbd"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec SUB = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("sub"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec SUP = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("sup"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec I = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("i"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec B = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("b"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec U = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("u"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec MARK = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("mark"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec RUBY = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("ruby"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec RB = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("rb"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec RT = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("rt"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec RTC = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("rtc"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec RP = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("rp"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec BDI = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("bdi"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec BDO = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("bdo"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec SPAN = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("span"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec BR = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("br"), HTMLElementType.VOID);
    private static final HTMLElementDefinitionSpec WBR = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("wbr"), HTMLElementType.VOID);

    // Edits
    private static final HTMLElementDefinitionSpec INS = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("ins"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec DEL = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("del"), HTMLElementType.NORMAL);

    // Embedded content
    private static final HTMLElementDefinitionSpec IMG = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("img"), HTMLElementType.VOID);
    private static final HTMLElementDefinitionSpec IFRAME = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("iframe"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec EMBED = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("embed"), HTMLElementType.VOID);
    private static final HTMLElementDefinitionSpec OBJECT = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("object"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec PARAM = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("param"), HTMLElementType.VOID);
    private static final HTMLElementDefinitionSpec VIDEO = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("video"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec AUDIO = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("audio"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec SOURCE = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("source"), HTMLElementType.VOID);
    private static final HTMLElementDefinitionSpec TRACK = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("track"), HTMLElementType.VOID);
    private static final HTMLElementDefinitionSpec CANVAS = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("canvas"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec MAP = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("map"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec AREA = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("area"), HTMLElementType.VOID);

    // Tabular data
    private static final HTMLElementDefinitionSpec TABLE = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("table"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec CAPTION = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("caption"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec COLGROUP = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("colgroup"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec COL = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("col"), HTMLElementType.VOID);
    private static final HTMLElementDefinitionSpec TBODY = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("tbody"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec THEAD = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("thead"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec TFOOT = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("tfoot"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec TR = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("tr"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec TD = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("td"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec TH = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("th"), HTMLElementType.NORMAL);

    // Forms
    private static final HTMLElementDefinitionSpec FORM = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("form"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec FIELDSET = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("fieldset"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec LEGEND = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("legend"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec LABEL = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("label"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec INPUT = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("input"), HTMLElementType.VOID);
    private static final HTMLElementDefinitionSpec BUTTON = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("button"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec SELECT = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("select"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec DATALIST = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("datalist"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec OPTGROUP = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("optgroup"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec OPTION = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("option"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec TEXTAREA = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("textarea"), HTMLElementType.ESCAPABLE_RAW_TEXT);
    private static final HTMLElementDefinitionSpec KEYGEN = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("keygen"), HTMLElementType.VOID);
    private static final HTMLElementDefinitionSpec OUTPUT = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("output"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec PROGRESS = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("progress"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec METER = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("meter"), HTMLElementType.NORMAL);

    // Interactive elements
    private static final HTMLElementDefinitionSpec DETAILS = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("details"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec SUMMARY = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("summary"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec COMMAND = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("command"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec MENU = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("menu"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec MENUITEM = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("menuitem"), HTMLElementType.VOID);
    private static final HTMLElementDefinitionSpec DIALOG = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("dialog"), HTMLElementType.NORMAL);

    // WebComponents
    private static final HTMLElementDefinitionSpec TEMPLATE = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("template"), HTMLElementType.RAW_TEXT);
    private static final HTMLElementDefinitionSpec ELEMENT = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("element"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec DECORATOR = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("decorator"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec CONTENT = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("content"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec SHADOW = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("shadow"), HTMLElementType.NORMAL);




    // We need two different repositories, for HTML and XML, because one is case-sensitive and the other is not.
    // Besides, we don't want HTML-only element types like "VOID" or "RAW_TEXT" be applied to XML elements even if they have the same name.
    // Also, there is no need to add any 'standard elements' to XML because other than the synthetic block, there are none, and avoiding its
    // creation we save a repository query each time an element is asked for.
    private final ElementDefinitionRepository htmlElementRepository;
    private final ElementDefinitionRepository xmlElementRepository;




    static {

        /*
         * Initialize the public static Set with all the standard HTML element names
         */

        final List<String> htmlElementDefinitionNamesAux = new ArrayList<String>(HTMLElementDefinitionSpec.ALL_SPECS.size() + 1);
        for (final HTMLElementDefinitionSpec elementDefinitionSpec : HTMLElementDefinitionSpec.ALL_SPECS) {
            for (final String completeElementName : elementDefinitionSpec.name.completeElementNames) {
                htmlElementDefinitionNamesAux.add(completeElementName);
            }
        }

        Collections.sort(htmlElementDefinitionNamesAux);

        ALL_STANDARD_HTML_ELEMENT_NAMES = Collections.unmodifiableSet(new LinkedHashSet<String>(htmlElementDefinitionNamesAux));

    }


    /**
     * <strong>ONLY FOR INTERNAL USE</strong>. This constructor is meant to be called only from inside the engine.
     * It should never be called directly from any other classes.
     *
     * @param elementProcessorsByTemplateMode the processors (element and node), already ordered by precedence, which
     *                                        might be of application to the elements which definition is to be stored
     *                                        here.
     */
    public ElementDefinitions(final Map<TemplateMode, Set<IElementProcessor>> elementProcessorsByTemplateMode) {

        super();


        final List<HTMLElementDefinition> standardHTMLElementDefinitions =
                new ArrayList<HTMLElementDefinition>(HTMLElementDefinitionSpec.ALL_SPECS.size() + 1);
        for (final HTMLElementDefinitionSpec definitionSpec : HTMLElementDefinitionSpec.ALL_SPECS) {
            standardHTMLElementDefinitions.add(
                    buildHTMLElementDefinition(definitionSpec.name, definitionSpec.type, elementProcessorsByTemplateMode.get(TemplateMode.HTML)));
        }


        /*
         * Initialize the repositories
         */
        this.htmlElementRepository = new ElementDefinitionRepository(true, elementProcessorsByTemplateMode);
        this.xmlElementRepository = new ElementDefinitionRepository(false, elementProcessorsByTemplateMode);


        /*
         * Register the standard elements at the element repository, in order to initialize it
         */
        for (final HTMLElementDefinition elementDefinition : standardHTMLElementDefinitions) {
            this.htmlElementRepository.storeStandardElement(elementDefinition);
        }

    }




    private static HTMLElementDefinition buildHTMLElementDefinition(
            final HTMLElementName name, final HTMLElementType type, final Set<IElementProcessor> elementProcessors) {

        // No need to use a list for sorting - the elementProcessors set has already been ordered
        final Set<IElementProcessor> associatedProcessors = new LinkedHashSet<IElementProcessor>(2);

        if (elementProcessors != null) {
            for (final IElementProcessor processor : elementProcessors) {

                // Cannot be null -- has been previously validated
                final TemplateMode templateMode = processor.getTemplateMode();

                if (!templateMode.isHTML()) {
                    // We are creating an HTML element definition, therefore we are only interested on HTML processors
                    continue;
                }

                final MatchingElementName matchingElementName = processor.getMatchingElementName();
                final MatchingAttributeName matchingAttributeName = processor.getMatchingAttributeName();

                if ((matchingElementName != null && matchingElementName.getTemplateMode() != TemplateMode.HTML) ||
                        (matchingAttributeName != null && matchingAttributeName.getTemplateMode() != TemplateMode.HTML)) {
                    throw new ConfigurationException("HTML processors must return HTML element names and HTML attribute names (processor: " + processor.getClass().getName() + ")");
                }

                if (matchingAttributeName != null && !matchingAttributeName.isMatchingAllAttributes()) {
                    // This processor requires a specific attribute to be present. Given filtering by attribute is more
                    // restricted than filtering by element, we will not associate this processor with the element
                    // (will be instead associated with the attribute).
                    continue;
                }

                if (matchingElementName != null && !matchingElementName.matches(name)) {
                    // Doesn't match. This processor cannot be associated with this element
                    continue;
                }

                associatedProcessors.add(processor);

            }
        }

        // Build the final instance
        return new HTMLElementDefinition(name, type, associatedProcessors);

    }




    private static XMLElementDefinition buildXMLElementDefinition(
            final XMLElementName name, final Set<IElementProcessor> elementProcessors) {

        // No need to use a list for sorting - the elementProcessors set has already been ordered
        final Set<IElementProcessor> associatedProcessors = new LinkedHashSet<IElementProcessor>(2);

        if (elementProcessors != null) {
            for (final IElementProcessor processor : elementProcessors) {

                // Cannot be null -- has been previously validated
                final TemplateMode templateMode = processor.getTemplateMode();

                if (!templateMode.isXML()) {
                    // We are creating an XML element definition, therefore we are only interested on XML processors
                    continue;
                }

                final MatchingElementName matchingElementName = processor.getMatchingElementName();
                final MatchingAttributeName matchingAttributeName = processor.getMatchingAttributeName();

                if ((matchingElementName != null && matchingElementName.getTemplateMode() != TemplateMode.XML) ||
                        (matchingAttributeName != null && matchingAttributeName.getTemplateMode() != TemplateMode.XML)) {
                    throw new ConfigurationException("XML processors must return XML element names and XML attribute names (processor: " + processor.getClass().getName() + ")");
                }

                if (matchingAttributeName != null && !matchingAttributeName.isMatchingAllAttributes()) {
                    // This processor requires a specific attribute to be present. Given filtering by attribute is more
                    // restricted than filtering by element, we will not associate this processor with the element
                    // (will be instead associated with the attribute).
                    continue;
                }

                if (matchingElementName != null && !matchingElementName.matches(name)) {
                    // Doesn't match. This processor is not associated with this element
                    // Note that elementName == null means "apply to all processors"
                    continue;
                }

                associatedProcessors.add(processor);

            }
        }

        // Build the final instance
        return new XMLElementDefinition(name, associatedProcessors);

    }




    public HTMLElementDefinition forHTMLName(final String elementName) {
        if (elementName == null || elementName.length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (HTMLElementDefinition) this.htmlElementRepository.getElement(elementName);
    }


    public HTMLElementDefinition forHTMLName(final String prefix, final String elementName) {
        if (elementName == null || elementName.length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (HTMLElementDefinition) this.htmlElementRepository.getElement(prefix, elementName);
    }


    public HTMLElementDefinition forHTMLName(final char[] elementName, final int elementNameOffset, final int elementNameLen) {
        if (elementName == null || elementNameLen == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (elementNameOffset < 0 || elementNameLen < 0) {
            throw new IllegalArgumentException("Both name offset and length must be equal to or greater than zero");
        }
        return (HTMLElementDefinition) this.htmlElementRepository.getElement(elementName, elementNameOffset, elementNameLen);
    }



    public XMLElementDefinition forXMLName(final String elementName) {
        if (elementName == null || elementName.length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (XMLElementDefinition) this.xmlElementRepository.getElement(elementName);
    }


    public XMLElementDefinition forXMLName(final String prefix, final String elementName) {
        if (elementName == null || elementName.length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (XMLElementDefinition) this.xmlElementRepository.getElement(prefix, elementName);
    }


    public XMLElementDefinition forXMLName(final char[] elementName, final int elementNameOffset, final int elementNameLen) {
        if (elementName == null || elementNameLen == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (elementNameOffset < 0 || elementNameLen < 0) {
            throw new IllegalArgumentException("Both name offset and length must be equal to or greater than zero");
        }
        return (XMLElementDefinition) this.xmlElementRepository.getElement(elementName, elementNameOffset, elementNameLen);
    }








    /*
     * This repository class is thread-safe. The reason for this is that it not only contains the
     * standard elements, but will also contain new instances of ElementDefinition created during processing (created
     * when asking the repository for them when they do not exist yet). As any thread can create a new element,
     * this has to be lock-protected.
     */
    static final class ElementDefinitionRepository {

        private final boolean html;

        // These have already been filtered previously - only element-oriented processors will be here
        private final Map<TemplateMode, Set<IElementProcessor>> elementProcessorsByTemplateMode;

        private final List<String> standardRepositoryNames; // read-only, no sync needed
        private final List<ElementDefinition> standardRepository; // read-only, no sync needed

        private final List<String> repositoryNames;  // read-write, sync will be needed
        private final List<ElementDefinition> repository;  // read-write, sync will be needed

        private final ReadWriteLock lock = new ReentrantReadWriteLock(true);
        private final Lock readLock = this.lock.readLock();
        private final Lock writeLock = this.lock.writeLock();


        ElementDefinitionRepository(final boolean html, final Map<TemplateMode, Set<IElementProcessor>> elementProcessorsByTemplateMode) {

            super();

            this.html = html;
            this.elementProcessorsByTemplateMode = elementProcessorsByTemplateMode;

            this.standardRepositoryNames = (html ? new ArrayList<String>(150) : null);
            this.standardRepository = (html ? new ArrayList<ElementDefinition>(150) : null);

            this.repositoryNames = new ArrayList<String>(150);
            this.repository = new ArrayList<ElementDefinition>(150);

        }


        ElementDefinition getElement(final char[] text, final int offset, final int len) {

            int index;

            if (this.standardRepository != null) {
                /*
                 * We first try to find it in the repositories containing the standard elements, which does not need
                 * any synchronization.
                 */
                index = binarySearch(!this.html, this.standardRepositoryNames, text, offset, len);

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
                index = binarySearch(!this.html, this.repositoryNames, text, offset, len);

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
                return storeElement(text, offset, len);
            } finally {
                this.writeLock.unlock();
            }

        }


        ElementDefinition getElement(final String completeElementName) {

            int index;

            if (this.standardRepository != null) {
                /*
                 * We first try to find it in the repository containing the standard elements, which does not need
                 * any synchronization.
                 */
                index = binarySearch(!this.html, this.standardRepositoryNames, completeElementName);

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
                index = binarySearch(!this.html, this.repositoryNames, completeElementName);

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
                return storeElement(completeElementName);
            } finally {
                this.writeLock.unlock();
            }

        }


        ElementDefinition getElement(final String prefix, final String elementName) {

            int index;

            if (this.standardRepository != null) {
                /*
                 * We first try to find it in the repository containing the standard elements, which does not need
                 * any synchronization.
                 */
                index = binarySearch(!this.html, this.standardRepositoryNames, prefix, elementName);

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
                index = binarySearch(!this.html, this.repositoryNames, prefix, elementName);

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
                return storeElement(prefix, elementName);
            } finally {
                this.writeLock.unlock();
            }

        }


        private ElementDefinition storeElement(final char[] text, final int offset, final int len) {

            int index = binarySearch(!this.html, this.repositoryNames, text, offset, len);
            if (index >= 0) {
                // It was already added while we were waiting for the lock!
                return this.repository.get(index);
            }

            final ElementDefinition elementDefinition =
                    this.html?
                            buildHTMLElementDefinition(ElementNames.forHTMLName(text, offset, len), HTMLElementType.NORMAL, this.elementProcessorsByTemplateMode.get(TemplateMode.HTML)) :
                            buildXMLElementDefinition(ElementNames.forXMLName(text, offset, len), this.elementProcessorsByTemplateMode.get(TemplateMode.XML));

            final String[] completeElementNames = elementDefinition.elementName.completeElementNames;

            for (final String completeElementName : completeElementNames) {

                index = binarySearch(!this.html, this.repositoryNames, completeElementName);

                // binary Search returned (-(insertion point) - 1)
                this.repositoryNames.add(((index + 1) * -1), completeElementName);
                this.repository.add(((index + 1) * -1), elementDefinition);

            }

            return elementDefinition;

        }


        private ElementDefinition storeElement(final String text) {

            int index = binarySearch(!this.html, this.repositoryNames, text);
            if (index >= 0) {
                // It was already added while we were waiting for the lock!
                return this.repository.get(index);
            }

            final ElementDefinition elementDefinition =
                    this.html?
                            buildHTMLElementDefinition(ElementNames.forHTMLName(text), HTMLElementType.NORMAL, this.elementProcessorsByTemplateMode.get(TemplateMode.HTML)) :
                            buildXMLElementDefinition(ElementNames.forXMLName(text), this.elementProcessorsByTemplateMode.get(TemplateMode.XML));

            final String[] completeElementNames = elementDefinition.elementName.completeElementNames;

            for (final String completeElementName : completeElementNames) {

                index = binarySearch(!this.html, this.repositoryNames, completeElementName);

                // binary Search returned (-(insertion point) - 1)
                this.repositoryNames.add(((index + 1) * -1), completeElementName);
                this.repository.add(((index + 1) * -1), elementDefinition);

            }

            return elementDefinition;

        }


        private ElementDefinition storeElement(final String prefix, final String elementName) {

            int index = binarySearch(!this.html, this.repositoryNames, prefix, elementName);
            if (index >= 0) {
                // It was already added while we were waiting for the lock!
                return this.repository.get(index);
            }

            final ElementDefinition elementDefinition =
                    this.html?
                            buildHTMLElementDefinition(ElementNames.forHTMLName(prefix, elementName), HTMLElementType.NORMAL, this.elementProcessorsByTemplateMode.get(TemplateMode.HTML)) :
                            buildXMLElementDefinition(ElementNames.forXMLName(prefix, elementName), this.elementProcessorsByTemplateMode.get(TemplateMode.XML));

            final String[] completeElementNames = elementDefinition.elementName.completeElementNames;

            for (final String completeElementName : completeElementNames) {

                index = binarySearch(!this.html, this.repositoryNames, completeElementName);

                // binary Search returned (-(insertion point) - 1)
                this.repositoryNames.add(((index + 1) * -1), completeElementName);
                this.repository.add(((index + 1) * -1), elementDefinition);

            }

            return elementDefinition;

        }


        private ElementDefinition storeStandardElement(final ElementDefinition elementDefinition) {

            // This method will only be called from within the ElementDefinitions class itself, during initialization of
            // standard elements.

            final String[] completeElementNames = elementDefinition.elementName.completeElementNames;

            int index;
            for (final String completeElementName : completeElementNames) {

                index = binarySearch(!this.html, this.standardRepositoryNames, completeElementName);

                // binary Search returned (-(insertion point) - 1)
                this.standardRepositoryNames.add(((index + 1) * -1), completeElementName);
                this.standardRepository.add(((index + 1) * -1), elementDefinition);

                index = binarySearch(!this.html, this.repositoryNames, completeElementName);

                // binary Search returned (-(insertion point) - 1)
                this.repositoryNames.add(((index + 1) * -1), completeElementName);
                this.repository.add(((index + 1) * -1), elementDefinition);

            }

            return elementDefinition;

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

                cmp = TextUtil.compareTo(caseSensitive, midVal, 0, midVal.length(), text, offset, len);

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

                cmp = TextUtil.compareTo(caseSensitive, midVal, text);

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
                                        final List<String> values, final String prefix, final String elementName) {

            // This method will be specialized in finding prefixed element names (in the prefix:name form)

            if (prefix == null) {
                return binarySearch(caseSensitive, values, elementName);
            }

            final int prefixLen = prefix.length();
            final int elementNameLen = elementName.length();

            int low = 0;
            int high = values.size() - 1;

            int mid, cmp;
            String midVal;
            int midValLen;

            while (low <= high) {

                mid = (low + high) >>> 1;
                midVal = values.get(mid);
                midValLen = midVal.length();

                if (TextUtil.startsWith(caseSensitive, midVal, prefix)) {

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

                            // Prefix matches and we made sure midVal has a ':', so let's try the elementName
                            cmp = TextUtil.compareTo(caseSensitive, midVal, prefixLen + 1, (midValLen - (prefixLen + 1)), elementName, 0, elementNameLen);

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

                    cmp = TextUtil.compareTo(caseSensitive, midVal, prefix);

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




    private static final class HTMLElementDefinitionSpec {

        static final List<HTMLElementDefinitionSpec> ALL_SPECS = new ArrayList<HTMLElementDefinitionSpec>();

        HTMLElementName name;
        HTMLElementType type;
        
        HTMLElementDefinitionSpec(final HTMLElementName name, final HTMLElementType type) {
            super();
            this.name = name;
            this.type = type;
            ALL_SPECS.add(this);
        }
        
    }

}
