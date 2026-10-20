@api @queryParameters
Feature: Popular Coupons API query parameters

  Scenario Outline: Valid eventCount values
    Given Popular Coupons API endpoint is configured
    When I send a GET request with eventCount <eventCount>
    Then the response status code should be 200
    And the response content type should be JSON
    And returned coupons should have <expectedCount> events

    Examples:
      | eventCount | expectedCount |
      | 2          | 2             |
      | 3          | 3             |

  Scenario: Invalid negative eventCount
    Given Popular Coupons API endpoint is configured
    When I send a GET request with raw eventCount "-1"
    Then the response status code should be 200
    And the application error code should be 400

  Scenario: Non numeric eventCount is rejected
    Given Popular Coupons API endpoint is configured
    When I send a GET request with raw eventCount "abc"
    Then the response status code should be 400

  Scenario: Unsupported eventCount is rejected
    Given Popular Coupons API endpoint is configured
    When I send a GET request with raw eventCount "999"
    Then the response status code should be 200
    And the application error code should be 400

  Scenario: Missing eventCount uses default behavior
    Given Popular Coupons API endpoint is configured
    When I send a GET request without eventCount
    Then the response status code should be 200
    And the application status code should be 200
    And the response should contain coupons

  @knownIssue
  Scenario: Verify eventCount 5 filtering behavior
    Given Popular Coupons API endpoint is configured
    When I send a GET request with eventCount 5
    Then the response status code should be 200
    And returned coupons should have 5 events