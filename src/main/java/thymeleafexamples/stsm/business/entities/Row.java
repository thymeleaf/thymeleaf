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
package thymeleafexamples.stsm.business.entities;



public class Row {

    private Variety variety = null;
    private Integer seedsPerCell = null;
    
    
    
    
    public Row() {
        super();
    }


    public Variety getVariety() {
        return this.variety;
    }


    public void setVariety(final Variety variety) {
        this.variety = variety;
    }

    public Integer getSeedsPerCell() {
        return this.seedsPerCell;
    }

    public void setSeedsPerCell(final Integer seedsPerCell) {
        this.seedsPerCell = seedsPerCell;
    }


    @Override
    public String toString() {
        return "Row [variety=" + this.variety + ", seedsPerCell=" + this.seedsPerCell + "]";
    }

    
}
