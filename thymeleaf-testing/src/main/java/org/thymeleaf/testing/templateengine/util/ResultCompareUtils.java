/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.testing.templateengine.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.attoparser.IMarkupParser;
import org.attoparser.MarkupParser;
import org.attoparser.ParseException;
import org.attoparser.config.ParseConfiguration;
import org.attoparser.trace.MarkupTraceEvent;
import org.attoparser.trace.TraceBuilderMarkupHandler;
import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.util.Validate;






public class ResultCompareUtils {

    
    private static final AttributeEventComparator ATTRIBUTE_EVENT_COMPARATOR = new AttributeEventComparator();
    private static final IMarkupParser PARSER;
    private static final ParseConfiguration MARKUP_PARSING_CONFIGURATION;


    
    private static final Set<String> BLOCK_ELEMENTS =
            new HashSet<String>(Arrays.asList(
                    new String[] {
                            "address", "article", "aside", "audio", "blockquote", "canvas", 
                            "dd", "div", "dl", "dt", "fieldset", "figcaption", "figure", "footer", 
                            "form", "h1", "h2", "h3", "h4", "h5", "h6", "header", "hgroup", "hr",
                            "li", "main", "nav", "noscript", "ol", "option", "output", "p", "pre", "section", "table", "tbody",
                            "tfoot", "tr", "td", "th", "ul", "video"
                    }));
    private static final Set<String> BLOCK_CONTAINER_ELEMENTS =
            new HashSet<String>(Arrays.asList(
                    new String[] {
                            "address", "article", "aside", "div", "dl", "fieldset", "footer", 
                            "form", "header", "hgroup",  "noscript", "ol", "section", "table", 
                            "tbody", "tr", "tfoot", "ul"
                    }));



    static {

        /*
         * Exactly the same configuration used at Thymeleaf's HtmlTemplateParser (> v3.0)
         */
        MARKUP_PARSING_CONFIGURATION = ParseConfiguration.htmlConfiguration();
        MARKUP_PARSING_CONFIGURATION.setElementBalancing(ParseConfiguration.ElementBalancing.AUTO_CLOSE);
        MARKUP_PARSING_CONFIGURATION.setCaseSensitive(false);
        MARKUP_PARSING_CONFIGURATION.setNoUnmatchedCloseElementsRequired(false);
        MARKUP_PARSING_CONFIGURATION.setUniqueAttributesInElementRequired(true);
        MARKUP_PARSING_CONFIGURATION.setXmlWellFormedAttributeValuesRequired(false);
        MARKUP_PARSING_CONFIGURATION.setUniqueRootElementPresence(ParseConfiguration.UniqueRootElementPresence.NOT_VALIDATED);
        MARKUP_PARSING_CONFIGURATION.getPrologParseConfiguration().setDoctypePresence(ParseConfiguration.PrologPresence.ALLOWED);
        MARKUP_PARSING_CONFIGURATION.getPrologParseConfiguration().setRequireDoctypeKeywordsUpperCase(false);
        MARKUP_PARSING_CONFIGURATION.getPrologParseConfiguration().setValidateProlog(false);
        MARKUP_PARSING_CONFIGURATION.getPrologParseConfiguration().setXmlDeclarationPresence(ParseConfiguration.PrologPresence.ALLOWED);

        PARSER = new MarkupParser(MARKUP_PARSING_CONFIGURATION);

    }




    /**
     *
     * @deprecated as of 3.0. Should use {@link #compareMarkupResults(String, String, boolean)} or
     *             {@link #compareTextResults(String, String)} instead. Will be removed in 3.1.
     * @param expected the expected result
     * @param actual the actual result
     * @param lenient whether comparison has to be made in lenient mode or not
     * @return the result comparison
     */
    @Deprecated
    public static ResultComparison compareResults(final String expected, final String actual, final boolean lenient) {
        return compareMarkupResults(expected, actual, lenient);
    }




