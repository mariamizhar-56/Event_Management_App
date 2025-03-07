/**
 * Integration test class for testing the ParticipantController in the Event Management Application.
 * This class tests the controller's interaction with the participant model and view, ensuring that 
 * participants are properly added, updated, and deleted from the system.
 * 
 * The tests ensure that the controller handles various scenarios such as:
 * - Adding a participant to an event, checking if the event exists and the participant is correctly associated.
 * - Handling cases where a participant already exists and is either associated or not associated with the selected event.
 * - Updating participant details and verifying the update is reflected in the system.
 * - Deleting a participant from an event, with special handling for participants associated with one or multiple events.
 * 
 * The tests are run with a MySQL database managed by TestContainers, and Hibernate is used to interact with the database.
 * Mockito is used for mocking the view layer to ensure that the controller's actions are correctly communicated to the view.
 * 
 * Each test verifies the appropriate interaction between the controller, model, and view, ensuring data consistency 
 * and correct UI behavior for managing participants in the event management system.
 */

package com.mycompany.eventmanagementapp.controller;

import org.junit.Test;
import org.junit.After;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.ClassRule;
import java.time.LocalDate;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.hibernate.SessionFactory;
import org.mockito.MockitoAnnotations;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;
import org.hibernate.boot.MetadataSources;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.containers.MySQLContainer;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import com.mycompany.eventmanagementapp.model.EventModel;
import com.mycompany.eventmanagementapp.model.ParticipantModel;
import com.mycompany.eventmanagementapp.repository.EventRepository;
import com.mycompany.eventmanagementapp.view.ParticipantManagementView;
import com.mycompany.eventmanagementapp.repository.ParticipantRepository;
import com.mycompany.eventmanagementapp.repository.mysql.EventMySqlRepository;
import com.mycompany.eventmanagementapp.repository.mysql.ParticipantMySqlRepository;

public class ParticipantControllerIT {

	private EventRepository eventRepository;

	private ParticipantRepository participantRepository;

	@Mock
	private ParticipantManagementView participantView;

	private AutoCloseable closeable;

	private static SessionFactory sessionFactory;

	private static StandardServiceRegistry registry;

	private ParticipantController participantController;

	private static final long EVENT_ID = 1;

	private static final long EVENT_ID_2 = 1;

	private static final String EVENT_NAME = "Music Festival";

	private static final String EVENT_NAME_2 = "University Event";

	private static final LocalDate EVENT_DATE = LocalDate.now().plusDays(10);

	private static final LocalDate EVENT_DATE_2 = LocalDate.now().plusDays(30);

	private static final String EVENT_LOCATION = "Florence";

	private static final String EVENT_LOCATION_2 = "Milan";

	private static final String PARTICIPANT_NAME = "John";

	private static final String PARTICIPANT_NAME_2 = "Martin";

	private static final String PARTICIPANT_EMAIL = "John@gmail.com";

	// Using MySQLContainer from Test Containers for Integration Testing
	@SuppressWarnings("resource")
	@ClassRule
	public static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.28"))
			.withDatabaseName("test").withUsername("test").withPassword("test");

	// Setup Database using Test Containers OR local MySQL Instance
	@BeforeClass
	public static void configureDB() {
		// Check if running in Eclipse
		if (System.getProperty("surefire.test.class.path") == null) {
			// Using Test Containers (Eclipse environment)
			mysqlContainer.start();
			// Configure Hibernate to use Test Containers MySQL
			registry = new StandardServiceRegistryBuilder().configure("hibernate-IT.cfg.xml")
					.applySetting("hibernate.connection.url", mysqlContainer.getJdbcUrl())
					.applySetting("hibernate.connection.username", mysqlContainer.getUsername())
					.applySetting("hibernate.connection.password", mysqlContainer.getPassword()).build();
		} else {
			// Using Maven Docker (Maven environment)
			registry = new StandardServiceRegistryBuilder().configure("hibernate-IT.cfg.xml")
					.applySetting("hibernate.connection.url", "jdbc:mysql://localhost:3307/event_management_app")
					.applySetting("hibernate.connection.password", "test").build();
		}
	}

	// Tear down the Database and stop the container if necessary
	@AfterClass
	public static void shutdownDB() {
		StandardServiceRegistryBuilder.destroy(registry);
		if (sessionFactory != null) {
			sessionFactory.close();
		}
		if (System.getProperty("surefire.test.class.path") == null) {
			// Test Containers (Eclipse environment)
			mysqlContainer.stop();
		}
	}

