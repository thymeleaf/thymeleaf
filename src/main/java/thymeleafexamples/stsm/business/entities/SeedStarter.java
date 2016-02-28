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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SeedStarter {

    private Integer id = null;
    private Date datePlanted = null;
    private Boolean covered = null;
    private Type type = Type.PLASTIC;
    private Feature[] features = null;
    
    private List<Row> rows = new ArrayList<Row>();
    
    
    public SeedStarter() {
        super();
    }


    public Integer getId() {
        return this.id;
    }


    public void setId(final Integer id) {
        this.id = id;
    }


    public Date getDatePlanted() {
        return this.datePlanted;
    }


    public void setDatePlanted(final Date datePlanted) {
        this.datePlanted = datePlanted;
    }


    public Boolean getCovered() {
        return this.covered;
    }


    public void setCovered(final Boolean covered) {
        this.covered = covered;
    }


    public Type getType() {
        return this.type;
    }


    public void setType(final Type type) {
        this.type = type;
    }


    public Feature[] getFeatures() {
        return this.features;
    }


    public void setFeatures(final Feature[] features) {
        this.features = features;
    }


    public List<Row> getRows() {
        return this.rows;
    }


    @Override
    public String toString() {
        return "SeedStarter [id=" + this.id + ", datePlanted=" + this.datePlanted
                + ", covered=" + this.covered + ", type=" + this.type + ", features="
                + Arrays.toString(this.features) + ", rows=" + this.rows + "]";
    }
    
}
