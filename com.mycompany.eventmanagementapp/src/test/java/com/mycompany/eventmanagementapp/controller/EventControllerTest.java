/**
 * EventControllerTest is a unit test class that verifies the functionality of the EventController 
 * class in the event management application. The tests cover various scenarios related to managing events, 
 * including adding, updating, and deleting events.
 * 
 * The following functionality is tested:
 * - Fetching all events.
 * - Adding a new event, ensuring valid event data (name, location, date) and avoiding duplicates.
 * - Handling scenarios where the event already exists, has invalid data, or the event is associated with participants.
 * - Updating an event's attributes such as name, location, and date, including validation checks.
 * - Deleting an event, including edge cases where the event is associated with participants or does not exist.
 * 
 * The tests ensure that the EventController performs as expected under normal and edge cases, including:
 * - Validating event attributes (name, location, date).
 * - Preventing duplicates and ensuring proper validation when adding or updating an event.
 * - Correct handling of event data updates and deletion when events are associated with participants.
 * 
 * The class uses the Mockito framework for mocking dependencies such as EventRepository, 
 * EventManagementView, and ParticipantRepository. It also uses the JUnit framework for structuring 
 * the tests and verifying behaviors through assertions and verifications.
 * 
 * Key functionalities tested:
 * - testAllEvents()
 * - testAddEventWhenEventDoesNotAlreadyExist()
 * - testAddEventWhenEventAlreadyExists()
 * - testAddEventWhenNameIsEmpty()
 * - testAddEventWhenLocationIsEmpty()
 * - testAddEventWhenDateIsInPast()
 * - testDeleteEventWhenExist()
 * - testUpdateEventWhenDoesNotExist()
 * - testUpdateEventWhenLocationIsEmpty()
 * - testUpdateEventWhenDateIsNull()
 */

package com.mycompany.eventmanagementapp.controller;

import java.util.Set;
import java.util.List;
import org.junit.Test;
import org.junit.After;
import org.mockito.Mock;
import org.junit.Before;
import java.util.Arrays;
import java.util.HashSet;
import java.time.LocalDate;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

import com.mycompany.eventmanagementapp.model.*;
import com.mycompany.eventmanagementapp.view.EventManagementView;
import com.mycompany.eventmanagementapp.repository.EventRepository;

public class EventControllerTest {

	@Mock
	private EventRepository eventRepository;

	@Mock
	private EventManagementView eventManagementView;

	@InjectMocks
	private EventController eventController;

	private AutoCloseable closeable;

	private static final long EVENT_ID = 1;
	
	private static final String EVENT_NAME = "Music Festival";
	
	private static final LocalDate EVENT_DATE = LocalDate.now().plusDays(10);
	
	private static final LocalDate EVENT_PAST_DATE = LocalDate.now().minusDays(10);
	
	private static final LocalDate EVENT_UPDATED_DATE = LocalDate.now().plusDays(30);
	
	private static final String EVENT_LOCATION = "Florence";

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
		eventController.getAllEvents();
		
