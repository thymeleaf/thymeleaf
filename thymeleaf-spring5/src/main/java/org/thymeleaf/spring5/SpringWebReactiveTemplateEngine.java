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
package org.thymeleaf.spring5;

import org.thymeleaf.spring5.context.reactive.SpringWebReactiveEngineContextFactory;
import org.thymeleaf.spring5.linkbuilder.reactive.SpringWebReactiveLinkBuilder;


/**
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.3
 *
 */
public class SpringWebReactiveTemplateEngine
        extends SpringTemplateEngine
        implements ISpringWebReactiveTemplateEngine {


    public SpringWebReactiveTemplateEngine() {

        super();
        // In Spring Web Reactive environments, we will need to use a special context factory in order to
        // use an environment-tailored implementation of IEngineContext.
        this.setEngineContextFactory(new SpringWebReactiveEngineContextFactory());
        // In Spring Web Reactive environments, we will need to use a special link builder able to adapt
        // the creation of URLs as a result of @{...} expressions in a way that makes sense in this
        // environment.
        this.setLinkBuilder(new SpringWebReactiveLinkBuilder());

    }


}
