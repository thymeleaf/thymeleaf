/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2026 Thymeleaf (http://www.thymeleaf.org)
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
package org.thymeleaf.spring6.integration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OrderForm {

    private List<OrderItem> items = new ArrayList<>();
    private List<String> features = new ArrayList<>();
    private String size;

    public OrderForm() {
        super();
    }

    public static OrderForm initial() {
        final OrderForm orderForm = new OrderForm();
        orderForm.setItems(new ArrayList<>(Arrays.asList(new OrderItem("pen", Integer.valueOf(2)), new OrderItem("ink", Integer.valueOf(1)))));
        orderForm.setFeatures(new ArrayList<>(Arrays.asList("fast")));
        orderForm.setSize("M");
        return orderForm;
    }

    public List<OrderItem> getItems() {
        return this.items;
    }

    public void setItems(final List<OrderItem> items) {
        this.items = items;
    }

    public List<String> getFeatures() {
        return this.features;
    }

    public void setFeatures(final List<String> features) {
        this.features = features;
    }

    public String getSize() {
        return this.size;
    }

    public void setSize(final String size) {
        this.size = size;
    }

}
