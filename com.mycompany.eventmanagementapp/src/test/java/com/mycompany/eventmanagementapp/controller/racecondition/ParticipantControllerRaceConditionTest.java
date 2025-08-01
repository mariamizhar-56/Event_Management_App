/**
 * ParticipantControllerRaceConditionTest is a test class that focuses on testing the race conditions 
 * in the ParticipantController class. This test ensures that the concurrent addition, deletion, 
 * and update of participants in the system are handled correctly by simulating concurrent actions 
 * using multiple threads.
 * 
 * The tests validate the thread-safety of operations involving participants, events, and their relationships 
 * (e.g., adding participants to events, deleting participants, and updating participant data).
 * 
 * The following operations are tested under concurrent conditions:
 * 1. Adding a new participant to an event concurrently.
 * 2. Adding an existing participant to an event concurrently.
 * 3. Deleting a participant when they are linked to a single event or multiple events.
 * 4. Updating a participant concurrently.
 * 
 * Each test simulates a scenario where multiple threads perform operations such as adding, deleting, or updating 
 * participants and ensures that no duplicates occur, no incorrect updates are made, and that the system behaves 
 * consistently in a multi-threaded environment. 
 * 
 * The tests use the Mockito framework for mocking dependencies and the Awaitility library to synchronize 
 * thread execution. The assertions validate that repository methods are called correctly, and race conditions 
 * do not affect the integrity of the data.
 * 
 * Dependencies:
 * - Mockito for mocking dependencies (EventRepository, ParticipantRepository, ParticipantManagementView).
 * - Awaitility for managing thread synchronization.
 * - AssertJ for assertion and verification of results.
 * 
 * Key functionalities tested:
 * - testAddParticipantToEventConcurrent()
 * - testAddExistingParticipantToEventConcurrent()
 * - testDeleteParticipantConcurrentWhenOnlyOneEventLinked()
 * - testDeleteParticipantConcurrentWhenMultipleEventsLinked()
 * - testUpdateParticipantConcurrent()
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
import com.mycompany.eventmanagementapp.model.ParticipantModel;
import com.mycompany.eventmanagementapp.repository.EventRepository;
import com.mycompany.eventmanagementapp.view.ParticipantManagementView;
import com.mycompany.eventmanagementapp.repository.ParticipantRepository;
import com.mycompany.eventmanagementapp.controller.ParticipantController;

public class ParticipantControllerRaceConditionTest {

	@Mock
	private EventRepository eventRepository;
	
	@Mock
	private ParticipantRepository participantRepository;
	
	@Mock
	private ParticipantManagementView participantManagementView;

	@InjectMocks
	private ParticipantController participantController;

	private AutoCloseable closeable;

	private static final long EVENT_ID = 1;
	
	private static final String EVENT_NAME = "Music Festival";
	
	private static final LocalDate EVENT_DATE = LocalDate.now().plusDays(10);
	
	private static final String EVENT_LOCATION = "Florence";
	
	private static final long EVENT_ID_2 = 2;
	
	private static final String EVENT_NAME_2 = "University Event";
	
	private static final LocalDate EVENT_DATE_2 = LocalDate.now().plusDays(20);
	
	private static final String EVENT_LOCATION_2 = "Milan";

	private static final long PARTICIPANT_ID = 1;
	
	private static final String PARTICIPANT_NAME = "John";
	
	private static final String PARTICIPANT_EMAIL = "John@gmail.com";

	@Before
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
	}

	@After
	public void releaseMocks() throws Exception {
		closeable.close();
	}

	@Test
	public void testAddParticipantToEventConcurrent() {
		// Create a fake in-memory database for events and participants
		List<ParticipantModel> participants = new ArrayList<>();

		// Create a new event
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);

		// Create a new participant
		ParticipantModel newParticipant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);

		// Stub the repository methods
		when(eventRepository.getEventById(EVENT_ID)).thenReturn(event);

		// Simulate the scenario where the participant does not exist initially
		when(participantRepository.getParticipantByEmail(PARTICIPANT_EMAIL))
				.thenAnswer(invocation -> participants.stream().findFirst().orElse(null));

		// Stub the addParticipant and updateEvent methods to simulate the behavior
		doAnswer(invocation -> {
			newParticipant.addEvent(event); // Create Link with Participant and Event
			participants.add(newParticipant); // Add the participant to the "database"
			return null;
		}).when(participantRepository).addParticipant(any(ParticipantModel.class));

		doAnswer(invocation -> {
			// updateEvent make sure that linking between Participant and Event will Persist
			// in Database, here we will not add any method to copy that functionality
			// because it is not required here.
			return null;
		}).when(eventRepository).updateEvent(any(EventModel.class));

		// Start multiple threads to simulate concurrent access
		List<Thread> threads = IntStream.range(0, 10)
				.mapToObj(i -> new Thread(() -> participantController.addParticipant(newParticipant, event)))
				.peek(Thread::start).collect(Collectors.toList());

		// Wait for all threads to finish execution
		await().atMost(10, TimeUnit.SECONDS).until(() -> threads.stream().noneMatch(Thread::isAlive));

		// Verify that only one participant was added to the list (no duplicates)
		assertThat(participants).containsExactly(newParticipant);
		// Verify that participant has been linked to specific event
		assertThat(participants.get(0).getEvents()).containsExactly(event);

		// Verify that the addParticipant method was called exactly once
		verify(participantRepository, times(1)).addParticipant(any(ParticipantModel.class));
		// Verify that the updateEvent method was called exactly once
		verify(eventRepository, times(1)).updateEvent(any(EventModel.class));
	}

	@Test
	public void testAddExistingParticipantToEventConcurrent() {
		// Create a fake in-memory database for events and participants
		List<ParticipantModel> participants = new ArrayList<>();

		// Create a new event
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);

		// Create an existing participant
		ParticipantModel existingParticipant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME,
				PARTICIPANT_EMAIL);
		participants.add(existingParticipant);

		// Stub the repository methods
		when(eventRepository.getEventById(EVENT_ID)).thenReturn(event);
		when(participantRepository.getParticipantByEmail(PARTICIPANT_EMAIL)).thenReturn(existingParticipant);

		// Stub the updateParticipant and updateEvent methods
		doAnswer(invocation -> {
			participants.get(0).addEvent(event); // Adding link between Existing Participant and Event
			return null;
		}).when(participantRepository).updateParticipant(any(ParticipantModel.class));

		doAnswer(invocation -> {
			// In the eventRepository, simulate updating the event (no-op for this test)
			return null;
		}).when(eventRepository).updateEvent(any(EventModel.class));

		// Start multiple threads to simulate concurrent access
		List<Thread> threads = IntStream.range(0, 10)
				.mapToObj(i -> new Thread(() -> participantController.addParticipant(existingParticipant, event)))
				.peek(Thread::start).collect(Collectors.toList());

		// Wait for all threads to finish execution
		await().atMost(10, TimeUnit.SECONDS).until(() -> threads.stream().noneMatch(Thread::isAlive));

		// Verify that the participant's event associations were updated and not
		// duplicated
		assertThat(existingParticipant.getEvents()).containsExactly(event);

		// Verify that the updateParticipant method was called exactly once
		verify(participantRepository, times(1)).updateParticipant(any(ParticipantModel.class));
		// Verify that the updateEvent method was called exactly once
		verify(eventRepository, times(1)).updateEvent(any(EventModel.class));
	}

	@Test
	public void testDeleteParticipantConcurrentWhenOnlyOneEventLinked() {
	    List<EventModel> events = new ArrayList<>();
	    List<ParticipantModel> participants = new ArrayList<>();

	    // Create a new event and a participant linked to it
	    EventModel event = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
	    events.add(event);
	    
	    ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
	    participant.addEvent(event);  // Link participant to the event
	    participants.add(participant);

	    // Stub repository methods
	    when(eventRepository.getEventById(EVENT_ID)).thenReturn(event);
	    when(participantRepository.getParticipantByEmail(PARTICIPANT_EMAIL)).thenAnswer(invocation -> participants.stream().findFirst().orElse(null));

	    // Stub methods to simulate removing the event and deleting the participant
	    doAnswer(invocation -> {
	        //No Activity required here, event removal will be handled by controller itself, Update method will only persists those changes in DB
	        return null;
	    }).when(participantRepository).updateParticipant(any(ParticipantModel.class));
	    
	    doAnswer(invocation -> {
	        //No Activity required here
	        return null;
	    }).when(eventRepository).updateEvent(any(EventModel.class));
	    
	    doAnswer(invocation -> {
	        participants.remove(participant);
	        return null;
	    }).when(participantRepository).deleteParticipant(any(ParticipantModel.class));

	    // Start 10 threads to simulate concurrent access
	    List<Thread> threads = IntStream.range(0, 10)
				.mapToObj(i -> new Thread(() -> participantController.deleteParticipant(participant, event)))
				.peek(Thread::start).collect(Collectors.toList());

	    // Wait for all threads to finish
	    await().atMost(10, TimeUnit.SECONDS).until(() -> threads.stream().noneMatch(Thread::isAlive));

	    // Verify that the participant was deleted as they were associated with only one event
	    assertThat(participants).isEmpty();
	    assertThat(events).containsExactly(event);  // Event should remain intact

	    // Verify that the updateParticipant method was called once
	    verify(participantRepository, times(1)).updateParticipant(any(ParticipantModel.class));
	    verify(participantRepository, times(1)).deleteParticipant(any(ParticipantModel.class));
	    verify(eventRepository, times(1)).updateEvent(any(EventModel.class));
	}
	
	@Test
	public void testDeleteParticipantConcurrentWhenMultipleEventsLinked() {
	    List<ParticipantModel> participants = new ArrayList<>();

	    // Create two events and a participant linked to both
	    EventModel event1 = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
	    EventModel event2 = new EventModel(EVENT_ID_2, EVENT_NAME_2, EVENT_DATE_2, EVENT_LOCATION_2);

	    ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
	    participant.addEvent(event1);  // Link participant to event1
	    participant.addEvent(event2);  // Link participant to event2
	    participants.add(participant);

	    // Stub repository methods
	    when(eventRepository.getEventById(EVENT_ID)).thenReturn(event1);
	    when(participantRepository.getParticipantByEmail(PARTICIPANT_EMAIL)).thenAnswer(invocation -> participants.stream().findFirst().orElse(null));

	    // Stub methods to simulate removing the event from the participant's list
	    doAnswer(invocation -> {
	        // No Activity Required.
	        return null;
	    }).when(participantRepository).updateParticipant(any(ParticipantModel.class));

	    doAnswer(invocation -> {
	        // No Activity Required
	        return null;
	    }).when(eventRepository).updateEvent(any(EventModel.class));

	    // Start 10 threads to simulate concurrent access
	    List<Thread> threads = IntStream.range(0, 10)
	            .mapToObj(i -> new Thread(() -> participantController.deleteParticipant(participant, event1)))
	            .peek(Thread::start)
	            .collect(Collectors.toList());

	    // Wait for all threads to finish
	    await().atMost(10, TimeUnit.SECONDS).until(() -> threads.stream().noneMatch(Thread::isAlive));

	    // Verify that the participant is still in the list because they are linked to another event
	    assertThat(participants).containsExactly(participant);
	    assertThat(participant.getEvents()).containsExactly(event2);  // Only event1 was removed

	    // Verify that the updateParticipant method was called once
	    verify(participantRepository, times(1)).updateParticipant(any(ParticipantModel.class));
	    verify(participantRepository, times(0)).deleteParticipant(any(ParticipantModel.class));
	    verify(eventRepository, times(1)).updateEvent(any(EventModel.class));
	}
	
	@Test
	public void testUpdateParticipantConcurrent() {
		// Temporary storage simulating the database
	    List<ParticipantModel> participants = new ArrayList<>();
	 // Temporary storage for updated participants
	    List<ParticipantModel> updatedParticipants = new ArrayList<>();
	    // Create and add participant to the list
	    ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
	    participants.add(participant);
	    // Mock the getParticipantByEmail method to simulate the repository behavior
	    when(participantRepository.getParticipantByEmail(PARTICIPANT_EMAIL)).thenAnswer(invocation -> participants.stream().findFirst().orElse(null));

	    doAnswer(invocation -> {
	    	participants.remove(participant);
	    	updatedParticipants.add(participant);
	        return null;
	    }).when(participantRepository).updateParticipant(any(ParticipantModel.class));
	    // Simulate concurrent updates by creating multiple threads
	    List<Thread> threads = IntStream.range(0, 10)
	            .mapToObj(i -> new Thread(() -> participantController.updateParticipant(participant))).peek(Thread::start)
	            .collect(Collectors.toList());
	    // Wait for all threads to finish execution
	    await().atMost(10, TimeUnit.SECONDS).until(() -> threads.stream().noneMatch(Thread::isAlive));
	    // Ensure that the participant is updated correctly
	    assertThat(participants).isEmpty();
	    assertThat(updatedParticipants).containsExactly(participant);
	    // Verify that the update method was called only once
	    verify(participantRepository, times(1)).updateParticipant(any(ParticipantModel.class));
	}
}