/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2022, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.spring.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.spring6.view.ThymeleafView;

public class TestThymeleafView extends ThymeleafView {

    @Autowired
    private ViewBean viewBean;
    private String something;


    public TestThymeleafView() {
        super();
    }

    public ViewBean getViewBean() {
        return viewBean;
    }

    public void setViewBean(final ViewBean viewBean) {
        this.viewBean = viewBean;
    }

    public String getSomething() {
        return something;
    }

    public void setSomething(final String something) {
        this.something = something;
    }

    public static class ViewBean {

        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(final String value) {
            this.value = value;
        }

    }

}
