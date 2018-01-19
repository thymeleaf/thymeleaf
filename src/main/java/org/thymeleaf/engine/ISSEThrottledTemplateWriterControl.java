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
package org.thymeleaf.engine;

import java.io.IOException;

/**
 * <p>
 *   Interface modelling a series of methods for monitoring the status of the {@link java.io.Writer} being
 *   internally used for producing output in a throttled execution for SSE events.
 * </p>
 * <p>
 *   Note this interface is <strong>internal</strong> and there is normally no reason why it should be directly
 *   used in user's code.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.4
 *
 */
public interface ISSEThrottledTemplateWriterControl extends IThrottledTemplateWriterControl {

    void startEvent(final char[] id, final char[] event);
    void endEvent() throws IOException;

}
