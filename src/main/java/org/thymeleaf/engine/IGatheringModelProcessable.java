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
package org.thymeleaf.engine;

import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IComment;
import org.thymeleaf.model.IDocType;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IProcessingInstruction;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.model.IText;
import org.thymeleaf.model.IXMLDeclaration;


/*
 *
 * @author Daniel Fernandez
 * @since 3.0.0
 *
 */
interface IGatheringModelProcessable extends IEngineProcessable {

    boolean isGatheringFinished();

    Model getInnerModel();

    void resetGatheredSkipFlags();

    ProcessorExecutionVars initializeProcessorExecutionVars();

    void gatherText(final IText text);
    void gatherComment(final IComment comment);
    void gatherCDATASection(final ICDATASection cdataSection);
    void gatherStandaloneElement(final IStandaloneElementTag standaloneElementTag);
    void gatherOpenElement(final IOpenElementTag openElementTag);
    void gatherCloseElement(final ICloseElementTag closeElementTag);
    void gatherUnmatchedCloseElement(final ICloseElementTag closeElementTag);
    void gatherDocType(final IDocType docType);
    void gatherXMLDeclaration(final IXMLDeclaration xmlDeclaration);
    void gatherProcessingInstruction(final IProcessingInstruction processingInstruction);

}