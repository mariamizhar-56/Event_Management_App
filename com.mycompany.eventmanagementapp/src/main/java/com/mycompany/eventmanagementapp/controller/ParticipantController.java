package com.mycompany.eventmanagementapp.controller;

import com.mycompany.eventmanagementapp.model.EventModel;
import com.mycompany.eventmanagementapp.model.ParticipantModel;
import com.mycompany.eventmanagementapp.repository.ParticipantRepository;
import com.mycompany.eventmanagementapp.repository.EventRepository;
import com.mycompany.eventmanagementapp.controller.utils.ValidationConfigurations;
import com.mycompany.eventmanagementapp.controller.utils.ValidationException;
import com.mycompany.eventmanagementapp.view.ParticipantManagementView;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ParticipantController {

	private static final Logger LOGGER = LogManager.getLogger(ParticipantController.class);
	private final ParticipantRepository participantRepository;
	private final EventRepository eventRepository;
	private ParticipantManagementView participantManagementView;

	public ParticipantController(ParticipantManagementView participantManagementView,
			ParticipantRepository participantRepository, EventRepository eventRepository) {
		this.participantManagementView = participantManagementView;
		this.participantRepository = participantRepository;
		this.eventRepository = eventRepository;
		LOGGER.info(
				"ParticipantController initialized with ParticipantManagementView, ParticipantRepository and EventRepository");
	}

	public void getAllParticipants() {
		LOGGER.info("Fetching all participants.");
		participantManagementView.showAllParticipants(participantRepository.getAllParticipants());
	}

	public void getAllEvents() {
		LOGGER.info("Fetching all events.");
		participantManagementView.showAllEvents(eventRepository.getAllEvents());
	}

	public synchronized void addParticipant(ParticipantModel participant, EventModel selectedEvent) {
		LOGGER.info("Adding a new participant: {}", participant);

		// Check for null values to avoid null pointer exceptions
		if (selectedEvent == null || participant == null) {
			LOGGER.error("Selected event or participant is null");
			participantManagementView.showError("Selected event or participant is null", participant);
			return;
		}

		// Validate the participant input
		if (!validateParticipant(participant)) {
			LOGGER.warn("Event validation failed: {}", participant);
			return;
		}

		// Check if the event exists by Id
		EventModel existingEvent = eventRepository.getEventById((selectedEvent.getEventId()));
		if (existingEvent == null) {
			LOGGER.warn("Event with id {} does not exist", selectedEvent.getEventId());
			participantManagementView.showError("Event doesn't exist with id " + selectedEvent.getEventId(),
					participant);
			return;
		}

		// Check for existing participant with same Email Id
		ParticipantModel existingParticipant = participantRepository
				.getParticipantByEmail(participant.getParticipantEmail());
		if (existingParticipant != null) {
			// Check if that participant has already associated with selected Event, If Yes
			// then show error
			if (existingParticipant.getEvents().contains(existingEvent)) {
				LOGGER.warn("Participant with email {} already exists and associated with event Id {}",
						existingParticipant.getParticipantEmail(), existingEvent.getEventId());
				participantManagementView
						.showError(
								"Participant already existed with email " + existingParticipant.getParticipantEmail()
										+ " and associated with event Id " + existingEvent.getEventId(),
								existingParticipant);
				return;
			}
			// else it means Participant with that Email exists but is not associated with
			// selected event, in that case we only associate participant to that event.
			existingParticipant.addEvent(existingEvent);
			participantRepository.updateParticipant(existingParticipant);
			eventRepository.updateEvent(existingEvent);
			participantManagementView.participantUpdated(existingParticipant);
			LOGGER.info("Existed Participant associated with event successfully: {}", existingParticipant);
			return;
		}

		// If no Participant exist then Add new participant and associate it with
		// selected event.
		participant.addEvent(existingEvent);
		participantRepository.addParticipant(participant);
		eventRepository.updateEvent(existingEvent);
		participantManagementView.participantAdded(participant);
		LOGGER.info("New Participant added and associated with event successfully: {}", participant);
	}

	public synchronized void updateParticipant(ParticipantModel participant) {
		LOGGER.info("Updating participant: {}", participant);

		// Check for null values to avoid null pointer exceptions
		if (participant == null) {
			LOGGER.error("participant is null");
			participantManagementView.showError("Participant is null", participant);
			return;
		}

		// Validate the participant input
		if (!validateParticipant(participant)) {
			LOGGER.warn("Event validation failed: {}", participant);
			return;
		}

		// Ensure participant exists before updating
		ParticipantModel existingParticipant = participantRepository
				.getParticipantByEmail(participant.getParticipantEmail());
		if (existingParticipant == null) {
			LOGGER.warn("Participant with email {} doesn't exists", participant.getParticipantEmail());
			participantManagementView.showError(
					"Participant doesn't exist with email " + participant.getParticipantEmail(), participant);
			return;
		}

		// Update participant
		participantRepository.updateParticipant(participant);
		participantManagementView.participantUpdated(participant);
		LOGGER.info("Participant updated successfully: {}", participant);
	}

	public synchronized void deleteParticipant(ParticipantModel participant, EventModel selectedEvent) {
		LOGGER.info("Deleting participant : {}", participant);

		// Check for null values to avoid null pointer exceptions
		if (selectedEvent == null || participant == null) {
			LOGGER.error("Selected event or participant is null");
			participantManagementView.showError("Selected event or participant is null", participant);
			return;
		}
		//Fetch fresh Event object from Database for proper removal of Participant and Event linking
		selectedEvent = eventRepository.getEventById(selectedEvent.getEventId());

		// Ensure participant exists before deleting
		ParticipantModel existingParticipant = participantRepository
				.getParticipantByEmail(participant.getParticipantEmail());
		if (existingParticipant == null) {
			LOGGER.warn("Participant with email {} doesn't exists", participant.getParticipantEmail());
			participantManagementView.showError(
					"Participant doesn't exist with email " + participant.getParticipantEmail(), participant);
			return;
		}
		// if Participant exist then check if selected event is associated with it or
		// not. If not then show error
		if (!existingParticipant.getEvents().contains(selectedEvent)) {
			LOGGER.warn("Participant with email {} doesn't associated with event Id {}",
					existingParticipant.getParticipantEmail(), selectedEvent.getEventId());
			participantManagementView.showError("Participant with email " + existingParticipant.getParticipantEmail()
					+ " is not associated with event Id " + selectedEvent.getEventId(), existingParticipant);
			return;
		}
		// If selected Event is associated with participant then remove that association
		existingParticipant.removeEvent(selectedEvent);
		participantRepository.updateParticipant(existingParticipant);
		eventRepository.updateEvent(selectedEvent);

		// Now check if there are no more association left then delete the Participant
		// from table
		if (existingParticipant.getEvents().isEmpty()) {
			participantRepository.deleteParticipant(existingParticipant);
			participantManagementView.participantDeleted(existingParticipant);
			LOGGER.info("Participant deleted successfully: {}", existingParticipant);
			return;
		} else {
			participantManagementView.participantUpdated(existingParticipant);
			LOGGER.info("Participant association with selected event removed successfully: {}", existingParticipant);
		}
	}

	private boolean validateParticipant(ParticipantModel participant) {
		LOGGER.debug("Validating participant: {}", participant);

		try {
			// Check if event Name and Location are valid strings
			ValidationConfigurations.validateString(participant.getParticipantName(), "Name");
			// Check if participant email is valid
			ValidationConfigurations.validateEmail(participant.getParticipantEmail());
			return true;
		} catch (ValidationException exception) {
			// Log and show validation errors in the view
			LOGGER.error("Validation error for participant: {}", participant, exception);
			participantManagementView.showError(exception.getMessage(), participant);
			return false;
		}
	}
}