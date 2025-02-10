package com.mycompany.eventmanagementapp.repository;

import com.mycompany.eventmanagementapp.model.EventModel;

import java.util.List;

public interface EventRepository {

	void addEvent(EventModel event);

	void updateEvent(EventModel event);

	void deleteEvent(EventModel event);

	List<EventModel> getAllEvents();

	EventModel getEventById(long eventId);
}
