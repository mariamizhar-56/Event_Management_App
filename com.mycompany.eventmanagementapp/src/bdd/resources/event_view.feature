Feature: Event Application Frame
  Specifications of the behavior of the Event Application Frame

  Scenario: The initial state of the view
    Given The database contain events with the following values
      | Music Festival | Florence | 2030-01-15 |
      | University Event | Milan | 2030-01-16 |
    When The Event View is shown
    Then The Event view list contains an element with the following values
      | Music Festival | Florence | 2030-01-15 |
      | University Event | Milan | 2030-01-16 |

  Scenario: Add a new Event
    When The Event View is shown
    And The user enters the following values in the text fields
      | txtEventName | txtEventLocation | txtEventDate |
      | Student Event | Florence | 2030-01-17 |
    And The user clicks the "Add Event" button
    Then The Event view list contains an element with the following values
      | Student Event | Florence | 2030-01-17 |

  Scenario: Add a new Event with past event date
    Given The database contain events with the following values
      | Music Festival | Florence | 2030-01-15 |
      | University Event | Milan | 2030-01-16 |
    When The Event View is shown
    And The user enters the following values in the text fields
      | txtEventName | txtEventLocation | txtEventDate |
      | Student Event | Florence | 2020-01-17 |
    And The user clicks the "Add Event" button
    Then An error is shown containing the following values
      | Student Event | Florence | 2020-01-17 |

  Scenario: Delete an Event
    Given The database contain events with the following values
      | Music Festival | Florence | 2030-01-15 |
      | University Event | Milan | 2030-01-16 |
    When The Event View is shown
    And The user selects an event from the list
    When The user clicks the "Delete Event" button
    Then The event is removed from the list

  Scenario: Update an Event
    Given The database contain events with the following values
      | Music Festival | Florence | 2030-01-15 |
      | University Event | Milan | 2030-01-16 |
    When The Event View is shown
    And The user selects an event from the list
    And All values are populated
      | txtEventName | txtEventLocation | txtEventDate |
      | Music Festival | Florence | 2030-01-15 |
    When The user enters the following values in the text fields
      | txtEventName |
      | Student Event|
    And The user clicks the "Update Event" button
    Then The Event view list contains an element with the following values
      | Student Event | Florence | 2030-01-15 |
      | University Event | Milan | 2030-01-16 |

  Scenario: Update an Event with past event date
    Given The database contain events with the following values
      | Music Festival | Florence | 2030-01-15 |
      | University Event | Milan | 2030-01-16 |
    When The Event View is shown
    And The user selects an event from the list
    And All values are populated
      | txtEventName | txtEventLocation | txtEventDate |
      | Music Festival | Florence | 2030-01-15 |
    When The user enters the following values in the text fields
      | txtEventDate |
      | 2020-01-17 |
    And The user clicks the "Update Event" button
    Then An error is shown containing the following values
      | Music Festival | Florence | 2020-01-17 |
