/*
 * =============================================================================
 * 
 *   Copyright (c) 2011, The THYMELEAF team (http://www.thymeleaf.org)
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
package thymeleafexamples.extrathyme.business.entities;

import java.util.Calendar;



public class Headline 
        implements Comparable<Headline> {

    private final Calendar date;
    private final String text;
    
    
    
    public Headline(final Calendar date, final String text) {
        super();
        this.date = date;
        this.text = text;
    }



    public Calendar getDate() {
        return this.date;
    }


    public String getText() {
        return this.text;
    }



    public int compareTo(final Headline o) {
        if (o == null) {
            return 1;
        }
        return this.date.compareTo(o.date);
    }
    

    
}
