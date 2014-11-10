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
    public static final Set<ElementDefinition> ALL_STANDARD_HTML_ELEMENTS;
    // Set containing all the standard element names, for possible external reference
    public static final Set<String> ALL_STANDARD_HTML_ELEMENT_NAMES;


    // Root
    static final ElementDefinition HTML = new ElementDefinition(ElementName.forHtmlName("html"), ElementType.NORMAL);

    // Document metadata
    static final ElementDefinition HEAD = new ElementDefinition(ElementName.forHtmlName("head"), ElementType.NORMAL);
    static final ElementDefinition TITLE = new ElementDefinition(ElementName.forHtmlName("title"), ElementType.ESCAPABLE_RAW_TEXT);
    static final ElementDefinition BASE = new ElementDefinition(ElementName.forHtmlName("base"), ElementType.VOID);
    static final ElementDefinition LINK = new ElementDefinition(ElementName.forHtmlName("link"), ElementType.VOID);
    static final ElementDefinition META = new ElementDefinition(ElementName.forHtmlName("meta"), ElementType.VOID);
    static final ElementDefinition STYLE = new ElementDefinition(ElementName.forHtmlName("style"), ElementType.RAW_TEXT);

    // Scripting
    static final ElementDefinition SCRIPT = new ElementDefinition(ElementName.forHtmlName("script"), ElementType.RAW_TEXT);
    static final ElementDefinition NOSCRIPT = new ElementDefinition(ElementName.forHtmlName("noscript"), ElementType.NORMAL);

    // Sections
    static final ElementDefinition BODY = new ElementDefinition(ElementName.forHtmlName("body"), ElementType.NORMAL);
    static final ElementDefinition ARTICLE = new ElementDefinition(ElementName.forHtmlName("article"), ElementType.NORMAL);
    static final ElementDefinition SECTION = new ElementDefinition(ElementName.forHtmlName("section"), ElementType.NORMAL);
    static final ElementDefinition NAV = new ElementDefinition(ElementName.forHtmlName("nav"), ElementType.NORMAL);
    static final ElementDefinition ASIDE = new ElementDefinition(ElementName.forHtmlName("aside"), ElementType.NORMAL);
    static final ElementDefinition H1 = new ElementDefinition(ElementName.forHtmlName("h1"), ElementType.NORMAL);
    static final ElementDefinition H2 = new ElementDefinition(ElementName.forHtmlName("h2"), ElementType.NORMAL);
    static final ElementDefinition H3 = new ElementDefinition(ElementName.forHtmlName("h3"), ElementType.NORMAL);
    static final ElementDefinition H4 = new ElementDefinition(ElementName.forHtmlName("h4"), ElementType.NORMAL);
    static final ElementDefinition H5 = new ElementDefinition(ElementName.forHtmlName("h5"), ElementType.NORMAL);
    static final ElementDefinition H6 = new ElementDefinition(ElementName.forHtmlName("h6"), ElementType.NORMAL);
    static final ElementDefinition HGROUP = new ElementDefinition(ElementName.forHtmlName("hgroup"), ElementType.NORMAL);
    static final ElementDefinition HEADER = new ElementDefinition(ElementName.forHtmlName("header"), ElementType.NORMAL);
    static final ElementDefinition FOOTER = new ElementDefinition(ElementName.forHtmlName("footer"), ElementType.NORMAL);
    static final ElementDefinition ADDRESS = new ElementDefinition(ElementName.forHtmlName("address"), ElementType.NORMAL);
    static final ElementDefinition MAIN = new ElementDefinition(ElementName.forHtmlName("main"), ElementType.NORMAL);

    // Grouping content
    static final ElementDefinition P = new ElementDefinition(ElementName.forHtmlName("p"), ElementType.NORMAL);
    static final ElementDefinition HR = new ElementDefinition(ElementName.forHtmlName("hr"), ElementType.VOID);
    static final ElementDefinition PRE = new ElementDefinition(ElementName.forHtmlName("pre"), ElementType.NORMAL);
    static final ElementDefinition BLOCKQUOTE = new ElementDefinition(ElementName.forHtmlName("blockquote"), ElementType.NORMAL);
    static final ElementDefinition OL = new ElementDefinition(ElementName.forHtmlName("ol"), ElementType.NORMAL);
    static final ElementDefinition UL = new ElementDefinition(ElementName.forHtmlName("ul"), ElementType.NORMAL);
    static final ElementDefinition LI = new ElementDefinition(ElementName.forHtmlName("li"), ElementType.NORMAL);
    static final ElementDefinition DL = new ElementDefinition(ElementName.forHtmlName("dl"), ElementType.NORMAL);
    static final ElementDefinition DT = new ElementDefinition(ElementName.forHtmlName("dt"), ElementType.NORMAL);
    static final ElementDefinition DD = new ElementDefinition(ElementName.forHtmlName("dd"), ElementType.NORMAL);
    static final ElementDefinition FIGURE = new ElementDefinition(ElementName.forHtmlName("figure"), ElementType.NORMAL);
    static final ElementDefinition FIGCAPTION = new ElementDefinition(ElementName.forHtmlName("figcaption"), ElementType.NORMAL);
    static final ElementDefinition DIV = new ElementDefinition(ElementName.forHtmlName("div"), ElementType.NORMAL);

    // Text-level semantics
    static final ElementDefinition A = new ElementDefinition(ElementName.forHtmlName("a"), ElementType.NORMAL);
    static final ElementDefinition EM = new ElementDefinition(ElementName.forHtmlName("em"), ElementType.NORMAL);
    static final ElementDefinition STRONG = new ElementDefinition(ElementName.forHtmlName("strong"), ElementType.NORMAL);
    static final ElementDefinition SMALL = new ElementDefinition(ElementName.forHtmlName("small"), ElementType.NORMAL);
    static final ElementDefinition S = new ElementDefinition(ElementName.forHtmlName("s"), ElementType.NORMAL);
    static final ElementDefinition CITE = new ElementDefinition(ElementName.forHtmlName("cite"), ElementType.NORMAL);
    static final ElementDefinition G = new ElementDefinition(ElementName.forHtmlName("g"), ElementType.NORMAL);
    static final ElementDefinition DFN = new ElementDefinition(ElementName.forHtmlName("dfn"), ElementType.NORMAL);
    static final ElementDefinition ABBR = new ElementDefinition(ElementName.forHtmlName("abbr"), ElementType.NORMAL);
    static final ElementDefinition TIME = new ElementDefinition(ElementName.forHtmlName("time"), ElementType.NORMAL);
    static final ElementDefinition CODE = new ElementDefinition(ElementName.forHtmlName("code"), ElementType.NORMAL);
    static final ElementDefinition VAR = new ElementDefinition(ElementName.forHtmlName("var"), ElementType.NORMAL);
    static final ElementDefinition SAMP = new ElementDefinition(ElementName.forHtmlName("samp"), ElementType.NORMAL);
    static final ElementDefinition KBD = new ElementDefinition(ElementName.forHtmlName("kbd"), ElementType.NORMAL);
    static final ElementDefinition SUB = new ElementDefinition(ElementName.forHtmlName("sub"), ElementType.NORMAL);
    static final ElementDefinition SUP = new ElementDefinition(ElementName.forHtmlName("sup"), ElementType.NORMAL);
    static final ElementDefinition I = new ElementDefinition(ElementName.forHtmlName("i"), ElementType.NORMAL);
    static final ElementDefinition B = new ElementDefinition(ElementName.forHtmlName("b"), ElementType.NORMAL);
    static final ElementDefinition U = new ElementDefinition(ElementName.forHtmlName("u"), ElementType.NORMAL);
    static final ElementDefinition MARK = new ElementDefinition(ElementName.forHtmlName("mark"), ElementType.NORMAL);
    static final ElementDefinition RUBY = new ElementDefinition(ElementName.forHtmlName("ruby"), ElementType.NORMAL);
    static final ElementDefinition RB = new ElementDefinition(ElementName.forHtmlName("rb"), ElementType.NORMAL);
    static final ElementDefinition RT = new ElementDefinition(ElementName.forHtmlName("rt"), ElementType.NORMAL);
    static final ElementDefinition RTC = new ElementDefinition(ElementName.forHtmlName("rtc"), ElementType.NORMAL);
    static final ElementDefinition RP = new ElementDefinition(ElementName.forHtmlName("rp"), ElementType.NORMAL);
    static final ElementDefinition BDI = new ElementDefinition(ElementName.forHtmlName("bdi"), ElementType.NORMAL);
    static final ElementDefinition BDO = new ElementDefinition(ElementName.forHtmlName("bdo"), ElementType.NORMAL);
    static final ElementDefinition SPAN = new ElementDefinition(ElementName.forHtmlName("span"), ElementType.NORMAL);
    static final ElementDefinition BR = new ElementDefinition(ElementName.forHtmlName("br"), ElementType.VOID);
    static final ElementDefinition WBR = new ElementDefinition(ElementName.forHtmlName("wbr"), ElementType.VOID);

    // Edits
    static final ElementDefinition INS = new ElementDefinition(ElementName.forHtmlName("ins"), ElementType.NORMAL);
    static final ElementDefinition DEL = new ElementDefinition(ElementName.forHtmlName("del"), ElementType.NORMAL);

    // Embedded content
    static final ElementDefinition IMG = new ElementDefinition(ElementName.forHtmlName("img"), ElementType.VOID);
    static final ElementDefinition IFRAME = new ElementDefinition(ElementName.forHtmlName("iframe"), ElementType.NORMAL);
    static final ElementDefinition EMBED = new ElementDefinition(ElementName.forHtmlName("embed"), ElementType.VOID);
    static final ElementDefinition OBJECT = new ElementDefinition(ElementName.forHtmlName("object"), ElementType.NORMAL);
    static final ElementDefinition PARAM = new ElementDefinition(ElementName.forHtmlName("param"), ElementType.VOID);
    static final ElementDefinition VIDEO = new ElementDefinition(ElementName.forHtmlName("video"), ElementType.NORMAL);
    static final ElementDefinition AUDIO = new ElementDefinition(ElementName.forHtmlName("audio"), ElementType.NORMAL);
    static final ElementDefinition SOURCE = new ElementDefinition(ElementName.forHtmlName("source"), ElementType.VOID);
    static final ElementDefinition TRACK = new ElementDefinition(ElementName.forHtmlName("track"), ElementType.VOID);
    static final ElementDefinition CANVAS = new ElementDefinition(ElementName.forHtmlName("canvas"), ElementType.NORMAL);
    static final ElementDefinition MAP = new ElementDefinition(ElementName.forHtmlName("map"), ElementType.NORMAL);
    static final ElementDefinition AREA = new ElementDefinition(ElementName.forHtmlName("area"), ElementType.VOID);

    // Tabular data
    static final ElementDefinition TABLE = new ElementDefinition(ElementName.forHtmlName("table"), ElementType.NORMAL);
    static final ElementDefinition CAPTION = new ElementDefinition(ElementName.forHtmlName("caption"), ElementType.NORMAL);
    static final ElementDefinition COLGROUP = new ElementDefinition(ElementName.forHtmlName("colgroup"), ElementType.NORMAL);
    static final ElementDefinition COL = new ElementDefinition(ElementName.forHtmlName("col"), ElementType.VOID);
    static final ElementDefinition TBODY = new ElementDefinition(ElementName.forHtmlName("tbody"), ElementType.NORMAL);
    static final ElementDefinition THEAD = new ElementDefinition(ElementName.forHtmlName("thead"), ElementType.NORMAL);
    static final ElementDefinition TFOOT = new ElementDefinition(ElementName.forHtmlName("tfoot"), ElementType.NORMAL);
    static final ElementDefinition TR = new ElementDefinition(ElementName.forHtmlName("tr"), ElementType.NORMAL);
    static final ElementDefinition TD = new ElementDefinition(ElementName.forHtmlName("td"), ElementType.NORMAL);
    static final ElementDefinition TH = new ElementDefinition(ElementName.forHtmlName("th"), ElementType.NORMAL);

    // Forms
    static final ElementDefinition FORM = new ElementDefinition(ElementName.forHtmlName("form"), ElementType.NORMAL);
    static final ElementDefinition FIELDSET = new ElementDefinition(ElementName.forHtmlName("fieldset"), ElementType.NORMAL);
    static final ElementDefinition LEGEND = new ElementDefinition(ElementName.forHtmlName("legend"), ElementType.NORMAL);
    static final ElementDefinition LABEL = new ElementDefinition(ElementName.forHtmlName("label"), ElementType.NORMAL);
    static final ElementDefinition INPUT = new ElementDefinition(ElementName.forHtmlName("input"), ElementType.VOID);
    static final ElementDefinition BUTTON = new ElementDefinition(ElementName.forHtmlName("button"), ElementType.NORMAL);
    static final ElementDefinition SELECT = new ElementDefinition(ElementName.forHtmlName("select"), ElementType.NORMAL);
    static final ElementDefinition DATALIST = new ElementDefinition(ElementName.forHtmlName("datalist"), ElementType.NORMAL);
    static final ElementDefinition OPTGROUP = new ElementDefinition(ElementName.forHtmlName("optgroup"), ElementType.NORMAL);
    static final ElementDefinition OPTION = new ElementDefinition(ElementName.forHtmlName("option"), ElementType.NORMAL);
    static final ElementDefinition TEXTAREA = new ElementDefinition(ElementName.forHtmlName("textarea"), ElementType.ESCAPABLE_RAW_TEXT);
    static final ElementDefinition KEYGEN = new ElementDefinition(ElementName.forHtmlName("keygen"), ElementType.VOID);
    static final ElementDefinition OUTPUT = new ElementDefinition(ElementName.forHtmlName("output"), ElementType.NORMAL);
    static final ElementDefinition PROGRESS = new ElementDefinition(ElementName.forHtmlName("progress"), ElementType.NORMAL);
    static final ElementDefinition METER = new ElementDefinition(ElementName.forHtmlName("meter"), ElementType.NORMAL);

    // Interactive elements
    static final ElementDefinition DETAILS = new ElementDefinition(ElementName.forHtmlName("details"), ElementType.NORMAL);
    static final ElementDefinition SUMMARY = new ElementDefinition(ElementName.forHtmlName("summary"), ElementType.NORMAL);
    static final ElementDefinition COMMAND = new ElementDefinition(ElementName.forHtmlName("command"), ElementType.NORMAL);
    static final ElementDefinition MENU = new ElementDefinition(ElementName.forHtmlName("menu"), ElementType.NORMAL);
    static final ElementDefinition MENUITEM = new ElementDefinition(ElementName.forHtmlName("menuitem"), ElementType.VOID);
    static final ElementDefinition DIALOG = new ElementDefinition(ElementName.forHtmlName("dialog"), ElementType.NORMAL);

    // WebComponents
    static final ElementDefinition TEMPLATE = new ElementDefinition(ElementName.forHtmlName("template"), ElementType.RAW_TEXT);
    static final ElementDefinition ELEMENT = new ElementDefinition(ElementName.forHtmlName("element"), ElementType.NORMAL);
    static final ElementDefinition DECORATOR = new ElementDefinition(ElementName.forHtmlName("decorator"), ElementType.NORMAL);
    static final ElementDefinition CONTENT = new ElementDefinition(ElementName.forHtmlName("content"), ElementType.NORMAL);
    static final ElementDefinition SHADOW = new ElementDefinition(ElementName.forHtmlName("shadow"), ElementType.NORMAL);




    // We need two different repositories, for HTML and XML, because one is case-sensitive and the other is not.
    // Besides, we don't want HTML-only element types like "VOID" or "RAW_TEXT" be applied to XML elements even if they have the same name.
    // Also, there is no need to add any 'standard elements' to XML because other than the synthetic block, there are none, and avoiding its
    // creation we save a repository query each time an element is asked for.
    private final ElementDefinitionRepository htmlElementRepository = new ElementDefinitionRepository(true);
    private final ElementDefinitionRepository xmlElementRepository = new ElementDefinitionRepository(false);




    static {

        final List<ElementDefinition> htmlElementDefinitionListAux =
                new ArrayList<ElementDefinition>(Arrays.asList(
                        new ElementDefinition[] {
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

        Collections.sort(htmlElementDefinitionListAux, ElementComparator.forHtml(true)); // In this case, namespaced==true doesn't really matter


        ALL_STANDARD_HTML_ELEMENTS =
                Collections.unmodifiableSet(new LinkedHashSet<ElementDefinition>(htmlElementDefinitionListAux));


        final LinkedHashSet<String> htmlElementDefinitionNamesAux = new LinkedHashSet<String>(ALL_STANDARD_HTML_ELEMENTS.size() + 1, 1.0f);
        for (final ElementDefinition elementDefinition : ALL_STANDARD_HTML_ELEMENTS) {
            final ElementName elementName = elementDefinition.getElementName();
            if (elementName.getCompleteNSElementName() != null) {
                htmlElementDefinitionNamesAux.add(elementName.getCompleteNSElementName());
            }
            if (elementName.getCompleteHtml5CustomElementName() != null) {
                htmlElementDefinitionNamesAux.add(elementName.getCompleteHtml5CustomElementName());
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




    public ElementDefinition forHtmlName(final String elementName) {
        if (elementName == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        return this.htmlElementRepository.getElement(elementName);
    }


    public ElementDefinition forHtmlName(final char[] elementName, final int elementNameOffset, final int elementNameLen) {
        if (elementName == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        if (elementNameOffset < 0 || elementNameLen < 0) {
            throw new IllegalArgumentException("Both name offset and length must be equal to or greater than zero");
        }
        return this.htmlElementRepository.getElement(elementName, elementNameOffset, elementNameLen);
    }



    public ElementDefinition forXmlName(final String elementName) {
        if (elementName == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        return this.xmlElementRepository.getElement(elementName);
    }


    public ElementDefinition forXmlName(final char[] elementName, final int elementNameOffset, final int elementNameLen) {
        if (elementName == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        if (elementNameOffset < 0 || elementNameLen < 0) {
            throw new IllegalArgumentException("Both name offset and length must be equal to or greater than zero");
        }
        return this.xmlElementRepository.getElement(elementName, elementNameOffset, elementNameLen);
    }








    /*
     * This repository class is thread-safe. The reason for this is that it not only contains the
     * standard elements, but will also contain new instances of ElementDefinition created during processing (created
     * when asking the repository for them when they do not exist yet). As any thread can create a new element,
     * this has to be lock-protected.
     */
    static final class ElementDefinitionRepository {

        private final boolean html;

        private final List<ElementDefinition> standardRepository; // read-only, no sync needed
        private final List<ElementDefinition> repositoryNS;  // read-write, sync will be needed
        private final List<ElementDefinition> repositoryHtml5Custom;  // read-write, sync will be needed

        private final ReadWriteLock lock = new ReentrantReadWriteLock(true);
        private final Lock readLock = this.lock.readLock();
        private final Lock writeLock = this.lock.writeLock();


        ElementDefinitionRepository(final boolean html) {
            super();
            this.html = html;
            this.standardRepository = (html ? new ArrayList<ElementDefinition>(150) : null);
            this.repositoryNS = new ArrayList<ElementDefinition>(150);
            this.repositoryHtml5Custom = (html ? new ArrayList<ElementDefinition>(150) : null);
        }


        ElementDefinition getElement(final char[] text, final int offset, final int len) {

            int index;

            if (this.standardRepository != null) { // either ns and html5 are null, or not
                /*
                 * We first try to find it in the repositories containing the standard elements, which does not need
                 * any synchronization.
                 */
                index = binarySearch(!this.html, this.standardRepository, text, offset, len, true);

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
                index = binarySearch(!this.html, this.repositoryNS, text, offset, len, true);

                if (index >= 0) {
                    return this.repositoryNS.get(index);
                }

                if (this.html) {

                    /*
                     * Now look for the element in the HTML5-custom repository
                     */
                    index = binarySearch(!this.html, this.repositoryHtml5Custom, text, offset, len, false);

                    if (index >= 0) {
                        return this.repositoryHtml5Custom.get(index);
                    }

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


        ElementDefinition getElement(final String text) {

            int index;

            if (this.standardRepository != null) { // either ns and html5 are null, or not
                /*
                 * We first try to find it in the repository containing the standard elements, which does not need
                 * any synchronization.
                 */
                index = binarySearch(!this.html, this.standardRepository, text, true);

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
                index = binarySearch(!this.html, this.repositoryNS, text, true);

                if (index >= 0) {
                    return this.repositoryNS.get(index);
                }

                if (this.html) {

                    /*
                     * Now look for the element in the HTML5-custom repository
                     */
                    index = binarySearch(!this.html, this.repositoryHtml5Custom, text, false);

                    if (index >= 0) {
                        return this.repositoryHtml5Custom.get(index);
                    }

                }

            } finally {
                this.readLock.unlock();
            }


            /*
             * NOT FOUND. We need to obtain a write lock and store the text
             */
            this.writeLock.lock();
            try {
                return storeElement(text);
            } finally {
                this.writeLock.unlock();
            }

        }


        private ElementDefinition storeElement(final char[] text, final int offset, final int len) {

            final ElementName elementName =
                    this.html? ElementName.forHtmlName(text, offset, len) : ElementName.forXmlName(text, offset, len);

            final int indexNS = binarySearch(!this.html, this.repositoryNS, elementName.completeNSElementName, true);
            if (indexNS >= 0) {
                // It was already added while we were waiting for the lock!
                return this.repositoryNS.get(indexNS);
            }

            final ElementDefinition element = new ElementDefinition(elementName, ElementType.NORMAL);

            if (this.html) {

                final int indexHtml5Custom =
                        binarySearch(!this.html, this.repositoryHtml5Custom, elementName.completeHtml5CustomElementName, false);
                if (indexHtml5Custom >= 0) {
                    throw new IllegalStateException(
                            "Element was present in the repository in namespaced format, but it exists in HTML5Custom format.");
                }

                // binary Search returned (-(insertion point) - 1)
                this.repositoryHtml5Custom.add(((indexHtml5Custom + 1) * -1), element);

            }

            // binary Search returned (-(insertion point) - 1)
            this.repositoryNS.add(((indexNS + 1) * -1), element);

            return element;

        }


        private ElementDefinition storeElement(final String text) {

            final ElementName elementName =
                    this.html? ElementName.forHtmlName(text) : ElementName.forXmlName(text);

            final int indexNS = binarySearch(!this.html, this.repositoryNS, elementName.completeNSElementName, true);
            if (indexNS >= 0) {
                // It was already added while we were waiting for the lock!
                return this.repositoryNS.get(indexNS);
            }

            final ElementDefinition element = new ElementDefinition(elementName, ElementType.NORMAL);

            if (this.html) {

                final int indexHtml5Custom =
                        binarySearch(!this.html, this.repositoryHtml5Custom, elementName.completeHtml5CustomElementName, false);
                if (indexHtml5Custom >= 0) {
                    throw new IllegalStateException(
                            "Element was present in the repository in namespaced format, but it exists in HTML5Custom format.");
                }

                // binary Search returned (-(insertion point) - 1)
                this.repositoryHtml5Custom.add(((indexHtml5Custom + 1) * -1), element);

            }

            // binary Search returned (-(insertion point) - 1)
            this.repositoryNS.add(((indexNS + 1) * -1), element);

            return element;

        }


        private ElementDefinition storeStandardElement(final ElementDefinition elementDefinition) {

            // This method will only be called from within the ElementDefinitions class itself, during initialization of
            // standard elements.

            final ElementComparator comparatorNS =
                    this.html ? ElementComparator.forHtml(true) : ElementComparator.forXml();
            final ElementComparator comparatorHtml5Custom =
                    this.html ? ElementComparator.forHtml(false) : null;

            if (this.standardRepository != null) {
                this.standardRepository.add(elementDefinition);
                Collections.sort(this.standardRepository, comparatorNS); // namespaced comparator is OK for standard
            }

            this.repositoryNS.add(elementDefinition);
            Collections.sort(this.repositoryNS, comparatorNS);

            this.repositoryHtml5Custom.add(elementDefinition);
            Collections.sort(this.repositoryHtml5Custom, comparatorHtml5Custom);

            return elementDefinition;

        }


        private static int binarySearch(
                final boolean caseSensitive, final List<ElementDefinition> values, final char[] text, final int offset, final int len,
                final boolean namespaced) {

            int low = 0;
            int high = values.size() - 1;

            int mid, cmp;
            String midVal;

            while (low <= high) {

                mid = (low + high) >>> 1;
                midVal = namespaced? values.get(mid).name.completeNSElementName : values.get(mid).name.completeHtml5CustomElementName;

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


        private static int binarySearch(
                final boolean caseSensitive, final List<ElementDefinition> values, final String text,
                final boolean namespaced) {

            int low = 0;
            int high = values.size() - 1;

            int mid, cmp;
            String midVal;

            while (low <= high) {

                mid = (low + high) >>> 1;
                midVal = namespaced? values.get(mid).name.completeNSElementName : values.get(mid).name.completeHtml5CustomElementName;

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


    }


    private static class ElementComparator implements Comparator<ElementDefinition> {

        private static ElementComparator INSTANCE_HTML_NS = new ElementComparator(true, true);
        private static ElementComparator INSTANCE_HTML_HTMLCUSTOM = new ElementComparator(true, false);
        private static ElementComparator INSTANCE_XML_NS = new ElementComparator(false, true);

        private final boolean html;
        private final boolean namespaced;

        static ElementComparator forHtml(final boolean namespaced) {
            return namespaced ? INSTANCE_HTML_NS : INSTANCE_HTML_HTMLCUSTOM;
        }

        static ElementComparator forXml() {
            return INSTANCE_XML_NS;
        }

        private ElementComparator(final boolean html, final boolean namespaced) {
            super();
            this.html = html;
            this.namespaced = namespaced;
        }

        public int compare(final ElementDefinition o1, final ElementDefinition o2) {
            // caseSensitive is true here because we might have
            if (this.namespaced) {
                return TextUtil.compareTo(!this.html, o1.name.completeNSElementName, o2.name.completeNSElementName);
            }
            return TextUtil.compareTo(!this.html, o1.name.completeHtml5CustomElementName, o2.name.completeHtml5CustomElementName);
        }
    }

}