    public static ResultComparison compareTextResults(final String expected, final String actual) {

        Validate.notNull(expected, "Expected result cannot be null");
        Validate.notNull(actual, "Actual result cannot be null");

        final int[] locator = new int[] {1, 1};

        final int actualLen = actual.length();
        final int expectedLen = expected.length();

        char c;
        int i = 0;

        while (i < actualLen && i < expectedLen) {

            c = actual.charAt(i);
            if (c != expected.charAt(i)) {

                final String actualFragment = actual.substring(Math.max(0, i - 20), Math.min(actualLen, i + 20));
                final String expectedFragment = expected.substring(Math.max(0, i - 20), Math.min(expectedLen, i + 20));

                final String explanation = createExplanation(actualFragment, locator[0], locator[1], expectedFragment);

                return new ResultComparison(false, explanation);

            }
            countChar(locator, c);
            i++;

        }

        if (i < actualLen || i < expectedLen) {

            final String actualFragment = actual.substring(Math.max(0, i - 20), Math.min(actualLen, i + 20));
            final String expectedFragment = expected.substring(Math.max(0, i - 20), Math.min(expectedLen, i + 20));

            final String explanation = createExplanation(actualFragment, locator[0], locator[1], expectedFragment);

            return new ResultComparison(false, explanation);

        }

        return new ResultComparison(true, "OK");

    }



    private static void countChar(final int[] locator, final char c) {
        if (c == '\n') {
            locator[0]++;
            locator[1] = 1;
            return;
        }
        locator[1]++;
    }




    public static ResultComparison compareMarkupResults(final String expected, final String actual, final boolean lenient) {

        Validate.notNull(expected, "Expected result cannot be null");
        Validate.notNull(actual, "Actual result cannot be null");

        final TraceBuilderMarkupHandler expectedHandler = new TraceBuilderMarkupHandler();
        final TraceBuilderMarkupHandler actualHandler = new TraceBuilderMarkupHandler();

        try {
            PARSER.parse(expected, expectedHandler);
            PARSER.parse(actual, actualHandler);
        } catch (final ParseException e) {
            throw new TestEngineExecutionException("Error while trying to compare results", e);
        }

        final List<MarkupTraceEvent> expectedTrace =
                (lenient? normalizeTrace(expectedHandler.getTrace()) : expectedHandler.getTrace());
        final List<MarkupTraceEvent> actualTrace =
                (lenient? normalizeTrace(actualHandler.getTrace()) : actualHandler.getTrace());

        final int actualTraceSize = actualTrace.size();
        final int expectedTraceSize = expectedTrace.size();

        for (int i = 0; i < actualTraceSize; i++) {

            final MarkupTraceEvent actualTraceItem = actualTrace.get(i);
            final MarkupTraceEvent expectedTraceItem =
                    (expectedTraceSize > i? expectedTrace.get(i) : null);

            if (actualTraceItem instanceof MarkupTraceEvent.DocumentStartTraceEvent &&
                    expectedTraceItem != null && expectedTraceItem instanceof MarkupTraceEvent.DocumentStartTraceEvent) {
                continue;
            }

            if (actualTraceItem instanceof MarkupTraceEvent.DocumentEndTraceEvent &&
                    expectedTraceItem != null && expectedTraceItem instanceof MarkupTraceEvent.DocumentEndTraceEvent) {
                continue;
            }

            if (expectedTraceItem == null) {

                final int[] actualFragmentReportSizes = computeErrorMessageLength(actualTrace, i, actual);
                final int[] expectedFragmentReportSizes = new int[] {20,0};

                final int actualTraceItemLine = computeFirstLine(actualTraceItem);
                final int actualTraceItemCol = computeFirstCol(actualTraceItem);

                final String actualFragment =
                        getFragmentSurrounding(
                                actual, actualTraceItemLine, actualTraceItemCol, actualFragmentReportSizes[0], actualFragmentReportSizes[1]);
                final String expectedFragment =
                        getFragmentSurrounding(
                                expected, Integer.MAX_VALUE, Integer.MAX_VALUE, expectedFragmentReportSizes[0], expectedFragmentReportSizes[1]);

                final String explanation =
                        createExplanation(actualFragment, actualTraceItemLine, actualTraceItemCol, expectedFragment);

                return new ResultComparison(false, explanation);

            }


            final MarkupTraceEvent comparableActualTraceItem = compressWhitespaceIfText(actualTraceItem);
            final MarkupTraceEvent comparableExpectedTraceItem = compressWhitespaceIfText(expectedTraceItem);

            final boolean itemMatches =
                    (lenient? comparableActualTraceItem.matchesTypeAndContent(comparableExpectedTraceItem) : actualTraceItem.equals(expectedTraceItem));

            if (!itemMatches) {

                final int[] actualFragmentReportSizes = computeErrorMessageLength(actualTrace, i, actual);
                final int[] expectedFragmentReportSizes = computeErrorMessageLength(expectedTrace, i, expected);

                final int actualTraceItemLine = computeFirstLine(actualTraceItem);
                final int actualTraceItemCol = computeFirstCol(actualTraceItem);
                final int expectedTraceItemLine = computeFirstLine(expectedTraceItem);
                final int expectedTraceItemCol = computeFirstCol(expectedTraceItem);

                final String actualFragment =
                        getFragmentSurrounding(
                                actual, actualTraceItemLine, actualTraceItemCol, actualFragmentReportSizes[0], actualFragmentReportSizes[1]);
                final String expectedFragment =
                        getFragmentSurrounding(
                                expected, expectedTraceItemLine, expectedTraceItemCol, expectedFragmentReportSizes[0], expectedFragmentReportSizes[1]);

                final String explanation =
                        createExplanation(actualFragment, actualTraceItemLine, actualTraceItemCol, expectedFragment);

                return new ResultComparison(false, explanation);

            }

        }


        return new ResultComparison(true, "OK");

    }
    
    
    
    

