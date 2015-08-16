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
package org.thymeleaf.standard.util;

import org.unbescape.css.CssEscape;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class StandardCSSUtils {





    public static String escapeString(final String str) {
        return CssEscape.escapeCssString(str);
    }




    public static String print(final Object object) {
        final StringBuilder output = new StringBuilder();
        print(output, object);
        return output.toString();
    }


    public static String printString(final String str) {
        final StringBuilder output = new StringBuilder();
        if (str == null) {
            printNull(output);
        } else {
            printString(output, str);
        }
        return output.toString();
    }

    public static String printNumber(final Number number) {
        final StringBuilder output = new StringBuilder();
        if (number == null) {
            printNull(output);
        } else {
            printNumber(output, number);
        }
        return output.toString();
    }

    public static String printBoolean(final Boolean bool) {
        final StringBuilder output = new StringBuilder();
        if (bool == null) {
            printNull(output);
        } else {
            printBoolean(output, bool);
        }
        return output.toString();
    }







    private static void print(final StringBuilder output, final Object object) {
        if (object == null) {
            printNull(output);
            return;
        }
        if (object instanceof CharSequence) {
            printString(output, object.toString());
            return;
        }
        if (object instanceof Character) {
            printString(output, object.toString());
            return;
        }
        if (object instanceof Number) {
            printNumber(output, (Number) object);
            return;
        }
        if (object instanceof Boolean) {
            printBoolean(output, (Boolean) object);
            return;
        }
        printString(output, object.toString());
    }


    private static void printNull(final StringBuilder output) {
        output.append(""); // There isn't really a 'null' token in CSS
    }


    private static void printString(final StringBuilder output, final String str) {
        output.append('\'');
        output.append(CssEscape.escapeCssString(str));
        output.append('\'');
    }


    private static void printNumber(final StringBuilder output, final Number number) {
        output.append(number.toString());
    }


    private static void printBoolean(final StringBuilder output, final Boolean bool) {
        output.append(bool.toString());
    }








    private StandardCSSUtils() {
        super();
    }



}
