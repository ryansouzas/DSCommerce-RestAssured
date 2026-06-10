package com.devsuperior.dscommerce.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class ProductControllerRA {

    private Long existingProductId, nonExistingProductId;
    private String productName;

    @BeforeEach
    void setUp() {
        baseURI = "http://localhost:8080";
        productName = "Macbook";
    }

    @Test
    public void findByIdShouldReturnProductWhenIdExists() {
        existingProductId = 2L;

        given()
                .get("/products/{id}", existingProductId)
        .then()
                .statusCode(200)
                .body("id", is(2))
                .body("name", equalTo("Smart TV"))
                .body("description", equalTo("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."))
                .body("price", equalTo(2190.0f))
                .body("imgUrl", equalTo("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/2-big.jpg"))
                .body("categories.id", hasItems(2, 3))
                .body("categories.name", hasItem("Computadores"));

    }

    @Test
    public void findAllShouldReturnPageProductWhenProductNameIsEmpty() {
        given()
                .get("/products")
        .then()
                .statusCode(200)
                .body("content.name", hasItems("Macbook Pro", "PC Gamer Tera"));
    }

    @Test
    public void findAllShouldReturnPageProductWhenProductNameIsNotEmpty() {
        given()
                .get("/products?name={productName}", productName)
        .then()
                .statusCode(200)
                .body("content.id[0]" , is(3))
                .body("content.name[0]", equalTo("Macbook Pro"))
                .body("content.price[0]", equalTo(1250.0f));
    }

    @Test
    public void findAllShouldReturnPagedProductsWithPriceGreaterThan2000() {
        given()
                .get("/products?size=25")
        .then()
                .statusCode(200)
                .body("content.findAll {it.price > 2000}.name ", hasItems("Smart TV", "PC Gamer Weed"));
    }
}
