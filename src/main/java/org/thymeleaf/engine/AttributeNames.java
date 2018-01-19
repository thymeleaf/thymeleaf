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
public class AttributeNames {


    // We need a different repository for each type of name
    private static final AttributeNamesRepository htmlAttributeNamesRepository = new AttributeNamesRepository(TemplateMode.HTML);
    private static final AttributeNamesRepository xmlAttributeNamesRepository = new AttributeNamesRepository(TemplateMode.XML);
    private static final AttributeNamesRepository textAttributeNamesRepository = new AttributeNamesRepository(TemplateMode.TEXT);





    private static TextAttributeName buildTextAttributeName(final char[] attributeNameBuffer, final int attributeNameOffset, final int attributeNameLen) {

        if (attributeNameBuffer == null || attributeNameLen == 0) {
            throw new IllegalArgumentException("Attribute name buffer cannot be null or empty");
        }
        if (attributeNameOffset < 0 || attributeNameLen < 0) {
            throw new IllegalArgumentException("Attribute name offset and len must be equal or greater than zero");
        }


        char c;
        int i = attributeNameOffset;
        int n = attributeNameLen;
        while (n-- != 0) {

            c = attributeNameBuffer[i++];
            if (c != ':') {
                continue;
            }

            if (c == ':') {
                if (i == attributeNameOffset + 1){
                    // ':' was the first char, no prefix there
                    return TextAttributeName.forName(null, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));
                }

                return TextAttributeName.forName(
                        new String(attributeNameBuffer, attributeNameOffset, (i - (attributeNameOffset + 1))),
                        new String(attributeNameBuffer, i, (attributeNameOffset + attributeNameLen) - i));
            }

        }

