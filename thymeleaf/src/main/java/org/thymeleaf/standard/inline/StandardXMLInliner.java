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
package org.thymeleaf.standard.inline;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templatemode.TemplateMode;
import org.unbescape.xml.XmlEscape;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class StandardXMLInliner extends AbstractStandardInliner {


    public StandardXMLInliner(final IEngineConfiguration configuration) {
        super(configuration, TemplateMode.XML);
    }


    @Override
    protected String produceEscapedOutput(final Object input) {
        if (input == null) {
            return "";
        }
        // Note we are outputting a body content here, so it is important that we use the version
        // of XML escaping meant for content, not attributes (slight differences)
        return XmlEscape.escapeXml10(input.toString());
    }

}
