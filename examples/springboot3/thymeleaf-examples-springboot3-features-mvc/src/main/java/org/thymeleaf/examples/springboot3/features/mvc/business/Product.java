package org.thymeleaf.examples.springboot3.features.mvc.business;

import java.time.LocalDate;

public class Product {

    private String name;
    private ProductCategory category;
    private double price;
    private boolean available;
    private LocalDate releaseDate;

    public Product() {
        super();
    }

    public Product(final String name, final ProductCategory category,
                   final double price, final boolean available, final LocalDate releaseDate) {
        super();
        this.name = name;
        this.category = category;
        this.price = price;
        this.available = available;
        this.releaseDate = releaseDate;
    }

    public String getName() { return this.name; }
    public void setName(final String name) { this.name = name; }

    public ProductCategory getCategory() { return this.category; }
    public void setCategory(final ProductCategory category) { this.category = category; }

    public double getPrice() { return this.price; }
    public void setPrice(final double price) { this.price = price; }

    public boolean isAvailable() { return this.available; }
    public void setAvailable(final boolean available) { this.available = available; }

    public LocalDate getReleaseDate() { return this.releaseDate; }
    public void setReleaseDate(final LocalDate releaseDate) { this.releaseDate = releaseDate; }

}
