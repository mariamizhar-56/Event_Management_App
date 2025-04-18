/**
 * This class contains integration tests for the race conditions in the EventController of the Event Management Application.
 * The tests ensure that concurrent operations on events (add, update, delete) are handled correctly by the EventController,
 * and no data inconsistencies or concurrency issues occur.
 *
 * The tests simulate multiple threads attempting to perform the same operation on an event concurrently. The operations 
 * tested include:
 * - Adding an event concurrently.
 * - Updating an event concurrently.
 * - Deleting an event concurrently.
 *
 * Test Containers are used to spin up a MySQL container for isolated testing, while Hibernate ORM handles database interactions.
 * The tests use Awaitility to ensure that all threads complete before performing assertions, and they verify that only the 
 * expected event data remains after concurrency operations.
 *
 * The primary purpose of these tests is to ensure the stability and correctness of the EventController when multiple users 
 * interact with the application concurrently.
 */

package com.mycompany.eventmanagementapp.racecondition;

import org.junit.Test;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.mockito.Mock;
import java.time.LocalDate;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import java.util.stream.IntStream;
import java.util.stream.Collectors;
import org.hibernate.SessionFactory;
import java.util.concurrent.TimeUnit;
import org.mockito.MockitoAnnotations;
import org.hibernate.boot.MetadataSources;
import static org.awaitility.Awaitility.await;
import static org.assertj.core.api.Assertions.assertThat;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import com.mycompany.eventmanagementapp.model.EventModel;
import com.mycompany.eventmanagementapp.controller.EventController;
import com.mycompany.eventmanagementapp.repository.EventRepository;
import com.mycompany.eventmanagementapp.dbconfigurations.DBConfigSetup;
import com.mycompany.eventmanagementapp.repository.mysql.EventMySqlRepository;
import com.mycompany.eventmanagementapp.view.screen.EventManagementViewScreen;
import com.mycompany.eventmanagementapp.dbconfigurations.DatabaseConfiguration;

public class EventControllerRaceConditionIT {

	private static DatabaseConfiguration databaseConfig;

	private static SessionFactory sessionFactory;

	private static StandardServiceRegistry registry;

	private AutoCloseable closeable;

	@Mock
	private EventManagementViewScreen eventView;

	private EventController eventController;

	private EventRepository eventRepository;

	private static final long DEFAULT_EVENT_ID = -1;

	private static final String EVENT_NAME = "Music Festival";

	private static final String EVENT_NAME_2 = "University Event";

	private static final LocalDate EVENT_DATE = LocalDate.now().plusDays(10);

	private static final String EVENT_LOCATION = "Florence";

	@BeforeClass
	public static void configureDB() {
	}

	// Tear down the database
	@AfterClass
	public static void shutdownServer() {
		StandardServiceRegistryBuilder.destroy(registry);
		if (sessionFactory != null) {
			sessionFactory.close();
		}
	}

	// Initialize Hibernate session factory and repository and controller before
	// each test
	@Before
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		databaseConfig = DBConfigSetup.getDatabaseConfig();
		databaseConfig.StartDatabaseConnection();
		registry = databaseConfig.getServiceRegistry();
		MetadataSources metadataSources = new MetadataSources(registry);
		sessionFactory = metadataSources.buildMetadata().buildSessionFactory();
		eventRepository = new EventMySqlRepository(sessionFactory);
		eventController = new EventController(eventView, eventRepository);
	}

	// Close mocks after each test
	@After
	public void releaseMocks() throws Exception {
		closeable.close();
	}

	// Test Add Event Concurrent
	@Test
	public void testAddEventConcurrent() {
		// Setup
		EventModel event = new EventModel(DEFAULT_EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);

		// Exercise
		List<Thread> threads = IntStream.range(0, 10).mapToObj(i -> new Thread(() -> {
			try {
				eventController.addEvent(event);
			} catch (Exception e) {
				e.printStackTrace();
			}
		})).peek(Thread::start).collect(Collectors.toList());
		await().atMost(20, TimeUnit.SECONDS).until(() -> threads.stream().noneMatch(Thread::isAlive));

		// Verify
		EventModel savedEvent = eventRepository.getAllEvents().get(0);
		assertThat(eventRepository.getAllEvents()).containsExactly(savedEvent);
	}

	// Test Update Event Concurrent
	@Test
	public void testUpdateEventConcurrent() {
		// Setup
		EventModel event = new EventModel(DEFAULT_EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		eventRepository.addEvent(event);
		EventModel savedEvent = eventRepository.getAllEvents().get(0);
		savedEvent.setEventName(EVENT_NAME_2);

		// Exercise
		List<Thread> threads = IntStream.range(0, 10).mapToObj(i -> new Thread(() -> {
			try {
				eventController.updateEvent(savedEvent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		})).peek(Thread::start).collect(Collectors.toList());
		await().atMost(20, TimeUnit.SECONDS).until(() -> threads.stream().noneMatch(Thread::isAlive));

		// Verify
		assertThat(eventRepository.getAllEvents()).containsExactly(savedEvent);
	}

	// Test Delete Event Concurrent
	@Test
	public void testDeleteEventConcurrent() {
		// Setup
		EventModel event = new EventModel(DEFAULT_EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		eventRepository.addEvent(event);
		EventModel savedEvent = eventRepository.getAllEvents().get(0);

		// Exercise
		List<Thread> threads = IntStream.range(0, 10).mapToObj(i -> new Thread(() -> {
			try {
				eventController.deleteEvent(savedEvent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		})).peek(Thread::start).collect(Collectors.toList());
		await().atMost(20, TimeUnit.SECONDS).until(() -> threads.stream().noneMatch(Thread::isAlive));

		// Verify
		assertThat(eventRepository.getAllEvents()).isEmpty();
	}
}
