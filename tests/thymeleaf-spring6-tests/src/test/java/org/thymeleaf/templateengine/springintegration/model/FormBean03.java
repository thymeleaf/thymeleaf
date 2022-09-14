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
package org.thymeleaf.templateengine.springintegration.model;

public class FormBean03 {

    private Boolean nullBool = null;
    private Boolean notNullTrue = Boolean.TRUE;
    private Boolean notNullFalse = Boolean.FALSE;

    private FormEnum01 choice = null;


    public FormBean03() {
        super();
    }

    public FormEnum01 getChoice() {
        return choice;
    }

    public void setChoice(final FormEnum01 choice) {
        this.choice = choice;
    }

    public Boolean getNullBool() {
        return nullBool;
    }

    public void setNullBool(final Boolean nullBool) {
        this.nullBool = nullBool;
    }

    public Boolean getNotNullTrue() {
        return notNullTrue;
    }

    public void setNotNullTrue(final Boolean notNullTrue) {
        this.notNullTrue = notNullTrue;
    }

    public Boolean getNotNullFalse() {
        return notNullFalse;
    }

    public void setNotNullFalse(final Boolean notNullFalse) {
        this.notNullFalse = notNullFalse;
    }


    @Override
    public String toString() {
        return "FormBean03{" +
                "nullBool=" + nullBool +
                ", notNullTrue=" + notNullTrue +
                ", notNullFalse=" + notNullFalse +
                '}';
    }

}
