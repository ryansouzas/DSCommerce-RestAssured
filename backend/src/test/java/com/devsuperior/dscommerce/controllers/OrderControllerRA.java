package com.devsuperior.dscommerce.controllers;

import com.devsuperior.dscommerce.tests.TokenUtil;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class OrderControllerRA {

    private String clientUsername, clientPassword, adminUsername, adminPassword;
    private String clientToken, adminToken,invalidToken;
    private Long existingOrderId, nonExistingOrderId;

    @BeforeEach
    void setUp() {
        baseURI = "http://localhost:8080";

        clientUsername = "maria@gmail.com";
        clientPassword = "123456";
        adminUsername = "alex@gmail.com";
        adminPassword = "123456";

        existingOrderId = 1L;
        nonExistingOrderId = 100L;

        clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
        adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);

        invalidToken = adminToken + "xpto";
    }

    @Test
    public void findByIdShouldReturnOrderWhenIdExistsAndAdminLogged(){

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .accept(ContentType.JSON)

        .when()
                .get("/orders/{id}", existingOrderId)
        .then()
                .statusCode(200)
                .body("id", is(1))
                .body("status", equalTo("PAID"))
                .body("moment", equalTo("2022-07-25T13:00:00Z"))
                .body("client.email", equalTo("maria@gmail.com"))
                .body("items.name", hasItems("The Lord of the Rings","Macbook Pro"))
                .body("total", is(1431.0F));
    }

    @Test
    public void findByIdShouldReturnOrderWhenIdExistsAndClientLogged(){

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .accept(ContentType.JSON)

                .when()
                .get("/orders/{id}", existingOrderId)
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("status", equalTo("PAID"))
                .body("moment", equalTo("2022-07-25T13:00:00Z"))
                .body("client.email", equalTo("maria@gmail.com"))
                .body("items.name", hasItems("The Lord of the Rings","Macbook Pro"))
                .body("total", is(1431.0F));
    }

    @Test
        public void findByIdShouldReturnForbiddenWhenIdExistsAndClientLoggedAndOrderDoesNotBelongUser(){
        Long otherOrderId = 2L;

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .accept(ContentType.JSON)

                .when()
                .get("/orders/{id}", otherOrderId)
                .then()
                .statusCode(403);
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesExistsAndAdminLogged(){

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .accept(ContentType.JSON)

                .when()
                .get("/orders/{id}", nonExistingOrderId)
                .then()
                .statusCode(404);
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesExistsAndClientLogged(){

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .accept(ContentType.JSON)

                .when()
                .get("/orders/{id}", nonExistingOrderId)
                .then()
                .statusCode(404);
    }

    @Test
    public void findByIdShouldReturnUnauthorizedWhenIdDoesExistsAndInvalidToken(){

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + invalidToken)
                .accept(ContentType.JSON)

                .when()
                .get("/orders/{id}", existingOrderId)
                .then()
                .statusCode(401);
    }




}
