/**
 * Test class contain GUI Unit Tests for the Participant Management View Screen functionality.
 * 
 * This class contains GUI test cases for validating the behavior and functionality of the Participant Management view in the Event Management Application. It uses AssertJ-Swing for GUI testing, Mockito for mocking dependencies, and Awaitility for handling asynchronous operations. The tests focus on verifying UI interactions, controls' initial states, button enablement/disablement logic, and proper delegation of actions to the `ParticipantController` class.
 * 
 * Test cases include:
 * 1. Validating the initial state of UI controls (text fields, buttons, lists).
 * 2. Ensuring buttons (e.g., Add, Update, Delete) are enabled/disabled based on input conditions (empty or invalid fields).
 * 3. Checking if the appropriate actions are performed when adding, updating, or deleting participants.
 * 4. Verifying the correct population of participant and event data into the UI.
 * 5. Testing the reset of the UI after certain operations (e.g., adding or deleting a participant).
 * 6. Ensuring that when a participant is deleted or updated, the changes reflect properly in the participant list and the error label is reset.
 * 7. Ensuring that when the "Event Screen" or "Refresh" buttons are clicked, they trigger the appropriate actions.
 * 8. Testing interaction between the Participant Management screen and the Event Management screen.
 * 
 * 
 * Dependencies:
 * - ParticipantController: Controls the interactions between the view and the participant data model.
 * - EventManagementViewScreen: The screen that displays event-related information.
 * - ParticipantManagementViewScreen: The screen that handles participant management actions.
 * - EventModel, ParticipantModel: Data models representing events and participants.
 * 
 * The tests ensure that the view behaves correctly under various user actions and updates the UI in response to changes in the underlying data.
 */

package com.mycompany.eventmanagementapp.view.screen;

import org.junit.Test;
import org.mockito.Mock;
import java.time.LocalDate;
import org.junit.runner.RunWith;
import java.util.concurrent.TimeUnit;
import org.mockito.MockitoAnnotations;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import static org.awaitility.Awaitility.await;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.core.matcher.JButtonMatcher;
import static org.assertj.core.api.Assertions.assertThat;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;

import com.mycompany.eventmanagementapp.model.EventModel;
import com.mycompany.eventmanagementapp.model.ParticipantModel;
import com.mycompany.eventmanagementapp.controller.ParticipantController;

@RunWith(GUITestRunner.class)
public class ParticipantManagementViewScreenTest extends AssertJSwingJUnitTestCase {

	private ParticipantManagementViewScreen participantViewScreen;

	private FrameFixture window;

	@Mock
	private ParticipantController participantController;

	@Mock
	private EventManagementViewScreen eventViewScreen;

	private AutoCloseable closeable;

	private static final long EVENT_ID = 1;

	private static final String EVENT_NAME_1 = "Music Festival";

	private static final LocalDate EVENT_DATE_1 = LocalDate.now().plusDays(10);

	private static final String EVENT_LOCATION_1 = "Florence";

	private static final long EVENT_ID_2 = 2;

	private static final String EVENT_NAME_2 = "University Event";

	private static final LocalDate EVENT_DATE_2 = LocalDate.now().plusDays(20);

	private static final String EVENT_LOCATION_2 = "Milan";

	private static final String EVENT_INAVLID_ID = "InvalidId";

	private static final long PARTICIPANT_ID = 1;

	private static final String PARTICIPANT_NAME = "John";

	private static final String PARTICIPANT_EMAIL = "John@gmail.com";

	private static final long PARTICIPANT_ID_2 = 2;

	private static final String PARTICIPANT_NAME_2 = "Martin";

	private static final String PARTICIPANT_EMAIL_2 = "martin@gmail.com";

	private static final String BTN_ADD_PARTICIPANT = "Add Participant";

	private static final String BTN_UPDATE_PARTICIPANT = "Update Participant";

	private static final String BTN_DELETE_PARTICIPANT = "Delete Participant";

	private static final String BTN_EVENT_SCREEN = "Event Screen";

