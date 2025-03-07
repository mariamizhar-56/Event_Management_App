/**
 * EventMySqlRepository is a repository class that interacts with the MySQL database 
 * to perform CRUD operations (Create, Read, Update, Delete) on EventModel objects. 
 * It uses Hibernate ORM to map Java objects to database tables and performs database 
 * transactions with the help of a SessionFactory. This class implements the EventRepository 
 * interface to provide methods for adding, updating, deleting, and retrieving events.
 * <p>
 * The class ensures proper transaction management by using Hibernate's session and transaction 
 * mechanisms and logs errors during database operations using Log4j.
 * <p>
 * The following methods are provided:
 * - addEvent: Adds a new event to the database.
 * - updateEvent: Updates an existing event in the database.
 * - deleteEvent: Deletes an event from the database.
 * - getAllEvents: Retrieves all events from the database.
 * - getEventById: Retrieves a specific event by its unique ID.
 */

package com.mycompany.eventmanagementapp.repository.mysql;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.apache.logging.log4j.LogManager;

import com.mycompany.eventmanagementapp.model.EventModel;
import com.mycompany.eventmanagementapp.repository.EventRepository;

public class EventMySqlRepository implements EventRepository {

    private SessionFactory sessionFactory;
    
    private static final Logger LOGGER = LogManager.getLogger(EventMySqlRepository.class);

    // Constructor to initialize the repository with the session factory
    public EventMySqlRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // Adds a new Event to the database
    @Override
    public void addEvent(EventModel event) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.save(event);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            LOGGER.error("Failed to add event", e);
            throw new HibernateException("Could not add event.", e);
        } finally {
            session.close();
        }
    }

    // Updates an existing Event in the database
    @Override
    public void updateEvent(EventModel event) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.update(event);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            LOGGER.error("Failed to update event", e);
            throw new HibernateException("Could not update event.", e);
        } finally {
            session.close();
        }
    }

    // Deletes an Event from the database
    @Override
    public void deleteEvent(EventModel event) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.delete(event);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            LOGGER.error("Failed to delete event", e);
            throw new HibernateException("Could not delete event.", e);
        } finally {
            session.close();
        }
    }

    // Retrieves all Events from the database
    @Override
    public List<EventModel> getAllEvents() {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery("from EventModel", EventModel.class).list();
        } finally {
            session.close();
        }
    }

    // Finds an Event by its unique ID
    @Override
    public EventModel getEventById(long eventId) {
        Session session = sessionFactory.openSession();
        try {
            return session.get(EventModel.class, eventId);
        } finally {
            session.close();
        }
    }
}