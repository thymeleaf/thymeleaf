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

    private static final char[] INNER_WHITE_SPACE = " ".toCharArray();

    private final DecoupledTemplateLogic decoupledTemplateLogic;
    private final boolean injectAttributes;

    private ParseSelection parseSelection;

    private boolean lastWasInnerWhiteSpace = false;



    public DecoupledTemplateLogicMarkupHandler(final DecoupledTemplateLogic decoupledTemplateLogic,
                                               final IMarkupHandler handler) {
        super(handler);

        Validate.notNull(decoupledTemplateLogic, "Decoupled Template Logic cannot be null");

        this.decoupledTemplateLogic = decoupledTemplateLogic;
        this.injectAttributes = this.decoupledTemplateLogic.hasInjectedAttributes();

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



    @Override
    public void handleInnerWhiteSpace(
            final char[] buffer,
            final int offset, final int len,
            final int line, final int col)
            throws ParseException {

        this.lastWasInnerWhiteSpace = true;
        super.handleInnerWhiteSpace(buffer, offset, len, line, col);

    }



    @Override
    public void handleAttribute(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int nameLine, final int nameCol,
            final int operatorOffset, final int operatorLen,
            final int operatorLine, final int operatorCol,
            final int valueContentOffset, final int valueContentLen,
            final int valueOuterOffset, final int valueOuterLen,
            final int valueLine, final int valueCol)
            throws ParseException {

        this.lastWasInnerWhiteSpace = false;
        super.handleAttribute(
                buffer,
                nameOffset, nameLen,
                nameLine, nameCol,
                operatorOffset, operatorLen,
                operatorLine, operatorCol,
                valueContentOffset, valueContentLen,
                valueOuterOffset, valueOuterLen,
                valueLine, valueCol);

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
                    this.decoupledTemplateLogic.getInjectedAttributesForSelector(selector);

            if (injectedAttributesForSelector == null) {
                continue;
            }

            for (final DecoupledInjectedAttribute injectedAttribute : injectedAttributesForSelector) {

                if (!this.lastWasInnerWhiteSpace) {
                    super.handleInnerWhiteSpace(INNER_WHITE_SPACE, 0, 1, line, col);
                }

                super.handleAttribute(
                        injectedAttribute.buffer,
                        injectedAttribute.nameOffset, injectedAttribute.nameLen,
                        line, col,
                        injectedAttribute.operatorOffset, injectedAttribute.operatorLen,
                        line, col,
                        injectedAttribute.valueContentOffset, injectedAttribute.valueContentLen,
                        injectedAttribute.valueOuterOffset, injectedAttribute.valueOuterLen,
                        line, col);

                this.lastWasInnerWhiteSpace = false;

            }

        }

    }



}
