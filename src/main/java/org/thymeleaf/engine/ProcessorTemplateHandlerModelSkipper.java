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

import java.util.Arrays;

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
final class ProcessorTemplateHandlerModelSkipper {

    // TODO Lateral effects here are a problem!!! we might be increasing modelLevel for a "before model", or for a gathered iteration that will contain the event again!


    static final int DEFAULT_MODEL_LEVELS = 10;

    static enum SkipBehaviour {

        PROCESS(false, false), SKIP_ALL(true, true), SKIP_CLOSE(false, false), SKIP_BODY(true, true), SKIP_BODY_ELEMENTS(false, true), SKIP_BODY_TAIL_ELEMENTS(false, false);

        final boolean skipNonElements;
        final boolean skipAllChildren;

        SkipBehaviour(final boolean skipNonElements, final boolean skipAllChildren) {
            this.skipNonElements = skipNonElements;
            this.skipAllChildren = skipAllChildren;
        }

    }

    private SkipBehaviour[] behaviourByLevel;
    private SkipBehaviour behaviour;
    private int modelLevel;


    ProcessorTemplateHandlerModelSkipper() {
        super();
        this.behaviourByLevel = new SkipBehaviour[DEFAULT_MODEL_LEVELS];
        this.modelLevel = 0;
        this.behaviourByLevel[this.modelLevel] = SkipBehaviour.PROCESS;
        this.behaviour = this.behaviourByLevel[this.modelLevel];
    }


    void setBehaviour(final SkipBehaviour behaviour) {
        this.behaviourByLevel[this.modelLevel] = behaviour;
        this.behaviour = behaviour;
    }


    boolean skipText() {
        return this.behaviour.skipNonElements;
    }


    boolean skipComment() {
        return this.behaviour.skipNonElements;
    }


    boolean skipCDATASection() {
        return this.behaviour.skipNonElements;
    }


    boolean skipStandaloneElement() {
        if (this.behaviour.skipNonElements || this.behaviour == SkipBehaviour.SKIP_BODY_ELEMENTS) {
            return true;
        }
        if (this.behaviour == SkipBehaviour.SKIP_BODY_TAIL_ELEMENTS) {
            // This was the first element, the others will be skipped
            setBehaviour(SkipBehaviour.SKIP_BODY_ELEMENTS);
        }
        return false;
    }


    boolean skipOpenElement() {

        final boolean skip = (this.behaviour.skipNonElements || this.behaviour == SkipBehaviour.SKIP_BODY_ELEMENTS);

        this.modelLevel++;

        if (this.behaviourByLevel.length == this.modelLevel) {
            this.behaviourByLevel = Arrays.copyOf(this.behaviourByLevel, this.behaviourByLevel.length + DEFAULT_MODEL_LEVELS/2);
        }
        setBehaviour(this.behaviour.skipAllChildren ? SkipBehaviour.SKIP_ALL : SkipBehaviour.PROCESS);

        return skip;

    }


    boolean skipCloseElement() {

        final boolean skip = (this.behaviour == SkipBehaviour.SKIP_ALL || this.behaviour == SkipBehaviour.SKIP_CLOSE);

        this.modelLevel--;
        this.behaviour = this.behaviourByLevel[this.modelLevel];

        if (this.behaviour == SkipBehaviour.SKIP_BODY_TAIL_ELEMENTS) {
            setBehaviour(SkipBehaviour.SKIP_BODY_ELEMENTS);
        }

        return skip;

    }


    boolean skipUnmatchedCloseElement() {
        return this.behaviour.skipNonElements;
    }


    boolean skipDocType() {
        return this.behaviour.skipNonElements;
    }


    boolean skipXMLDeclaration() {
        return this.behaviour.skipNonElements;
    }


    boolean skipProcessingInstruction() {
        return this.behaviour.skipNonElements;
    }

    
}