    private static List<MarkupTraceEvent> normalizeTrace(final List<MarkupTraceEvent> trace) {
        
        String lastOpenElementName = null;
        String lastClosedElementName = null;
        boolean lastIsWhiteSpace = false;
        
        final List<MarkupTraceEvent> newTrace = new ArrayList<MarkupTraceEvent>();
        final List<MarkupTraceEvent> currentAttributeList = new ArrayList<MarkupTraceEvent>();
        
        for (final MarkupTraceEvent event : trace) {
            
            final MarkupTraceEvent.EventType eventType = event.getEventType();
            
            if (!currentAttributeList.isEmpty()) {
                if (MarkupTraceEvent.EventType.ATTRIBUTE != eventType &&
                        MarkupTraceEvent.EventType.INNER_WHITE_SPACE != eventType) {
                    Collections.sort(currentAttributeList, ATTRIBUTE_EVENT_COMPARATOR);
                    newTrace.addAll(currentAttributeList);
                    currentAttributeList.clear();
                    lastIsWhiteSpace = false;
                }
            }
            
            if (newTrace.size() == 2) {
                /*
                 * We will ignore every leading text node before anything not being a DOCTYPE or
                 * an XML DECLARATION (in which cases, leading text will matter).
                 * 
                 * We check newTrace to be of size 2 because we look for: 
                 * [0] = DOCUMENT_START, [1] = TEXT
                 */
                if (MarkupTraceEvent.EventType.DOC_TYPE != eventType &&
                    MarkupTraceEvent.EventType.XML_DECLARATION != eventType &&
                    MarkupTraceEvent.EventType.DOCUMENT_END != eventType) {
                    
                    if (lastIsWhiteSpace) {
                        newTrace.remove(newTrace.size() - 1);
                        lastIsWhiteSpace = false;
                    }
                    
                }
            }
            
            if (MarkupTraceEvent.EventType.TEXT == eventType) {
                // We need to compress all whitespace in order to perform a correct lenient check
                
                final String text = ((MarkupTraceEvent.TextTraceEvent)event).getContent();

                // We will not perform the whitespace compression here. Instead, we will
                // do that only in the very moment the text event items are compared, so that
                // every computation regarding error messages is done on the original text.
                newTrace.add(event);
                
                if (isAllWhitespace(text)) {
                    lastIsWhiteSpace = true;
                } else {
                    lastOpenElementName = null;
                    lastClosedElementName = null;
                }
                
                
            } else if (MarkupTraceEvent.EventType.INNER_WHITE_SPACE == eventType) {
                
                // These events are not relevant for result matching, so we just ignore them 
                // (they represent mere inter-attribute whitespace)
                
            } else if (MarkupTraceEvent.EventType.ATTRIBUTE == eventType) {
                
                currentAttributeList.add(event);
                
            } else if (MarkupTraceEvent.EventType.CLOSE_ELEMENT_END == eventType ||
                       MarkupTraceEvent.EventType.STANDALONE_ELEMENT_END == eventType) {
                
                /*
                 * We need to store the name of the last element closed in order to later determine whether
                 * any white space events between it and the following element should be ignored.
                 */
                
                lastClosedElementName =
                        (MarkupTraceEvent.EventType.CLOSE_ELEMENT_END == eventType?
                                ((MarkupTraceEvent.CloseElementEndTraceEvent)event).getElementName().toLowerCase() :
                                ((MarkupTraceEvent.StandaloneElementEndTraceEvent)event).getElementName().toLowerCase());

                lastOpenElementName = null;
                newTrace.add(event);
                lastIsWhiteSpace = false;
                
            } else if (MarkupTraceEvent.EventType.OPEN_ELEMENT_START == eventType ||
                       MarkupTraceEvent.EventType.STANDALONE_ELEMENT_START == eventType) {

                /*
                 * Whitespace between block elements (e.g. <div>, <p>...) or between block containers
                 * and block elements (e.g. <div><p>, <ul><li>) should not be considered.
                 */

                final String elementName =
                        (MarkupTraceEvent.EventType.OPEN_ELEMENT_START == eventType?
                                ((MarkupTraceEvent.OpenElementStartTraceEvent)event).getElementName().toLowerCase() :
                                ((MarkupTraceEvent.StandaloneElementStartTraceEvent)event).getElementName().toLowerCase());

                if (lastClosedElementName != null && 
                    BLOCK_ELEMENTS.contains(lastClosedElementName) &&
                    BLOCK_ELEMENTS.contains(elementName)) {
                    
                    if (lastIsWhiteSpace) {
                        newTrace.remove(newTrace.size() - 1);
                        lastIsWhiteSpace = false;
                    }

                } else if (lastOpenElementName != null && 
                           BLOCK_CONTAINER_ELEMENTS.contains(lastOpenElementName) &&
                           BLOCK_ELEMENTS.contains(elementName)) {
   
                    if (lastIsWhiteSpace) {
                        newTrace.remove(newTrace.size() - 1);
                        lastIsWhiteSpace = false;
                    }

                } 
                
                if (MarkupTraceEvent.EventType.STANDALONE_ELEMENT_START == eventType) {
                    lastOpenElementName = null;
                    lastClosedElementName = elementName;
                } else {
                    lastOpenElementName = elementName;
                    lastClosedElementName = null;
                }
                newTrace.add(event);
                lastIsWhiteSpace = false;
                
            } else if (MarkupTraceEvent.EventType.CLOSE_ELEMENT_START == eventType) {

                /*
                 * Whitespace between block elements closing tags and block containers closing tags
                 * (e.g. </p></div>, </li></ul>) should not be considered.
                 */
                
                final String elementName = ((MarkupTraceEvent.CloseElementStartTraceEvent)event).getElementName().toLowerCase();
                
                if (lastClosedElementName != null && 
                    BLOCK_ELEMENTS.contains(lastClosedElementName) &&
                    BLOCK_CONTAINER_ELEMENTS.contains(elementName)) {
                    
                    if (lastIsWhiteSpace) {
                        newTrace.remove(newTrace.size() - 1);
                        lastIsWhiteSpace = false;
                    }

                } 
                
                lastOpenElementName = null;
                lastClosedElementName = elementName;
                newTrace.add(event);
                lastIsWhiteSpace = false;
                
            } else if (MarkupTraceEvent.EventType.DOCUMENT_END == eventType) {
                
                /*
                 * If the last event before document end is just whitespace text and trace is 
                 * bigger than just two events (document start + one event),
                 * we will just remove it. Whitespace at the end of a document has no influence at all.
                 */
                final MarkupTraceEvent lastEvent =
                        (newTrace.size() > 2? newTrace.get(newTrace.size() - 1) : null);
                if (lastEvent != null && MarkupTraceEvent.EventType.TEXT == lastEvent.getEventType()) {
                    final String text = ((MarkupTraceEvent.TextTraceEvent)lastEvent).getContent();
                    if (isAllWhitespace(text)) {
                        newTrace.remove(newTrace.size() - 1);
                    }
                }
                newTrace.add(event);
                lastIsWhiteSpace = false;
                
            } else if (MarkupTraceEvent.EventType.OPEN_ELEMENT_END == eventType) {
                
                // If we are closing an opening tag, we should not initialize the "lastOpenElementName" field
                // that we just set at the OPEN_ELEMENT_START
                lastClosedElementName = null;
                newTrace.add(event);
                lastIsWhiteSpace = false;
                
            } else {
                
                lastOpenElementName = null;
                lastClosedElementName = null;
                newTrace.add(event);
                lastIsWhiteSpace = false;
                
            }
            
        }
        
        return newTrace;
        
    }
    
    
    
    
    
