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
package org.thymeleaf.standard.processor.attr;

/**
 * Iterates through a collection of objects, repeating each of the child
 * elements for every object in that collection, eg:
 * <p>
 * &lt;tr th:each="prod : ${prods}"&gt;<br/>
 * &nbsp; &lt;td th:text="${prod.name}"&gt;Product name&lt;/td&gt;<br/>
 * &nbsp; &lt;td th:text="${prod.price}"&gt;Product price&lt;/td&gt;<br/>
 * &lt;/tr&gt;
 * <p>
 * That "prod : ${prods}" attribute value you see above means "for each element
 * in the result of evaluating ${prods}, repeat this fragment of template
 * setting that element into a variable called prod".
 * <p>
 * A collection can be any <tt>java.util.Iterable</tt>, a <tt>java.util.Map</tt>
 * (iteration variables will be a <tt>java.util.Map.Entry</tt>), or an array.
 * 
 * @author Daniel Fern&aacute;ndez
 * @since 1.0
 */
public final class StandardEachAttrProcessor
        extends AbstractStandardIterationAttrProcessor {

    
    public static final int ATTR_PRECEDENCE = 200;
    public static final String ATTR_NAME = "each";

    
    
    
    public StandardEachAttrProcessor() {
        super(ATTR_NAME);
    }



    @Override
    public int getPrecedence() {
        return ATTR_PRECEDENCE;
    }
    
    
    
}
