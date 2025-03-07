/**
 * This class contains integration tests for the Participant Management functionality in the Event Management Application.
 * The tests validate the correct functionality of adding, updating, and deleting participants through the UI, ensuring that 
 * the operations are performed correctly in conjunction with the backend database and event management logic.
 *
 * The tests are focused on:
 * - Adding a new participant to an event.
 * - Adding an existing participant to a new event.
 * - Updating participant details, such as name.
 * - Deleting a participant from one or more events, ensuring the logic handles both single-event and multi-event participants correctly.
 *
 * The tests use TestContainers to spin up a MySQL container, Hibernate ORM for database interactions, and AssertJ Swing for 
 * interacting with the Swing-based UI. The tests assert that after performing actions in the UI, the correct changes are made 
 * to the participant data in the repository and the UI reflects these changes.
 *
 * The primary purpose of these tests is to verify that the participant management functionality works as expected, 
 * both in terms of UI interactions and backend operations, ensuring data consistency and correct UI behavior.
 */

package com.mycompany.eventmanagementapp.mvc;

import org.junit.Test;
import java.time.LocalDate;
import org.junit.ClassRule;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.hibernate.SessionFactory;
import java.util.concurrent.TimeUnit;
import org.hibernate.boot.MetadataSources;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import static org.awaitility.Awaitility.await;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.containers.MySQLContainer;
import org.assertj.swing.core.matcher.JButtonMatcher;
import static org.assertj.core.api.Assertions.assertThat;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import com.mycompany.eventmanagementapp.model.*;
import com.mycompany.eventmanagementapp.controller.*;
import com.mycompany.eventmanagementapp.repository.mysql.*;
import com.mycompany.eventmanagementapp.view.screen.ParticipantManagementViewScreen;

public class ParticipantModelViewControllerIT extends AssertJSwingJUnitTestCase {

	// Using MySQLContainer from Test Containers for Integration Testing
	@SuppressWarnings("resource")
	@ClassRule
	public static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.28"))
			.withDatabaseName("test").withUsername("test").withPassword("test");

	private static SessionFactory sessionFactory;

	private static StandardServiceRegistry registry;

	private FrameFixture window;

	private ParticipantMySqlRepository participantRepository;

	private EventMySqlRepository eventRepository;

	private ParticipantController participantController;

	private static final long EVENT_ID = 1;
	
	private static final String EVENT_NAME_1 = "Music Festival";
	
	private static final LocalDate EVENT_DATE_1 = LocalDate.now().plusDays(10);
	
	private static final String EVENT_LOCATION_1 = "Florence";
	
	private static final String EVENT_NAME_2 = "University Event";
	
	private static final LocalDate EVENT_DATE_2 = LocalDate.now().plusDays(20);
	
	private static final String EVENT_LOCATION_2 = "Milan";
	
	private static final String PARTICIPANT_NAME = "John";
	
	private static final String PARTICIPANT_EMAIL = "John@gmail.com";
	
	private static final String PARTICIPANT_NAME_2 = "Martin";

	private static final String BTN_ADD_PARTICIPANT = "Add Participant";
	
	private static final String BTN_UPDATE_PARTICIPANT = "Update Participant";
	
	private static final String BTN_DELETE_PARTICIPANT = "Delete Participant";

	private static final String TXT_EVENT_ID = "txtEventId";
	
	private static final String TXT_PARTICIPANT_NAME = "txtParticipantName";
	
	private static final String TXT_PARTICIPANT_EMAIL = "txtParticipantEmail";

	private static final String LIST_PARTICIPANT = "participantList";
	
	private static final String LIST_EVENT = "eventListForParticipant";

	// Setup the MySQL container and Hibernate session before running the tests
	@BeforeClass
	public static void setupContainer() {
		mysqlContainer.start();
		registry = new StandardServiceRegistryBuilder().configure("hibernate-IT.cfg.xml")
				.applySetting("hibernate.connection.url", mysqlContainer.getJdbcUrl())
				.applySetting("hibernate.connection.username", mysqlContainer.getUsername())
				.applySetting("hibernate.connection.password", mysqlContainer.getPassword()).build();
	}

	// Tear down the session and stop the MySQL container after the tests
	@AfterClass
	public static void shutdownServer() {
		StandardServiceRegistryBuilder.destroy(registry);
		if (sessionFactory != null) {
			sessionFactory.close();
		}
		mysqlContainer.stop();
	}

	// Setup UI and Repositories before each test
	@Override
	protected void onSetUp() throws Exception {
		MetadataSources metadataSources = new MetadataSources(registry);
		sessionFactory = metadataSources.buildMetadata().buildSessionFactory();
		participantRepository = new ParticipantMySqlRepository(sessionFactory);
		eventRepository = new EventMySqlRepository(sessionFactory);

		window = new FrameFixture(robot(), GuiActionRunner.execute(() -> {
			ParticipantManagementViewScreen participantView = new ParticipantManagementViewScreen();
			participantController = new ParticipantController(participantView, participantRepository, eventRepository);
			participantView.setParticipantController(participantController);
			return participantView;
		}));
		window.show(); // Shows the UI frame for testing
	}

