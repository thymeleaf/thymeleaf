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
package org.thymeleaf.engine21.springintegration.model;

import java.util.Date;

public class FormBean01 {

    private Integer id = null;
    private String name = null;
    private Date date = null;
    
    
    public FormBean01() {
        super();
    }


    public Integer getId() {
        return this.id;
    }


    public void setId(final Integer id) {
        this.id = id;
    }


    public String getName() {
        return this.name;
    }

    public Date getDate() {
        return this.date;
    }


    public void setName(final String name) {
        this.name = name;
    }

    public void setDate(final Date date) {
        this.date = date;
    }


    @Override
    public String toString() {
        return "FormBean01 [name=" + this.name + ", date=" + this.date + "]";
    }
    
}
