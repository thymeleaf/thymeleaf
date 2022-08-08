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
package org.thymeleaf.templateengine.conversion.conversion7;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.thymeleaf.util.DateUtils;

public class Conversion7Bean {

    @DateTimeFormat(pattern = "yyyy_MM_dd")
    private Date date = DateUtils.create(2020,1,1).getTime();

    private Conversion7InnerBean inner = new Conversion7InnerBean();


    public Conversion7Bean() {
        super();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(final Date date) {
        this.date = date;
    }

    public Conversion7InnerBean getInner() {
        return inner;
    }

    public void setInner(final Conversion7InnerBean inner) {
        this.inner = inner;
    }

}