	private static final String BTN_REFRESH_SCREEN = "Refresh";

	private static final String LBL_PARTICIPANT_ID = "Participant ID:";

	private static final String LBL_EVENT_ID = "Event ID:";

	private static final String LBL_PARTICIPANT_NAME = "Participant Name:";

	private static final String LBL_PARTICIPANT_EMAIL = "Participant Email:";

	private static final String TXT_EVENT_ID = "txtEventId";

	private static final String TXT_PARTICIPANT_ID = "txtParticipantId";

	private static final String TXT_PARTICIPANT_NAME = "txtParticipantName";

	private static final String TXT_PARTICIPANT_EMAIL = "txtParticipantEmail";

	private static final String TXT_PARTICIPANT_ERROR = "lblError";

	private static final String LIST_PARTICIPANT = "participantList";

	private static final String LIST_EVENT = "eventListForParticipant";

	@Override
	protected void onSetUp() {
		closeable = MockitoAnnotations.openMocks(this);
		GuiActionRunner.execute(() -> {
			participantViewScreen = new ParticipantManagementViewScreen();
			participantViewScreen.setParticipantController(participantController);
			participantViewScreen.setEventView(eventViewScreen);
			return participantViewScreen;
		});
		window = new FrameFixture(robot(), participantViewScreen);
		window.show();
	}

	@Override
	protected void onTearDown() throws Exception {
		closeable.close();
	}

	// Test initial states of UI controls
	@Test
	@GUITest
	public void testControlsInitialStates() {
		// labels and Textboxes
		window.label(JLabelMatcher.withText(LBL_PARTICIPANT_ID)).requireVisible();
		window.textBox(TXT_PARTICIPANT_ID).requireVisible().requireNotEditable();
		// for EventId label and textbox
		window.label(JLabelMatcher.withText(LBL_EVENT_ID)).requireVisible();
		window.textBox(TXT_EVENT_ID).requireVisible().requireEnabled();
		// for ParticipantName label and textbox
		window.label(JLabelMatcher.withText(LBL_PARTICIPANT_NAME)).requireVisible();
		window.textBox(TXT_PARTICIPANT_NAME).requireVisible().requireEnabled();
		// for ParticipantEmail label and textbox
		window.label(JLabelMatcher.withText(LBL_PARTICIPANT_EMAIL)).requireVisible();
		window.textBox(TXT_PARTICIPANT_EMAIL).requireVisible().requireEnabled();

		// Participant & Event List
		window.list(LIST_PARTICIPANT).requireVisible();
		window.list(LIST_EVENT).requireVisible();

		// Buttons
		window.button(JButtonMatcher.withText(BTN_ADD_PARTICIPANT)).requireVisible().requireDisabled();
		window.button(JButtonMatcher.withText(BTN_UPDATE_PARTICIPANT)).requireVisible().requireDisabled();
		window.button(JButtonMatcher.withText(BTN_DELETE_PARTICIPANT)).requireVisible().requireDisabled();
		window.button(JButtonMatcher.withText(BTN_EVENT_SCREEN)).requireVisible().requireEnabled();
		window.button(JButtonMatcher.withText(BTN_REFRESH_SCREEN)).requireVisible().requireEnabled();

		// Error Text
		window.textBox(TXT_PARTICIPANT_ERROR).requireVisible().requireNotEditable().requireText(" ");
	}

