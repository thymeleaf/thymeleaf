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
package thymeleafexamples.sayhello.dialect;

import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.processor.attr.IAttrProcessor;

public class HelloDialect extends AbstractDialect {

    public HelloDialect() {
        super();
    }
    
    /*
     * All of this dialect's attributes and/or tags
     * will start with 'hello:*'
     */
    public String getPrefix() {
        return "hello";
    }

    
    /*
     * Non-lenient: if a tag starting with 'hello:' is
     * found but no processor exists in this dialect for it,
     * throw an exception. 
     */
    public boolean isLenient() {
        return false;
    }

    
    /*
     * The attribute processors.
     */
    @Override
    public Set<IAttrProcessor> getAttrProcessors() {
        final Set<IAttrProcessor> attrProcessors = new HashSet<IAttrProcessor>();
        attrProcessors.add(new SayToAttrProcessor());
        attrProcessors.add(new SayToPlanetAttrProcessor());
        return attrProcessors;
    }


}
