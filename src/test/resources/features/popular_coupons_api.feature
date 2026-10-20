# language: en

@api @smoke
Feature: Popular Coupons API

  Scenario: Popular Coupons returns a successful response
    Given Popular Coupons API endpoint is configured
    When I send a GET request with eventCount 0
    Then the response status code should be 200
    And the response content type should be JSON
    And the response should be deserialized into POJO
    And coupon event counts and odd ranges should be valid
    And the response should match the JSON schema
    And coupon sports should be consistent
    And coupons should be ordered by orderIndex