/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2013, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.engine21.conversion.conversion3;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.springframework.format.Formatter;


public class CalendarFormatter implements Formatter<Calendar> {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM(dd)");


    public CalendarFormatter() {
        super();
    }



    public Calendar parse(String text, Locale locale) throws ParseException {
        synchronized (SDF) {
            final Date date = SDF.parse(text);
            final Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(date.getTime());
            return cal;
        }
    }

    public String print(Calendar object, Locale locale) {
        synchronized (SDF) {
            return SDF.format(object.getTime());
        }
    }
}
