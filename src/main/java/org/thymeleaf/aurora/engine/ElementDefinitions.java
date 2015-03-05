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
package org.thymeleaf.aurora.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.attoparser.util.TextUtil;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class ElementDefinitions {



    // Set containing all the standard elements, for possible external reference
    public static final Set<HTMLElementDefinition> ALL_STANDARD_HTML_ELEMENTS;
    // Set containing all the standard element names, for possible external reference
    public static final Set<String> ALL_STANDARD_HTML_ELEMENT_NAMES;


    // Root
    static final HTMLElementDefinition HTML = new HTMLElementDefinition(ElementNames.forHTMLName("html"), HTMLElementType.NORMAL);

    // Document metadata
    static final HTMLElementDefinition HEAD = new HTMLElementDefinition(ElementNames.forHTMLName("head"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition TITLE = new HTMLElementDefinition(ElementNames.forHTMLName("title"), HTMLElementType.ESCAPABLE_RAW_TEXT);
    static final HTMLElementDefinition BASE = new HTMLElementDefinition(ElementNames.forHTMLName("base"), HTMLElementType.VOID);
    static final HTMLElementDefinition LINK = new HTMLElementDefinition(ElementNames.forHTMLName("link"), HTMLElementType.VOID);
    static final HTMLElementDefinition META = new HTMLElementDefinition(ElementNames.forHTMLName("meta"), HTMLElementType.VOID);
    static final HTMLElementDefinition STYLE = new HTMLElementDefinition(ElementNames.forHTMLName("style"), HTMLElementType.RAW_TEXT);

    // Scripting
    static final HTMLElementDefinition SCRIPT = new HTMLElementDefinition(ElementNames.forHTMLName("script"), HTMLElementType.RAW_TEXT);
    static final HTMLElementDefinition NOSCRIPT = new HTMLElementDefinition(ElementNames.forHTMLName("noscript"), HTMLElementType.NORMAL);

    // Sections
    static final HTMLElementDefinition BODY = new HTMLElementDefinition(ElementNames.forHTMLName("body"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition ARTICLE = new HTMLElementDefinition(ElementNames.forHTMLName("article"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition SECTION = new HTMLElementDefinition(ElementNames.forHTMLName("section"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition NAV = new HTMLElementDefinition(ElementNames.forHTMLName("nav"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition ASIDE = new HTMLElementDefinition(ElementNames.forHTMLName("aside"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition H1 = new HTMLElementDefinition(ElementNames.forHTMLName("h1"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition H2 = new HTMLElementDefinition(ElementNames.forHTMLName("h2"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition H3 = new HTMLElementDefinition(ElementNames.forHTMLName("h3"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition H4 = new HTMLElementDefinition(ElementNames.forHTMLName("h4"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition H5 = new HTMLElementDefinition(ElementNames.forHTMLName("h5"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition H6 = new HTMLElementDefinition(ElementNames.forHTMLName("h6"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition HGROUP = new HTMLElementDefinition(ElementNames.forHTMLName("hgroup"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition HEADER = new HTMLElementDefinition(ElementNames.forHTMLName("header"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition FOOTER = new HTMLElementDefinition(ElementNames.forHTMLName("footer"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition ADDRESS = new HTMLElementDefinition(ElementNames.forHTMLName("address"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition MAIN = new HTMLElementDefinition(ElementNames.forHTMLName("main"), HTMLElementType.NORMAL);

    // Grouping content
    static final HTMLElementDefinition P = new HTMLElementDefinition(ElementNames.forHTMLName("p"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition HR = new HTMLElementDefinition(ElementNames.forHTMLName("hr"), HTMLElementType.VOID);
    static final HTMLElementDefinition PRE = new HTMLElementDefinition(ElementNames.forHTMLName("pre"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition BLOCKQUOTE = new HTMLElementDefinition(ElementNames.forHTMLName("blockquote"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition OL = new HTMLElementDefinition(ElementNames.forHTMLName("ol"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition UL = new HTMLElementDefinition(ElementNames.forHTMLName("ul"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition LI = new HTMLElementDefinition(ElementNames.forHTMLName("li"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition DL = new HTMLElementDefinition(ElementNames.forHTMLName("dl"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition DT = new HTMLElementDefinition(ElementNames.forHTMLName("dt"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition DD = new HTMLElementDefinition(ElementNames.forHTMLName("dd"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition FIGURE = new HTMLElementDefinition(ElementNames.forHTMLName("figure"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition FIGCAPTION = new HTMLElementDefinition(ElementNames.forHTMLName("figcaption"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition DIV = new HTMLElementDefinition(ElementNames.forHTMLName("div"), HTMLElementType.NORMAL);

    // Text-level semantics
    static final HTMLElementDefinition A = new HTMLElementDefinition(ElementNames.forHTMLName("a"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition EM = new HTMLElementDefinition(ElementNames.forHTMLName("em"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition STRONG = new HTMLElementDefinition(ElementNames.forHTMLName("strong"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition SMALL = new HTMLElementDefinition(ElementNames.forHTMLName("small"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition S = new HTMLElementDefinition(ElementNames.forHTMLName("s"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition CITE = new HTMLElementDefinition(ElementNames.forHTMLName("cite"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition G = new HTMLElementDefinition(ElementNames.forHTMLName("g"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition DFN = new HTMLElementDefinition(ElementNames.forHTMLName("dfn"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition ABBR = new HTMLElementDefinition(ElementNames.forHTMLName("abbr"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition TIME = new HTMLElementDefinition(ElementNames.forHTMLName("time"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition CODE = new HTMLElementDefinition(ElementNames.forHTMLName("code"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition VAR = new HTMLElementDefinition(ElementNames.forHTMLName("var"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition SAMP = new HTMLElementDefinition(ElementNames.forHTMLName("samp"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition KBD = new HTMLElementDefinition(ElementNames.forHTMLName("kbd"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition SUB = new HTMLElementDefinition(ElementNames.forHTMLName("sub"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition SUP = new HTMLElementDefinition(ElementNames.forHTMLName("sup"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition I = new HTMLElementDefinition(ElementNames.forHTMLName("i"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition B = new HTMLElementDefinition(ElementNames.forHTMLName("b"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition U = new HTMLElementDefinition(ElementNames.forHTMLName("u"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition MARK = new HTMLElementDefinition(ElementNames.forHTMLName("mark"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition RUBY = new HTMLElementDefinition(ElementNames.forHTMLName("ruby"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition RB = new HTMLElementDefinition(ElementNames.forHTMLName("rb"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition RT = new HTMLElementDefinition(ElementNames.forHTMLName("rt"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition RTC = new HTMLElementDefinition(ElementNames.forHTMLName("rtc"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition RP = new HTMLElementDefinition(ElementNames.forHTMLName("rp"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition BDI = new HTMLElementDefinition(ElementNames.forHTMLName("bdi"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition BDO = new HTMLElementDefinition(ElementNames.forHTMLName("bdo"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition SPAN = new HTMLElementDefinition(ElementNames.forHTMLName("span"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition BR = new HTMLElementDefinition(ElementNames.forHTMLName("br"), HTMLElementType.VOID);
    static final HTMLElementDefinition WBR = new HTMLElementDefinition(ElementNames.forHTMLName("wbr"), HTMLElementType.VOID);

    // Edits
    static final HTMLElementDefinition INS = new HTMLElementDefinition(ElementNames.forHTMLName("ins"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition DEL = new HTMLElementDefinition(ElementNames.forHTMLName("del"), HTMLElementType.NORMAL);

    // Embedded content
    static final HTMLElementDefinition IMG = new HTMLElementDefinition(ElementNames.forHTMLName("img"), HTMLElementType.VOID);
    static final HTMLElementDefinition IFRAME = new HTMLElementDefinition(ElementNames.forHTMLName("iframe"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition EMBED = new HTMLElementDefinition(ElementNames.forHTMLName("embed"), HTMLElementType.VOID);
    static final HTMLElementDefinition OBJECT = new HTMLElementDefinition(ElementNames.forHTMLName("object"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition PARAM = new HTMLElementDefinition(ElementNames.forHTMLName("param"), HTMLElementType.VOID);
    static final HTMLElementDefinition VIDEO = new HTMLElementDefinition(ElementNames.forHTMLName("video"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition AUDIO = new HTMLElementDefinition(ElementNames.forHTMLName("audio"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition SOURCE = new HTMLElementDefinition(ElementNames.forHTMLName("source"), HTMLElementType.VOID);
    static final HTMLElementDefinition TRACK = new HTMLElementDefinition(ElementNames.forHTMLName("track"), HTMLElementType.VOID);
    static final HTMLElementDefinition CANVAS = new HTMLElementDefinition(ElementNames.forHTMLName("canvas"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition MAP = new HTMLElementDefinition(ElementNames.forHTMLName("map"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition AREA = new HTMLElementDefinition(ElementNames.forHTMLName("area"), HTMLElementType.VOID);

    // Tabular data
    static final HTMLElementDefinition TABLE = new HTMLElementDefinition(ElementNames.forHTMLName("table"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition CAPTION = new HTMLElementDefinition(ElementNames.forHTMLName("caption"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition COLGROUP = new HTMLElementDefinition(ElementNames.forHTMLName("colgroup"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition COL = new HTMLElementDefinition(ElementNames.forHTMLName("col"), HTMLElementType.VOID);
    static final HTMLElementDefinition TBODY = new HTMLElementDefinition(ElementNames.forHTMLName("tbody"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition THEAD = new HTMLElementDefinition(ElementNames.forHTMLName("thead"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition TFOOT = new HTMLElementDefinition(ElementNames.forHTMLName("tfoot"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition TR = new HTMLElementDefinition(ElementNames.forHTMLName("tr"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition TD = new HTMLElementDefinition(ElementNames.forHTMLName("td"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition TH = new HTMLElementDefinition(ElementNames.forHTMLName("th"), HTMLElementType.NORMAL);

    // Forms
    static final HTMLElementDefinition FORM = new HTMLElementDefinition(ElementNames.forHTMLName("form"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition FIELDSET = new HTMLElementDefinition(ElementNames.forHTMLName("fieldset"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition LEGEND = new HTMLElementDefinition(ElementNames.forHTMLName("legend"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition LABEL = new HTMLElementDefinition(ElementNames.forHTMLName("label"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition INPUT = new HTMLElementDefinition(ElementNames.forHTMLName("input"), HTMLElementType.VOID);
    static final HTMLElementDefinition BUTTON = new HTMLElementDefinition(ElementNames.forHTMLName("button"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition SELECT = new HTMLElementDefinition(ElementNames.forHTMLName("select"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition DATALIST = new HTMLElementDefinition(ElementNames.forHTMLName("datalist"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition OPTGROUP = new HTMLElementDefinition(ElementNames.forHTMLName("optgroup"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition OPTION = new HTMLElementDefinition(ElementNames.forHTMLName("option"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition TEXTAREA = new HTMLElementDefinition(ElementNames.forHTMLName("textarea"), HTMLElementType.ESCAPABLE_RAW_TEXT);
    static final HTMLElementDefinition KEYGEN = new HTMLElementDefinition(ElementNames.forHTMLName("keygen"), HTMLElementType.VOID);
    static final HTMLElementDefinition OUTPUT = new HTMLElementDefinition(ElementNames.forHTMLName("output"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition PROGRESS = new HTMLElementDefinition(ElementNames.forHTMLName("progress"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition METER = new HTMLElementDefinition(ElementNames.forHTMLName("meter"), HTMLElementType.NORMAL);

    // Interactive elements
    static final HTMLElementDefinition DETAILS = new HTMLElementDefinition(ElementNames.forHTMLName("details"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition SUMMARY = new HTMLElementDefinition(ElementNames.forHTMLName("summary"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition COMMAND = new HTMLElementDefinition(ElementNames.forHTMLName("command"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition MENU = new HTMLElementDefinition(ElementNames.forHTMLName("menu"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition MENUITEM = new HTMLElementDefinition(ElementNames.forHTMLName("menuitem"), HTMLElementType.VOID);
    static final HTMLElementDefinition DIALOG = new HTMLElementDefinition(ElementNames.forHTMLName("dialog"), HTMLElementType.NORMAL);

    // WebComponents
    static final HTMLElementDefinition TEMPLATE = new HTMLElementDefinition(ElementNames.forHTMLName("template"), HTMLElementType.RAW_TEXT);
    static final HTMLElementDefinition ELEMENT = new HTMLElementDefinition(ElementNames.forHTMLName("element"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition DECORATOR = new HTMLElementDefinition(ElementNames.forHTMLName("decorator"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition CONTENT = new HTMLElementDefinition(ElementNames.forHTMLName("content"), HTMLElementType.NORMAL);
    static final HTMLElementDefinition SHADOW = new HTMLElementDefinition(ElementNames.forHTMLName("shadow"), HTMLElementType.NORMAL);




    // We need two different repositories, for HTML and XML, because one is case-sensitive and the other is not.
    // Besides, we don't want HTML-only element types like "VOID" or "RAW_TEXT" be applied to XML elements even if they have the same name.
    // Also, there is no need to add any 'standard elements' to XML because other than the synthetic block, there are none, and avoiding its
    // creation we save a repository query each time an element is asked for.
    private final ElementDefinitionRepository htmlElementRepository = new ElementDefinitionRepository(true);
    private final ElementDefinitionRepository xmlElementRepository = new ElementDefinitionRepository(false);




    static {

        final List<HTMLElementDefinition> htmlElementDefinitionListAux =
                new ArrayList<HTMLElementDefinition>(Arrays.asList(
                        new HTMLElementDefinition[] {
                                HTML, HEAD, TITLE, BASE, LINK, META, STYLE, SCRIPT, NOSCRIPT, BODY, ARTICLE,
                                SECTION, NAV, ASIDE, H1, H2, H3, H4, H5, H6, HGROUP, HEADER, FOOTER,
                                ADDRESS, P, HR, PRE, BLOCKQUOTE, OL, UL, LI, DL, DT, DD, FIGURE,
                                FIGCAPTION, DIV, A, EM, STRONG, SMALL, S, CITE, G, DFN, ABBR, TIME,
                                CODE, VAR, SAMP, KBD, SUB, SUP, I, B, U, MARK, RUBY, RB, RT, RTC,
                                RP, BDI, BDO, SPAN, BR, WBR, INS, DEL, IMG, IFRAME, EMBED, OBJECT,
                                PARAM, VIDEO, AUDIO, SOURCE, TRACK, CANVAS, MAP, AREA, TABLE, CAPTION,
                                COLGROUP, COL, TBODY, THEAD, TFOOT, TR, TD, TH, FORM, FIELDSET, LEGEND, LABEL,
                                INPUT, BUTTON, SELECT, DATALIST, OPTGROUP, OPTION, TEXTAREA, KEYGEN, OUTPUT, PROGRESS,
                                METER, DETAILS, SUMMARY, COMMAND, MENU, MENUITEM, DIALOG, MAIN, TEMPLATE,
                                ELEMENT, DECORATOR, CONTENT, SHADOW
                        }));

        Collections.sort(htmlElementDefinitionListAux, new Comparator<ElementDefinition>() {
            public int compare(final ElementDefinition o1, final ElementDefinition o2) {
                return o1.elementName.elementName.compareTo(o2.elementName.elementName);
            }
        });


        ALL_STANDARD_HTML_ELEMENTS =
                Collections.unmodifiableSet(new LinkedHashSet<HTMLElementDefinition>(htmlElementDefinitionListAux));


        final LinkedHashSet<String> htmlElementDefinitionNamesAux = new LinkedHashSet<String>(ALL_STANDARD_HTML_ELEMENTS.size() + 1, 1.0f);
        for (final ElementDefinition elementDefinition : ALL_STANDARD_HTML_ELEMENTS) {
            for (final String completeElementName : elementDefinition.elementName.completeElementNames) {
                htmlElementDefinitionNamesAux.add(completeElementName);
            }
        }

        ALL_STANDARD_HTML_ELEMENT_NAMES = Collections.unmodifiableSet(htmlElementDefinitionNamesAux);


    }






    public ElementDefinitions() {

        super();

        /*
         * Register the standard elements at the element repository, in order to initialize it
         */
        for (final ElementDefinition elementDefinition : ALL_STANDARD_HTML_ELEMENTS) {
            this.htmlElementRepository.storeStandardElement(elementDefinition);
        }

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

        private final List<String> standardRepositoryNames; // read-only, no sync needed
        private final List<ElementDefinition> standardRepository; // read-only, no sync needed

        private final List<String> repositoryNames;  // read-write, sync will be needed
        private final List<ElementDefinition> repository;  // read-write, sync will be needed

        private final ReadWriteLock lock = new ReentrantReadWriteLock(true);
        private final Lock readLock = this.lock.readLock();
        private final Lock writeLock = this.lock.writeLock();


        ElementDefinitionRepository(final boolean html) {

            super();

            this.html = html;

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
                            new HTMLElementDefinition(ElementNames.forHTMLName(text, offset, len), HTMLElementType.NORMAL) :
                            new XMLElementDefinition(ElementNames.forXMLName(text, offset, len));

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
                            new HTMLElementDefinition(ElementNames.forHTMLName(text), HTMLElementType.NORMAL) :
                            new XMLElementDefinition(ElementNames.forXMLName(text));

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
                            new HTMLElementDefinition(ElementNames.forHTMLName(prefix, elementName), HTMLElementType.NORMAL) :
                            new XMLElementDefinition(ElementNames.forXMLName(prefix, elementName));

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

}
