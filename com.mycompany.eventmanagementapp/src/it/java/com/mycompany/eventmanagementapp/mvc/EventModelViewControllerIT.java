/**
 * This class contains integration tests for the Event Management functionality in the Event Management Application.
 * The tests validate the correct functionality of adding, updating, and deleting events through the UI, ensuring that 
 * the operations are performed correctly in conjunction with the backend database and event management logic.
 *
 * The tests are focused on:
 * - Adding a new event through the UI and verifying its presence in the repository.
 * - Updating an existing event through the UI and verifying the updated data in the repository.
 * - Deleting an event through the UI and verifying that it is removed from the repository.
 *
 * The tests use TestContainers to spin up a MySQL container, Hibernate ORM for database interactions, and AssertJ Swing for 
 * interacting with the Swing-based UI. The tests assert that after performing actions in the UI, the correct changes are made 
 * to the event data in the repository and the UI reflects these changes.
 *
 * The primary purpose of these tests is to verify that the event management functionality works as expected, 
 * both in terms of UI interactions and backend operations, ensuring data consistency and correct UI behavior.
 */

package com.mycompany.eventmanagementapp.mvc;

import org.junit.Test;
import java.time.LocalDate;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.hibernate.SessionFactory;
import java.util.concurrent.TimeUnit;
import org.hibernate.boot.MetadataSources;
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
import com.mycompany.eventmanagementapp.view.screen.EventManagementViewScreen;
import com.mycompany.eventmanagementapp.dbconfigurations.DatabaseConfiguration;

public class EventModelViewControllerIT extends AssertJSwingJUnitTestCase {

	private static DatabaseConfiguration databaseConfig;

	private static SessionFactory sessionFactory;

	private static StandardServiceRegistry registry;

	private FrameFixture window;

	private EventMySqlRepository eventRepository;

	private EventController eventController;

	private static final String EVENT_NAME_1 = "Music Festival";

	private static final LocalDate EVENT_DATE_1 = LocalDate.now().plusDays(10);

	private static final String EVENT_LOCATION_1 = "Florence";

	private static final LocalDate EVENT_DATE_2 = LocalDate.now().plusDays(20);

	private static final String BTN_ADD_EVENT = "Add Event";

	private static final String BTN_UPDATE_EVENT = "Update Event";

	private static final String BTN_DELETE_EVENT = "Delete Event";

	private static final String TXT_EVENT_NAME = "txtEventName";

	private static final String TXT_EVENT_LOCATION = "txtEventLocation";

	private static final String TXT_EVENT_DATE = "txtEventDate";

	private static final String LIST_EVENT = "eventList";

	// Setup Database Config for Eclipse OR Maven
	@BeforeClass
	public static void configureDB() {
		databaseConfig = DBConfigSetup.getDatabaseConfig();
		databaseConfig.StartDatabaseConnection();
	}

	// Tear down the session
	@AfterClass
	public static void shutdownServer() {
		StandardServiceRegistryBuilder.destroy(registry);
		if (sessionFactory != null) {
			sessionFactory.close();
		}
	}

	// Setup UI and Repositories before each test
	@Override
	protected void onSetUp() throws Exception {
		registry = databaseConfig.getServiceRegistry();
		MetadataSources metadataSources = new MetadataSources(registry);
		sessionFactory = metadataSources.buildMetadata().buildSessionFactory();
		eventRepository = new EventMySqlRepository(sessionFactory);

		window = new FrameFixture(robot(), GuiActionRunner.execute(() -> {
			EventManagementViewScreen eventView = new EventManagementViewScreen();
			eventController = new EventController(eventView, eventRepository);
			eventView.setEventController(eventController);
			return eventView;
		}));
		window.show(); // Shows the UI frame for testing
	}

	// Test Add Event From UI
	@Test
	public void testAddEvent() {
		// Setup
		setFieldValues(EVENT_NAME_1, EVENT_LOCATION_1, EVENT_DATE_1.toString());

		// Exercise
		window.button(JButtonMatcher.withText(BTN_ADD_EVENT)).click();

		// Verify
		await().atMost(5, TimeUnit.SECONDS)
				.untilAsserted(() -> assertThat(eventRepository.getAllEvents()).isNotEmpty());
		EventModel addedEvent = eventRepository.getAllEvents().get(0);
		assertThat(eventRepository.getEventById(addedEvent.getEventId())).isEqualTo(addedEvent);
	}

	// Test Update Event From UI
	@Test
	public void testUpdateEvent() {
		// Setup
		EventModel event = new EventModel(EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		eventRepository.addEvent(event);
		GuiActionRunner.execute(() -> eventController.getAllEvents());

		// Exercise
		window.list(LIST_EVENT).selectItem(0);
		EventModel updatedEvent = new EventModel(event.getEventId(), EVENT_NAME_1, EVENT_DATE_2, EVENT_LOCATION_1);
		window.textBox(TXT_EVENT_DATE).setText(updatedEvent.getEventDate().toString());
		window.button(JButtonMatcher.withText(BTN_UPDATE_EVENT)).click();

		// Verify
		await().atMost(5, TimeUnit.SECONDS).untilAsserted(
				() -> assertThat(eventRepository.getEventById(event.getEventId())).isEqualTo(updatedEvent));
	}

	// Test Delete Event From UI
	@Test
	public void testDeleteEvent() {
		// Setup
		EventModel event = new EventModel(EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		eventRepository.addEvent(event);
		GuiActionRunner.execute(() -> eventController.getAllEvents());

		// Exercise
		window.list(LIST_EVENT).selectItem(0);
		window.button(JButtonMatcher.withText(BTN_DELETE_EVENT)).click();

		// Verify
		await().atMost(10, TimeUnit.SECONDS)
				.untilAsserted(() -> assertThat(eventRepository.getEventById(event.getEventId())).isNull());
	}

	// Helper Method for UI Input
	private void setFieldValues(String eventName, String eventLocation, String eventDate) {
		window.textBox(TXT_EVENT_NAME).enterText(eventName);
		window.textBox(TXT_EVENT_LOCATION).enterText(eventLocation);
		window.textBox(TXT_EVENT_DATE).enterText(eventDate);
	}
}
