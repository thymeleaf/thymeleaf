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
        
        teams.add(new Team("SPC", "Spearmint Caterpillars", 73, 21, 10, 5));
        teams.add(new Team("BAD", "Basil Dragons", 72, 21, 9, 6));
        teams.add(new Team("SPS", "Sweet Paprika Savages", 57, 15, 12, 9));
        teams.add(new Team("PAW", "Parsley Warriors", 54, 15, 9, 12));
        teams.add(new Team("PCO", "Polar Corianders", 49, 11, 16, 9));
        teams.add(new Team("CSA", "Cinnamon Sailors", 48, 13, 9, 14));
        teams.add(new Team("LTR", "Laurel Troglodytes", 41, 10, 11, 15));
        teams.add(new Team("ARP", "Angry Red Peppers", 32, 8, 8, 20));
        teams.add(new Team("ROS", "Rosemary 75ers", 32, 7, 11, 18));
        teams.add(new Team("SHU", "Saffron Hunters", 31, 8, 7, 21));
        
        return teams;
        
    }
    
    
    
}