    private static MarkupTraceEvent compressWhitespaceIfText(final MarkupTraceEvent event) {
        
        final MarkupTraceEvent.EventType eventType = event.getEventType();
        if (MarkupTraceEvent.EventType.TEXT != eventType) {
            return event;
        }

        final MarkupTraceEvent.TextTraceEvent textEvent = ((MarkupTraceEvent.TextTraceEvent)event);

        final String text = textEvent.getContent();
                
        final StringBuilder strBuilder = new StringBuilder(text.length());
        
        boolean whitespace = false;
        
        final int textLen = text.length();
        for (int i = 0; i < textLen; i++) {
            final char c = text.charAt(i);
            if (Character.isWhitespace(c)) {
                if (!whitespace) {
                    strBuilder.append(' ');
                    whitespace = true;
                }
            } else {
                whitespace = false;
                strBuilder.append(c);
            }
        }
        
        return new MarkupTraceEvent.TextTraceEvent(strBuilder.toString(), textEvent.getLine(), textEvent.getCol());
        
    }
    
    
    
    
    
    private static boolean isAllWhitespace(final String text) {
        final int textLen = text.length();
        for (int i = 0; i < textLen; i++) {
            final char c = text.charAt(i);
            if (!Character.isWhitespace(c)) {
                return false;
            }
        }
        return true;
    }
    
    
    
