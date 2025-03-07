/**
 * The EventModel class represents an event in the Event Management Application.
 * It contains the details of an event including the event's name, date, location, 
 * and a set of associated participants.
 *
 * This class is annotated as an entity for JPA (Java Persistence API), making it a persistent class 
 * that can be mapped to the "event" table in a relational database. It defines a many-to-many 
 * relationship with the ParticipantModel, meaning that each event can have multiple participants 
 * and each participant can be associated with multiple events.
 *
 * Key properties:
 * - eventId: The unique identifier for the event (primary key).
 * - eventName: The name of the event.
 * - eventDate: The date of the event.
 * - eventLocation: The location where the event is held.
 * - participants: A set of participants associated with this event.
 *
 * Constructors:
 * - Default constructor: Initializes the EventModel object.
 * - Constructor with eventId, eventName, eventDate, and eventLocation.
 * - Constructor with eventName, eventDate, and eventLocation (without eventId).
 *
 * Methods:
 * - Add and remove participants: Utility methods to manage the relationship between 
 *   events and participants.
 * - Getters and setters: For all the properties.
 * - hashCode and equals: Used to compare EventModel objects and generate hash codes for collections.
 * - toString: Provides a string representation of the event.
 *
 * JPA annotations:
 * - @Entity: Marks this class as a JPA entity.
 * - @Table: Specifies the table name ("event") for mapping.
 * - @Id and @GeneratedValue: Marks eventId as the primary key and defines its generation strategy.
 * - @Column: Specifies the columns for eventName, eventDate, and eventLocation.
 * - @ManyToMany: Defines the many-to-many relationship with participants.
 * - @JoinTable: Defines the join table to map the relationship between events and participants.
 */

package com.mycompany.eventmanagementapp.model;

import java.util.Set;
import java.util.HashSet;
import java.util.Objects;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "event")
public class EventModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    @Column(name = "event_name", nullable = false)
    private String eventName;

    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    @Column(name = "event_location", nullable = false)
    private String eventLocation;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.EAGER)
    @JoinTable(
        name = "event_participant",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "participant_id")
    )
    private Set<ParticipantModel> participants = new HashSet<>();

    // Constructors
    public EventModel() {
        super();
    }
    
    public EventModel(long eventId, String eventName, LocalDate eventDate, String eventLocation) {
    	this.eventId = eventId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventLocation = eventLocation;
    }

    public EventModel(String eventName, LocalDate eventDate, String eventLocation) {
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventLocation = eventLocation;
    }

    // Getters and Setters
    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public Set<ParticipantModel> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<ParticipantModel> participants) {
        this.participants = participants;
    }

    // Utility methods to manage the relationship
    public void addParticipant(ParticipantModel participant) {
        participants.add(participant);
        participant.getEvents().add(this);
    }

    public void removeParticipant(ParticipantModel participant) {
        participants.remove(participant);
        participant.getEvents().remove(this);
    }

    // Override hashCode and equals
    @Override
    public int hashCode() {
        return Objects.hash(eventDate, eventId, eventLocation, eventName);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EventModel other = (EventModel) obj;
        return Objects.equals(eventDate, other.eventDate) && Objects.equals(eventId, other.eventId)
                && Objects.equals(eventLocation, other.eventLocation) && Objects.equals(eventName, other.eventName);
    }
    @Override
	public String toString() {
		return "Event [" + eventId + ", " + eventName + ", " + eventLocation + ", " + eventDate + "]";
	}
}