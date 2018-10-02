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
package org.thymeleaf.spring5.context.webflux;

import org.reactivestreams.Publisher;
import org.springframework.core.ReactiveAdapter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.thymeleaf.spring5.view.reactive.ThymeleafReactiveView;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * <p>
 *   Utility class used by reactive context variable container/wrappers.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
class ReactiveContextVariableUtils {



    /**
     * <p>
     *   Lazily resolve the reactive asynchronous object into a {@link Publisher}.
     * </p>
     * <p>
     *   The main aim of this method is to mirror the mechanism used by Spring for resolving
     *   asynchronous variables at the model of views (see Spring's
     *   {@link org.springframework.web.reactive.result.view.ViewResolutionResultHandler}):
     * </p>
     * <ul>
     *     <li>{@code Flux<T>} or other <em>multi-valued</em> streams are resolved as
     *         {@code List<T>} so that they are <em>iterable</em>.</li>
     *     <li>{@code Mono<T>} or other <em>single-valued</em> streams are resolved as
     *         {@code T} so that they are directly referenceable just like any other object.</li>
     * </ul>
     *
     * @param asyncObj the asynchronous object being wrapped by this lazy variable.
     * @param reactiveAdapterRegistry the Spring {@link ReactiveAdapterRegistry}.
     * @return the resolved {@link Publisher}.
     */
    static Publisher<Object> computePublisherValue(
            final Object asyncObj, final ReactiveAdapterRegistry reactiveAdapterRegistry) {

        if (asyncObj instanceof Flux<?> || asyncObj instanceof Mono<?>) {
            // If the async object is a Flux or a Mono, we don't need the ReactiveAdapterRegistry (and we allow
            // initialization to happen without the registry, which is not possible with other Publisher<?>
            // implementations.
            return (Publisher<Object>) asyncObj;
        }


        if (reactiveAdapterRegistry == null) {
            throw new IllegalArgumentException(
                    "Could not initialize lazy reactive context variable (data driver or explicitly-set " +
                    "reactive wrapper):  Value is of class " + asyncObj.getClass().getName() +", but no " +
                    "ReactiveAdapterRegistry has been set. This can happen if this context variable is used " +
                    "for rendering a template without going through a " +
                    ThymeleafReactiveView.class.getSimpleName() + " or if there is no " +
                    "ReactiveAdapterRegistry bean registered at the application context. In such cases, it is " +
                    "required that the wrapped lazy variable values are instances of either " +
                    Flux.class.getName() + " or " + Mono.class.getName() + ".");
        }

        final ReactiveAdapter adapter = reactiveAdapterRegistry.getAdapter(null, asyncObj);
        if (adapter != null) {
            final Publisher<Object> publisher = adapter.toPublisher(asyncObj);
            if (adapter.isMultiValue()) {
                return Flux.from(publisher);
            } else {
                return Mono.from(publisher);
            }
        }

        throw new IllegalArgumentException(
                "Reactive context variable (data driver or explicitly-set reactive wrapper) is of " +
                "class " + asyncObj.getClass().getName() +", but the ReactiveAdapterRegistry " +
                "does not contain a valid adapter able to convert it into a supported reactive data stream.");

    }




    private ReactiveContextVariableUtils() {
        super();
    }

}
