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

/**
 * <p>
 *   Interface to be implemented by context variables wrapping <em>reactive data streams</em> which
 *   are meant to <em>drive</em> the reactive-friendly execution of a template (making Thymeleaf act as a consumer of
 *   this data stream).
 * </p>
 * <p>
 *   The wrapped <em>data stream</em> variable will usually have the shape of an implementation of the
 *   {@link org.reactivestreams.Publisher} interface, such as {@link reactor.core.publisher.Flux}.
 * </p>
 * <p>
 *   This interface extends from {@link IReactiveLazyContextVariable} and inherits all of its related
 *   behaviour, but also marks the wrapped variable as a template data-driver.
 *   The presence of a variable of this type in the context actually sets the engine into <strong>data-driven
 *   mode</strong>, and only one of these variables is allowed to appear in the context for template execution.
 *   <strong>Templates executed in <em>data-driven</em> mode are expected to have some kind <em>iteration</em>
 *   on the data-driver variable</strong>, normally by means of a <tt>th:each</tt> attribute.
 * </p>
 * <p>
 *   Variables of this type have a {@link #getDataStreamBufferSizeElements()} property, which describes
 *   the size (in elements) of the buffers that will be created from the data-driver stream before triggering
 *   the execution of the template for the published data. Normally there is no need to execute the template engine
 *   and generate markup for each element of data published by the data stream, so this buffering
 *   prevents Thymeleaf from executing more times than actually needed.
 * </p>
 * <p>
 *   The {@link ReactiveDataDriverContextVariable} class contains a sensible implementation of this interface,
 *   directly usable in most scenarios.
 * </p>
 * <p>
 *   Example use:
 * </p>
 * <pre><code>
 * &#64;RequestMapping("/something")
 * public String doSomething(final Model model) {
 *     final Publisher&lt;Item&gt; data = ...;
 *     model.addAttribute("data", new ReactiveDataDriverContextVariable&lt;&gt;(data, 100));
 *     return "view";
 * }
 * </code></pre>
 * <p>
 *   And then at the template:
 * </p>
 * <pre><code>
 * &lt;table&gt;
 *   &lt;tbody&gt;
 *     &lt;tr th:each=&quot;item : ${data}&quot;&gt;
 *       &lt;td th:text=&quot;${item}&quot;&gt;some item...&lt;/td&gt;
 *     &lt;/tr&gt;
 *   &lt;/tbody&gt;
 * &lt;/table&gt;
 * </code></pre>
 *
 * @param <T> the type of the values being returned by the wrapped data stream.
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
public interface IReactiveDataDriverContextVariable<T> extends IReactiveLazyContextVariable<T> {

    /**
     * <p>
     *   Returns the size (in elements) of the buffers that will be created from the data-driver stream
     *   before triggering the execution of the template for the published data.
     * </p>
     * <p>
     *   Normally there is no need to execute the template engine and generate markup for each element of data
     *   published by the data stream, so this buffering  prevents Thymeleaf from executing more times
     *   than actually needed.
     * </p>
     *
     * @return the size (in elements) of the buffers to be created for each (partial) execution of the engine.
     */
    public int getDataStreamBufferSizeElements();

}
