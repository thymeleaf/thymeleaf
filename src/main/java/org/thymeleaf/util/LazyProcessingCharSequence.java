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
package org.thymeleaf.util;

import java.io.IOException;
import java.io.Writer;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.TemplateModel;


/**
 * <p>
 *   Character sequence that performs a lazy evaluation of a {@link TemplateModel} by the template engine.
 * </p>
 * <p>
 *   It is used sometimes internally by the engine in order to avoid the creation of extra String objects in
 *   some scenarios (e.g. inliners).
 * </p>
 * <p>
 *   This is mostly an <strong>internal class</strong>, and its use is not recommended from user's code.
 * </p>
 * <p>
 *   This class is <strong>not</strong> thread-safe.
 * </p>
 *
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class LazyProcessingCharSequence extends AbstractLazyCharSequence {

    private final ITemplateContext context;
    private final TemplateModel templateModel;


    public LazyProcessingCharSequence(final ITemplateContext context, final TemplateModel templateModel) {

        super();

        if (context == null) {
            throw new IllegalArgumentException("Template Context is null, which is forbidden");
        }
        if (templateModel == null) {
            throw new IllegalArgumentException("Template Model is null, which is forbidden");
        }

        this.context = context;
        this.templateModel = templateModel;

    }




    @Override
    protected String resolveText() {
        final Writer stringWriter = new FastStringWriter();
        this.context.getConfiguration().getTemplateManager().process(this.templateModel, this.context, stringWriter);
        return stringWriter.toString();
    }


    @Override
    protected void writeUnresolved(final Writer writer) throws IOException {
        this.context.getConfiguration().getTemplateManager().process(this.templateModel, this.context, writer);
    }



}
