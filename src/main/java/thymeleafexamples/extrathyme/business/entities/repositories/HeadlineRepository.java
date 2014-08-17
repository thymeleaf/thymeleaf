/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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
package thymeleafexamples.extrathyme.business.entities.repositories;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.stereotype.Repository;
import thymeleafexamples.extrathyme.business.entities.Headline;


@Repository
public class HeadlineRepository {

    
    public HeadlineRepository() {
        super();
    }
    
    
    public List<Headline> findAllHeadlines() {
        
        final List<Headline> headlines = new ArrayList<Headline>();
        
        headlines.add(new Headline(Calendar.getInstance(), "Spearmint Caterpillars 1 - 0 Parsley Warriors"));
        headlines.add(new Headline(Calendar.getInstance(), "Laurel Troglodytes 1 - 1 Rosemary 75ers"));
        headlines.add(new Headline(Calendar.getInstance(), "Saffron Hunters 0 - 2 Polar Corianders"));
        headlines.add(new Headline(Calendar.getInstance(), "Angry Red Peppers 4 - 2 Basil Dragons"));
        headlines.add(new Headline(Calendar.getInstance(), "Sweet Paprika Savages 1 - 3 Cinnamon Sailors"));
        
        return headlines;
        
    }
    
    
    
}
