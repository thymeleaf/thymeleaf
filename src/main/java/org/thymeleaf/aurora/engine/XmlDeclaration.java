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

import org.thymeleaf.aurora.util.TextUtil;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class XmlDeclaration {

    public static final String DEFAULT_KEYWORD = "xml";

    private static final String ATTRIBUTE_NAME_VERSION = "version";
    private static final String ATTRIBUTE_NAME_ENCODING = "encoding";
    private static final String ATTRIBUTE_NAME_STANDALONE = "standalone";

    private String xmlDeclaration;
    private String keyword;
    private String version;
    private String encoding;
    private String standalone;


    /*
     * Objects of this class are meant to both be reused by the engine and also created fresh by the processors. This
     * should allow reducing the number of instances of this class to the minimum.
     *
     * The 'doctype' property, which is computed from the other properties, should be computed lazily in order to avoid
     * unnecessary creation of Strings which would use more memory than needed.
     */


    // Meant to be called only from within the engine
    XmlDeclaration() {

        super();

        this.keyword = null;
        this.version = null;
        this.encoding = null;
        this.standalone = null;

        this.xmlDeclaration = null;

    }



    public XmlDeclaration(
            final String version,
            final String encoding,
            final String standalone) {
        super();
        initializeFromXmlDeclaration(DEFAULT_KEYWORD, version, encoding, standalone);
    }




    public String getKeyword() {
        return this.keyword;
    }

    public String getVersion() {
        return this.version;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public String getStandalone() {
        return this.standalone;
    }




    public String getXmlDeclaration() {

        if (this.keyword == null) {
            // Should never happen, but just in case
            return null;
        }

        if (this.xmlDeclaration == null) {

            final StringBuilder strBuilder = new StringBuilder(70);
            strBuilder.append("<?");
            strBuilder.append(this.keyword);
            if (this.version != null) {
                strBuilder.append(' ');
                strBuilder.append(ATTRIBUTE_NAME_VERSION);
                strBuilder.append("=\"");
                strBuilder.append(this.version);
                strBuilder.append('"');
            }
            if (this.encoding != null) {
                strBuilder.append(' ');
                strBuilder.append(ATTRIBUTE_NAME_ENCODING);
                strBuilder.append("=\"");
                strBuilder.append(this.encoding);
                strBuilder.append('"');
            }
            if (this.standalone != null) {
                strBuilder.append(' ');
                strBuilder.append(ATTRIBUTE_NAME_STANDALONE);
                strBuilder.append("=\"");
                strBuilder.append(this.standalone);
                strBuilder.append('"');
            }
            strBuilder.append("?>");

            this.xmlDeclaration = strBuilder.toString();

        }

        return this.xmlDeclaration;

    }




    public void setVersion(final String version) {
        initializeFromXmlDeclaration(this.keyword, version, this.encoding, this.standalone);
    }

    public void setEncoding(final String encoding) {
        initializeFromXmlDeclaration(this.keyword, this.version, encoding, this.standalone);
    }

    public void setStandalone(final String standalone) {
        initializeFromXmlDeclaration(this.keyword, this.version, this.encoding, standalone);
    }




    // Meant to be called only from within the engine - removes the need to validate compute the 'xmlDeclaration' field
    void setXmlDeclaration(
            final String xmlDeclaration,
            final String keyword,
            final String version,
            final String encoding,
            final String standalone) {

        this.keyword = keyword;
        this.version = version;
        this.encoding = encoding;
        this.standalone = standalone;

        this.xmlDeclaration = xmlDeclaration;

    }



    private void initializeFromXmlDeclaration(
            final String keyword,
            final String version,
            final String encoding,
            final String standalone) {

        if (keyword == null || !TextUtil.equals(true, DEFAULT_KEYWORD, keyword)) {
            throw new IllegalArgumentException("XML Declaration keyword must be non-null and equal to '" + DEFAULT_KEYWORD + "'");
        }

        this.keyword = keyword;
        this.version = version;
        this.encoding = encoding;
        this.standalone = standalone;

        this.xmlDeclaration = null;

    }


}
