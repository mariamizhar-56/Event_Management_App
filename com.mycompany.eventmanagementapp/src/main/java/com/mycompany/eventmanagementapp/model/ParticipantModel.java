/**
 * The ParticipantModel class represents a participant in the Event Management Application.
 * It contains the details of a participant including their name, email, and a set of events they are associated with.
 * This class is annotated as an entity for JPA (Java Persistence API), making it a persistent class 
 * that can be mapped to the "participant" table in a relational database. It defines a many-to-many 
 * relationship with the EventModel, meaning that each participant can be associated with multiple events 
 * and each event can have multiple participants.
 *
 * Key properties:
 * - participantId: The unique identifier for the participant (primary key).
 * - participantName: The name of the participant.
 * - participantEmail: The email of the participant, which must be unique.
 * - events: A set of events associated with this participant.
 *
 * Constructors:
 * - Default constructor: Initializes the ParticipantModel object.
 * - Constructor with participantId, participantName, and participantEmail.
 * - Constructor with participantName and participantEmail (without participantId).
 *
 * Methods:
 * - Add and remove events: Utility methods to manage the relationship between 
 *   participants and events.
 * - Getters and setters: For all the properties.
 * - hashCode and equals: Used to compare ParticipantModel objects and generate hash codes for collections.
 * - toString: Provides a string representation of the participant.
 *
 * JPA annotations:
 * - @Entity: Marks this class as a JPA entity.
 * - @Table: Specifies the table name ("participant") for mapping.
 * - @Id and @GeneratedValue: Marks participantId as the primary key and defines its generation strategy.
 * - @Column: Specifies the columns for participantName and participantEmail.
 * - @ManyToMany: Defines the many-to-many relationship with events, with the mappedBy attribute indicating 
 *   that the relationship is managed by the EventModel class.
 */

package com.mycompany.eventmanagementapp.model;

import java.util.Set;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;

@Entity
@Table(name = "participant")
public class ParticipantModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long participantId;

    @Column(name = "participant_name", nullable = false)
    private String participantName;

    @Column(name = "participant_email", nullable = false, unique = true)
    private String participantEmail;

    @ManyToMany(mappedBy = "participants", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.EAGER)
    private Set<EventModel> events = new HashSet<>();

    // Constructors
    public ParticipantModel() {
        super();
    }
    
    public ParticipantModel(long participantId, String participantName, String participantEmail) {
    	this.participantId = participantId;
        this.participantName = participantName;
        this.participantEmail = participantEmail;
    }

    public ParticipantModel(String participantName, String participantEmail) {
        this.participantName = participantName;
        this.participantEmail = participantEmail;
    }

    // Getters and Setters
    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public String getParticipantEmail() {
        return participantEmail;
    }

    public void setParticipantEmail(String participantEmail) {
        this.participantEmail = participantEmail;
    }

    public Set<EventModel> getEvents() {
        return events;
    }

    public void setEvents(Set<EventModel> events) {
        this.events = events;
    }

    // Utility methods to manage the relationship
    public void addEvent(EventModel event) {
        events.add(event);
        event.getParticipants().add(this);
    }

    public void removeEvent(EventModel event) {
        events.remove(event);
        event.getParticipants().remove(this);
    }

    // Override hashCode and equals
    @Override
    public int hashCode() {
        return Objects.hash(participantId, participantName, participantEmail);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        ParticipantModel other = (ParticipantModel) obj;
        return Objects.equals(participantId, other.participantId)
                && Objects.equals(participantName, other.participantName)
                && Objects.equals(participantEmail, other.participantEmail);
    }
    
    @Override
	public String toString() {
		return "Participant [" + participantId + ", " + participantName + ", " + participantEmail + "]";
	}
}