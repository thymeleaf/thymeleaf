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

import java.util.Arrays;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.engine.AttributeNames;
import org.thymeleaf.engine.ElementNames;
import org.thymeleaf.standard.processor.StandardBlockTagProcessor;
import org.thymeleaf.standard.processor.StandardInlineHTMLTagProcessor;
import org.thymeleaf.standard.processor.StandardTextTagProcessor;
import org.thymeleaf.standard.processor.StandardUtextTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.EscapedAttributeUtils;
import org.thymeleaf.util.TextUtils;

/**
 * <p>
 *   Class in charge of performing the required event transformations on templates or fragments being parsed so that
 *   output expressions are treated as normal element-oriented parsing events.
 * </p>
 * <p>
 *   Note this class is <strong>meant for internal use only</strong>.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 */
public final class OutputExpressionInlinePreProcessorHandler implements IInlinePreProcessorHandler {


    /*
     * This class converts inlined output expressions into their equivalent element events, which makes it possible
     * to cache parsed inlined expressions.
     *
     * Some examples:
     *
     *     [[${someVar}]]            ->     [# th:text="${someVar}"/]          (decomposed into the corresponding events)
     *     [(${someVar})]            ->     [# th:utext="${someVar}"/]         (decomposed into the corresponding events)
     *
     * NOTE: The inlining mechanism is a part of the Standard Dialects, so the conversion performed by this handler
     *       on inlined output expressions should only be applied if one of the Standard Dialects has been configured.
     */


    private static final int DEFAULT_LEVELS_SIZE = 2;

    private final IInlinePreProcessorHandler next;

    private final String standardDialectPrefix;
    private final String[] inlineAttributeNames;

    private final char[] blockElementName;
    private final String escapedTextAttributeName;
    private final String unescapedTextAttributeName;

    private int execLevel;

    private TemplateMode[] inlineTemplateModes;
    private int[] inlineExecLevels;
    private int inlineIndex;


    private char[] attributeBuffer;



    public OutputExpressionInlinePreProcessorHandler(
            final IEngineConfiguration configuration, final TemplateMode templateMode,
            final String standardDialectPrefix, final IInlinePreProcessorHandler handler) {

        super();

        this.next = handler;

        this.standardDialectPrefix = standardDialectPrefix;

        this.inlineAttributeNames =
                AttributeNames.forName(templateMode, this.standardDialectPrefix, StandardInlineHTMLTagProcessor.ATTR_NAME).getCompleteAttributeNames();

        this.blockElementName =
                ElementNames.forName(templateMode, this.standardDialectPrefix, StandardBlockTagProcessor.ELEMENT_NAME).getCompleteElementNames()[0].toCharArray();

        this.escapedTextAttributeName =
                AttributeNames.forName(templateMode, this.standardDialectPrefix, StandardTextTagProcessor.ATTR_NAME).getCompleteAttributeNames()[0];
        this.unescapedTextAttributeName =
                AttributeNames.forName(templateMode, this.standardDialectPrefix, StandardUtextTagProcessor.ATTR_NAME).getCompleteAttributeNames()[0];

        this.inlineTemplateModes = new TemplateMode[DEFAULT_LEVELS_SIZE];
        this.inlineExecLevels = new int[DEFAULT_LEVELS_SIZE];
        Arrays.fill(this.inlineTemplateModes, null);
        Arrays.fill(this.inlineExecLevels, -1);
        this.inlineIndex = 0;

        this.execLevel = 0;

        this.inlineTemplateModes[this.inlineIndex] = templateMode;
        this.inlineExecLevels[this.inlineIndex] = this.execLevel;

        this.attributeBuffer = null;

    }






