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
import java.util.HashSet;
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
public final class AttributeDefinitions {



    // Set containing all the standard elements, for possible external reference
    public static final Set<AttributeDefinition> ALL_STANDARD_HTML_ATTRIBUTES;
    // Set containing all the standard element names, for possible external reference
    public static final Set<String> ALL_STANDARD_HTML_ATTRIBUTE_NAMES;



    // We need two different repositories, for HTML and XML, because one is case-sensitive and the other is not.
    private final AttributeDefinitionRepository htmlAttributeRepository = new AttributeDefinitionRepository(true);
    private final AttributeDefinitionRepository xmlAttributeRepository = new AttributeDefinitionRepository(false);



    static {

        final List<String> htmlAttributeNameListAux =
                new ArrayList<String>(Arrays.asList(new String[]{
                        "abbr", "accept", "accept-charset", "accesskey", "action", "align", "alt", "archive",
                        "autocomplete", "autofocus", "autoplay", "axis", "border", "cellpadding", "cellspacing",
                        "challenge", "char", "charoff", "charset", "checked", "cite", "class", "classid",
                        "codebase", "codetype", "cols", "colspan", "command", "content", "contenteditable",
                        "contextmenu", "controls", "coords", "data", "datetime", "declare", "default",
                        "defer", "dir", "disabled", "draggable", "dropzone", "enctype", "for", "form",
                        "formaction", "formenctype", "formmethod", "formnovalidate", "formtarget",
                        "frame", "headers", "height", "hidden", "high", "href", "hreflang", "http-equiv",
                        "icon", "id", "ismap", "keytype", "kind", "label", "lang", "list", "longdesc",
                        "loop", "low", "max", "maxlength", "media", "method", "min", "multiple", "muted",
                        "name", "nohref", "novalidate", "onabort", "onafterprint", "onbeforeprint",
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
                        "placeholder", "poster", "preload", "profile", "radiogroup", "readonly", "rel",
                        "required", "rev", "rows", "rowspan", "rules", "scheme", "scope", "selected",
                        "shape", "size", "span", "spellcheck", "src", "srclang", "standby", "style", "summary",
                        "tabindex", "title", "translate", "type", "usemap", "valign", "value", "valuetype",
                        "width", "xml:lang", "xml:space", "xmlns"
                }));

        Collections.sort(htmlAttributeNameListAux);

        final Set<String> htmlBooleanAttributeNameListAux =
                new HashSet<String>(Arrays.asList(new String[]{
                        "async", "autofocus", "autoplay", "checked", "controls",
                        "declare", "default", "defer", "disabled", "formnovalidate",
                        "hidden", "ismap", "loop", "multiple", "novalidate",
                        "nowrap", "open", "pubdate", "readonly", "required",
                        "reversed", "selected", "scoped", "seamless"
                }));


        ALL_STANDARD_HTML_ATTRIBUTE_NAMES =
                Collections.unmodifiableSet(new LinkedHashSet<String>(htmlAttributeNameListAux));


        final List<AttributeDefinition> htmlAttributeDefinitionListAux =
                new ArrayList<AttributeDefinition>(ALL_STANDARD_HTML_ATTRIBUTE_NAMES.size() + 1);
        for (final String attributeNameStr : ALL_STANDARD_HTML_ATTRIBUTE_NAMES) {
            final AttributeName attributeName = AttributeName.forHtmlName(attributeNameStr);
            final AttributeDefinition attributeDefinition = new AttributeDefinition(attributeName, htmlBooleanAttributeNameListAux.contains(attributeNameStr));
            htmlAttributeDefinitionListAux.add(attributeDefinition);
        }

        ALL_STANDARD_HTML_ATTRIBUTES =
                Collections.unmodifiableSet(new LinkedHashSet<AttributeDefinition>(htmlAttributeDefinitionListAux));



    }






    public AttributeDefinitions() {

        super();

        /*
         * Register the standard elements at the element repository, in order to initialize it
         */
        for (final AttributeDefinition attributeDefinition : ALL_STANDARD_HTML_ATTRIBUTES) {
            this.htmlAttributeRepository.storeStandardAttribute(attributeDefinition);
        }

    }





