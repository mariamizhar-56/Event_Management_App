/**
 * EventController handles the business logic for managing events within the Event Management Application.
 * It acts as a bridge between the Event Management View and the Event Repository, handling operations such as:
 * - Fetching all events
 * - Adding new events
 * - Updating existing events
 * - Deleting events
 *
 * The controller ensures that all event-related actions are validated, and it communicates the results back to the view.
 * It also handles error cases, such as when an event already exists or when an event is not found.
 * 
 * The controller uses a synchronized approach for add, update, and delete operations to avoid race conditions and ensure thread safety.
 *
 * Key functionalities:
 * - Retrieves all events from the repository and displays them in the view.
 * - Validates events before adding or updating them (checks for missing or incorrect data).
 * - Handles the deletion of events, with checks for associated participants (prevents deletion if participants are linked).
 * - Provides detailed logging of each action and validation process.
 *
 * Dependencies:
 * - EventManagementView: The view layer for displaying events and error messages.
 * - EventRepository: The repository layer for accessing and modifying event data in the database.
 * - ValidationConfigurations: A utility class used for validating event data, such as name, location, and date.
 * 
 * Logging:
 * - Logs various levels of information (info, warn, debug, error) for all operations to help track actions and errors in the system.
 * 
 * Methods:
 * - getAllEvents: Fetches and displays all events.
 * - addEvent: Adds a new event, after validating the input and checking if it already exists.
 * - updateEvent: Updates an existing event, ensuring that the event exists and the input is valid.
 * - deleteEvent: Deletes an event, checking if it exists and if there are any participants associated with it.
 * - validateEvent: Validates the event data (name, location, date) before any operation.
 */

package com.mycompany.eventmanagementapp.controller;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.mycompany.eventmanagementapp.model.EventModel;
import com.mycompany.eventmanagementapp.view.EventManagementView;
import com.mycompany.eventmanagementapp.repository.EventRepository;
import com.mycompany.eventmanagementapp.controller.utils.ValidationException;
import com.mycompany.eventmanagementapp.controller.utils.ValidationConfigurations;

public class EventController {

	private static final Logger LOGGER = LogManager.getLogger(EventController.class);
	
	private EventManagementView eventManagementView;
	
	private EventRepository eventRepository;

	public EventController(EventManagementView eventManagementView, EventRepository eventRepository) {
		this.eventManagementView = eventManagementView;
		this.eventRepository = eventRepository;
		LOGGER.info("EventController initialized with EventManagementView and EventRepository");
	}

	//Get All Events
	public void getAllEvents() {
		LOGGER.info("Fetching all events.");
		eventManagementView.showAllEvents(eventRepository.getAllEvents());
	}

	//Add Event Method for Event Controller
	public synchronized void addEvent(EventModel event) {
		LOGGER.info("Adding a new event: {}", event);

		// Validate the event input
		if (!validateEvent(event)) {
			LOGGER.warn("Event validation failed: {}", event);
			return;
		}

		// Check if a event with the same Id exists
		EventModel existingEvent = eventRepository.getEventById((event.getEventId()));
		if (existingEvent != null) {
			LOGGER.warn("existingEvent with id {} already exists", existingEvent.getEventId());
			eventManagementView.showError("Event already existed with id " + event.getEventId(), existingEvent);
			return;
		}

		// Save the new event and notify the view
		eventRepository.addEvent(event);
		eventManagementView.eventAdded(event);
		LOGGER.info("New event added successfully: {}", event);
	}

	//Update Event Method for Event Controller
	public synchronized void updateEvent(EventModel event) {
		LOGGER.info("Updating existing event: {}", event);

		// Validate the event input
		if (!validateEvent(event)) {
			LOGGER.warn("Event validation failed: {}", event);
			return;
		}

		// Check if the event exists by Id
		EventModel existingEvent = eventRepository.getEventById((event.getEventId()));
		if (existingEvent == null) {
			LOGGER.warn("Event with id {} does not exist", event.getEventId());
			eventManagementView.showError("Event doesn't exist with id " + event.getEventId(), event);
			return;
		}

		// Update the event and notify the view
		event.setParticipants(existingEvent.getParticipants());
		eventRepository.updateEvent(event);
		eventManagementView.eventUpdated(event);
		LOGGER.info("Event updated successfully: {}", event);
	}

	//Delete Event Method for Event Controller
	public synchronized void deleteEvent(EventModel event) {
		LOGGER.info("Deleting event : {}", event);

		// Check if the event exists
		EventModel existingEvent = eventRepository.getEventById((event.getEventId()));
		if (existingEvent == null) {
			LOGGER.warn("Event with id {} does not exist", event.getEventId());
			eventManagementView.showError("Event doesn't exist with id " + event.getEventId(), event);
			return;
		}

		// Check if the event has associated participants, which would prevent deletion
		if (!event.getParticipants().isEmpty()) {
			LOGGER.warn("Event with id {} cannot be deleted because it has associated participants",
					event.getEventId());
			eventManagementView.showError("Event cannot be deleted. Participants are associated with it", event);
			return;
		}

		// Delete the event if it doesn't have any participants in it.
		eventRepository.deleteEvent(event);
		eventManagementView.eventDeleted(event);
		LOGGER.info("Event deleted successfully: {}", event);
	}

	//Validate Event Method for validating event input
	private boolean validateEvent(EventModel event) {
		LOGGER.debug("Validating event: {}", event);

		try {
			// Check if event Name and Location are valid strings
			ValidationConfigurations.validateString(event.getEventName(), "Name");
			ValidationConfigurations.validateString(event.getEventLocation(), "Location");
			// Check if event Date is valid
			ValidationConfigurations.validateDate(event.getEventDate());
			return true;
		} catch (ValidationException exception) {
			// Log and show validation errors in the view
			LOGGER.error("Validation error for event: {}", event, exception);
			eventManagementView.showError(exception.getMessage(), event);
			return false;
		}
	}
}