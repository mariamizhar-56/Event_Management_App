/**
 * EventControllerRaceConditionTest is a class that contains unit tests to verify the handling of race conditions
 * in the EventController class. It ensures that methods related to adding, updating, and deleting events are 
 * thread-safe when executed concurrently.
 * 
 * The tests simulate concurrent operations on event data in a mock database and validate the expected behavior
 * of the system under race conditions. This is achieved by using multiple threads to simulate concurrent actions 
 * such as adding, deleting, and updating events.
 * 
 * The tests utilize the Mockito framework for mocking dependencies and the Awaitility library to manage thread 
 * synchronization. Each test verifies that only one event is added, deleted, or updated, and that the event 
 * repository methods are invoked correctly.
 * 
 * Key functionalities tested:
 * - Adding an event concurrently and ensuring only one event is added.
 * - Deleting an event concurrently and ensuring the event is deleted correctly.
 * - Updating an event concurrently and ensuring the event is updated correctly.
 * 
 * The tests also validate the proper invocation of event repository methods and ensure that concurrent operations 
 * do not result in inconsistent state or race conditions.
 * 
 * Dependencies:
 * - Mockito for mocking dependencies (EventRepository and EventManagementView).
 * - Awaitility for managing thread synchronization.
 * - AssertJ for assertion and verification of results.
 * 
 * Tests include:
 * - testAddEventConcurrent()
 * - testDeleteEventConcurrent()
 * - testUpdateEventConcurrent()
 */

package com.mycompany.eventmanagementapp.controller.racecondition;

import org.junit.Test;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.mockito.Mock;
import java.time.LocalDate;
import java.util.ArrayList;
import org.mockito.InjectMocks;
import java.util.stream.IntStream;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doAnswer;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.eventmanagementapp.model.EventModel;
import com.mycompany.eventmanagementapp.view.EventManagementView;
import com.mycompany.eventmanagementapp.controller.EventController;
import com.mycompany.eventmanagementapp.repository.EventRepository;

public class EventControllerRaceConditionTest {

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
	
	private static final String EVENT_LOCATION = "Florence";

	@Before
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
	}

	@After
	public void releaseMocks() throws Exception {
		closeable.close();
	}

	@Test
	public void testAddEventConcurrent() {
		// Temporary storage simulating the database
	    List<EventModel> eventList = new ArrayList<>();
	    // Create a new event
	    EventModel event = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
	    // Mock the getEventById method to simulate the repository behavior
	    when(eventRepository.getEventById(EVENT_ID))
	            .thenAnswer(invocation -> eventList.stream().findFirst().orElse(null));
	    // Mock the addEvent method to simulate adding the event to the storage
	    doAnswer(invocation -> {
	        eventList.add(event);
	        return null;
	    }).when(eventRepository).addEvent(any(EventModel.class));
	    // Simulate concurrent addition by creating multiple threads
	    List<Thread> threads = IntStream.range(0, 10)
	            .mapToObj(i -> new Thread(() -> eventController.addEvent(event))).peek(Thread::start)
	            .collect(Collectors.toList());
	    // Wait for all threads to finish execution
	    await().atMost(10, TimeUnit.SECONDS).until(() -> threads.stream().noneMatch(Thread::isAlive));
	    // Ensure that only one event was added to the list
	    assertThat(eventList).containsExactly(event);
	}

	@Test
	public void testDeleteEventConcurrent() {
		// Temporary storage simulating the database
	    List<EventModel> eventList = new ArrayList<>();
	 // Temporary storage for deleted events
	    List<EventModel> deletedEventList = new ArrayList<>();
	    // Create and add an event to the list
	    EventModel event = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
	    eventList.add(event);
	    // Mock the getEventById method to simulate the repository behavior
	    when(eventRepository.getEventById(EVENT_ID))
	    .thenAnswer(invocation -> eventList.stream().findFirst().orElse(null));
	    // Mock the delete method to simulate event deletion
	    doAnswer(invocation -> {
	        eventList.remove(event);
	        deletedEventList.add(event);
	        return null;
	    }).when(eventRepository).deleteEvent(any(EventModel.class));
	    // Simulate concurrent deletion by creating multiple threads
	    List<Thread> threads = IntStream.range(0, 10)
	            .mapToObj(i -> new Thread(() -> eventController.deleteEvent(event))).peek(Thread::start)
	            .collect(Collectors.toList());
	    // Wait for all threads to finish execution
	    await().atMost(10, TimeUnit.SECONDS).until(() -> threads.stream().noneMatch(Thread::isAlive));
	    // Ensure that the event is deleted and the delete action was properly recorded
	    assertThat(eventList).isEmpty();
	    assertThat(deletedEventList).containsExactly(event);
	    // Verify that the delete method was called only once
	    verify(eventRepository, times(1)).deleteEvent(any(EventModel.class));
	}

	@Test
	public void testUpdateEventConcurrent() {
		// Temporary storage simulating the database
	    List<EventModel> eventList = new ArrayList<>();
	    // Temporary storage for updated events
	    List<EventModel> updatedEventList = new ArrayList<>();
	    // Create and add an event to the list
	    EventModel event = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
	    eventList.add(event);
	    // Mock the getEventById method to simulate the repository behavior
	    when(eventRepository.getEventById(EVENT_ID))
	    .thenAnswer(invocation -> eventList.stream().findFirst().orElse(null));
	    // Mock the update method to simulate the event update
	    doAnswer(invocation -> {
	        eventList.remove(event);
	        updatedEventList.add(event);
	        return null;
	    }).when(eventRepository).updateEvent(any(EventModel.class));
	    // Simulate concurrent updates by creating multiple threads
	    List<Thread> threads = IntStream.range(0, 10)
	            .mapToObj(i -> new Thread(() -> eventController.updateEvent(event))).peek(Thread::start)
	            .collect(Collectors.toList());
	    // Wait for all threads to finish execution
	    await().atMost(10, TimeUnit.SECONDS).until(() -> threads.stream().noneMatch(Thread::isAlive));
	    // Ensure that the event is updated correctly
	    assertThat(eventList).isEmpty();
	    assertThat(updatedEventList).containsExactly(event);
	    // Verify that the update method was called only once
	    verify(eventRepository, times(1)).updateEvent(any(EventModel.class));
	}
}