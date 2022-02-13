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

import org.thymeleaf.templateparser.raw.IRawHandler;
import org.thymeleaf.templateparser.raw.RawParseException;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class TemplateHandlerAdapterRawHandler implements IRawHandler {

    private final String templateName;
    private final ITemplateHandler templateHandler;
    private final int lineOffset;
    private final int colOffset;


    public TemplateHandlerAdapterRawHandler(final String templateName,
                                            final ITemplateHandler templateHandler,
                                            final int lineOffset, final int colOffset) {
        super();

        Validate.notNull(templateHandler, "Template handler cannot be null");

        this.templateName = templateName;

        this.templateHandler = templateHandler;

        // These cannot be null
        this.lineOffset = (lineOffset > 0 ? lineOffset - 1 : lineOffset); // line n for offset will be line 1 for the newly parsed template
        this.colOffset = (colOffset > 0 ? colOffset - 1 : colOffset); // line n for offset will be line 1 for the newly parsed template

    }



    public void handleDocumentStart(
            final long startTimeNanos, final int line, final int col)
            throws RawParseException {
        // The reported times refer to parsing times, and processing a template is more complex, so we'll just ignore the info
        this.templateHandler.handleTemplateStart(TemplateStart.TEMPLATE_START_INSTANCE);
    }


    public void handleDocumentEnd(
            final long endTimeNanos, final long totalTimeNanos, final int line, final int col)
            throws RawParseException {
        // The reported times refer to parsing times, and processing a template is more complex, so we'll just ignore the info
        this.templateHandler.handleTemplateEnd(TemplateEnd.TEMPLATE_END_INSTANCE);
    }



    public void handleText(
            final char[] buffer,
            final int offset, final int len,
            final int line, final int col)
            throws RawParseException {
        this.templateHandler.handleText(
                new Text(new String(buffer, offset, len), this.templateName, this.lineOffset + line, (line == 1? this.colOffset : 0) + col));
    }


}
