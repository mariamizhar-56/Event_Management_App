
package com.mycompany.eventmanagementapp.controller;

import com.mycompany.eventmanagementapp.model.EventModel;
import com.mycompany.eventmanagementapp.model.ParticipantModel;
import com.mycompany.eventmanagementapp.repository.EventRepository;
import com.mycompany.eventmanagementapp.repository.ParticipantRepository;
import com.mycompany.eventmanagementapp.view.ParticipantManagementView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

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
		List<EventModel> events = Arrays.asList(new EventModel());
		when(eventRepository.getAllEvents()).thenReturn(events);
		participantController.getAllEvents();
		verify(participantManagementView).showAllEvents(events);
	}

	// Test case for fetching all participants
	@Test
	public void testAllParticipants() {
		List<ParticipantModel> participants = Arrays.asList(new ParticipantModel());
		when(participantRepository.getAllParticipants()).thenReturn(participants);
		participantController.getAllParticipants();
		verify(participantManagementView).showAllParticipants(participants);
	}

	// Add Participant Test Cases
	// Test case for adding a participant when participant is null
	@Test
	public void testAddParticipantWhenParticipantIsNull() {
		EventModel selectedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		ParticipantModel participant = null;
		participantController.addParticipant(participant, selectedEvent);
		verify(participantManagementView).showError("Selected event or participant is null", participant);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for adding a participant when selected event is null
	@Test
	public void testAddParticipantWhenEventIsNull() {
		EventModel selectedEvent = null;
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		participantController.addParticipant(participant, selectedEvent);
		verify(participantManagementView).showError("Selected event or participant is null", participant);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for adding a participant when selected event does not exist
	@Test
	public void testAddParticipantWhenSelectedEventDoesNotExist() {
		EventModel selectedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		when(eventRepository.getEventById(EVENT_ID)).thenReturn(null);
		participantController.addParticipant(participant, selectedEvent);
		verify(participantManagementView).showError("Event doesn't exist with id " + EVENT_ID, participant);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for adding a Participant to event when participant not already
	// exist in system
	@Test
	public void testAddParticipantToEventWhenParticipantWithEmailDoesNotExist() {
		EventModel selectedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);

		// Create a spy on the participant object to track addEvent call
		ParticipantModel spyParticipant = spy(participant);

		when(eventRepository.getEventById(EVENT_ID)).thenReturn(selectedEvent);
		when(participantRepository.getParticipantByEmail(PARTICIPANT_EMAIL)).thenReturn(null);

		// Call the addParticipant method
		participantController.addParticipant(spyParticipant, selectedEvent);

		// Create an inOrder verification to ensure methods are called in the correct
		// order
		InOrder inOrder = inOrder(eventRepository, participantRepository, participantManagementView);

		// Verify the addParticipant method is called on the repository
		inOrder.verify(participantRepository).addParticipant(spyParticipant);

		// Verify the updateEvent method is called on the repository
		inOrder.verify(eventRepository).updateEvent(selectedEvent);

		// Verify the participantAdded method is called in the view
		inOrder.verify(participantManagementView).participantAdded(spyParticipant);

		// Verify that addEvent was called on the participant with the existingEvent
		verify(spyParticipant).addEvent(selectedEvent);

		// Verify no more interactions with the repositories or the view
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for adding a Participant to event when participant is already
	// associated with selected event
	@Test
	public void testAddParticipantToEventWhenParticipantAlreadyAssosciatedWithSelectedEvent() {
		EventModel selectedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		participant.addEvent(selectedEvent);
		when(eventRepository.getEventById(EVENT_ID)).thenReturn(selectedEvent);
		when(participantRepository.getParticipantByEmail(PARTICIPANT_EMAIL)).thenReturn(participant);
		participantController.addParticipant(participant, selectedEvent);
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
	    EventModel selectedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
	    ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
	    
	    // Create a spy on the participant object to track addEvent call
	    ParticipantModel spyParticipant = spy(participant);

	    // Mock the behavior of the repository to return the participant
	    when(eventRepository.getEventById(EVENT_ID)).thenReturn(selectedEvent);
	    when(participantRepository.getParticipantByEmail(PARTICIPANT_EMAIL)).thenReturn(spyParticipant);

	    // Call the addParticipant method
	    participantController.addParticipant(spyParticipant, selectedEvent);

	    // Create an inOrder verification to ensure methods are called in the correct order
	    InOrder inOrder = inOrder(eventRepository, participantRepository, participantManagementView);
	    
	    // Verify that the updateParticipant method is called on the repository
	    inOrder.verify(participantRepository).updateParticipant(spyParticipant);
	    
	    // Verify that the updateEvent method is called on the repository
	    inOrder.verify(eventRepository).updateEvent(selectedEvent);
	    
	    // Verify that the participantUpdated method is called in the view
	    inOrder.verify(participantManagementView).participantUpdated(spyParticipant);

	    // Verify that addEvent was called on the participant with the selectedEvent
	    verify(spyParticipant).addEvent(selectedEvent);

	    // Verify no more interactions with the repositories or the view
	    verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}


	// Test case for adding a participant to event when the name is null
	@Test
	public void testAddParticipantWhenNameIsNull() {
		EventModel selectedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, null, PARTICIPANT_EMAIL);
		participantController.addParticipant(participant, selectedEvent);
		verify(participantManagementView).showError("Name is required and cannot be null or empty", participant);
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for adding a participant to event when the name is empty
	@Test
	public void testAddParticipantWhenNameIsEmpty() {
		EventModel selectedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, "", PARTICIPANT_EMAIL);
		participantController.addParticipant(participant, selectedEvent);
		verify(participantManagementView).showError("Name is required and cannot be null or empty", participant);
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for adding a participant to event when the email is null
	@Test
	public void testAddParticipantWhenEmailIsNull() {
		EventModel selectedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, null);
		participantController.addParticipant(participant, selectedEvent);
		verify(participantManagementView).showError("Email cannot be null or empty", participant);
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for adding participant to event when the email is empty
	@Test
	public void testAddParticipantWhenEmailIsEmpty() {
		EventModel selectedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, "");
		participantController.addParticipant(participant, selectedEvent);
		verify(participantManagementView).showError("Email cannot be null or empty", participant);
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for adding a participant to event when the email format is
	// invalid
	@Test
	public void testAddParticipantWhenEmailFormatIsInvalid() {
		EventModel selectedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME,
				PARTICIPANT_INVALID_EMAIL);
		participantController.addParticipant(participant, selectedEvent);
		verify(participantManagementView).showError("Invalid email format.", participant);
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Delete Participant Test Cases
	// Test case for deleting a participant when participant is null
	@Test
	public void testDeleteParticipantWhenParticipantIsNull() {
		EventModel selectedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		ParticipantModel participant = null;
		participantController.deleteParticipant(participant, selectedEvent);
		verify(participantManagementView).showError("Selected event or participant is null", participant);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for deleting a participant when selected event is null
	@Test
	public void testDeleteParticipantWhenEventIsNull() {
		EventModel selectedEvent = null;
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		participantController.deleteParticipant(participant, selectedEvent);
		verify(participantManagementView).showError("Selected event or participant is null", participant);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for deleting a participant when it does not exist
	@Test
	public void testDeleteParticipantWhenDoesNotExist() {
		EventModel selectedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		when(participantRepository.getParticipantByEmail(PARTICIPANT_EMAIL)).thenReturn(null);
		when(eventRepository.getEventById(selectedEvent.getEventId())).thenReturn(selectedEvent);
		participantController.deleteParticipant(participant, selectedEvent);
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
		EventModel selectedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		when(participantRepository.getParticipantByEmail(PARTICIPANT_EMAIL)).thenReturn(participant);
		when(eventRepository.getEventById(selectedEvent.getEventId())).thenReturn(selectedEvent);
		participantController.deleteParticipant(participant, selectedEvent);
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
		EventModel selectedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		participant.addEvent(selectedEvent);
		when(participantRepository.getParticipantByEmail(PARTICIPANT_EMAIL)).thenReturn(participant);
		when(eventRepository.getEventById(selectedEvent.getEventId())).thenReturn(selectedEvent);
		participantController.deleteParticipant(participant, selectedEvent);
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
		EventModel selectedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		EventModel AdditionalEvent = new EventModel(EVENT_ID_2, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		participant.addEvent(selectedEvent);
		participant.addEvent(AdditionalEvent);
		when(participantRepository.getParticipantByEmail(PARTICIPANT_EMAIL)).thenReturn(participant);
		when(eventRepository.getEventById(selectedEvent.getEventId())).thenReturn(selectedEvent);
		participantController.deleteParticipant(participant, selectedEvent);
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
		ParticipantModel participant = null;
		participantController.updateParticipant(participant);
		verify(participantManagementView).showError("Participant is null", participant);
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for updating a participant when it does not exist
	@Test
	public void testUpdateParticipantWhenDoesNotExist() {
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		when(participantRepository.getParticipantByEmail(PARTICIPANT_EMAIL)).thenReturn(null);
		participantController.updateParticipant(participant);
		verify(participantManagementView).showError("Participant doesn't exist with email " + PARTICIPANT_EMAIL,
				participant);
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for updating a Participant when participant existed
	@Test
	public void testUpdateParticipantWhenExist() {
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		when(participantRepository.getParticipantByEmail(PARTICIPANT_EMAIL)).thenReturn(participant);
		participantController.updateParticipant(participant);
		InOrder inOrder = inOrder(participantRepository, participantManagementView);
		inOrder.verify(participantRepository).updateParticipant(participant);
		inOrder.verify(participantManagementView).participantUpdated(participant);
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for updating participant when the name is null
	@Test
	public void testUpdateParticipantWhenNameIsNull() {
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, null, PARTICIPANT_EMAIL);
		participantController.updateParticipant(participant);
		verify(participantManagementView).showError("Name is required and cannot be null or empty", participant);
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for updating a participant when name is empty
	@Test
	public void testUpdateParticipantWhenNameIsEmpty() {
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, "", PARTICIPANT_EMAIL);
		participantController.updateParticipant(participant);
		verify(participantManagementView).showError("Name is required and cannot be null or empty", participant);
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for updating a participant when email is null
	@Test
	public void testUpdateParticipantWhenEmailIsNull() {
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, null);
		participantController.updateParticipant(participant);
		verify(participantManagementView).showError("Email cannot be null or empty", participant);
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for updating a participant when email is empty
	@Test
	public void testUpdateParticipantWhenEmailIsEmpty() {
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, "");
		participantController.updateParticipant(participant);
		verify(participantManagementView).showError("Email cannot be null or empty", participant);
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for updating a participant when email format is invalid
	@Test
	public void testUpdateParticipantWhenEmailFormatIsInvalid() {
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME,
				PARTICIPANT_INVALID_EMAIL);
		participantController.updateParticipant(participant);
		verify(participantManagementView).showError("Invalid email format.", participant);
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}
}
