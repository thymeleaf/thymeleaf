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
package thymeleafexamples.gtvg.business.entities.repositories;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import thymeleafexamples.gtvg.business.entities.Customer;
import thymeleafexamples.gtvg.business.util.CalendarUtil;


public class CustomerRepository {

    private static final CustomerRepository INSTANCE = new CustomerRepository();
    private final Map<Integer,Customer> customersById;

    
    
    public static CustomerRepository getInstance() {
        return INSTANCE;
    }
   
    
    
    private CustomerRepository() {
        
        super();
        
        this.customersById = new LinkedHashMap<Integer, Customer>();
        
        final Customer cust1 = new Customer();
        cust1.setId(Integer.valueOf(1));
        cust1.setName("James Cucumber");
        cust1.setCustomerSince(CalendarUtil.calendarFor(2006, 4, 2, 13, 20));
        this.customersById.put(cust1.getId(), cust1);
        
        final Customer cust2 = new Customer();
        cust2.setId(Integer.valueOf(2));
        cust2.setName("Anna Lettuce");
        cust2.setCustomerSince(CalendarUtil.calendarFor(2005, 1, 30, 17, 14));
        this.customersById.put(cust2.getId(), cust2);
        
        final Customer cust3 = new Customer();
        cust3.setId(Integer.valueOf(3));
        cust3.setName("Boris Tomato");
        cust3.setCustomerSince(CalendarUtil.calendarFor(2008, 12, 2, 9, 53));
        this.customersById.put(cust3.getId(), cust3);
        
        final Customer cust4 = new Customer();
        cust4.setId(Integer.valueOf(4));
        cust4.setName("Shannon Parsley");
        cust4.setCustomerSince(CalendarUtil.calendarFor(2009, 3, 24, 10, 45));
        this.customersById.put(cust4.getId(), cust4);
        
        final Customer cust5 = new Customer();
        cust5.setId(Integer.valueOf(5));
        cust5.setName("Susan Cheddar");
        cust5.setCustomerSince(CalendarUtil.calendarFor(2007, 10, 1, 15, 02));
        this.customersById.put(cust5.getId(), cust5);
        
        final Customer cust6 = new Customer();
        cust6.setId(Integer.valueOf(6));
        cust6.setName("George Garlic");
        cust6.setCustomerSince(CalendarUtil.calendarFor(2010, 5, 18, 20, 30));
        this.customersById.put(cust6.getId(), cust6);
        
    }
    
    
    
    public List<Customer> findAll() {
        return new ArrayList<Customer>(this.customersById.values());
    }
    
    public Customer findById(final Integer id) {
        return this.customersById.get(id);
    }
    
    
    
}
