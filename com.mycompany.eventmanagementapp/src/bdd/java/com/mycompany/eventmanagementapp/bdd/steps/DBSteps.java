/**
 * DBSteps class handles the database interactions for the Event Management App during the Cucumber BDD tests.
 * It sets up the test database, defines methods for inserting, updating, and removing events and participants, 
 * and manages the connection to the database using Hibernate and MySQL containers.
 * 
 * Key functionalities:
 * - Configures and manages the test database using TestContainers (MySQL container).
 * - Defines the Cucumber step definitions for setting up the database with event and participant data.
 * - Provides methods for adding test events and participants, and simulating actions like adding/removing participants 
 *   from events during test execution.
 */

package com.mycompany.eventmanagementapp.bdd.steps;

import java.util.List;
import java.time.LocalDate;
import org.hibernate.Session;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import org.hibernate.Transaction;
import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.containers.MySQLContainer;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import com.mycompany.eventmanagementapp.model.EventModel;
import com.mycompany.eventmanagementapp.model.ParticipantModel;

public class DBSteps {

	static final long DEFAULT_EVENT_ID = -1;
	
	static final long EVENT_ID = 1;
	
	static final String EVENT_NAME_1 = "Music Festival";
	
	static final LocalDate EVENT_DATE_1 = LocalDate.now().plusDays(10);
	
	static final String EVENT_LOCATION_1 = "Florence";
	
	static final long EVENT_ID_2 = 2;
	
	static final String EVENT_NAME_2 = "University Event";
	
	static final LocalDate EVENT_DATE_2 = LocalDate.now().plusDays(20);
	
	static final String EVENT_LOCATION_2 = "Milan";

	static final long PARTICIPANT_ID = 1;
	
	static final String PARTICIPANT_NAME = "John";
	
	static final String PARTICIPANT_EMAIL = "john@gmail.com";
	
	static final long PARTICIPANT_ID_2 = 2;
	
	static final String PARTICIPANT_NAME_2 = "Martin";
	
	static final String PARTICIPANT_EMAIL_2 = "martin@gmail.com";

	// Hibernate session management
	private static SessionFactory sessionFactory;
	
	private static StandardServiceRegistry registry;
	
	static String dbURL = "";
	
	static final String DB_USER = "test";
	
	static final String DB_PASS = "test";

	private EventModel firstEvent;
	
	private ParticipantModel SecondParticipant;
	
	// Test Containers MySQL container
	@SuppressWarnings("resource")
	public static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.28"))
			.withDatabaseName("test").withUsername("test").withPassword("test");

	@BeforeAll
	public static void configureDB() {
		if (isRunningInEclipse()) {
			mysqlContainer.start();
			dbURL = mysqlContainer.getJdbcUrl();
			System.setProperty("ENVIRONMENT", "testWithEclipes");
			registry = new StandardServiceRegistryBuilder().configure("hibernate-IT.cfg.xml")
					.applySetting("hibernate.connection.url", dbURL)
					.applySetting("hibernate.connection.username", DB_USER)
					.applySetting("hibernate.connection.password", DB_PASS).build();
		} else {
			// For Maven or any other environment, use the default configuration
			registry = new StandardServiceRegistryBuilder().configure("hibernate-IT.cfg.xml")
					.applySetting("hibernate.connection.url", "jdbc:mysql://localhost:3307/event_management_app")
					.applySetting("hibernate.connection.password", "test").build();
		}
	}

	private static boolean isRunningInEclipse() {
		return System.getProperty("surefire.test.class.path") == null;
	}

	@AfterAll
	public static void shutdownDB() {
		// Clean up Hibernate resources after each test
		StandardServiceRegistryBuilder.destroy(registry);
		if (sessionFactory != null) {
			sessionFactory.close();
		}
		if (System.getProperty("surefire.test.class.path") == null) {
			// Using Test containers (Eclipse environment)
			mysqlContainer.stop();
		}
	}

	@Before
	public void setup() {
		MetadataSources metadataSources = new MetadataSources(registry);
		sessionFactory = metadataSources.buildMetadata().buildSessionFactory();
	}

	@Given("The database contain events with the following values")
	public void the_database_contain_events_with_the_following_values(List<List<String>> values) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		
		values.forEach(eventValues -> {
			EventModel event = new EventModel(DEFAULT_EVENT_ID, eventValues.get(0), LocalDate.parse(eventValues.get(2)), eventValues.get(1));
			session.save(event);
		});
		
