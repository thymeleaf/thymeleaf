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
package org.thymeleaf.aurora.engine;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.aurora.dialect.IDialect;
import org.thymeleaf.aurora.dialect.IProcessorDialect;
import org.thymeleaf.aurora.processor.IProcessor;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class DialectInitialization {

    private final ElementDefinitions elementDefinitions;
    private final AttributeDefinitions attributeDefinitions;




    public static DialectInitialization build(final Map<String,IDialect> dialects) {

        Validate.notNull(dialects, "Dialect map cannot be null");

        final Set<IProcessor> processors = new LinkedHashSet<IProcessor>(80);
        for (final Map.Entry<String,IDialect> dialectEntry : dialects.entrySet()) {

            final String dialectPrefix = dialectEntry.getKey();
            final IDialect dialect = dialectEntry.getValue();

            if (dialect == null) {
                throw new IllegalArgumentException("Null dialect has been specified");
            }

            if (dialect instanceof IProcessorDialect) {

                final Set<IProcessor> dialectProcessors = ((IProcessorDialect)dialect).getProcessors();
                if (dialectProcessors == null) {
                    throw new IllegalArgumentException("Dialect should not return null processor set: " + dialect.getClass().getName());
                }
                for (final IProcessor dialectProcessor : dialectProcessors) {

                    if (dialectProcessor == null) {
                        throw new IllegalArgumentException("Dialect should not return null processor in processor set: " + dialect.getClass().getName());
                    }
                    if (processors.contains(dialectProcessor)) {
                        throw new IllegalArgumentException(
                                "The same processor of class " + dialectProcessor.getClass().getName() + " has been " +
                                "specified more than one (probably in different dialects). Processor instances should " +
                                "be unique among all configured dialects.");
                    }

                    // Initialize the processor
                    dialectProcessor.setDialect(dialect);
                    dialectProcessor.setDialectPrefix(dialectPrefix);

                    // Add the processor to the set
                    processors.add(dialectProcessor);

                }

            }

        }


        final ElementDefinitions elementDefinitions = new ElementDefinitions(processors);
        final AttributeDefinitions attributeDefinitions = new AttributeDefinitions(processors);

        return new DialectInitialization(elementDefinitions, attributeDefinitions);
    }






    private DialectInitialization(final ElementDefinitions elementDefinitions, final AttributeDefinitions attributeDefinitions) {
        super();
        this.elementDefinitions = elementDefinitions;
        this.attributeDefinitions = attributeDefinitions;
    }


    public ElementDefinitions getElementDefinitions() {
        return this.elementDefinitions;
    }

    public AttributeDefinitions getAttributeDefinitions() {
        return this.attributeDefinitions;
    }



}
