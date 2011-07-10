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
package thymeleafexamples.extrathyme.business.entities.repositories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import thymeleafexamples.extrathyme.business.entities.Team;


@Repository
public class TeamRepository {

    
    public TeamRepository() {
        super();
    }
    
    
    public List<Team> findAllTeams() {
        
        final List<Team> teams = new ArrayList<Team>();
        
        teams.add(new Team("BAD", "Basil Dragons", 75, 68, 32));
        teams.add(new Team("PAW", "Parsley Warriors", 67, 67, 45));
        teams.add(new Team("ACO", "Antarctica Corianders", 61, 58, 55));
        teams.add(new Team("CSA", "Cinnamon Sailors", 58, 48, 45));
        teams.add(new Team("RHP", "Raging Hot Paprikas", 54, 49, 61));
        teams.add(new Team("ROS", "Rosemary 75ers", 53, 43, 52));
        
        return teams;
        
    }
    
    
    
}
