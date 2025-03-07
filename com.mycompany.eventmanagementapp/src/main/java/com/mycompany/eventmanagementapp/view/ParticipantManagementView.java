/**
 * ParticipantManagementView is an interface that defines the contract for the view layer 
 * in the Event Management application, specifically for managing participants. It provides 
 * methods for displaying participant information and handling updates related to participant 
 * operations, such as adding, updating, deleting, and showing errors.
 * <p>
 * This interface is intended to be implemented by any view class responsible for interacting 
 * with the user regarding participant-related data. It ensures that the relevant participant 
 * and event data are displayed properly and enables appropriate user interactions with the application.
 * <p>
 * The following methods are provided:
 * - showAllParticipants: Displays a list of all participants.
 * - showAllEvents: Displays a list of all events.
 * - participantAdded: Notifies the view that a participant has been successfully added.
 * - showError: Displays an error message related to a specific participant.
 * - participantDeleted: Notifies the view that a participant has been successfully deleted.
 * - participantUpdated: Notifies the view that a participant has been successfully updated.
 */

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
}