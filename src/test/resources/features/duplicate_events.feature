@api @extra
Feature: Duplicate Event Validation

  Scenario: Events in a coupon should be unique
    Given Popular Coupons API endpoint is configured
    When I send a GET request with eventCount 0
    Then the response status code should be 200
    And coupon events should be unique