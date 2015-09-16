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
package org.thymeleaf.model;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.thymeleaf.engine.AttributeDefinition;
import org.thymeleaf.engine.AttributeName;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public interface IElementAttributes {

    public static enum ValueQuotes { DOUBLE, SINGLE, NONE }


    public int size();

    public List<String> getAllCompleteNames();
    public List<AttributeName> getAllAttributeNames();

    public boolean hasAttribute(final String completeName);
    public boolean hasAttribute(final String prefix, final String name);
    public boolean hasAttribute(final AttributeName attributeName);

    public String getValue(final String completeName);
    public String getValue(final String prefix, final String name);
    public String getValue(final AttributeName attributeName);

    public AttributeDefinition getAttributeDefinition(final String completeName);
    public AttributeDefinition getAttributeDefinition(final String prefix, final String name);
    public AttributeDefinition getAttributeDefinition(final AttributeName attributeName);

    public ValueQuotes getValueQuotes(final String completeName);
    public ValueQuotes getValueQuotes(final String prefix, final String name);
    public ValueQuotes getValueQuotes(final AttributeName attributeName);

    public boolean hasLocation(final String completeName);
    public boolean hasLocation(final String prefix, final String name);
    public boolean hasLocation(final AttributeName attributeName);

    public int getLine(final String completeName);
    public int getLine(final String prefix, final String name);
    public int getLine(final AttributeName attributeName);

    public int getCol(final String completeName);
    public int getCol(final String prefix, final String name);
    public int getCol(final AttributeName attributeName);

    public void clearAll();

    public void setAttribute(final String completeName, final String value);
    public void setAttribute(final String completeName, final String value, final ValueQuotes valueQuotes);

    public void replaceAttribute(final AttributeName oldName, final String completeNewName, final String value);
    public void replaceAttribute(final AttributeName oldName, final String completeNewName, final String value, final ValueQuotes valueQuotes);

    public void removeAttribute(final String prefix, final String name);
    public void removeAttribute(final String completeName);
    public void removeAttribute(final AttributeName attributeName);

    public void write(final Writer writer) throws IOException;

}
