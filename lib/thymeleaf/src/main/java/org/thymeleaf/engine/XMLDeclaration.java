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

import java.io.IOException;
import java.io.Writer;

import org.thymeleaf.model.IModelVisitor;
import org.thymeleaf.model.IXMLDeclaration;

/*
 * Engine implementation of IXMLDeclaration.
 *
 * @author Daniel Fernandez
 * @since 3.0.0
 */
final class XMLDeclaration extends AbstractTemplateEvent implements IXMLDeclaration, IEngineTemplateEvent {

    // XML Declaration nodes do not exist in text parsing, so we are safe expliciting markup structures here
    public static final String DEFAULT_KEYWORD = "xml";
    public static final String DEFAULT_VERSION = "1.0";

    public static final String ATTRIBUTE_NAME_VERSION = "version";
    public static final String ATTRIBUTE_NAME_ENCODING = "encoding";
    public static final String ATTRIBUTE_NAME_STANDALONE = "standalone";

    private final String keyword;
    private final String version;
    private final String encoding;
    private final String standalone;

    private final String xmlDeclaration;



    XMLDeclaration(final String encoding) {
        this(DEFAULT_KEYWORD, DEFAULT_VERSION, encoding, null);
    }


    XMLDeclaration(
            final String keyword,
            final String version,
            final String encoding,
            final String standalone) {
        super();
        this.keyword = keyword;
        this.version = version;
        this.encoding = encoding;
        this.standalone = standalone;
        this.xmlDeclaration = computeXmlDeclaration();
    }


    XMLDeclaration(
            final String xmlDeclaration,
            final String keyword,
            final String version,
            final String encoding,
            final String standalone,
            final String templateName, final int line, final int col) {
        super(templateName, line, col);
        this.keyword = keyword;
        this.version = version;
        this.encoding = encoding;
        this.standalone = standalone;
        this.xmlDeclaration = (xmlDeclaration != null? xmlDeclaration : computeXmlDeclaration());
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
        return this.xmlDeclaration;
    }




    private String computeXmlDeclaration() {

        final StringBuilder strBuilder = new StringBuilder(40);

        strBuilder.append("<?");
        strBuilder.append(this.keyword);
        if (this.version != null) {
            strBuilder.append(' ');
            strBuilder.append(XMLDeclaration.ATTRIBUTE_NAME_VERSION);
            strBuilder.append("=\"");
            strBuilder.append(this.version);
            strBuilder.append('"');
        }
        if (this.encoding != null) {
            strBuilder.append(' ');
            strBuilder.append(XMLDeclaration.ATTRIBUTE_NAME_ENCODING);
            strBuilder.append("=\"");
            strBuilder.append(this.encoding);
            strBuilder.append('"');
        }
        if (this.standalone != null) {
            strBuilder.append(' ');
            strBuilder.append(XMLDeclaration.ATTRIBUTE_NAME_STANDALONE);
            strBuilder.append("=\"");
            strBuilder.append(this.standalone);
            strBuilder.append('"');
        }
        strBuilder.append("?>");

        return strBuilder.toString();

    }




    public void accept(final IModelVisitor visitor) {
        visitor.visit(this);
    }


    public void write(final Writer writer) throws IOException {
        writer.write(this.xmlDeclaration);
    }




    static XMLDeclaration asEngineXMLDeclaration(final IXMLDeclaration xmlDeclaration) {

        if (xmlDeclaration instanceof XMLDeclaration) {
            return (XMLDeclaration) xmlDeclaration;
        }

        return new XMLDeclaration(
                null,
                xmlDeclaration.getKeyword(),
                xmlDeclaration.getVersion(),
                xmlDeclaration.getEncoding(),
                xmlDeclaration.getStandalone(),
                xmlDeclaration.getTemplateName(), xmlDeclaration.getLine(), xmlDeclaration.getCol());

    }




    @Override
    public void beHandled(final ITemplateHandler handler) {
        handler.handleXMLDeclaration(this);
    }




    @Override
    public String toString() {
        return getXmlDeclaration();
    }

}
