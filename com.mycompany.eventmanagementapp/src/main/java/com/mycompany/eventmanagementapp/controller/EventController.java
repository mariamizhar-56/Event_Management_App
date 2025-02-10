package com.mycompany.eventmanagementapp.controller;

import com.mycompany.eventmanagementapp.model.EventModel;
import com.mycompany.eventmanagementapp.repository.EventRepository;
import com.mycompany.eventmanagementapp.controller.utils.ValidationConfigurations;
import com.mycompany.eventmanagementapp.controller.utils.ValidationException;
import com.mycompany.eventmanagementapp.view.EventManagementView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class EventController {

	private static final Logger LOGGER = LogManager.getLogger(EventController.class);
	private EventManagementView eventManagementView;
	private EventRepository eventRepository;

	public EventController(EventManagementView eventManagementView, EventRepository eventRepository) {
		this.eventManagementView = eventManagementView;
		this.eventRepository = eventRepository;
		LOGGER.info("EventController initialized with EventManagementView and EventRepository");
	}

	public void getAllEvents() {
		LOGGER.info("Fetching all events.");
		eventManagementView.showAllEvents(eventRepository.getAllEvents());
	}

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
