/**
 * ParticipantMySqlRepository is a repository class that manages ParticipantModel objects 
 * in the MySQL database. It provides CRUD operations (Create, Read, Update, Delete) 
 * using Hibernate ORM to interact with the database.
 * <p>
 * This class implements the ParticipantRepository interface and provides methods for adding, 
 * updating, deleting, and retrieving participants from the database. It ensures proper 
 * transaction management with the help of Hibernate's session and transaction mechanisms.
 * Additionally, errors during database operations are logged using Log4j.
 * <p>
 * The following methods are provided:
 * - addParticipant: Adds a new participant to the database.
 * - updateParticipant: Updates an existing participant in the database.
 * - deleteParticipant: Deletes a participant from the database.
 * - getAllParticipants: Retrieves all participants from the database.
 * - getParticipantById: Retrieves a specific participant by their unique ID.
 * - getParticipantByEmail: Retrieves a participant by their email address.
 */

package com.mycompany.eventmanagementapp.repository.mysql;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.apache.logging.log4j.LogManager;

import com.mycompany.eventmanagementapp.model.ParticipantModel;
import com.mycompany.eventmanagementapp.repository.ParticipantRepository;

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