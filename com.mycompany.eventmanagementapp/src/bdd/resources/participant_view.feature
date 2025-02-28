Feature: Participant Application Frame
  Specifications of the behavior of the Participant Application Frame

  Scenario: The initial state of the view
    Given The database contain the Participants with the following values
      | John   | john@gmail.com   | 1 | Music Festival   | Florence | 2030-01-15 |
      | John   | john@gmail.com   | 2 | University Event | Milan    | 2030-01-16 |
      | Martin | martin@gmail.com | 1 | Music Festival   | Florence | 2030-01-15 |
    When The Participant View is shown
    Then The Participant list contains an element with the following values
      | John   | john@gmail.com   |
      | Martin | martin@gmail.com |
    And The Event list contains an element with the following values
    	| Music Festival   | Florence | 2030-01-15 |
      | University Event | Milan    | 2030-01-16 |

  Scenario: Add a new Participant
    Given The database contain events for participant with the following values
      | 1 | Music Festival   | Florence | 2030-01-15 |
      | 2 | University Event | Milan    | 2030-01-16 |
    When The Participant View is shown
    And The user enters the following values in the text fields
      | txtEventId | txtParticipantName | txtParticipantEmail       |
      | 1          | testParticipant    | testparticipant@gmail.com |
    And The user clicks the "Add Participant" button
    Then The Participant list contains an element with the following values
     | testParticipant    | testparticipant@gmail.com |

  Scenario: Add a new Participant with invalid event Id
    Given The database contain events for participant with the following values
      | 1 | Music Festival   | Florence | 2030-01-15 |
      | 2 | University Event | Milan    | 2030-01-16 |
    When The Participant View is shown
    And The user enters the following values in the text fields
      | txtEventId | txtParticipantName | txtParticipantEmail       |
      | 9          | testParticipant    | testparticipant@gmail.com |
    And The user clicks the "Add Participant" button
    Then An error is shown containing the following values
      | testParticipant    | testparticipant@gmail.com |

  Scenario: Delete Participant which is only associated to one Event
    Given The database contain the Participants with the following values
      | John   | john@gmail.com   | 1 | Music Festival   | Florence | 2030-01-15 |
      | John   | john@gmail.com   | 2 | University Event | Milan    | 2030-01-16 |
      | Martin | martin@gmail.com | 1 | Music Festival   | Florence | 2030-01-15 |
    And The Participant View is shown
    And The user selects a participant which only associated to one Event from the Participant list
    And The user selects an event from the Event list
    When The user clicks the "Delete Participant" button
    Then The participant is removed from the Participant list
    
  Scenario: Delete Participant which is associated to more than one Event
    Given The database contain the Participants with the following values
      | John   | john@gmail.com   | 1 | Music Festival   | Florence | 2030-01-15 |
      | John   | john@gmail.com   | 2 | University Event | Milan    | 2030-01-16 |
      | Martin | martin@gmail.com | 1 | Music Festival   | Florence | 2030-01-15 |
    And The Participant View is shown
    And The user selects a participant which associated to more than one Event from the Participant list
    And The user selects an event from the Event list
    When The user clicks the "Delete Participant" button
    Then The participant is removed from that Event but stays in Participant list

  Scenario: Update a Participant
    Given The database contain the Participants with the following values
      | John   | john@gmail.com   | 1 | Music Festival   | Florence | 2030-01-15 |
      | John   | john@gmail.com   | 2 | University Event | Milan    | 2030-01-16 |
      | Martin | martin@gmail.com | 1 | Music Festival   | Florence | 2030-01-15 |
    And The Participant View is shown
    When The user selects a participant from the Participant list
    Then All values are populated
      | txtParticipantName | txtParticipantEmail |
      | Martin             | martin@gmail.com    |
    When The user enters the following values in the text fields
      | txtParticipantName |
      | Martin2            |
    And The user clicks the "Update Participant" button
    Then The Participant list contains an element with the following values
      | John    | john@gmail.com   |
      | Martin2 | martin@gmail.com |

  Scenario: Update Participant with new email
    Given The database contain the Participants with the following values
      | John   | john@gmail.com   | 1 | Music Festival   | Florence | 2030-01-15 |
      | John   | john@gmail.com   | 2 | University Event | Milan    | 2030-01-16 |
      | Martin | martin@gmail.com | 1 | Music Festival   | Florence | 2030-01-15 |
    And The Participant View is shown
    When The user selects a participant from the Participant list
    Then All values are populated
      | txtParticipantName | txtParticipantEmail |
      | Martin             | martin@gmail.com    |
    When The user enters the following values in the text fields
      | txtParticipantEmail |
      | martin2@gmail.com   |
    And The user clicks the "Update Participant" button
    Then An error is shown containing the following values
       | Martin | martin2@gmail.com |
    

