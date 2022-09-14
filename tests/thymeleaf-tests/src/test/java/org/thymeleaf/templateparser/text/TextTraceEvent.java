/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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

import java.util.Arrays;


public abstract class TextTraceEvent {

    public enum EventType {

        DOCUMENT_START("DS"), DOCUMENT_END("DE"),

        STANDALONE_ELEMENT_START("SES"), STANDALONE_ELEMENT_END("SEE"),

        OPEN_ELEMENT_START("OES"), OPEN_ELEMENT_END("OEE"),

        CLOSE_ELEMENT_START("CES"), CLOSE_ELEMENT_END("CEE"),

        ATTRIBUTE("A"), COMMENT("C"),

        TEXT("T");


        private String stringRepresentation;

        private EventType(final String stringRepresentation) {
            this.stringRepresentation = stringRepresentation;
        }

        public String toString() {
            return this.stringRepresentation;
        }

    }


    private final EventType eventType;
    final String[] contents;
    final int[] lines;
    final int[] cols;







    private TextTraceEvent(final EventType eventType, final int[] lines, final int[] cols, final String... contents) {

        super();

        if (eventType == null) {
            throw new IllegalArgumentException("Event type cannot be null");
        }

        this.eventType = eventType;
        this.contents = contents;
        this.lines = lines;
        this.cols = cols;

    }


    /**
     * <p>
     *   Returns the type of event.
     * </p>
     *
     * @return the type of event.
     */
    public EventType getEventType() {
        return this.eventType;
    }




    @Override
    public String toString() {

        final StringBuilder strBuilder = new StringBuilder();

        strBuilder.append(this.eventType);

        if (this.contents != null && this.lines != null & this.lines.length == this.contents.length) {

            for (int i = 0; i < this.contents.length; i++) {
                strBuilder.append('(');
                if (this.contents[i] != null) {
                    strBuilder.append(this.contents[i]);
                }
                strBuilder.append(')');
                strBuilder.append('{');
                strBuilder.append(String.valueOf(this.lines[i]));
                strBuilder.append(',');
                strBuilder.append(String.valueOf(this.cols[i]));
                strBuilder.append('}');
            }
            
            return strBuilder.toString();
            
        }

        
        if (this.contents != null) {
            for (final String contentItem : this.contents) {
                strBuilder.append('(');
                if (contentItem != null) {
                    strBuilder.append(contentItem);
                }
                strBuilder.append(')');
            }
        }

        strBuilder.append('{');
        strBuilder.append(String.valueOf(this.lines[0]));
        strBuilder.append(',');
        strBuilder.append(String.valueOf(this.cols[0]));
        strBuilder.append('}');

        return strBuilder.toString();

    }


    /**
     * <p>
     *   Checks whether two events are equal, but only comparing their types and contents, not the lines
     *   and columns in which they appeared.
     * </p>
     *
     * @param event the event this object will be matched for equality to.
     * @return true if both objects match type and content, false if not.
     */
    public boolean matchesTypeAndContent(final TextTraceEvent event) {
        if (this == event) {
            return true;
        }
        if (event == null) {
            return false;
        }
        if (this.eventType == null) {
            if (event.eventType != null) {
                return false;
            }
        } else if (!this.eventType.equals(event.eventType)) {
            return false;
        }
        if (this.contents == null) {
            if (event.contents != null) {
                return false;
            }
        } else if (!Arrays.equals(this.contents, event.contents)) {
            return false;
        }
        return true;
    }




    @Override
    public int hashCode() {
        int result = this.eventType.hashCode();
        result = 31 * result + Arrays.hashCode(this.contents);
        result = 31 * result + Arrays.hashCode(this.lines);
        result = 31 * result + Arrays.hashCode(this.cols);
        return result;
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final TextTraceEvent that = (TextTraceEvent) o;

        if (!Arrays.equals(this.cols, that.cols)) {
            return false;
        }
        if (!Arrays.equals(this.contents, that.contents)) {
            return false;
        }
        if (!Arrays.equals(this.lines, that.lines)) {
            return false;
        }

        return this.eventType == that.eventType;

    }






    public static final class DocumentStartTraceEvent extends TextTraceEvent {

        public DocumentStartTraceEvent(final long startTimeNanos, final int line, final int col) {
            super(EventType.DOCUMENT_START, new int[] {line}, new int[] {col}, String.valueOf(startTimeNanos));
        }

        public long getStartTimeNanos() {
            return Long.parseLong(this.contents[0]);
        }

        public int getLine() {
            return this.lines[0];
        }

        public int getCol() {
            return this.cols[0];
        }

    }

    public static final class DocumentEndTraceEvent extends TextTraceEvent {

