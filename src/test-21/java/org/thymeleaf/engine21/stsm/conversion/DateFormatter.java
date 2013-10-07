/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.engine21.stsm.conversion;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.springframework.format.Formatter;


public class DateFormatter implements Formatter<Date> {

    private static final SimpleDateFormat SDF;


    static {
        SDF = new SimpleDateFormat("MM/dd/yyyy");
        SDF.setLenient(false);
    }


    public DateFormatter() {
        super();
    }



    public Date parse(final String text, final Locale locale) throws ParseException {
        synchronized (SDF) {
            return SDF.parse(text);
        }
    }

    public String print(final Date object, final Locale locale) {
        synchronized (SDF) {
            return (object == null? "" : SDF.format(object));
        }
    }
}
