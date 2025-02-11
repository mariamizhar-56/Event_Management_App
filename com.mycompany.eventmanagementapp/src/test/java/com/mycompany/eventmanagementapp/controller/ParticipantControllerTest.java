
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

	public static final long EVENT_ID = 1;
	public static final long EVENT_ID_2 = 2;
	public static final String EVENT_NAME = "Music Festival";
	public static final LocalDate EVENT_DATE = LocalDate.of(2026, 10, 5);
	public static final String EVENT_LOCATION = "Florence";

	public static final long PARTICIPANT_ID = 1;
	public static final String PARTICIPANT_NAME = "John";
	public static final String PARTICIPANT_EMAIL = "John@gmail.com";
	public static final String PARTICIPANT_INVALID_EMAIL = "John@gmail";

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
		when(eventRepository.getEventById(EVENT_ID)).thenReturn(selectedEvent);
		when(participantRepository.getParticipantByEmail(PARTICIPANT_EMAIL)).thenReturn(null);
		participantController.addParticipant(participant, selectedEvent);
		InOrder inOrder = inOrder(eventRepository, participantRepository, participantManagementView);
		inOrder.verify(participantRepository).addParticipant(participant);
		inOrder.verify(eventRepository).updateEvent(selectedEvent);
		inOrder.verify(participantManagementView).participantAdded(participant);
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
		when(eventRepository.getEventById(EVENT_ID)).thenReturn(selectedEvent);
		when(participantRepository.getParticipantByEmail(PARTICIPANT_EMAIL)).thenReturn(participant);
		participantController.addParticipant(participant, selectedEvent);
		InOrder inOrder = inOrder(eventRepository, participantRepository, participantManagementView);
		inOrder.verify(participantRepository).updateParticipant(participant);
		inOrder.verify(eventRepository).updateEvent(selectedEvent);
		inOrder.verify(participantManagementView).participantUpdated(participant);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for adding a adding participant to event when the name is null
	@Test
	public void testAddEventWhenNameIsNull() {
		EventModel selectedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, null, PARTICIPANT_EMAIL);
		participantController.addParticipant(participant, selectedEvent);
		verify(participantManagementView).showError("Name is required and cannot be null or empty", participant);
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for adding a adding participant to event when the name is empty
	@Test
	public void testAddParticipantWhenNameIsEmpty() {
		EventModel selectedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, "", PARTICIPANT_EMAIL);
		participantController.addParticipant(participant, selectedEvent);
		verify(participantManagementView).showError("Name is required and cannot be null or empty", participant);
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for adding a adding participant to event when the email is null
	@Test
	public void testAddEventWhenEmailIsNull() {
		EventModel selectedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, null);
		participantController.addParticipant(participant, selectedEvent);
		verify(participantManagementView).showError("Email cannot be null or empty", participant);
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for adding a adding participant to event when the email is empty
	@Test
	public void testAddParticipantWhenEmailIsEmpty() {
		EventModel selectedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, "");
		participantController.addParticipant(participant, selectedEvent);
		verify(participantManagementView).showError("Email cannot be null or empty", participant);
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}

	// Test case for adding a adding participant to event when the email format is
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

	// Delete Event Test Cases
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
		participantController.deleteParticipant(participant, selectedEvent);
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
		participantController.deleteParticipant(participant, selectedEvent);
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
		participantController.deleteParticipant(participant, selectedEvent);
		InOrder inOrder = inOrder(eventRepository, participantRepository, participantManagementView);
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
		participantController.deleteParticipant(participant, selectedEvent);
		InOrder inOrder = inOrder(eventRepository, participantRepository, participantManagementView);
		inOrder.verify(participantRepository).updateParticipant(participant);
		inOrder.verify(eventRepository).updateEvent(selectedEvent);
		inOrder.verify(participantManagementView).participantUpdated(participant);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(ignoreStubs(participantRepository));
		verifyNoMoreInteractions(participantManagementView);
	}
}
