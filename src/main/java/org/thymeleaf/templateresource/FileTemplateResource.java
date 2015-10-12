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

        this.path = cleanPath(path);
        this.file = new File(path);
        this.characterEncoding = characterEncoding;

    }


    public FileTemplateResource(final File file, final String characterEncoding) {

        super();

        Validate.notNull(file, "Resource File cannot be null");
        // Character encoding CAN be null (system default will be used)

        this.path = cleanPath(file.getPath());
        this.file = file;
        this.characterEncoding = characterEncoding;

    }




    public String getName() {
        return this.file.getAbsolutePath();
    }




    public Reader reader() throws IOException {

        final InputStream inputStream = new FileInputStream(this.file);

        if (!StringUtils.isEmptyOrWhitespace(this.characterEncoding)) {
            return new InputStreamReader(inputStream, this.characterEncoding);
        }

        return new InputStreamReader(inputStream);

    }




    public ITemplateResource relative(final String relativePath) throws IOException {

        Validate.notEmpty(relativePath, "Relative Path cannot be null or empty");

        final String fullRelativePath = createRelativePath(this.path, relativePath);
        return new FileTemplateResource(fullRelativePath, this.characterEncoding);

    }




    public boolean exists() {
        return this.file.exists();
    }





    static String cleanPath(final String path) {

        if (path == null) {
            return null;
        }

        // First replace Windows folder separators with UNIX's
        String unixPath = StringUtils.replace(path, "\\", "/");

        // Some shortcuts, just in case this is empty or simply has no '.' or '..'
        if (unixPath.length() == 0 || unixPath.indexOf('.') < 0) {
            return unixPath;
        }

        // We make sure path starts with '/' in order to simplify the algorithm
        boolean rootBased = (unixPath.charAt(0) == '/');
        unixPath = (rootBased? unixPath : ('/' + unixPath));

        // We will traverse path in reverse order, looking for '.' and '..' tokens and processing them
        final StringBuilder strBuilder = new StringBuilder(unixPath.length());

        int index = unixPath.lastIndexOf('/');
        int pos = unixPath.length() - 1;
        int topCount = 0;
        while (index >= 0) { // will always be 0 for the last iteration, as we prefixed the path with '/'

            final int tokenLen = (pos - (index + 1));

            if (tokenLen > 0) {

                if (tokenLen == 1 && unixPath.charAt(index + 1) == '.') {
                    // Token is '.' -> just ignore it
                } else if (tokenLen == 2 && unixPath.charAt(index + 1) == '.' && unixPath.charAt(index + 2) == '.') {
                    // Token is '..' -> count as a 'top' operation
                    topCount++;
                } else if (topCount > 0){
                    // Whatever comes here has been removed by a 'top' operation, so ignore
                    topCount--;
                } else {
                    // Token is OK, just add (with its corresponding '/')
                    strBuilder.insert(0, unixPath, index, tokenLen);
                }

            }

            pos = index - 1;
            index = (pos >= 0? unixPath.lastIndexOf('/', pos) : -1);

        }

        // Add all 'top' tokens appeared at the very beginning of the path
        for (int i = 0; i < topCount; i++) {
            strBuilder.insert(0, "/..");
        }

        // Perform last cleanup
        if (!rootBased) {
            strBuilder.deleteCharAt(0);
        }

        return strBuilder.toString();
    }




    static String createRelativePath(final String path, final String relativePath) {
        final int separatorPos = path.lastIndexOf('/');
        if (separatorPos != -1) {
            final StringBuilder pathBuilder = new StringBuilder(path.length() + relativePath.length());
            pathBuilder.append(path, 0, separatorPos);
            if (relativePath.charAt(0) != '/') {
                pathBuilder.append('/');
            }
            pathBuilder.append(relativePath);
            return pathBuilder.toString();
        }
        return relativePath;
    }


}
