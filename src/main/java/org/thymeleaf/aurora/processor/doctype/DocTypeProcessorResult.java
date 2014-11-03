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
package org.thymeleaf.aurora.processor.doctype;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class DocTypeProcessorResult {

    private final String keyword;
    private final String elementName;
    private final String type;
    private final String publicId;
    private final String systemId;
    private final String internalSubset;


    public DocTypeProcessorResult(
            final String keyword,
            final String elementName,
            final String type,
            final String publicId,
            final String systemId,
            final String internalSubset) {

        super();
        this.keyword = keyword;
        this.elementName = elementName;
        this.type = type;
        this.publicId = publicId;
        this.systemId = systemId;
        this.internalSubset = internalSubset;

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

}
