/*
 * =============================================================================
 * 
 *   Copyright (c) 2011, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.util;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.thymeleaf.util.Validate;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
final class NumberDecimalFormatSymbols extends DecimalFormatSymbols {

        private static final long serialVersionUID = 2760073908451356276L;

        
        NumberDecimalFormatSymbols(final NumberPointType decimalPointType, final NumberPointType thousandsPointType, final Locale locale) {
            
            super(Locale.US);
            
            Validate.notNull(decimalPointType, "Decimal point type cannot be null");
            Validate.notNull(thousandsPointType, "Thousands point type cannot be null");
            Validate.notNull(locale, "Locale cannot be null");
            
            switch (decimalPointType) {
                case POINT :
                    setDecimalSeparator('.');
                    break;
                case COMMA :
                    setDecimalSeparator(',');
                    break;
                case DEFAULT :
                    final DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance(locale);
                    setDecimalSeparator(dfs.getDecimalSeparator());
                    break;
                case NONE :
                    // This should never happen
                    setDecimalSeparator('?');
                    break;
            }
            switch (thousandsPointType) {
                case POINT :
                    setGroupingSeparator('.');
                    break;
                case COMMA :
                    setGroupingSeparator(',');
                    break;
                case DEFAULT :
                    final DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance(locale);
                    setGroupingSeparator(dfs.getGroupingSeparator());
                    break;
                case NONE :
                    // This should never be shown
                    setGroupingSeparator('?');
                    break;
            }
        }
    
}
