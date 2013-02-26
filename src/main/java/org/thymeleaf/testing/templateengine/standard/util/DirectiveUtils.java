/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.testing.templateengine.standard.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;






public final class DirectiveUtils {

    private static final Pattern DIRECTIVE_PATTERN =
            Pattern.compile("(\\p{Alnum}*)(\\[(\\S*)\\])?");
    private static final int DIRECTIVE_NAME_GROUP = 1;
    private static final int DIRECTIVE_QUALIFIER_GROUP = 3;
    
    
    public static final String RESOLVER_DIRECTIVE_QUALIFIER = "resolver";
    public static final String MAIN_DIRECTIVE_QUALIFIER = null;

    
    

    public static boolean isDirective(final String name) {
        if (name == null) {
            return false;
        }
        final Matcher m = DIRECTIVE_PATTERN.matcher(name);
        return m.matches();
    }
    

    
    public static String extractDirectiveName(final String directive) {
        if (directive == null) {
            return null;
        }
        final Matcher m = DIRECTIVE_PATTERN.matcher(directive);
        if (!m.matches()) {
            return null;
        }
        final String name = m.group(DIRECTIVE_NAME_GROUP);
        if (name == null || name.trim().equals("")) {
            return null;
        }
        return name.trim();
    }
    

    
    public static String extractDirectiveQualifier(final String directive) {
        if (directive == null) {
            return null;
        }
        final Matcher m = DIRECTIVE_PATTERN.matcher(directive);
        if (!m.matches()) {
            return null;
        }
        final String qualifier = m.group(DIRECTIVE_QUALIFIER_GROUP);
        if (qualifier == null || qualifier.trim().equals("")) {
            return null;
        }
        return qualifier.trim();
    }
    

    
    public static boolean hasQualifier(final String directive) {
        return extractDirectiveQualifier(directive) != null;
    }
    
    
    
    public static boolean isResolverQualifier(final String directiveQualifier) {
        return directiveQualifier != null && directiveQualifier.equals(RESOLVER_DIRECTIVE_QUALIFIER);
    }
    
    
    public static String buildResolverDirective(final String directive) {
        if (directive == null) {
            return null;
        }
        if (hasQualifier(directive)) {
            final String directiveName = extractDirectiveName(directive);
            return directiveName + "[" + RESOLVER_DIRECTIVE_QUALIFIER + "]";
        }
        return directive + "[" + RESOLVER_DIRECTIVE_QUALIFIER + "]";
    }
    
    
    
    
    
    private DirectiveUtils() {
        super();
    }
    
    
}
