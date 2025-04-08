/**
 * This class contains integration tests for the race conditions in the ParticipantController of the Event Management Application.
 * It uses JUnit, TestContainers, Mocking (Mockito), and AssertJ for testing concurrency and database interactions.
 *
 * The main purpose of these tests is to simulate concurrent operations on the Participant entity and ensure that the application
 * behaves correctly under race conditions. The tests specifically target:
 * - Adding a participant concurrently from multiple threads.
 * - Updating a participant concurrently and ensuring the changes are correctly persisted.
 * - Deleting a participant concurrently and ensuring that the deletion occurs without errors.
 *
 * Test Containers are used to spin up a MySQL container for isolated testing with Hibernate ORM for database interactions.
 * The tests simulate multiple concurrent actions (add, update, delete) and use Awaitility to ensure that all threads complete
 * before performing verification.
 * 
 * The class ensures that the application's ParticipantController handles concurrent database operations correctly without data 
 * inconsistencies or race conditions.
 */

package com.mycompany.eventmanagementapp.racecondition;

import org.junit.Test;
import java.util.List;
import org.junit.Before;
import org.mockito.Mock;
import java.time.LocalDate;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import java.util.stream.IntStream;
import java.util.stream.Collectors;
import org.hibernate.SessionFactory;
import java.util.concurrent.TimeUnit;
import org.hibernate.boot.MetadataSources;
import static org.awaitility.Awaitility.await;
import static org.assertj.core.api.Assertions.assertThat;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import com.mycompany.eventmanagementapp.model.*;
import com.mycompany.eventmanagementapp.controller.*;
import com.mycompany.eventmanagementapp.repository.*;
import com.mycompany.eventmanagementapp.view.screen.*;
import com.mycompany.eventmanagementapp.repository.mysql.*;
import com.mycompany.eventmanagementapp.dbconfigurations.DBConfigSetup;
import com.mycompany.eventmanagementapp.dbconfigurations.DatabaseConfiguration;

public class ParticipantControllerRaceConditionIT {

	private static DatabaseConfiguration databaseConfig;

	private static SessionFactory sessionFactory;

	private static StandardServiceRegistry registry;

	@Mock
	private ParticipantManagementViewScreen participantView;

	private ParticipantController participantController;

	private EventRepository eventRepository;

	private ParticipantRepository participantRepository;

	private static final long DEFAULT_EVENT_ID = -1;

	private static final String EVENT_NAME = "Music Festival";

	private static final LocalDate EVENT_DATE = LocalDate.now().plusDays(10);

	private static final String EVENT_LOCATION = "Florence";

	private static final String PARTICIPANT_NAME = "John";

	private static final String PARTICIPANT_NAME_2 = "Martin";

	private static final String PARTICIPANT_EMAIL = "John@gmail.com";

	// Setup Database Config for Eclipse OR Maven
	@BeforeClass
	public static void configureDB() {
//		databaseConfig = DBConfigSetup.getDatabaseConfig();
//		databaseConfig.StartDatabaseConnection();
	}

	// Tear down the database after tests
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
		databaseConfig = DBConfigSetup.getDatabaseConfig();
		databaseConfig.StartDatabaseConnection();
		registry = databaseConfig.getServiceRegistry();
		MetadataSources metadataSources = new MetadataSources(registry);
		sessionFactory = metadataSources.buildMetadata().buildSessionFactory();
		eventRepository = new EventMySqlRepository(sessionFactory);
		participantRepository = new ParticipantMySqlRepository(sessionFactory);
		participantController = new ParticipantController(participantView, participantRepository, eventRepository);
	}

	// Test Add Participant Concurrent
	@Test
	public void testAddParticipantConcurrent() {
		// Setup
		EventModel event = new EventModel(DEFAULT_EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		eventRepository.addEvent(event);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		participant.addEvent(event);

		// Exercise
		List<Thread> threads = IntStream.range(0, 10).mapToObj(i -> new Thread(() -> {
			try {
				participantController.addParticipant(participant, event);
			} catch (Exception e) {
				e.printStackTrace();
			}
		})).peek(Thread::start).collect(Collectors.toList());
		await().atMost(20, TimeUnit.SECONDS).until(() -> threads.stream().noneMatch(Thread::isAlive));

		// Verify
		ParticipantModel savedParticipant = participantRepository.getAllParticipants().get(0);
		assertThat(participantRepository.getAllParticipants()).containsExactly(savedParticipant);
	}

	// Test Update Participant Concurrent
	@Test
	public void testUpdateParticipantConcurrent() {
		// Setup
		EventModel event = new EventModel(DEFAULT_EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		eventRepository.addEvent(event);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		participant.addEvent(event);
		participantRepository.addParticipant(participant);
		eventRepository.updateEvent(event);
		ParticipantModel savedParticipant = participantRepository.getAllParticipants().get(0);
		savedParticipant.setParticipantName(PARTICIPANT_NAME_2);

		// Exercise
		List<Thread> threads = IntStream.range(0, 10).mapToObj(i -> new Thread(() -> {
			try {
				participantController.updateParticipant(savedParticipant);
			} catch (Exception e) {
				e.printStackTrace();
			}
		})).peek(Thread::start).collect(Collectors.toList());
		await().atMost(20, TimeUnit.SECONDS).until(() -> threads.stream().noneMatch(Thread::isAlive));

		// Verify
		assertThat(participantRepository.getAllParticipants()).containsExactly(savedParticipant);
	}

	// Test Delete Participant Concurrent
	@Test
	public void testDeleteParticipantConcurrent() {
		// Setup
		EventModel event = new EventModel(DEFAULT_EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		eventRepository.addEvent(event);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		participant.addEvent(event);
		participantRepository.addParticipant(participant);
		eventRepository.updateEvent(event);
		ParticipantModel savedParticipant = participantRepository.getAllParticipants().get(0);
		EventModel savedEvent = eventRepository.getAllEvents().get(0);

		// Exercise
		List<Thread> threads = IntStream.range(0, 10).mapToObj(i -> new Thread(() -> {
			try {
				participantController.deleteParticipant(savedParticipant, savedEvent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		})).peek(Thread::start).collect(Collectors.toList());
		await().atMost(20, TimeUnit.SECONDS).until(() -> threads.stream().noneMatch(Thread::isAlive));

		// Verify
		assertThat(participantRepository.getAllParticipants()).isEmpty();
	}
}
