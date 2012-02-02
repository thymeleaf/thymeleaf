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
package org.thymeleaf;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 * 
 * @deprecated This enum has been deprecated as of 2.0.0. Template modes are now specified
 *             as Strings instead of enums (for enhanced extensibility). See
 *             {@link org.thymeleaf.templateresolver.TemplateResolver#setTemplateMode(String)}.
 *
 */
@Deprecated
public enum TemplateMode {
    
    XML,
    VALIDXML,
    XHTML,
    VALIDXHTML,
    LEGACYHTML5,
    HTML5;
    
}
