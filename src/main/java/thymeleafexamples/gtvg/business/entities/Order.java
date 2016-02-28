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
import java.util.LinkedHashSet;
import java.util.Set;

public class Order {

    private Integer id = null;
    private Calendar date = null;
    private Customer customer = null;
    private Set<OrderLine> orderLines = new LinkedHashSet<OrderLine>();
    
    public Order() {
        super();
    }

    
    public Integer getId() {
        return this.id;
    }
    public void setId(final Integer id) {
        this.id = id;
    }
    

    public Calendar getDate() {
        return this.date;
    }
    public void setDate(final Calendar date) {
        this.date = date;
    }
    

    public Customer getCustomer() {
        return this.customer;
    }
    public void setCustomer(final Customer customer) {
        this.customer = customer;
    }
    

    public Set<OrderLine> getOrderLines() {
        return this.orderLines;
    }
    
}
