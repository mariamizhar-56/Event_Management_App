package com.mycompany.eventmanagementapp.racecondition;

import org.junit.Test;
import java.util.List;
import org.junit.Before;
import org.mockito.Mock;
import org.junit.ClassRule;
import java.time.LocalDate;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import java.util.stream.IntStream;
import java.util.stream.Collectors;
import org.hibernate.SessionFactory;
import java.util.concurrent.TimeUnit;
import org.hibernate.boot.MetadataSources;
import static org.awaitility.Awaitility.await;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.containers.MySQLContainer;
import static org.assertj.core.api.Assertions.assertThat;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import com.mycompany.eventmanagementapp.model.EventModel;
import com.mycompany.eventmanagementapp.controller.EventController;
import com.mycompany.eventmanagementapp.repository.EventRepository;
import com.mycompany.eventmanagementapp.repository.mysql.EventMySqlRepository;
import com.mycompany.eventmanagementapp.view.screen.EventManagementViewScreen;

public class EventControllerRaceConditionIT {

	// Using MySQLContainer from Test Containers for Integration Testing
	@SuppressWarnings("resource")
	@ClassRule
	public static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.28"))
			.withDatabaseName("test").withUsername("test").withPassword("test");

	private static SessionFactory sessionFactory;

	private static StandardServiceRegistry registry;

	@Mock
	private EventManagementViewScreen eventView;

	private EventController eventController;

	private EventRepository eventRepository;

	private static final long DEFAULT_EVENT_ID = -1;

	private static final String EVENT_NAME = "Music Festival";

	private static final String EVENT_NAME_2 = "University Event";

	private static final LocalDate EVENT_DATE = LocalDate.now().plusDays(10);

	private static final String EVENT_LOCATION = "Florence";

	// Set up the MySQL container and Hibernate configuration
	@BeforeClass
	public static void setupContainer() {
		mysqlContainer.start();
		registry = new StandardServiceRegistryBuilder().configure("hibernate-IT.cfg.xml")
				.applySetting("hibernate.connection.url", mysqlContainer.getJdbcUrl())
				.applySetting("hibernate.connection.username", mysqlContainer.getUsername())
				.applySetting("hibernate.connection.password", mysqlContainer.getPassword()).build();
	}

	// Tear down the database and stop the container after tests
	@AfterClass
	public static void shutdownServer() {
		StandardServiceRegistryBuilder.destroy(registry);
		if (sessionFactory != null) {
			sessionFactory.close();
		}
		mysqlContainer.stop();
	}

	// Initialize Hibernate session factory and repository and controller before
	// each test
	@Before
	public void setup() {
		MetadataSources metadataSources = new MetadataSources(registry);
		sessionFactory = metadataSources.buildMetadata().buildSessionFactory();
		eventRepository = new EventMySqlRepository(sessionFactory);
		eventController = new EventController(eventView, eventRepository);
	}

	// Test Add Event Concurrent
	@Test
	public void testAddEventConcurrent() {
		//Setup
		EventModel event = new EventModel(DEFAULT_EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);

		//Exercise
		List<Thread> threads = IntStream.range(0, 10).mapToObj(i -> new Thread(() -> {
			try {
				eventController.addEvent(event);
			} catch (Exception e) {
				e.printStackTrace();
			}
		})).peek(t -> t.start()).collect(Collectors.toList());
		await().atMost(20, TimeUnit.SECONDS).until(() -> threads.stream().noneMatch(t -> t.isAlive()));
		
		//Verify
		EventModel savedEvent = eventRepository.getAllEvents().get(0);
		assertThat(eventRepository.getAllEvents()).containsExactly(savedEvent);
	}
	
	// Test Update Event Concurrent
	@Test
	public void testUpdateEventConcurrent() {
		//Setup
		EventModel event = new EventModel(DEFAULT_EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		eventRepository.addEvent(event);
		EventModel savedEvent = eventRepository.getAllEvents().get(0);
		savedEvent.setEventName(EVENT_NAME_2);

		//Exercise
		List<Thread> threads = IntStream.range(0, 10).mapToObj(i -> new Thread(() -> {
			try {
				eventController.updateEvent(savedEvent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		})).peek(t -> t.start()).collect(Collectors.toList());
		await().atMost(20, TimeUnit.SECONDS).until(() -> threads.stream().noneMatch(t -> t.isAlive()));
		
		//Verify
		assertThat(eventRepository.getAllEvents()).containsExactly(savedEvent);
	}
	
	// Test Delete Event Concurrent
	@Test
	public void testDeleteEventConcurrent() {
		//Setup
		EventModel event = new EventModel(DEFAULT_EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		eventRepository.addEvent(event);
		EventModel savedEvent = eventRepository.getAllEvents().get(0);

		//Exercise
		List<Thread> threads = IntStream.range(0, 10).mapToObj(i -> new Thread(() -> {
			try {
				eventController.deleteEvent(savedEvent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		})).peek(t -> t.start()).collect(Collectors.toList());
		await().atMost(20, TimeUnit.SECONDS).until(() -> threads.stream().noneMatch(t -> t.isAlive()));
		
		//Verify
		assertThat(eventRepository.getAllEvents()).isEmpty();
	}
}
