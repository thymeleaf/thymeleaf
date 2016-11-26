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
    public Publisher<T> getDataStream() {
        return this.dataStream;
    }

    @Override
    protected Iterable<T> loadValue() {
        return Flux.from(this.dataStream).collectList().block();
    }

}