    public void handleText(
            final char[] buffer,
            final int offset, final int len,
            final int line, final int col) {

        if (this.inlineTemplateModes[this.inlineIndex] != this.inlineTemplateModes[0]) {
            // Even if this text might contain some inlining, it's not something that we can do now - it's inlining
            // for a template mode different than the template's and therefore we should wait for the corresponding
            // inlined block to be re-parsed.
            this.next.handleText(buffer, offset, len, line, col);
            return;
        }

        if (!mightNeedInlining(buffer, offset, len)) {
            // Fail fast - we know for sure we will not need inlining for this text
            this.next.handleText(buffer, offset, len, line, col);
            return;
        }

        performInlining(buffer, offset, len, line, col);

    }




    public void handleStandaloneElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final boolean minimized,
            final int line, final int col) {
        increaseExecLevel();
        this.next.handleStandaloneElementStart(buffer, nameOffset, nameLen, minimized, line, col);
    }


    public void handleStandaloneElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final boolean minimized,
            final int line, final int col) {
        decreaseExecLevel();
        this.next.handleStandaloneElementEnd(buffer, nameOffset, nameLen, minimized, line, col);
    }


    public void handleOpenElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col) {
        increaseExecLevel();
        this.next.handleOpenElementStart(buffer, nameOffset, nameLen, line, col);
    }


    public void handleOpenElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col) {
        this.next.handleOpenElementEnd(buffer, nameOffset, nameLen, line, col);
    }


    public void handleAutoOpenElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col) {
        increaseExecLevel();
        this.next.handleAutoOpenElementStart(buffer, nameOffset, nameLen, line, col);
    }


    public void handleAutoOpenElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col) {
        this.next.handleAutoOpenElementEnd(buffer, nameOffset, nameLen, line, col);
    }


    public void handleCloseElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col) {
        this.next.handleCloseElementStart(buffer, nameOffset, nameLen, line, col);
    }


    public void handleCloseElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col) {
        decreaseExecLevel();
        this.next.handleCloseElementEnd(buffer, nameOffset, nameLen, line, col);
    }


    public void handleAutoCloseElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col) {
        this.next.handleAutoCloseElementStart(buffer, nameOffset, nameLen, line, col);
    }


    public void handleAutoCloseElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col) {
        decreaseExecLevel();
        this.next.handleAutoCloseElementEnd(buffer, nameOffset, nameLen, line, col);
    }


    public void handleAttribute(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int nameLine, final int nameCol,
            final int operatorOffset, final int operatorLen,
            final int operatorLine, final int operatorCol,
            final int valueContentOffset, final int valueContentLen,
            final int valueOuterOffset, final int valueOuterLen,
            final int valueLine, final int valueCol) {


        if (isInlineAttribute(buffer, nameOffset, nameLen)) {
            final String inlineModeAttributeValue =
                    EscapedAttributeUtils.unescapeAttribute(
                            this.inlineTemplateModes[0], new String(buffer, valueContentOffset, valueContentLen));
            final TemplateMode inlineTemplateMode = computeAssociatedTemplateMode(inlineModeAttributeValue);
            setInlineTemplateMode(inlineTemplateMode);
        }

        this.next.handleAttribute(
                buffer,
                nameOffset, nameLen, nameLine, nameCol,
                operatorOffset, operatorLen, operatorLine, operatorCol,
                valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen, valueLine, valueCol);

    }

    

    private void increaseExecLevel() {
        this.execLevel++;
    }

    private void decreaseExecLevel() {
        if (this.inlineExecLevels[this.inlineIndex] == this.execLevel) {
            this.inlineTemplateModes[this.inlineIndex] = null;
            this.inlineExecLevels[this.inlineIndex] = -1;
            this.inlineIndex--;
        }
        this.execLevel--;
    }



    private boolean isInlineAttribute(final char[] buffer, final int nameOffset, final int nameLen) {
        final boolean caseSensitive = this.inlineTemplateModes[0].isCaseSensitive();
        for (final String inlineAttributeName : this.inlineAttributeNames) {
            if (TextUtils.equals(caseSensitive, inlineAttributeName, 0, inlineAttributeName.length(), buffer, nameOffset, nameLen)) {
                return true;
            }
        }
        return false;
    }





    private void setInlineTemplateMode(final TemplateMode templateMode) {

        if (this.inlineExecLevels[this.inlineIndex] != this.execLevel) { // Just in case we have TWO th:inline in the same tag
            this.inlineIndex++;
        }

        if (this.inlineIndex >= this.inlineTemplateModes.length) {
            this.inlineTemplateModes = Arrays.copyOf(this.inlineTemplateModes, this.inlineTemplateModes.length + 2);
            final int oldInlineExecLevelsLen = this.inlineExecLevels.length;
            this.inlineExecLevels = Arrays.copyOf(this.inlineExecLevels, this.inlineExecLevels.length + 2);
            Arrays.fill(this.inlineExecLevels, oldInlineExecLevelsLen, this.inlineExecLevels.length, -1); // Initialize the new positions in the array to -1
        }

        this.inlineTemplateModes[this.inlineIndex] = templateMode;
        this.inlineExecLevels[this.inlineIndex] = this.execLevel;

    }



    private static TemplateMode computeAssociatedTemplateMode(final String inlineModeAttributeValue) {
        final StandardInlineMode inlineMode = StandardInlineMode.parse(inlineModeAttributeValue);
        if (inlineMode == null) {
            return null;
        }
        switch (inlineMode) {
            case NONE:
                return null;
            case HTML:
                return TemplateMode.HTML;
            case XML:
                return TemplateMode.XML;
            case TEXT:
                return TemplateMode.TEXT;
            case JAVASCRIPT:
                return TemplateMode.JAVASCRIPT;
            case CSS:
                return TemplateMode.CSS;
            default:
                throw new IllegalArgumentException("Unrecognized inline mode: " + inlineMode);
        }
    }





    private static boolean mightNeedInlining(final char[] buffer, final int offset, final int len) {
        int n = len;
        int i = offset;
        char c;
        while (n-- != 0) {
            c = buffer[i];
            if (c == '[' && n > 0) {
                c = buffer[i + 1];
                if (c == '[' || c == '(') {
                    // There probably is some kind of [[...]] or [(...)] inlined expression
                    return true;
                }
            }
            i++;
        }
        return false;
    }






    private void performInlining(
            final char[] text,
            final int offset, final int len,
            final int line, final int col) {

        final int[] locator = new int[] { line, col };

        int i = offset;
        int current = i;
        int maxi = offset + len;

        int expStart, expEnd;
        int currentLine = -1;
        int currentCol = -1;
        char innerClosingChar = 0x0;

        boolean inExpression = false;

        while (i < maxi) {

            currentLine = locator[0];
            currentCol = locator[1];

            if (!inExpression) {

                expStart = findNextStructureStart(text, i, maxi, locator);

                if (expStart == -1) {
                    this.next.handleText(text, current, (maxi - current), currentLine, currentCol);
                    return;
                }

                inExpression = true;

                if (expStart > current) {
                    // We avoid empty-string text events
                    this.next.handleText(text, current, (expStart - current), currentLine, currentCol);
                }

                innerClosingChar = ((text[expStart + 1] == '[' )? ']' : ')');
                current = expStart;
                i = current + 2;

            } else {

                // The inner closing char we will be looking for will depend on the type of expression we just found

                expEnd = findNextStructureEndAvoidQuotes(text, i, maxi, innerClosingChar, locator);

                if (expEnd < 0) {
                    this.next.handleText(text, current, (maxi - current), currentLine, currentCol);
                    return;
                }

                final String textAttributeName = (text[current + 1] == '[')? this.escapedTextAttributeName : this.unescapedTextAttributeName;
                final int textAttributeNameLen = textAttributeName.length();
                final int textAttributeValueLen = (expEnd - (current + 2));

                prepareAttributeBuffer(textAttributeName, text, current + 2, textAttributeValueLen);


                this.next.handleOpenElementStart(this.blockElementName, 0, this.blockElementName.length, currentLine, currentCol + 2);
                this.next.handleAttribute(
                        this.attributeBuffer,
                        0, textAttributeNameLen,
                        currentLine, currentCol + 2,
                        textAttributeNameLen, 1,
                        currentLine, currentCol + 2,
                        textAttributeNameLen + 2, textAttributeValueLen,
                        textAttributeNameLen + 1, textAttributeValueLen + 2,
                        currentLine, currentCol + 2);
                this.next.handleOpenElementEnd(this.blockElementName, 0, this.blockElementName.length, currentLine, currentCol + 2);

                this.next.handleCloseElementStart(this.blockElementName, 0, this.blockElementName.length, currentLine, currentCol + 2);
                this.next.handleCloseElementEnd(this.blockElementName, 0, this.blockElementName.length, currentLine, currentCol + 2);



                // The ')]' or ']]' suffix will be considered as processed too
                countChar(locator, text[expEnd]);
                countChar(locator, text[expEnd + 1]);

                inExpression = false;

                current = expEnd + 2;
                i = current;


            }

        }

        if (inExpression) {// Just in case input ended in '[[' or '[('
            this.next.handleText(text, current, (maxi - current), currentLine, currentCol);
        }

    }







    private static void countChar(final int[] locator, final char c) {
        if (c == '\n') {
            locator[0]++;
            locator[1] = 1;
            return;
        }
        locator[1]++;
    }


    private static int findNextStructureStart(
            final char[] text, final int offset, final int maxi,
            final int[] locator) {

        char c;

        int colIndex = offset;

        int i = offset;
        int n = (maxi - offset);

        while (n-- != 0) {

            c = text[i];

            if (c == '\n') {
                colIndex = i;
                locator[1] = 0;
                locator[0]++;
            } else if (c == '[' && n > 0) {
                c = text[i + 1];
                if (c == '[' || c == '(') { // We've probably found either a [[...]] or a [(...)] (at least its start)
                    locator[1] += (i - colIndex);
                    return i;
                }
            }

            i++;

        }

        locator[1] += (maxi - colIndex);
        return -1;

    }




    private static int findNextStructureEndAvoidQuotes(
            final char[] text, final int offset, final int maxi,
            final char innerClosingChar, final int[] locator) {

        boolean inQuotes = false;
        boolean inApos = false;

        char c;

        int colIndex = offset;

        int i = offset;
        int n = (maxi - offset);

        while (n-- != 0) {

            c = text[i];

            if (c == '\n') {
                colIndex = i;
                locator[1] = 0;
                locator[0]++;
            } else if (c == '"' && !inApos) {
                inQuotes = !inQuotes;
            } else if (c == '\'' && !inQuotes) {
                inApos = !inApos;
            } else if (c == innerClosingChar && !inQuotes && !inApos && n > 0) {
                c = text[i + 1];
                if (c == ']') {
                    locator[1] += (i - colIndex);
                    return i;
                }
            }

            i++;

        }

        locator[1] += (maxi - colIndex);
        return -1;

    }



    private void prepareAttributeBuffer(final String attributeName, final char[] valueText, final int valueOffset, final int valueLen) {

        final int attributeNameLen = attributeName.length();
        final int requiredLen = attributeNameLen + 2 + valueLen + 1; // {name}="{value}"

        if (this.attributeBuffer == null || this.attributeBuffer.length < requiredLen) {
            this.attributeBuffer = new char[Math.max(requiredLen, 30)];
        }

        attributeName.getChars(0, attributeNameLen, this.attributeBuffer, 0);
        this.attributeBuffer[attributeNameLen] = '=';
        this.attributeBuffer[attributeNameLen + 1] = '\"';
        System.arraycopy(valueText, valueOffset, this.attributeBuffer, attributeNameLen + 2, valueLen);
        this.attributeBuffer[requiredLen - 1] = '\"';

    }



}