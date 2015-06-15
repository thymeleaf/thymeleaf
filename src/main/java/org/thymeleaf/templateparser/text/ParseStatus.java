/*
 * =============================================================================
 * 
 *   Copyright (c) 2012-2014, The ATTOPARSER team (http://www.attoparser.org)
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
package org.thymeleaf.templateparser.text;



/**
 * <p>
 *   Class used for reporting the status of current parsing operations to handlers.
 * </p>
 * <p>
 *   Instances of this class operate at a very low level, and are only useful in very specific scenarios,
 *   so most {@link org.attoparser.IMarkupHandler} implementations should just ignore its existence and
 *   consider it only for internal use.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class ParseStatus {

    int offset;
    int line;
    int col;
    boolean inStructure;



    /**
     * <p>
     *   Builds a new instance of this class.
     * </p>
     * <p>
     *   This constructor is for internal use. As a general rule of thumb, there is no reason why any user of this
     *   class would need to call this constructor.
     * </p>
     */
    public ParseStatus() {
        super();
    }


    /**
     * <p>
     *   Returns the line in the document the parser is currently located at.
     * </p>
     * <p>
     *   Note this should not be used for event reference, because the parser cursor might be ahead of the events
     *   it is reporting. In order to know the lines and cols an event was found at, use the <tt>(line,col)</tt>
     *   pairs reported with every event handler.
     * </p>
     *
     * @return the line number.
     */
    public int getLine() {
        return this.line;
    }


    /**
     * <p>
     *   Returns the column in the current line in the document the parser is currently located at.
     * </p>
     * <p>
     *   Note this should not be used for event reference, because the parser cursor might be ahead of the events
     *   it is reporting. In order to know the lines and cols an event was found at, use the <tt>(line,col)</tt>
     *   pairs reported with every event handler.
     * </p>
     *
     * @return the column number.
     */
    public int getCol() {
        return this.col;
    }

}
