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
package org.thymeleaf.testing.templateengine.resource;

import org.thymeleaf.testing.templateengine.util.EscapeUtils;
import org.thymeleaf.util.Validate;





public class StringTestResource 
        extends AbstractTestResource implements ITestResourceItem {

    private final String TYPE = "classpath";
    
    private final String str;
    
    
    public StringTestResource(final String name, final String str) {
        super(name);
        Validate.notNull(name, "Name cannot be null");
        Validate.notNull(str, "Resource string cannot be null");
        this.str = str;
    }
    
    
    public String getType() {
        return this.TYPE;
    }
    
    public String readAsText() {
        return EscapeUtils.unescapeUnicode(this.str);
    }
    
}
