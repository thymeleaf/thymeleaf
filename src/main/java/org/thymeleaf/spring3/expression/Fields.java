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
package org.thymeleaf.spring3.expression;

import java.util.List;

import org.thymeleaf.Arguments;
import org.thymeleaf.spring3.util.FieldUtils;
import org.thymeleaf.templateresolver.TemplateResolution;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public class Fields {

    private final Arguments arguments;
    private final TemplateResolution templateResolution;
    
    
    
    public boolean hasErrors(final String field) {
        return FieldUtils.hasErrors(this.arguments, this.templateResolution, field);
    }
    
    
    public List<String> errors(final String field) {
        return FieldUtils.errors(this.arguments, this.templateResolution, field);
    }

    
    public String idFromName(final String fieldName) {
        return FieldUtils.idFromName(fieldName);
    }
    

    
	public Fields(final Arguments arguments, final TemplateResolution templateResolution) {
	    
	    super();
	    
	    this.arguments = arguments;
	    this.templateResolution = templateResolution;
	    
	}

}
