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
package org.thymeleaf.expression;

import java.util.Set;

import org.thymeleaf.context.IExpressionContext;


/**
 * <p>
 *   Container object for all the expression utility objects to be made available in expressions. The aim of objects
 *   implementing this interface is to keep most expression utility objects instanced for the complete execution of
 *   a template, always being able to update themselves at any moment in time by means of a reference to the
 *   {@link IExpressionContext} that is using them.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public interface IExpressionObjects {


    public int size();
    public boolean containsObject(final String name);
    public Set<String> getObjectNames();
    public Object getObject(final String name);

}
