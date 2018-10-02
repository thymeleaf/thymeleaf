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
package org.thymeleaf.model;

import java.io.IOException;
import java.io.Writer;

import org.thymeleaf.engine.AttributeDefinition;

/**
 * <p>
 *   Interface defining an attribute contained in a tag.
 * </p>
 * <p>
 *   Container tags are implementations of {@link IProcessableElementTag}.
 * </p>
 * <p>
 *   Note that any implementations of this interface should be <strong>immutable</strong>.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public interface IAttribute {


    /**
     * <p>
     *   Returns the complete name of the attribute, exactly as it was written in the original template (if it
     *   did appear there).
     * </p>
     *
     * @return the complete name.
     */
    public String getAttributeCompleteName();

    /**
     * <p>
     *   Returns the {@link AttributeDefinition} corresponding to this attribute.
     * </p>
     * <p>
     *   The attribute definition contains several metadata related to the attribute. For example, if the
     *   template mode is {@link org.thymeleaf.templatemode.TemplateMode#HTML}, an attribute definition could
     *   specify whether the attribute is boolean (represents a true/false value by appearing or not appearing
     *   at a specific tag).
     * </p>
     *
     * @return the attribute definition.
     */
    public AttributeDefinition getAttributeDefinition();

    /**
     * <p>
     *   Returns the operator specified for this attribute.
     * </p>
     * <p>
     *   The operator itself, if present, is always an equals sign ({@code =}), but the reason this is specified as a separate
     *   field is that it could be surrounded by white space, which should be respected in output when present at
     *   the input template.
     * </p>
     * <p>
     *   If the attribute is specified without a value at all (and therefore no operator either), this method will
     *   return null.
     * </p>
     *
     * @return the attribute operator (might be null if no value specified).
     */
    public String getOperator();

    /**
     * <p>
     *   Returns the value of this attribute, or null if it has none.
     * </p>
     * <p>
     *   A null-valued attribute is an attribute of which only the name has been specified (only allowed in HTML mode).
     * </p>
     *
     * @return the value of this attribute, or null if it has none.
     */
    public String getValue();

    /**
     * <p>
     *   Returns the type of quotes surrounding the attribute value.
     * </p>
     *
     * @return the {@link AttributeValueQuotes} value representing the attribute value quotes (might be null).
     */
    public AttributeValueQuotes getValueQuotes();

    /**
     * <p>
     *   Checks whether this attribute contains location information (template name, line and column).
     * </p>
     * <p>
     *   Only attributes that are generated during the parsing of templates contain location info, locating them
     *   in their original template. All attributes generated during template processing and not originally present
     *   at the template do not contain this location data.
     * </p>
     *
     * @return whether the attribute contains location data or not.
     */
    public boolean hasLocation();

    /**
     * <p>
     *   Returns the name of the template from which parsing this attribute was originally created.
     * </p>
     *
     * @return the name of the template
     */
    public String getTemplateName();

    /**
     * <p>
     *   Returns the line at which this attribute can be found in the template specified by {@link #getTemplateName()}.
     * </p>
     *
     * @return the line number, starting in 1.
     */
    public int getLine();

    /**
     * <p>
     *   Returns the column at which this attribute can be found in the template specified by {@link #getTemplateName()}.
     * </p>
     *
     * @return the column number, starting in 1.
     */
    public int getCol();

    /**
     * <p>
     *   Writes this attribute to the specified {@link Writer}.
     * </p>
     * <p>
     *   This is usually called as a part of {@link ITemplateEvent#write(Writer)}.
     * </p>
     *
     * @param writer the writer this attribute should be written to.
     * @throws IOException if an input/output exception occurs.
     */
    public void write(final Writer writer) throws IOException;


}
