/**
 * This class contains the integration tests for the Participant Management view of the Event Management Application.
 * It uses AssertJ-Swing and JUnit for testing the GUI interactions and MySQLContainer from TestContainers for database management.
 * The tests cover various aspects of the participant management functionality, such as adding, updating, deleting participants,
 * and validating the display of events and participants in the GUI.
 * 
 * The key operations tested include:
 * - Adding a new participant and verifying successful addition.
 * - Handling errors when adding or updating participants with invalid data.
 * - Updating participant details and verifying the changes in the UI.
 * - Deleting participants and ensuring they are removed from the list.
 * 
 * The class also sets up and tears down a MySQL database using TestContainers before and after the tests, ensuring that each 
 * test runs in an isolated environment.
 */

package com.mycompany.eventmanagementapp.view.screen;

import org.junit.Test;
import java.time.LocalDate;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.hibernate.SessionFactory;
import java.util.concurrent.TimeUnit;
import org.hibernate.boot.MetadataSources;
import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import static org.awaitility.Awaitility.await;
import org.assertj.swing.core.matcher.JButtonMatcher;
import static org.assertj.core.api.Assertions.assertThat;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import com.mycompany.eventmanagementapp.model.*;
import com.mycompany.eventmanagementapp.controller.*;
import com.mycompany.eventmanagementapp.repository.mysql.*;
import com.mycompany.eventmanagementapp.dbconfigurations.DBConfigSetup;
import com.mycompany.eventmanagementapp.dbconfigurations.DatabaseConfiguration;

public class ParticipantManagementViewScreenIT extends AssertJSwingJUnitTestCase {

	private static DatabaseConfiguration databaseConfig;

	private static SessionFactory sessionFactory;

	private static StandardServiceRegistry registry;

	private FrameFixture window;

	private ParticipantManagementViewScreen participantView;

	private ParticipantController participantController;

	private ParticipantMySqlRepository participantRepository;

	private EventMySqlRepository eventRepository;

	private static final long EVENT_DEFAULT_ID = -1;

	private static final long EVENT_ID = 1;

	private static final String EVENT_NAME_1 = "Music Festival";

	private static final LocalDate EVENT_DATE_1 = LocalDate.now().plusDays(10);

	private static final String EVENT_LOCATION_1 = "Florence";

	private static final String EVENT_NAME_2 = "University Event";

	private static final LocalDate EVENT_DATE_2 = LocalDate.now().plusDays(20);

	private static final String EVENT_LOCATION_2 = "Milan";

	private static final long PARTICIPANT_ID = 1;

	private static final String PARTICIPANT_NAME = "John";

	private static final String PARTICIPANT_EMAIL = "John@gmail.com";

	private static final String PARTICIPANT_NAME_2 = "Martin";

	private static final String PARTICIPANT_EMAIL_2 = "martin@gmail.com";

	private static final String BTN_ADD_PARTICIPANT = "Add Participant";

	private static final String BTN_UPDATE_PARTICIPANT = "Update Participant";

	private static final String BTN_DELETE_PARTICIPANT = "Delete Participant";

	private static final String TXT_EVENT_ID = "txtEventId";

	private static final String TXT_PARTICIPANT_NAME = "txtParticipantName";

	private static final String TXT_PARTICIPANT_EMAIL = "txtParticipantEmail";

	private static final String TXT_PARTICIPANT_ERROR = "lblError";

	private static final String LIST_PARTICIPANT = "participantList";

	private static final String LIST_EVENT = "eventListForParticipant";

	// Setup Database Config for Eclipse OR Maven
	@BeforeClass
	public static void configureDB() {
//		databaseConfig = DBConfigSetup.getDatabaseConfig();
//		databaseConfig.StartDatabaseConnection();
	}

	// Tear down the session and stop the MySQL container after tests
	@AfterClass
	public static void shutdownServer() {
		StandardServiceRegistryBuilder.destroy(registry);
		if (sessionFactory != null) {
			sessionFactory.close();
		}
	}
	
	@Before
	public void setup() {
		databaseConfig = DBConfigSetup.getDatabaseConfig();
		databaseConfig.StartDatabaseConnection();

	}

