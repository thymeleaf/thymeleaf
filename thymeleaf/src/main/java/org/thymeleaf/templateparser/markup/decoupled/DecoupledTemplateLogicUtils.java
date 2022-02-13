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
package org.thymeleaf.templateparser.markup.decoupled;

import java.io.IOException;
import java.util.Set;

import org.attoparser.IMarkupParser;
import org.attoparser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.util.LoggingUtils;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Utility class performing the required operations for computing the decoupled template logic corresponding
 *   to a template being parsed.
 * </p>
 * <p>
 *   This class computes a {@link DecoupledTemplateLogic} by parsing an additional resource
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class DecoupledTemplateLogicUtils {


    private static final Logger logger = LoggerFactory.getLogger(DecoupledTemplateLogicUtils.class);




    public static DecoupledTemplateLogic computeDecoupledTemplateLogic(
            final IEngineConfiguration configuration,
            final String ownerTemplate, final String template, final Set<String> templateSelectors,
            final ITemplateResource resource, final TemplateMode templateMode,
            final IMarkupParser parser) throws IOException, ParseException {

        Validate.notNull(configuration, "Engine Configuration cannot be null");
        Validate.notNull(template, "Template cannot be null");
        Validate.notNull(resource, "Template Resource cannot be null");
        Validate.notNull(templateMode, "Template Mode cannot be null");

        final IDecoupledTemplateLogicResolver decoupledTemplateLogicResolver = configuration.getDecoupledTemplateLogicResolver();

        final ITemplateResource decoupledResource =
                decoupledTemplateLogicResolver.resolveDecoupledTemplateLogic(
                        configuration, ownerTemplate, template, templateSelectors, resource, templateMode);

        if (!decoupledResource.exists()) {

            if (logger.isTraceEnabled()) {
                logger.trace(
                        "[THYMELEAF][{}] Decoupled logic for template \"{}\" could not be resolved as relative resource \"{}\". " +
                        "This does not need to be an error, as templates may lack a corresponding decoupled logic file.",
                        new Object[] {TemplateEngine.threadIndex(), LoggingUtils.loggifyTemplateName(template), decoupledResource.getDescription()});
            }

            return null;
        }


        if (logger.isTraceEnabled()) {
            logger.trace(
                    "[THYMELEAF][{}] Decoupled logic for template \"{}\" has been resolved as relative resource \"{}\"",
                    new Object[] {TemplateEngine.threadIndex(), LoggingUtils.loggifyTemplateName(template), decoupledResource.getDescription()});
        }


        /*
         * The decoupled template logic resource exists, so we should parse it before the template itself, in order
         * to obtain the logic to be injected on the "real" template during parsing.
         */

        final DecoupledTemplateLogicBuilderMarkupHandler decoupledMarkupHandler =
                new DecoupledTemplateLogicBuilderMarkupHandler(template, templateMode);

        parser.parse(decoupledResource.reader(), decoupledMarkupHandler);

        return decoupledMarkupHandler.getDecoupledTemplateLogic();

    }



    private DecoupledTemplateLogicUtils() {
        super();
    }

}
