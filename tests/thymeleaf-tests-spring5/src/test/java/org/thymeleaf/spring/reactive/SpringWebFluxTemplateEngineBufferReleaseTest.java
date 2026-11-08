/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2026 Thymeleaf (http://www.thymeleaf.org)
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
package org.thymeleaf.spring.reactive;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.MediaType;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringWebFluxTemplateEngine;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import org.thymeleaf.templateresolver.StringTemplateResolver;
import reactor.core.publisher.Flux;

/**
 * <p>
 *   Checks that {@link DataBuffer} objects allocated by
 *   {@link SpringWebFluxTemplateEngine} are always released when template
 *   processing fails, whatever the execution mode (full, chunked, data-driven).
 * </p>
 * <p>
 *   Before the fix, the buffer allocated for the chunk being processed was
 *   neither emitted nor released when an exception was raised during template
 *   processing, which leaked the underlying (pooled, direct) Netty
 *   {@link ByteBuf} -- as reported by Netty's {@code ResourceLeakDetector}.
 * </p>
 */
public final class SpringWebFluxTemplateEngineBufferReleaseTest {


    // Enough static markup before the failing expression to make sure several chunks
    // are emitted before the exception is raised in chunked / data-driven modes
    private static final String LONG_TEXT =
            "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef" +
            "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef" +
            "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";

    private static final String FAILING_TEMPLATE =
            "<html><body>" + LONG_TEXT +
            "<div th:text=\"${failingBean.fail()}\">FAIL</div>" +
            "</body></html>";

    private static final String FAILING_DATA_DRIVEN_TEMPLATE =
            "<html><body>" + LONG_TEXT +
            "<div th:each=\"item : ${items}\" th:text=\"${failingBean.fail()}\">FAIL</div>" +
            "</body></html>";



    @Test
    public void testBufferReleasedOnErrorInFullMode() {

        final TrackingDataBufferFactory bufferFactory = new TrackingDataBufferFactory();
        final Context context = failingContext();

        // Integer.MAX_VALUE chunk size = no limit = FULL mode
        final Publisher<DataBuffer> stream =
                createTemplateEngine().processStream(
                        FAILING_TEMPLATE, null, context, bufferFactory,
                        MediaType.TEXT_HTML, StandardCharsets.UTF_8, Integer.MAX_VALUE);

        assertStreamFailsReleasingAllBuffers(stream, bufferFactory);

    }


    @Test
    public void testBufferReleasedOnErrorInChunkedMode() {

        final TrackingDataBufferFactory bufferFactory = new TrackingDataBufferFactory();
        final Context context = failingContext();

        final Publisher<DataBuffer> stream =
                createTemplateEngine().processStream(
                        FAILING_TEMPLATE, null, context, bufferFactory,
                        MediaType.TEXT_HTML, StandardCharsets.UTF_8, 64);

        assertStreamFailsReleasingAllBuffers(stream, bufferFactory);

    }


    @Test
    public void testBufferReleasedOnErrorInDataDrivenMode() {

        final TrackingDataBufferFactory bufferFactory = new TrackingDataBufferFactory();
        final Context context = failingContext();
        context.setVariable("items", new ReactiveDataDriverContextVariable(Flux.range(1, 10), 2));

        final Publisher<DataBuffer> stream =
                createTemplateEngine().processStream(
                        FAILING_DATA_DRIVEN_TEMPLATE, null, context, bufferFactory,
                        MediaType.TEXT_HTML, StandardCharsets.UTF_8, 64);

        assertStreamFailsReleasingAllBuffers(stream, bufferFactory);

    }


    @Test
    public void testAllBuffersReleasedOnSuccess() {

        final TrackingDataBufferFactory bufferFactory = new TrackingDataBufferFactory();
        final Context context = new Context();

        final Publisher<DataBuffer> stream =
                createTemplateEngine().processStream(
                        "<html><body>" + LONG_TEXT + "</body></html>", null, context, bufferFactory,
                        MediaType.TEXT_HTML, StandardCharsets.UTF_8, 64);

        // Consume the stream, releasing every emitted buffer as any downstream owner should
        Flux.from(stream).doOnNext(DataBufferUtils::release).then().block();

        Assertions.assertFalse(bufferFactory.allocatedBuffers.isEmpty());
        for (final NettyDataBuffer buffer : bufferFactory.allocatedBuffers) {
            Assertions.assertEquals(0, buffer.getNativeBuffer().refCnt());
        }

    }




    private static SpringWebFluxTemplateEngine createTemplateEngine() {
        final SpringWebFluxTemplateEngine templateEngine = new SpringWebFluxTemplateEngine();
        templateEngine.setTemplateResolver(new StringTemplateResolver());
        return templateEngine;
    }


    private static Context failingContext() {
        final Context context = new Context();
        context.setVariable("failingBean", new FailingBean());
        return context;
    }


    private static void assertStreamFailsReleasingAllBuffers(
            final Publisher<DataBuffer> stream, final TrackingDataBufferFactory bufferFactory) {

        try {
            // Consume the stream, releasing every emitted buffer as any downstream owner should
            Flux.from(stream).doOnNext(DataBufferUtils::release).then().block();
            Assertions.fail("Template processing should have failed");
        } catch (final Exception e) {
            // Expected: the template contains an expression that always throws
        }

        Assertions.assertFalse(bufferFactory.allocatedBuffers.isEmpty());
        for (final NettyDataBuffer buffer : bufferFactory.allocatedBuffers) {
            Assertions.assertEquals(
                    0, buffer.getNativeBuffer().refCnt(),
                    "DataBuffer allocated by the template engine was never released");
        }

    }




    public static class FailingBean {
        public String fail() {
            throw new RuntimeException("Exception raised on purpose during template processing");
        }
    }


    /*
     * DataBufferFactory delegating on a Netty-based factory and keeping track of every
     * allocated buffer, so that tests can verify they have all been released (refCnt == 0)
     * once the stream has terminated.
     */
    static final class TrackingDataBufferFactory implements DataBufferFactory {

        final List<NettyDataBuffer> allocatedBuffers = new CopyOnWriteArrayList<>();
        private final NettyDataBufferFactory delegate =
                new NettyDataBufferFactory(PooledByteBufAllocator.DEFAULT);

        private DataBuffer track(final DataBuffer dataBuffer) {
            this.allocatedBuffers.add((NettyDataBuffer) dataBuffer);
            return dataBuffer;
        }

        @Override
        public DataBuffer allocateBuffer() {
            return track(this.delegate.allocateBuffer());
        }

        @Override
        public DataBuffer allocateBuffer(final int initialCapacity) {
            return track(this.delegate.allocateBuffer(initialCapacity));
        }

        @Override
        public DataBuffer wrap(final ByteBuffer byteBuffer) {
            return this.delegate.wrap(byteBuffer);
        }

        @Override
        public DataBuffer wrap(final byte[] bytes) {
            return this.delegate.wrap(bytes);
        }

        @Override
        public DataBuffer join(final List<? extends DataBuffer> dataBuffers) {
            return this.delegate.join(dataBuffers);
        }

    }


}
