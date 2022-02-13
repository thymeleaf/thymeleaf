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
package org.thymeleaf.spring5.context;

import java.util.Map;

/**
 * <p>
 *   This interface is meant to abstract a Spring {@code RequestDataValueProcessor}, without the client code
 *   needing to know if it is a Spring WebMVC or Spring WebFlux implementation of this
 *   {@code RequestDataValueProcessor}.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
public interface IThymeleafRequestDataValueProcessor {

    public String processAction(final String action, final String httpMethod);

    public String processFormFieldValue(final String name, final String value, final String type);

    public Map<String, String> getExtraHiddenFields();

    public String processUrl(final String url);

}
