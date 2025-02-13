package com.mycompany.eventmanagementapp.controller.racecondition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.mycompany.eventmanagementapp.controller.EventController;
import com.mycompany.eventmanagementapp.model.EventModel;
import com.mycompany.eventmanagementapp.repository.EventRepository;
import com.mycompany.eventmanagementapp.view.EventManagementView;


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
	    List<EventModel> eventList = new ArrayList<>(); // Temporary storage simulating the database
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
	            .mapToObj(i -> new Thread(() -> eventController.addEvent(event))).peek(t -> t.start())
	            .collect(Collectors.toList());
	    // Wait for all threads to finish execution
	    await().atMost(10, TimeUnit.SECONDS).until(() -> threads.stream().noneMatch(t -> t.isAlive()));
	    // Ensure that only one event was added to the list
	    assertThat(eventList).containsExactly(event);
	}

	@Test
	public void testDeleteEventConcurrent() {
	    List<EventModel> eventList = new ArrayList<>(); // Temporary storage simulating the database
	    List<EventModel> deletedEventList = new ArrayList<>(); // Temporary storage for deleted events
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
	            .mapToObj(i -> new Thread(() -> eventController.deleteEvent(event))).peek(t -> t.start())
	            .collect(Collectors.toList());
	    // Wait for all threads to finish execution
	    await().atMost(10, TimeUnit.SECONDS).until(() -> threads.stream().noneMatch(t -> t.isAlive()));
	    // Ensure that the event is deleted and the delete action was properly recorded
	    assertThat(eventList).isEmpty();
	    assertThat(deletedEventList).containsExactly(event);
	    // Verify that the delete method was called only once
	    verify(eventRepository, times(1)).deleteEvent(any(EventModel.class));
	}

	@Test
	public void testUpdateEventConcurrent() {
	    List<EventModel> eventList = new ArrayList<>(); // Temporary storage simulating the database
	    List<EventModel> updatedEventList = new ArrayList<>(); // Temporary storage for updated events
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
	            .mapToObj(i -> new Thread(() -> eventController.updateEvent(event))).peek(t -> t.start())
	            .collect(Collectors.toList());
	    // Wait for all threads to finish execution
	    await().atMost(10, TimeUnit.SECONDS).until(() -> threads.stream().noneMatch(t -> t.isAlive()));
	    // Ensure that the event is updated correctly
	    assertThat(eventList).isEmpty();
	    assertThat(updatedEventList).containsExactly(event);
	    // Verify that the update method was called only once
	    verify(eventRepository, times(1)).updateEvent(any(EventModel.class));
	}
}

