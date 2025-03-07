/**
 * The ParticipantRepository interface defines the contract for interacting with the participant data 
 * storage (such as a database) in the Event Management Application. This interface provides methods 
 * to perform CRUD operations on participant data, including adding, updating, deleting, and retrieving participants.
 * The methods are intended to be implemented by a class responsible for handling the persistence logic, 
 * such as interacting with a database using an ORM framework like Hibernate.
 *
 * Key Methods:
 * - addParticipant: Adds a new participant to the storage.
 * - updateParticipant: Updates an existing participant in the storage.
 * - deleteParticipant: Deletes a participant from the storage.
 * - getAllParticipants: Retrieves a list of all participants from the storage.
 * - getParticipantById: Retrieves a specific participant by their ID.
 * - getParticipantByEmail: Retrieves a participant by their email address.
 *
 */

package com.mycompany.eventmanagementapp.repository;

import java.util.List;
import com.mycompany.eventmanagementapp.model.ParticipantModel;

public interface ParticipantRepository {

	void addParticipant(ParticipantModel participant);

	void updateParticipant(ParticipantModel participant);

	void deleteParticipant(ParticipantModel participant);

	List<ParticipantModel> getAllParticipants();

	ParticipantModel getParticipantById(long participantId);

	ParticipantModel getParticipantByEmail(String email);
}