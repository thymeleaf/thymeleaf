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
package org.thymeleaf;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public enum TemplateMode {
    
    XML("XML", false, false, true, false, false),
    VALIDXML("VALIDXML", true, false, true, false, false),
    XHTML("XHTML", false, true, false, true, false),
    VALIDXHTML("VALIDXHTML", true, true, false, true, false),
    LEGACYHTML5("LEGACYHTML5", false, true, false, false, true),
    HTML5("HTML5", false, true, false, false, true);
    

    private String name;
    private boolean validating;
    private boolean web;
    private boolean xml;
    private boolean xhtml;
    private boolean html5;
    
    
    TemplateMode(
            final String name,
            final boolean validating, final boolean web,
            final boolean xml, final boolean xhtml, final boolean html5) {
        this.name = name;
        this.validating = validating;
        this.web = web;
        this.xml = xml;
        this.xhtml = xhtml;
        this.html5 = html5;
    }

    
    public String getName() {
        return this.name;
    }
    
    public boolean isValidating() {
        return this.validating;
    }
    
    public boolean isWeb() {
        return this.web;
    }

    public boolean isXML() {
        return this.xml;
    }

    public boolean isXHTML() {
        return this.xhtml;
    }

    public boolean isHTML5() {
        return this.html5;
    }
    
    
    @Override
    public String toString() {
        return this.name;
    }
    
}
