package com.mycompany.eventmanagementapp.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.*;

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