        public DocumentEndTraceEvent(final long endTimeNanos, final long totalTimeNanos, final int line, final int col) {
            super(EventType.DOCUMENT_END, new int[] {line}, new int[] {col}, String.valueOf(endTimeNanos), String.valueOf(totalTimeNanos));
        }

        public long getStartTimeNanos() {
            return Long.parseLong(this.contents[0]);
        }

        public long getTotalTimeNanos() {
            return Long.parseLong(this.contents[1]);
        }

        public int getLine() {
            return this.lines[0];
        }

        public int getCol() {
            return this.cols[0];
        }

    }

    static abstract class AbstractContentTraceEvent extends TextTraceEvent {

        protected AbstractContentTraceEvent(final EventType type, final String content, final int line, final int col) {
            super(type, new int[] {line}, new int[] {col}, content);
            if (content == null) {
                throw new IllegalArgumentException("Contentn cannot be null");
            }
        }

        public String getContent() {
            return this.contents[0];
        }

        public int getLine() {
            return this.lines[0];
        }

        public int getCol() {
            return this.cols[0];
        }

    }

    public static final class TextTextTraceEvent extends AbstractContentTraceEvent {
        public TextTextTraceEvent(final String content, final int line, final int col) {
            super(EventType.TEXT, content, line, col);
        }
    }

    public static final class CommentTraceEvent extends AbstractContentTraceEvent {
        public CommentTraceEvent(final String content, final int line, final int col) {
            super(EventType.COMMENT, content, line, col);
        }
    }

    static abstract class AbstractElementTraceEvent extends TextTraceEvent {

        protected AbstractElementTraceEvent(final EventType type, final String elementName, final int line, final int col) {
            super(type, new int[] {line}, new int[] {col}, elementName);
            if (elementName == null) {
                throw new IllegalArgumentException("Element name cannot be null");
            }
        }

        public String getElementName() {
            return this.contents[0];
        }

        public int getLine() {
            return this.lines[0];
        }

        public int getCol() {
            return this.cols[0];
        }

    }

    public static final class StandaloneElementStartTraceEvent extends AbstractElementTraceEvent {
        public StandaloneElementStartTraceEvent(final String elementName, final int line, final int col) {
            super(EventType.STANDALONE_ELEMENT_START, elementName, line, col);
        }
    }

    public static final class StandaloneElementEndTraceEvent extends AbstractElementTraceEvent {
        public StandaloneElementEndTraceEvent(final String elementName, final int line, final int col) {
            super(EventType.STANDALONE_ELEMENT_END, elementName, line, col);
        }
    }

    public static final class OpenElementStartTraceEvent extends AbstractElementTraceEvent {
        public OpenElementStartTraceEvent(final String elementName, final int line, final int col) {
            super(EventType.OPEN_ELEMENT_START, elementName, line, col);
        }
    }

    public static final class OpenElementEndTraceEvent extends AbstractElementTraceEvent {
        public OpenElementEndTraceEvent(final String elementName, final int line, final int col) {
            super(EventType.OPEN_ELEMENT_END, elementName, line, col);
        }
    }

    public static final class CloseElementStartTraceEvent extends AbstractElementTraceEvent {
        public CloseElementStartTraceEvent(final String elementName, final int line, final int col) {
            super(EventType.CLOSE_ELEMENT_START, elementName, line, col);
        }
    }

    public static final class CloseElementEndTraceEvent extends AbstractElementTraceEvent {
        public CloseElementEndTraceEvent(final String elementName, final int line, final int col) {
            super(EventType.CLOSE_ELEMENT_END, elementName, line, col);
        }
    }

    public static final class AttributeTraceEvent extends TextTraceEvent {

        public AttributeTraceEvent(
                final String name,
                final int nameLine, final int nameCol,
                final String operator,
                final int operatorLine, final int operatorCol,
                final String outerValue,
                final int valueLine, final int valueCol) {
            super(EventType.ATTRIBUTE, new int[] {nameLine, operatorLine, valueLine}, new int[] {nameCol, operatorCol, valueCol}, name, operator, outerValue);
            if (name == null || name.trim().equals("")) {
                throw new IllegalArgumentException("Attribute name cannot be null or empty");
            }
        }

        public String getName() {
            return this.contents[0];
        }

        public String getOperator() {
            return this.contents[1];
        }

        public String getOuterValue() {
            return this.contents[2];
        }

        public int getNameLine() {
            return this.lines[0];
        }

        public int getNameCol() {
            return this.cols[0];
        }

        public int getOperatorLine() {
            return this.lines[1];
        }

        public int getOperatorCol() {
            return this.cols[1];
        }

        public int getOuterValueLine() {
            return this.lines[2];
        }

        public int getOuterValueCol() {
            return this.cols[2];
        }

    }



}