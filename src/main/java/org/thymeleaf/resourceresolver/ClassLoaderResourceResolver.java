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
package org.thymeleaf.resourceresolver;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.aurora.IEngineConfiguration;
import org.thymeleaf.aurora.context.IContext;
import org.thymeleaf.aurora.resource.IResource;
import org.thymeleaf.aurora.resource.ReaderResource;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.util.ClassLoaderUtils;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Implementation of {@link IResourceResolver} that resolves
 *   resources as classloader resources, using
 *   {@link org.thymeleaf.util.ClassLoaderUtils#getClassLoader(Class)} for
 *   obtaining the class loader and then executing
 *   {@link ClassLoader#getResourceAsStream(String)}.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0 (reimplemented in 3.0.0)
 *
 */
public final class ClassLoaderResourceResolver 
        implements IResourceResolver {

    private static final Logger logger = LoggerFactory.getLogger(ClassLoaderResourceResolver.class);

    public static final String NAME = "CLASSLOADER";

    
    public ClassLoaderResourceResolver() {
        super();
    }
    

    public String getName() {
        return NAME; 
    }


    public IResource getResource(
            final IEngineConfiguration configuration, final IContext context,
            final String resourceName, final String characterEncoding) {

        Validate.notNull(resourceName, "Resource name cannot be null");

        try {

            final InputStream inputStream =
                    ClassLoaderUtils.getClassLoader(ClassLoaderResourceResolver.class).getResourceAsStream(resourceName);
            if (inputStream == null) {
                return null;
            }

            final InputStreamReader reader;
            if (!StringUtils.isEmptyOrWhitespace(characterEncoding)) {
                reader = new InputStreamReader(inputStream, characterEncoding);
            } else {
                reader = new InputStreamReader(inputStream);
            }

            return new ReaderResource(resourceName, reader);

        } catch (final UnsupportedEncodingException e) {
            throw new TemplateInputException("Exception reading resource: " + resourceName, e);
        } catch (final Throwable t) {
            showException(resourceName, t);
            return null;
        }

    }



    private static void showException(final String resourceName, final Throwable t) {
        if (logger.isDebugEnabled()) {
            if (logger.isTraceEnabled()) {
                logger.trace(
                        String.format(
                                "[THYMELEAF][%s] Resource \"%s\" could not be resolved. This can be normal as " +
                                        "maybe this resource is not intended to be resolved by this resolver. " +
                                        "Exception is provided for tracing purposes: ",
                                TemplateEngine.threadIndex(), resourceName),
                        t);
            } else {
                logger.debug(
                        String.format(
                                "[THYMELEAF][%s] Resource \"%s\" could not be resolved. This can be normal as " +
                                        "maybe this resource is not intended to be resolved by this resolver. " +
                                        "Exception message is provided (set the log to TRACE for the entire trace): " +
                                        "%s: %s",
                                TemplateEngine.threadIndex(), resourceName,
                                t.getClass().getName(), t.getMessage()));
            }
        }
    }

}
