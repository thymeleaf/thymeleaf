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
    static final ElementDefinition HTML = new ElementDefinition(ElementNames.forHtmlName("html"), ElementType.NORMAL);

    // Document metadata
    static final ElementDefinition HEAD = new ElementDefinition(ElementNames.forHtmlName("head"), ElementType.NORMAL);
    static final ElementDefinition TITLE = new ElementDefinition(ElementNames.forHtmlName("title"), ElementType.ESCAPABLE_RAW_TEXT);
    static final ElementDefinition BASE = new ElementDefinition(ElementNames.forHtmlName("base"), ElementType.VOID);
    static final ElementDefinition LINK = new ElementDefinition(ElementNames.forHtmlName("link"), ElementType.VOID);
    static final ElementDefinition META = new ElementDefinition(ElementNames.forHtmlName("meta"), ElementType.VOID);
    static final ElementDefinition STYLE = new ElementDefinition(ElementNames.forHtmlName("style"), ElementType.RAW_TEXT);

    // Scripting
    static final ElementDefinition SCRIPT = new ElementDefinition(ElementNames.forHtmlName("script"), ElementType.RAW_TEXT);
    static final ElementDefinition NOSCRIPT = new ElementDefinition(ElementNames.forHtmlName("noscript"), ElementType.NORMAL);

    // Sections
    static final ElementDefinition BODY = new ElementDefinition(ElementNames.forHtmlName("body"), ElementType.NORMAL);
    static final ElementDefinition ARTICLE = new ElementDefinition(ElementNames.forHtmlName("article"), ElementType.NORMAL);
    static final ElementDefinition SECTION = new ElementDefinition(ElementNames.forHtmlName("section"), ElementType.NORMAL);
    static final ElementDefinition NAV = new ElementDefinition(ElementNames.forHtmlName("nav"), ElementType.NORMAL);
    static final ElementDefinition ASIDE = new ElementDefinition(ElementNames.forHtmlName("aside"), ElementType.NORMAL);
    static final ElementDefinition H1 = new ElementDefinition(ElementNames.forHtmlName("h1"), ElementType.NORMAL);
    static final ElementDefinition H2 = new ElementDefinition(ElementNames.forHtmlName("h2"), ElementType.NORMAL);
    static final ElementDefinition H3 = new ElementDefinition(ElementNames.forHtmlName("h3"), ElementType.NORMAL);
    static final ElementDefinition H4 = new ElementDefinition(ElementNames.forHtmlName("h4"), ElementType.NORMAL);
    static final ElementDefinition H5 = new ElementDefinition(ElementNames.forHtmlName("h5"), ElementType.NORMAL);
    static final ElementDefinition H6 = new ElementDefinition(ElementNames.forHtmlName("h6"), ElementType.NORMAL);
    static final ElementDefinition HGROUP = new ElementDefinition(ElementNames.forHtmlName("hgroup"), ElementType.NORMAL);
    static final ElementDefinition HEADER = new ElementDefinition(ElementNames.forHtmlName("header"), ElementType.NORMAL);
    static final ElementDefinition FOOTER = new ElementDefinition(ElementNames.forHtmlName("footer"), ElementType.NORMAL);
    static final ElementDefinition ADDRESS = new ElementDefinition(ElementNames.forHtmlName("address"), ElementType.NORMAL);
    static final ElementDefinition MAIN = new ElementDefinition(ElementNames.forHtmlName("main"), ElementType.NORMAL);

    // Grouping content
    static final ElementDefinition P = new ElementDefinition(ElementNames.forHtmlName("p"), ElementType.NORMAL);
    static final ElementDefinition HR = new ElementDefinition(ElementNames.forHtmlName("hr"), ElementType.VOID);
    static final ElementDefinition PRE = new ElementDefinition(ElementNames.forHtmlName("pre"), ElementType.NORMAL);
    static final ElementDefinition BLOCKQUOTE = new ElementDefinition(ElementNames.forHtmlName("blockquote"), ElementType.NORMAL);
    static final ElementDefinition OL = new ElementDefinition(ElementNames.forHtmlName("ol"), ElementType.NORMAL);
    static final ElementDefinition UL = new ElementDefinition(ElementNames.forHtmlName("ul"), ElementType.NORMAL);
    static final ElementDefinition LI = new ElementDefinition(ElementNames.forHtmlName("li"), ElementType.NORMAL);
    static final ElementDefinition DL = new ElementDefinition(ElementNames.forHtmlName("dl"), ElementType.NORMAL);
    static final ElementDefinition DT = new ElementDefinition(ElementNames.forHtmlName("dt"), ElementType.NORMAL);
    static final ElementDefinition DD = new ElementDefinition(ElementNames.forHtmlName("dd"), ElementType.NORMAL);
    static final ElementDefinition FIGURE = new ElementDefinition(ElementNames.forHtmlName("figure"), ElementType.NORMAL);
    static final ElementDefinition FIGCAPTION = new ElementDefinition(ElementNames.forHtmlName("figcaption"), ElementType.NORMAL);
    static final ElementDefinition DIV = new ElementDefinition(ElementNames.forHtmlName("div"), ElementType.NORMAL);

    // Text-level semantics
    static final ElementDefinition A = new ElementDefinition(ElementNames.forHtmlName("a"), ElementType.NORMAL);
    static final ElementDefinition EM = new ElementDefinition(ElementNames.forHtmlName("em"), ElementType.NORMAL);
    static final ElementDefinition STRONG = new ElementDefinition(ElementNames.forHtmlName("strong"), ElementType.NORMAL);
    static final ElementDefinition SMALL = new ElementDefinition(ElementNames.forHtmlName("small"), ElementType.NORMAL);
    static final ElementDefinition S = new ElementDefinition(ElementNames.forHtmlName("s"), ElementType.NORMAL);
    static final ElementDefinition CITE = new ElementDefinition(ElementNames.forHtmlName("cite"), ElementType.NORMAL);
    static final ElementDefinition G = new ElementDefinition(ElementNames.forHtmlName("g"), ElementType.NORMAL);
    static final ElementDefinition DFN = new ElementDefinition(ElementNames.forHtmlName("dfn"), ElementType.NORMAL);
    static final ElementDefinition ABBR = new ElementDefinition(ElementNames.forHtmlName("abbr"), ElementType.NORMAL);
    static final ElementDefinition TIME = new ElementDefinition(ElementNames.forHtmlName("time"), ElementType.NORMAL);
    static final ElementDefinition CODE = new ElementDefinition(ElementNames.forHtmlName("code"), ElementType.NORMAL);
    static final ElementDefinition VAR = new ElementDefinition(ElementNames.forHtmlName("var"), ElementType.NORMAL);
    static final ElementDefinition SAMP = new ElementDefinition(ElementNames.forHtmlName("samp"), ElementType.NORMAL);
    static final ElementDefinition KBD = new ElementDefinition(ElementNames.forHtmlName("kbd"), ElementType.NORMAL);
    static final ElementDefinition SUB = new ElementDefinition(ElementNames.forHtmlName("sub"), ElementType.NORMAL);
    static final ElementDefinition SUP = new ElementDefinition(ElementNames.forHtmlName("sup"), ElementType.NORMAL);
    static final ElementDefinition I = new ElementDefinition(ElementNames.forHtmlName("i"), ElementType.NORMAL);
    static final ElementDefinition B = new ElementDefinition(ElementNames.forHtmlName("b"), ElementType.NORMAL);
    static final ElementDefinition U = new ElementDefinition(ElementNames.forHtmlName("u"), ElementType.NORMAL);
    static final ElementDefinition MARK = new ElementDefinition(ElementNames.forHtmlName("mark"), ElementType.NORMAL);
    static final ElementDefinition RUBY = new ElementDefinition(ElementNames.forHtmlName("ruby"), ElementType.NORMAL);
    static final ElementDefinition RB = new ElementDefinition(ElementNames.forHtmlName("rb"), ElementType.NORMAL);
    static final ElementDefinition RT = new ElementDefinition(ElementNames.forHtmlName("rt"), ElementType.NORMAL);
    static final ElementDefinition RTC = new ElementDefinition(ElementNames.forHtmlName("rtc"), ElementType.NORMAL);
    static final ElementDefinition RP = new ElementDefinition(ElementNames.forHtmlName("rp"), ElementType.NORMAL);
    static final ElementDefinition BDI = new ElementDefinition(ElementNames.forHtmlName("bdi"), ElementType.NORMAL);
    static final ElementDefinition BDO = new ElementDefinition(ElementNames.forHtmlName("bdo"), ElementType.NORMAL);
    static final ElementDefinition SPAN = new ElementDefinition(ElementNames.forHtmlName("span"), ElementType.NORMAL);
    static final ElementDefinition BR = new ElementDefinition(ElementNames.forHtmlName("br"), ElementType.VOID);
    static final ElementDefinition WBR = new ElementDefinition(ElementNames.forHtmlName("wbr"), ElementType.VOID);

    // Edits
    static final ElementDefinition INS = new ElementDefinition(ElementNames.forHtmlName("ins"), ElementType.NORMAL);
    static final ElementDefinition DEL = new ElementDefinition(ElementNames.forHtmlName("del"), ElementType.NORMAL);

    // Embedded content
    static final ElementDefinition IMG = new ElementDefinition(ElementNames.forHtmlName("img"), ElementType.VOID);
    static final ElementDefinition IFRAME = new ElementDefinition(ElementNames.forHtmlName("iframe"), ElementType.NORMAL);
    static final ElementDefinition EMBED = new ElementDefinition(ElementNames.forHtmlName("embed"), ElementType.VOID);
    static final ElementDefinition OBJECT = new ElementDefinition(ElementNames.forHtmlName("object"), ElementType.NORMAL);
    static final ElementDefinition PARAM = new ElementDefinition(ElementNames.forHtmlName("param"), ElementType.VOID);
    static final ElementDefinition VIDEO = new ElementDefinition(ElementNames.forHtmlName("video"), ElementType.NORMAL);
    static final ElementDefinition AUDIO = new ElementDefinition(ElementNames.forHtmlName("audio"), ElementType.NORMAL);
    static final ElementDefinition SOURCE = new ElementDefinition(ElementNames.forHtmlName("source"), ElementType.VOID);
    static final ElementDefinition TRACK = new ElementDefinition(ElementNames.forHtmlName("track"), ElementType.VOID);
    static final ElementDefinition CANVAS = new ElementDefinition(ElementNames.forHtmlName("canvas"), ElementType.NORMAL);
    static final ElementDefinition MAP = new ElementDefinition(ElementNames.forHtmlName("map"), ElementType.NORMAL);
    static final ElementDefinition AREA = new ElementDefinition(ElementNames.forHtmlName("area"), ElementType.VOID);

    // Tabular data
    static final ElementDefinition TABLE = new ElementDefinition(ElementNames.forHtmlName("table"), ElementType.NORMAL);
    static final ElementDefinition CAPTION = new ElementDefinition(ElementNames.forHtmlName("caption"), ElementType.NORMAL);
    static final ElementDefinition COLGROUP = new ElementDefinition(ElementNames.forHtmlName("colgroup"), ElementType.NORMAL);
    static final ElementDefinition COL = new ElementDefinition(ElementNames.forHtmlName("col"), ElementType.VOID);
    static final ElementDefinition TBODY = new ElementDefinition(ElementNames.forHtmlName("tbody"), ElementType.NORMAL);
    static final ElementDefinition THEAD = new ElementDefinition(ElementNames.forHtmlName("thead"), ElementType.NORMAL);
    static final ElementDefinition TFOOT = new ElementDefinition(ElementNames.forHtmlName("tfoot"), ElementType.NORMAL);
    static final ElementDefinition TR = new ElementDefinition(ElementNames.forHtmlName("tr"), ElementType.NORMAL);
    static final ElementDefinition TD = new ElementDefinition(ElementNames.forHtmlName("td"), ElementType.NORMAL);
    static final ElementDefinition TH = new ElementDefinition(ElementNames.forHtmlName("th"), ElementType.NORMAL);

    // Forms
    static final ElementDefinition FORM = new ElementDefinition(ElementNames.forHtmlName("form"), ElementType.NORMAL);
    static final ElementDefinition FIELDSET = new ElementDefinition(ElementNames.forHtmlName("fieldset"), ElementType.NORMAL);
    static final ElementDefinition LEGEND = new ElementDefinition(ElementNames.forHtmlName("legend"), ElementType.NORMAL);
    static final ElementDefinition LABEL = new ElementDefinition(ElementNames.forHtmlName("label"), ElementType.NORMAL);
    static final ElementDefinition INPUT = new ElementDefinition(ElementNames.forHtmlName("input"), ElementType.VOID);
    static final ElementDefinition BUTTON = new ElementDefinition(ElementNames.forHtmlName("button"), ElementType.NORMAL);
    static final ElementDefinition SELECT = new ElementDefinition(ElementNames.forHtmlName("select"), ElementType.NORMAL);
    static final ElementDefinition DATALIST = new ElementDefinition(ElementNames.forHtmlName("datalist"), ElementType.NORMAL);
    static final ElementDefinition OPTGROUP = new ElementDefinition(ElementNames.forHtmlName("optgroup"), ElementType.NORMAL);
    static final ElementDefinition OPTION = new ElementDefinition(ElementNames.forHtmlName("option"), ElementType.NORMAL);
    static final ElementDefinition TEXTAREA = new ElementDefinition(ElementNames.forHtmlName("textarea"), ElementType.ESCAPABLE_RAW_TEXT);
    static final ElementDefinition KEYGEN = new ElementDefinition(ElementNames.forHtmlName("keygen"), ElementType.VOID);
    static final ElementDefinition OUTPUT = new ElementDefinition(ElementNames.forHtmlName("output"), ElementType.NORMAL);
    static final ElementDefinition PROGRESS = new ElementDefinition(ElementNames.forHtmlName("progress"), ElementType.NORMAL);
    static final ElementDefinition METER = new ElementDefinition(ElementNames.forHtmlName("meter"), ElementType.NORMAL);

    // Interactive elements
    static final ElementDefinition DETAILS = new ElementDefinition(ElementNames.forHtmlName("details"), ElementType.NORMAL);
    static final ElementDefinition SUMMARY = new ElementDefinition(ElementNames.forHtmlName("summary"), ElementType.NORMAL);
    static final ElementDefinition COMMAND = new ElementDefinition(ElementNames.forHtmlName("command"), ElementType.NORMAL);
    static final ElementDefinition MENU = new ElementDefinition(ElementNames.forHtmlName("menu"), ElementType.NORMAL);
    static final ElementDefinition MENUITEM = new ElementDefinition(ElementNames.forHtmlName("menuitem"), ElementType.VOID);
    static final ElementDefinition DIALOG = new ElementDefinition(ElementNames.forHtmlName("dialog"), ElementType.NORMAL);

    // WebComponents
    static final ElementDefinition TEMPLATE = new ElementDefinition(ElementNames.forHtmlName("template"), ElementType.RAW_TEXT);
    static final ElementDefinition ELEMENT = new ElementDefinition(ElementNames.forHtmlName("element"), ElementType.NORMAL);
    static final ElementDefinition DECORATOR = new ElementDefinition(ElementNames.forHtmlName("decorator"), ElementType.NORMAL);
    static final ElementDefinition CONTENT = new ElementDefinition(ElementNames.forHtmlName("content"), ElementType.NORMAL);
    static final ElementDefinition SHADOW = new ElementDefinition(ElementNames.forHtmlName("shadow"), ElementType.NORMAL);




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

        Collections.sort(htmlElementDefinitionListAux, new Comparator<ElementDefinition>() {
            public int compare(final ElementDefinition o1, final ElementDefinition o2) {
                return o1.getElementName().getElementName().compareTo(o2.getElementName().getElementName());
            }
        });


        ALL_STANDARD_HTML_ELEMENTS =
                Collections.unmodifiableSet(new LinkedHashSet<ElementDefinition>(htmlElementDefinitionListAux));


        final LinkedHashSet<String> htmlElementDefinitionNamesAux = new LinkedHashSet<String>(ALL_STANDARD_HTML_ELEMENTS.size() + 1, 1.0f);
        for (final ElementDefinition elementDefinition : ALL_STANDARD_HTML_ELEMENTS) {
            for (final String completeElementName : elementDefinition.getElementName().getCompleteElementNames()) {
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


        ElementDefinition getElement(final String text) {

            int index;

            if (this.standardRepository != null) {
                /*
                 * We first try to find it in the repository containing the standard elements, which does not need
                 * any synchronization.
                 */
                index = binarySearch(!this.html, this.standardRepositoryNames, text);

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
                index = binarySearch(!this.html, this.repositoryNames, text);

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
                return storeElement(text);
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

            final ElementName elementName =
                    this.html? ElementNames.forHtmlName(text, offset, len) : ElementNames.forXmlName(text, offset, len);

            final ElementDefinition elementDefinition = new ElementDefinition(elementName, ElementType.NORMAL);

            final String[] completeElementNames = elementName.getCompleteElementNames();

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

            final ElementName elementName =
                    this.html? ElementNames.forHtmlName(text) : ElementNames.forXmlName(text);

            final ElementDefinition elementDefinition = new ElementDefinition(elementName, ElementType.NORMAL);

            final String[] completeElementNames = elementName.getCompleteElementNames();

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

            final String[] completeElementNames = elementDefinition.getElementName().getCompleteElementNames();

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


    }

}
