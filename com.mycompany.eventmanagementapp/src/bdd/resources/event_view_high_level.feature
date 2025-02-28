Feature: Event View High Level
  Specifications of the behavior of the Event View Window

  Background: 
    Given The database contains a few events
    And The Event View is shown

  Scenario: Add a new Event
    Given The user provides event data in the text fields
    And The user clicks the "Add Event" button
    Then The list contains the new event

  Scenario: Add a new Event with past event date
    Given The user provides event data in the text fields, specifying past event date
    When The user clicks the "Add Event" button
    Then An error is shown containing the information of the event

  Scenario: Delete an Event
    Given The user selects an event from the list
    When The user clicks the "Delete Event" button
    Then The event is removed from the list

  Scenario: Delete a non existing Event
    Given The user selects an event from the list
    But The event is in the meantime removed from the database
    When The user clicks the "Delete Event" button
    Then An error is shown containing the information of the event
    And The event is removed from the list
    
  Scenario: Update an Event
    Given The user selects an event from the list
    And The selected event is populated in textfields
    And The user provides Updated event data in the text fields
    And The user clicks the "Update Event" button
    Then The list contains the updated event
    
    Scenario: Update an Event with past event date
    Given The user selects an event from the list
    And The selected event is populated in textfields
    And The user provides Updated event data in the text fields, specifying past event date
    And The user clicks the "Update Event" button
    Then An error is shown containing the information of the event
    