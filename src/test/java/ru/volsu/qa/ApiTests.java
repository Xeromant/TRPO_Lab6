package ru.volsu.qa;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.volsu.qa.models.Post;
import java.lang.Math;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class ApiTests {

    public String access_token;
    public String resources;

    @BeforeClass
    public void beforeClass() {
        RestAssured.baseURI = "https://gorest.co.in";
        RestAssured.port = 443;
        access_token = "?access-token=tKS49RjA_j0tZv60Tw1gEAmrdLjL-amZzccL";
        resources = "/public-api/users";
    }

    @DataProvider(name = "GetByFirstNameProvider")
    public Object[][] first_nameDataProvider(){
        return new Object[][]{
                {"&first_name=Doroty"},
                {"&first_name=Pavel"},
                {"&first_name=Morty"}
        };
    }

    @DataProvider(name = "GetIDProvider")
    public Object[][] IdUsersDataProvider(){
        return new Object[][]{
                {"/1827"},
                {"/35034"},
                {"/31040"}
        };
    }

    @DataProvider(name = "deleteIdUsersDataProvider")
    public Object[][] DeleteIdUsersDataProvider(){
        return new Object[][]{
                {"/31580"}
        };
    }

    @DataProvider(name = "deleteIdUsersDataProvider1")
    public Object[][] DeleteIdUsersDataProvider1(){
        return new Object[][]{
                {"/10293"}
        };
    }

    @DataProvider(name = "postRandomEmailDataProvider")
    public Object[][] RandomEmailDataProvider(){
        return new Object[][]{
                {"zhelt"+Math.random()+"@mail.ru"}
        };
    }

    @Test
    public void testGetAllUsers() {
        given()
                .log().all()
        .when()
                .request("GET", resources+access_token)
        .then()
                .log().all()
                .statusCode(200);
    }

    @Test(dataProvider = "GetByFirstNameProvider")
    public void GetUsersByFirstName(String firstname) {
        given()
                .log().all()
        .when()
                .request("GET", resources+access_token+firstname)
        .then()
                .log().body()
                .statusCode(200);
    }

    @Test(dataProvider = "GetIDProvider")
    public void testGetIdUser(String UserID) {
        given()
                .log().all()
        .when()
                .request("GET", resources+UserID+access_token)
        .then()
                .log().all()
                .statusCode(200);
    }

    @Test(dataProvider = "postRandomEmailDataProvider") //Создание нового пользователя
    public void testAddUsers(String randomEmail){
        Post newPost = new Post("Tim", "Zheltov", "male","01.01.1999",
                randomEmail,"+896-125-147", "https://gorest.co.in/","Volgograd","active");
        given()
                .log().body()
                .contentType(ContentType.JSON).body(newPost)
        .when()
                .post( resources+access_token)
        .then()
                .log().body()
                .assertThat()
                .body("result.first_name", equalTo(newPost.getFirst_name()))
                .statusCode(302);
    }

    @Test(dataProvider = "postRandomEmailDataProvider") // Заменить пользователя методом put
    public void testPutUsers(String randomEmail){
        Post newPost = new Post("Tim", "Zheltov", "male","01.01.1999",
                randomEmail,"+896-125-147", "https://gorest.co.in/","Volgograd","active");
        given()
                .log().body()
                .contentType(ContentType.JSON).body(newPost)
        .when()
                .put( resources+"/31540"+access_token)
        .then()
                .log().body()
                .assertThat()
                .body("result.first_name", equalTo(newPost.getFirst_name()))
                .statusCode(200);
    }

    @Test(dataProvider = "deleteIdUsersDataProvider")
    public void testDeleteIdUser(String User_ID) {
        given()
                .log().all()
        .when()
                .delete( resources+User_ID+access_token)
        .then()
                .log().all()
                .assertThat()
                .body("result", equalTo(null))
                .statusCode(200);
    }

    @DataProvider(name = "UncorrectUserDataEmpty")
    public Object[][] UncorrectedUsersDataProviderEmpty(){
        return new Object[][]{
                {""}
        };
    }
    @DataProvider(name = "UncorrectUserData")
    public Object[][] UncorrectedUsersDataProvider(){
        return new Object[][]{
                {"tKS49RjA_j0tZv60Tw1gEAmrdLjL-amZzccL"}
        };
    }

    @Test(dataProvider = "UncorrectUserDataEmpty")
    public void testUncorrectedGetAllUsersEmpty(String access_token) {

        try {
            given()
                    .log().all()
                    .when()
                    .request("GET", resources + access_token)
                    .then()
                    .log().all()
                    .assertThat()
                    .body("_meta.message", equalTo(""))
                    .statusCode(200);
        }catch (AssertionError e){
            System.out.println("Ожидаемый текст исключения: java.lang.AssertionError: 1 expectation failed.\n" +
                    "JSON path _meta.message doesn't match.\n" +
                    "Expected: \n" +
                    "  Actual: Authentication failed.\n");
            System.out.println("Текст исключения: " + e);
            Assert.assertEquals(e.toString(),"java.lang.AssertionError: 1 expectation failed.\n" +
                    "JSON path _meta.message doesn't match.\n" +
                    "Expected: \n" +
                    "  Actual: Authentication failed.\n");
        }

    }

    @Test(dataProvider = "UncorrectUserData")
    public void testUncorrectedGetAllUsers(String access_token) {

        try {
            given()
                    .log().all()
            .when()
                    .request("GET", resources + access_token)
            .then()
                    .log().all()
                    .assertThat()
                    .body("_meta.message", equalTo(""))
                    .statusCode(200);
        }catch (AssertionError e){
            System.out.println("Ожидаемый текст исключения: java.lang.AssertionError: 1 expectation failed.\n" +
                    "XML path _meta.message doesn't match.\n" +
                    "Expected: \n" +
                    "  Actual: \n");
            System.out.println("Текст исключения: " + e);
            Assert.assertEquals(e.toString(),"java.lang.AssertionError: 1 expectation failed.\n" +
                    "XML path _meta.message doesn't match.\n" +
                    "Expected: \n" +
                    "  Actual: \n");
        }

    }

    @Test
    public void testFailAddUsers(){
        Post newPost = new Post("Tomas", "Fles", "male","01.01.1321",
                "fle123123s@mail.ru","+999-999-999", "https://gorest.co.in/","Volgograd","active");
        try {
            given()
                    .log().body()
                    .contentType(ContentType.JSON).body(newPost)
            .when()
                    .post(resources + access_token)
            .then()
                    .log().body()
                    .assertThat()
                    .body("result.email", equalTo(newPost.getEmail()))
                    .statusCode(302);
        }catch(AssertionError e){
            System.out.println("Ожидаемый текст исключения: java.lang.AssertionError: 1 expectation failed.\n" +
                    "JSON path result.email doesn't match.\n" +
                    "Expected: fle123123s@mail.ru\n" +
                    "  Actual: [null]\n");
            System.out.println("Текст исключения: " + e);
            Assert.assertEquals(e.toString(),"java.lang.AssertionError: 1 expectation failed.\n" +
                    "JSON path result.email doesn't match.\n" +
                    "Expected: fle123123s@mail.ru\n" +
                    "  Actual: [null]\n");
        }
    }

    @Test(dataProvider = "deleteIdUsersDataProvider1")
    public void testFailDeleteIdUser(String idUsers) {
        try {
            given()
                    .log().all()
            .when()
                    .delete(resources + idUsers + access_token)
            .then()
                    .log().all()
                    .assertThat()
                    .body("result", equalTo(null))
                    .statusCode(404);
        }catch(AssertionError e){
            System.out.println("Ожидаемый текст исключения: java.lang.AssertionError: 1 expectation failed.\n" +
                    "JSON path result doesn't match.\n" +
                    "Expected: null\n" +
                    "  Actual: {code=0, name=Not Found, message=Object not found: 10293, status=404}\n");
            System.out.println("Текст исключения: " + e);
            Assert.assertEquals(e.toString(),"java.lang.AssertionError: 1 expectation failed.\n" +
                    "JSON path result doesn't match.\n" +
                    "Expected: null\n" +
                    "  Actual: {code=0, name=Not Found, message=Object not found: 10293, status=404}\n");
        }
    }
}







