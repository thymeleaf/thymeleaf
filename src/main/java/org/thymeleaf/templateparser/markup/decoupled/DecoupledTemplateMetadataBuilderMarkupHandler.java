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

import org.attoparser.AbstractMarkupHandler;
import org.attoparser.ParseException;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.model.IElementAttributes;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.text.ITextRepository;
import org.thymeleaf.util.TextUtils;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Implementation of {@link org.attoparser.IMarkupHandler} used for building and populating instances of
 *   {@link DecoupledTemplateMetadata} as a result of parsing a decoupled template logic resource.
 * </p>
 * <p>
 *   Once built and populated, instances of {@link DecoupledTemplateMetadata} are handled over to
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
public final class DecoupledTemplateMetadataBuilderMarkupHandler extends AbstractMarkupHandler {

    public static final String TAG_NAME_ATTR = "attr";
    public static final String ATTRIBUTE_NAME_SEL = "sel";

    private static final char[] TAG_NAME_ATTR_CHARS = TAG_NAME_ATTR.toCharArray();
    private static final char[] ATTRIBUTE_NAME_SEL_CHARS = ATTRIBUTE_NAME_SEL.toCharArray();

    private final String templateName;
    private final ITextRepository textRepository;
    private final TemplateMode templateMode;
    private final DecoupledTemplateMetadata decoupledTemplateMetadata;

    // TODO this has to be made hierarchical!!
    private boolean inAttr = false;
    private String currentSelector = null;




    public DecoupledTemplateMetadataBuilderMarkupHandler(final String templateName,
                                                         final ITextRepository textRepository,
                                                         final TemplateMode templateMode,
                                                         final DecoupledTemplateMetadata decoupledTemplateMetadata) {
        super();

        Validate.notEmpty(templateName, "Template name cannot be null or empty");
        Validate.notNull(textRepository, "Text Repository cannot be null");
        Validate.notNull(templateMode, "Template mode cannot be null");
        Validate.notNull(decoupledTemplateMetadata, "Decoupled template metadata cannot be null");

        this.templateName = templateName;
        this.textRepository = textRepository;
        this.templateMode = templateMode;
        this.decoupledTemplateMetadata = decoupledTemplateMetadata;

    }




    @Override
    public void handleStandaloneElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final boolean minimized, final int line, final int col)
            throws ParseException {

        if (!TextUtils.equals(this.templateMode.isCaseSensitive(), buffer, nameOffset, nameLen, TAG_NAME_ATTR_CHARS, 0, TAG_NAME_ATTR_CHARS.length)) {
            // This is not an <attr> tag, so just ignore
            return;
        }

        this.inAttr = true;

    }

    @Override
    public void handleStandaloneElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final boolean minimized, final int line, final int col)
            throws ParseException {

        if (this.inAttr && this.currentSelector == null) {
            throw new TemplateInputException(
                    "Error while processing decoupled logic file: attr injection tag contains no " +
                    "selector (\"sel\") attribute", this.templateName, line, col);
        }

        this.inAttr = false;
        this.currentSelector = null;

    }



    @Override
    public void handleOpenElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        this.inAttr = false;
        this.currentSelector = null;

        if (!TextUtils.equals(this.templateMode.isCaseSensitive(), buffer, nameOffset, nameLen, TAG_NAME_ATTR_CHARS, 0, TAG_NAME_ATTR_CHARS.length)) {
            // This is not an <attr> tag, so just ignore
            return;
        }

        this.inAttr = true;

    }



    @Override
    public void handleOpenElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        if (this.inAttr && this.currentSelector == null) {
            throw new TemplateInputException(
                    "Error while processing decoupled logic file: attr injection tag contains no " +
                    "selector (\"sel\") attribute", this.templateName, line, col);
        }

        this.inAttr = false;
        this.currentSelector = null;

    }



    @Override
    public void handleCloseElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        // TODO Nothing to be done for now... but we will have to do things for hierarchical selectors

    }

    @Override
    public void handleCloseElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        // TODO Nothing to be done for now... but we will have to do things for hierarchical selectors

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


        if (!this.inAttr) {
            // Just ignore, we don't know what is this
            return;
        }

        // Check for the "sel" attribute
        if (TextUtils.equals(this.templateMode.isCaseSensitive(), buffer, nameOffset, nameLen, ATTRIBUTE_NAME_SEL_CHARS, 0, ATTRIBUTE_NAME_SEL_CHARS.length)) {

            if (this.currentSelector != null) {
                throw new TemplateInputException(
                        "Error while processing decoupled logic file: selector (\"sel\") attribute found more than " +
                        "once in attr injection tag", this.templateName, nameLine, nameCol);
            }

            // TODO Make this hierarchical
            this.currentSelector = this.textRepository.getText(buffer, valueContentOffset, valueContentLen);
            return;

        }


        /*
         * We know this is not the selector attribute, so we will just consider this attribute, whichever it is, as
         * an attribute to be injected into the template being parsed.
         */

        final String attributeName = this.textRepository.getText(buffer, nameOffset, nameLen);

        final String attributeValue =
                (operatorLen > 0 ?
                        this.textRepository.getText(buffer, valueContentOffset, valueContentLen) : null);

        final IElementAttributes.ValueQuotes valueQuotes;
        if (attributeValue == null) {
            valueQuotes = null;
        } else if (valueOuterOffset == valueContentOffset) {
            valueQuotes = IElementAttributes.ValueQuotes.NONE;
        } else if (buffer[valueOuterOffset] == '"') {
            valueQuotes = IElementAttributes.ValueQuotes.DOUBLE;
        } else if (buffer[valueOuterOffset] == '\'') {
            valueQuotes = IElementAttributes.ValueQuotes.SINGLE;
        } else {
            valueQuotes = IElementAttributes.ValueQuotes.NONE;
        }

        // Add the attribute to the decoupled metadata
        this.decoupledTemplateMetadata.addInjectedAttribute(this.currentSelector, attributeName, valueQuotes, attributeValue);

    }



}
