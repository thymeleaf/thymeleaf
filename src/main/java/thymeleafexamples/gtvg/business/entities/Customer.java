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
package thymeleafexamples.gtvg.business.entities;

import java.util.Calendar;

public class Customer {

    private Integer id = null;
    private String name = null;
    private Calendar customerSince = null;

    
    public Customer() {
        super();
    }


    public Integer getId() {
        return this.id;
    }
    public void setId(final Integer id) {
        this.id = id;
    }
    

    public String getName() {
        return this.name;
    }
    public void setName(final String name) {
        this.name = name;
    }
    

    public Calendar getCustomerSince() {
        return this.customerSince;
    }
    public void setCustomerSince(final Calendar customerSince) {
        this.customerSince = customerSince;
    }
    
}
