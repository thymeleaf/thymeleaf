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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import thymeleafexamples.gtvg.business.entities.Customer;
import thymeleafexamples.gtvg.business.entities.Order;
import thymeleafexamples.gtvg.business.entities.OrderLine;
import thymeleafexamples.gtvg.business.entities.Product;
import thymeleafexamples.gtvg.business.util.CalendarUtil;


public class OrderRepository {

    
    private static final OrderRepository INSTANCE = new OrderRepository();
    private final Map<Integer,Order> ordersById;
    private final Map<Integer,List<Order>> ordersByCustomerId;
    
    

    
    public static OrderRepository getInstance() {
        return INSTANCE;
    }
    
    
    
    private OrderRepository() {
        
        super();
        
        this.ordersById = new LinkedHashMap<Integer, Order>();
        this.ordersByCustomerId = new LinkedHashMap<Integer, List<Order>>();
        
        final Customer cust1 = CustomerRepository.getInstance().findById(Integer.valueOf(1));
        this.ordersByCustomerId.put(cust1.getId(), new ArrayList<Order>());
        
        final Customer cust4 = CustomerRepository.getInstance().findById(Integer.valueOf(4));
        this.ordersByCustomerId.put(cust4.getId(), new ArrayList<Order>());
        
        final Customer cust6 = CustomerRepository.getInstance().findById(Integer.valueOf(6));
        this.ordersByCustomerId.put(cust6.getId(), new ArrayList<Order>());

        
        final Product prod1 = ProductRepository.getInstance().findById(Integer.valueOf(1));
        final Product prod2 = ProductRepository.getInstance().findById(Integer.valueOf(2));
        final Product prod3 = ProductRepository.getInstance().findById(Integer.valueOf(3));
        final Product prod4 = ProductRepository.getInstance().findById(Integer.valueOf(4));
        
        
        final Order order1 = new Order();
        order1.setId(Integer.valueOf(1));
        order1.setCustomer(cust4);
        order1.setDate(CalendarUtil.calendarFor(2009, 1, 12, 10, 23));
        this.ordersById.put(order1.getId(), order1);
        this.ordersByCustomerId.get(cust4.getId()).add(order1);

        final OrderLine orderLine11 = new OrderLine();
        orderLine11.setProduct(prod2);
        orderLine11.setAmount(Integer.valueOf(2));
        orderLine11.setPurchasePrice(new BigDecimal("0.99"));
        order1.getOrderLines().add(orderLine11);

        final OrderLine orderLine12 = new OrderLine();
        orderLine12.setProduct(prod3);
        orderLine12.setAmount(Integer.valueOf(4));
        orderLine12.setPurchasePrice(new BigDecimal("2.50"));
        order1.getOrderLines().add(orderLine12);

        final OrderLine orderLine13 = new OrderLine();
        orderLine13.setProduct(prod4);
        orderLine13.setAmount(Integer.valueOf(1));
        orderLine13.setPurchasePrice(new BigDecimal("15.50"));
        order1.getOrderLines().add(orderLine13);
        
        
        final Order order2 = new Order();
        order2.setId(Integer.valueOf(2));
        order2.setCustomer(cust6);
        order2.setDate(CalendarUtil.calendarFor(2010, 6, 9, 21, 01));
        this.ordersById.put(order2.getId(), order2);
        this.ordersByCustomerId.get(cust6.getId()).add(order2);

        final OrderLine orderLine21 = new OrderLine();
        orderLine21.setProduct(prod1);
        orderLine21.setAmount(Integer.valueOf(5));
        orderLine21.setPurchasePrice(new BigDecimal("3.75"));
        order2.getOrderLines().add(orderLine21);

        final OrderLine orderLine22 = new OrderLine();
        orderLine22.setProduct(prod4);
        orderLine22.setAmount(Integer.valueOf(2));
        orderLine22.setPurchasePrice(new BigDecimal("17.99"));
        order2.getOrderLines().add(orderLine22);

        
        
        final Order order3 = new Order();
        order3.setId(Integer.valueOf(3));
        order3.setCustomer(cust1);
        order3.setDate(CalendarUtil.calendarFor(2010, 7, 18, 22, 32));
        this.ordersById.put(order3.getId(), order3);
        this.ordersByCustomerId.get(cust4.getId()).add(order3);

        final OrderLine orderLine32 = new OrderLine();
        orderLine32.setProduct(prod1);
        orderLine32.setAmount(Integer.valueOf(8));
        orderLine32.setPurchasePrice(new BigDecimal("5.99"));
        order3.getOrderLines().add(orderLine32);
        
        
    }
    
    
    
    public List<Order> findAll() {
        return new ArrayList<Order>(this.ordersById.values());
    }
    
    public Order findById(final Integer id) {
        return this.ordersById.get(id);
    }
    
    
    public List<Order> findByCustomerId(final Integer customerId) {
        final List<Order> ordersForCustomerId = this.ordersByCustomerId.get(customerId);
        if (ordersForCustomerId == null) {
            return new ArrayList<Order>();
        }
        return ordersForCustomerId;
    }
    
    
}