		session.getTransaction().commit();
		session.close();
	}
	
	@Given("The database contain events for participant with the following values")
	public void the_database_contain_events_for_participant_with_the_following_values(List<List<String>> values) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		
		values.forEach(eventValues -> {
			EventModel event = new EventModel(DEFAULT_EVENT_ID, eventValues.get(1), LocalDate.parse(eventValues.get(3)), eventValues.get(2));
			session.save(event);
		});
		
		session.getTransaction().commit();
		session.close();
	}

	@Given("The database contain the Participants with the following values")
	public void the_database_contain_the_Participants_with_the_following_values(List<List<String>> values) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		
		values.forEach(participantValues -> {
			EventModel event = new EventModel(DEFAULT_EVENT_ID, participantValues.get(3), LocalDate.parse(participantValues.get(5)), participantValues.get(4));
			Long eventId = Long.parseLong(participantValues.get(2));
			EventModel existingEvent = session.get(EventModel.class, eventId);
			if (existingEvent == null) {
				session.save(event);
				existingEvent = event;
			}
			
			ParticipantModel participant = new ParticipantModel(participantValues.get(0), participantValues.get(1));
			ParticipantModel existingParticipant = session.createQuery("from ParticipantModel where participantEmail = :email", ParticipantModel.class)
			        .setParameter("email", participant.getParticipantEmail())
			        .uniqueResult();
			if (existingParticipant == null) {
				participant.addEvent(existingEvent);
				session.save(participant);
				session.update(existingEvent);
			}else {
				existingParticipant.addEvent(existingEvent);
				session.update(existingParticipant);
				session.update(existingEvent);
			}
		});
		
		session.getTransaction().commit();
		session.close();
	}

	@Given("The database contains a few events")
	public void the_database_contains_a_few_events() {
		firstEvent = new EventModel(DEFAULT_EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		addTestEventToDatabase(firstEvent);
		EventModel event2 = new EventModel(DEFAULT_EVENT_ID, EVENT_NAME_2, EVENT_DATE_2, EVENT_LOCATION_2);
		addTestEventToDatabase(event2);
	}

	@Given("The event is in the meantime removed from the database")
	public void the_event_is_in_the_meantime_removed_from_the_database() {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.delete(firstEvent);
		session.getTransaction().commit();
		session.close();
	}

	@Given("The database contains a few participants")
	public void the_database_contains_a_few_category() {
		EventModel event1 = new EventModel(DEFAULT_EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		addTestEventToDatabase(event1);
		EventModel event2 = new EventModel(DEFAULT_EVENT_ID, EVENT_NAME_2, EVENT_DATE_2, EVENT_LOCATION_2);
		addTestEventToDatabase(event2);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		addTestParticipantToDatabase(participant, event1);
		addTestParticipantToDatabase(participant, event2);
	    SecondParticipant = new ParticipantModel(PARTICIPANT_NAME_2, PARTICIPANT_EMAIL_2);
		addTestParticipantToDatabase(SecondParticipant, event1);
	}

	@Given("The participant is in the meantime removed from that event")
	public void the_participant_is_in_the_meantime_removed_from_that_event() {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		
		EventModel associatedEvent = SecondParticipant.getEvents().iterator().next();
		EventModel existingEvent = session.get(EventModel.class, associatedEvent.getEventId());
		SecondParticipant.removeEvent(existingEvent);
		SecondParticipant = (ParticipantModel) session.merge(SecondParticipant);
		session.delete(SecondParticipant);
		
		session.getTransaction().commit();
		session.close();
	}

	private void addTestEventToDatabase(EventModel event) {
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		session.save(event);
		transaction.commit();
		session.close();
	}

	private void addTestParticipantToDatabase(ParticipantModel participant, EventModel event) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		
		ParticipantModel existingParticipant = session.createQuery("from ParticipantModel where participantEmail = :email", ParticipantModel.class)
		        .setParameter("email", participant.getParticipantEmail())
		        .uniqueResult();
		
		EventModel existingEvent = session.get(EventModel.class, event.getEventId());
		
		if (existingParticipant == null) {
			participant.addEvent(existingEvent);
			session.save(participant);
			session.update(existingEvent);
		} else {
			existingParticipant.addEvent(existingEvent);
			session.update(existingParticipant);
			session.update(existingEvent);
			participant = existingParticipant;
		}
		
		session.getTransaction().commit();
		session.close();
	}
}