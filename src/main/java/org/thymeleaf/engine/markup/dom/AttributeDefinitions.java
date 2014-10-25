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
package org.thymeleaf.engine.markup.dom;

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
public final class AttributeDefinitions {


    // We need two different repositories, for HTML and XML, because one is case-sensitive and the other is not.
    private static final AttributeDefinitionRepository HTML_ATTRIBUTE_REPOSITORY = new AttributeDefinitionRepository(false, true);
    private static final AttributeDefinitionRepository XML_ATTRIBUTE_REPOSITORY = new AttributeDefinitionRepository(true, true);



    // Set containing all the standard elements, for possible external reference
    public static final Set<AttributeDefinition> ALL_STANDARD_HTML_ATTRIBUTES;
    // Set containing all the standard element names, for possible external reference
    public static final Set<String> ALL_STANDARD_HTML_ATTRIBUTE_NAMES;




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

        ALL_STANDARD_HTML_ATTRIBUTE_NAMES =
                Collections.unmodifiableSet(new LinkedHashSet<String>(htmlAttributeNameListAux));



        final List<AttributeDefinition> htmlAttributeDefinitionListAux =
                new ArrayList<AttributeDefinition>(ALL_STANDARD_HTML_ATTRIBUTE_NAMES.size() + 1);
        for (final String attributeName : ALL_STANDARD_HTML_ATTRIBUTE_NAMES) {
            final AttributeDefinition attributeDefinition = new AttributeDefinition(attributeName);
            htmlAttributeDefinitionListAux.add(attributeDefinition);
        }

        ALL_STANDARD_HTML_ATTRIBUTES =
                Collections.unmodifiableSet(new LinkedHashSet<AttributeDefinition>(htmlAttributeDefinitionListAux));



        /*
         * Register the standard elements at the element repository, in order to initialize it
         */
        for (final AttributeDefinition attributeDefinition : ALL_STANDARD_HTML_ATTRIBUTES) {
            HTML_ATTRIBUTE_REPOSITORY.storeStandardElement(attributeDefinition);
        }


        /*
         * Add Thymeleaf attributes to the repo.
         */

        // First, the th: and data-th- equivalents to all standard attribute names added to the HTML repository
        for (final String attributeName : ALL_STANDARD_HTML_ATTRIBUTE_NAMES) {
            final AttributeDefinition attributeDefinitionTh = new AttributeDefinition("th:" + attributeName);
            final AttributeDefinition attributeDefinitionDataTh = new AttributeDefinition("data-th-" + attributeName);
            HTML_ATTRIBUTE_REPOSITORY.storeStandardElement(attributeDefinitionTh);
            HTML_ATTRIBUTE_REPOSITORY.storeStandardElement(attributeDefinitionDataTh);
        }

        // Now add other thymeleaf attribute names, those that are not the th: or data-th- equivalents to standard HTML attributes
        final String[] customThymeleafAttributeNames = new String[] {
            "alt-title", "assert", "attr", "attrappend", "attrprepend", "case", "classappend",
            "each", "fragment" , "if", "inline", "unless", "include", "lang-xmllang",
            "remove", "replace", "styleappend", "substituteby", "switch", "text", "utext",
            "with", "xmlbase", "xmllang", "xmlspace"
        };
        for (final String attributeName : customThymeleafAttributeNames) {
            final AttributeDefinition attributeDefinitionTh = new AttributeDefinition("th:" + attributeName);
            final AttributeDefinition attributeDefinitionDataTh = new AttributeDefinition("data-th-" + attributeName);
            HTML_ATTRIBUTE_REPOSITORY.storeStandardElement(attributeDefinitionTh);
            HTML_ATTRIBUTE_REPOSITORY.storeStandardElement(attributeDefinitionDataTh);
            // This time we add them to the XML repository too, as it makes sense that these attributes are
            // (probably) the only ones used in XML documents.
            XML_ATTRIBUTE_REPOSITORY.storeStandardElement(attributeDefinitionTh);
            XML_ATTRIBUTE_REPOSITORY.storeStandardElement(attributeDefinitionDataTh);
        }

