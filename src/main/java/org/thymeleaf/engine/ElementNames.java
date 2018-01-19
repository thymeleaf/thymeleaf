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
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.TextUtils;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public class ElementNames {


    // We need a different repository for each type of name
    private static final ElementNamesRepository htmlElementNamesRepository = new ElementNamesRepository(TemplateMode.HTML);
    private static final ElementNamesRepository xmlElementNamesRepository = new ElementNamesRepository(TemplateMode.XML);
    private static final ElementNamesRepository textElementNamesRepository = new ElementNamesRepository(TemplateMode.TEXT);





    private static TextElementName buildTextElementName(final char[] elementNameBuffer, final int elementNameOffset, final int elementNameLen) {

        if (elementNameBuffer == null) { // In text modes, elementName can actually be the empty string
            throw new IllegalArgumentException("Element name buffer cannot be null or empty");
        }
        if (elementNameOffset < 0 || elementNameLen < 0) {
            throw new IllegalArgumentException("Element name offset and len must be equal or greater than zero");
        }


        char c;
        int i = elementNameOffset;
        int n = elementNameLen;
        while (n-- != 0) {

            c = elementNameBuffer[i++];
            if (c != ':') {
                continue;
            }

            if (c == ':') {
                if (i == elementNameOffset + 1){
                    // ':' was the first char, no prefix there
                    return TextElementName.forName(null, new String(elementNameBuffer, elementNameOffset, elementNameLen));
                }

                return TextElementName.forName(
                        new String(elementNameBuffer, elementNameOffset, (i - (elementNameOffset + 1))),
                        new String(elementNameBuffer, i, (elementNameOffset + elementNameLen) - i));
            }

        }

        return TextElementName.forName(null, new String(elementNameBuffer, elementNameOffset, elementNameLen));

    }



    private static XMLElementName buildXMLElementName(final char[] elementNameBuffer, final int elementNameOffset, final int elementNameLen) {

        if (elementNameBuffer == null || elementNameLen == 0) {
            throw new IllegalArgumentException("Element name buffer cannot be null or empty");
        }
        if (elementNameOffset < 0 || elementNameLen < 0) {
            throw new IllegalArgumentException("Element name offset and len must be equal or greater than zero");
        }


        char c;
        int i = elementNameOffset;
        int n = elementNameLen;
        while (n-- != 0) {

            c = elementNameBuffer[i++];
            if (c != ':') {
                continue;
            }

            if (c == ':') {
                if (i == elementNameOffset + 1){
                    // ':' was the first char, no prefix there
                    return XMLElementName.forName(null, new String(elementNameBuffer, elementNameOffset, elementNameLen));
                }

                return XMLElementName.forName(
                        new String(elementNameBuffer, elementNameOffset, (i - (elementNameOffset + 1))),
                        new String(elementNameBuffer, i, (elementNameOffset + elementNameLen) - i));
            }

        }

        return XMLElementName.forName(null, new String(elementNameBuffer, elementNameOffset, elementNameLen));

    }



    private static HTMLElementName buildHTMLElementName(final char[] elementNameBuffer, final int elementNameOffset, final int elementNameLen) {

        if (elementNameBuffer == null || elementNameLen == 0) {
            throw new IllegalArgumentException("Element name buffer cannot be null or empty");
        }
        if (elementNameOffset < 0 || elementNameLen < 0) {
            throw new IllegalArgumentException("Element name offset and len must be equal or greater than zero");
        }


        char c;
        int i = elementNameOffset;
        int n = elementNameLen;
        while (n-- != 0) {

            c = elementNameBuffer[i++];
            if (c != ':' && c != '-') {
                continue;
            }

            if (c == ':') {
                if (i == elementNameOffset + 1){
                    // ':' was the first char, no prefix there
                    return HTMLElementName.forName(null, new String(elementNameBuffer, elementNameOffset, elementNameLen));
                }

                if (TextUtils.equals(TemplateMode.HTML.isCaseSensitive(), "xml:", 0, 4, elementNameBuffer, elementNameOffset, (i - elementNameOffset)) ||
                        TextUtils.equals(TemplateMode.HTML.isCaseSensitive(), "xmlns:", 0, 6, elementNameBuffer, elementNameOffset, (i - elementNameOffset))) {
                    // 'xml' is not a valid dialect prefix in HTML mode
                    return HTMLElementName.forName(null, new String(elementNameBuffer, elementNameOffset, elementNameLen));
                }

                return HTMLElementName.forName(
                        new String(elementNameBuffer, elementNameOffset, (i - (elementNameOffset + 1))),
                        new String(elementNameBuffer, i, (elementNameOffset + elementNameLen) - i));
            }

            if (c == '-') {
                if (i == elementNameOffset + 1) {
                    // '-' was the first char, no prefix there
                    return HTMLElementName.forName(null, new String(elementNameBuffer, elementNameOffset, elementNameLen));

                }
                return HTMLElementName.forName(
                        new String(elementNameBuffer, elementNameOffset, (i - (elementNameOffset + 1))),
                        new String(elementNameBuffer, i, (elementNameOffset + elementNameLen) - i));
            }

        }

        return HTMLElementName.forName(null, new String(elementNameBuffer, elementNameOffset, elementNameLen));

    }



    private static TextElementName buildTextElementName(final String elementName) {

        if (elementName == null) { // In text modes, elementName can actually be the empty string
            throw new IllegalArgumentException("Element name cannot be null");
        }


        char c;
        int i = 0;
        int n = elementName.length();
        while (n-- != 0) {

            c = elementName.charAt(i++);
            if (c != ':') {
                continue;
            }

            if (c == ':') {
                if (i == 1){
                    // ':' was the first char, no prefix there
                    return TextElementName.forName(null, elementName);
                }

                return TextElementName.forName(
                        elementName.substring(0, i - 1),
                        elementName.substring(i, elementName.length()));
            }

        }

        return TextElementName.forName(null, elementName);

    }



    private static XMLElementName buildXMLElementName(final String elementName) {

        if (elementName == null || elementName.length() == 0) {
            throw new IllegalArgumentException("Element name cannot be null or empty");
        }


        char c;
        int i = 0;
        int n = elementName.length();
        while (n-- != 0) {

            c = elementName.charAt(i++);
            if (c != ':') {
                continue;
            }

            if (c == ':') {
                if (i == 1){
                    // ':' was the first char, no prefix there
                    return XMLElementName.forName(null, elementName);
                }

                return XMLElementName.forName(
                        elementName.substring(0, i - 1),
                        elementName.substring(i, elementName.length()));
            }

        }

        return XMLElementName.forName(null, elementName);

    }



    private static HTMLElementName buildHTMLElementName(final String elementName) {

        if (elementName == null || elementName.length() == 0) {
            throw new IllegalArgumentException("Element name cannot be null or empty");
        }

        char c;
        int i = 0;
        int n = elementName.length();
        while (n-- != 0) {

            c = elementName.charAt(i++);
            if (c != ':' && c != '-') {
                continue;
            }

            if (c == ':') {
                if (i == 1){
                    // ':' was the first char, no prefix there
                    return HTMLElementName.forName(null, elementName);
                }

                if (TextUtils.equals(TemplateMode.HTML.isCaseSensitive(), "xml:", 0, 4, elementName, 0, i) ||
                        TextUtils.equals(TemplateMode.HTML.isCaseSensitive(), "xmlns:", 0, 6, elementName, 0, i)) {
                    // 'xml' is not a valid dialect prefix in HTML mode
                    return HTMLElementName.forName(null, elementName);
                }

                return HTMLElementName.forName(
                        elementName.substring(0, i - 1),
                        elementName.substring(i,elementName.length()));
            }

            if (c == '-') {
                if (i == 1) {
                    // '-' was the first char, no prefix there
                    return HTMLElementName.forName(null, elementName);

                }
                return HTMLElementName.forName(
                        elementName.substring(0, i - 1),
                        elementName.substring(i, elementName.length()));
            }

        }

        return HTMLElementName.forName(null, elementName);

    }



    private static TextElementName buildTextElementName(final String prefix, final String elementName) {
        if (elementName == null) { // Note TEXT element names CAN be empty (only element names, not attribute names)
            throw new IllegalArgumentException("Element name cannot be null or empty");
        }
        if (prefix == null || prefix.trim().length() == 0) {
            return buildTextElementName(elementName);
        }
        return TextElementName.forName(prefix, elementName);
    }



    private static XMLElementName buildXMLElementName(final String prefix, final String elementName) {
        if (elementName == null || elementName.length() == 0) {
            throw new IllegalArgumentException("Element name cannot be null or empty");
        }
        if (prefix == null || prefix.trim().length() == 0) {
            return buildXMLElementName(elementName);
        }
        return XMLElementName.forName(prefix, elementName);
    }



    private static HTMLElementName buildHTMLElementName(final String prefix, final String elementName) {
        if (elementName == null || elementName.length() == 0) {
            throw new IllegalArgumentException("Element name cannot be null or empty");
        }
        if (prefix == null || prefix.trim().length() == 0) {
            return buildHTMLElementName(elementName);
        }
        return HTMLElementName.forName(prefix, elementName);
    }



    public static ElementName forName(
            final TemplateMode templateMode, final char[] elementNameBuffer, final int elementNameOffset, final int elementNameLen) {

        if (templateMode == null) {
            throw new IllegalArgumentException("Template Mode cannot be null");
        }

        if (templateMode == TemplateMode.HTML) {
            return forHTMLName(elementNameBuffer, elementNameOffset, elementNameLen);
        }

        if (templateMode == TemplateMode.XML) {
            return forXMLName(elementNameBuffer, elementNameOffset, elementNameLen);
        }

        if (templateMode.isText()) {
            return forTextName(elementNameBuffer, elementNameOffset, elementNameLen);
        }

        throw new IllegalArgumentException("Unknown template mode '" + templateMode + "'");

    }



    public static ElementName forName(final TemplateMode templateMode, final String elementName) {

        if (templateMode == null) {
            throw new IllegalArgumentException("Template Mode cannot be null");
        }

        if (templateMode == TemplateMode.HTML) {
            return forHTMLName(elementName);
        }

        if (templateMode == TemplateMode.XML) {
            return forXMLName(elementName);
        }

        if (templateMode.isText()) {
            return forTextName(elementName);
        }

        throw new IllegalArgumentException("Unknown template mode '" + templateMode + "'");

    }



    public static ElementName forName(final TemplateMode templateMode, final String prefix, final String elementName) {

        if (templateMode == null) {
            throw new IllegalArgumentException("Template Mode cannot be null");
        }

        if (templateMode == TemplateMode.HTML) {
            return forHTMLName(prefix, elementName);
        }

        if (templateMode == TemplateMode.XML) {
            return forXMLName(prefix, elementName);
        }

        if (templateMode.isText()) {
            return forTextName(prefix, elementName);
        }

        throw new IllegalArgumentException("Unknown template mode '" + templateMode + "'");

    }



    public static TextElementName forTextName(final char[] elementNameBuffer, final int elementNameOffset, final int elementNameLen) {
        if (elementNameBuffer == null) { // Note TEXT element names CAN be empty (only element names, not attribute names)
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (elementNameOffset < 0 || elementNameLen < 0) {
            throw new IllegalArgumentException("Both name offset and length must be equal to or greater than zero");
        }
        return (TextElementName) textElementNamesRepository.getElement(elementNameBuffer, elementNameOffset, elementNameLen);
    }

    public static XMLElementName forXMLName(final char[] elementNameBuffer, final int elementNameOffset, final int elementNameLen) {
        if (elementNameBuffer == null || elementNameLen == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (elementNameOffset < 0 || elementNameLen < 0) {
            throw new IllegalArgumentException("Both name offset and length must be equal to or greater than zero");
        }
        return (XMLElementName) xmlElementNamesRepository.getElement(elementNameBuffer, elementNameOffset, elementNameLen);
    }

    public static HTMLElementName forHTMLName(final char[] elementNameBuffer, final int elementNameOffset, final int elementNameLen) {
        if (elementNameBuffer == null || elementNameLen == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (elementNameOffset < 0 || elementNameLen < 0) {
            throw new IllegalArgumentException("Both name offset and length must be equal to or greater than zero");
        }
        return (HTMLElementName) htmlElementNamesRepository.getElement(elementNameBuffer, elementNameOffset, elementNameLen);
    }


    public static TextElementName forTextName(final String elementName) {
        if (elementName == null) { // Note TEXT element names CAN be empty (only element names, not attribute names)
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (TextElementName) textElementNamesRepository.getElement(elementName);
    }

    public static XMLElementName forXMLName(final String elementName) {
        if (elementName == null || elementName.trim().length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (XMLElementName) xmlElementNamesRepository.getElement(elementName);
    }

    public static HTMLElementName forHTMLName(final String elementName) {
        if (elementName == null || elementName.trim().length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (HTMLElementName) htmlElementNamesRepository.getElement(elementName);
    }


    public static TextElementName forTextName(final String prefix, final String elementName) {
        // Note TEXT element names CAN be empty (only element names, not attribute names)
        // However a non-empty prefix plus an empty element is NOT allowed
        if (elementName == null || (elementName.trim().length() == 0 && (prefix != null && prefix.trim().length() > 0))) {
            throw new IllegalArgumentException("Name cannot be null (nor empty if prefix is not empty)");
        }
        return (TextElementName) textElementNamesRepository.getElement(prefix, elementName);
    }

    public static XMLElementName forXMLName(final String prefix, final String elementName) {
        if (elementName == null || elementName.trim().length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (XMLElementName) xmlElementNamesRepository.getElement(prefix, elementName);
    }

    public static HTMLElementName forHTMLName(final String prefix, final String elementName) {
        if (elementName == null || elementName.trim().length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (HTMLElementName) htmlElementNamesRepository.getElement(prefix, elementName);
    }




    
    private ElementNames() {
        super();
    }






    /*
     * This repository class is thread-safe, as it will contain new instances of ElementName created during
     * processing (created when asking the repository for them when they do not exist yet). As any thread can
     * create a new element, this has to be lock-protected.
     */
    static final class ElementNamesRepository {

        private final TemplateMode templateMode;

        private final List<String> repositoryNames;  // read-write, sync will be needed
        private final List<ElementName> repository;  // read-write, sync will be needed

        private final ReadWriteLock lock = new ReentrantReadWriteLock(true);
        private final Lock readLock = this.lock.readLock();
        private final Lock writeLock = this.lock.writeLock();


        ElementNamesRepository(final TemplateMode templateMode) {

            super();

            this.templateMode = templateMode;

            this.repositoryNames = new ArrayList<String>(500);
            this.repository = new ArrayList<ElementName>(500);

        }


        ElementName getElement(final char[] text, final int offset, final int len) {

            int index;

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
                return storeElement(text, offset, len);
            } finally {
                this.writeLock.unlock();
            }

        }


        ElementName getElement(final String completeElementName) {

            int index;

            this.readLock.lock();
            try {

                /*
                 * First look for the element in the namespaced repository
                 */
                index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, completeElementName);

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


        ElementName getElement(final String prefix, final String elementName) {

            int index;

            this.readLock.lock();
            try {

                /*
                 * First look for the element in the namespaced repository
                 */
                index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, prefix, elementName);

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


        private ElementName storeElement(final char[] text, final int offset, final int len) {

            int index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, text, offset, len);
            if (index >= 0) {
                // It was already added while we were waiting for the lock!
                return this.repository.get(index);
            }

            final ElementName name;
            if (this.templateMode == TemplateMode.HTML) {
                name = buildHTMLElementName(text, offset, len);
            } else if (this.templateMode == TemplateMode.XML) {
                name = buildXMLElementName(text, offset, len);
            } else { // this.templateMode.isText()
                name = buildTextElementName(text, offset, len);
            }

            final String[] completeElementNames = name.completeElementNames;

            for (final String completeElementName : completeElementNames) {

                index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, completeElementName);

                // binary Search returned (-(insertion point) - 1)
                this.repositoryNames.add(((index + 1) * -1), completeElementName);
                this.repository.add(((index + 1) * -1), name);

            }

            return name;

        }


        private ElementName storeElement(final String elementName) {

            int index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, elementName);
            if (index >= 0) {
                // It was already added while we were waiting for the lock!
                return this.repository.get(index);
            }

            final ElementName name;
            if (this.templateMode == TemplateMode.HTML) {
                name = buildHTMLElementName(elementName);
            } else if (this.templateMode == TemplateMode.XML) {
                name = buildXMLElementName(elementName);
            } else { // this.templateMode.isText()
                name = buildTextElementName(elementName);
            }

            final String[] completeElementNames = name.completeElementNames;

            for (final String completeElementName : completeElementNames) {

                index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, completeElementName);

                // binary Search returned (-(insertion point) - 1)
                this.repositoryNames.add(((index + 1) * -1), completeElementName);
                this.repository.add(((index + 1) * -1), name);

            }

            return name;

        }


        private ElementName storeElement(final String prefix, final String elementName) {

            int index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, prefix, elementName);
            if (index >= 0) {
                // It was already added while we were waiting for the lock!
                return this.repository.get(index);
            }

            final ElementName name;
            if (this.templateMode == TemplateMode.HTML) {
                name = buildHTMLElementName(prefix, elementName);
            } else if (this.templateMode == TemplateMode.XML) {
                name = buildXMLElementName(prefix, elementName);
            } else { // this.templateMode.isText()
                name = buildTextElementName(prefix, elementName);
            }

            final String[] completeElementNames = name.completeElementNames;

            for (final String completeElementName : completeElementNames) {

                index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, completeElementName);

                // binary Search returned (-(insertion point) - 1)
                this.repositoryNames.add(((index + 1) * -1), completeElementName);
                this.repository.add(((index + 1) * -1), name);

            }

            return name;

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
                                        final List<String> values, final String prefix, final String elementName) {

            // This method will be specialized in finding prefixed element names (in the prefix:name form)

            if (prefix == null || prefix.trim().length() == 0) {
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

                            // Prefix matches and we made sure midVal has a ':', so let's try the elementName
                            cmp = TextUtils.compareTo(caseSensitive, midVal, prefixLen + 1, (midValLen - (prefixLen + 1)), elementName, 0, elementNameLen);

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
