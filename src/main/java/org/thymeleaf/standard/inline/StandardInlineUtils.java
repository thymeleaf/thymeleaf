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
package org.thymeleaf.standard.inline;

import java.util.ArrayList;
import java.util.List;

import org.thymeleaf.model.IText;
import org.thymeleaf.util.AggregateCharSequence;

/*
 *
 * @author Daniel Fernandez
 * @since 3.0.0
 * 
 */
final class StandardInlineUtils {



    static boolean mightNeedInlining(final CharSequence text) {

        int n = text.length();

        char c;
        while (n-- != 0) {

            c = text.charAt(n);
            if (c == ']' && n > 0) {
                c = text.charAt(n - 1);
                if (c == ']' || c == ')') {
                    // There probably is some kind of [[...]] or [(...)] inlined expression
                    return true;
                }
            }

        }

        return false;

    }



    static CharSequence performInlining(final CharSequence text) {

        final int[] locator =
                (text instanceof IText)?
                    new int[] { ((IText)text).getLine(), ((IText)text).getCol() } :
                    new int[] { Integer.MIN_VALUE, Integer.MIN_VALUE }; // Negative (line,col) will mean 'no locator'

        List<CharSequence> textFragments = null;

        int i = 0;
        int current = i;
        int maxi = text.length();

        int expStart, expEnd;
        int currentLine, currentCol;
        char innerClosingChar;

        while (i < maxi) {

            expStart = findNextStructureStart(text, i, maxi, locator);
            if (expStart == -1) {

                if (textFragments == null) {
                    return text;
                }

            }

            current = expStart;
            currentLine = locator[0];
            currentCol = locator[1];

            // The inner closing char we will be looking for will depend on the type of expression we just found
            innerClosingChar = text.charAt(expStart + 1) == '[' ? ']' : ')';

            expEnd = findNextStructureEndAvoidQuotes(text, expStart + 2, maxi, innerClosingChar, locator);
            if (expEnd == -1) {

                if (textFragments == null) {
                    return text;
                }

            }

        }



        final List<CharSequence> textFragments = new ArrayList<CharSequence>(4);

        textFragments.add("$$$");
        textFragments.add("INLINED");
        textFragments.add("$$$");

        return new AggregateCharSequence(textFragments);

    }







    static int findNextStructureStart(
            final CharSequence text, final int offset, final int maxi,
            final int[] locator) {

        char c;

        int colIndex = offset;

        int i = offset;
        int n = (maxi - offset);

        while (n-- != 0) {

            c = text.charAt(i);

            if (c == '\n') {
                colIndex = i;
                locator[1] = 0;
                locator[0]++;
            } else if (c == '[' && n > 0) {
                c = text.charAt(i + 1);
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




    static int findNextStructureEndAvoidQuotes(
            final CharSequence text, final int offset, final int maxi,
            final char innerClosingChar, final int[] locator) {

        boolean inQuotes = false;
        boolean inApos = false;

        char c;

        int colIndex = offset;

        int i = offset;
        int n = (maxi - offset);

        while (n-- != 0) {

            c = text.charAt(i);

            if (c == '\n') {
                colIndex = i;
                locator[1] = 0;
                locator[0]++;
            } else if (c == '"' && !inApos) {
                inQuotes = !inQuotes;
            } else if (c == '\'' && !inQuotes) {
                inApos = !inApos;
            } else if (c == innerClosingChar && !inQuotes && !inApos && n > 0) {
                c = text.charAt(i + 1);
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





    private StandardInlineUtils() {
        super();
    }


}
