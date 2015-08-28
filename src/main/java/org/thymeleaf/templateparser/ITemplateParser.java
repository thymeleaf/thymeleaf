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
package org.thymeleaf.templateparser;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.engine.ITemplateHandler;
import org.thymeleaf.resource.IResource;
import org.thymeleaf.templatemode.TemplateMode;


/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public interface ITemplateParser {


    public void parseStandalone(
                      final IEngineConfiguration configuration,
                      final ParsableArtifactType artifactType,
                      final IResource resource,
                      final String[] selectors,
                      final TemplateMode templateMode,
                      final ITemplateHandler handler);


    public void parseNested(
                      final IEngineConfiguration configuration,
                      final ParsableArtifactType artifactType,
                      final String ownerTemplate,
                      final IResource resource,
                      final int lineOffset, final int colOffset,
                      final TemplateMode templateMode,
                      final ITemplateHandler handler);

}
