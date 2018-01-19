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

import org.thymeleaf.model.IDocType;
import org.thymeleaf.model.IModelVisitor;

/*
 * Engine implementation of IDocType.
 *
 * @author Daniel Fernandez
 * @since 3.0.0
 */
final class DocType extends AbstractTemplateEvent implements IDocType, IEngineTemplateEvent {

    // DOCTYPE nodes do not exist in text parsing, so we are safe expliciting markup structures here
    public static final String DEFAULT_KEYWORD = "DOCTYPE";
    public static final String DEFAULT_ELEMENT_NAME = "html";
    public static final String DEFAULT_TYPE_PUBLIC = "PUBLIC";
    public static final String DEFAULT_TYPE_SYSTEM = "SYSTEM";

    private final String keyword;
    private final String elementName;
    private final String type;
    private final String publicId;
    private final String systemId;
    private final String internalSubset;

    private final String docType;


    DocType() {
        this(null, null);
    }


    DocType(final String publicId, final String systemId) {
        this(DEFAULT_KEYWORD, DEFAULT_ELEMENT_NAME, publicId, systemId, null);
    }


    DocType(
            final String keyword,
            final String elementName,
            final String publicId,
            final String systemId,
            final String internalSubset) {
        super();
        this.keyword = keyword;
        this.elementName = elementName;
        this.type = computeType(publicId, systemId);
        this.publicId = publicId;
        this.systemId = systemId;
        this.internalSubset = internalSubset;
        this.docType = computeDocType();
    }


    DocType(
            final String docType,
            final String keyword,
            final String elementName,
            final String publicId,
            final String systemId,
            final String internalSubset,
            final String templateName, final int line, final int col) {
        super(templateName, line, col);
        this.keyword = keyword;
        this.elementName = elementName;
        this.type = computeType(publicId, systemId);
        this.publicId = publicId;
        this.systemId = systemId;
        this.internalSubset = internalSubset;
        this.docType = (docType != null? docType : computeDocType());
    }



    public String getKeyword() {
        return this.keyword;
    }

    public String getElementName() {
        return this.elementName;
    }

    public String getType() {
        return this.type;
    }

    public String getPublicId() {
        return this.publicId;
    }

    public String getSystemId() {
        return this.systemId;
    }

    public String getInternalSubset() {
        return this.internalSubset;
    }

    public String getDocType() {
        return this.docType;
    }




    private String computeDocType() {

        final StringBuilder strBuilder = new StringBuilder(120);

        strBuilder.append("<!");
        strBuilder.append(this.keyword);
        strBuilder.append(' ');
        strBuilder.append(this.elementName);
        if (this.type != null) {
            strBuilder.append(' ');
            strBuilder.append(type);
            if (this.publicId != null) {
                strBuilder.append(" \"");
                strBuilder.append(this.publicId);
                strBuilder.append('"');
            }
            strBuilder.append(" \"");
            strBuilder.append(this.systemId);
            strBuilder.append('"');
        }
        if (this.internalSubset != null) {
            strBuilder.append(" [");
            strBuilder.append(this.internalSubset);
            strBuilder.append(']');
        }
        strBuilder.append('>');

        return strBuilder.toString();

    }




    private static String computeType(final String publicId, final String systemId) {

        if (publicId != null && systemId == null) {
            throw new IllegalArgumentException(
                    "DOCTYPE clause cannot have a non-null PUBLIC ID and a null SYSTEM ID");
        }

        if (publicId == null && systemId == null) {
            return null;
        }

        if (publicId != null) {
            return DEFAULT_TYPE_PUBLIC;
        }

        return DEFAULT_TYPE_SYSTEM;

    }




    public void accept(final IModelVisitor visitor) {
        visitor.visit(this);
    }


    public void write(final Writer writer) throws IOException {
        writer.write(this.docType);
    }




    // Meant to be called only from within the engine
    static DocType asEngineDocType(final IDocType docType) {

        if (docType instanceof DocType) {
            return (DocType) docType;
        }

        return new DocType(
                null,
                docType.getKeyword(),
                docType.getElementName(),
                docType.getPublicId(),
                docType.getSystemId(),
                docType.getInternalSubset(),
                docType.getTemplateName(), docType.getLine(), docType.getCol());

    }




    @Override
    public void beHandled(final ITemplateHandler handler) {
        handler.handleDocType(this);
    }




    @Override
    public String toString() {
        return getDocType();
    }


}