        return TextAttributeName.forName(null, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));

    }



    private static XMLAttributeName buildXMLAttributeName(final char[] attributeNameBuffer, final int attributeNameOffset, final int attributeNameLen) {

        if (attributeNameBuffer == null || attributeNameLen == 0) {
            throw new IllegalArgumentException("Attribute name buffer cannot be null or empty");
        }
        if (attributeNameOffset < 0 || attributeNameLen < 0) {
            throw new IllegalArgumentException("Attribute name offset and len must be equal or greater than zero");
        }

        char c;
        int i = attributeNameOffset;
        int n = attributeNameLen;
        while (n-- != 0) {

            c = attributeNameBuffer[i++];
            if (c != ':') {
                continue;
            }

            if (c == ':') {
                if (i == attributeNameOffset + 1){
                    // ':' was the first char, no prefix there
                    return XMLAttributeName.forName(null, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));
                }

                return XMLAttributeName.forName(
                        new String(attributeNameBuffer, attributeNameOffset, (i - (attributeNameOffset + 1))),
                        new String(attributeNameBuffer, i, (attributeNameOffset + attributeNameLen) - i));
            }

        }

        return XMLAttributeName.forName(null, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));

    }



    private static HTMLAttributeName buildHTMLAttributeName(final char[] attributeNameBuffer, final int attributeNameOffset, final int attributeNameLen) {

        if (attributeNameBuffer == null || attributeNameLen == 0) {
            throw new IllegalArgumentException("Attribute name buffer cannot be null or empty");
        }
        if (attributeNameOffset < 0 || attributeNameLen < 0) {
            throw new IllegalArgumentException("Attribute name offset and len must be equal or greater than zero");
        }

        char c;
        int i = attributeNameOffset;
        int n = attributeNameLen;
        boolean inData = false;
        while (n-- != 0) {

            c = attributeNameBuffer[i++];
            if (c != ':' && c != '-') {
                continue;
            }

            if (!inData && c == ':') {
                if (i == attributeNameOffset + 1){
                    // ':' was the first char, no prefix there
                    return HTMLAttributeName.forName(null, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));
                }

                if (TextUtils.equals(TemplateMode.HTML.isCaseSensitive(), "xml:", 0, 4, attributeNameBuffer, attributeNameOffset, (i - attributeNameOffset)) ||
                        TextUtils.equals(TemplateMode.HTML.isCaseSensitive(), "xmlns:", 0, 6, attributeNameBuffer, attributeNameOffset, (i - attributeNameOffset))) {
                    // 'xml' and 'xmlns' are not a valid dialect prefix in HTML mode
                    return HTMLAttributeName.forName(null, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));
                }

                return HTMLAttributeName.forName(
                        new String(attributeNameBuffer, attributeNameOffset, (i - (attributeNameOffset + 1))),
                        new String(attributeNameBuffer, i, (attributeNameOffset + attributeNameLen) - i));
            }

            if (!inData && c == '-') {
                if (i == attributeNameOffset + 5 && TextUtils.equals(TemplateMode.HTML.isCaseSensitive(), "data", 0, 4, attributeNameBuffer, attributeNameOffset, (i - (attributeNameOffset + 1)))) {
                    inData = true;
                    continue;
                } else {
                    // this is just a normal, non-thymeleaf 'data-*' attribute
                    return HTMLAttributeName.forName(null, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));
                }
            }

            if (inData && c == '-') {
                if (i == attributeNameOffset + 6) {
                    // '-' was the first char after 'data-', no prefix there
                    return HTMLAttributeName.forName(null, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));

                }
                return HTMLAttributeName.forName(
                        new String(attributeNameBuffer, attributeNameOffset + 5, (i - (attributeNameOffset + 6))),
                        new String(attributeNameBuffer, i, (attributeNameOffset + attributeNameLen) - i));
            }

        }

        return HTMLAttributeName.forName(null, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));

    }



    private static TextAttributeName buildTextAttributeName(final String attributeName) {

        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Attribute name cannot be null or empty");
        }

        char c;
        int i = 0;
        int n = attributeName.length();
        while (n-- != 0) {

            c = attributeName.charAt(i++);
            if (c != ':') {
                continue;
            }

            if (c == ':') {
                if (i == 1){
                    // ':' was the first char, no prefix there
                    return TextAttributeName.forName(null, attributeName);
                }

                return TextAttributeName.forName(
                        attributeName.substring(0, i - 1),
                        attributeName.substring(i, attributeName.length()));
            }

        }

        return TextAttributeName.forName(null, attributeName);

    }



    private static XMLAttributeName buildXMLAttributeName(final String attributeName) {

        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Attribute name cannot be null or empty");
        }


        char c;
        int i = 0;
        int n = attributeName.length();
        while (n-- != 0) {

            c = attributeName.charAt(i++);
            if (c != ':') {
                continue;
            }

            if (c == ':') {
                if (i == 1){
                    // ':' was the first char, no prefix there
                    return XMLAttributeName.forName(null, attributeName);
                }

                return XMLAttributeName.forName(
                        attributeName.substring(0, i - 1),
                        attributeName.substring(i, attributeName.length()));
            }

        }

        return XMLAttributeName.forName(null, attributeName);

    }



    private static HTMLAttributeName buildHTMLAttributeName(final String attributeName) {

        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Attribute name cannot be null or empty");
        }

        char c;
        int i = 0;
        int n = attributeName.length();
        boolean inData = false;
        while (n-- != 0) {

            c = attributeName.charAt(i++);
            if (c != ':' && c != '-') {
                continue;
            }

            if (!inData && c == ':') {
                if (i == 1){
                    // ':' was the first char, no prefix there
                    return HTMLAttributeName.forName(null, attributeName);
                }

                if (TextUtils.equals(TemplateMode.HTML.isCaseSensitive(), "xml:", 0, 4, attributeName, 0, i) ||
                        TextUtils.equals(TemplateMode.HTML.isCaseSensitive(), "xmlns:", 0, 6, attributeName, 0, i)) {
                    // 'xml' is not a valid dialect prefix in HTML mode
                    return HTMLAttributeName.forName(null, attributeName);
                }

                return HTMLAttributeName.forName(
                        attributeName.substring(0, i - 1),
                        attributeName.substring(i,attributeName.length()));
            }

            if (!inData && c == '-') {
                if (i == 5 && TextUtils.equals(TemplateMode.HTML.isCaseSensitive(), "data", 0, 4, attributeName, 0, 4)) {
                    inData = true;
                    continue;
                } else {
                    // this is just a normal, non-thymeleaf 'data-*' attribute
                    return HTMLAttributeName.forName(null, attributeName);
                }
            }

            if (inData && c == '-') {
                if (i == 6) {
                    // '-' was the first char after 'data-', no prefix there
                    return HTMLAttributeName.forName(null, attributeName);

                }
                return HTMLAttributeName.forName(
                        attributeName.substring(5, i - 1),
                        attributeName.substring(i, attributeName.length()));
            }

        }

        return HTMLAttributeName.forName(null, attributeName);

    }



    private static TextAttributeName buildTextAttributeName(final String prefix, final String attributeName) {
        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Attribute name cannot be null or empty");
        }
        if (prefix == null || prefix.trim().length() == 0) {
            return buildTextAttributeName(attributeName);
        }
        return TextAttributeName.forName(prefix, attributeName);
    }



    private static XMLAttributeName buildXMLAttributeName(final String prefix, final String attributeName) {
        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Attribute name cannot be null or empty");
        }
        if (prefix == null || prefix.trim().length() == 0) {
            return buildXMLAttributeName(attributeName);
        }
        return XMLAttributeName.forName(prefix, attributeName);
    }



    private static HTMLAttributeName buildHTMLAttributeName(final String prefix, final String attributeName) {
        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Attribute name cannot be null or empty");
        }
        if (prefix == null || prefix.trim().length() == 0) {
            return buildHTMLAttributeName(attributeName);
        }
        return HTMLAttributeName.forName(prefix, attributeName);
    }




    public static AttributeName forName(
            final TemplateMode templateMode, final char[] attributeNameBuffer, final int attributeNameOffset, final int attributeNameLen) {

        if (templateMode == null) {
            throw new IllegalArgumentException("Template Mode cannot be null");
        }

        if (templateMode == TemplateMode.HTML) {
            return forHTMLName(attributeNameBuffer, attributeNameOffset, attributeNameLen);
        }

        if (templateMode == TemplateMode.XML) {
            return forXMLName(attributeNameBuffer, attributeNameOffset, attributeNameLen);
        }

        if (templateMode.isText()) {
            return forTextName(attributeNameBuffer, attributeNameOffset, attributeNameLen);
        }

        throw new IllegalArgumentException("Unknown template mode '" + templateMode + "'");

    }

    public static AttributeName forName(final TemplateMode templateMode, final String attributeName) {

        if (templateMode == null) {
            throw new IllegalArgumentException("Template Mode cannot be null");
        }

        if (templateMode == TemplateMode.HTML) {
            return forHTMLName(attributeName);
        }

        if (templateMode == TemplateMode.XML) {
            return forXMLName(attributeName);
        }

        if (templateMode.isText()) {
            return forTextName(attributeName);
        }

        throw new IllegalArgumentException("Unknown template mode '" + templateMode + "'");

    }

    public static AttributeName forName(final TemplateMode templateMode, final String prefix, final String attributeName) {

        if (templateMode == null) {
            throw new IllegalArgumentException("Template Mode cannot be null");
        }

        if (templateMode == TemplateMode.HTML) {
            return forHTMLName(prefix, attributeName);
        }

        if (templateMode == TemplateMode.XML) {
            return forXMLName(prefix, attributeName);
        }

        if (templateMode.isText()) {
            return forTextName(prefix, attributeName);
        }

        throw new IllegalArgumentException("Unknown template mode '" + templateMode + "'");

    }


    public static TextAttributeName forTextName(final char[] attributeNameBuffer, final int attributeNameOffset, final int attributeNameLen) {
        if (attributeNameBuffer == null || attributeNameLen == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (attributeNameOffset < 0 || attributeNameLen < 0) {
            throw new IllegalArgumentException("Both name offset and length must be equal to or greater than zero");
        }
        return (TextAttributeName) textAttributeNamesRepository.getAttribute(attributeNameBuffer, attributeNameOffset, attributeNameLen);
    }

    public static XMLAttributeName forXMLName(final char[] attributeNameBuffer, final int attributeNameOffset, final int attributeNameLen) {
        if (attributeNameBuffer == null || attributeNameLen == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (attributeNameOffset < 0 || attributeNameLen < 0) {
            throw new IllegalArgumentException("Both name offset and length must be equal to or greater than zero");
        }
        return (XMLAttributeName) xmlAttributeNamesRepository.getAttribute(attributeNameBuffer, attributeNameOffset, attributeNameLen);
    }

    public static HTMLAttributeName forHTMLName(final char[] attributeNameBuffer, final int attributeNameOffset, final int attributeNameLen) {
        if (attributeNameBuffer == null || attributeNameLen == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (attributeNameOffset < 0 || attributeNameLen < 0) {
            throw new IllegalArgumentException("Both name offset and length must be equal to or greater than zero");
        }
        return (HTMLAttributeName) htmlAttributeNamesRepository.getAttribute(attributeNameBuffer, attributeNameOffset, attributeNameLen);
    }


    public static TextAttributeName forTextName(final String attributeName) {
        if (attributeName == null || attributeName.trim().length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (TextAttributeName) textAttributeNamesRepository.getAttribute(attributeName);
    }

    public static XMLAttributeName forXMLName(final String attributeName) {
        if (attributeName == null || attributeName.trim().length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (XMLAttributeName) xmlAttributeNamesRepository.getAttribute(attributeName);
    }

    public static HTMLAttributeName forHTMLName(final String attributeName) {
        if (attributeName == null || attributeName.trim().length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (HTMLAttributeName) htmlAttributeNamesRepository.getAttribute(attributeName);
    }


    public static TextAttributeName forTextName(final String prefix, final String attributeName) {
        if (attributeName == null || attributeName.trim().length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (TextAttributeName) textAttributeNamesRepository.getAttribute(prefix, attributeName);
    }

    public static XMLAttributeName forXMLName(final String prefix, final String attributeName) {
        if (attributeName == null || attributeName.trim().length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (XMLAttributeName) xmlAttributeNamesRepository.getAttribute(prefix, attributeName);
    }

    public static HTMLAttributeName forHTMLName(final String prefix, final String attributeName) {
            if (attributeName == null || attributeName.trim().length() == 0) {
                throw new IllegalArgumentException("Name cannot be null or empty");
            }
            return (HTMLAttributeName) htmlAttributeNamesRepository.getAttribute(prefix, attributeName);
    }





    private AttributeNames() {
        super();
    }






    /*
     * This repository class is thread-safe, as it will contain new instances of AttributeName created during
     * processing (created when asking the repository for them when they do not exist yet). As any thread can
     * create a new attribute, this has to be lock-protected.
     */
    static final class AttributeNamesRepository {

        private final TemplateMode templateMode;

        private final List<String> repositoryNames;  // read-write, sync will be needed
        private final List<AttributeName> repository;  // read-write, sync will be needed

        private final ReadWriteLock lock = new ReentrantReadWriteLock(true);
        private final Lock readLock = this.lock.readLock();
        private final Lock writeLock = this.lock.writeLock();


        AttributeNamesRepository(final TemplateMode templateMode) {

            super();

            this.templateMode = templateMode;

            this.repositoryNames = new ArrayList<String>(500);
            this.repository = new ArrayList<AttributeName>(500);

        }


        AttributeName getAttribute(final char[] text, final int offset, final int len) {

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
                return storeAttribute(text, offset, len);
            } finally {
                this.writeLock.unlock();
            }

        }


        AttributeName getAttribute(final String completeAttributeName) {

            int index;

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


        AttributeName getAttribute(final String prefix, final String attributeName) {

            int index;

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


        private AttributeName storeAttribute(final char[] text, final int offset, final int len) {

            int index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, text, offset, len);
            if (index >= 0) {
                // It was already added while we were waiting for the lock!
                return this.repository.get(index);
            }

            final AttributeName name;
            if (this.templateMode == TemplateMode.HTML) {
                name = buildHTMLAttributeName(text, offset, len);
            } else if (this.templateMode == TemplateMode.XML) {
                name = buildXMLAttributeName(text, offset, len);
            } else { // this.templateMode.isText()
                name = buildTextAttributeName(text, offset, len);
            }

            final String[] completeAttributeNames = name.completeAttributeNames;

            for (final String completeAttributeName : completeAttributeNames) {

                index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, completeAttributeName);

                // binary Search returned (-(insertion point) - 1)
                this.repositoryNames.add(((index + 1) * -1), completeAttributeName);
                this.repository.add(((index + 1) * -1), name);

            }

            return name;

        }


        private AttributeName storeAttribute(final String attributeName) {

            int index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, attributeName);
            if (index >= 0) {
                // It was already added while we were waiting for the lock!
                return this.repository.get(index);
            }

            final AttributeName name;
            if (this.templateMode == TemplateMode.HTML) {
                name = buildHTMLAttributeName(attributeName);
            } else if (this.templateMode == TemplateMode.XML) {
                name = buildXMLAttributeName(attributeName);
            } else { // this.templateMode.isText()
                name = buildTextAttributeName(attributeName);
            }

            final String[] completeAttributeNames = name.completeAttributeNames;

            for (final String completeAttributeName : completeAttributeNames) {

                index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, completeAttributeName);

                // binary Search returned (-(insertion point) - 1)
                this.repositoryNames.add(((index + 1) * -1), completeAttributeName);
                this.repository.add(((index + 1) * -1), name);

            }

            return name;

        }


        private AttributeName storeAttribute(final String prefix, final String attributeName) {

            int index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, prefix, attributeName);
            if (index >= 0) {
                // It was already added while we were waiting for the lock!
                return this.repository.get(index);
            }

            final AttributeName name;
            if (this.templateMode == TemplateMode.HTML) {
                name = buildHTMLAttributeName(prefix, attributeName);
            } else if (this.templateMode == TemplateMode.XML) {
                name = buildXMLAttributeName(prefix, attributeName);
            } else { // this.templateMode.isText()
                name = buildTextAttributeName(prefix, attributeName);
            }

            final String[] completeAttributeNames = name.completeAttributeNames;

            for (final String completeAttributeName : completeAttributeNames) {

                index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, completeAttributeName);

                // binary Search returned (-(insertion point) - 1)
                this.repositoryNames.add(((index + 1) * -1), completeAttributeName);
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
