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
package org.thymeleaf.spring5.templateresource;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Implementation of {@link ITemplateResource} that resolves
 *   resources by delegating on Spring's resource resolution mechanism, implemented by the
 *   {@link org.springframework.core.io.ResourceLoader} interface.
 * </p>
 * <p>
 *   This resource resolver accesses the Spring resource resolution mechanism by means of
 *   calls to {@link ApplicationContext#getResource(String)}.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
public final class SpringResourceTemplateResource implements ITemplateResource {


    private final Resource resource;
    private final String characterEncoding;



    public SpringResourceTemplateResource(
            final ApplicationContext applicationContext, final String location, final String characterEncoding) {

        super();

        Validate.notNull(applicationContext, "Application Context cannot be null");
        Validate.notEmpty(location, "Resource Location cannot be null or empty");
        // Character encoding CAN be null (system default will be used)

        this.resource = applicationContext.getResource(location);
        this.characterEncoding = characterEncoding;

    }


    public SpringResourceTemplateResource(
            final Resource resource, final String characterEncoding) {

        super();

        Validate.notNull(resource, "Resource cannot be null");
        // Character encoding CAN be null (system default will be used)

        this.resource = resource;
        this.characterEncoding = characterEncoding;

    }


    public String getDescription() {
        return this.resource.getDescription();
    }

    public String getBaseName() {
        return computeBaseName(this.resource.getFilename());
    }

    public boolean exists() {
        return this.resource.exists();
    }

    public Reader reader() throws IOException {

        // Will never return null, but an IOException if not found
        final InputStream inputStream = this.resource.getInputStream();

        if (!StringUtils.isEmptyOrWhitespace(this.characterEncoding)) {
            return new BufferedReader(new InputStreamReader(new BufferedInputStream(inputStream), this.characterEncoding));
        }

        return new BufferedReader(new InputStreamReader(new BufferedInputStream(inputStream)));

    }

    public ITemplateResource relative(final String relativeLocation) {
        final Resource relativeResource;
        try {
            relativeResource = this.resource.createRelative(relativeLocation);
        } catch (final IOException e) {
            // Given we have delegated the createRelative(...) mechanism to Spring, it's better if we don't do
            // any assumptions on what this IOException means and simply return a resource object that returns
            // no reader and exists() == false.
            return new SpringResourceInvalidRelativeTemplateResource(getDescription(), relativeLocation, e);
        }
        return new SpringResourceTemplateResource(relativeResource, this.characterEncoding);
    }



    static String computeBaseName(final String path) {

        if (path == null || path.length() == 0) {
            return null;
        }

        // First remove a trailing '/' if it exists
        final String basePath = (path.charAt(path.length() - 1) == '/'? path.substring(0,path.length() - 1) : path);

        final int slashPos = basePath.lastIndexOf('/');
        if (slashPos != -1) {
            final int dotPos = basePath.lastIndexOf('.');
            if (dotPos != -1 && dotPos > slashPos + 1) {
                return basePath.substring(slashPos + 1, dotPos);
            }
            return basePath.substring(slashPos + 1);
        } else {
            final int dotPos = basePath.lastIndexOf('.');
            if (dotPos != -1) {
                return basePath.substring(0, dotPos);
            }
        }

        return (basePath.length() > 0? basePath : null);

    }




    private static final class SpringResourceInvalidRelativeTemplateResource implements ITemplateResource {

        private final String originalResourceDescription;
        private final String relativeLocation;
        private final IOException ioException;


        SpringResourceInvalidRelativeTemplateResource(
                final String originalResourceDescription,
                final String relativeLocation,
                final IOException ioException) {
            super();
            this.originalResourceDescription = originalResourceDescription;
            this.relativeLocation = relativeLocation;
            this.ioException = ioException;
        }


        @Override
        public String getDescription() {
            return "Invalid relative resource for relative location \"" + this.relativeLocation +
                    "\" and original resource " + this.originalResourceDescription + ": " + this.ioException.getMessage();
        }

        @Override
        public String getBaseName() {
            return "Invalid relative resource for relative location \"" + this.relativeLocation +
                    "\" and original resource " + this.originalResourceDescription + ": " + this.ioException.getMessage();
        }

        @Override
        public boolean exists() {
            return false;
        }

        @Override
        public Reader reader() throws IOException {
            throw new IOException("Invalid relative resource", this.ioException);
        }

        @Override
        public ITemplateResource relative(final String relativeLocation) {
            return this;
        }

        @Override
        public String toString() {
            return getDescription();
        }

    }


}
