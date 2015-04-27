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
package org.thymeleaf.standard.processor;

import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.IElementStructureHandler;
import org.thymeleaf.engine.ParsedFragmentMarkup;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.standard.expression.Assignation;
import org.thymeleaf.standard.expression.AssignationSequence;
import org.thymeleaf.standard.expression.FragmentSelection;
import org.thymeleaf.standard.expression.FragmentSelectionUtils;
import org.thymeleaf.standard.expression.IStandardExpression;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class StandardIncludeTagProcessor extends AbstractStandardFragmentInsertionTagProcessor {

    public static final int PRECEDENCE = 100;
    public static final String ATTR_NAME = "include";





    public StandardIncludeTagProcessor() {
        super(ATTR_NAME, PRECEDENCE, false, true);
    }



}
