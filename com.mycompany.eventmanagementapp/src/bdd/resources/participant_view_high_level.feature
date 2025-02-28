Feature: Participant View High Level
  Specifications of the behavior of the Participant View Window

  Background: 
    Given The database contains a few participants
    And The Participant View is shown

  Scenario: Add a new Participant
    Given The user provides participant data in the text fields
    And The user clicks the "Add Participant" button
    Then The Participant list contains the new participant

  Scenario: Add a new Participant with invalid event Id
    Given The user provides participant data in the text fields, specifying invalid event Id
    When The user clicks the "Add Participant" button
    Then An error is shown containing the information of the participant

  Scenario: Delete Participant which is only associated to one Event
    Given The user selects a participant which only associated to one Event from the Participant list
    And The user selects an event from the Event list
    When The user clicks the "Delete Participant" button
    Then The participant is removed from the Participant list
    
  Scenario: Delete Participant which is associated to more than one Event
    Given The user selects a participant which associated to more than one Event from the Participant list
    And The user selects an event from the Event list
    When The user clicks the "Delete Participant" button
    Then The participant is removed from that Event but stays in Participant list

  Scenario: Delete non existing Participant
    Given The user selects a participant from the Participant list
    And The user selects an event from the Event list
    But The participant is in the meantime removed from that event
    When The user clicks the "Delete Participant" button
    Then An error is shown containing the information of the participant
    And The participant is removed from the Participant list
    
  Scenario: Update a Participant
    Given The user selects a participant from the Participant list
    And The selected participant is populated in text fields
    And The user provides Updated participant data in the text fields
    When The user clicks the "Update Participant" button
    Then The Participant list contains the updated Participant
    
    Scenario: Update Participant with new email
    Given The user selects a participant from the Participant list
    And The selected participant is populated in text fields
    And The user provides Updated participant data in the text fields specifying new email
    When The user clicks the "Update Participant" button
    Then An error is shown containing the information of the participant
    