    private static String getFragmentSurrounding(
            final String text, final int line, final int col, final int before, final int after) {
        
        final int textLen = text.length();
        
        int cline = 1;
        int ccol = 1;
        int pos = 0;
        
        while (pos < textLen) {
            if (cline >= line && ccol >= col) {
                break;
            }
            final char c = text.charAt(pos);
            if (c == '\n') {
                cline++;
                ccol = 1;
            } else {
                ccol++;
            }
            pos++;
        }
        
        if (pos >= textLen) {
            pos = textLen - 1;
        }
        
        // pos contains the position in text marking the desired location
        // we should get 50 chars before, 50 chars after
        
        final int startPos = Math.max(0, (pos - before));
        final int endPos = Math.min(textLen, (pos + after));

        return new String(text.substring(startPos, endPos));
        
    }    


    
    
    private static String createExplanation(
            final String actualFragment, final int actualLine, final int actualCol, final String expectedFragment) {
        return "Actual result does not match expected result.\nObtained:\n[" + actualFragment + "]\n" +
               "at line " + actualLine + " col " + actualCol + ", but " +
               "expected:\n[" + expectedFragment + "]";
    }
    
    
    
    
    private static int[] computeErrorMessageLength(final List<MarkupTraceEvent> trace, final int position, final String result) {

        MarkupTraceEvent eventItem = trace.get(position);

        MarkupTraceEvent.EventType eventType = eventItem.getEventType();

        if (MarkupTraceEvent.EventType.TEXT == eventType) {

            final MarkupTraceEvent.TextTraceEvent textEvent = (MarkupTraceEvent.TextTraceEvent) eventItem;

            final String content = textEvent.getContent();
            final int contentLen = content == null ? 0 : content.length();

            return new int[] {20, contentLen + 20};
            
        }

        if (MarkupTraceEvent.EventType.ATTRIBUTE == eventType ||
                MarkupTraceEvent.EventType.OPEN_ELEMENT_START == eventType ||
                MarkupTraceEvent.EventType.STANDALONE_ELEMENT_START == eventType) {
            
            // We will try to output all text from the start of the container tag to the last
            // attribute in the same tag.

            final int attributeLine = computeFirstLine(eventItem);
            final int attributeCol = computeFirstCol(eventItem);

            /*
             * If this event is an attribute, we need to go back in the sequence until we find the corresponding
             * element, so that we can include it complete in the output.
             */

            int i = position;
            eventItem = trace.get(i);
            eventType = eventItem.getEventType();
            while (MarkupTraceEvent.EventType.OPEN_ELEMENT_START != eventType &&
                    MarkupTraceEvent.EventType.STANDALONE_ELEMENT_START != eventType &&
                    i > 0) {
                eventItem = trace.get(--i);
                eventType = eventItem.getEventType();
            }

            final int elementLine = computeFirstLine(eventItem);
            final int elementCol = computeFirstCol(eventItem);

            final int beforeDistance =
                    computeDistance(result, elementLine, elementCol, attributeLine, attributeCol);
            
            int afterDistance = 0;
            int lastAttributeLen = 0;
            eventItem = trace.get(++i);
            eventType = eventItem.getEventType();

            while (MarkupTraceEvent.EventType.ATTRIBUTE == eventType && i < trace.size()) {

                final MarkupTraceEvent.AttributeTraceEvent attributeTraceEvent =
                        (MarkupTraceEvent.AttributeTraceEvent) eventItem;

                final int distance = 
                        computeDistance(result, attributeLine, attributeCol, attributeTraceEvent.getNameLine(), attributeTraceEvent.getNameCol());
                if (distance > afterDistance) {
                    afterDistance = distance;
                    lastAttributeLen =
                            attributeTraceEvent.getName().length() +
                            (attributeTraceEvent.getOperator() != null? attributeTraceEvent.getOperator().length() : 0) +
                            (attributeTraceEvent.getOuterValue() != null? attributeTraceEvent.getOuterValue().length() : 0);
                }

                eventItem = trace.get(++i);
                eventType = eventItem.getEventType();

            }
            

            return new int[] { (beforeDistance + 20), (afterDistance + lastAttributeLen + 80) };
            
        }
        
        return new int[] {20, 80};
        
    }
    

    
    
