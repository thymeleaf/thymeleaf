/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.util;

import java.io.IOException;
import java.io.Writer;

import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.standard.expression.Fragment;


/**
 * <p>
 *   Character sequence that performs a lazy writing operation of a {@link Fragment} into an output {@link Writer}.
 * </p>
 * <p>
 *   It is used sometimes internally by the engine in order to avoid the creation of extra String objects in
 *   some scenarios (e.g. th:utext writing Fragments).
 * </p>
 * <p>
 *   This is mostly an <strong>internal class</strong>, and its use is not recommended from user's code.
 * </p>
 * <p>
 *   This class is <strong>not</strong> thread-safe.
 * </p>
 *
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class LazyFragmentCharSequence extends AbstractLazyCharSequence {

    private final Fragment fragment;


    public LazyFragmentCharSequence(final Fragment fragment) {

        super();

        if (fragment == null) {
            throw new IllegalArgumentException("Fragment is null, which is forbidden");
        }

        this.fragment = fragment;

    }




    @Override
    protected String resolveText() {
        final Writer stringWriter = new FastStringWriter();
        try {
            this.fragment.write(stringWriter);
        } catch (final IOException e) {
            throw new TemplateProcessingException("Error processing lazy fragment text", e);
        }
        return stringWriter.toString();
    }


    @Override
    protected void writeUnresolved(final Writer writer) throws IOException {
        this.fragment.write(writer);
    }


}
