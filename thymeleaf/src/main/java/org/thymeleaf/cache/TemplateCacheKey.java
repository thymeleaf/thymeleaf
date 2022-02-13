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
package org.thymeleaf.cache;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.LoggingUtils;
import org.thymeleaf.util.Validate;


/**
 * <p>
 *   This class models objects used as keys in the Template Cache.
 * </p>
 * <p>
 *   Objects of this class <strong>should only be created from inside the engine</strong>.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 */
public final class TemplateCacheKey implements Serializable {

    private static final long serialVersionUID = 45842555123291L;

    private final String ownerTemplate;
    private final String template;
    private final Set<String> templateSelectors;
    private final int lineOffset;
    private final int colOffset;
    private final TemplateMode templateMode;
    private final Map<String,Object> templateResolutionAttributes;
    private final int h;


    public TemplateCacheKey(
            final String ownerTemplate, final String template, final Set<String> templateSelectors,
            final int lineOffset, final int colOffset, final TemplateMode templateMode,
            final Map<String,Object> templateResolutionAttributes) {

        // NOTE we are assuming that templateSelectors is either null or a non-empty, naturally-ordered set. Also,
        //      we are also assuming that templateResolutionAttributes is either null or non-empty. Also, BOTH
        //      should be unmodifiable. This should have been sorted out at the TemplateSpec constructor.

        super();

        // ownerTemplate will be null if this template is standalone (not something we are processing from inside another one like e.g. an inlining)
        Validate.notNull(template, "Template cannot be null");
        // templateSelectors can be null if we are selecting the entire template
        // templateMode can be null if this template is standalone (no owner template) AND we are forcing a specific template mode for its processing
        // templateResolutionAttributes

        this.ownerTemplate = ownerTemplate;
        this.template = template;
        this.templateSelectors = templateSelectors;
        this.lineOffset = lineOffset;
        this.colOffset = colOffset;
        this.templateMode = templateMode;
        this.templateResolutionAttributes = templateResolutionAttributes;

        // This being a cache key, its equals and hashCode methods will potentially execute many
        // times, so this could help performance
        this.h = computeHashCode();

    }

    public String getOwnerTemplate() {
        return this.ownerTemplate;
    }

    public String getTemplate() {
        return this.template;
    }

    public Set<String> getTemplateSelectors() {
        return this.templateSelectors;
    }

    public int getLineOffset() {
        return this.lineOffset;
    }

    public int getColOffset() {
        return this.colOffset;
    }

    public TemplateMode getTemplateMode() {
        return this.templateMode;
    }

    public Map<String, Object> getTemplateResolutionAttributes() {
        return this.templateResolutionAttributes;
    }


    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof TemplateCacheKey)) {
            return false;
        }

        final TemplateCacheKey that = (TemplateCacheKey) o;

        if (this.h != that.h) { // fail fast
            return false;
        }

        if (this.lineOffset != that.lineOffset) {
            return false;
        }
        if (this.colOffset != that.colOffset) {
            return false;
        }
        if (this.ownerTemplate != null ? !this.ownerTemplate.equals(that.ownerTemplate) : that.ownerTemplate != null) {
            return false;
        }
        if (!this.template.equals(that.template)) {
            return false;
        }
        if (this.templateSelectors != null ? !this.templateSelectors.equals(that.templateSelectors) : that.templateSelectors != null) {
            return false;
        }
        if (this.templateMode != that.templateMode) {
            return false;
        }
        // Note how it is important that template resolution attribute values correctly implement equals() and hashCode()
        return !(this.templateResolutionAttributes != null ? !this.templateResolutionAttributes.equals(that.templateResolutionAttributes) : that.templateResolutionAttributes != null);

    }


    @Override
    public int hashCode() {
        return this.h;
    }


    private int computeHashCode() {
        int result = this.ownerTemplate != null ? this.ownerTemplate.hashCode() : 0;
        result = 31 * result + this.template.hashCode();
        result = 31 * result + (this.templateSelectors != null ? this.templateSelectors.hashCode() : 0);
        result = 31 * result + this.lineOffset;
        result = 31 * result + this.colOffset;
        result = 31 * result + (this.templateMode != null ? this.templateMode.hashCode() : 0);
        result = 31 * result + (this.templateResolutionAttributes != null ? this.templateResolutionAttributes.hashCode() : 0);
        return result;
    }




    @Override
    public String toString() {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(LoggingUtils.loggifyTemplateName(this.template));
        if (this.ownerTemplate != null) {
            strBuilder.append('@');
            strBuilder.append('(');
            strBuilder.append(LoggingUtils.loggifyTemplateName(this.ownerTemplate));
            strBuilder.append(';');
            strBuilder.append(this.lineOffset);
            strBuilder.append(',');
            strBuilder.append(this.colOffset);
            strBuilder.append(')');
        }
        if (this.templateSelectors != null) {
            strBuilder.append("::");
            strBuilder.append(this.templateSelectors);
        }
        if (this.templateMode != null) {
            strBuilder.append(" @");
            strBuilder.append(this.templateMode);
        }
        if (this.templateResolutionAttributes != null) {
            strBuilder.append(" (");
            strBuilder.append(this.templateResolutionAttributes);
            strBuilder.append(")");
        }
        return strBuilder.toString();
    }

}