	// Test Add Participant From UI
	@Test
	public void testAddNewParticipantToEvent() {
		// Setup
		EventModel event = new EventModel(EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		eventRepository.addEvent(event);
		GuiActionRunner.execute(() -> participantController.getAllEvents());
		setFieldValues(String.valueOf(EVENT_ID), PARTICIPANT_NAME, PARTICIPANT_EMAIL);

		// Exercise
		window.button(JButtonMatcher.withText(BTN_ADD_PARTICIPANT)).click();

		// Verify
		await().atMost(5, TimeUnit.SECONDS)
				.untilAsserted(() -> assertThat(participantRepository.getAllParticipants()).isNotEmpty());
		ParticipantModel addedParticipant = participantRepository.getParticipantByEmail(PARTICIPANT_EMAIL);
		assertThat(participantRepository.getParticipantById(addedParticipant.getParticipantId()))
				.isEqualTo(addedParticipant);
		assertThat(addedParticipant.getEvents()).contains(event);
	}

	// Test Add Participant From UI
	@Test
	public void testAddExistingParticipantToEvent() {
		// Setup
		EventModel existingEvent = new EventModel(EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		EventModel selectedEvent = new EventModel(EVENT_NAME_2, EVENT_DATE_2, EVENT_LOCATION_2);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		eventRepository.addEvent(existingEvent);
		eventRepository.addEvent(selectedEvent);
		participant.addEvent(existingEvent);
		participantRepository.addParticipant(participant);
		eventRepository.updateEvent(existingEvent);
		GuiActionRunner.execute(() -> participantController.getAllEvents());
		setFieldValues(String.valueOf(selectedEvent.getEventId()), PARTICIPANT_NAME, PARTICIPANT_EMAIL);

		// Exercise
		window.button(JButtonMatcher.withText(BTN_ADD_PARTICIPANT)).click();

		// Verify
		await().atMost(5, TimeUnit.SECONDS)
				.untilAsserted(() -> assertThat(participantRepository.getAllParticipants()).isNotEmpty());
		ParticipantModel addedParticipant = participantRepository.getParticipantByEmail(PARTICIPANT_EMAIL);
		assertThat(participantRepository.getParticipantById(addedParticipant.getParticipantId()))
				.isEqualTo(addedParticipant);
		assertThat(addedParticipant.getEvents()).contains(existingEvent, selectedEvent);
	}

	// Test Update Participant From UI
	@Test
	public void testUpdateParticipant() {
		// Setup
		EventModel event = new EventModel(EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		eventRepository.addEvent(event);
		participant.addEvent(event);
		participantRepository.addParticipant(participant);
		eventRepository.updateEvent(event);
		GuiActionRunner.execute(() -> participantController.getAllParticipants());

		// Exercise
		window.list(LIST_PARTICIPANT).selectItem(0);
		ParticipantModel updatedParticipant = new ParticipantModel(participant.getParticipantId(), PARTICIPANT_NAME_2,
				PARTICIPANT_EMAIL);
		window.textBox(TXT_PARTICIPANT_NAME).setText(updatedParticipant.getParticipantName());
		window.button(JButtonMatcher.withText(BTN_UPDATE_PARTICIPANT)).click();

		// Verify
		await().atMost(5, TimeUnit.SECONDS).untilAsserted(
				() -> assertThat(participantRepository.getParticipantById(participant.getParticipantId()))
						.isEqualTo(updatedParticipant));
	}

	// Test Delete Participant From UI
	@Test
	public void testDeleteParticipantWithMoreThanOneEvent() {
		// Setup
		EventModel event1 = new EventModel(EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		EventModel event2 = new EventModel(EVENT_NAME_2, EVENT_DATE_2, EVENT_LOCATION_2);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		eventRepository.addEvent(event1);
		eventRepository.addEvent(event2);
		participant.addEvent(event1);
		participant.addEvent(event2);
		participantRepository.addParticipant(participant);
		eventRepository.updateEvent(event1);
		eventRepository.updateEvent(event2);
		GuiActionRunner.execute(() -> {
			participantController.getAllParticipants();
			participantController.getAllEvents();
		});

		// Exercise
		window.list(LIST_PARTICIPANT).selectItem(0);
		window.list(LIST_EVENT).selectItem(0); // Selecting event2 for Participate Deletion, by default latest added event will come first in List View.
		window.button(JButtonMatcher.withText(BTN_DELETE_PARTICIPANT)).click();

		// Verify
		await().atMost(10, TimeUnit.SECONDS).untilAsserted(
				() -> assertThat(participantRepository.getParticipantById(participant.getParticipantId()).getEvents())
						.containsOnly(event1));
	}

	// Test Delete Participant From UI
	@Test
	public void testDeleteParticipantWithOnlyOneEvent() {
		// Setup
		EventModel event = new EventModel(EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		eventRepository.addEvent(event);
		event = eventRepository.getAllEvents().get(0);
		participant.addEvent(event);
		participantRepository.addParticipant(participant);
		eventRepository.updateEvent(event);
		GuiActionRunner.execute(() -> {
			participantController.getAllParticipants();
			participantController.getAllEvents();
		});

		// Exercise
		window.list(LIST_PARTICIPANT).selectItem(0);
		window.list(LIST_EVENT).selectItem(0);
		window.button(JButtonMatcher.withText(BTN_DELETE_PARTICIPANT)).click();

		// Verify
		await().atMost(10, TimeUnit.SECONDS).untilAsserted(
				() -> assertThat(participantRepository.getParticipantById(participant.getParticipantId())).isNull());
	}

	// Helper Method for UI Input
	private void setFieldValues(String eventId, String participantName, String participantEmail) {
		window.textBox(TXT_EVENT_ID).enterText(eventId);
		window.textBox(TXT_PARTICIPANT_NAME).enterText(participantName);
		window.textBox(TXT_PARTICIPANT_EMAIL).enterText(participantEmail);
	}
}
