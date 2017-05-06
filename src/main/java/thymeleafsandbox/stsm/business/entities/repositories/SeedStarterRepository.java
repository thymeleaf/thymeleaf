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
package thymeleafsandbox.stsm.business.entities.repositories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import thymeleafsandbox.stsm.business.entities.SeedStarter;


@Repository
public class SeedStarterRepository {

    private final List<SeedStarter> seedStarters = new ArrayList<SeedStarter>();
    
    
    
    public SeedStarterRepository() {
        super();
    }
    
    

    /*
     * There is no real need to make these return Publishers instead of
     * values directly, but we will do it anyway to emulate an environment
     * in which data is obtained from a reactive data source.
     */


    public Flux<SeedStarter> findAll() {
        return Flux.fromIterable(this.seedStarters);
    }

    
    public Mono<Void> add(final SeedStarter seedStarter) {
        return Mono.fromRunnable(() -> this.seedStarters.add(seedStarter));
    }
    
    
    
}
