/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2022, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.spring.reactive.exchange;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

public final class TestingWebSession implements WebSession {

    private final Map<String, Object> attributes;


    public TestingWebSession(final Map<String,Object> attributes) {
        super();
        this.attributes = attributes;
    }


    @Override
    public String getId() {
        return "sessiontestingid"; // No need for this to be non-fix for testing
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public <T> T getAttribute(final String s) {
        return (T) this.attributes.get(s);
    }

    @Override
    public <T> T getRequiredAttribute(final String name) {
        if (this.attributes.containsKey(name)) {
            return (T) this.attributes.get(name);
        }
        throw new IllegalArgumentException("name required attribute not present");
    }

    @Override
    public <T> T getAttributeOrDefault(final String name, final T defaultValue) {
        if (this.attributes.containsKey(name)) {
            return (T) this.attributes.get(name);
        }
        return defaultValue;
    }

    @Override
    public void start() {
        // Nothing to be done
    }

    @Override
    public boolean isStarted() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Mono<Void> changeSessionId() {
        return Mono.empty();
    }

    @Override
    public Mono<Void> invalidate() {
        // Nothing to be done
        return null;
    }

    @Override
    public Mono<Void> save() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isExpired() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Instant getCreationTime() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Instant getLastAccessTime() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMaxIdleTime(final Duration duration) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Duration getMaxIdleTime() {
        throw new UnsupportedOperationException();
    }

}
