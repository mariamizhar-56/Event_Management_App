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

import com.mycompany.eventmanagementapp.model.ParticipantModel;

public class ParticipantMySqlRepositoryTest {

	private SessionFactory sessionFactory;
	private static StandardServiceRegistry registry;
	private ParticipantMySqlRepository participantRepository;

	private static final long PARTICIPANT_ID = 1;
	private static final String PARTICIPANT_NAME_1 = "John";
	private static final String PARTICIPANT_EMAIL_1 = "John@gmail.com";
	private static final String PARTICIPANT_NAME_2 = "Robert";
	private static final String PARTICIPANT_EMAIL_2 = "Robert@gmail.com";

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
		participantRepository = new ParticipantMySqlRepository(sessionFactory);
	}

	// Test for retrieving all Participants from the database when it is empty
	@Test
	public void testgetAllParticipantsWhenDatabaseIsEmpty() {
		//Setup, Exercise & Verify
		assertThat(participantRepository.getAllParticipants()).isEmpty();
	}

	// Test for retrieving all Participants when database is not empty
	@Test
	public void testgetAllEventsWhenDatabaseIsNotEmpty() {
		//Setup
		ParticipantModel participant1 = new ParticipantModel(PARTICIPANT_NAME_1, PARTICIPANT_EMAIL_1);
		ParticipantModel participant2 = new ParticipantModel(PARTICIPANT_NAME_2, PARTICIPANT_EMAIL_2);
		//Exercise
		long participantId1 = addParticipantToTestDatabase(participant1);
		long participantId2 = addParticipantToTestDatabase(participant2);

		ParticipantModel[] expectedParticipants = new ParticipantModel[] {
				new ParticipantModel(participantId1, PARTICIPANT_NAME_1, PARTICIPANT_EMAIL_1),
				new ParticipantModel(participantId2, PARTICIPANT_NAME_2, PARTICIPANT_EMAIL_2) };
		//Verify
		Assertions.assertThat(participantRepository.getAllParticipants()).containsExactly(expectedParticipants);
	}

	// Test for retrieving Participant by Id when it is not found
	@Test
	public void testgetParticipantByIdWhenNotFound() {
		//Setup, Exercise & Verify
		Assertions.assertThat(participantRepository.getParticipantById(PARTICIPANT_ID)).isNull();
	}

	// Test for retrieving an Participant by Id when it is found
	@Test
	public void testgetParticipantByIdWhenFound() {
		//Setup
		ParticipantModel participant1 = new ParticipantModel(PARTICIPANT_NAME_1, PARTICIPANT_EMAIL_1);
		ParticipantModel participant2 = new ParticipantModel(PARTICIPANT_NAME_2, PARTICIPANT_EMAIL_2);
		//Exercise
		addParticipantToTestDatabase(participant1);
		long participantId2 = addParticipantToTestDatabase(participant2);
		ParticipantModel expectedParticipant = new ParticipantModel(participantId2, PARTICIPANT_NAME_2,
				PARTICIPANT_EMAIL_2);
		//Verify
		ParticipantModel actualParticipant = participantRepository.getParticipantById(participantId2);
		Assertions.assertThat(actualParticipant).isEqualTo(expectedParticipant);
	}

	// Test for retrieving Participant by Email when it is not found
	@Test
	public void testgetParticipantByEmailWhenNotFound() {
		//Setup, Exercise & Verify
		Assertions.assertThat(participantRepository.getParticipantByEmail(PARTICIPANT_EMAIL_1)).isNull();
	}

	// Test for retrieving an Participant by Email when it is found
	@Test
	public void testgetParticipantByEmailWhenFound() {
		//Setup
		ParticipantModel participant1 = new ParticipantModel(PARTICIPANT_NAME_1, PARTICIPANT_EMAIL_1);
		ParticipantModel participant2 = new ParticipantModel(PARTICIPANT_NAME_2, PARTICIPANT_EMAIL_2);
		//Exercise
		addParticipantToTestDatabase(participant1);
		long participantId2 = addParticipantToTestDatabase(participant2);
		ParticipantModel expectedParticipant = new ParticipantModel(participantId2, PARTICIPANT_NAME_2,
				PARTICIPANT_EMAIL_2);
		//Verify
		ParticipantModel actualParticipant = participantRepository
				.getParticipantByEmail(participant2.getParticipantEmail());
		Assertions.assertThat(actualParticipant).isEqualTo(expectedParticipant);
	}

	// Test for adding a new Participant in database
	@Test
	public void testAddParticipant() {
		//Setup
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_NAME_1, PARTICIPANT_EMAIL_1);
		//Exercise
		participantRepository.addParticipant(participant);
		//Verify
		ParticipantModel[] expectedParticipant = new ParticipantModel[] { participant };
		Assertions.assertThat(participantRepository.getAllParticipants()).containsExactly(expectedParticipant);
	}

	// Test when adding a null Participant to database
	@Test
	public void testAddParticipantWhenParticipantIsNull() {
		//Setup, Exercise & Verify
		assertThatThrownBy(() -> participantRepository.addParticipant(null)).isInstanceOf(HibernateException.class)
				.hasMessageContaining("Could not add participant.");
	}

	// Test for deleting an Participant from database
	@Test
	public void testDeleteParticipant() {
		//Setup
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_NAME_1, PARTICIPANT_EMAIL_1);
		//Exercise
		addParticipantToTestDatabase(participant);
		participantRepository.deleteParticipant(participant);
		//Verify
		Assertions.assertThat(participantRepository.getAllParticipants()).isEmpty();
	}

	// Test for deleting a null Participant from database
	@Test
	public void testDeleteParticipantWhenParticipantIsNull() {
		//Setup, Exercise & Verify
		assertThatThrownBy(() -> participantRepository.deleteParticipant(null)).isInstanceOf(HibernateException.class)
				.hasMessageContaining("Could not delete participant.");
	}

	// Test for updating an existing Participant from database
	@Test
	public void testUpdateParticipant() {
		//Setup
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_NAME_1, PARTICIPANT_EMAIL_1);
		//Exercise
		long participantId = addParticipantToTestDatabase(participant);
		ParticipantModel updatedParticipant = new ParticipantModel(participantId, PARTICIPANT_NAME_2,
				PARTICIPANT_EMAIL_1);
		participantRepository.updateParticipant(updatedParticipant);
		//Verify
		Assertions.assertThat(participantRepository.getAllParticipants())
				.containsExactly(new ParticipantModel[] { updatedParticipant });
	}

	// Test for updating a null event from database
	@Test
	public void testUpdateParticipantWhenParticipantIsNull() {
		//Setup, Exercise & Verify
		assertThatThrownBy(() -> participantRepository.updateParticipant(null)).isInstanceOf(HibernateException.class)
				.hasMessageContaining("Could not update participant.");
	}

	// Utility Functions
	// Utility function to insert a participant into the database.
	private long addParticipantToTestDatabase(ParticipantModel participant) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(participant);
		session.getTransaction().commit();
		session.close();
		return participant.getParticipantId();
	}
}
