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

import com.mycompany.eventmanagementapp.model.EventModel;
import com.mycompany.eventmanagementapp.controller.EventController;
import com.mycompany.eventmanagementapp.repository.mysql.EventMySqlRepository;
import com.mycompany.eventmanagementapp.view.screen.EventManagementViewScreen;

public class EventModelViewControllerIT extends AssertJSwingJUnitTestCase {

	// Using MySQLContainer from Test Containers for Integration Testing
	@SuppressWarnings("resource")
	@ClassRule
	public static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.28"))
			.withDatabaseName("test").withUsername("test").withPassword("test");

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

	//Helper Method for UI Input
	private void setFieldValues(String eventName, String eventLocation, String eventDate) {
		window.textBox(TXT_EVENT_NAME).enterText(eventName);
		window.textBox(TXT_EVENT_LOCATION).enterText(eventLocation);
		window.textBox(TXT_EVENT_DATE).enterText(eventDate);
	}
}
