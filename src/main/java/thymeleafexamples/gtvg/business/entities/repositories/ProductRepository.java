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
package thymeleafexamples.gtvg.business.entities.repositories;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import thymeleafexamples.gtvg.business.entities.Comment;
import thymeleafexamples.gtvg.business.entities.Product;


public class ProductRepository {

    private static final ProductRepository INSTANCE = new ProductRepository();
    private final Map<Integer,Product> productsById;
    
    
    
    public static ProductRepository getInstance() {
        return INSTANCE;
    }
    
    
    private ProductRepository() {
        
        super();
        
        this.productsById = new LinkedHashMap<Integer, Product>();
        
        final Product prod1 = new Product();
        prod1.setId(Integer.valueOf(1));
        prod1.setName("Fresh Sweet Basil");
        prod1.setInStock(true);
        prod1.setPrice(new BigDecimal("4.99"));
        this.productsById.put(prod1.getId(), prod1);
        
        final Product prod2 = new Product();
        prod2.setId(Integer.valueOf(2));
        prod2.setName("Italian Tomato");
        prod2.setInStock(false);
        prod2.setPrice(new BigDecimal("1.25"));
        this.productsById.put(prod2.getId(), prod2);
        
        final Product prod3 = new Product();
        prod3.setId(Integer.valueOf(3));
        prod3.setName("Yellow Bell Pepper");
        prod3.setInStock(true);
        prod3.setPrice(new BigDecimal("2.50"));
        this.productsById.put(prod3.getId(), prod3);
        
        final Product prod4 = new Product();
        prod4.setId(Integer.valueOf(4));
        prod4.setName("Old Cheddar");
        prod4.setInStock(true);
        prod4.setPrice(new BigDecimal("18.75"));
        this.productsById.put(prod4.getId(), prod4);
        
        
        final Comment comm21 = new Comment();
        comm21.setId(Integer.valueOf(1));
        comm21.setText("I'm so sad this product is no longer available!");
        prod2.getComments().add(comm21);
        
        final Comment comm22 = new Comment();
        comm22.setId(Integer.valueOf(2));
        comm22.setText("When do you expect to have it back?");
        prod2.getComments().add(comm22);
        
        final Comment comm41 = new Comment();
        comm41.setId(Integer.valueOf(3));
        comm41.setText("Very tasty! I'd definitely buy it again!");
        prod4.getComments().add(comm41);
        
    }
    
    
    
    public List<Product> findAll() {
        return new ArrayList<Product>(this.productsById.values());
    }
    
    public Product findById(final Integer id) {
        return this.productsById.get(id);
    }
    
    
    
}
