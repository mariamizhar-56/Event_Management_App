/**
 * EventManagementView is an interface that defines the contract for the view layer in 
 * the Event Management application. It provides methods for displaying events and 
 * updates related to event operations, such as adding, updating, deleting, and showing errors.
 * <p>
 * This interface is intended to be implemented by any view class that will display 
 * events and their status to the user. It ensures that the relevant event data is 
 * passed to the view for proper display and user interaction.
 * <p>
 * The following methods are provided:
 * - showAllEvents: Displays a list of all events.
 * - eventAdded: Notifies the view that an event has been successfully added.
 * - showError: Displays an error message related to a specific event.
 * - eventDeleted: Notifies the view that an event has been successfully deleted.
 * - eventUpdated: Notifies the view that an event has been successfully updated.
 */

package com.mycompany.eventmanagementapp.view;

import java.util.List;

import com.mycompany.eventmanagementapp.model.EventModel;

public interface EventManagementView {

	void showAllEvents(List<EventModel> events);

	void eventAdded(EventModel event);

	void showError(String message, EventModel event);

	void eventDeleted(EventModel event);

	void eventUpdated(EventModel event);
}