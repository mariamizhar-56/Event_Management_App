package com.mycompany.eventmanagementapp.repository.mysql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mycompany.eventmanagementapp.model.EventModel;

public class EventMySqlRepositoryTest {

	private SessionFactory sessionFactory;
	private static StandardServiceRegistry registry;
	private EventMySqlRepository eventRepository;

	private static final long EVENT_ID = 1;
	private static final String EVENT_NAME_1 = "Music Festival";
	private static final LocalDate EVENT_DATE_1 = LocalDate.now().plusDays(10);
	private static final String EVENT_LOCATION_1 = "Florence";
	private static final String EVENT_NAME_2 = "University Event";
	private static final LocalDate EVENT_DATE_2 = LocalDate.now().plusDays(20);
	private static final String EVENT_LOCATION_2 = "Milan";

	@BeforeClass
	public static void setupServer() {
		// Setup Hibernate configuration for test database
		registry = new StandardServiceRegistryBuilder().configure("hibernate-test.cfg.xml").build();
	}

	@AfterClass
	public static void shutdownServer() {
		// Cleanup Hibernate resources after tests are completed
		StandardServiceRegistryBuilder.destroy(registry);
	}

	@Before
	public void setup() {
		// Setup sessionFactory and repository before each test
		sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
		eventRepository = new EventMySqlRepository(sessionFactory);
	}

	// Test for retrieving all Events from the database when it is empty
	@Test
	public void testgetAllEventsWhenDatabaseIsEmpty() {
		//Setup, Exercise & Verify
		assertThat(eventRepository.getAllEvents()).isEmpty();
	}

	// Test for retrieving all Events when database is not empty
	@Test
	public void testgetAllEventsWhenDatabaseIsNotEmpty() {
		//Setup
		EventModel event1 = new EventModel(EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		EventModel event2 = new EventModel(EVENT_NAME_2, EVENT_DATE_2, EVENT_LOCATION_2);
		//Exercise
		long eventId1 = addEventToTestDatabase(event1);
		long eventId2 = addEventToTestDatabase(event2);

		EventModel[] expectedEvents = new EventModel[] {
				new EventModel(eventId1, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1),
				new EventModel(eventId2, EVENT_NAME_2, EVENT_DATE_2, EVENT_LOCATION_2) };
		//Verify
		Assertions.assertThat(eventRepository.getAllEvents()).containsExactly(expectedEvents);
	}

	// Test for retrieving an event by Id when it is not found
	@Test
	public void testgetEventByIdWhenNotFound() {
		//Setup, Exercise & Verify
		Assertions.assertThat(eventRepository.getEventById(EVENT_ID)).isNull();
	}

	// Test for retrieving an event by Id when it is found
	@Test
	public void testgetEventByIdWhenFound() {
		//Setup
		EventModel event1 = new EventModel(EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		EventModel event2 = new EventModel(EVENT_NAME_2, EVENT_DATE_2, EVENT_LOCATION_2);
		//Exercise
		addEventToTestDatabase(event1);
		long eventId2 = addEventToTestDatabase(event2);
		EventModel expectedEvent = new EventModel(eventId2, EVENT_NAME_2, EVENT_DATE_2, EVENT_LOCATION_2);
		//Verify
		EventModel actualEvent = eventRepository.getEventById(eventId2);
		Assertions.assertThat(actualEvent).isEqualTo(expectedEvent);
	}

	// Test for adding a new event in database
	@Test
	public void testAddEvent() {
		//Setup
		EventModel event = new EventModel(EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		//Exercise
		eventRepository.addEvent(event);
		EventModel[] expectedEvent = new EventModel[] { event };
		//Verify
		Assertions.assertThat(eventRepository.getAllEvents()).containsExactly(expectedEvent);
	}

	// Test when adding a null event to database
	@Test
	public void testAddEventWhenEventIsNull() {
		//Setup, Exercise & Verify
		assertThatThrownBy(() -> eventRepository.addEvent(null)).isInstanceOf(HibernateException.class)
				.hasMessageContaining("Could not add event.");
	}

	// Test for deleting an event from database
	@Test
	public void testDeleteEvent() {
		//Setup
		EventModel event = new EventModel(EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		//Exercise
		addEventToTestDatabase(event);
		eventRepository.deleteEvent(event);
		//Verify
		Assertions.assertThat(eventRepository.getAllEvents()).isEmpty();
	}

	// Test for deleting a null event from database
	@Test
	public void testDeleteEventWhenEventIsNull() {
		//Setup, Exercise & Verify
		assertThatThrownBy(() -> eventRepository.deleteEvent(null)).isInstanceOf(HibernateException.class)
				.hasMessageContaining("Could not delete event.");
	}

	// Test for updating an existing event from database
	@Test
	public void testUpdateEvent() {
		//Setup
		EventModel event = new EventModel(EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		//Exercise
		long eventId = addEventToTestDatabase(event);
		EventModel updatedEvent = new EventModel(eventId, EVENT_NAME_1, EVENT_DATE_2, EVENT_LOCATION_2);
		eventRepository.updateEvent(updatedEvent);
		//Verify
		Assertions.assertThat(eventRepository.getAllEvents()).containsExactly(new EventModel[] { updatedEvent });
	}

	// Test for updating a null event from database
	@Test
	public void testUpdateEventWhenEventIsNull() {
		//Setup, Exercise & Verify
		assertThatThrownBy(() -> eventRepository.updateEvent(null)).isInstanceOf(HibernateException.class)
				.hasMessageContaining("Could not update event.");
	}

	// Utility Functions
	// Utility function to insert an event into the database.
	private long addEventToTestDatabase(EventModel event) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(event);
		session.getTransaction().commit();
		session.close();
		return event.getEventId();
	}
}
