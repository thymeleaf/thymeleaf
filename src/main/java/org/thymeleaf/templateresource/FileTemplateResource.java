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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;

import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Implementation of {@link ITemplateResource} representing a file in the file system.
 * </p>
 * <p>
 *   Objects of this class are usually created by {@link org.thymeleaf.templateresolver.FileTemplateResolver}.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class FileTemplateResource implements ITemplateResource, Serializable {


    private final String path;
    private final File file;
    private final String characterEncoding;



    public FileTemplateResource(final String path, final String characterEncoding) {

        super();

        Validate.notEmpty(path, "Resource Path cannot be null or empty");
        // Character encoding CAN be null (system default will be used)

        this.path = TemplateResourceUtils.cleanPath(path);
        this.file = new File(path);
        this.characterEncoding = characterEncoding;

    }


    public FileTemplateResource(final File file, final String characterEncoding) {

        super();

        Validate.notNull(file, "Resource File cannot be null");
        // Character encoding CAN be null (system default will be used)

        this.path = TemplateResourceUtils.cleanPath(file.getPath());
        this.file = file;
        this.characterEncoding = characterEncoding;

    }




    public String getDescription() {
        return this.file.getAbsolutePath();
    }




    public String getBaseName() {
        return TemplateResourceUtils.computeBaseName(this.path);
    }




    public Reader reader() throws IOException {

        final InputStream inputStream = new FileInputStream(this.file);

        if (!StringUtils.isEmptyOrWhitespace(this.characterEncoding)) {
            return new BufferedReader(new InputStreamReader(new BufferedInputStream(inputStream), this.characterEncoding));
        }

        return new BufferedReader(new InputStreamReader(new BufferedInputStream(inputStream)));

    }




    public ITemplateResource relative(final String relativeLocation) {

        Validate.notEmpty(relativeLocation, "Relative Path cannot be null or empty");

        final String fullRelativeLocation = TemplateResourceUtils.computeRelativeLocation(this.path, relativeLocation);
        return new FileTemplateResource(fullRelativeLocation, this.characterEncoding);

    }




    public boolean exists() {
        return this.file.exists();
    }



}
