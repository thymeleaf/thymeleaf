/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.templateparser;

import java.util.Set;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.engine.ITemplateHandler;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresource.ITemplateResource;


/**
 * <p>
 *   Interface to be implemented by al the parsers used for parsing templates at a {@link org.thymeleaf.TemplateEngine}.
 * </p>
 * <p>
 *   Implementations of this interface should be <strong>thread-safe</strong>.
 * </p>
 *
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public interface ITemplateParser {


    public void parseStandalone(
                    final IEngineConfiguration configuration,
                    final String ownerTemplate,
                    final String template,
                    final Set<String> templateSelectors,
                    final ITemplateResource resource,
                    final TemplateMode templateMode,
                    final boolean useDecoupledLogic,
                    final ITemplateHandler handler);


    public void parseString(
                    final IEngineConfiguration configuration,
                    final String ownerTemplate,
                    final String template,
                    final int lineOffset, final int colOffset,
                    final TemplateMode templateMode,
                    final ITemplateHandler handler);

}
