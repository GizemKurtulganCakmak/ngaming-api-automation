package com.gizemcakmak.automation.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gizemcakmak.automation.model.Coupon;
import com.gizemcakmak.automation.model.CouponEvent;
import com.gizemcakmak.automation.model.PopularCouponsResponse;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.*;

public class PopularCouponsApiSteps {

    private Response response;
    private Integer actualStatusCode;
    private String actualResponseBody;

    private final String baseUrl = System.getProperty(
            "baseUrl",
            "https://pc.nesine.com"
    );

    @Given("Popular Coupons API endpoint is configured")
    public void configureEndpoint() {
        assertFalse(baseUrl.isBlank());
    }

    @When("I send a GET request with eventCount {int}")
    public void sendGetRequest(int eventCount) {

        response = given()
                .baseUri(baseUrl)
                .header("Accept","application/json")
                .header("Accept-Encoding","gzip")
                .queryParam("eventCount", eventCount)
                .when()
                .get("/v1/PopularCoupons");

        System.out.println(
                "HTTP Status: " + response.statusCode()
        );


    }

    @Then("the response status code should be {int}")
    public void verifyStatusCode(int expected) {

        int actual = actualStatusCode != null
                ? actualStatusCode
                : response.statusCode();

        assertEquals(expected, actual);
    }

    @Then("the response content type should be JSON")
    public void verifyContentType() {

        assertNotNull(response);

        assertTrue(
                response.contentType()
                        .toLowerCase()
                        .contains("json"),
                "Unexpected Content-Type: " + response.contentType()
        );
    }


    @Then("the response should be deserialized into POJO")
    public void verifyDeserialization() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        PopularCouponsResponse result =
                mapper.readValue(
                        response.asString(),
                        PopularCouponsResponse.class
                );

        assertEquals(200, result.sc());
        assertNotNull(result.coupons());

