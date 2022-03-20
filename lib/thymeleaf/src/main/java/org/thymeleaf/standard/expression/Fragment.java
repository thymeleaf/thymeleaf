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
package org.thymeleaf.standard.expression;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Map;

import org.thymeleaf.engine.TemplateModel;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.FastStringWriter;
import org.thymeleaf.util.Validate;


/**
 * <p>
 *   Class that models the result of a {@link FragmentExpression}, i.e. the result of a fragment expression in
 *   the form of {@code ~{template :: fragment? (parameters)?}}
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class Fragment {

    public static final Fragment EMPTY_FRAGMENT = new Fragment();

    private final TemplateModel templateModel;
    private final Map<String,Object> parameters;
    private final boolean syntheticParameters;


    public Fragment(
            final TemplateModel templateModel, final Map<String, Object> parameters,
            final boolean syntheticParameters) {
        super();
        Validate.notNull(templateModel, "Template model cannot be null");
        this.templateModel = templateModel;
        this.parameters = parameters != null ? Collections.unmodifiableMap(parameters) : null;
        this.syntheticParameters = (this.parameters != null && this.parameters.size() > 0 && syntheticParameters);
    }


    // Creates the empty Fragment
    private Fragment() {
        super();
        this.templateModel = null;
        this.parameters = null;
        this.syntheticParameters = false;
    }


    public TemplateModel getTemplateModel() {
        return this.templateModel;
    }

    public Map<String, Object> getParameters() {
        return this.parameters;
    }

    public boolean hasSyntheticParameters() {
        return this.syntheticParameters;
    }


    public void write(final Writer writer) throws IOException {
        if (this.templateModel != null) {
            this.templateModel.write(writer);
        }
    }


    @Override
    public String toString() {
        final Writer stringWriter = new FastStringWriter();
        try {
            write(stringWriter);
        } catch (final IOException e) {
            throw new TemplateProcessingException("Exception while creating String representation of model entity", e);
        }
        return stringWriter.toString();
    }


}
