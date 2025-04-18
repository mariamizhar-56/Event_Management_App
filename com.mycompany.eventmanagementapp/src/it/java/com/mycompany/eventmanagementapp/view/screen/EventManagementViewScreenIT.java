/**
 * This class contains the integration tests for the Event Management view of the Event Management Application.
 * It uses AssertJ-Swing, JUnit, and TestContainers for testing the GUI and database interactions.
 * The tests cover various aspects of the event management functionality, such as adding, updating, deleting events,
 * and validating the display of events in the GUI.
 * 
 * The key operations tested include:
 * - Adding a new event and verifying successful addition.
 * - Handling errors when adding or updating events with invalid data (e.g., past event date).
 * - Updating event details and verifying the changes in the UI.
 * - Deleting events and ensuring they are removed from the list.
 * 
 * The class also sets up and tears down a MySQL database using TestContainers before and after the tests, ensuring that each 
 * test runs in an isolated environment.
 */

package com.mycompany.eventmanagementapp.view.screen;

import org.junit.Test;
import java.time.LocalDate;
import org.junit.AfterClass;
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

import com.mycompany.eventmanagementapp.model.EventModel;
import com.mycompany.eventmanagementapp.controller.EventController;
import com.mycompany.eventmanagementapp.dbconfigurations.DBConfigSetup;
import com.mycompany.eventmanagementapp.repository.mysql.EventMySqlRepository;
import com.mycompany.eventmanagementapp.dbconfigurations.DatabaseConfiguration;

public class EventManagementViewScreenIT extends AssertJSwingJUnitTestCase {

	private static DatabaseConfiguration databaseConfig;

	private static SessionFactory sessionFactory;

	private static StandardServiceRegistry registry;

	private FrameFixture window;

	private EventManagementViewScreen eventView;

	private EventController eventController;

	private EventMySqlRepository eventRepository;

	private static final long EVENT_ID = 1;

	private static final long EVENT_DEFAULT_ID = -1;

	private static final String EVENT_NAME_1 = "Music Festival";

	private static final LocalDate EVENT_DATE_1 = LocalDate.now().plusDays(10);

	private static final String EVENT_LOCATION_1 = "Florence";

	private static final String EVENT_NAME_2 = "University Event";

	private static final LocalDate EVENT_DATE_2 = LocalDate.now().plusDays(20);

	private static final String EVENT_LOCATION_2 = "Milan";

	private static final LocalDate EVENT_DATE_INVALID = LocalDate.now().minusDays(10);

	private static final String BTN_ADD_EVENT = "Add Event";

	private static final String BTN_UPDATE_EVENT = "Update Event";

	private static final String BTN_DELETE_EVENT = "Delete Event";

	private static final String TXT_EVENT_NAME = "txtEventName";

	private static final String TXT_EVENT_LOCATION = "txtEventLocation";

	private static final String TXT_EVENT_DATE = "txtEventDate";

	private static final String TXT_EVENT_ERROR = "lblError";

	private static final String LIST_EVENT = "eventList";

	// Tear down the session and stop the MySQL container after tests
	@AfterClass
	public static void shutdownServer() {
		StandardServiceRegistryBuilder.destroy(registry);
		if (sessionFactory != null) {
			sessionFactory.close();
		}
	}

	// Set up the UI and Repositories before each test
	@Override
	protected void onSetUp() throws Exception {
		databaseConfig = DBConfigSetup.getDatabaseConfig();
		databaseConfig.StartDatabaseConnection();
		registry = databaseConfig.getServiceRegistry();
		MetadataSources metadataSources = new MetadataSources(registry);
		sessionFactory = metadataSources.buildMetadata().buildSessionFactory();
		eventRepository = new EventMySqlRepository(sessionFactory);

		GuiActionRunner.execute(() -> {
			eventView = new EventManagementViewScreen();
			eventController = new EventController(eventView, eventRepository);
			eventView.setEventController(eventController);
			return eventView;
		});
		window = new FrameFixture(robot(), eventView);
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
		GuiActionRunner.execute(() -> eventController.getAllEvents());

		// Verify
		assertThat(window.list(LIST_EVENT).contents()).containsExactlyInAnyOrder(getDisplayString(event1),
				getDisplayString(event2));
	}

	// Test Add Event Success
	@Test
	@GUITest
	public void testAddEventSuccess() {
		// Setup
		setFieldValues(EVENT_NAME_1, EVENT_LOCATION_1, EVENT_DATE_1.toString());

		// Exercise
		window.button(JButtonMatcher.withText(BTN_ADD_EVENT)).click();

		// Verify
		await().atMost(5, TimeUnit.SECONDS)
				.untilAsserted(() -> assertThat(eventRepository.getAllEvents()).isNotEmpty());
		EventModel addedEvent = eventRepository.getAllEvents().get(0);
		assertThat(window.list(LIST_EVENT).contents()).containsExactly(getDisplayString(addedEvent));
	}

