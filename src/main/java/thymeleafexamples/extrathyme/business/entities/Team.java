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
package thymeleafexamples.extrathyme.business.entities;



public class Team {

    private final String code;
    private final String name;
    private final int points;
    private final int won;
    private final int drawn;
    private final int lost;
    
    
    
    
    public Team(final String code, final String name, 
            final int points, final int won, final int drawn, final int lost) {
        super();
        this.code = code;
        this.name = name;
        this.points = points;
        this.won = won;
        this.drawn = drawn;
        this.lost = lost;
    }



    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    public int getPoints() {
        return this.points;
    }

    public int getWon() {
        return this.won;
    }

    public int getDrawn() {
        return this.drawn;
    }

    public int getLost() {
        return this.lost;
    }


    
}
