package com.mycompany.eventmanagementapp.repository.mysql;

import com.mycompany.eventmanagementapp.model.ParticipantModel;
import com.mycompany.eventmanagementapp.repository.ParticipantRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class ParticipantMySqlRepository implements ParticipantRepository {

    private static final Logger LOGGER = LogManager.getLogger(ParticipantMySqlRepository.class);
    private SessionFactory sessionFactory;

    // Constructor to initialize the ParticipantMySqlRepository with the session factory
    public ParticipantMySqlRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // Adds a new participant to the database
    @Override
    public void addParticipant(ParticipantModel participant) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        try {
            session.save(participant);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            LOGGER.error("Failed to add participant", e);
            throw new HibernateException("Could not add participant.", e);
        } finally {
            session.close();
        }
    }

    // Updates an existing participant in the database
    @Override
    public void updateParticipant(ParticipantModel participant) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        try {
            session.update(participant);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            LOGGER.error("Failed to update participant", e);
            throw new HibernateException("Could not update participant.", e);
        } finally {
            session.close();
        }
    }

    // Deletes a participant from the database
    @Override
    public void deleteParticipant(ParticipantModel participant) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        try {
            session.delete(participant);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            LOGGER.error("Failed to delete participant", e);
            throw new HibernateException("Could not delete participant.", e);
        } finally {
            session.close();
        }
    }

    // Retrieves all participants from the database
    @Override
    public List<ParticipantModel> getAllParticipants() {
        Session session = sessionFactory.openSession();

        try {
            return session.createQuery("from ParticipantModel", ParticipantModel.class).list();
        } finally {
            session.close();
        }
    }

    // Retrieves a participant by their ID from the database
    @Override
    public ParticipantModel getParticipantById(long participantId) {
        Session session = sessionFactory.openSession();

        try {
            return session.get(ParticipantModel.class, participantId);
        } finally {
            session.close();
        }
    }

    // Retrieves a list of participants associated with a specific event ID
	/*
	 * @Override public List<ParticipantModel> getParticipantsByEventId(long
	 * eventId) { Session session = sessionFactory.openSession();
	 * 
	 * try { return session.
	 * createQuery("select p from ParticipantModel p join p.events e where e.eventId = :eventId"
	 * , ParticipantModel.class) .setParameter("eventId", eventId) .list(); }
	 * finally { session.close(); } }
	 */

    // Retrieves a participant by their email address
    @Override
    public ParticipantModel getParticipantByEmail(String email) {
        Session session = sessionFactory.openSession();

        try {
            return session.createQuery("from ParticipantModel where participantEmail = :email", ParticipantModel.class)
                    .setParameter("email", email)
                    .uniqueResult();
        } finally {
            session.close();
        }
    }
}
