/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.extras.springsecurity4.dialect;

import java.util.LinkedHashSet;
import java.util.Set;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IExpressionObjectsDialect;
import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;
import org.thymeleaf.extras.springsecurity4.dialect.expression.SpringSecurityExpressionObjectFactory;
import org.thymeleaf.extras.springsecurity4.dialect.processor.AuthenticationAttrProcessor;
import org.thymeleaf.extras.springsecurity4.dialect.processor.AuthorizeAclAttrProcessor;
import org.thymeleaf.extras.springsecurity4.dialect.processor.AuthorizeAttrProcessor;
import org.thymeleaf.extras.springsecurity4.dialect.processor.AuthorizeUrlAttrProcessor;
import org.thymeleaf.processor.IProcessor;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 */
public class SpringSecurityDialect
        extends AbstractDialect implements IProcessorDialect, IExpressionObjectsDialect {

    public static final String DEFAULT_PREFIX = "sec";

    public static final IExpressionObjectFactory EXPRESSION_OBJECT_FACTORY = new SpringSecurityExpressionObjectFactory();
    


    public SpringSecurityDialect() {
        super("SpringSecurity");
    }

    
    
    public String getPrefix() {
        return DEFAULT_PREFIX;
    }





    
    public Set<IProcessor> getProcessors(final String dialectPrefix) {
        final Set<IProcessor> processors = new LinkedHashSet<IProcessor>();
        processors.add(new AuthenticationAttrProcessor(dialectPrefix));
        // synonym (sec:authorize = sec:authorize-expr) for similarity with
        // "authorize-url" and "autorize-acl"
        processors.add(new AuthorizeAttrProcessor(dialectPrefix, AuthorizeAttrProcessor.ATTR_NAME));
        processors.add(new AuthorizeAttrProcessor(dialectPrefix, AuthorizeAttrProcessor.ATTR_NAME_EXPR));
        processors.add(new AuthorizeUrlAttrProcessor(dialectPrefix));
        processors.add(new AuthorizeAclAttrProcessor(dialectPrefix));
        return processors;
    }





    public IExpressionObjectFactory getExpressionObjectFactory() {
        return EXPRESSION_OBJECT_FACTORY;
    }
    
    
}
