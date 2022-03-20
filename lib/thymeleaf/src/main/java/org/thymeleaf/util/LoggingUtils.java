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
package org.thymeleaf.util;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class LoggingUtils {



    public static String loggifyTemplateName(final String template) {
        if (template == null) {
            return null;
        }
        if (template.length() <= 120) {
            return template.replace('\n', ' ');
        }
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(template.substring(0, 35).replace('\n', ' '));
        strBuilder.append("[...]");
        strBuilder.append(template.substring(template.length() - 80).replace('\n', ' '));
        return strBuilder.toString();
    }






    private LoggingUtils() {
        super();
    }
    
    
}
