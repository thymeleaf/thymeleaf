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
package org.thymeleaf.standard.serializer;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.exceptions.TemplateProcessingException;


/**
 * <p>
 *   Utility class for the easy obtention of objects relevant to the serialization of output values
 *   in template modes like JavaScript and/or CSS.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class StandardSerializers {


    /**
     * Name used for registering the <i>Standard JavaScript Serializer</i> object as an
     * <i>execution attribute</i> at the Standard Dialects.
     */
    public static final String STANDARD_JAVASCRIPT_SERIALIZER_ATTRIBUTE_NAME = "StandardJavaScriptSerializer";

    /**
     * Name used for registering the <i>Standard CSS Serializer</i> object as an
     * <i>execution attribute</i> at the Standard Dialects.
     */
    public static final String STANDARD_CSS_SERIALIZER_ATTRIBUTE_NAME = "StandardCSSSerializer";




    private StandardSerializers() {
        super();
    }


    /**
     * <p>
     *   Obtain the JavaScript serializer (implementation of {@link IStandardJavaScriptSerializer}) registered by
     *   the Standard Dialect that is being currently used.
     * </p>
     *
     * @param configuration the configuration object for the current template execution environment.
     * @return the parser object.
     */
    public static IStandardJavaScriptSerializer getJavaScriptSerializer(final IEngineConfiguration configuration) {
        final Object serializer =
                configuration.getExecutionAttributes().get(STANDARD_JAVASCRIPT_SERIALIZER_ATTRIBUTE_NAME);
        if (serializer == null || (!(serializer instanceof IStandardJavaScriptSerializer))) {
            throw new TemplateProcessingException(
                    "No JavaScript Serializer has been registered as an execution argument. " +
                    "This is a requirement for using Standard serialization, and might happen " +
                    "if neither the Standard or the SpringStandard dialects have " +
                    "been added to the Template Engine and none of the specified dialects registers an " +
                    "attribute of type " + IStandardJavaScriptSerializer.class.getName() + " with name " +
                    "\"" + STANDARD_JAVASCRIPT_SERIALIZER_ATTRIBUTE_NAME + "\"");
        }
        return (IStandardJavaScriptSerializer) serializer;
    }



    /**
     * <p>
     *   Obtain the CSS serializer (implementation of {@link IStandardCSSSerializer}) registered by
     *   the Standard Dialect that is being currently used.
     * </p>
     *
     * @param configuration the configuration object for the current template execution environment.
     * @return the variable expression evaluator object.
     */
    public static IStandardCSSSerializer getCSSSerializer(final IEngineConfiguration configuration) {
        final Object serializer =
                configuration.getExecutionAttributes().get(STANDARD_CSS_SERIALIZER_ATTRIBUTE_NAME);
        if (serializer == null || (!(serializer instanceof IStandardCSSSerializer))) {
            throw new TemplateProcessingException(
                    "No CSS Serializer has been registered as an execution argument. " +
                    "This is a requirement for using Standard serialization, and might happen " +
                    "if neither the Standard or the SpringStandard dialects have " +
                    "been added to the Template Engine and none of the specified dialects registers an " +
                    "attribute of type " + IStandardCSSSerializer.class.getName() + " with name " +
                    "\"" + STANDARD_CSS_SERIALIZER_ATTRIBUTE_NAME + "\"");
        }
        return (IStandardCSSSerializer) serializer;
    }


}
