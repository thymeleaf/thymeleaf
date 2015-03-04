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
    public static final Set<HtmlElementDefinition> ALL_STANDARD_HTML_ELEMENTS;
    // Set containing all the standard element names, for possible external reference
    public static final Set<String> ALL_STANDARD_HTML_ELEMENT_NAMES;


    // Root
    static final HtmlElementDefinition HTML = new HtmlElementDefinition(ElementNames.forHtmlName("html"), HtmlElementType.NORMAL);

    // Document metadata
    static final HtmlElementDefinition HEAD = new HtmlElementDefinition(ElementNames.forHtmlName("head"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition TITLE = new HtmlElementDefinition(ElementNames.forHtmlName("title"), HtmlElementType.ESCAPABLE_RAW_TEXT);
    static final HtmlElementDefinition BASE = new HtmlElementDefinition(ElementNames.forHtmlName("base"), HtmlElementType.VOID);
    static final HtmlElementDefinition LINK = new HtmlElementDefinition(ElementNames.forHtmlName("link"), HtmlElementType.VOID);
    static final HtmlElementDefinition META = new HtmlElementDefinition(ElementNames.forHtmlName("meta"), HtmlElementType.VOID);
    static final HtmlElementDefinition STYLE = new HtmlElementDefinition(ElementNames.forHtmlName("style"), HtmlElementType.RAW_TEXT);

    // Scripting
    static final HtmlElementDefinition SCRIPT = new HtmlElementDefinition(ElementNames.forHtmlName("script"), HtmlElementType.RAW_TEXT);
    static final HtmlElementDefinition NOSCRIPT = new HtmlElementDefinition(ElementNames.forHtmlName("noscript"), HtmlElementType.NORMAL);

    // Sections
    static final HtmlElementDefinition BODY = new HtmlElementDefinition(ElementNames.forHtmlName("body"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition ARTICLE = new HtmlElementDefinition(ElementNames.forHtmlName("article"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition SECTION = new HtmlElementDefinition(ElementNames.forHtmlName("section"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition NAV = new HtmlElementDefinition(ElementNames.forHtmlName("nav"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition ASIDE = new HtmlElementDefinition(ElementNames.forHtmlName("aside"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition H1 = new HtmlElementDefinition(ElementNames.forHtmlName("h1"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition H2 = new HtmlElementDefinition(ElementNames.forHtmlName("h2"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition H3 = new HtmlElementDefinition(ElementNames.forHtmlName("h3"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition H4 = new HtmlElementDefinition(ElementNames.forHtmlName("h4"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition H5 = new HtmlElementDefinition(ElementNames.forHtmlName("h5"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition H6 = new HtmlElementDefinition(ElementNames.forHtmlName("h6"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition HGROUP = new HtmlElementDefinition(ElementNames.forHtmlName("hgroup"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition HEADER = new HtmlElementDefinition(ElementNames.forHtmlName("header"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition FOOTER = new HtmlElementDefinition(ElementNames.forHtmlName("footer"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition ADDRESS = new HtmlElementDefinition(ElementNames.forHtmlName("address"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition MAIN = new HtmlElementDefinition(ElementNames.forHtmlName("main"), HtmlElementType.NORMAL);

    // Grouping content
    static final HtmlElementDefinition P = new HtmlElementDefinition(ElementNames.forHtmlName("p"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition HR = new HtmlElementDefinition(ElementNames.forHtmlName("hr"), HtmlElementType.VOID);
    static final HtmlElementDefinition PRE = new HtmlElementDefinition(ElementNames.forHtmlName("pre"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition BLOCKQUOTE = new HtmlElementDefinition(ElementNames.forHtmlName("blockquote"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition OL = new HtmlElementDefinition(ElementNames.forHtmlName("ol"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition UL = new HtmlElementDefinition(ElementNames.forHtmlName("ul"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition LI = new HtmlElementDefinition(ElementNames.forHtmlName("li"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition DL = new HtmlElementDefinition(ElementNames.forHtmlName("dl"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition DT = new HtmlElementDefinition(ElementNames.forHtmlName("dt"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition DD = new HtmlElementDefinition(ElementNames.forHtmlName("dd"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition FIGURE = new HtmlElementDefinition(ElementNames.forHtmlName("figure"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition FIGCAPTION = new HtmlElementDefinition(ElementNames.forHtmlName("figcaption"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition DIV = new HtmlElementDefinition(ElementNames.forHtmlName("div"), HtmlElementType.NORMAL);

    // Text-level semantics
    static final HtmlElementDefinition A = new HtmlElementDefinition(ElementNames.forHtmlName("a"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition EM = new HtmlElementDefinition(ElementNames.forHtmlName("em"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition STRONG = new HtmlElementDefinition(ElementNames.forHtmlName("strong"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition SMALL = new HtmlElementDefinition(ElementNames.forHtmlName("small"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition S = new HtmlElementDefinition(ElementNames.forHtmlName("s"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition CITE = new HtmlElementDefinition(ElementNames.forHtmlName("cite"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition G = new HtmlElementDefinition(ElementNames.forHtmlName("g"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition DFN = new HtmlElementDefinition(ElementNames.forHtmlName("dfn"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition ABBR = new HtmlElementDefinition(ElementNames.forHtmlName("abbr"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition TIME = new HtmlElementDefinition(ElementNames.forHtmlName("time"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition CODE = new HtmlElementDefinition(ElementNames.forHtmlName("code"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition VAR = new HtmlElementDefinition(ElementNames.forHtmlName("var"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition SAMP = new HtmlElementDefinition(ElementNames.forHtmlName("samp"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition KBD = new HtmlElementDefinition(ElementNames.forHtmlName("kbd"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition SUB = new HtmlElementDefinition(ElementNames.forHtmlName("sub"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition SUP = new HtmlElementDefinition(ElementNames.forHtmlName("sup"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition I = new HtmlElementDefinition(ElementNames.forHtmlName("i"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition B = new HtmlElementDefinition(ElementNames.forHtmlName("b"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition U = new HtmlElementDefinition(ElementNames.forHtmlName("u"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition MARK = new HtmlElementDefinition(ElementNames.forHtmlName("mark"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition RUBY = new HtmlElementDefinition(ElementNames.forHtmlName("ruby"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition RB = new HtmlElementDefinition(ElementNames.forHtmlName("rb"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition RT = new HtmlElementDefinition(ElementNames.forHtmlName("rt"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition RTC = new HtmlElementDefinition(ElementNames.forHtmlName("rtc"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition RP = new HtmlElementDefinition(ElementNames.forHtmlName("rp"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition BDI = new HtmlElementDefinition(ElementNames.forHtmlName("bdi"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition BDO = new HtmlElementDefinition(ElementNames.forHtmlName("bdo"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition SPAN = new HtmlElementDefinition(ElementNames.forHtmlName("span"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition BR = new HtmlElementDefinition(ElementNames.forHtmlName("br"), HtmlElementType.VOID);
    static final HtmlElementDefinition WBR = new HtmlElementDefinition(ElementNames.forHtmlName("wbr"), HtmlElementType.VOID);

    // Edits
    static final HtmlElementDefinition INS = new HtmlElementDefinition(ElementNames.forHtmlName("ins"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition DEL = new HtmlElementDefinition(ElementNames.forHtmlName("del"), HtmlElementType.NORMAL);

    // Embedded content
    static final HtmlElementDefinition IMG = new HtmlElementDefinition(ElementNames.forHtmlName("img"), HtmlElementType.VOID);
    static final HtmlElementDefinition IFRAME = new HtmlElementDefinition(ElementNames.forHtmlName("iframe"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition EMBED = new HtmlElementDefinition(ElementNames.forHtmlName("embed"), HtmlElementType.VOID);
    static final HtmlElementDefinition OBJECT = new HtmlElementDefinition(ElementNames.forHtmlName("object"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition PARAM = new HtmlElementDefinition(ElementNames.forHtmlName("param"), HtmlElementType.VOID);
    static final HtmlElementDefinition VIDEO = new HtmlElementDefinition(ElementNames.forHtmlName("video"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition AUDIO = new HtmlElementDefinition(ElementNames.forHtmlName("audio"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition SOURCE = new HtmlElementDefinition(ElementNames.forHtmlName("source"), HtmlElementType.VOID);
    static final HtmlElementDefinition TRACK = new HtmlElementDefinition(ElementNames.forHtmlName("track"), HtmlElementType.VOID);
    static final HtmlElementDefinition CANVAS = new HtmlElementDefinition(ElementNames.forHtmlName("canvas"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition MAP = new HtmlElementDefinition(ElementNames.forHtmlName("map"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition AREA = new HtmlElementDefinition(ElementNames.forHtmlName("area"), HtmlElementType.VOID);

    // Tabular data
    static final HtmlElementDefinition TABLE = new HtmlElementDefinition(ElementNames.forHtmlName("table"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition CAPTION = new HtmlElementDefinition(ElementNames.forHtmlName("caption"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition COLGROUP = new HtmlElementDefinition(ElementNames.forHtmlName("colgroup"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition COL = new HtmlElementDefinition(ElementNames.forHtmlName("col"), HtmlElementType.VOID);
    static final HtmlElementDefinition TBODY = new HtmlElementDefinition(ElementNames.forHtmlName("tbody"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition THEAD = new HtmlElementDefinition(ElementNames.forHtmlName("thead"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition TFOOT = new HtmlElementDefinition(ElementNames.forHtmlName("tfoot"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition TR = new HtmlElementDefinition(ElementNames.forHtmlName("tr"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition TD = new HtmlElementDefinition(ElementNames.forHtmlName("td"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition TH = new HtmlElementDefinition(ElementNames.forHtmlName("th"), HtmlElementType.NORMAL);

    // Forms
    static final HtmlElementDefinition FORM = new HtmlElementDefinition(ElementNames.forHtmlName("form"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition FIELDSET = new HtmlElementDefinition(ElementNames.forHtmlName("fieldset"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition LEGEND = new HtmlElementDefinition(ElementNames.forHtmlName("legend"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition LABEL = new HtmlElementDefinition(ElementNames.forHtmlName("label"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition INPUT = new HtmlElementDefinition(ElementNames.forHtmlName("input"), HtmlElementType.VOID);
    static final HtmlElementDefinition BUTTON = new HtmlElementDefinition(ElementNames.forHtmlName("button"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition SELECT = new HtmlElementDefinition(ElementNames.forHtmlName("select"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition DATALIST = new HtmlElementDefinition(ElementNames.forHtmlName("datalist"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition OPTGROUP = new HtmlElementDefinition(ElementNames.forHtmlName("optgroup"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition OPTION = new HtmlElementDefinition(ElementNames.forHtmlName("option"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition TEXTAREA = new HtmlElementDefinition(ElementNames.forHtmlName("textarea"), HtmlElementType.ESCAPABLE_RAW_TEXT);
    static final HtmlElementDefinition KEYGEN = new HtmlElementDefinition(ElementNames.forHtmlName("keygen"), HtmlElementType.VOID);
    static final HtmlElementDefinition OUTPUT = new HtmlElementDefinition(ElementNames.forHtmlName("output"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition PROGRESS = new HtmlElementDefinition(ElementNames.forHtmlName("progress"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition METER = new HtmlElementDefinition(ElementNames.forHtmlName("meter"), HtmlElementType.NORMAL);

    // Interactive elements
    static final HtmlElementDefinition DETAILS = new HtmlElementDefinition(ElementNames.forHtmlName("details"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition SUMMARY = new HtmlElementDefinition(ElementNames.forHtmlName("summary"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition COMMAND = new HtmlElementDefinition(ElementNames.forHtmlName("command"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition MENU = new HtmlElementDefinition(ElementNames.forHtmlName("menu"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition MENUITEM = new HtmlElementDefinition(ElementNames.forHtmlName("menuitem"), HtmlElementType.VOID);
    static final HtmlElementDefinition DIALOG = new HtmlElementDefinition(ElementNames.forHtmlName("dialog"), HtmlElementType.NORMAL);

    // WebComponents
    static final HtmlElementDefinition TEMPLATE = new HtmlElementDefinition(ElementNames.forHtmlName("template"), HtmlElementType.RAW_TEXT);
    static final HtmlElementDefinition ELEMENT = new HtmlElementDefinition(ElementNames.forHtmlName("element"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition DECORATOR = new HtmlElementDefinition(ElementNames.forHtmlName("decorator"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition CONTENT = new HtmlElementDefinition(ElementNames.forHtmlName("content"), HtmlElementType.NORMAL);
    static final HtmlElementDefinition SHADOW = new HtmlElementDefinition(ElementNames.forHtmlName("shadow"), HtmlElementType.NORMAL);




    // We need two different repositories, for HTML and XML, because one is case-sensitive and the other is not.
    // Besides, we don't want HTML-only element types like "VOID" or "RAW_TEXT" be applied to XML elements even if they have the same name.
    // Also, there is no need to add any 'standard elements' to XML because other than the synthetic block, there are none, and avoiding its
    // creation we save a repository query each time an element is asked for.
    private final ElementDefinitionRepository htmlElementRepository = new ElementDefinitionRepository(true);
    private final ElementDefinitionRepository xmlElementRepository = new ElementDefinitionRepository(false);




    static {

        final List<HtmlElementDefinition> htmlElementDefinitionListAux =
                new ArrayList<HtmlElementDefinition>(Arrays.asList(
                        new HtmlElementDefinition[] {
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
                Collections.unmodifiableSet(new LinkedHashSet<HtmlElementDefinition>(htmlElementDefinitionListAux));


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




    public HtmlElementDefinition forHtmlName(final String elementName) {
        if (elementName == null || elementName.length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (HtmlElementDefinition) this.htmlElementRepository.getElement(elementName);
    }


    public HtmlElementDefinition forHtmlName(final String prefix, final String elementName) {
        if (elementName == null || elementName.length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (HtmlElementDefinition) this.htmlElementRepository.getElement(prefix, elementName);
    }


    public HtmlElementDefinition forHtmlName(final char[] elementName, final int elementNameOffset, final int elementNameLen) {
        if (elementName == null || elementNameLen == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (elementNameOffset < 0 || elementNameLen < 0) {
            throw new IllegalArgumentException("Both name offset and length must be equal to or greater than zero");
        }
        return (HtmlElementDefinition) this.htmlElementRepository.getElement(elementName, elementNameOffset, elementNameLen);
    }



    public XmlElementDefinition forXmlName(final String elementName) {
        if (elementName == null || elementName.length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (XmlElementDefinition) this.xmlElementRepository.getElement(elementName);
    }


    public XmlElementDefinition forXmlName(final String prefix, final String elementName) {
        if (elementName == null || elementName.length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (XmlElementDefinition) this.xmlElementRepository.getElement(prefix, elementName);
    }


    public XmlElementDefinition forXmlName(final char[] elementName, final int elementNameOffset, final int elementNameLen) {
        if (elementName == null || elementNameLen == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (elementNameOffset < 0 || elementNameLen < 0) {
            throw new IllegalArgumentException("Both name offset and length must be equal to or greater than zero");
        }
        return (XmlElementDefinition) this.xmlElementRepository.getElement(elementName, elementNameOffset, elementNameLen);
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
                            new HtmlElementDefinition(ElementNames.forHtmlName(text, offset, len), HtmlElementType.NORMAL) :
                            new XmlElementDefinition(ElementNames.forXmlName(text, offset, len));

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
                            new HtmlElementDefinition(ElementNames.forHtmlName(text), HtmlElementType.NORMAL) :
                            new XmlElementDefinition(ElementNames.forXmlName(text));

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
                            new HtmlElementDefinition(ElementNames.forHtmlName(prefix, elementName), HtmlElementType.NORMAL) :
                            new XmlElementDefinition(ElementNames.forXmlName(prefix, elementName));

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
