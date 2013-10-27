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
package org.thymeleaf.standard.processor.attr;

/**
 * Lets you use expressions directly in your template.
 * <p>
 * If this attribute's value is <b>text</b>, then you can use the [[...]] syntax
 * to put expressions within your text without having to use the <tt>th:text</tt>
 * attribute processor, eg:
 * <p>
 * &lt;p th:inline="text"&gt;Hello [[${session.user.name}]]!&lt;p&gt;
 * <p>
 * If this attribute's value is <b>javascript</b> or <b>dart</b>, and used in a
 * &lt;script&gt; tag, then you can insert expressions directly into your
 * JavaScript/Dart code with the <tt>/*[[...]]*&#47;</tt>, <tt>/*[+...+]*&#47;</tt>,
 * and <tt>/*[-...-]*&#47;</tt> syntaxes.
 * 
 * @author Daniel Fern&aacute;ndez
 * @since 1.0
 */
public final class StandardInlineAttrProcessor 
        extends AbstractStandardTextInlinerAttrProcessor {

    public static final int ATTR_PRECEDENCE = 1000;
    public static final String ATTR_NAME = "inline";

    
    
    
    public StandardInlineAttrProcessor() {
        super(ATTR_NAME);
    }

    

    @Override
    public int getPrecedence() {
        return ATTR_PRECEDENCE;
    }
    

    
}
