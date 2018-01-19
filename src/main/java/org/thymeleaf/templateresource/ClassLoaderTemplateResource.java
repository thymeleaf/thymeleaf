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
package org.thymeleaf.templateresource;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.thymeleaf.util.ClassLoaderUtils;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Implementation of {@link ITemplateResource} representing a resource accessible by a {@link ClassLoader}
 *   (i.e. living at the <em>class path</em>).
 * </p>
 * <p>
 *   Objects of this class are usually created by {@link org.thymeleaf.templateresolver.ClassLoaderTemplateResolver}.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class ClassLoaderTemplateResource implements ITemplateResource {


    private final ClassLoader optionalClassLoader;
    private final String path;
    private final String characterEncoding;


    /**
     * <p>
     *   Create a ClassLoader-based template resource, without specifying the specific class loader
     *   to be used for resolving the resource.
     * </p>
     * <p>
     *   If created this way, the sequence explained in
     *   {@link org.thymeleaf.util.ClassLoaderUtils#loadResourceAsStream(String)} will be used for resolving
     *   the resource.
     * </p>
     *
     * @param path the path to the template resource.
     * @param characterEncoding the character encoding to be used to read the resource.
     *
     * @since 3.0.3
     */
    public ClassLoaderTemplateResource(final String path, final String characterEncoding) {
        this(null, path, characterEncoding);
    }


    /**
     * <p>
     *   Create a ClassLoader-based template resource, specifying the specific class loader
     *   to be used for resolving the resource.
     * </p>
     *
     * @param classLoader the class loader to be used for resource resolution.
     * @param path the path to the template resource.
     * @param characterEncoding the character encoding to be used to read the resource.
     *
     * @since 3.0.3
     */
    public ClassLoaderTemplateResource(final ClassLoader classLoader, final String path, final String characterEncoding) {

        super();

        // Class Loader CAN be null (will apply the default sequence of class loaders
        Validate.notEmpty(path, "Resource Path cannot be null or empty");
        // Character encoding CAN be null (system default will be used)

        this.optionalClassLoader = classLoader;
        final String cleanPath = TemplateResourceUtils.cleanPath(path);
        this.path = (cleanPath.charAt(0) == '/' ? cleanPath.substring(1) : cleanPath);
        this.characterEncoding = characterEncoding;

    }




    public String getDescription() {
        return this.path;
    }




    public String getBaseName() {
        return TemplateResourceUtils.computeBaseName(this.path);
    }




    public Reader reader() throws IOException {

        final InputStream inputStream;
        if (this.optionalClassLoader != null) {
            inputStream = this.optionalClassLoader.getResourceAsStream(this.path);
        } else {
            inputStream = ClassLoaderUtils.findResourceAsStream(this.path);
        }

        if (inputStream == null) {
            throw new FileNotFoundException(String.format("ClassLoader resource \"%s\" could not be resolved", this.path));
        }

        if (!StringUtils.isEmptyOrWhitespace(this.characterEncoding)) {
            return new BufferedReader(new InputStreamReader(new BufferedInputStream(inputStream), this.characterEncoding));
        }

        return new BufferedReader(new InputStreamReader(new BufferedInputStream(inputStream)));

    }




    public ITemplateResource relative(final String relativeLocation) {

        Validate.notEmpty(relativeLocation, "Relative Path cannot be null or empty");

        final String fullRelativeLocation = TemplateResourceUtils.computeRelativeLocation(this.path, relativeLocation);
        return new ClassLoaderTemplateResource(this.optionalClassLoader, fullRelativeLocation, this.characterEncoding);

    }




    public boolean exists() {
        if (this.optionalClassLoader != null) {
            return (this.optionalClassLoader.getResource(this.path) != null);
        }
        return ClassLoaderUtils.isResourcePresent(this.path);
    }


}
