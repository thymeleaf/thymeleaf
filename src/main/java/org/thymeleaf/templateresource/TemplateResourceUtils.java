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

import org.thymeleaf.util.StringUtils;

/**
 * <p>
 *   Utility methods used by several implementations of {@link ITemplateResource}
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
final class TemplateResourceUtils {



    static String cleanPath(final String path) {

        if (path == null) {
            return null;
        }

        // First replace Windows folder separators with UNIX's
        String unixPath = StringUtils.replace(path, "\\", "/");

        // Some shortcuts, just in case this is empty or simply has no '.' or '..' (and no double-/ we should simplify)
        if (unixPath.length() == 0 || (unixPath.indexOf("/.") < 0 && unixPath.indexOf("//") < 0)) {
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

            final int tokenLen = pos - index;

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
                    strBuilder.insert(0, unixPath, index, (index + tokenLen + 1));
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




    static String computeRelativePath(final String path, final String relativePath) {
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




    static String computeBaseName(final String path) {

        if (path == null) {
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
        }

        return basePath;

    }





    private TemplateResourceUtils() {
        super();
    }

}
