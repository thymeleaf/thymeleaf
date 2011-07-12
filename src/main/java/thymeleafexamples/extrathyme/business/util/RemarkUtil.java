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
package thymeleafexamples.extrathyme.business.util;

import thymeleafexamples.extrathyme.business.entities.Remark;



public class RemarkUtil {


    public static Remark getRemarkForPosition(final Integer position) {
        
        if (position == null) {
            return null;
        }
        switch (position.intValue()) {
            case 1: return Remark.WORLD_CHAMPIONS_LEAGUE;
            case 2:
            case 3: return Remark.CONTINENTAL_PLAYOFFS;
            case 10: return Remark.RELEGATION;
        }
        return null;
        
    }
    

    private RemarkUtil() {
        super();
    }
    
}
