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
package org.thymeleaf.templateparser.markup.decoupled;

import java.util.List;

import org.attoparser.AbstractChainedMarkupHandler;
import org.attoparser.IMarkupHandler;
import org.attoparser.ParseException;
import org.attoparser.select.ParseSelection;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class DecoupledTemplateLogicMarkupHandler extends AbstractChainedMarkupHandler {


    // The node-selection markup handler should be the first one in the chain, so that we make sure that any
    // block selection operations (like the ones performed by the block-selection markup handlers) can be
    // performed on attributes injected in a decoupled manner (eg: a "th:fragment"/"th:ref" injected externally)
    private static final int INJECTION_LEVEL = 0;

    private final DecoupledTemplateMetadata decoupledTemplateMetadata;
    private final boolean injectAttributes;

    private ParseSelection parseSelection;



    public DecoupledTemplateLogicMarkupHandler(final DecoupledTemplateMetadata decoupledTemplateMetadata,
                                               final IMarkupHandler handler) {
        super(handler);

        Validate.notNull(decoupledTemplateMetadata, "Decoupled Template Metadata cannot be null");

        this.decoupledTemplateMetadata = decoupledTemplateMetadata;
        this.injectAttributes = this.decoupledTemplateMetadata.hasInjectedAttributes();

    }




    @Override
    public void setParseSelection(final ParseSelection selection) {
        this.parseSelection = selection;
        super.setParseSelection(selection);
    }



    @Override
    public void handleStandaloneElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final boolean minimized, final int line, final int col)
            throws ParseException {

        if (this.injectAttributes) {
            processInjectedAttributes(line, col);
        }

        super.handleStandaloneElementEnd(buffer, nameOffset, nameLen, minimized, line, col);

    }



    @Override
    public void handleOpenElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        if (this.injectAttributes) {
            processInjectedAttributes(line, col);
        }

        super.handleOpenElementEnd(buffer, nameOffset, nameLen, line, col);

    }






    private void processInjectedAttributes(final int line, final int col) throws ParseException {

        if (!this.parseSelection.isMatchingAny(INJECTION_LEVEL)) {
            return;
        }

        final String[] selectors = this.parseSelection.getCurrentSelection(INJECTION_LEVEL);

        if (selectors == null || selectors.length == 0) {
            return;
        }

        for (final String selector : selectors) {

            final List<DecoupledInjectedAttribute> injectedAttributesForSelector =
                    this.decoupledTemplateMetadata.getInjectedAttributesForSelector(selector);

            if (injectedAttributesForSelector == null) {
                continue;
            }

            for (final DecoupledInjectedAttribute injectedAttribute : injectedAttributesForSelector) {

                super.handleAttribute(
                        injectedAttribute.buffer,
                        injectedAttribute.nameOffset, injectedAttribute.nameLen,
                        line, col,
                        injectedAttribute.operatorOffset, injectedAttribute.operatorLen,
                        line, col,
                        injectedAttribute.valueContentOffset, injectedAttribute.valueContentLen,
                        injectedAttribute.valueOuterOffset, injectedAttribute.valueOuterLen,
                        line, col);

            }

        }

    }



}