	// Set up the UI and Repositories before each test
	@Override
	protected void onSetUp() throws Exception {
//		databaseConfig = DBConfigSetup.getDatabaseConfig();
//		databaseConfig.StartDatabaseConnection();
		registry = databaseConfig.getServiceRegistry();
		MetadataSources metadataSources = new MetadataSources(registry);
		sessionFactory = metadataSources.buildMetadata().buildSessionFactory();
		participantRepository = new ParticipantMySqlRepository(sessionFactory);
		eventRepository = new EventMySqlRepository(sessionFactory);

		GuiActionRunner.execute(() -> {
			participantView = new ParticipantManagementViewScreen();
			participantController = new ParticipantController(participantView, participantRepository, eventRepository);
			participantView.setParticipantController(participantController);
			return participantView;
		});
		window = new FrameFixture(robot(), participantView);
		window.show();
	}

	// Test All Events
	@Test
	@GUITest
	public void testAllEvents() {
		// Setup
		EventModel event1 = new EventModel(EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		EventModel event2 = new EventModel(EVENT_NAME_2, EVENT_DATE_2, EVENT_LOCATION_2);
		eventRepository.addEvent(event1);
		eventRepository.addEvent(event2);

		// Exercise
		GuiActionRunner.execute(() -> participantController.getAllEvents());

		// Verify
		assertThat(window.list(LIST_EVENT).contents()).containsExactlyInAnyOrder(event1.toString(), event2.toString());
	}

	// Test All Participants
	@Test
	@GUITest
	public void testAllParticipants() {
		// Setup
		ParticipantModel participant1 = new ParticipantModel(PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		ParticipantModel participant2 = new ParticipantModel(PARTICIPANT_NAME_2, PARTICIPANT_EMAIL_2);
		participantRepository.addParticipant(participant1);
		participantRepository.addParticipant(participant2);

		// Exercise
		GuiActionRunner.execute(() -> participantController.getAllParticipants());

		// Verify
		assertThat(window.list(LIST_PARTICIPANT).contents()).containsExactlyInAnyOrder(participant1.toString(),
				participant2.toString());
	}

	// Test Add Participant Success
	@Test
	@GUITest
	public void testAddParticipantSuccess() {
		// Setup
		EventModel event = new EventModel(EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		eventRepository.addEvent(event);
		GuiActionRunner.execute(() -> participantController.getAllEvents());
		setFieldValues(event.getEventId().toString(), PARTICIPANT_NAME, PARTICIPANT_EMAIL);

		// Exercise
		window.button(JButtonMatcher.withText(BTN_ADD_PARTICIPANT)).click();

		// Verify
		await().atMost(5, TimeUnit.SECONDS)
				.untilAsserted(() -> assertThat(participantRepository.getAllParticipants()).isNotEmpty());
		ParticipantModel addedParticipant = participantRepository.getAllParticipants().get(0);
		assertThat(window.list(LIST_PARTICIPANT).contents()).containsExactly(addedParticipant.toString());
	}

	// Test Add Participant Failure
	@Test
	@GUITest
	public void testAddParticipantFailure() {
		// Setup
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		setFieldValues(String.valueOf(EVENT_ID), PARTICIPANT_NAME, PARTICIPANT_EMAIL);

		// Exercise
		window.button(JButtonMatcher.withText(BTN_ADD_PARTICIPANT)).click();

		// Verify
		await().atMost(5, TimeUnit.SECONDS)
				.untilAsserted(() -> assertThat(window.textBox(TXT_PARTICIPANT_ERROR).text().trim()).isNotBlank());
		assertThat(window.list(LIST_PARTICIPANT).contents()).isEmpty();
		window.textBox(TXT_PARTICIPANT_ERROR)
				.requireText("Event doesn't exist with id " + EVENT_ID + ": " + participant.toString());
	}

	// Test Update Participant Success
	@Test
	@GUITest
	public void testUpdateParticipantSuccess() {
		// Setup
		EventModel event = new EventModel(EVENT_DEFAULT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		eventRepository.addEvent(event);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		GuiActionRunner.execute(() -> {
			participantController.addParticipant(participant, event);
		});

		// Exercise
		window.list(LIST_PARTICIPANT).selectItem(0);
		ParticipantModel updatedParticipant = participantRepository.getParticipantById(participant.getParticipantId());
		updatedParticipant.setParticipantName(PARTICIPANT_NAME_2);
		window.textBox(TXT_PARTICIPANT_NAME).setText(updatedParticipant.getParticipantName());
		window.button(JButtonMatcher.withText(BTN_UPDATE_PARTICIPANT)).click();

		// verify
		await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> assertThat(window.list(LIST_PARTICIPANT).contents())
				.containsExactly(updatedParticipant.toString()));
	}

	// Test Update Participant Failure
	@Test
	@GUITest
	public void testUpdateParticipantFailure() {
		// Setup
		EventModel event = new EventModel(EVENT_DEFAULT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		eventRepository.addEvent(event);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		GuiActionRunner.execute(() -> {
			participantController.addParticipant(participant, event);
		});

		// Exercise
		ParticipantModel oldParticipant = participantRepository.getParticipantById(participant.getParticipantId());
		window.list(LIST_PARTICIPANT).selectItem(0);
		participant.setParticipantEmail(PARTICIPANT_EMAIL_2);
		window.textBox(TXT_PARTICIPANT_EMAIL).setText(participant.getParticipantEmail());
		window.button(JButtonMatcher.withText(BTN_UPDATE_PARTICIPANT)).click();

		// verify
		await().atMost(10, TimeUnit.SECONDS)
				.untilAsserted(() -> assertThat(window.textBox(TXT_PARTICIPANT_ERROR).text().trim()).isNotBlank());
		assertThat(window.list(LIST_PARTICIPANT).contents()).containsExactly(oldParticipant.toString());
		window.textBox(TXT_PARTICIPANT_ERROR).requireText("Participant doesn't exist with email "
				+ participant.getParticipantEmail() + ": " + participant.toString());
	}

	// Test Delete Participant Success
	@Test
	@GUITest
	public void testDeleteParticipantSuccess() {
		// Setup
		EventModel event = new EventModel(EVENT_DEFAULT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		eventRepository.addEvent(event);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		GuiActionRunner.execute(() -> {
			participantController.addParticipant(participant, event);
		});

		// Exercise
		window.list(LIST_PARTICIPANT).selectItem(0);
		window.list(LIST_EVENT).selectItem(0);
		window.button(JButtonMatcher.withText(BTN_DELETE_PARTICIPANT)).click();

		// verify
		await().atMost(15, TimeUnit.SECONDS)
				.untilAsserted(() -> assertThat(window.list(LIST_PARTICIPANT).contents()).isEmpty());
	}

	// Test Delete Participant Failure
	@Test
	@GUITest
	public void testDeleteParticipantFailure() {
		// Setup
		EventModel event = new EventModel(EVENT_DEFAULT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		eventRepository.addEvent(event);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		participant.addEvent(event);
		GuiActionRunner.execute(() -> participantView.getParticipantListModel().addElement(participant));

		// Exercise
		window.list(LIST_PARTICIPANT).selectItem(0);
		window.list(LIST_EVENT).selectItem(0);
		window.button(JButtonMatcher.withText(BTN_DELETE_PARTICIPANT)).click();

		// Verify
		await().atMost(10, TimeUnit.SECONDS)
				.untilAsserted(() -> assertThat(window.textBox(TXT_PARTICIPANT_ERROR).text().trim()).isNotBlank());
		assertThat(window.list(LIST_PARTICIPANT).contents()).isEmpty();
		window.textBox(TXT_PARTICIPANT_ERROR).requireText("Participant doesn't exist with email "
				+ participant.getParticipantEmail() + ": " + participant.toString());
	}

	// Helper Method
	private void setFieldValues(String eventId, String participantName, String participantEmail) {
		window.textBox(TXT_EVENT_ID).enterText(eventId);
		window.textBox(TXT_PARTICIPANT_NAME).enterText(participantName);
		window.textBox(TXT_PARTICIPANT_EMAIL).enterText(participantEmail);
	}
}
