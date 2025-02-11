
package com.mycompany.eventmanagementapp.controller;

import com.mycompany.eventmanagementapp.model.*;
import com.mycompany.eventmanagementapp.repository.EventRepository;
import com.mycompany.eventmanagementapp.view.EventManagementView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class EventControllerTest {

	@Mock
	private EventRepository eventRepository;

	@Mock
	private EventManagementView eventManagementView;

	@InjectMocks
	private EventController eventController;

	private AutoCloseable closeable;

	public static final long EVENT_ID = 1;
	public static final String EVENT_NAME = "Music Festival";
	public static final LocalDate EVENT_DATE = LocalDate.of(2026, 10, 5);
	public static final LocalDate EVENT_PAST_DATE = LocalDate.of(2022, 10, 5);
	public static final LocalDate EVENT_UPDATED_DATE = LocalDate.of(2027, 10, 5);
	public static final String EVENT_LOCATION = "Florence";

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
		eventController.getAllEvents();
		verify(eventManagementView).showAllEvents(events);
	}

	// AddEvent function Test Cases
	// Test case for adding a new event when it doesn't already exist
	@Test
	public void testAddEventWhenEventDoesNotAlreadyExist() {
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		when(eventRepository.getEventById(EVENT_ID)).thenReturn(null);
		eventController.addEvent(event);
		InOrder inOrder = inOrder(eventRepository, eventManagementView);
		inOrder.verify(eventRepository).addEvent(event);
		inOrder.verify(eventManagementView).eventAdded(event);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(eventManagementView);
	}

	// Test case for adding a new event that already exists
	@Test
	public void testAddEventWhenEventAlreadyExists() {
		EventModel newEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		EventModel existingEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		when(eventRepository.getEventById(EVENT_ID)).thenReturn(existingEvent);
		eventController.addEvent(newEvent);
		verify(eventManagementView).showError("Event already existed with id " + EVENT_ID, existingEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(eventManagementView);
	}

	// Test case for adding a new event when the name is empty
	@Test
	public void testAddEventWhenNameIsEmpty() {
		EventModel newEvent = new EventModel(EVENT_ID, "", EVENT_DATE, EVENT_LOCATION);
		eventController.addEvent(newEvent);
		verify(eventManagementView).showError("Name is required and cannot be null or empty", newEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(eventManagementView);
	}

	// Test case for adding a new event when the name is null
	@Test
	public void testAddEventWhenNameIsNull() {
		EventModel newEvent = new EventModel(EVENT_ID, null, EVENT_DATE, EVENT_LOCATION);
		eventController.addEvent(newEvent);
		verify(eventManagementView).showError("Name is required and cannot be null or empty", newEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(eventManagementView);
	}

	// Test case for adding a new event when the location is empty
	@Test
	public void testAddEventWhenLocationIsEmpty() {
		EventModel newEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, "");
		eventController.addEvent(newEvent);
		verify(eventManagementView).showError("Location is required and cannot be null or empty", newEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(eventManagementView);
	}

	// Test case for adding a new event when the location is null
	@Test
	public void testAddEventWhenLocationIsNull() {
		EventModel newEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, null);
		eventController.addEvent(newEvent);
		verify(eventManagementView).showError("Location is required and cannot be null or empty", newEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(eventManagementView);
	}

	// Test case for adding a new event when the Date is in past
	@Test
	public void testAddEventWhenDateIsInPast() {
		EventModel newEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_PAST_DATE, EVENT_LOCATION);
		eventController.addEvent(newEvent);
		verify(eventManagementView).showError("Date cannot be in the past", newEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(eventManagementView);
	}

	// Test case for adding a new event when the Date is null
	@Test
	public void testAddEventWhenDateIsNull() {
		EventModel newEvent = new EventModel(EVENT_ID, EVENT_NAME, null, EVENT_LOCATION);
		eventController.addEvent(newEvent);
		verify(eventManagementView).showError("Date is required and cannot be null", newEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(eventManagementView);
	}

	// Delete Event Test Cases
	// Test case for deleting an event when it exists
	@Test
	public void testDeleteEventWhenExist() {
		EventModel deleteEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		when(eventRepository.getEventById(EVENT_ID)).thenReturn(deleteEvent);
		eventController.deleteEvent(deleteEvent);
		InOrder inOrder = inOrder(eventRepository, eventManagementView);
		inOrder.verify(eventRepository).deleteEvent(deleteEvent);
		inOrder.verify(eventManagementView).eventDeleted(deleteEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(eventManagementView);
	}

	// Test case for deleting an event when it does not exist
	@Test
	public void testDeleteEventWhenDoesNotExist() {
		EventModel deleteEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		when(eventRepository.getEventById(EVENT_ID)).thenReturn(null);
		eventController.deleteEvent(deleteEvent);
		verify(eventManagementView).showError("Event doesn't exist with id " + EVENT_ID, deleteEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(eventManagementView);
	}

	// Test case for deleting an event that has associated participants
	@Test
	public void testDeleteEventWhenHasParticipants() {
		EventModel deleteEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		// Create participant
		ParticipantModel participant = new ParticipantModel(1, "John", "john@gmail.com");
		// Initialize a set of participants
		Set<ParticipantModel> participants = new HashSet<>();
		participants.add(participant);
		deleteEvent.setParticipants(participants);
		when(eventRepository.getEventById(EVENT_ID)).thenReturn(deleteEvent);
		eventController.deleteEvent(deleteEvent);
		verify(eventManagementView).showError("Event cannot be deleted. Participants are associated with it",
				deleteEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(eventManagementView);
	}

	// Update Event Test Cases
	// Test case for updating an event name, here we are also verifying the EventModel.SetParticipants function that is being called in updateEvent function. This will give our Controller class 100 % mutation score.
	@Test
	public void testUpdateEventNameWhenExist() {
	    EventModel updatedEvent = new EventModel(EVENT_ID, EVENT_NAME + " Updated", EVENT_DATE, EVENT_LOCATION);
	    EventModel existingEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
	    
	    // Mock the repository to return the existing event
	    when(eventRepository.getEventById(EVENT_ID)).thenReturn(existingEvent);

	    // Create a spy on updatedEvent to track method calls
	    EventModel spyUpdatedEvent = spy(updatedEvent);
	    
	    // Perform the update action
	    eventController.updateEvent(spyUpdatedEvent);

	    // Create an inOrder verification to ensure method calls happen in the correct order
	    InOrder inOrder = inOrder(eventRepository, eventManagementView);
	    
	    // Verify the updateEvent method is called
	    inOrder.verify(eventRepository).updateEvent(spyUpdatedEvent);
	    
	    // Verify the eventUpdated method is called
	    inOrder.verify(eventManagementView).eventUpdated(spyUpdatedEvent);
	    
	    // Verify that the setParticipants method was called with the existing participants
	    verify(spyUpdatedEvent).setParticipants(existingEvent.getParticipants());

	    // Verify no further interactions with the eventRepository or eventManagementView
	    verifyNoMoreInteractions(ignoreStubs(eventRepository));
	    verifyNoMoreInteractions((eventManagementView));
	}


	// Test case for updating an event location
	@Test
	public void testUpdateEventLocationWhenExist() {
		EventModel updatedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION + " Updated");
		EventModel existingEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		when(eventRepository.getEventById(EVENT_ID)).thenReturn(existingEvent);
		eventController.updateEvent(updatedEvent);
		InOrder inOrder = inOrder(eventRepository, eventManagementView);
		inOrder.verify(eventRepository).updateEvent(updatedEvent);
		inOrder.verify(eventManagementView).eventUpdated(updatedEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
	    verifyNoMoreInteractions((eventManagementView));
	}

	// Test case for updating an event location
	@Test
	public void testUpdateEventDateWhenExist() {
		EventModel updatedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_UPDATED_DATE, EVENT_LOCATION);
		EventModel existingEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		when(eventRepository.getEventById(EVENT_ID)).thenReturn(existingEvent);
		eventController.updateEvent(updatedEvent);
		InOrder inOrder = inOrder(eventRepository, eventManagementView);
		inOrder.verify(eventRepository).updateEvent(updatedEvent);
		inOrder.verify(eventManagementView).eventUpdated(updatedEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
	    verifyNoMoreInteractions((eventManagementView));
	}

	// Test case for updating an event when it does not exist
	@Test
	public void testUpdateEventWhenDoesNotExist() {
		EventModel updatedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_UPDATED_DATE, EVENT_LOCATION);
		when(eventRepository.getEventById(EVENT_ID)).thenReturn(null);
		eventController.updateEvent(updatedEvent);
		verify(eventManagementView).showError("Event doesn't exist with id " + EVENT_ID, updatedEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
	    verifyNoMoreInteractions((eventManagementView));
	}

	// Test case for updating a new event when the name is empty
	@Test
	public void testUpdateEventWhenNameIsEmpty() {
		EventModel updatedEvent = new EventModel(EVENT_ID, "", EVENT_DATE, EVENT_LOCATION);
		eventController.updateEvent(updatedEvent);
		verify(eventManagementView).showError("Name is required and cannot be null or empty", updatedEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
	    verifyNoMoreInteractions((eventManagementView));
	}

	// Test case for updating a new event when the name is null
	@Test
	public void testUpdateEventWhenNameIsNull() {
		EventModel updatedEvent = new EventModel(EVENT_ID, null, EVENT_DATE, EVENT_LOCATION);
		eventController.updateEvent(updatedEvent);
		verify(eventManagementView).showError("Name is required and cannot be null or empty", updatedEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
	    verifyNoMoreInteractions((eventManagementView));
	}

	// Test case for updating a new event when the location is empty
	@Test
	public void testUpdateEventWhenLocationIsEmpty() {
		EventModel updatedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, "");
		eventController.updateEvent(updatedEvent);
		verify(eventManagementView).showError("Location is required and cannot be null or empty", updatedEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
	    verifyNoMoreInteractions((eventManagementView));
	}

	// Test case for updating a new event when the location is null
	@Test
	public void testUpdateEventWhenLocationIsNull() {
		EventModel updatedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, null);
		eventController.updateEvent(updatedEvent);
		verify(eventManagementView).showError("Location is required and cannot be null or empty", updatedEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
	    verifyNoMoreInteractions((eventManagementView));
	}

	// Test case for updating a new event when the Date is in past
	@Test
	public void testUpdateEventWhenDateIsInPast() {
		EventModel updatedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_PAST_DATE, EVENT_LOCATION);
		eventController.updateEvent(updatedEvent);
		verify(eventManagementView).showError("Date cannot be in the past", updatedEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
	    verifyNoMoreInteractions((eventManagementView));
	}

	// Test case for updating a new event when the Date is null
	@Test
	public void testUpdateEventWhenDateIsNull() {
		EventModel updatedEvent = new EventModel(EVENT_ID, EVENT_NAME, null, EVENT_LOCATION);
		eventController.updateEvent(updatedEvent);
		verify(eventManagementView).showError("Date is required and cannot be null", updatedEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
	    verifyNoMoreInteractions((eventManagementView));
	}
}
