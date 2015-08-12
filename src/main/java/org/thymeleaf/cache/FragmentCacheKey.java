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
package org.thymeleaf.cache;

import java.util.Arrays;

import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;


/**
 * <p>
 *   This class models objects used as keys in the Fragment Cache.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 */
public final class FragmentCacheKey {

    private final boolean textualTemplate;
    private final String template;
    private final String[] markupSelectors;
    private final TemplateMode forcedTemplateMode;
    private final String fragment;




    public FragmentCacheKey(
            final String template, final String[] markupSelectors, final String fragment, final boolean textualTemplate, final TemplateMode forcedTemplateMode) {
        super();
        Validate.notNull(template, "Template cannot be null");
        // markupSelectors can be null if we are selecting an entire template as fragment, or if this is a textual fragment
        // fragment can be null if we are using markup selectors, or if we are selecting an entire template as fragment
        this.textualTemplate = textualTemplate;
        this.template = template;
        this.forcedTemplateMode = forcedTemplateMode;
        if (markupSelectors != null) {
            this.markupSelectors = markupSelectors.clone();
            Arrays.sort(this.markupSelectors); // we need to sort this so that Arrays.sort() works in equals()
        } else {
            this.markupSelectors = null;
        }
        this.fragment = fragment;
    }


    public boolean isTextualTemplate() {
        return this.textualTemplate;
    }

    public String getTemplate() {
        return this.template;
    }

    public String[] getMarkupSelectors() {
        return this.markupSelectors;
    }

    public TemplateMode getForcedTemplateMode() {
        return this.forcedTemplateMode;
    }

    public String getFragment() {
        return this.fragment;
    }



    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }
        if (!(o instanceof FragmentCacheKey)) {
            return false;
        }

        final FragmentCacheKey that = (FragmentCacheKey) o;

        if (this.textualTemplate != that.textualTemplate) {
            return false;
        }
        if (!this.template.equals(that.template)) {
            return false;
        }
        if (!Arrays.equals(this.markupSelectors, that.markupSelectors)) {
            return false;
        }
        if (this.forcedTemplateMode != that.forcedTemplateMode) {
            return false;
        }
        return !(this.fragment != null ? !this.fragment.equals(that.fragment) : that.fragment != null);

    }


    @Override
    public int hashCode() {
        int result = (this.textualTemplate ? 1 : 0);
        result = 31 * result + this.template.hashCode();
        result = 31 * result + (this.markupSelectors != null ? Arrays.hashCode(this.markupSelectors) : 0);
        result = 31 * result + (this.forcedTemplateMode != null ? this.forcedTemplateMode.hashCode() : 0);
        result = 31 * result + (this.fragment != null ? this.fragment.hashCode() : 0);
        return result;
    }




    @Override
    public String toString() {
        final StringBuilder strBuilder = new StringBuilder();
        if (this.textualTemplate) {
            strBuilder.append("(textual)");
        }
        strBuilder.append(StringUtils.abbreviate(this.template, 40).replace('\n', '\\'));
        if (this.markupSelectors != null && this.markupSelectors.length > 0) {
            strBuilder.append("::");
            strBuilder.append(Arrays.toString(this.markupSelectors));
        }
        if (this.forcedTemplateMode != null) {
            strBuilder.append("@(");
            strBuilder.append(this.forcedTemplateMode);
            strBuilder.append(")");
        }
        if (this.fragment != null) {
            strBuilder.append("#(");
            strBuilder.append(StringUtils.abbreviate(this.fragment,40).replace('\n', '\\'));
            strBuilder.append(")");
        }
        return strBuilder.toString();
    }

}
