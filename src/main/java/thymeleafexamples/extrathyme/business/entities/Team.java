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

    private String name = null;
    private Integer points = null;
    
    
    
    
    public Team() {
        super();
    }

    public Team(final String name, final int points) {
        super();
        this.name = name;
        this.points = Integer.valueOf(points);
    }



    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Integer getPoints() {
        return this.points;
    }

    public void setPoints(final Integer points) {
        this.points = points;
    }



    @Override
    public String toString() {
        return "Team [name=" + this.name + ", points=" + this.points + "]";
    }

    
}