	// Test Add Event Failure
	@Test
	@GUITest
	public void testAddEventFailure() {
		// Setup
		EventModel event = new EventModel(EVENT_DEFAULT_ID, EVENT_NAME_1, EVENT_DATE_INVALID, EVENT_LOCATION_1);
		setFieldValues(EVENT_NAME_1, EVENT_LOCATION_1, EVENT_DATE_INVALID.toString());

		// Exercise
		window.button(JButtonMatcher.withText(BTN_ADD_EVENT)).click();

		// Verify
		await().atMost(5, TimeUnit.SECONDS)
				.untilAsserted(() -> assertThat(window.textBox(TXT_EVENT_ERROR).text().trim()).isNotBlank());
		assertThat(window.list(LIST_EVENT).contents()).isEmpty();
		window.textBox(TXT_EVENT_ERROR).requireText("Date cannot be in the past: " + event);
	}

	// Test Update Event Success
	@Test
	@GUITest
	public void testUpdateEventSuccess() {
		// Setup
		EventModel event = new EventModel(EVENT_DEFAULT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		GuiActionRunner.execute(() -> {
			eventController.addEvent(event);
		});

		// Exercise
		window.list(LIST_EVENT).selectItem(0);
		EventModel updatedEvent = eventRepository.getEventById(event.getEventId());
		updatedEvent.setEventLocation(EVENT_LOCATION_2);
		window.textBox(TXT_EVENT_LOCATION).setText(updatedEvent.getEventLocation());
		window.button(JButtonMatcher.withText(BTN_UPDATE_EVENT)).click();

		// verify
		await().atMost(5, TimeUnit.SECONDS).untilAsserted(
				() -> assertThat(window.list(LIST_EVENT).contents()).containsExactly(getDisplayString(updatedEvent)));
	}

	// Test Update Event Failure
	@Test
	@GUITest
	public void testUpdateEventFailure() {
		// Setup
		EventModel event = new EventModel(EVENT_DEFAULT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		GuiActionRunner.execute(() -> {
			eventController.addEvent(event);
		});

		// Exercise
		window.list(LIST_EVENT).selectItem(0);
		EventModel updatedEvent = eventRepository.getEventById(event.getEventId());
		updatedEvent.setEventDate(EVENT_DATE_INVALID);
		window.textBox(TXT_EVENT_DATE).setText(updatedEvent.getEventDate().toString());
		window.button(JButtonMatcher.withText(BTN_UPDATE_EVENT)).click();

		// verify
		await().atMost(5, TimeUnit.SECONDS)
				.untilAsserted(() -> assertThat(window.textBox(TXT_EVENT_ERROR).text().trim()).isNotBlank());
		assertThat(window.list(LIST_EVENT).contents()).containsExactly(getDisplayString(event));
		window.textBox(TXT_EVENT_ERROR).requireText("Date cannot be in the past: " + updatedEvent);
	}

	// Test Delete Event Success
	@Test
	@GUITest
	public void testDeleteEventSuccess() {
		// Setup
		EventModel event = new EventModel(EVENT_DEFAULT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		GuiActionRunner.execute(() -> {
			eventController.addEvent(event);
		});

		// Exercise
		window.list(LIST_EVENT).selectItem(0);
		window.button(JButtonMatcher.withText(BTN_DELETE_EVENT)).click();

		// verify
		await().atMost(15, TimeUnit.SECONDS)
				.untilAsserted(() -> assertThat(window.list(LIST_EVENT).contents()).isEmpty());
	}

	// Test Delete Failure
	@Test
	@GUITest
	public void testDeleteEventFailure() {
		// Setup
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		GuiActionRunner.execute(() -> eventView.getEventListModel().addElement(event));

		// Exercise
		window.list(LIST_EVENT).selectItem(0);
		window.button(JButtonMatcher.withText(BTN_DELETE_EVENT)).click();

		// Verify
		await().atMost(10, TimeUnit.SECONDS)
				.untilAsserted(() -> assertThat(window.textBox(TXT_EVENT_ERROR).text().trim()).isNotBlank());
		assertThat(window.list(LIST_EVENT).contents()).isEmpty();
		window.textBox(TXT_EVENT_ERROR).requireText("Event doesn't exist with id " + event.getEventId() + ": " + event);
	}

	// Helper Methods
	private void setFieldValues(String eventName, String eventLocation, String eventDate) {
		window.textBox(TXT_EVENT_NAME).enterText(eventName);
		window.textBox(TXT_EVENT_LOCATION).enterText(eventLocation);
		window.textBox(TXT_EVENT_DATE).enterText(eventDate);
	}

	private String getDisplayString(EventModel event) {
		return event.getEventId() + " | " + event.getEventName() + " | " + event.getEventLocation() + " | "
				+ event.getEventDate();
	}
}
