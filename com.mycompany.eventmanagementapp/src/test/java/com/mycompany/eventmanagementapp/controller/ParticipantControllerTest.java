/**
 * ParticipantControllerTest is a unit test class that verifies the functionality of the ParticipantController 
 * class in the event management application. The tests cover various scenarios related to managing participants 
 * within events, including adding, updating, and deleting participants.
 * 
 * The following functionality is tested:
 * - Fetching all events and participants.
 * - Adding a participant to an event, ensuring valid participant data and event associations.
 * - Handling scenarios where the participant or event is null, non-existent, or already linked.
 * - Updating participant information, including name and email validation.
 * - Deleting participants from events, including edge cases where a participant is not found or is linked to multiple events.
 * 
 * The tests ensure that the ParticipantController performs as expected under normal and edge cases, including:
 * - Null or invalid input for participant or event data.
 * - Preventing duplicates and ensuring proper validation when adding a participant.
 * - Correct handling of participant data updates and event deletions.
 * 
 * The class uses the Mockito framework for mocking dependencies such as EventRepository, ParticipantRepository, 
 * and ParticipantManagementView. It also uses the JUnit framework for structuring the tests and verifying behaviors 
 * through assertions and verifications.
 * 
 * Key functionalities tested:
 * - testAllEvents()
 * - testAllParticipants()
 * - testAddParticipantWhenParticipantIsNull()
 * - testAddParticipantWhenEventIsNull()
 * - testAddParticipantWhenSelectedEventDoesNotExist()
 * - testAddParticipantToEventWhenParticipantWithEmailDoesNotExist()
 * - testAddParticipantToEventWhenParticipantAlreadyAssosciatedWithSelectedEvent()
 * - testAddParticipantToEventWhenParticipantWithEmailExistButNotAssociatedWithSelectedEvent()
 * - testDeleteParticipantWhenParticipantIsNull()
 * - testDeleteParticipantWhenEventIsNull()
 * - testDeleteParticipantWhenDoesNotExist()
 * - testDeleteParticipantWhenDoesNotAssociatedWithSelectedEvent()
 * - testDeleteParticipantWhenItHasSelectedEventAssociatedButNoMoreAssociatedEventsLeft()
 * - testDeleteParticipantWhenItHasSelectedEventAssociatedAndHasMoreAssociatedEventsLeft()
 * - testUpdateParticipantWhenParticipantIsNull()
 * - testUpdateParticipantWhenDoesNotExist()
 * - testUpdateParticipantWhenExist()
 * - testUpdateParticipantWhenNameIsNull()
 * - testUpdateParticipantWhenNameIsEmpty()
 * - testUpdateParticipantWhenEmailIsNull()
 * - testUpdateParticipantWhenEmailIsEmpty()
 * - testUpdateParticipantWhenEmailFormatIsInvalid()
 */

package com.mycompany.eventmanagementapp.controller;

import java.util.List;
import org.junit.Test;
import org.junit.After;
import org.mockito.Mock;
import org.junit.Before;
import java.util.Arrays;
import java.time.LocalDate;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

import com.mycompany.eventmanagementapp.model.EventModel;
import com.mycompany.eventmanagementapp.model.ParticipantModel;
import com.mycompany.eventmanagementapp.repository.EventRepository;
import com.mycompany.eventmanagementapp.view.ParticipantManagementView;
import com.mycompany.eventmanagementapp.repository.ParticipantRepository;

public class ParticipantControllerTest {
	@Mock
	private EventRepository eventRepository;

	@Mock
	private ParticipantRepository participantRepository;

	@Mock
	private ParticipantManagementView participantManagementView;

	@InjectMocks
	private ParticipantController participantController;

	private static final long EVENT_ID = 1;
	
	private static final long EVENT_ID_2 = 2;
	
	private static final String EVENT_NAME = "Music Festival";
	
	private static final LocalDate EVENT_DATE = LocalDate.now().plusDays(10);
	
	private static final String EVENT_LOCATION = "Florence";

	private static final long PARTICIPANT_ID = 1;
	
	private static final String PARTICIPANT_NAME = "John";
	
	private static final String PARTICIPANT_EMAIL = "John@gmail.com";
	
	private static final String PARTICIPANT_INVALID_EMAIL = "John@gmail";

	private AutoCloseable closeable;

