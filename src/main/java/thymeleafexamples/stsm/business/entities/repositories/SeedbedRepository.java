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
package thymeleafexamples.stsm.business.entities.repositories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import thymeleafexamples.stsm.business.entities.Seedbed;


@Repository
public class SeedbedRepository {

    private final List<Seedbed> seedbeds = new ArrayList<Seedbed>();
    
    
    
    public SeedbedRepository() {
        super();
    }
    
    
    
    public List<Seedbed> findAll() {
        return new ArrayList<Seedbed>(this.seedbeds);
    }

    
    public void add(final Seedbed seedbed) {
        this.seedbeds.add(seedbed);
        System.out.println("New size: " + this.seedbeds.size());
    }
    
    
    
}
