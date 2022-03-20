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
package org.thymeleaf.dialect;

/**
 * <p>
 *   Base interface for all dialects created for extending the features available during
 *   Thymeleaf's template processing.
 * </p>
 * <p>
 *   Note this is a base interface without much meaning of its own. Instead, dialects
 *   should implement one or several of the following sub-interfaces:
 * </p>
 * <ul>
 *   <li>{@link IProcessorDialect} for dialects providing processors.</li>
 *   <li>{@link IPreProcessorDialect} for dialects providing pre-processors.</li>
 *   <li>{@link IPostProcessorDialect} for dialects providing post-processors.</li>
 *   <li>{@link IExpressionObjectDialect} for dialects providing expression objects.</li>
 *   <li>{@link IExecutionAttributeDialect} for dialects providing execution attributes.</li>
 * </ul>
 * <p>
 *   Note a class with this name existed since 1.0, but it was completely reimplemented
 *   in Thymeleaf 3.0
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @see IProcessorDialect
 * @see IPreProcessorDialect
 * @see IPostProcessorDialect
 * @see IExpressionObjectDialect
 * @see IExecutionAttributeDialect
 * @see org.thymeleaf.standard.StandardDialect
 *
 * @since 3.0.0
 *
 */
public interface IDialect {

    public String getName();

}
