package org.thymeleaf.examples.springboot3.features.mvc.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.examples.springboot3.features.mvc.business.Product;
import org.thymeleaf.examples.springboot3.features.mvc.business.ProductCategory;
import org.thymeleaf.examples.springboot3.features.mvc.business.ProductRecord;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

@Controller
public class FeaturesController {

    @GetMapping("/")
    public String index() {
        return "redirect:/javascript";
    }

    @GetMapping("/javascript")
    public String javascript(final Model model) {

        final Product pojo = new Product("Organic Thyme", ProductCategory.FOOD, 3.99, true, LocalDate.of(2024, 3, 1));
        final ProductRecord record = new ProductRecord("Basil & Deluxe", ProductCategory.FOOD, 4.50, LocalDate.of(2024, 6, 15));

        model.addAttribute("pojo", pojo);
        model.addAttribute("record", record);
        model.addAttribute("productList", List.of(
                new Product("Thyme", ProductCategory.FOOD, 3.99, true, LocalDate.of(2024, 3, 1)),
                new Product("Basil", ProductCategory.FOOD, 4.50, true, LocalDate.of(2024, 6, 15)),
                new Product("Rosemary", ProductCategory.TOOLS, 3.75, false, LocalDate.of(2024, 9, 1))
        ));
        model.addAttribute("utilDate", new Date());
        model.addAttribute("localDate", LocalDate.of(2024, 6, 15));
        model.addAttribute("localDateTime", LocalDateTime.of(2024, 6, 15, 12, 30, 0));
        model.addAttribute("dangerousString", "Product details & specifications: /info/details");
        model.addAttribute("nullValue", null);
        model.addAttribute("boolValue", true);
        model.addAttribute("numericValue", 42.5);
        model.addAttribute("enumValue", ProductCategory.ELECTRONICS);

        return "javascript";
    }

    @GetMapping("/expressions")
    public String expressions(final Model model) {

        model.addAttribute("name", "Thymeleaf");
        model.addAttribute("num", 42);
        model.addAttribute("price", 1234.5678);
        model.addAttribute("items", List.of("banana", "apple", "cherry", "date"));
        model.addAttribute("emptyList", Collections.emptyList());

        final Map<String, Integer> myMap = new LinkedHashMap<>();
        myMap.put("alpha", 1);
        myMap.put("beta", 2);
        myMap.put("gamma", 3);
        model.addAttribute("myMap", myMap);

        model.addAttribute("strArray", new String[]{"java", "spring", "thymeleaf"});
        model.addAttribute("today", new Date());
        model.addAttribute("nullValue", null);
        model.addAttribute("boolTrue", true);
        model.addAttribute("boolFalse", false);

        return "expressions";
    }

    @GetMapping("/iteration")
    public String iteration(final Model model) {

        model.addAttribute("fruits", List.of("apple", "banana", "cherry", "date", "elderberry"));

        final Map<String, Integer> scores = new LinkedHashMap<>();
        scores.put("Alice", 95);
        scores.put("Bob", 87);
        scores.put("Carol", 92);
        model.addAttribute("scores", scores);

        model.addAttribute("tags", new LinkedHashSet<>(Arrays.asList("java", "spring", "thymeleaf")));
        model.addAttribute("primes", new int[]{2, 3, 5, 7, 11});
        model.addAttribute("matrix", List.of(
                List.of(1, 2, 3),
                List.of(4, 5, 6),
                List.of(7, 8, 9)
        ));

        return "iteration";
    }

    @GetMapping("/conditionals")
    public String conditionals(final Model model) {

        model.addAttribute("truthyBool", true);
        model.addAttribute("falsyBool", false);
        model.addAttribute("nullValue", null);
        model.addAttribute("emptyStr", "");
        model.addAttribute("zeroNum", 0);
        model.addAttribute("nonZeroNum", 42);
        model.addAttribute("nonEmptyStr", "hello");
        model.addAttribute("season", "SUMMER");

        return "conditionals";
    }

    @GetMapping("/fragments")
    public String fragments(final Model model) {

        model.addAttribute("pageTitle", "Fragment Tests");
        model.addAttribute("items", List.of("Alpha", "Beta", "Gamma", "Delta"));
        model.addAttribute("greeting", "Hello from fragment!");

        return "fragments";
    }

}