    private static int computeDistance(
            final String text, 
            final int lineFrom, final int colFrom,
            final int lineTo, final int colTo) {
        
        final int textLen = text.length();
        
        int startPos = 0;
        int endPos = 0;
        
        {
            int line = 1;
            int col = 1;
            for (int i = 0; i < textLen; i++) {
                if (line == lineFrom && col == colFrom) {
                    startPos = i;
                    break;
                }
                final char c = text.charAt(i);
                if (c == '\n') {
                    line++;
                    col = 1;
                    continue;
                }
                col++;
            }
        }

        {
            int line = 1;
            int col = 1;
            for (int i = 0; i < textLen; i++) {
                if (line == lineTo && col == colTo) {
                    endPos = i;
                    break;
                }
                final char c = text.charAt(i);
                if (c == '\n') {
                    line++;
                    col = 1;
                    continue;
                }
                col++;
            }
        }

        return (endPos - startPos);
        
    }
    



    private static int computeFirstLine(final MarkupTraceEvent event) {

        if (event instanceof MarkupTraceEvent.XmlDeclarationTraceEvent) {
            return ((MarkupTraceEvent.XmlDeclarationTraceEvent)event).getKeywordLine();
        }
        if (event instanceof MarkupTraceEvent.DocTypeTraceEvent) {
            return ((MarkupTraceEvent.DocTypeTraceEvent)event).getKeywordLine();
        }
        if (event instanceof MarkupTraceEvent.CommentTraceEvent) {
            return ((MarkupTraceEvent.CommentTraceEvent)event).getLine();
        }
        if (event instanceof MarkupTraceEvent.TextTraceEvent) {
            return ((MarkupTraceEvent.TextTraceEvent)event).getLine();
        }
        if (event instanceof MarkupTraceEvent.ProcessingInstructionTraceEvent) {
            return ((MarkupTraceEvent.ProcessingInstructionTraceEvent)event).getTargetLine();
        }
        if (event instanceof MarkupTraceEvent.CDATASectionTraceEvent) {
            return ((MarkupTraceEvent.CDATASectionTraceEvent)event).getLine();
        }
        if (event instanceof MarkupTraceEvent.StandaloneElementStartTraceEvent) {
            return ((MarkupTraceEvent.StandaloneElementStartTraceEvent)event).getLine();
        }
        if (event instanceof MarkupTraceEvent.StandaloneElementEndTraceEvent) {
            return ((MarkupTraceEvent.StandaloneElementEndTraceEvent)event).getLine();
        }
        if (event instanceof MarkupTraceEvent.OpenElementStartTraceEvent) {
            return ((MarkupTraceEvent.OpenElementStartTraceEvent)event).getLine();
        }
        if (event instanceof MarkupTraceEvent.OpenElementEndTraceEvent) {
            return ((MarkupTraceEvent.OpenElementEndTraceEvent)event).getLine();
        }
        if (event instanceof MarkupTraceEvent.AutoOpenElementStartTraceEvent) {
            return ((MarkupTraceEvent.AutoOpenElementStartTraceEvent)event).getLine();
        }
        if (event instanceof MarkupTraceEvent.AutoOpenElementEndTraceEvent) {
            return ((MarkupTraceEvent.AutoOpenElementEndTraceEvent)event).getLine();
        }
        if (event instanceof MarkupTraceEvent.CloseElementStartTraceEvent) {
            return ((MarkupTraceEvent.CloseElementStartTraceEvent)event).getLine();
        }
        if (event instanceof MarkupTraceEvent.CloseElementEndTraceEvent) {
            return ((MarkupTraceEvent.CloseElementEndTraceEvent)event).getLine();
        }
        if (event instanceof MarkupTraceEvent.AutoCloseElementStartTraceEvent) {
            return ((MarkupTraceEvent.AutoCloseElementStartTraceEvent)event).getLine();
        }
        if (event instanceof MarkupTraceEvent.AutoCloseElementEndTraceEvent) {
            return ((MarkupTraceEvent.AutoCloseElementEndTraceEvent)event).getLine();
        }
        if (event instanceof MarkupTraceEvent.AttributeTraceEvent) {
            return ((MarkupTraceEvent.AttributeTraceEvent)event).getNameLine();
        }
        if (event instanceof MarkupTraceEvent.InnerWhiteSpaceTraceEvent) {
            return ((MarkupTraceEvent.InnerWhiteSpaceTraceEvent)event).getLine();
        }
        if (event instanceof MarkupTraceEvent.NonMinimizedStandaloneElementStartTraceEvent) {
            return ((MarkupTraceEvent.NonMinimizedStandaloneElementStartTraceEvent)event).getLine();
        }
        if (event instanceof MarkupTraceEvent.NonMinimizedStandaloneElementEndTraceEvent) {
            return ((MarkupTraceEvent.NonMinimizedStandaloneElementEndTraceEvent)event).getLine();
        }
        if (event instanceof MarkupTraceEvent.UnmatchedCloseElementStartTraceEvent) {
            return ((MarkupTraceEvent.UnmatchedCloseElementStartTraceEvent)event).getLine();
        }
        if (event instanceof MarkupTraceEvent.UnmatchedCloseElementEndTraceEvent) {
            return ((MarkupTraceEvent.UnmatchedCloseElementEndTraceEvent) event).getLine();
        }
        if (event instanceof MarkupTraceEvent.DocumentStartTraceEvent) {
            return ((MarkupTraceEvent.DocumentStartTraceEvent)event).getLine();
        }
        if (event instanceof MarkupTraceEvent.DocumentEndTraceEvent) {
            return ((MarkupTraceEvent.DocumentEndTraceEvent)event).getLine();
        }

        throw new IllegalStateException("Unrecognized event class: " + event.getClass().getName());

    }


