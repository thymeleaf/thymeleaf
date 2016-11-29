/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.spring5.context.reactive;

import org.reactivestreams.Publisher;
import org.thymeleaf.context.LazyContextVariable;
import reactor.core.publisher.Flux;

/**
 * <p>
 *   Basic implementation of the {@link IReactiveLazyContextVariable} interface.
 * </p>
 * <p>
 *   This class keeps a reference on the data stream passed as a constructor argument, and returns it
 *   through its {@link #getDataStream()} method. If a variable implementing this interface is resolved by
 *   an expression in the template, it will consume all the values published by the data stream into a
 *   {@link java.util.List} object, so that it can be used just like any other iterable variable.
 * </p>
 * <p>
 *   Example use:
 * </p>
 * <pre><code>
 * &#64;RequestMapping("/something")
 * public String doSomething(final Model model) {
 *     final Publisher&lt;Item&gt; someStream = ...;
 *     model.addAttribute("someData", new ReactiveLazyContextVariable&lt;&gt;(someStream);
 *     return "view";
 * }
 * </code></pre>
 * <p>
 *   This class is NOT thread-safe. Thread-safety is not a requirement for context variables.
 * </p>
 *
 * @see IReactiveLazyContextVariable
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
public class ReactiveLazyContextVariable<T>
        extends LazyContextVariable<Iterable<T>>
        implements IReactiveLazyContextVariable<T> {

    private final Publisher<T> dataStream;


    public ReactiveLazyContextVariable(final Publisher<T> dataStream) {
        super();
        this.dataStream = dataStream;
    }


    @Override
    public final Publisher<T> getDataStream() {
        return this.dataStream;
    }

    @Override
    protected final Iterable<T> loadValue() {
        return Flux.from(this.dataStream).collectList().block();
    }

}
