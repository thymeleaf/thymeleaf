package org.thymeleaf.parser;

import org.thymeleaf.context.AbstractContext;
import org.thymeleaf.context.IContextExecutionInfo;

public class TestContext extends AbstractContext
{
    @Override
    protected IContextExecutionInfo buildContextExecutionInfo(String templateName)
    {
        throw new UnsupportedOperationException("not implemented");
    }
}
