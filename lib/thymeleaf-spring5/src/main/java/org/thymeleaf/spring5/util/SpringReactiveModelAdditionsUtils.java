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
package org.thymeleaf.spring5.util;

/**
 * <p>
 *   Utilities for identifying names of execution attributes defined at dialects that should be resolved before
 *   view execution in Spring WebFlux view executions. Execution attributes identified this way will be added to
 *   the model attributes by the 1st phase of the execution of Thymeleaf Spring WebFlux views in order to be
 *   resolved before the view is really executed (2nd phase).
 * </p>
 * <p>
 *   Values of these execution attributes are allowed to be:
 * </p>
 * <ul>
 *     <li>{@code Publisher<?>} (including {@code Flux<?>} and {@code Mono<?>}).</li>
 *     <li>{@code Supplier<? extends Publisher<?>>}: The supplier will be called at {@code View}
 *          rendering time and the result will be added to the Model.</li>
 *     <li>{@code Function<ServerWebExchange,? extends Publisher<?>>}: The function will be called
 *          at {@code View} rendering time and the result will be added to the Model.</li>
 * </ul>
 * <p>
 *     Names will be prefixed with: {@code "ThymeleafReactiveModelAdditions:"}
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.1.0
 *
 */
public final class SpringReactiveModelAdditionsUtils {

    private static final String REACTIVE_MODEL_ADDITIONS_PREFIX = "ThymeleafReactiveModelAdditions:";


    public static boolean isReactiveModelAdditionName(final String name) {
        return name != null && name.startsWith(REACTIVE_MODEL_ADDITIONS_PREFIX);
    }

    public static String fromReactiveModelAdditionName(final String name) {
        if (!isReactiveModelAdditionName(name)) {
            return name;
        }
        return name.substring(REACTIVE_MODEL_ADDITIONS_PREFIX.length());
    }

    public static String toReactiveModelAdditionName(final String name) {
        if (name == null) {
            return null;
        }
        return REACTIVE_MODEL_ADDITIONS_PREFIX + name;
    }


    private SpringReactiveModelAdditionsUtils() {
        super();
    }


}