	@Before
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
	}

	@After
	public void releaseMocks() throws Exception {
		closeable.close();
	}

	// Test case for fetching all events
	@Test
	public void testAllEvents() {
		//Setup
		List<EventModel> events = Arrays.asList(new EventModel());
		
		//Exercise
		when(eventRepository.getAllEvents()).thenReturn(events);
		participantController.getAllEvents();
		
		//Verify
		verify(participantManagementView).showAllEvents(events);
	}

	// Test case for fetching all participants
	@Test
	public void testAllParticipants() {
		//Setup
		List<ParticipantModel> participants = Arrays.asList(new ParticipantModel());
		
		//Exercise
		when(participantRepository.getAllParticipants()).thenReturn(participants);
		participantController.getAllParticipants();
		
		//Verify
		verify(participantManagementView).showAllParticipants(participants);
	}

	// Add Participant Test Cases
	// Test case for adding a participant when participant is null
	@Test
	public void testAddParticipantWhenParticipantIsNull() {
		//Setup
		EventModel selectedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		ParticipantModel participant = null;
		
		//Exercise
		participantController.addParticipant(participant, selectedEvent);
		
		//Verify
		verify(participantManagementView).showError("Selected event or participant is null", participant);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for adding a participant when selected event is null
	@Test
	public void testAddParticipantWhenEventIsNull() {
		//Setup
		EventModel selectedEvent = null;
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		
		//Exercise
		participantController.addParticipant(participant, selectedEvent);
		
		//Verify
		verify(participantManagementView).showError("Selected event or participant is null", participant);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for adding a participant when selected event does not exist
	@Test
	public void testAddParticipantWhenSelectedEventDoesNotExist() {
		//Setup
		EventModel selectedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		
		//Exercise
		when(eventRepository.getEventById(EVENT_ID)).thenReturn(null);
		participantController.addParticipant(participant, selectedEvent);
		
		//Verify
		verify(participantManagementView).showError("Event doesn't exist with id " + EVENT_ID, participant);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for adding a Participant to event when participant not already
	// exist in system
	@Test
	public void testAddParticipantToEventWhenParticipantWithEmailDoesNotExist() {
		//Setup
		EventModel selectedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		// Create a spy on the participant object to track addEvent call
		ParticipantModel spyParticipant = spy(participant);

		//Exercise
		when(eventRepository.getEventById(EVENT_ID)).thenReturn(selectedEvent);
		when(participantRepository.getParticipantByEmail(PARTICIPANT_EMAIL)).thenReturn(null);
		participantController.addParticipant(spyParticipant, selectedEvent);

		//Verify
		InOrder inOrder = inOrder(eventRepository, participantRepository, participantManagementView);
		inOrder.verify(participantRepository).addParticipant(spyParticipant);
		inOrder.verify(eventRepository).updateEvent(selectedEvent);
		inOrder.verify(participantManagementView).participantAdded(spyParticipant);
		verify(spyParticipant).addEvent(selectedEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for adding a Participant to event when participant is already
	// associated with selected event
	@Test
	public void testAddParticipantToEventWhenParticipantAlreadyAssosciatedWithSelectedEvent() {
		//Setup
		EventModel selectedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		participant.addEvent(selectedEvent);
		
		//Exercise
		when(eventRepository.getEventById(EVENT_ID)).thenReturn(selectedEvent);
		when(participantRepository.getParticipantByEmail(PARTICIPANT_EMAIL)).thenReturn(participant);
		participantController.addParticipant(participant, selectedEvent);
		
		//Verify
		verify(participantManagementView).showError("Participant already existed with email " + PARTICIPANT_EMAIL
				+ " and associated with event Id " + EVENT_ID, participant);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for adding a Participant to event when participant existed but not
	// associated with selected event.
	@Test
	public void testAddParticipantToEventWhenParticipantWithEmailExistButNotAssociatedWithSelectedEvent() {
	    //Setup
		EventModel selectedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
	    ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL); 
	    // Create a spy on the participant object to track addEvent call
	    ParticipantModel spyParticipant = spy(participant);

	    //Exercise
	    when(eventRepository.getEventById(EVENT_ID)).thenReturn(selectedEvent);
	    when(participantRepository.getParticipantByEmail(PARTICIPANT_EMAIL)).thenReturn(spyParticipant);
	    participantController.addParticipant(spyParticipant, selectedEvent);

	    //Verify
	    InOrder inOrder = inOrder(eventRepository, participantRepository, participantManagementView);
	    inOrder.verify(participantRepository).updateParticipant(spyParticipant);
	    inOrder.verify(eventRepository).updateEvent(selectedEvent);
	    inOrder.verify(participantManagementView).participantUpdated(spyParticipant);
	    // Verify that addEvent was called on the participant with the selectedEvent
	    verify(spyParticipant).addEvent(selectedEvent);
	    verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for adding a participant to event when the name is null
	@Test
	public void testAddParticipantWhenNameIsNull() {
		//Setup
		EventModel selectedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, null, PARTICIPANT_EMAIL);
		
		//Exercise
		participantController.addParticipant(participant, selectedEvent);
		
		//Verify
		verify(participantManagementView).showError("Name is required and cannot be null or empty", participant);
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for adding a participant to event when the name is empty
	@Test
	public void testAddParticipantWhenNameIsEmpty() {
		//Setup
		EventModel selectedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, "", PARTICIPANT_EMAIL);
		
		//Exercise
		participantController.addParticipant(participant, selectedEvent);
		
		//Verify
		verify(participantManagementView).showError("Name is required and cannot be null or empty", participant);
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for adding a participant to event when the email is null
	@Test
	public void testAddParticipantWhenEmailIsNull() {
		//Setup
		EventModel selectedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, null);
		
		//Exercise
		participantController.addParticipant(participant, selectedEvent);
		
		//Verify
		verify(participantManagementView).showError("Email cannot be null or empty", participant);
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for adding participant to event when the email is empty
	@Test
	public void testAddParticipantWhenEmailIsEmpty() {
		//Setup
		EventModel selectedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, "");
		
		//Exercise
		participantController.addParticipant(participant, selectedEvent);
		
		//Verify
		verify(participantManagementView).showError("Email cannot be null or empty", participant);
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for adding a participant to event when the email format is
	// invalid
	@Test
	public void testAddParticipantWhenEmailFormatIsInvalid() {
		//Setup
		EventModel selectedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME,
				PARTICIPANT_INVALID_EMAIL);
		
		//Exercise
		participantController.addParticipant(participant, selectedEvent);
		
		//Verify
		verify(participantManagementView).showError("Invalid email format.", participant);
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Delete Participant Test Cases
	// Test case for deleting a participant when participant is null
	@Test
	public void testDeleteParticipantWhenParticipantIsNull() {
		//Setup
		EventModel selectedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		ParticipantModel participant = null;
		
		//Exercise
		participantController.deleteParticipant(participant, selectedEvent);
		
		//Verify
		verify(participantManagementView).showError("Selected event or participant is null", participant);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for deleting a participant when selected event is null
	@Test
	public void testDeleteParticipantWhenEventIsNull() {
		//Setup
		EventModel selectedEvent = null;
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		
		//Exercise
		participantController.deleteParticipant(participant, selectedEvent);
		
		//Verify
		verify(participantManagementView).showError("Selected event or participant is null", participant);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for deleting a participant when it does not exist
	@Test
	public void testDeleteParticipantWhenDoesNotExist() {
		//Setup
		EventModel selectedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		
		//Exercise
		when(participantRepository.getParticipantByEmail(PARTICIPANT_EMAIL)).thenReturn(null);
		when(eventRepository.getEventById(selectedEvent.getEventId())).thenReturn(selectedEvent);
		participantController.deleteParticipant(participant, selectedEvent);
		
		//Verify
		verify(eventRepository).getEventById(selectedEvent.getEventId());
		verify(participantManagementView).showError("Participant doesn't exist with email " + PARTICIPANT_EMAIL,
				participant);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for deleting a participant from event when it is not associated
	// with selected event
	@Test
	public void testDeleteParticipantWhenDoesNotAssociatedWithSelectedEvent() {
		//Setup
		EventModel selectedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		
		//Exercise
		when(participantRepository.getParticipantByEmail(PARTICIPANT_EMAIL)).thenReturn(participant);
		when(eventRepository.getEventById(selectedEvent.getEventId())).thenReturn(selectedEvent);
		participantController.deleteParticipant(participant, selectedEvent);
		
		//Verify
		verify(eventRepository).getEventById(selectedEvent.getEventId());
		verify(participantManagementView).showError("Participant with email " + PARTICIPANT_EMAIL
				+ " is not associated with event Id " + selectedEvent.getEventId(), participant);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for deleting a participant from event when it is associated
	// with selected event and apart from that it has no more associated
	// events left.
	@Test
	public void testDeleteParticipantWhenItHasSelectedEventAssociatedButNoMoreAssociatedEventsLeft() {
		//Setup & Exercise
		EventModel selectedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		participant.addEvent(selectedEvent);
		when(participantRepository.getParticipantByEmail(PARTICIPANT_EMAIL)).thenReturn(participant);
		when(eventRepository.getEventById(selectedEvent.getEventId())).thenReturn(selectedEvent);
		participantController.deleteParticipant(participant, selectedEvent);
		
		//Verify
		InOrder inOrder = inOrder(eventRepository, participantRepository, participantManagementView);
		inOrder.verify(eventRepository).getEventById(selectedEvent.getEventId());
		inOrder.verify(participantRepository).updateParticipant(participant);
		inOrder.verify(eventRepository).updateEvent(selectedEvent);
		inOrder.verify(participantRepository).deleteParticipant(participant);
		inOrder.verify(participantManagementView).participantDeleted(participant);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for deleting a participant from event when it is associated
	// with selected event and apart from that it has more associated
	// events left.
	@Test
	public void testDeleteParticipantWhenItHasSelectedEventAssociatedAndHasMoreAssociatedEventsLeft() {
		//Setup & Exercise
		EventModel selectedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		EventModel additionalEvent = new EventModel(EVENT_ID_2, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		participant.addEvent(selectedEvent);
		participant.addEvent(additionalEvent);
		when(participantRepository.getParticipantByEmail(PARTICIPANT_EMAIL)).thenReturn(participant);
		when(eventRepository.getEventById(selectedEvent.getEventId())).thenReturn(selectedEvent);
		participantController.deleteParticipant(participant, selectedEvent);
		
		//Verify
		InOrder inOrder = inOrder(eventRepository, participantRepository, participantManagementView);
		inOrder.verify(eventRepository).getEventById(selectedEvent.getEventId());
		inOrder.verify(participantRepository).updateParticipant(participant);
		inOrder.verify(eventRepository).updateEvent(selectedEvent);
		inOrder.verify(participantManagementView).participantUpdated(participant);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Update Participant Test Cases
	// Test case for updating a participant when participant is null
	@Test
	public void testUpdateParticipantWhenParticipantIsNull() {
		//Setup
		ParticipantModel participant = null;
		
		//Exercise
		participantController.updateParticipant(participant);
		
		//Verify
		verify(participantManagementView).showError("Participant is null", participant);
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for updating a participant when it does not exist
	@Test
	public void testUpdateParticipantWhenDoesNotExist() {
		//Setup
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		
		//Exercise
		when(participantRepository.getParticipantByEmail(PARTICIPANT_EMAIL)).thenReturn(null);
		participantController.updateParticipant(participant);
		
		//Verify
		verify(participantManagementView).showError("Participant doesn't exist with email " + PARTICIPANT_EMAIL,
				participant);
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for updating a Participant when participant existed
	@Test
	public void testUpdateParticipantWhenExist() {
		//Setup
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		
		//Exercise
		when(participantRepository.getParticipantByEmail(PARTICIPANT_EMAIL)).thenReturn(participant);
		participantController.updateParticipant(participant);
		
		//Verify
		InOrder inOrder = inOrder(participantRepository, participantManagementView);
		inOrder.verify(participantRepository).updateParticipant(participant);
		inOrder.verify(participantManagementView).participantUpdated(participant);
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for updating participant when the name is null
	@Test
	public void testUpdateParticipantWhenNameIsNull() {
		//Setup
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, null, PARTICIPANT_EMAIL);
		
		//Exercise
		participantController.updateParticipant(participant);
		
		//Verify
		verify(participantManagementView).showError("Name is required and cannot be null or empty", participant);
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for updating a participant when name is empty
	@Test
	public void testUpdateParticipantWhenNameIsEmpty() {
		//Setup
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, "", PARTICIPANT_EMAIL);
		
		//Exercise
		participantController.updateParticipant(participant);
		
		//Verify
		verify(participantManagementView).showError("Name is required and cannot be null or empty", participant);
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for updating a participant when email is null
	@Test
	public void testUpdateParticipantWhenEmailIsNull() {
		//Setup
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, null);
		
		//Exercise
		participantController.updateParticipant(participant);
		
		//Verify
		verify(participantManagementView).showError("Email cannot be null or empty", participant);
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for updating a participant when email is empty
	@Test
	public void testUpdateParticipantWhenEmailIsEmpty() {
		//Setup
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, "");
		
		//Exercise
		participantController.updateParticipant(participant);
		
		//Verify
		verify(participantManagementView).showError("Email cannot be null or empty", participant);
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for updating a participant when email format is invalid
	@Test
	public void testUpdateParticipantWhenEmailFormatIsInvalid() {
		//Setup
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME,
				PARTICIPANT_INVALID_EMAIL);
		
		//Exercise
		participantController.updateParticipant(participant);
		
		//Verify
		verify(participantManagementView).showError("Invalid email format.", participant);
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}
}
