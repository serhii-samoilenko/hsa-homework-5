package com.example

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.hamcrest.Matchers.notNullValue
import org.hamcrest.Matchers.oneOf
import org.junit.jupiter.api.Test

@QuarkusTest
class PersonResourceTest {

    @Test
    fun `should try to get a random person`() {
        given()
            .`when`().get("/person/random")
            .then()
            .statusCode(oneOf(204, 200))
    }

    @Test
    fun `should create a random person`() {
        given()
            .`when`().post("/person/random")
            .then()
            .statusCode(200)
            .body("firstName", notNullValue())
            .body("lastName", notNullValue())
            .body("age", notNullValue())
            .body("phoneNumber", notNullValue())
    }

    @Test
    fun `should get count of DB entries`() {
        given()
            .`when`().get("/person/count")
            .then()
            .statusCode(200)
    }

    @Test
    fun `should delete all DB entries`() {
        given()
            .`when`().delete("/person/all")
            .then()
            .statusCode(204)
    }
}
