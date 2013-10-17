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

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;


public class Conversion3Object {

    private Long id = null;
    private Integer age = null;
    private String name = null;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Conversion3Date date = null;


    public Conversion3Object() {
        super();
    }


    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Integer getAge() {
        return this.age;
    }

    public void setAge(final Integer age) {
        this.age = age;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Conversion3Date getDate() {
        return this.date;
    }

    public void setDate(final Date date) {
        this.date = new Conversion3Date(date);
    }

}
