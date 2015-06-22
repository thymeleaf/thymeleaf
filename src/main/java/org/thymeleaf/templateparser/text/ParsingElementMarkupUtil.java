/*
 * =============================================================================
 * 
 *   Copyright (c) 2012-2014, The ATTOPARSER team (http://www.attoparser.org)
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
package org.thymeleaf.templateparser.text;


import org.attoparser.ParseException;

/*
 * Class containing utility methods for parsing elements (tags).
 *
 * @author Daniel Fernandez
 * @since 3.0.0
 */
final class ParsingElementMarkupUtil {


    

    
    private ParsingElementMarkupUtil() {
        super();
    }




    static void parseStandaloneElement(
            final char[] buffer,
            final int contentOffset, final int contentLen,
            @SuppressWarnings("unused") final int outerOffset, @SuppressWarnings("unused") final int outerLen,
            final int line, final int col,
            final MarkupEventProcessor eventProcessor)
            throws ParseException {

        final int maxi = contentOffset + contentLen;

        final int[] locator = new int[] {line, col + 1};

        /*
         * Extract the element name first
         */

        final int elementNameEnd =
            ParsingMarkupUtil.findNextWhitespaceCharWildcard(buffer, contentOffset, maxi, true, locator);

        if (elementNameEnd == -1) {
            // The buffer only contains the element name

            eventProcessor.processStandaloneElementStart(
                    buffer, contentOffset, contentLen,
                    true, line, col);

            eventProcessor.processStandaloneElementEnd(
                    buffer, contentOffset, contentLen,
                    true, locator[0], locator[1]);

            return;

        }


        eventProcessor.processStandaloneElementStart(
                buffer, contentOffset, (elementNameEnd - contentOffset),
                true, line, col);


        // This parseAttributeSequence will take care of calling handleInnerWhitespace when appropriate.
        ParsingAttributeSequenceUtil.parseAttributeSequence(
                buffer, elementNameEnd, maxi - elementNameEnd, locator, eventProcessor);


        eventProcessor.processStandaloneElementEnd(
                buffer, contentOffset, (elementNameEnd - contentOffset),
                true, locator[0], locator[1]);

    }




    static void parseOpenElement(
            final char[] buffer,
            final int contentOffset, final int contentLen,
            @SuppressWarnings("unused") final int outerOffset, @SuppressWarnings("unused") final int outerLen,
            final int line, final int col,
            final MarkupEventProcessor eventProcessor)
            throws ParseException {

        final int maxi = contentOffset + contentLen;

        final int[] locator = new int[] {line, col + 1};

        /*
         * Extract the element name first
         */

        final int elementNameEnd =
                ParsingMarkupUtil.findNextWhitespaceCharWildcard(buffer, contentOffset, maxi, true, locator);

        if (elementNameEnd == -1) {
            // The buffer only contains the element name

            eventProcessor.processOpenElementStart(
                    buffer, contentOffset, contentLen,
                    line, col);

            eventProcessor.processOpenElementEnd(
                    buffer, contentOffset, contentLen,
                    locator[0], locator[1]);

            return;

        }


        eventProcessor.processOpenElementStart(
                buffer, contentOffset, (elementNameEnd - contentOffset),
                line, col);


        // This parseAttributeSequence will take care of calling handleInnerWhitespace when appropriate.
        ParsingAttributeSequenceUtil.parseAttributeSequence(
                buffer, elementNameEnd, maxi - elementNameEnd, locator, eventProcessor);


        eventProcessor.processOpenElementEnd(
                buffer, contentOffset, (elementNameEnd - contentOffset),
                locator[0], locator[1]);

    }




    static void parseCloseElement(
            final char[] buffer,
            final int contentOffset, final int contentLen,
            final int outerOffset, final int outerLen,
            final int line, final int col,
            final MarkupEventProcessor eventProcessor)
            throws ParseException {

        final int maxi = contentOffset + contentLen;

        final int[] locator = new int[] {line, col + 2};

        /*
         * Extract the element name first
         */

        final int elementNameEnd =
            ParsingMarkupUtil.findNextWhitespaceCharWildcard(buffer, contentOffset, maxi, true, locator);

        if (elementNameEnd == -1) {
            // The buffer only contains the element name

            eventProcessor.processCloseElementStart(
                    buffer, contentOffset, contentLen,
                    line, col);

            eventProcessor.processCloseElementEnd(
                    buffer, contentOffset, contentLen,
                    locator[0], locator[1]);

            return;

        }


        eventProcessor.processCloseElementStart(
                buffer, contentOffset, (elementNameEnd - contentOffset),
                line, col);


        final int wsEnd =
            ParsingMarkupUtil.findNextNonWhitespaceCharWildcard(buffer, elementNameEnd, maxi, locator);
        
        if (wsEnd != -1) {
            // This is a close tag, so everything should be whitespace
            // until the end of the close tag
            throw new ParseException(
                    "Could not parse as a well-formed closing element " +
                    "\"" + new String(buffer, outerOffset, outerLen) + "\"" +
                    ": No attributes are allowed here", line, col);
        }


        // Inner whitespace is not reported in this case


        eventProcessor.processCloseElementEnd(
                buffer, contentOffset, (elementNameEnd - contentOffset),
                locator[0], locator[1]);

    }
    
    

    
    
    
    static boolean isOpenElementStart(final char[] buffer, final int offset, final int maxi) {
        
        final int len = maxi - offset;
        
        return (len > 1 && 
                    buffer[offset] == '<' &&
                    isElementName(buffer, offset + 1, maxi));
        
    }

    
    static boolean isCloseElementStart(final char[] buffer, final int offset, final int maxi) {
        
        final int len = maxi - offset;
        
        return (len > 2 && 
                    buffer[offset] == '<' &&
                    buffer[offset + 1] == '/' &&
                    isElementName(buffer, offset + 2, maxi));
        
    }


    
    
    private static boolean isElementName(final char[] buffer, final int offset, final int maxi) {
        
        final int len = maxi - offset;
        
        if (len > 1 && buffer[offset] == '!') {
            if (len > 8) {
                return (buffer[offset + 1] != '-' && buffer[offset + 1] != '!' && 
                        buffer[offset + 1] != '/' && buffer[offset + 1] != '?' && 
                        buffer[offset + 1] != '[' &&
                        !((buffer[offset + 1] == 'D' || buffer[offset + 1] == 'd') && 
                          (buffer[offset + 2] == 'O' || buffer[offset + 2] == 'o') && 
                          (buffer[offset + 3] == 'C' || buffer[offset + 3] == 'c') && 
                          (buffer[offset + 4] == 'T' || buffer[offset + 4] == 't') && 
                          (buffer[offset + 5] == 'Y' || buffer[offset + 5] == 'y') && 
                          (buffer[offset + 6] == 'P' || buffer[offset + 6] == 'p') && 
                          (buffer[offset + 7] == 'E' || buffer[offset + 7] == 'e') &&
                          (Character.isWhitespace(buffer[offset + 8]) || buffer[offset + 8] == '>'))); 
            }
            return (buffer[offset + 1] != '-' && buffer[offset + 1] != '!' && 
                    buffer[offset + 1] != '/' && buffer[offset + 1] != '?' && 
                    buffer[offset + 1] != '[' && !Character.isWhitespace(buffer[offset + 1])); 
        }
        return (len > 0 &&
                buffer[offset] != '-' && buffer[offset] != '!' && 
                buffer[offset] != '/' && buffer[offset] != '?' && 
                buffer[offset] != '[' && !Character.isWhitespace(buffer[offset]));
        
    }







    
}
