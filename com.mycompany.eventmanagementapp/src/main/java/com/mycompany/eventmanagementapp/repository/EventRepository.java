/**
 * The EventRepository interface defines the contract for interacting with the event data storage 
 * (such as a database) in the Event Management Application. This interface provides methods to 
 * perform CRUD operations on event data, including adding, updating, deleting, and retrieving events.
 * The methods are intended to be implemented by a class that handles the persistence logic, such as 
 * interacting with a database using an ORM framework like Hibernate.
 *
 * Key Methods:
 * - addEvent: Adds a new event to the storage.
 * - updateEvent: Updates an existing event in the storage.
 * - deleteEvent: Deletes an event from the storage.
 * - getAllEvents: Retrieves a list of all events from the storage.
 * - getEventById: Retrieves a specific event by its ID.
 *
 */

package com.mycompany.eventmanagementapp.repository;

import java.util.List;

import com.mycompany.eventmanagementapp.model.EventModel;

public interface EventRepository {

	void addEvent(EventModel event);

	void updateEvent(EventModel event);

	void deleteEvent(EventModel event);

	List<EventModel> getAllEvents();

	EventModel getEventById(long eventId);
}