	// Test Add Button is enabled when eventId is not empty
	@Test
	public void testWhenEventIdIsNonEmptyThenAddButtonShouldBeEnabled() {
		//Setup & Exercise
		setFieldValues(String.valueOf(EVENT_ID), PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		
		//Verify
		window.button(JButtonMatcher.withText(BTN_ADD_PARTICIPANT)).requireEnabled();
	}

	// Test Add Button is disabled when eventId is not empty but invalid
	// Integer,long
	@Test
	public void testWhenEventIdIsNonEmptyButInvalidIntegerThenAddButtonShouldBeDisabled() {
		//Setup & Exercise
		setFieldValues(EVENT_INAVLID_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		
		//Verify
		window.button(JButtonMatcher.withText(BTN_ADD_PARTICIPANT)).requireDisabled();
	}

	// Test Add Button is disabled when eventId is empty
	@Test
	public void testWhenEventIdIsEmptyThenAddButtonShouldBeDisabled() {
		//Setup & Exercise
		setFieldValues("", PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		
		//Verify
		window.button(JButtonMatcher.withText(BTN_ADD_PARTICIPANT)).requireDisabled();
	}

	// Test Add Button is enabled when participantName is not empty
	@Test
	public void testWhenParticipantNameIsNonEmptyThenAddButtonShouldBeEnabled() {
		//Setup & Exercise
		setFieldValues(String.valueOf(EVENT_ID), PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		
		//Verify
		window.button(JButtonMatcher.withText(BTN_ADD_PARTICIPANT)).requireEnabled();
	}

	// Test Add Button is disabled when participantName is empty
	@Test
	public void testWhenParticipantNameIsEmptyThenAddButtonShouldBeDisabled() {
		//Setup & Exercise
		setFieldValues(String.valueOf(EVENT_ID), "", PARTICIPANT_EMAIL);
		
		//Verify
		window.button(JButtonMatcher.withText(BTN_ADD_PARTICIPANT)).requireDisabled();
	}

	// Test Add Button is enabled when participantEmail is not empty
	@Test
	public void testWhenParticipantEmailIsNonEmptyThenAddButtonShouldBeEnabled() {
		//Setup & Exercise
		setFieldValues(String.valueOf(EVENT_ID), PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		
		//Verify
		window.button(JButtonMatcher.withText(BTN_ADD_PARTICIPANT)).requireEnabled();
	}

	// Test Add Button is disabled when participantEmail is empty
	@Test
	public void testWhenEventDateIsEmptyThenAddButtonShouldBeDisabled() {
		//Setup & Exercise
		setFieldValues(String.valueOf(EVENT_ID), PARTICIPANT_NAME, "");
		
		//Verify
		window.button(JButtonMatcher.withText(BTN_ADD_PARTICIPANT)).requireDisabled();
	}

	// Test Delete button disabled when participant is selected but its linked event
	// is not selected
	@Test
	public void testDeleteButtonDisabledWhenParticipantSelectedButEventIsNotSelected() {
		//Setup
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		participant.addEvent(event);
		GuiActionRunner.execute(() -> participantViewScreen.participantAdded(participant));

		//Exercise
		window.list(LIST_PARTICIPANT).selectItem(0);

		//Verify
		JButtonFixture deleteButton = window.button(JButtonMatcher.withText(BTN_DELETE_PARTICIPANT));
		deleteButton.requireDisabled();
	}

	// Test populating text fields when participant is selected from participant
	// List
	@Test
	public void testPopulatingFieldsWhenParticipantIsSelected() {
		//Setup
		GuiActionRunner.execute(() -> participantViewScreen.getParticipantListModel()
				.addElement(new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL)));

		//Exercise
		window.list(LIST_PARTICIPANT).selectItem(0);

		//Verify
		window.textBox(TXT_PARTICIPANT_ID).requireText(String.valueOf(PARTICIPANT_ID));
		window.textBox(TXT_PARTICIPANT_NAME).requireText(PARTICIPANT_NAME);
		window.textBox(TXT_PARTICIPANT_EMAIL).requireText(PARTICIPANT_EMAIL);
	}

	// Test Update Button is disabled when participantName is empty
	@Test
	public void testWhenParticipantNameIsBlankThenUpdateButtonShouldBeDisabled() {
		//Setup
		GuiActionRunner.execute(() -> participantViewScreen.getParticipantListModel()
				.addElement(new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL)));
		
		//Exercise & Verify
		window.list(LIST_PARTICIPANT).selectItem(0);

		JButtonFixture updateButton = window.button(JButtonMatcher.withText(BTN_UPDATE_PARTICIPANT));
		updateButton.requireEnabled();
		window.textBox(TXT_PARTICIPANT_NAME).setText("");
		window.textBox(TXT_PARTICIPANT_NAME).enterText("  ");
		updateButton.requireDisabled();
	}

	// Test Update Button is disabled when participantEmail is empty
	@Test
	public void testWhenParticipantEmailIsBlankThenUpdateButtonShouldBeDisabled() {
		//Setup
		GuiActionRunner.execute(() -> participantViewScreen.getParticipantListModel()
				.addElement(new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL)));
		
		//Exercise & Verify
		window.list(LIST_PARTICIPANT).selectItem(0);

		JButtonFixture updateButton = window.button(JButtonMatcher.withText(BTN_UPDATE_PARTICIPANT));
		updateButton.requireEnabled();
		window.textBox(TXT_PARTICIPANT_EMAIL).setText("");
		window.textBox(TXT_PARTICIPANT_EMAIL).enterText("  ");
		updateButton.requireDisabled();
	}

	// Test Update Button is enabled when participant List is selected
	@Test
	public void testUpdateButtonEnabledWhenParticipantListIsSelected() {
		//Setup
		GuiActionRunner.execute(() -> participantViewScreen.getParticipantListModel()
				.addElement(new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL)));

		//Exercise
		window.list(LIST_PARTICIPANT).selectItem(0);

		//Verify
		JButtonFixture updateButton = window.button(JButtonMatcher.withText(BTN_UPDATE_PARTICIPANT));
		updateButton.requireEnabled();
	}

	// Test when Event Screen opens when "Event Screen" button is clicked
	@Test
	public void testEventScreenButtonShouldOpenEventScreen() {
		//Setup & Exercise
		window.button(JButtonMatcher.withText(BTN_EVENT_SCREEN)).click();
		
		//Verify
		verify(eventViewScreen).setVisible(true);
	}

	// Test displaying all events in the Event list when no participant is selected
	@Test
	public void testShowAllEventsToTheEventListWhenNoParticipantIsSelected() {
		//Setup
		EventModel event1 = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		EventModel event2 = new EventModel(EVENT_ID_2, EVENT_NAME_2, EVENT_DATE_2, EVENT_LOCATION_2);

		//Exercise
		GuiActionRunner.execute(() -> participantViewScreen.showAllEvents(asList(event1, event2)));

		//Verify
		assertThat(window.list(LIST_EVENT).contents()).containsExactly(getDisplayString(event1),
				getDisplayString(event2));
	}

	// Test displaying all participants in the Participant list which are added
	@Test
	public void testShowAllParticipantToTheParticipantListWithProperDescription() {
		//Setup
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);

		//Exercise
		GuiActionRunner.execute(() -> participantViewScreen.showAllParticipants(asList(participant)));

		//Verify
		assertThat(window.list(LIST_PARTICIPANT).contents()).containsExactly(getDisplayString(participant));
	}

