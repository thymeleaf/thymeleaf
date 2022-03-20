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
package org.thymeleaf.processor.comment;

import org.thymeleaf.model.IModel;

/**
 * <p>
 *   Structure handler class meant to be used by {@link ICommentProcessor} implementations.
 * </p>
 * <p>
 *   Structure handlers allow processors to instruct the engine to perform a series of actions that cannot
 *   be done directly from the processors themselves, usually because these actions are applied or have effects
 *   on scopes broader than the processed events themselves.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @see ICommentProcessor
 * @since 3.0.0
 * 
 */
public interface ICommentStructureHandler {


    /**
     * <p>
     *   Resets all actions specified so far for the current processor execution.
     * </p>
     */
    public void reset();

    /**
     * <p>
     *   Instructs the engine to set a new content for this Comment.
     * </p>
     * @param content the new content
     */
    public void setContent(final CharSequence content);

    /**
     * <p>
     *   Instructs the engine to replace the current event with the specified model (a {@link IModel}).
     * </p>
     *
     * @param model the model to be used as a replacement.
     * @param processable whether the model should be considered <em>processable</em> or not.
     */
    public void replaceWith(final IModel model, final boolean processable);

    /**
     * <p>
     *   Instructs the engine to remove the entire event that is being processed.
     * </p>
     */
    public void removeComment();

}