		//Verify
		verify(eventManagementView).showAllEvents(events);
	}

	// AddEvent function Test Cases
	// Test case for adding a new event when it doesn't already exist
	@Test
	public void testAddEventWhenEventDoesNotAlreadyExist() {
		//Setup
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		
		//Exercise
		when(eventRepository.getEventById(EVENT_ID)).thenReturn(null);
		eventController.addEvent(event);
		
		//Verify
		InOrder inOrder = inOrder(eventRepository, eventManagementView);
		inOrder.verify(eventRepository).addEvent(event);
		inOrder.verify(eventManagementView).eventAdded(event);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(eventManagementView);
	}

	// Test case for adding a new event that already exists
	@Test
	public void testAddEventWhenEventAlreadyExists() {
		//Setup
		EventModel newEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		EventModel existingEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		
		//Exercise
		when(eventRepository.getEventById(EVENT_ID)).thenReturn(existingEvent);
		eventController.addEvent(newEvent);
		
		//Verify
		verify(eventManagementView).showError("Event already existed with id " + EVENT_ID, existingEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(eventManagementView);
	}

	// Test case for adding a new event when the name is empty
	@Test
	public void testAddEventWhenNameIsEmpty() {
		//Setup
		EventModel newEvent = new EventModel(EVENT_ID, "", EVENT_DATE, EVENT_LOCATION);
		
		//Exercise
		eventController.addEvent(newEvent);
		
		//Verify
		verify(eventManagementView).showError("Name is required and cannot be null or empty", newEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(eventManagementView);
	}

	// Test case for adding a new event when the name is null
	@Test
	public void testAddEventWhenNameIsNull() {
		//Setup
		EventModel newEvent = new EventModel(EVENT_ID, null, EVENT_DATE, EVENT_LOCATION);
		
		//Exercise
		eventController.addEvent(newEvent);
		
		//Verify
		verify(eventManagementView).showError("Name is required and cannot be null or empty", newEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(eventManagementView);
	}

	// Test case for adding a new event when the location is empty
	@Test
	public void testAddEventWhenLocationIsEmpty() {
		//Setup
		EventModel newEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, "");
		
		//Exercise
		eventController.addEvent(newEvent);
		
		//Verify
		verify(eventManagementView).showError("Location is required and cannot be null or empty", newEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(eventManagementView);
	}

	// Test case for adding a new event when the location is null
	@Test
	public void testAddEventWhenLocationIsNull() {
		//Setup
		EventModel newEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, null);
		
		//Exercise
		eventController.addEvent(newEvent);
		
		//Verify
		verify(eventManagementView).showError("Location is required and cannot be null or empty", newEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(eventManagementView);
	}

	// Test case for adding a new event when the Date is in past
	@Test
	public void testAddEventWhenDateIsInPast() {
		//Setup
		EventModel newEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_PAST_DATE, EVENT_LOCATION);
		
		//Exercise
		eventController.addEvent(newEvent);
		
		//Verify
		verify(eventManagementView).showError("Date cannot be in the past", newEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(eventManagementView);
	}

	// Test case for adding a new event when the Date is null
	@Test
	public void testAddEventWhenDateIsNull() {
		//Setup
		EventModel newEvent = new EventModel(EVENT_ID, EVENT_NAME, null, EVENT_LOCATION);
		
		//Exercise
		eventController.addEvent(newEvent);
		
		//Verify
		verify(eventManagementView).showError("Date is required and cannot be null", newEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(eventManagementView);
	}

	// Delete Event Test Cases
	// Test case for deleting an event when it exists
	@Test
	public void testDeleteEventWhenExist() {
		//Setup
		EventModel deleteEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		
		//Exercise
		when(eventRepository.getEventById(EVENT_ID)).thenReturn(deleteEvent);
		eventController.deleteEvent(deleteEvent);
		
		//Verify
		InOrder inOrder = inOrder(eventRepository, eventManagementView);
		inOrder.verify(eventRepository).deleteEvent(deleteEvent);
		inOrder.verify(eventManagementView).eventDeleted(deleteEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(eventManagementView);
	}

	// Test case for deleting an event when it does not exist
	@Test
	public void testDeleteEventWhenDoesNotExist() {
		//Setup
		EventModel deleteEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		
		//Exercise
		when(eventRepository.getEventById(EVENT_ID)).thenReturn(null);
		eventController.deleteEvent(deleteEvent);
		
		//Verify
		verify(eventManagementView).showError("Event doesn't exist with id " + EVENT_ID, deleteEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(eventManagementView);
	}

	// Test case for deleting an event that has associated participants
	@Test
	public void testDeleteEventWhenHasParticipants() {
		//Setup & Exercise
		EventModel deleteEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		// Create participant
		ParticipantModel participant = new ParticipantModel(1, "John", "john@gmail.com");
		// Initialize a set of participants
		Set<ParticipantModel> participants = new HashSet<>();
		participants.add(participant);
		deleteEvent.setParticipants(participants);
		when(eventRepository.getEventById(EVENT_ID)).thenReturn(deleteEvent);
		eventController.deleteEvent(deleteEvent);
		
		//Verify
		verify(eventManagementView).showError("Event cannot be deleted. Participants are associated with it",
				deleteEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
		verifyNoMoreInteractions(eventManagementView);
	}

	// Update Event Test Cases
	// Test case for updating an event name, here we are also verifying the EventModel.SetParticipants function that is being called in updateEvent function. This will give our Controller class 100 % mutation score.
	@Test
	public void testUpdateEventNameWhenExist() {
		//Setup
	    EventModel updatedEvent = new EventModel(EVENT_ID, EVENT_NAME + " Updated", EVENT_DATE, EVENT_LOCATION);
	    EventModel existingEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
	    
	    //Exercise
	    when(eventRepository.getEventById(EVENT_ID)).thenReturn(existingEvent);
	    // Create a spy on updatedEvent to track method calls
	    EventModel spyUpdatedEvent = spy(updatedEvent);
	    eventController.updateEvent(spyUpdatedEvent);

	    //Verify
	    InOrder inOrder = inOrder(eventRepository, eventManagementView);
	    inOrder.verify(eventRepository).updateEvent(spyUpdatedEvent);
	    inOrder.verify(eventManagementView).eventUpdated(spyUpdatedEvent);
	    // Verify that the setParticipants method was called with the existing participants
	    verify(spyUpdatedEvent).setParticipants(existingEvent.getParticipants());
	    verifyNoMoreInteractions(ignoreStubs(eventRepository));
	    verifyNoMoreInteractions((eventManagementView));
	}

	// Test case for updating an event location
	@Test
	public void testUpdateEventLocationWhenExist() {
		//Setup
		EventModel updatedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION + " Updated");
		EventModel existingEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		
		//Exercise
		when(eventRepository.getEventById(EVENT_ID)).thenReturn(existingEvent);
		eventController.updateEvent(updatedEvent);
		
		//Verify
		InOrder inOrder = inOrder(eventRepository, eventManagementView);
		inOrder.verify(eventRepository).updateEvent(updatedEvent);
		inOrder.verify(eventManagementView).eventUpdated(updatedEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
	    verifyNoMoreInteractions((eventManagementView));
	}

	// Test case for updating an event location
	@Test
	public void testUpdateEventDateWhenExist() {
		//Setup
		EventModel updatedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_UPDATED_DATE, EVENT_LOCATION);
		EventModel existingEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		
		//Exercise
		when(eventRepository.getEventById(EVENT_ID)).thenReturn(existingEvent);
		eventController.updateEvent(updatedEvent);
		
		//Verify
		InOrder inOrder = inOrder(eventRepository, eventManagementView);
		inOrder.verify(eventRepository).updateEvent(updatedEvent);
		inOrder.verify(eventManagementView).eventUpdated(updatedEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
	    verifyNoMoreInteractions((eventManagementView));
	}

	// Test case for updating an event when it does not exist
	@Test
	public void testUpdateEventWhenDoesNotExist() {
		//Setup
		EventModel updatedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_UPDATED_DATE, EVENT_LOCATION);
		
		//Exercise
		when(eventRepository.getEventById(EVENT_ID)).thenReturn(null);
		eventController.updateEvent(updatedEvent);
		
		//Verify
		verify(eventManagementView).showError("Event doesn't exist with id " + EVENT_ID, updatedEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
	    verifyNoMoreInteractions((eventManagementView));
	}

	// Test case for updating a new event when the name is empty
	@Test
	public void testUpdateEventWhenNameIsEmpty() {
		//Setup
		EventModel updatedEvent = new EventModel(EVENT_ID, "", EVENT_DATE, EVENT_LOCATION);
		
		//Exercise
		eventController.updateEvent(updatedEvent);
		
		//Verify
		verify(eventManagementView).showError("Name is required and cannot be null or empty", updatedEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
	    verifyNoMoreInteractions((eventManagementView));
	}

	// Test case for updating a new event when the name is null
	@Test
	public void testUpdateEventWhenNameIsNull() {
		//Setup
		EventModel updatedEvent = new EventModel(EVENT_ID, null, EVENT_DATE, EVENT_LOCATION);
		
		//Exercise
		eventController.updateEvent(updatedEvent);
		
		//Verify
		verify(eventManagementView).showError("Name is required and cannot be null or empty", updatedEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
	    verifyNoMoreInteractions((eventManagementView));
	}

	// Test case for updating a new event when the location is empty
	@Test
	public void testUpdateEventWhenLocationIsEmpty() {
		//Setup
		EventModel updatedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, "");
		
		//Exercise
		eventController.updateEvent(updatedEvent);
		
		//Verify
		verify(eventManagementView).showError("Location is required and cannot be null or empty", updatedEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
	    verifyNoMoreInteractions((eventManagementView));
	}

	// Test case for updating a new event when the location is null
	@Test
	public void testUpdateEventWhenLocationIsNull() {
		//Setup
		EventModel updatedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, null);
		
		//Exercise
		eventController.updateEvent(updatedEvent);
		
		//Verify
		verify(eventManagementView).showError("Location is required and cannot be null or empty", updatedEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
	    verifyNoMoreInteractions((eventManagementView));
	}

	// Test case for updating a new event when the Date is in past
	@Test
	public void testUpdateEventWhenDateIsInPast() {
		//Setup
		EventModel updatedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_PAST_DATE, EVENT_LOCATION);
		
		//Exercise
		eventController.updateEvent(updatedEvent);
		
		//Verify
		verify(eventManagementView).showError("Date cannot be in the past", updatedEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
	    verifyNoMoreInteractions((eventManagementView));
	}

	// Test case for updating a new event when the Date is null
	@Test
	public void testUpdateEventWhenDateIsNull() {
		//Setup
		EventModel updatedEvent = new EventModel(EVENT_ID, EVENT_NAME, null, EVENT_LOCATION);
		
		//Exercise
		eventController.updateEvent(updatedEvent);
		
		//Verify
		verify(eventManagementView).showError("Date is required and cannot be null", updatedEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
	    verifyNoMoreInteractions((eventManagementView));
	}
}