    private static int computeFirstCol(final MarkupTraceEvent event) {

        if (event instanceof MarkupTraceEvent.XmlDeclarationTraceEvent) {
            return ((MarkupTraceEvent.XmlDeclarationTraceEvent)event).getKeywordCol();
        }
        if (event instanceof MarkupTraceEvent.DocTypeTraceEvent) {
            return ((MarkupTraceEvent.DocTypeTraceEvent)event).getKeywordCol();
        }
        if (event instanceof MarkupTraceEvent.CommentTraceEvent) {
            return ((MarkupTraceEvent.CommentTraceEvent)event).getCol();
        }
        if (event instanceof MarkupTraceEvent.TextTraceEvent) {
            return ((MarkupTraceEvent.TextTraceEvent)event).getCol();
        }
        if (event instanceof MarkupTraceEvent.ProcessingInstructionTraceEvent) {
            return ((MarkupTraceEvent.ProcessingInstructionTraceEvent)event).getTargetCol();
        }
        if (event instanceof MarkupTraceEvent.CDATASectionTraceEvent) {
            return ((MarkupTraceEvent.CDATASectionTraceEvent)event).getCol();
        }
        if (event instanceof MarkupTraceEvent.StandaloneElementStartTraceEvent) {
            return ((MarkupTraceEvent.StandaloneElementStartTraceEvent)event).getCol();
        }
        if (event instanceof MarkupTraceEvent.StandaloneElementEndTraceEvent) {
            return ((MarkupTraceEvent.StandaloneElementEndTraceEvent)event).getCol();
        }
        if (event instanceof MarkupTraceEvent.OpenElementStartTraceEvent) {
            return ((MarkupTraceEvent.OpenElementStartTraceEvent)event).getCol();
        }
        if (event instanceof MarkupTraceEvent.OpenElementEndTraceEvent) {
            return ((MarkupTraceEvent.OpenElementEndTraceEvent)event).getCol();
        }
        if (event instanceof MarkupTraceEvent.AutoOpenElementStartTraceEvent) {
            return ((MarkupTraceEvent.AutoOpenElementStartTraceEvent)event).getCol();
        }
        if (event instanceof MarkupTraceEvent.AutoOpenElementEndTraceEvent) {
            return ((MarkupTraceEvent.AutoOpenElementEndTraceEvent)event).getCol();
        }
        if (event instanceof MarkupTraceEvent.CloseElementStartTraceEvent) {
            return ((MarkupTraceEvent.CloseElementStartTraceEvent)event).getCol();
        }
        if (event instanceof MarkupTraceEvent.CloseElementEndTraceEvent) {
            return ((MarkupTraceEvent.CloseElementEndTraceEvent)event).getCol();
        }
        if (event instanceof MarkupTraceEvent.AutoCloseElementStartTraceEvent) {
            return ((MarkupTraceEvent.AutoCloseElementStartTraceEvent)event).getCol();
        }
        if (event instanceof MarkupTraceEvent.AutoCloseElementEndTraceEvent) {
            return ((MarkupTraceEvent.AutoCloseElementEndTraceEvent)event).getCol();
        }
        if (event instanceof MarkupTraceEvent.AttributeTraceEvent) {
            return ((MarkupTraceEvent.AttributeTraceEvent)event).getNameCol();
        }
        if (event instanceof MarkupTraceEvent.InnerWhiteSpaceTraceEvent) {
            return ((MarkupTraceEvent.InnerWhiteSpaceTraceEvent)event).getCol();
        }
        if (event instanceof MarkupTraceEvent.NonMinimizedStandaloneElementStartTraceEvent) {
            return ((MarkupTraceEvent.NonMinimizedStandaloneElementStartTraceEvent)event).getCol();
        }
        if (event instanceof MarkupTraceEvent.NonMinimizedStandaloneElementEndTraceEvent) {
            return ((MarkupTraceEvent.NonMinimizedStandaloneElementEndTraceEvent)event).getCol();
        }
        if (event instanceof MarkupTraceEvent.UnmatchedCloseElementStartTraceEvent) {
            return ((MarkupTraceEvent.UnmatchedCloseElementStartTraceEvent)event).getCol();
        }
        if (event instanceof MarkupTraceEvent.UnmatchedCloseElementEndTraceEvent) {
            return ((MarkupTraceEvent.UnmatchedCloseElementEndTraceEvent)event).getCol();
        }
        if (event instanceof MarkupTraceEvent.DocumentStartTraceEvent) {
            return ((MarkupTraceEvent.DocumentStartTraceEvent)event).getCol();
        }
        if (event instanceof MarkupTraceEvent.DocumentEndTraceEvent) {
            return ((MarkupTraceEvent.DocumentEndTraceEvent)event).getCol();
        }

        throw new IllegalStateException("Unrecognized event class: " + event.getClass().getName());

    }


    
    
    
    
    private ResultCompareUtils() {
        super();
    }
    
    
    
    
    private static class AttributeEventComparator implements Comparator<MarkupTraceEvent> {

        
        AttributeEventComparator() {
            super();
        }
        
        public int compare(final MarkupTraceEvent o1, final MarkupTraceEvent o2) {
            
            if (o1 == null) {
                return -1;
            }
            if (o2 == null) {
                return 1;
            }

            final MarkupTraceEvent.AttributeTraceEvent ao1 = (MarkupTraceEvent.AttributeTraceEvent) o1;
            final MarkupTraceEvent.AttributeTraceEvent ao2 = (MarkupTraceEvent.AttributeTraceEvent) o2;

            return ao1.getName().compareTo(ao2.getName());

        }
        
    }
    

    
    
    public static class ResultComparison {
        
        private final boolean result;
        private final String explanation;
        
        public ResultComparison(final boolean result, final String explanation) {
            super();
            this.result = result;
            this.explanation = explanation;
        }

        public boolean getResult() {
            return this.result;
        }

        public String getExplanation() {
            return this.explanation;
        }
        
    }
    
    
    
}