        System.out.println(
                "Deserialize edilen kupon sayısı: " +
                        result.coupons().size()
        );
    }

    @Then("coupon event counts and odd ranges should be valid")
    public void verifyCouponConsistency() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        PopularCouponsResponse result =
                mapper.readValue(
                        response.asString(),
                        PopularCouponsResponse.class
                );

        List<Coupon> coupons = result.coupons();

        assertNotNull(coupons);
        assertFalse(coupons.isEmpty());

        for (Coupon coupon : coupons) {

            assertNotNull(coupon.events());
            assertNotNull(coupon.eventCount());

            assertEquals(
                    coupon.eventCount().intValue(),
                    coupon.events().size(),
                    "Etkinlik sayısı uyuşmuyor: " +
                            coupon.couponId()
            );

            for (CouponEvent event : coupon.events()) {

                assertNotNull(event.minOdd());
                assertNotNull(event.maxOdd());

                assertTrue(
                        event.minOdd().compareTo(
                                event.maxOdd()
                        ) <= 0,
                        "Minimum oran maksimumdan büyük: " +
                                event.name()
                );
            }
        }
    }

    @Then("the response should match the JSON schema")
    public void verifyJsonSchema() {

        response.then().assertThat()
                .body(matchesJsonSchemaInClasspath(
                        "schemas/popular-coupons.schema.json"
                ));
    }

    @When("I send a GET request without eventCount")
    public void sendWithoutEventCount() {

        response = given()
                .baseUri(baseUrl)
                .header("Accept", "application/json")
                .header("Accept-Encoding", "gzip")
                .when()
                .get("/v1/PopularCoupons");
    }

    @When("I send a GET request with raw eventCount {string}")
    public void sendRawEventCount(String value) throws Exception {

        response = null;

        String encodedValue = URLEncoder.encode(
                value, StandardCharsets.UTF_8
        );

        String url = baseUrl
                + "/v1/PopularCoupons?eventCount="
                + encodedValue;

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .header("Accept-Encoding", "identity")
                .GET()
                .build();

        HttpResponse<String> result = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        actualStatusCode = result.statusCode();
        actualResponseBody = result.body();

    }

    @Then("returned coupons should have {int} events")
    public void verifyEventCount(int expectedCount)
            throws Exception {

        PopularCouponsResponse result =
                new ObjectMapper().readValue(
                        response.asString(),
                        PopularCouponsResponse.class
                );

        assertNotNull(result.coupons());
        assertFalse(result.coupons().isEmpty());

        for (Coupon coupon : result.coupons()) {
            assertEquals(
                    expectedCount,
                    coupon.events().size(),
                    "Unexpected event count for coupon: " +
                            coupon.couponId()
            );
        }
    }

    @Then("the response behavior is logged")
    public void logResponseBehavior() {

        System.out.println("HTTP Status: " + response.statusCode());
        System.out.println("Content-Encoding: "
                + response.getHeader("Content-Encoding"));

        String body = response.asString();

        System.out.println(
                "Response Body: " +
                        body.substring(0, Math.min(500, body.length()))
        );
    }

    @Then("the application error code should be {int}")
    public void verifyApplicationError(int expected) {

        String body = actualResponseBody != null
                ? actualResponseBody
                : response != null
                ? response.asString()
                : null;

        assertNotNull(body, "Response body bulunamadı");

        io.restassured.path.json.JsonPath json =
                new io.restassured.path.json.JsonPath(body);

        assertEquals(
                expected,
                json.getInt("sc"),
                "Uygulama hata kodu yanlış"
        );

        assertEquals(
                101,
                json.getInt("el[0].c"),
                "Beklenen hata detayı bulunamadı"
        );

        assertNull(
                json.get("d"),
                "Hatalı istekte kupon verisi dönmemeli"
        );
    }
    @Then("the application status code should be {int}")
    public void verifyApplicationStatus(int expected) {

        assertNotNull(response);

        assertEquals(
                expected,
                response.jsonPath().getInt("sc")
        );
    }

    @Then("the response should contain coupons")
    public void verifyCouponsExist() {

        assertNotNull(response);

        List<?> coupons = response.jsonPath().getList("d");

        assertNotNull(coupons);
        assertFalse(coupons.isEmpty());
    }
    @Then("coupon sports should be consistent")
    public void verifySportConsistency() throws Exception {

        PopularCouponsResponse result =
                new ObjectMapper().readValue(
                        response.asString(),
                        PopularCouponsResponse.class
                );

        assertNotNull(result.coupons());
        assertFalse(result.coupons().isEmpty());

        for (Coupon coupon : result.coupons()) {
            for (CouponEvent event : coupon.events()) {

                assertNotNull(event.type());
                assertFalse(event.type().isBlank());
                assertNotNull(event.sportId());

                // Observed mapping: Football = 1
                if ("Football".equals(event.type())) {
                    assertEquals(
                            1,
                            event.sportId(),
                            "Sport ID mismatch: " + event.name()
                    );
                }
            }
        }
    }

    @Then("coupons should be ordered by orderIndex")
    public void verifyCouponOrdering() throws Exception {

        PopularCouponsResponse result =
                new ObjectMapper().readValue(
                        response.asString(),
                        PopularCouponsResponse.class
                );

        assertNotNull(result.coupons());
        assertFalse(result.coupons().isEmpty());

        int previousIndex = Integer.MIN_VALUE;

        for (Coupon coupon : result.coupons()) {

            assertNotNull(coupon.orderIndex());

            assertTrue(
                    coupon.orderIndex() > previousIndex,
                    "Unexpected coupon orderIndex: " +
                            coupon.orderIndex()
            );

            previousIndex = coupon.orderIndex();
        }
    }
    @Then("coupon events should be unique")
    public void verifyUniqueEvents() throws Exception {

        PopularCouponsResponse result =
                new ObjectMapper().readValue(
                        response.asString(),
                        PopularCouponsResponse.class
                );

        for (Coupon coupon : result.coupons()) {
            Set<Long> eventIds = new HashSet<>();

            for (CouponEvent event : coupon.events()) {
                assertNotNull(event.betradarId());
                assertTrue(
                        eventIds.add(event.betradarId()),
                        "Duplicate event in coupon: " + coupon.couponId()
                );
            }
        }
    }

    @Then("coupon minimum event dates should be valid")
    public void verifyMinimumEventDates() throws Exception {

        PopularCouponsResponse result =
                new ObjectMapper().readValue(
                        response.asString(),
                        PopularCouponsResponse.class
                );

        for (Coupon coupon : result.coupons()) {

            long earliest = coupon.events().stream()
                    .mapToLong(CouponEvent::startDateEpoch)
                    .min()
                    .orElseThrow();

            assertEquals(
                    earliest,
                    Long.parseLong(coupon.minEventDateEpoch()),
                    "Minimum event date mismatch: " + coupon.couponId()
            );
        }
    }
}