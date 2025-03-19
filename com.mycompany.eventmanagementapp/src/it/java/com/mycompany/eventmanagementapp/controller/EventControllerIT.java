/**
 * Integration test class for testing the EventController in the Event Management Application.
 * This class verifies the functionality of the EventController, which manages events in the system, 
 * ensuring that events are correctly retrieved, added, updated, and deleted.
 * 
 * The tests cover various scenarios such as:
 * - Retrieving and displaying all events in the view.
 * - Adding a new event to the system and ensuring the view is updated.
 * - Updating an existing event and ensuring the changes are reflected in the view.
 * - Deleting an event, including handling cases where the event has participants associated with it.
 * 
 * The tests are performed with a MySQL database managed by TestContainers. The class uses Hibernate for 
 * database interaction and Mockito for mocking the view layer to ensure proper communication between the 
 * controller and the view.
 * 
 * Each test verifies that the controller interacts correctly with the model and updates the view accordingly,
 * ensuring the expected behavior of event management actions (add, update, delete).
 */

package com.mycompany.eventmanagementapp.controller;

import org.junit.Test;
import org.junit.After;
import org.mockito.Mock;
import org.junit.Before;
import java.time.LocalDate;
import org.junit.BeforeClass;
import org.hibernate.SessionFactory;
import org.mockito.MockitoAnnotations;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;

import com.mycompany.eventmanagementapp.model.EventModel;
import com.mycompany.eventmanagementapp.model.ParticipantModel;
import com.mycompany.eventmanagementapp.view.EventManagementView;
import com.mycompany.eventmanagementapp.repository.EventRepository;
import com.mycompany.eventmanagementapp.dbconfigurations.DBConfigSetup;
import com.mycompany.eventmanagementapp.repository.ParticipantRepository;
import com.mycompany.eventmanagementapp.repository.mysql.EventMySqlRepository;
import com.mycompany.eventmanagementapp.dbconfigurations.DatabaseConfiguration;
import com.mycompany.eventmanagementapp.repository.mysql.ParticipantMySqlRepository;

public class EventControllerIT {

	private static DatabaseConfiguration databaseConfig;
	
	private EventRepository eventRepository;
	
	private ParticipantRepository participantRepository;
	
	@Mock
	private EventManagementView eventView;
	
	private AutoCloseable closeable;
	
	private static SessionFactory sessionFactory;

	private static StandardServiceRegistry registry;
	
	private EventController eventController;
	
	private static final long DEFAULT_EVENT_ID = -1;
	
	private static final String EVENT_NAME = "Music Festival";
	
	private static final String EVENT_NAME_2 = "University Event";
	
	private static final LocalDate EVENT_DATE = LocalDate.now().plusDays(10);
	
	private static final LocalDate EVENT_DATE_2 = LocalDate.now().plusDays(30);
	
	private static final String EVENT_LOCATION = "Florence";
	
	private static final String EVENT_LOCATION_2 = "Milan";
	
	private static final String PARTICIPANT_NAME = "John";
	
	private static final String PARTICIPANT_EMAIL = "John@gmail.com";

	// Setup Database Config for Eclipse OR Maven
	@BeforeClass
	public static void configureDB() {
		databaseConfig = DBConfigSetup.getDatabaseConfig();
		databaseConfig.StartDatabaseConnection();
	}

	// Initialize mocks and set up Hibernate session factory and repository
	@Before
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		registry = databaseConfig.getServiceRegistry();
		MetadataSources metadataSources = new MetadataSources(registry);
		sessionFactory = metadataSources.buildMetadata().buildSessionFactory();
		eventRepository = new EventMySqlRepository(sessionFactory);
		participantRepository = new ParticipantMySqlRepository(sessionFactory);
		eventController = new EventController(eventView, eventRepository);
	}

	// Close mocks after each test
	@After
	public void releaseMocks() throws Exception {
		if (sessionFactory != null) {
			sessionFactory.close();
		}
		closeable.close();
	}

	// Test that the controller retrieves all events and displays them in the
	// view
	@Test
	public void testShowAllEvents() {
		//Setup
		EventModel event = new EventModel(EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		eventRepository.addEvent(event);
		
		//Exercise
		eventController.getAllEvents();
		
		//Verify
		verify(eventView).showAllEvents(asList(event));
	}

	// Test that the controller adds a new event and updates the view
	@Test
	public void testAddEvent() {
		//Setup
		EventModel event = new EventModel(DEFAULT_EVENT_ID, EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		
		//Exercise
		eventController.addEvent(event);
		
		//Verify
		verify(eventView).eventAdded(event);
	}
	
	// Test that the controller updates an event and update the view
		@Test
		public void testUpdateEvent() {
			//Setup
			EventModel event = new EventModel(EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
			eventRepository.addEvent(event);
			EventModel updatedEvent = new EventModel(event.getEventId(), EVENT_NAME_2, EVENT_DATE_2, EVENT_LOCATION_2);
			
			//Exercise
			eventController.updateEvent(updatedEvent);
			
			//Verify
			verify(eventView).eventUpdated(updatedEvent);
		}

	// Test that the controller deletes an event when it has No Participants and updates the view
	@Test
	public void testDeleteEventWhenItHasNoParticipants() {
		//Setup
		EventModel event = new EventModel(EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
		eventRepository.addEvent(event);
		
		//Exercise
		eventController.deleteEvent(event);
		
		//Verify
		verify(eventView).eventDeleted(event);
	}
	
	// Test Delete Event event when it has Participants
		@Test
		public void testDeleteEventWhenItHasParticipants() {
			//Setup
			EventModel event = new EventModel(EVENT_NAME, EVENT_DATE, EVENT_LOCATION);
			ParticipantModel participant = new ParticipantModel(PARTICIPANT_NAME, PARTICIPANT_EMAIL);
			eventRepository.addEvent(event);
			participant.addEvent(event);
			participantRepository.addParticipant(participant);
			eventRepository.updateEvent(event);
			
			//Exercise
			eventController.deleteEvent(event);
			
			//Verify
			verify(eventView).showError("Event cannot be deleted. Participants are associated with it", event);
		}
}

