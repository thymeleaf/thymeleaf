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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Product {

    private Integer id = null;
    private String name = null;
    private BigDecimal price = null;
    private boolean inStock = false;
    private List<Comment> comments = new ArrayList<Comment>();
    
    
    public Product() {
        super();
    }


    public Product(final Integer id, final String name, final boolean inStock, final BigDecimal price) {
        super();
        this.id = id;
        this.name = name;
        this.price = price;
        this.inStock = inStock;
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
    

    public BigDecimal getPrice() {
        return this.price;
    }
    public void setPrice(final BigDecimal price) {
        this.price = price;
    }


    public boolean isInStock() {
        return this.inStock;
    }
    public void setInStock(final boolean inStock) {
        this.inStock = inStock;
    }
    
    
    public List<Comment> getComments() {
        return this.comments;
    }
    
}
