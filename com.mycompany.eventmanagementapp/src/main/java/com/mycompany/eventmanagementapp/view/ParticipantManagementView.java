package com.mycompany.eventmanagementapp.view;

import java.util.List;

import com.mycompany.eventmanagementapp.model.EventModel;
import com.mycompany.eventmanagementapp.model.ParticipantModel;

public interface ParticipantManagementView {

	void showAllParticipants(List<ParticipantModel> participants);

	void showAllEvents(List<EventModel> events);

	void participantAdded(ParticipantModel participant);

	void showError(String message, ParticipantModel participant);

	void participantDeleted(ParticipantModel participant);

	void participantUpdated(ParticipantModel participant);

	// void showErrorparticipantNotFound(String message, ParticipantModel
	// participant);
}
