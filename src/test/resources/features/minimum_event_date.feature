@api @extra
Feature: Minimum Event Date Validation

  Scenario: Coupon minimum date should match earliest event
    Given Popular Coupons API endpoint is configured
    When I send a GET request with eventCount 0
    Then the response status code should be 200
    And coupon minimum event dates should be valid