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
package org.thymeleaf.context;

import java.util.List;

import org.thymeleaf.engine.TemplateData;
import org.thymeleaf.inline.IInliner;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.TemplateResolution;

/**
 * <p>
 *   Interface implemented by all classes containing the context required for template processing.
 * </p>
 * <p>
 *   This interface extends {@link IExpressionContext} and {@link IContext} by adding the required
 *   information needed to process templates.
 * </p>
 * <p>
 *   Note that, in order for the template engine to use a custom-made implementation of this interface
 *   for template processing instead of cloning its data, such implementation should also implement
 *   the {@link IEngineContext} interface.
 * </p>
 * <p>
 *   Also note these implementations do not have to be thread-safe, and in fact should not be shared by different
 *   threads or template executions. They are meant to be local to a specific template engine execution.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 * 
 */
public interface ITemplateContext extends IExpressionContext {

    // Template Data works as a local variable so that it can be nested when a fragment is inserted
    public TemplateData getTemplateData();

    // This will always correspond to the template mode of the current template resolution
    public TemplateMode getTemplateMode();

    public List<TemplateData> getTemplateStack();

    // Selection target works as a local variable, but is used so often that it has its own methods in order to allow
    // specific performance improvements to be designed for them
    public boolean hasSelectionTarget();
    public Object getSelectionTarget();

    // Text inlining works as a local variable, but is used so often that it has its own methods in order to allow
    // specific performance improvements to be designed for them
    public IInliner getInliner();


    public String getMessage(
            final Class<?> origin, final String key, final Object[] messageParameters, final boolean useAbsentMessageRepresentation);

    public IdentifierSequences getIdentifierSequences();

}
