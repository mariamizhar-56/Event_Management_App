package com.mycompany.eventmanagementapp.repository;

import com.mycompany.eventmanagementapp.model.ParticipantModel;
import java.util.List;

/**
 * Repository interface for managing participants in events.
 */
public interface ParticipantRepository {

	void addParticipant(ParticipantModel participant);

	void updateParticipant(ParticipantModel participant);

	void deleteParticipant(ParticipantModel participant);

	List<ParticipantModel> getAllParticipants();

	ParticipantModel getParticipantById(long participantId);

	ParticipantModel getParticipantByEmail(String email);
}
