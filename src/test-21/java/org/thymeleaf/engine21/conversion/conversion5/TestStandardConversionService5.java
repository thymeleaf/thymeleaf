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
package org.thymeleaf.engine21.conversion.conversion5;

import org.thymeleaf.Configuration;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.standard.expression.AbstractStandardConversionService;


public class TestStandardConversionService5 extends AbstractStandardConversionService {


    public TestStandardConversionService5() {
        super();
    }


    @Override
    protected String convertToString(final Configuration configuration, final IProcessingContext processingContext, final Object object) {
        return "[" + super.convertToString(configuration, processingContext, object) + "]";
    }

}
