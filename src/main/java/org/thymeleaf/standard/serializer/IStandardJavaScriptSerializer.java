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


import java.io.Writer;

/**
 * <p>
 *   Common interface for all classes that can act as JavaScript serializers in JavaScript templates processed
 *   with the Standard Dialect.
 * </p>
 * <p>
 *   JavaScript serializer objects are in charge of outputting values both in templates using
 *   {@link org.thymeleaf.templatemode.TemplateMode#JAVASCRIPT} and in inlined code by means of e.g.
 *   {@code th:inline="javascript"} blocks.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @see StandardJavaScriptSerializer
 * @since 3.0.0
 *
 */
public interface IStandardJavaScriptSerializer {

    public void serializeValue(final Object object, final Writer writer);

}