	// Initialize mocks and set up Hibernate session factory and repository
	@Before
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		MetadataSources metadataSources = new MetadataSources(registry);
		sessionFactory = metadataSources.buildMetadata().buildSessionFactory();
		eventRepository = new EventMySqlRepository(sessionFactory);
		participantRepository = new ParticipantMySqlRepository(sessionFactory);
		participantController = new ParticipantController(participantView, participantRepository, eventRepository);
	}

	// Close mocks after each test
	@After
	public void releaseMocks() throws Exception {
		closeable.close();
	}

	// Test Show All Events on ParticipantView
	@Test
	public void testShowAllEvents() {
		// Setup
		EventModel event = new EventModel(EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		eventRepository.addEvent(event);

		// Exercise
		participantController.getAllEvents();

		// Verify
		verify(participantView).showAllEvents(asList(event));
	}

	// Test Show All Participants on ParticipantView
	@Test
	public void testShowAllParticipants() {
		// Setup
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		participantRepository.addParticipant(participant);

		// Exercise
		participantController.getAllParticipants();

		// Verify
		verify(participantView).showAllParticipants(asList(participant));
	}

	// Test Add Participant when related event not exist
	@Test
	public void testAddParticipantWhenEventNotExist() {
		// Setup
		EventModel selectedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_NAME, PARTICIPANT_EMAIL);

		// Exercise
		participantController.addParticipant(participant, selectedEvent);

		// Verify
		verify(participantView).showError("Event doesn't exist with id " + selectedEvent.getEventId(), participant);
	}

	// Test Add Participant when it is already associated with selected event
	@Test
	public void testAddParticipantWhenEventAlreadyAssociated() {
		// Setup
		EventModel selectedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		eventRepository.addEvent(selectedEvent);
		participant.addEvent(selectedEvent);
		participantRepository.addParticipant(participant);
		eventRepository.updateEvent(selectedEvent);

		// Exercise
		participantController.addParticipant(participant, selectedEvent);

		// Verify
		verify(participantView).showError("Participant already existed with email " + participant.getParticipantEmail()
				+ " and associated with event Id " + selectedEvent.getEventId(), participant);
	}

	// Test Add Participant when it is already existed but not associated with
	// selected event
	@Test
	public void testAddParticipantWhenItAlreadyExistedButNotAssociatedWithSelectedEvent() {
		// Setup
		EventModel existingEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		EventModel selectedEvent = new EventModel(EVENT_ID_2, EVENT_NAME_2, EVENT_DATE_2, EVENT_LOCATION_2);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		eventRepository.addEvent(existingEvent);
		eventRepository.addEvent(selectedEvent);
		participant.addEvent(existingEvent);
		participantRepository.addParticipant(participant);
		eventRepository.updateEvent(existingEvent);

		// Exercise
		participantController.addParticipant(participant, selectedEvent);

		// Verify
		verify(participantView).participantUpdated(participant);
	}

	// Test Add Participant when it is not existed
	@Test
	public void testAddParticipantWhenItDoesNotExist() {
		// Setup
		EventModel selectedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		eventRepository.addEvent(selectedEvent);

		// Exercise
		participantController.addParticipant(participant, selectedEvent);

		// Verify
		verify(participantView).participantAdded(participant);
	}

	// Test Update Participant
	@Test
	public void testUpdateParticipant() {
		// Setup
		EventModel selectedEvent = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		eventRepository.addEvent(selectedEvent);
		participant.addEvent(selectedEvent);
		participantRepository.addParticipant(participant);
		eventRepository.updateEvent(selectedEvent);
		ParticipantModel updatedParticipant = participantRepository
				.getParticipantByEmail(participant.getParticipantEmail());
		updatedParticipant.setParticipantName(PARTICIPANT_NAME_2);

		// Exercise
		participantController.updateParticipant(updatedParticipant);

		// Verify
		verify(participantView).participantUpdated(updatedParticipant);
	}

	// Test Delete Participant When it is associated with more than 1 events
	@Test
	public void testDeleteParticipantWhenItIsAssociatedWithMoreThanOneEvent() {
		// Setup
		EventModel event1 = new EventModel(EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		EventModel event2 = new EventModel(EVENT_ID_2, EVENT_NAME_2, EVENT_DATE_2, EVENT_LOCATION_2);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		eventRepository.addEvent(event1);
		eventRepository.addEvent(event2);
		participant.addEvent(event1);
		participant.addEvent(event2);
		participantRepository.addParticipant(participant);
		eventRepository.updateEvent(event1);
		eventRepository.updateEvent(event2);

		// Exercise
		participantController.deleteParticipant(participant, event2);

		// Verify
		verify(participantView).participantUpdated(participant);
	}

	// Test Delete Participant When it is associated with only 1 event
	@Test
	public void testDeleteParticipantWhenItIsAssociatedWithOnlyOneEvent() {
		// Setup
		EventModel event = new EventModel(EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		eventRepository.addEvent(event);
		event = eventRepository.getAllEvents().get(0);
		participant.addEvent(event);
		participantRepository.addParticipant(participant);
		eventRepository.updateEvent(event);
		event = eventRepository.getEventById(event.getEventId());

		// Exercise
		participantController.deleteParticipant(participant, event);

		// Verify
		verify(participantView).participantDeleted(participant);
	}
}
