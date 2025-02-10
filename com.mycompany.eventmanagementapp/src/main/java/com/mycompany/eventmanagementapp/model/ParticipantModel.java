package com.mycompany.eventmanagementapp.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
