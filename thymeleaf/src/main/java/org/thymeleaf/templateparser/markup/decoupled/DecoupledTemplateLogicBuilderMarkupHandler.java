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

import java.util.ArrayList;
import java.util.List;

import org.attoparser.AbstractMarkupHandler;
import org.attoparser.ParseException;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.TextUtils;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Implementation of {@link org.attoparser.IMarkupHandler} used for building and populating instances of
 *   {@link DecoupledTemplateLogic} as a result of parsing a decoupled template logic resource.
 * </p>
 * <p>
 *   Once built and populated, instances of {@link DecoupledTemplateLogic} are handled over to
 *   {@link org.thymeleaf.engine.TemplateHandlerAdapterMarkupHandler} instances which are one of the steps in
 *   the template parsing chain (converting parser events into {@link org.thymeleaf.engine.ITemplateHandler} events).
 *   Attributes specified here to be injected into the template are injected at real-time during the parsing operation
 *   itself, so that overhead is minimal (and zero once the template is cached).
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class DecoupledTemplateLogicBuilderMarkupHandler extends AbstractMarkupHandler {

    public static final String TAG_NAME_LOGIC = "thlogic";
    public static final String TAG_NAME_ATTR = "attr";
    public static final String ATTRIBUTE_NAME_SEL = "sel";

    private static final char[] TAG_NAME_LOGIC_CHARS = TAG_NAME_LOGIC.toCharArray();
    private static final char[] TAG_NAME_ATTR_CHARS = TAG_NAME_ATTR.toCharArray();
    private static final char[] ATTRIBUTE_NAME_SEL_CHARS = ATTRIBUTE_NAME_SEL.toCharArray();

    private final String templateName;
    private final TemplateMode templateMode;
    private final DecoupledTemplateLogic decoupledTemplateLogic;

    private boolean inLogicBody = false;
    private boolean inAttrTag = false;
    private Selector selector = new Selector();
    private List<DecoupledInjectedAttribute> currentInjectedAttributes = new ArrayList<DecoupledInjectedAttribute>(8);




    public DecoupledTemplateLogicBuilderMarkupHandler(final String templateName,
                                                      final TemplateMode templateMode) {
        super();

        Validate.notEmpty(templateName, "Template name cannot be null or empty");
        Validate.notNull(templateMode, "Template mode cannot be null");

        this.templateName = templateName;
        this.templateMode = templateMode;
        this.decoupledTemplateLogic = new DecoupledTemplateLogic();

    }



    public DecoupledTemplateLogic getDecoupledTemplateLogic() {
        return this.decoupledTemplateLogic;
    }



    @Override
    public void handleStandaloneElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final boolean minimized, final int line, final int col)
            throws ParseException {

        if (!this.inLogicBody) {
            // A standalone element that is not inside a <logic> tag makes no sense
            return;
        }

        if (!TextUtils.equals(this.templateMode.isCaseSensitive(), buffer, nameOffset, nameLen, TAG_NAME_ATTR_CHARS, 0, TAG_NAME_ATTR_CHARS.length)) {
            // This is not an <attr> tag, so just ignore
            return;
        }

        this.selector.increaseLevel();
        this.inAttrTag = true;
        this.currentInjectedAttributes.clear();

    }

    @Override
    public void handleStandaloneElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final boolean minimized, final int line, final int col)
            throws ParseException {

        if (!this.inLogicBody) {
            // A standalone element that is not inside a <logic> tag makes no sense
            return;
        }

        if (this.inAttrTag && this.selector.isLevelEmpty()) {
            throw new TemplateInputException(
                    "Error while processing decoupled logic file: <attr> injection tag does not contain any " +
                    "\"sel\" selector attributes.", this.templateName, line, col);
        }

        // Time to add the attributes to the decoupled logic. We do it here in order to allow the "sel" attribute to
        // be in any position inside the <attr> tag (even after the injected attributes themselves).
        final String currentSelector = this.selector.getCurrentSelector();
        for (final DecoupledInjectedAttribute injectedAttribute : this.currentInjectedAttributes) {
            this.decoupledTemplateLogic.addInjectedAttribute(currentSelector, injectedAttribute);
        }

        this.currentInjectedAttributes.clear();
        this.inAttrTag = false;
        this.selector.decreaseLevel();


    }



    @Override
    public void handleOpenElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        if (!this.inLogicBody) {
            if (TextUtils.equals(this.templateMode.isCaseSensitive(), buffer, nameOffset, nameLen, TAG_NAME_LOGIC_CHARS, 0, TAG_NAME_LOGIC_CHARS.length)) {
                this.inLogicBody = true;
            }
            return;
        }

        if (!TextUtils.equals(this.templateMode.isCaseSensitive(), buffer, nameOffset, nameLen, TAG_NAME_ATTR_CHARS, 0, TAG_NAME_ATTR_CHARS.length)) {
            // This is not an <attr> tag, so just ignore
            return;
        }

        this.selector.increaseLevel();
        this.inAttrTag = true;
        this.currentInjectedAttributes.clear();

    }



    @Override
    public void handleOpenElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        if (!this.inLogicBody) {
            // A standalone element that is not inside a <logic> tag makes no sense
            return;
        }

        if (this.inAttrTag && this.selector.isLevelEmpty()) {
            throw new TemplateInputException(
                    "Error while processing decoupled logic file: <attr> injection tag does not contain any " +
                    "\"sel\" selector attributes.", this.templateName, line, col);
        }

        // Time to add the attributes to the decoupled logic. We do it here in order to allow the "sel" attribute to
        // be in any position inside the <attr> tag (even after the injected attributes themselves).
        final String currentSelector = this.selector.getCurrentSelector();
        for (final DecoupledInjectedAttribute injectedAttribute : this.currentInjectedAttributes) {
            this.decoupledTemplateLogic.addInjectedAttribute(currentSelector, injectedAttribute);
        }

        this.currentInjectedAttributes.clear();
        this.inAttrTag = false;

    }



    @Override
    public void handleCloseElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        if (!this.inLogicBody) {
            // A standalone element that is not inside a <logic> tag makes no sense
            return;
        }

        if (TextUtils.equals(this.templateMode.isCaseSensitive(), buffer, nameOffset, nameLen, TAG_NAME_LOGIC_CHARS, 0, TAG_NAME_LOGIC_CHARS.length)) {
            this.inLogicBody = false;
            return;
        }

        if (TextUtils.equals(this.templateMode.isCaseSensitive(), buffer, nameOffset, nameLen, TAG_NAME_ATTR_CHARS, 0, TAG_NAME_ATTR_CHARS.length)) {
            this.selector.decreaseLevel();
            return;
        }

    }



    @Override
    public void handleAutoCloseElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        if (!this.inLogicBody) {
            // A standalone element that is not inside a <logic> tag makes no sense
            return;
        }

        if (TextUtils.equals(this.templateMode.isCaseSensitive(), buffer, nameOffset, nameLen, TAG_NAME_LOGIC_CHARS, 0, TAG_NAME_LOGIC_CHARS.length)) {
            this.inLogicBody = false;
            return;
        }

        if (TextUtils.equals(this.templateMode.isCaseSensitive(), buffer, nameOffset, nameLen, TAG_NAME_ATTR_CHARS, 0, TAG_NAME_ATTR_CHARS.length)) {
            this.selector.decreaseLevel();
            return;
        }

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


        if (!this.inAttrTag) {
            // Just ignore, we don't know what is this
            return;
        }

        // Check for the "sel" attribute
        if (TextUtils.equals(this.templateMode.isCaseSensitive(), buffer, nameOffset, nameLen, ATTRIBUTE_NAME_SEL_CHARS, 0, ATTRIBUTE_NAME_SEL_CHARS.length)) {

            if (!this.selector.isLevelEmpty()) {
                throw new TemplateInputException(
                        "Error while processing decoupled logic file: selector (\"sel\") attribute found more than " +
                        "once in attr injection tag", this.templateName, nameLine, nameCol);
            }

            this.selector.setSelector(new String(buffer, valueContentOffset, valueContentLen));
            return;

        }


        /*
         * We know this is not the selector attribute, so we will just consider this attribute, whichever it is, as
         * an attribute to be injected into the template being parsed.
         */

        final DecoupledInjectedAttribute injectedAttribute =
                DecoupledInjectedAttribute.createAttribute(
                        buffer,
                        nameOffset, nameLen,
                        operatorOffset, operatorLen,
                        valueContentOffset, valueContentLen,
                        valueOuterOffset, valueOuterLen);
        this.currentInjectedAttributes.add(injectedAttribute);

    }




    private static final class Selector {

        private int level = -1;
        private List<String> selectorLevels = new ArrayList<String>(5);
        private String currentSelector = null;

        Selector() {
            super();
        }

        void increaseLevel() {
            this.level++;
        }

        void decreaseLevel() {
            if (this.level < 0) {
                throw new IndexOutOfBoundsException("Cannot decrease level when the selector is clean");
            }
            if (this.selectorLevels.size() > this.level) {
                this.selectorLevels.remove(this.level);
            }
            this.level--;
        }

        void setSelector(final String selector) {
            this.selectorLevels.add((selector.charAt(0) == '/' ? selector : "//" + selector));
            this.currentSelector = null;
        }

        boolean isLevelEmpty() {
            return this.selectorLevels.size() <= this.level;
        }

        String getCurrentSelector() {
            if (this.currentSelector == null) {
                this.currentSelector = StringUtils.join(this.selectorLevels, "");
            }
            return this.currentSelector;
        }


        @Override
        public String toString() {
            return "[" + this.level + "]" + this.selectorLevels.toString();
        }

    }


}