    public AttributeDefinition forHtmlName(final String attributeName) {
        if (attributeName == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        return this.htmlAttributeRepository.getAttribute(attributeName);
    }


    public AttributeDefinition forHtmlName(final char[] attributeName, final int attributeNameOffset, final int attributeNameLen) {
        if (attributeName == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        if (attributeNameOffset < 0 || attributeNameLen < 0) {
            throw new IllegalArgumentException("Both name offset and length must be equal to or greater than zero");
        }
        return this.htmlAttributeRepository.getAttribute(attributeName, attributeNameOffset, attributeNameLen);
    }



    public AttributeDefinition forXmlName(final String attributeName) {
        if (attributeName == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        return this.xmlAttributeRepository.getAttribute(attributeName);
    }


    public AttributeDefinition forXmlName(final char[] attributeName, final int attributeNameOffset, final int attributeNameLen) {
        if (attributeName == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        if (attributeNameOffset < 0 || attributeNameLen < 0) {
            throw new IllegalArgumentException("Both name offset and length must be equal to or greater than zero");
        }
        return this.xmlAttributeRepository.getAttribute(attributeName, attributeNameOffset, attributeNameLen);
    }





    /*
     * This repository class is thread-safe. The reason for this is that it not only contains the
     * standard attributes, but will also contain new instances of AttributeDefinition created during processing (created
     * when asking the repository for them when they do not exist yet). As any thread can create a new attribute,
     * this has to be lock-protected.
     */
    static final class AttributeDefinitionRepository {

        private final boolean html;

        private final List<AttributeDefinition> standardRepository; // read-only, no sync needed
        private final List<AttributeDefinition> repositoryNS;  // read-write, sync will be needed
        private final List<AttributeDefinition> repositoryHtml5Custom;  // read-write, sync will be needed

        private final ReadWriteLock lock = new ReentrantReadWriteLock(true);
        private final Lock readLock = this.lock.readLock();
        private final Lock writeLock = this.lock.writeLock();


        AttributeDefinitionRepository(final boolean html) {
            super();
            this.html = html;
            this.standardRepository = (html ? new ArrayList<AttributeDefinition>(150) : null);
            this.repositoryNS = new ArrayList<AttributeDefinition>(500);
            this.repositoryHtml5Custom = (html ? new ArrayList<AttributeDefinition>(500) : null);
        }


        AttributeDefinition getAttribute(final char[] text, final int offset, final int len) {

            int index;

            if (this.standardRepository != null) { // either ns and html5 are null, or not
                /*
                 * We first try to find it in the repository containing the standard elements, which does not need
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
                return storeAttribute(text, offset, len);
            } finally {
                this.writeLock.unlock();
            }

        }


        AttributeDefinition getAttribute(final String text) {

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
                return storeAttribute(text);
            } finally {
                this.writeLock.unlock();
            }

        }


        private AttributeDefinition storeAttribute(final char[] text, final int offset, final int len) {

            final AttributeName attributeName =
                    this.html? AttributeName.forHtmlName(text, offset, len) : AttributeName.forXmlName(text, offset, len);

            final int indexNS = binarySearch(!this.html, this.repositoryNS, attributeName.completeNSAttributeName, true);
            if (indexNS >= 0) {
                // It was already added while we were waiting for the lock!
                return this.repositoryNS.get(indexNS);
            }

            final AttributeDefinition element = new AttributeDefinition(attributeName);

            if (this.html) {

                final int indexHtml5Custom =
                        binarySearch(!this.html, this.repositoryHtml5Custom, attributeName.completeHtml5CustomAttributeName, false);
                if (indexHtml5Custom >= 0) {
                    throw new IllegalStateException(
                            "Attribute was present in the repository in namespaced format, but it exists in HTML5Custom format.");
                }

                // binary Search returned (-(insertion point) - 1)
                this.repositoryHtml5Custom.add(((indexHtml5Custom + 1) * -1), element);

            }

            // binary Search returned (-(insertion point) - 1)
            this.repositoryNS.add(((indexNS + 1) * -1), element);

            return element;

        }


        private AttributeDefinition storeAttribute(final String text) {

            final AttributeName attributeName =
                    this.html? AttributeName.forHtmlName(text) : AttributeName.forXmlName(text);

            final int indexNS = binarySearch(!this.html, this.repositoryNS, attributeName.completeNSAttributeName, true);
            if (indexNS >= 0) {
                // It was already added while we were waiting for the lock!
                return this.repositoryNS.get(indexNS);
            }

            final AttributeDefinition element = new AttributeDefinition(attributeName);

            if (this.html) {

                final int indexHtml5Custom =
                        binarySearch(!this.html, this.repositoryHtml5Custom, attributeName.completeHtml5CustomAttributeName, false);
                if (indexHtml5Custom >= 0) {
                    throw new IllegalStateException(
                            "Attribute was present in the repository in namespaced format, but it exists in HTML5Custom format.");
                }

                // binary Search returned (-(insertion point) - 1)
                this.repositoryHtml5Custom.add(((indexHtml5Custom + 1) * -1), element);

            }

            // binary Search returned (-(insertion point) - 1)
            this.repositoryNS.add(((indexNS + 1) * -1), element);

            return element;

        }


        private AttributeDefinition storeStandardAttribute(final AttributeDefinition attributeDefinition) {

            // This method will only be called from within the AttributeDefinitions class itself, during initialization of
            // standard elements.

            final AttributeComparator comparatorNS =
                    this.html ? AttributeComparator.forHtml(true) : AttributeComparator.forXml();
            final AttributeComparator comparatorHtml5Custom =
                    this.html ? AttributeComparator.forHtml(false) : null;

            if (this.standardRepository != null) {
                this.standardRepository.add(attributeDefinition);
                Collections.sort(this.standardRepository, comparatorNS); // namespaced comparator is OK for standard
            }

            this.repositoryNS.add(attributeDefinition);
            Collections.sort(this.repositoryNS, comparatorNS);

            this.repositoryHtml5Custom.add(attributeDefinition);
            Collections.sort(this.repositoryHtml5Custom, comparatorHtml5Custom);

            return attributeDefinition;

        }


        private static int binarySearch(
                final boolean caseSensitive, final List<AttributeDefinition> values, final char[] text, final int offset, final int len,
                final boolean namespaced) {

            int low = 0;
            int high = values.size() - 1;

            int mid, cmp;
            String midVal;

            while (low <= high) {

                mid = (low + high) >>> 1;
                midVal = namespaced? values.get(mid).name.completeNSAttributeName : values.get(mid).name.completeHtml5CustomAttributeName;

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
                final boolean caseSensitive, final List<AttributeDefinition> values, final String text,
                final boolean namespaced) {

            int low = 0;
            int high = values.size() - 1;

            int mid, cmp;
            String midVal;

            while (low <= high) {

                mid = (low + high) >>> 1;
                midVal = namespaced? values.get(mid).name.completeNSAttributeName : values.get(mid).name.completeHtml5CustomAttributeName;

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


    private static class AttributeComparator implements Comparator<AttributeDefinition> {

        private static AttributeComparator INSTANCE_HTML_NS = new AttributeComparator(true, true);
        private static AttributeComparator INSTANCE_HTML_HTMLCUSTOM = new AttributeComparator(true, false);
        private static AttributeComparator INSTANCE_XML_NS = new AttributeComparator(false, true);

        private final boolean html;
        private final boolean namespaced;

        static AttributeComparator forHtml(final boolean namespaced) {
            return namespaced ? INSTANCE_HTML_NS : INSTANCE_HTML_HTMLCUSTOM;
        }

        static AttributeComparator forXml() {
            return INSTANCE_XML_NS;
        }

        private AttributeComparator(final boolean html, final boolean namespaced) {
            super();
            this.html = html;
            this.namespaced = namespaced;
        }

        public int compare(final AttributeDefinition o1, final AttributeDefinition o2) {
            // caseSensitive is true here because we might have
            if (this.namespaced) {
                return TextUtil.compareTo(!this.html, o1.name.completeNSAttributeName, o2.name.completeNSAttributeName);
            }
            return TextUtil.compareTo(!this.html, o1.name.completeHtml5CustomAttributeName, o2.name.completeHtml5CustomAttributeName);
        }
    }



}
