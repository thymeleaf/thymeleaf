package org.thymeleaf.examples.springboot3.features.mvc.business;

import java.time.LocalDate;

public record ProductRecord(String name, ProductCategory category, double price, LocalDate releaseDate) {}
