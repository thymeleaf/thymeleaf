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
package org.thymeleaf.templateresource;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

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


    private final ClassLoader classLoader;
    private final String path;
    private final String characterEncoding;



    public ClassLoaderTemplateResource(final ClassLoader classLoader, final String path, final String characterEncoding) {

        super();

        Validate.notNull(classLoader, "Class Loader cannot be null");
        Validate.notEmpty(path, "Resource Path cannot be null or empty");
        // Character encoding CAN be null (system default will be used)

        this.classLoader = classLoader;
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

        final InputStream inputStream = this.classLoader.getResourceAsStream(this.path);
        if (inputStream == null) {
            throw new FileNotFoundException(String.format("ClassLoader resource \"%s\" does not exist", this.path));
        }

        if (!StringUtils.isEmptyOrWhitespace(this.characterEncoding)) {
            return new BufferedReader(new InputStreamReader(new BufferedInputStream(inputStream), this.characterEncoding));
        }

        return new BufferedReader(new InputStreamReader(new BufferedInputStream(inputStream)));

    }




    public ITemplateResource relative(final String relativeLocation) {

        Validate.notEmpty(relativeLocation, "Relative Path cannot be null or empty");

        final String fullRelativeLocation = TemplateResourceUtils.computeRelativeLocation(this.path, relativeLocation);
        return new ClassLoaderTemplateResource(this.classLoader, fullRelativeLocation, this.characterEncoding);

    }




    public boolean exists() {
        return (this.classLoader.getResource(this.path) != null);
    }


}
