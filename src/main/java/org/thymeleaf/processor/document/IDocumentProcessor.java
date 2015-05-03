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
package org.thymeleaf.processor.document;

import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.engine.IDocumentStructureHandler;
import org.thymeleaf.model.IDocumentEnd;
import org.thymeleaf.model.IDocumentStart;
import org.thymeleaf.processor.IProcessor;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public interface IDocumentProcessor extends IProcessor {

    public void processDocumentStart(
            final ITemplateProcessingContext processingContext,
            final IDocumentStart documentStart,
            final IDocumentStructureHandler structureHandler);

    public void processDocumentEnd(
            final ITemplateProcessingContext processingContext,
            final IDocumentEnd documentEnd,
            final IDocumentStructureHandler structureHandler);

}
