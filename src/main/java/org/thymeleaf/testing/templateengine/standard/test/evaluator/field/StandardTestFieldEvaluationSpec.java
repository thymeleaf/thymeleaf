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
package org.thymeleaf.testing.templateengine.standard.test.evaluator.field;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.testing.templateengine.standard.test.data.StandardTestFieldNaming;
import org.thymeleaf.testing.templateengine.standard.test.evaluator.field.defaultevaluators.DefaultCacheStandardTestFieldEvaluator;
import org.thymeleaf.testing.templateengine.standard.test.evaluator.field.defaultevaluators.DefaultContextStandardTestFieldEvaluator;
import org.thymeleaf.testing.templateengine.standard.test.evaluator.field.defaultevaluators.DefaultExactMatchStandardTestFieldEvaluator;
import org.thymeleaf.testing.templateengine.standard.test.evaluator.field.defaultevaluators.DefaultExceptionMessagePatternTestFieldEvaluator;
import org.thymeleaf.testing.templateengine.standard.test.evaluator.field.defaultevaluators.DefaultExceptionTestFieldEvaluator;
import org.thymeleaf.testing.templateengine.standard.test.evaluator.field.defaultevaluators.DefaultExtendsStandardTestFieldEvaluator;
import org.thymeleaf.testing.templateengine.standard.test.evaluator.field.defaultevaluators.DefaultFragmentStandardTestFieldEvaluator;
import org.thymeleaf.testing.templateengine.standard.test.evaluator.field.defaultevaluators.DefaultInputStandardTestFieldEvaluator;
import org.thymeleaf.testing.templateengine.standard.test.evaluator.field.defaultevaluators.DefaultMessagesStandardTestFieldEvaluator;
import org.thymeleaf.testing.templateengine.standard.test.evaluator.field.defaultevaluators.DefaultOutputStandardTestFieldEvaluator;
import org.thymeleaf.testing.templateengine.standard.test.evaluator.field.defaultevaluators.DefaultTemplateModeStandardTestFieldEvaluator;
import org.thymeleaf.testing.templateengine.standard.test.evaluator.field.defaultevaluators.DefaultTestNameStandardTestFieldEvaluator;
import org.thymeleaf.util.Validate;






public class StandardTestFieldEvaluationSpec {

    
    
    public static final StandardTestFieldEvaluationSpec TEST_NAME_FIELD_SPEC = 
            new StandardTestFieldEvaluationSpec(
                    StandardTestFieldNaming.FIELD_NAME_NAME, 
                    DefaultTestNameStandardTestFieldEvaluator.INSTANCE);
    
    public static final StandardTestFieldEvaluationSpec TEMPLATE_MODE_FIELD_SPEC = 
            new StandardTestFieldEvaluationSpec(
                    StandardTestFieldNaming.FIELD_NAME_TEMPLATE_MODE, 
                    DefaultTemplateModeStandardTestFieldEvaluator.INSTANCE);
    
    public static final StandardTestFieldEvaluationSpec CACHE_FIELD_SPEC = 
            new StandardTestFieldEvaluationSpec(
                    StandardTestFieldNaming.FIELD_NAME_CACHE, 
                    DefaultCacheStandardTestFieldEvaluator.INSTANCE);
    
    public static final StandardTestFieldEvaluationSpec CONTEXT_FIELD_SPEC = 
            new StandardTestFieldEvaluationSpec(
                    StandardTestFieldNaming.FIELD_NAME_CONTEXT, 
                    DefaultContextStandardTestFieldEvaluator.INSTANCE);
    
    public static final StandardTestFieldEvaluationSpec MESSAGES_FIELD_SPEC = 
            new StandardTestFieldEvaluationSpec(
                    StandardTestFieldNaming.FIELD_NAME_MESSAGES, 
                    DefaultMessagesStandardTestFieldEvaluator.INSTANCE);
    
    public static final StandardTestFieldEvaluationSpec FRAGMENT_FIELD_SPEC = 
            new StandardTestFieldEvaluationSpec(
                    StandardTestFieldNaming.FIELD_NAME_FRAGMENT, 
                    DefaultFragmentStandardTestFieldEvaluator.INSTANCE);
    
