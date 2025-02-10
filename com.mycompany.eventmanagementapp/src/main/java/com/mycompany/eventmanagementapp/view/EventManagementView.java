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