	// Test when showError is called then it should show message on screen.
	@Test
	public void testShowErrorShouldShowTheMessageInErrorSection() {
		//Setup
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);

		//Exercise
		participantViewScreen.showError("Error Occurred", participant);

		//Verify
		window.textBox(TXT_PARTICIPANT_ERROR).requireText("Error Occurred: " + participant);
	}

	// Test adding participant to the event and resetting error label
	@Test
	public void testParticipantAddedShouldAddParticipantToTheListAndResetTheErrorLabel() {
		//Setup
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		participant.addEvent(event);

		//Exercise
		participantViewScreen.participantAdded(participant);

		//Verify
		assertThat(window.list(LIST_PARTICIPANT).contents()).containsExactly(getDisplayString(participant));
		window.textBox(TXT_PARTICIPANT_ERROR).requireText(" ");
	}

	// Test when participant is added then screen should reset
	@Test
	public void testWhenParticipantAddedThenScreenShouldReset() {
		//Setup
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);

		//Exercise
		participantViewScreen.participantAdded(participant);

		//Verify
		await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> window.textBox(TXT_PARTICIPANT_NAME).requireText(""));
		window.textBox(TXT_EVENT_ID).requireText("");
		window.textBox(TXT_PARTICIPANT_ID).requireText("");
		window.textBox(TXT_PARTICIPANT_EMAIL).requireText("");
		window.button(JButtonMatcher.withText(BTN_UPDATE_PARTICIPANT)).requireDisabled();
		window.button(JButtonMatcher.withText(BTN_DELETE_PARTICIPANT)).requireDisabled();
		window.button(JButtonMatcher.withText(BTN_ADD_PARTICIPANT)).requireDisabled();
		window.list(LIST_PARTICIPANT).requireNoSelection();
		// This will get called by participantAdded and when window is activated, so in
		// total 2 times.
		verify(participantController, times(2)).getAllEvents();
	}

	// Test when participant is deleted from the event and is not a part of anymore
	// events then it should be removed from Participant List and error label should
	// be reset
	@Test
	public void testParticipantDeleteFromLastEventShouldRemoveTheParticipantFromTheListAndResetTheErrorLabel() {
		//Setup
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		participant.addEvent(event);

		//Exercise
		GuiActionRunner.execute(() -> {
			participantViewScreen.participantAdded(participant);
		});
		participantViewScreen.participantDeleted(participant);

		//Verify
		assertThat(window.list(LIST_PARTICIPANT).contents()).isEmpty();
		window.textBox(TXT_PARTICIPANT_ERROR).requireText(" ");
	}

	// Test when participant is deleted then screen should reset
	@Test
	public void testWhenParticipantDeletedThenScreenShouldReset() {
		//Setup
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		participant.addEvent(event);

		//Exercise
		GuiActionRunner.execute(() -> {
			participantViewScreen.participantAdded(participant);
		});
		participantViewScreen.participantDeleted(participant);

		//Verify
		await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> window.textBox(TXT_PARTICIPANT_NAME).requireText(""));
		window.textBox(TXT_EVENT_ID).requireText("");
		window.textBox(TXT_PARTICIPANT_ID).requireText("");
		window.textBox(TXT_PARTICIPANT_EMAIL).requireText("");
		window.button(JButtonMatcher.withText(BTN_UPDATE_PARTICIPANT)).requireDisabled();
		window.button(JButtonMatcher.withText(BTN_DELETE_PARTICIPANT)).requireDisabled();
		window.button(JButtonMatcher.withText(BTN_ADD_PARTICIPANT)).requireDisabled();
		window.list(LIST_PARTICIPANT).requireNoSelection();
		// This will get called by participantAdded, participantDeleted and one time
		// when window is activated, so in total 3 times.
		verify(participantController, times(3)).getAllEvents();
	}

	// Test when participant is deleted from the event but is a part of more than 1
	// event then it should not be removed from Participant List.
	@Test
	public void testParticipantDeleteFromEventWhenItIsPartOfMultipleEventsThenParticipantShouldNotBeRemoved() {
		//Setup
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		EventModel event1 = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		EventModel event2 = new EventModel(EVENT_ID_2, EVENT_NAME_2, EVENT_DATE_2, EVENT_LOCATION_2);
		participant.addEvent(event1);
		participant.addEvent(event2);

		//Exercise
		GuiActionRunner.execute(() -> {
			participantViewScreen.participantAdded(participant);
			participant.removeEvent(event2);
		});
		participantViewScreen.participantUpdated(participant);

		//Verify
		assertThat(window.list(LIST_PARTICIPANT).contents()).containsOnly(getDisplayString(participant));
	}

	// Test when participant is deleted from the event but is a part of more than 1
	// event then event List should be updated for that specific participant
	@Test
	public void testParticipantDeleteFromEventWhenItIsPartOfMultipleEventsThenEventListShouldBeUpdated() {
		//Setup
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		EventModel event1 = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		EventModel event2 = new EventModel(EVENT_ID_2, EVENT_NAME_2, EVENT_DATE_2, EVENT_LOCATION_2);
		participant.addEvent(event1);
		participant.addEvent(event2);

		//Exercise
		GuiActionRunner.execute(() -> {
			participantViewScreen.participantAdded(participant);
			participant.removeEvent(event2);
		});
		participantViewScreen.participantUpdated(participant);
		window.list(LIST_PARTICIPANT).clickItem(0);

		//Verify
		assertThat(window.list(LIST_EVENT).contents()).containsOnly(getDisplayString(event1));
	}

	// Test updating event in the list and resetting the error label
	@Test
	public void testParticipantUpdateShouldUpdateTheParticipantFromTheListAndResetTheErrorLabel() {
		//Setup
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		participant.addEvent(event);

		//Exercise
		participantViewScreen.participantAdded(participant);
		participant.setParticipantName(PARTICIPANT_NAME_2);
		participantViewScreen.participantUpdated(participant);

		//Verify
		assertThat(window.list(LIST_PARTICIPANT).contents()).containsExactly(getDisplayString(participant));
		window.textBox(TXT_PARTICIPANT_ERROR).requireText(" ");
	}

	// Test when participant is updated then screen should reset
	@Test
	public void testWhenParticipantUpdatedThenScreenShouldReset() {
		//Setup
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		participant.addEvent(event);

		//Exercise
		participantViewScreen.participantAdded(participant);
		participant.setParticipantName(PARTICIPANT_NAME_2); // updated participant Name
		participantViewScreen.participantUpdated(participant);

		//Verify
		await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> window.textBox(TXT_PARTICIPANT_NAME).requireText(""));
		window.textBox(TXT_EVENT_ID).requireText("");
		window.textBox(TXT_PARTICIPANT_ID).requireText("");
		window.textBox(TXT_PARTICIPANT_EMAIL).requireText("");
		window.button(JButtonMatcher.withText(BTN_UPDATE_PARTICIPANT)).requireDisabled();
		window.button(JButtonMatcher.withText(BTN_DELETE_PARTICIPANT)).requireDisabled();
		window.button(JButtonMatcher.withText(BTN_ADD_PARTICIPANT)).requireDisabled();
		window.list(LIST_PARTICIPANT).requireNoSelection();
		// This will get called by participantAdded, participantUpdated and one time
		// when window is activated, so in total 3 times.
		verify(participantController, times(3)).getAllEvents();
	}

	// Test participant is not updated when it is not found
	@Test
	public void testParticipantUpdateShouldNotUpdateWhenParticipantNotFound() {
		//Setup
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		ParticipantModel updatedParticipant = new ParticipantModel(PARTICIPANT_ID_2, PARTICIPANT_NAME_2,
				PARTICIPANT_EMAIL_2);
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		participant.addEvent(event);
		updatedParticipant.addEvent(event);

		//Exercise
		participantViewScreen.participantAdded(participant);
		participantViewScreen.participantUpdated(updatedParticipant);

		//Verify
		assertThat(window.list(LIST_PARTICIPANT).contents()).containsExactly(getDisplayString(participant));
	}

	// Test Add Participant Button is calling controller method
	@Test
	public void testAddParticipantButtonShouldDelegateToParticipantControllerAddParticipant() {
		//Setup
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		EventModel event = new EventModel();
		event.setEventId(EVENT_ID);
		setFieldValues(String.valueOf(EVENT_ID), PARTICIPANT_NAME, PARTICIPANT_EMAIL);

		//Exercise
		window.button(JButtonMatcher.withText(BTN_ADD_PARTICIPANT)).click();

		//Verify
		await().atMost(10, TimeUnit.SECONDS)
				.untilAsserted(() -> verify(participantController).addParticipant(participant, event));
	}

	// Test Delete Participant Button is calling controller method
	@Test
	public void testDeleteParticipantButtonShouldDelegateToParticipantControllerDeleteParticipant() {
		//Setup
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		participant.addEvent(event);

		//Exercise
		GuiActionRunner.execute(() -> {
			participantViewScreen.participantAdded(participant);
		});
		window.list(LIST_PARTICIPANT).selectItem(0);
		window.list(LIST_EVENT).selectItem(0);
		window.button(JButtonMatcher.withText(BTN_DELETE_PARTICIPANT)).click();

		//Verify
		await().atMost(10, TimeUnit.SECONDS)
				.untilAsserted(() -> verify(participantController).deleteParticipant(participant, event));
	}

	// Test Update Participant Button is calling controller method
	@Test
	public void testUpdateParticipantButtonShouldDelegateToParticipantControllerUpdateParticipant() {
		//Setup
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		participant.addEvent(event);

		//Exercise
		GuiActionRunner.execute(() -> {
			participantViewScreen.participantAdded(participant);
		});
		window.list(LIST_PARTICIPANT).selectItem(0);
		window.textBox(TXT_PARTICIPANT_NAME).setText(PARTICIPANT_NAME_2);
		window.button(JButtonMatcher.withText(BTN_UPDATE_PARTICIPANT)).click();
		participant.setParticipantName(PARTICIPANT_NAME_2);

		//Verify
		await().atMost(10, TimeUnit.SECONDS)
				.untilAsserted(() -> verify(participantController).updateParticipant(participant));
	}

	// Test when Participant is selected from List it should auto populate text
	// fields and enable Update Button
	@Test
	public void testParticipantSelectionFromListShouldPopulateFieldsAndEnableUpdateButton() {
		//Setup
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		participant.addEvent(event);

		//Exercise
		GuiActionRunner.execute(() -> {
			participantViewScreen.participantAdded(participant);
		});
		window.list(LIST_PARTICIPANT).selectItem(0);

		//Verify
		await().atMost(10, TimeUnit.SECONDS)
				.untilAsserted(() -> window.textBox(TXT_PARTICIPANT_NAME).requireText(PARTICIPANT_NAME));
		window.textBox(TXT_PARTICIPANT_ID).requireText(String.valueOf(PARTICIPANT_ID));
		window.textBox(TXT_PARTICIPANT_NAME).requireText(PARTICIPANT_NAME);
		window.textBox(TXT_PARTICIPANT_EMAIL).requireText(PARTICIPANT_EMAIL);
		window.button(JButtonMatcher.withText(BTN_UPDATE_PARTICIPANT)).requireEnabled();
	}

	// Test when Participant and Event is selected from List then it should enable
	// Delete Button
	@Test
	public void testParticipantAndSelectionFromListShouldEnableDeleteButton() {
		//Setup
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		participant.addEvent(event);

		//Exercise
		GuiActionRunner.execute(() -> {
			participantViewScreen.participantAdded(participant);
		});
		window.list(LIST_PARTICIPANT).selectItem(0);
		window.list(LIST_EVENT).selectItem(0);

		//Verify
		window.button(JButtonMatcher.withText(BTN_DELETE_PARTICIPANT)).requireEnabled();
	}

	// Test when Participant is selected but Event is deSelected from List then it
	// should disable Delete Button
	@Test
	public void testDeleteButtonDisabledWhenParticipantSelectedButEventDeselectedFromList() {
		//Setup
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		participant.addEvent(event);

		//Exercise
		GuiActionRunner.execute(() -> {
			participantViewScreen.participantAdded(participant);
		});
		window.list(LIST_PARTICIPANT).selectItem(0);
		window.list(LIST_EVENT).selectItem(0);
		window.list(LIST_EVENT).clearSelection();

		//Verify
		window.button(JButtonMatcher.withText(BTN_DELETE_PARTICIPANT)).requireDisabled();
	}
	
	// Test when Participant and Event are deSelected from List then it
	// should disable Delete Button
	@Test
	public void testDeleteButtonDisabledWhenParticipantAndEventDeselectedFromList() {
		//Setup
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		participant.addEvent(event);

		//Exercise
		GuiActionRunner.execute(() -> {
			participantViewScreen.participantAdded(participant);
		});
		window.list(LIST_PARTICIPANT).selectItem(0);
		window.list(LIST_EVENT).selectItem(0);
		window.list(LIST_PARTICIPANT).clearSelection();
		window.list(LIST_EVENT).clearSelection();

		//Verify
		window.button(JButtonMatcher.withText(BTN_DELETE_PARTICIPANT)).requireDisabled();
	}

	// Test when Participant is deselected from List then it should disable Update
	// Button
	@Test
	public void testParticipantSelectionFromListShouldDisableUpdateButton() {
		//Setup
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		participant.addEvent(event);

		//Exercise
		GuiActionRunner.execute(() -> {
			participantViewScreen.participantAdded(participant);
		});
		window.list(LIST_PARTICIPANT).selectItem(0);
		window.list(LIST_PARTICIPANT).clearSelection();

		//Verify
		window.button(JButtonMatcher.withText(BTN_UPDATE_PARTICIPANT)).requireDisabled();
	}

	// Test when Refresh button is clicked then Participant Screen should fully
	// reset
	@Test
	public void testWhenRefreshButtonClickedThenParticipantScreenShouldFullyReset() {
		//Setup
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		participant.addEvent(event);

		//Exercise
		GuiActionRunner.execute(() -> {
			participantViewScreen.participantAdded(participant);
		});
		window.list(LIST_PARTICIPANT).selectItem(0);
		window.list(LIST_EVENT).selectItem(0);
		window.button(JButtonMatcher.withText(BTN_REFRESH_SCREEN)).click();

		//Verify
		await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> window.textBox(TXT_PARTICIPANT_NAME).requireText(""));
		window.textBox(TXT_EVENT_ID).requireText("");
		window.textBox(TXT_PARTICIPANT_ID).requireText("");
		window.textBox(TXT_PARTICIPANT_NAME).requireText("");
		window.textBox(TXT_PARTICIPANT_EMAIL).requireText("");
		window.button(JButtonMatcher.withText(BTN_UPDATE_PARTICIPANT)).requireDisabled();
		window.button(JButtonMatcher.withText(BTN_DELETE_PARTICIPANT)).requireDisabled();
		window.button(JButtonMatcher.withText(BTN_ADD_PARTICIPANT)).requireDisabled();
		// This will get called by participantAdded, one time when window is activated
		// and also when Refresh button is clicked, so in total 3 times.
		verify(participantController, times(3)).getAllEvents();
	}

	// Test update button is enabled when event is selected and updated values are
	// entered in text fields
	@Test
	public void testUpdateButtonEnabledWhenParticipantListIsSelectedAndUpdatedValuesAreReEntered() {
		//Setup
		GuiActionRunner.execute(() -> participantViewScreen.getParticipantListModel()
				.addElement(new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL)));

		//Exercise
		window.list(LIST_PARTICIPANT).selectItem(0);
		window.textBox(TXT_PARTICIPANT_NAME).setText("");
		window.textBox(TXT_PARTICIPANT_EMAIL).setText("");
		window.textBox(TXT_PARTICIPANT_NAME).enterText(PARTICIPANT_NAME);
		window.textBox(TXT_PARTICIPANT_EMAIL).enterText(PARTICIPANT_EMAIL);

		//Verify
		JButtonFixture updateButton = window.button(JButtonMatcher.withText(BTN_UPDATE_PARTICIPANT));
		updateButton.requireEnabled();
	}

	// Helper Methods
	private void setFieldValues(String eventId, String participantName, String participantEmail) {
		window.textBox(TXT_EVENT_ID).enterText(eventId);
		window.textBox(TXT_PARTICIPANT_NAME).enterText(participantName);
		window.textBox(TXT_PARTICIPANT_EMAIL).enterText(participantEmail);
	}

	private String getDisplayString(ParticipantModel participant) {
		return "Participant [" + participant.getParticipantId() + ", " + participant.getParticipantName() + ", "
				+ participant.getParticipantEmail() + "]";
	}

	private String getDisplayString(EventModel event) {
		return "Event [" + event.getEventId() + ", " + event.getEventName() + ", " + event.getEventLocation() + ", "
				+ event.getEventDate() + "]";
	}
}