        // Finally, the "xmlns" attribute could be common in XML files
        XML_ATTRIBUTE_REPOSITORY.storeStandardElement(new AttributeDefinition("xmlns"));

    }




    public static AttributeDefinition forHtmlName(final String attributeName) {
        if (attributeName == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        return HTML_ATTRIBUTE_REPOSITORY.getAttribute(attributeName);
    }


    public static AttributeDefinition forHtmlName(final char[] attributeName, final int attributeNameOffset, final int attributeNameLen) {
        if (attributeName == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        if (attributeNameOffset < 0 || attributeNameLen < 0) {
            throw new IllegalArgumentException("Both name offset and length must be equal to or greater than zero");
        }
        return HTML_ATTRIBUTE_REPOSITORY.getAttribute(attributeName, attributeNameOffset, attributeNameLen);
    }



    public static AttributeDefinition forXmlName(final String attributeName) {
        if (attributeName == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        return XML_ATTRIBUTE_REPOSITORY.getAttribute(attributeName);
    }


    public static AttributeDefinition forXmlName(final char[] attributeName, final int attributeNameOffset, final int attributeNameLen) {
        if (attributeName == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        if (attributeNameOffset < 0 || attributeNameLen < 0) {
            throw new IllegalArgumentException("Both name offset and length must be equal to or greater than zero");
        }
        return XML_ATTRIBUTE_REPOSITORY.getAttribute(attributeName, attributeNameOffset, attributeNameLen);
    }






    private AttributeDefinitions() {
        super();
    }



    /*
     * This repository class is thread-safe. The reason for this is that it not only contains the
     * standard attributes, but will also contain new instances of AttributeDefinition created during processing (created
     * when asking the repository for them when they do not exist yet). As any thread can create a new attribute,
     * this has to be lock-protected.
     */
    static final class AttributeDefinitionRepository {

        private final boolean caseSensitive;

        private final List<AttributeDefinition> standardRepository; // read-only, no sync needed
        private final List<AttributeDefinition> repository;  // read-write, sync will be needed

        private final ReadWriteLock lock = new ReentrantReadWriteLock(true);
        private final Lock readLock = this.lock.readLock();
        private final Lock writeLock = this.lock.writeLock();


        AttributeDefinitionRepository(final boolean caseSensitive, final boolean createStandardRepo) {
            super();
            this.caseSensitive = caseSensitive;
            this.standardRepository = (createStandardRepo ? new ArrayList<AttributeDefinition>(700) : null);
            this.repository = new ArrayList<AttributeDefinition>(700);
        }


        AttributeDefinition getAttribute(final char[] text, final int offset, final int len) {

            int index;

            if (this.standardRepository != null) {
                /*
                 * We first try to find it in the repository containing the standard elements, which does not need
                 * any synchronization.
                 */
                index = binarySearch(this.caseSensitive, this.standardRepository, text, offset, len);

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

                index = binarySearch(this.caseSensitive, this.repository, text, offset, len);

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


        AttributeDefinition getAttribute(final String text) {

            int index;

            if (this.standardRepository != null) {
                /*
                 * We first try to find it in the repository containing the standard elements, which does not need
                 * any synchronization.
                 */
                index = binarySearch(this.caseSensitive, this.standardRepository, text);

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

                index = binarySearch(this.caseSensitive, this.repository, text);

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


        private AttributeDefinition storeElement(final char[] text, final int offset, final int len) {

            final int index = binarySearch(this.caseSensitive, this.repository, text, offset, len);
            if (index >= 0) {
                // It was already added while we were waiting for the lock!
                return this.repository.get(index);
            }

            final String elementName = new String(text, offset, len);

            final AttributeDefinition attribute =
                    new AttributeDefinition((this.caseSensitive? elementName : elementName.toLowerCase()));

            // binary Search returned (-(insertion point) - 1)
            this.repository.add(((index + 1) * -1), attribute);

            return attribute;

        }


        private AttributeDefinition storeElement(final String text) {

            final int index = binarySearch(this.caseSensitive, this.repository, text);
            if (index >= 0) {
                // It was already added while we were waiting for the lock!
                return this.repository.get(index);
            }

            final String attributeName = text;

            final AttributeDefinition attribute =
                    new AttributeDefinition((this.caseSensitive? attributeName : attributeName.toLowerCase()));

            // binary Search returned (-(insertion point) - 1)
            this.repository.add(((index + 1) * -1), attribute);

            return attribute;

        }


        private AttributeDefinition storeStandardElement(final AttributeDefinition element) {

            // This method will only be called from within the HtmlElements class itself, during initialization of
            // standard elements.

            if (this.standardRepository != null) {
                this.standardRepository.add(element);
                Collections.sort(this.standardRepository, AttributeComparator.forCaseSensitive(this.caseSensitive));
            }

            this.repository.add(element);
            Collections.sort(this.repository, AttributeComparator.forCaseSensitive(this.caseSensitive));

            return element;

        }


        private static int binarySearch(final boolean caseSensitive, final List<AttributeDefinition> values,
                                        final char[] text, final int offset, final int len) {

            int low = 0;
            int high = values.size() - 1;

            int mid, cmp;
            String midVal;

            while (low <= high) {

                mid = (low + high) >>> 1;
                midVal = values.get(mid).name;

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


        private static int binarySearch(final boolean caseSensitive, final List<AttributeDefinition> values, final String text) {

            int low = 0;
            int high = values.size() - 1;

            int mid, cmp;
            String midVal;

            while (low <= high) {

                mid = (low + high) >>> 1;
                midVal = values.get(mid).name;

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

        private static AttributeComparator INSTANCE_CASE_SENSITIVE = new AttributeComparator(true);
        private static AttributeComparator INSTANCE_CASE_INSENSITIVE = new AttributeComparator(false);

        private final boolean caseSensitive;

        static AttributeComparator forCaseSensitive(final boolean caseSensitive) {
            return caseSensitive ? INSTANCE_CASE_SENSITIVE : INSTANCE_CASE_INSENSITIVE;
        }

        private AttributeComparator(final boolean caseSensitive) {
            super();
            this.caseSensitive = caseSensitive;
        }

        public int compare(final AttributeDefinition o1, final AttributeDefinition o2) {
            // caseSensitive is true here because we might have
            return TextUtil.compareTo(this.caseSensitive, o1.name, o2.name);
        }
    }



}
