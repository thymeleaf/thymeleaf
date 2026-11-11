/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2026 Thymeleaf (http://www.thymeleaf.org)
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
package org.thymeleaf.spring6.integration;

public class FormBean {

    private String name;
    private Integer age;
    private Code code;
    private boolean active;
    private String color;

    public FormBean() {
        super();
    }

    public static FormBean initial() {
        final FormBean formBean = new FormBean();
        formBean.setName("Ann");
        formBean.setAge(Integer.valueOf(30));
        formBean.setCode(new Code("abc"));
        formBean.setActive(true);
        formBean.setColor("green");
        return formBean;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Integer getAge() {
        return this.age;
    }

    public void setAge(final Integer age) {
        this.age = age;
    }

    public Code getCode() {
        return this.code;
    }

    public void setCode(final Code code) {
        this.code = code;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(final String color) {
        this.color = color;
    }

}
