
package com.mycompany.eventmanagementapp.controller;

import com.mycompany.eventmanagementapp.model.EventModel;
import com.mycompany.eventmanagementapp.repository.EventRepository;
import com.mycompany.eventmanagementapp.view.EventManagementView;
import com.mycompany.eventmanagementapp.model.EventModel;
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
import java.util.List;

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
		List<EventModel> event = Arrays.asList(new EventModel());
		when(eventRepository.getAllEvents()).thenReturn(event);
		eventController.getAllEvents();
		verify(eventManagementView).showAllEvents(event);
	}

	// AddEvent function Test Cases
	// Test case for adding a new event when it doesn't already exist
	@Test
	public void testAddEventWhenEventDoesNotAlreadyExist() {
		EventModel event = new EventModel(1, "Music Festival", LocalDate.of(2026, 10, 5), "Florence");
		when(eventRepository.getEventById(1)).thenReturn(null);
		eventController.addEvent(event);
		InOrder inOrder = inOrder(eventRepository, eventManagementView);
		inOrder.verify(eventRepository).addEvent(event);
		inOrder.verify(eventManagementView).eventAdded(event);
	}

	// Test case for adding a new event that already exists
	@Test
	public void testAddEventWhenEventAlreadyExists() {
		EventModel newEvent = new EventModel(1, "Music Festival", LocalDate.of(2026, 10, 5), "Florence");
		EventModel existingEvent = new EventModel(1, "Music Party", LocalDate.of(2026, 10, 5), "Florence");
		when(eventRepository.getEventById(1)).thenReturn(existingEvent);
		eventController.addEvent(newEvent);
		verify(eventManagementView).showError("Event already existed with id 1", existingEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
	}

	// Test case for adding a new event when the name is empty
	@Test
	public void testAddEventWhenNameIsEmpty() {
		EventModel newEvent = new EventModel(1, "", LocalDate.of(2026, 10, 5), "Florence");
		eventController.addEvent(newEvent);
		verify(eventManagementView).showError("Name is required and cannot be null or empty", newEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
	}

	// Test case for adding a new event when the name is null
	@Test
	public void testAddEventWhenNameIsNull() {
		EventModel newEvent = new EventModel(1, null, LocalDate.of(2026, 10, 5), "Florence");
		eventController.addEvent(newEvent);
		verify(eventManagementView).showError("Name is required and cannot be null or empty", newEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
	}

	// Test case for adding a new event when the location is empty
	@Test
	public void testAddEventWhenLocationIsEmpty() {
		EventModel newEvent = new EventModel(1, "Music Festival", LocalDate.of(2026, 10, 5), "");
		eventController.addEvent(newEvent);
		verify(eventManagementView).showError("Location is required and cannot be null or empty", newEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
	}

	// Test case for adding a new event when the location is null
	@Test
	public void testAddEventWhenLocationIsNull() {
		EventModel newEvent = new EventModel(1, "Music Festival", LocalDate.of(2026, 10, 5), null);
		eventController.addEvent(newEvent);
		verify(eventManagementView).showError("Location is required and cannot be null or empty", newEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
	}

	// Test case for adding a new event when the Date is in past
	@Test
	public void testAddEventWhenDateIsInPast() {
		EventModel newEvent = new EventModel(1, "Music Festival", LocalDate.of(2022, 10, 5), "Florence");
		eventController.addEvent(newEvent);
		verify(eventManagementView).showError("Date cannot be in the past", newEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
	}

	// Test case for adding a new event when the Date is null
	@Test
	public void testAddEventWhenDateIsNull() {
		EventModel newEvent = new EventModel(1, "Music Festival", null, "Florence");
		eventController.addEvent(newEvent);
		verify(eventManagementView).showError("Date is required and cannot be null", newEvent);
		verifyNoMoreInteractions(ignoreStubs(eventRepository));
	}
}
