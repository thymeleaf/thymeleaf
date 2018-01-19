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
package org.thymeleaf.standard.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.inline.IInliner;
import org.thymeleaf.inline.NoOpInliner;
import org.thymeleaf.standard.inline.StandardInlineMode;
import org.thymeleaf.standard.inline.StandardTextInliner;
import org.thymeleaf.standard.inline.StandardXMLInliner;
import org.thymeleaf.templatemode.TemplateMode;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class StandardInlineXMLTagProcessor extends AbstractStandardTextInlineSettingTagProcessor {

    public static final int PRECEDENCE = 1000;
    public static final String ATTR_NAME = "inline";




    public StandardInlineXMLTagProcessor(final String dialectPrefix) {
        super(TemplateMode.XML, dialectPrefix, ATTR_NAME, PRECEDENCE);
    }



    @Override
    protected IInliner getInliner(final ITemplateContext context, final StandardInlineMode inlineMode) {

        switch (inlineMode) {
            case NONE:
                return NoOpInliner.INSTANCE;
            case XML:
                return new StandardXMLInliner(context.getConfiguration());
            case TEXT:
                return new StandardTextInliner(context.getConfiguration());
            default:
                throw new TemplateProcessingException(
                        "Invalid inline mode selected: " + inlineMode + ". Allowed inline modes in template mode " +
                        getTemplateMode() + " are: " +
                        "\"" + StandardInlineMode.XML + "\", \"" + StandardInlineMode.TEXT + "\", " +
                        "\"" + StandardInlineMode.NONE + "\"");
        }

    }




}