    public static final StandardTestFieldEvaluationSpec INPUT_FIELD_SPEC = 
            new StandardTestFieldEvaluationSpec(
                    StandardTestFieldNaming.FIELD_NAME_INPUT, 
                    DefaultInputStandardTestFieldEvaluator.INSTANCE);
    
    public static final StandardTestFieldEvaluationSpec OUTPUT_FIELD_SPEC = 
            new StandardTestFieldEvaluationSpec(
                    StandardTestFieldNaming.FIELD_NAME_OUTPUT, 
                    DefaultOutputStandardTestFieldEvaluator.INSTANCE);
    
    public static final StandardTestFieldEvaluationSpec EXCEPTION_FIELD_SPEC = 
            new StandardTestFieldEvaluationSpec(
                    StandardTestFieldNaming.FIELD_NAME_EXCEPTION, 
                    DefaultExceptionTestFieldEvaluator.INSTANCE);
    
    public static final StandardTestFieldEvaluationSpec EXCEPTION_MESSAGE_PATTERN_FIELD_SPEC = 
            new StandardTestFieldEvaluationSpec(
                    StandardTestFieldNaming.FIELD_NAME_EXCEPTION_MESSAGE_PATTERN, 
                    DefaultExceptionMessagePatternTestFieldEvaluator.INSTANCE);
    
    public static final StandardTestFieldEvaluationSpec EXACT_MATCH_FIELD_SPEC = 
            new StandardTestFieldEvaluationSpec(
                    StandardTestFieldNaming.FIELD_NAME_EXACT_MATCH, 
                    DefaultExactMatchStandardTestFieldEvaluator.INSTANCE);
    
    public static final StandardTestFieldEvaluationSpec EXTENDS_FIELD_SPEC = 
            new StandardTestFieldEvaluationSpec(
                    StandardTestFieldNaming.FIELD_NAME_EXTENDS, 
                    DefaultExtendsStandardTestFieldEvaluator.INSTANCE);


    
    public static final Set<StandardTestFieldEvaluationSpec> STANDARD_TEST_FIELD_SPECS =
            Collections.unmodifiableSet(
                    new HashSet<StandardTestFieldEvaluationSpec>(Arrays.asList(
                            new StandardTestFieldEvaluationSpec[] {
                                    StandardTestFieldEvaluationSpec.TEST_NAME_FIELD_SPEC,
                                    StandardTestFieldEvaluationSpec.TEMPLATE_MODE_FIELD_SPEC,
                                    StandardTestFieldEvaluationSpec.CACHE_FIELD_SPEC,
                                    StandardTestFieldEvaluationSpec.CONTEXT_FIELD_SPEC,
                                    StandardTestFieldEvaluationSpec.MESSAGES_FIELD_SPEC,
                                    StandardTestFieldEvaluationSpec.FRAGMENT_FIELD_SPEC,
                                    StandardTestFieldEvaluationSpec.INPUT_FIELD_SPEC,
                                    StandardTestFieldEvaluationSpec.OUTPUT_FIELD_SPEC,
                                    StandardTestFieldEvaluationSpec.EXCEPTION_FIELD_SPEC,
                                    StandardTestFieldEvaluationSpec.EXCEPTION_MESSAGE_PATTERN_FIELD_SPEC,
                                    StandardTestFieldEvaluationSpec.EXACT_MATCH_FIELD_SPEC,
                                    StandardTestFieldEvaluationSpec.EXTENDS_FIELD_SPEC
                            })));
    
    
    
    
    private final String name;
    private final IStandardTestFieldEvaluator evaluator;
    
    
    
    public StandardTestFieldEvaluationSpec(final String name, final IStandardTestFieldEvaluator evaluator) {
        super();
        Validate.notNull(name, "Field name cannot null");
        Validate.notNull(evaluator, "Field evaluator cannot be null");
        this.name = name;
        this.evaluator = evaluator;
    }


    public String getName() {
        return this.name;
    }


    public IStandardTestFieldEvaluator getEvaluator() {
        return this.evaluator;
    }
    
    
}
