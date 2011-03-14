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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import thymeleafexamples.stsm.business.entities.Variety;


public class VarietyRepository {

    private static final VarietyRepository INSTANCE = new VarietyRepository();
    private final Map<Integer,Variety> varietiesById;
    
    
    
    public static VarietyRepository getInstance() {
        return INSTANCE;
    }
    
    
    private VarietyRepository() {
        
        super();
        
        this.varietiesById = new LinkedHashMap<Integer,Variety>();
        
        final Variety var1 = new Variety();
        var1.setId(Integer.valueOf(1));
        var1.setName("Common Thyme");
        this.varietiesById.put(var1.getId(), var1);
        
        final Variety var2 = new Variety();
        var2.setId(Integer.valueOf(2));
        var2.setName("Citrus Thyme");
        this.varietiesById.put(var2.getId(), var2);
        
        final Variety var3 = new Variety();
        var3.setId(Integer.valueOf(3));
        var3.setName("Caraway Thyme");
        this.varietiesById.put(var3.getId(), var3);
        
        final Variety var4 = new Variety();
        var4.setId(Integer.valueOf(4));
        var4.setName("Woolly Thyme");
        this.varietiesById.put(var4.getId(), var4);
        
        final Variety var5 = new Variety();
        var5.setId(Integer.valueOf(5));
        var5.setName("Wild Thyme");
        this.varietiesById.put(var5.getId(), var5);
        
    }
    
    
    
    public List<Variety> findAll() {
        return new ArrayList<Variety>(this.varietiesById.values());
    }
    
    public Variety findById(final Integer id) {
        return this.varietiesById.get(id);
    }
    
    
    
}
