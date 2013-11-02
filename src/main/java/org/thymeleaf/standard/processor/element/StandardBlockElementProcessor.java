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
package org.thymeleaf.standard.processor.element;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.element.AbstractNoOpElementProcessor;

/**
 * An attribute container that allows template developers to specify whichever
 * attributes they want, executes them, and then simply dissapears without a
 * trace.
 * 
 * @author Daniel Fern&aacute;ndez
 * @since 2.1.0
 */
public final class StandardBlockElementProcessor
        extends AbstractNoOpElementProcessor {

    public static final int ATTR_PRECEDENCE = 100000;
    public static final String ELEMENT_NAME = "block";




    public StandardBlockElementProcessor() {
        super(ELEMENT_NAME);
    }

    
    @Override
    public int getPrecedence() {
        return ATTR_PRECEDENCE;
    }


    @Override
    protected boolean removeHostElement(final Arguments arguments, final Element element) {
        return true;
    }
}
