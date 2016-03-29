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
package org.thymeleaf.engine;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IComment;
import org.thymeleaf.model.IDocType;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IProcessingInstruction;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.model.IText;
import org.thymeleaf.model.IXMLDeclaration;
import org.thymeleaf.templatemode.TemplateMode;


/*
 * This is an internal class for gathering a sequence of template events into a Model object. This will
 * be used from ProcessorTemplateHandler for gathering iterated sequences, as well as models that are to be
 * passed to ElementModelProcessors.
 * 
 * NOTE there is no need to implement ITemplateHandler or extend AbstractTemplateHandler.
 *
 * @author Daniel Fernandez
 * @since 3.0.0
 *
 */
final class ProcessorTemplateHandlerModelGatherer {

    enum GatheringType { ITERATION, MODEL, NONE }

    private final IEngineConfiguration configuration;
    private final TemplateMode templateMode;

    private boolean gathering;
    private boolean finished;

    private Model model;
    private GatheringType gatheringType;
    private int modelLevel;


    ProcessorTemplateHandlerModelGatherer(final IEngineConfiguration configuration, final TemplateMode templateMode) {
        super();
        this.configuration = configuration;
        this.templateMode = templateMode;
        reset();
    }


    void reset() {
        this.gatheringType = null;
        this.modelLevel = -1;
        this.gathering = false;
        this.finished = false;
        // TODO Once gathering of model for openelement->modelprocessor is refactored, we should probably be able to set model to null here
        // TODO Once model is set to null, we can change the checks below to, instead of this.modelLevel < 0, this.model != null
    }


    void init(final GatheringType gatheringType) {
        this.gatheringType = gatheringType;
        this.modelLevel = 0;
        // We create a new model for each gathering operation, so that this gatherer can be used for nested executions
        this.model = new Model(this.configuration, this.templateMode);
        this.gathering = true;
        this.finished = false;
    }


    boolean isFinished() {
        return this.finished;
    }


    GatheringType getGatheringType() {
        return this.gatheringType;
    }


    Model getModel() {
        return this.model;
    }



    boolean gatherText(final IText text) {
        if (!this.gathering) {
            return false;
        }
        this.model.add(text);
        return true;
    }


    boolean gatherComment(final IComment comment) {
        if (!this.gathering) {
            return false;
        }
        this.model.add(comment);
        return true;
    }


    boolean gatherCDATASection(final ICDATASection cdataSection) {
        if (!this.gathering) {
            return false;
        }
        this.model.add(cdataSection);
        return true;
    }


    boolean gatherStandaloneElement(final IStandaloneElementTag standaloneElementTag) {
        if (!this.gathering) {
            return false;
        }
        this.model.add(standaloneElementTag);
        if (this.model.size() == 1) {
            // This was the fist event and it is a standalone, so we consider gathering done
            this.finished = true;
        }
        return true;
    }


    boolean gatherOpenElement(final IOpenElementTag openElementTag) {
        if (!this.gathering) {
            return false;
        }
        this.model.add(openElementTag);
        this.modelLevel++;
        return true;
    }


    boolean gatherCloseElement(final ICloseElementTag closeElementTag) {
        if (!this.gathering) {
            return false;
        }
        this.model.add(closeElementTag);
        if (!closeElementTag.isUnmatched()) {
            this.modelLevel--;
            if (this.modelLevel == 0) {
                // OK, we are finished gathering, this close tag ends the process
                this.finished = true;
            }
        }
        return true;
    }


    boolean gatherUnmatchedCloseElement(final ICloseElementTag closeElementTag) {
        if (!this.gathering) {
            return false;
        }
        this.model.add(closeElementTag);
        return true;
    }


    boolean gatherDocType(final IDocType docType) {
        if (!this.gathering) {
            return false;
        }
        this.model.add(docType);
        return true;
    }


    boolean gatherXMLDeclaration(final IXMLDeclaration xmlDeclaration) {
        if (!this.gathering) {
            return false;
        }
        this.model.add(xmlDeclaration);
        return true;
    }


    boolean gatherProcessingInstruction(final IProcessingInstruction processingInstruction) {
        if (!this.gathering) {
            return false;
        }
        this.model.add(processingInstruction);
        return true;
    }

    
}