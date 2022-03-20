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
package org.thymeleaf.templateparser.markup.decoupled;

/**
 * <p>
 *   Value class used for containing each of the attributes that should be injected during parsing into a
 *   template that is processed with associated <em>decoupled logic</em>.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class DecoupledInjectedAttribute {

    final char[] buffer;
    final int nameOffset;
    final int nameLen;
    final int operatorOffset;
    final int operatorLen;
    final int valueContentOffset;
    final int valueContentLen;
    final int valueOuterOffset;
    final int valueOuterLen;


    /*
     * We use a factory method here because we will not be using the same buffer specified for this method, so we
     * require a bit of processing before actually calling a constructor (not that this could not be done at the
     * constructor, but it gives us higher flexibility this way. Maybe in the future we reuse instances or something...
     */
    public static DecoupledInjectedAttribute createAttribute(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int operatorOffset, final int operatorLen,
            final int valueContentOffset, final int valueContentLen,
            final int valueOuterOffset, final int valueOuterLen) {

        final char[] newBuffer = new char[nameLen + operatorLen + valueOuterLen];
        System.arraycopy(buffer, nameOffset, newBuffer, 0, nameLen);
        System.arraycopy(buffer, operatorOffset, newBuffer, nameLen, operatorLen);
        System.arraycopy(buffer, valueOuterOffset, newBuffer, (nameLen + operatorLen), valueOuterLen);

        return new DecoupledInjectedAttribute(
                newBuffer,
                0, nameLen,
                (operatorOffset - nameOffset), operatorLen,
                (valueContentOffset - nameOffset), valueContentLen,
                (valueOuterOffset - nameOffset), valueOuterLen);
    }




    private DecoupledInjectedAttribute(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int operatorOffset, final int operatorLen,
            final int valueContentOffset, final int valueContentLen,
            final int valueOuterOffset, final int valueOuterLen) {

        super();

        this.buffer = buffer;
        this.nameOffset = nameOffset;
        this.nameLen = nameLen;
        this.operatorOffset = operatorOffset;
        this.operatorLen = operatorLen;
        this.valueContentOffset = valueContentOffset;
        this.valueContentLen = valueContentLen;
        this.valueOuterOffset = valueOuterOffset;
        this.valueOuterLen = valueOuterLen;

    }


    public String getName() {
        return new String(this.buffer, this.nameOffset, this.nameLen);
    }

    public String getOperator() {
        return new String(this.buffer, this.operatorOffset, this.operatorLen);
    }

    public String getValueContent() {
        return new String(this.buffer, this.valueContentOffset, this.valueContentLen);
    }

    public String getValueOuter() {
        return new String(this.buffer, this.valueOuterOffset, this.valueOuterLen);
    }




    @Override
    public String toString() {
        // The internal buffer should not be shared, it should only contain this attribute
        return new String(this.buffer);
    }


}

