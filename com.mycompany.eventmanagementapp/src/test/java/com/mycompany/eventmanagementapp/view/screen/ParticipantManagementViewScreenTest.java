package com.mycompany.eventmanagementapp.view.screen;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.awt.Color;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.mycompany.eventmanagementapp.controller.EventController;
import com.mycompany.eventmanagementapp.controller.ParticipantController;
import com.mycompany.eventmanagementapp.view.EventManagementView;
import com.mycompany.eventmanagementapp.model.EventModel;
import com.mycompany.eventmanagementapp.model.ParticipantModel;

public class ParticipantManagementViewScreenTest extends AssertJSwingJUnitTestCase {

	private ParticipantManagementViewScreen participantViewScreen;
	private FrameFixture window;

	@Mock
	private ParticipantController participantController;

	@Mock
	EventManagementViewScreen eventViewScreen;

	/*
	 * @Mock private EventManagementViewScreen eventViewScreen;
	 */

	private AutoCloseable closeable;

	private static final long EVENT_ID = 1;
	private static final String EVENT_NAME_1 = "Music Festival";
	private static final LocalDate EVENT_DATE_1 = LocalDate.now().plusDays(10);
	private static final String EVENT_LOCATION_1 = "Florence";
	private static final long EVENT_ID_2 = 2;
	private static final String EVENT_NAME_2 = "University Event";
	private static final LocalDate EVENT_DATE_2 = LocalDate.now().plusDays(20);
	private static final String EVENT_LOCATION_2 = "Milan";
	private static final String EVENT_DATE_INVALID = "2027-04";
	private static final String EVENT_INAVLID_ID = "InvalidId";

	private static final long PARTICIPANT_ID = 1;
	private static final String PARTICIPANT_NAME = "John";
	private static final String PARTICIPANT_EMAIL = "John@gmail.com";
	private static final String PARTICIPANT_INVALID_EMAIL = "John@gmail";
	private static final long PARTICIPANT_ID_2 = 2;
	private static final String PARTICIPANT_NAME_2 = "Martin";
	private static final String PARTICIPANT_EMAIL_2 = "martin@gmail.com";
	private static final String PARTICIPANT_INAVLID_ID = "InvalidId";

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
		// for ParticipantId label and textbox
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
		window.list(LIST_PARTICIPANT);
		window.list(LIST_EVENT);

		// Buttons
		window.button(JButtonMatcher.withText(BTN_ADD_PARTICIPANT)).requireVisible().requireDisabled();
		window.button(JButtonMatcher.withText(BTN_UPDATE_PARTICIPANT)).requireVisible().requireDisabled();
		window.button(JButtonMatcher.withText(BTN_DELETE_PARTICIPANT)).requireVisible().requireDisabled();
		window.button(JButtonMatcher.withText(BTN_EVENT_SCREEN)).requireVisible().requireEnabled();

		// Error Text
		window.textBox(TXT_PARTICIPANT_ERROR).requireVisible().requireNotEditable().requireText(" ");
	}

	// Test Add Button is enabled when eventId is not empty
	@Test
	public void testWhenEventIdIsNonEmptyThenAddButtonShouldBeEnabled() {
		setFieldValues(String.valueOf(EVENT_ID), PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		window.button(JButtonMatcher.withText(BTN_ADD_PARTICIPANT)).requireEnabled();
	}

	// Test Add Button is disabled when eventId is not empty but invalid
	// Integer/long
	@Test
	public void testWhenEventIdIsNonEmptyButInvalidIntegerThenAddButtonShouldBeDisabled() {
		setFieldValues(EVENT_INAVLID_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		window.button(JButtonMatcher.withText(BTN_ADD_PARTICIPANT)).requireDisabled();
	}

	// Test Add Button is disabled when eventId is empty
	@Test
	public void testWhenEventIdIsEmptyThenAddButtonShouldBeDisabled() {
		setFieldValues("", PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		window.button(JButtonMatcher.withText(BTN_ADD_PARTICIPANT)).requireDisabled();
	}

	// Test Add Button is enabled when participantName is not empty
	@Test
	public void testWhenParticipantNameIsNonEmptyThenAddButtonShouldBeEnabled() {
		setFieldValues(String.valueOf(EVENT_ID), PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		window.button(JButtonMatcher.withText(BTN_ADD_PARTICIPANT)).requireEnabled();
	}

	// Test Add Button is disabled when participantName is empty
	@Test
	public void testWhenParticipantNameIsEmptyThenAddButtonShouldBeDisabled() {
		setFieldValues(String.valueOf(EVENT_ID), "", PARTICIPANT_EMAIL);
		window.button(JButtonMatcher.withText(BTN_ADD_PARTICIPANT)).requireDisabled();
	}

	// Test Add Button is enabled when participantEmail is not empty
	@Test
	public void testWhenParticipantEmailIsNonEmptyThenAddButtonShouldBeEnabled() {
		setFieldValues(String.valueOf(EVENT_ID), PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		window.button(JButtonMatcher.withText(BTN_ADD_PARTICIPANT)).requireEnabled();
	}

	// Test Add Button is disabled when participantEmail is empty
	@Test
	public void testWhenEventDateIsEmptyThenAddButtonShouldBeDisabled() {
		setFieldValues(String.valueOf(EVENT_ID), PARTICIPANT_NAME, "");
		window.button(JButtonMatcher.withText(BTN_ADD_PARTICIPANT)).requireDisabled();
	}

	// Test Delete button disabled when participant is selected but its linked event
	// is not selected
	@Test
	public void testDeleteButtonDisabledWhenBothParticipantAndEventIsNotSelected() {
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		participant.addEvent(event);

		GuiActionRunner.execute(() -> participantViewScreen.participantAdded(participant));

		window.list(LIST_PARTICIPANT).selectItem(0);

		JButtonFixture deleteButton = window.button(JButtonMatcher.withText(BTN_DELETE_PARTICIPANT));
		deleteButton.requireDisabled();
	}

	// Test populating text fields when participant is selected from participant
	// List
	@Test
	public void testPopulatingFieldsWhenParticipantIsSelected() {
		GuiActionRunner.execute(() -> participantViewScreen.getParticipantListModel()
				.addElement(new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL)));
		window.list(LIST_PARTICIPANT).selectItem(0);

		window.textBox(TXT_PARTICIPANT_ID).requireText(String.valueOf(PARTICIPANT_ID));
		window.textBox(TXT_PARTICIPANT_NAME).requireText(PARTICIPANT_NAME);
		window.textBox(TXT_PARTICIPANT_EMAIL).requireText(PARTICIPANT_EMAIL);
	}

	// Test Update Button is disabled when participantName is empty
	@Test
	public void testWhenParticipantNameIsBlankThenUpdateButtonShouldBeDisabled() {
		GuiActionRunner.execute(() -> participantViewScreen.getParticipantListModel()
				.addElement(new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL)));
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
		GuiActionRunner.execute(() -> participantViewScreen.getParticipantListModel()
				.addElement(new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL)));
		window.list(LIST_PARTICIPANT).selectItem(0);

		JButtonFixture updateButton = window.button(JButtonMatcher.withText(BTN_UPDATE_PARTICIPANT));
		updateButton.requireEnabled();
		window.textBox(TXT_PARTICIPANT_EMAIL).setText("");
		window.textBox(TXT_PARTICIPANT_EMAIL).enterText("  ");
		updateButton.requireDisabled();
	}

	// Test update button is disabled when participant is De-selected 
	@Test
	public void testUpdateButtonDisabledWhenParticipantListIsDeSelected() {
		GuiActionRunner.execute(() -> participantViewScreen.getParticipantListModel()
				.addElement(new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL)));
		
		window.list(LIST_PARTICIPANT).selectItem(0);
		window.list(LIST_PARTICIPANT).clearSelection();
		setFieldValues("", PARTICIPANT_NAME, PARTICIPANT_EMAIL);

		JButtonFixture updateButton = window.button(JButtonMatcher.withText(BTN_UPDATE_PARTICIPANT));
		updateButton.requireDisabled();
	} 
	@Test
	public void testUpdateButtonEnabledWhenParticipantListIsSelected() {
		GuiActionRunner.execute(() -> participantViewScreen.getParticipantListModel()
				.addElement(new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL)));
		
		window.list(LIST_PARTICIPANT).selectItem(0);
		setFieldValues("", PARTICIPANT_NAME, PARTICIPANT_EMAIL);

		JButtonFixture updateButton = window.button(JButtonMatcher.withText(BTN_UPDATE_PARTICIPANT));
		updateButton.requireEnabled();
	}

	// Test when Event Screen opens when "Event Screen" button is
	// clicked
	@Test
	public void testEventScreenButtonShouldOpenEventScreen() {
		window.button(JButtonMatcher.withText(BTN_EVENT_SCREEN)).click();
		verify(eventViewScreen).setVisible(true);
	}

	// Test displaying all events in the Event list when no participant is selected
	@Test
	public void testShowAllEventsToTheEventListWhenNoParticipantIsSelected() {
		EventModel event1 = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		EventModel event2 = new EventModel(EVENT_ID_2, EVENT_NAME_2, EVENT_DATE_2, EVENT_LOCATION_2);
		GuiActionRunner.execute(() -> participantViewScreen.showAllEvents(asList(event1, event2)));
		assertThat(window.list(LIST_EVENT).contents()).containsExactly(getDisplayString(event1),
				getDisplayString(event2));
	}

	// Test displaying all participants in the Participant list which are added
	@Test
	public void testShowAllParticipantToTheParticipantListWithProperDescription() {
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);

		GuiActionRunner.execute(() -> participantViewScreen.showAllParticipants(asList(participant)));

		assertThat(window.list(LIST_PARTICIPANT).contents()).containsExactly(getDisplayString(participant));
	}

	// Test when showError is called then it should show message on screen.
	@Test
	public void testShowErrorShouldShowTheMessageInErrorSection() {
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);

		participantViewScreen.showError("Error Occurred", participant);

		window.textBox(TXT_PARTICIPANT_ERROR).requireText("Error Occurred: " + participant);
	}

	// Test adding participant to the event and resetting error label
	@Test
	public void testParticipantAddedShouldAddParticipantToTheListAndResetTheErrorLabel() {
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		participant.addEvent(event);

		participantViewScreen.participantAdded(participant);

		assertThat(window.list(LIST_PARTICIPANT).contents()).containsExactly(getDisplayString(participant));
		window.textBox(TXT_PARTICIPANT_ERROR).requireText(" ");
	}

	// Test when participant is added then screen should reset
	@Test
	public void testWhenParticipantAddedThenScreenShouldReset() {
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);

		participantViewScreen.participantAdded(participant);

		await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> window.textBox(TXT_PARTICIPANT_NAME).requireText(""));
		window.textBox(TXT_EVENT_ID).requireText("");
		window.textBox(TXT_PARTICIPANT_ID).requireText("");
		window.textBox(TXT_PARTICIPANT_NAME).requireText("");
		window.textBox(TXT_PARTICIPANT_EMAIL).requireText("");
		window.button(JButtonMatcher.withText(BTN_UPDATE_PARTICIPANT)).requireDisabled();
		window.button(JButtonMatcher.withText(BTN_DELETE_PARTICIPANT)).requireDisabled();
		window.button(JButtonMatcher.withText(BTN_ADD_PARTICIPANT)).requireDisabled();
		window.list(LIST_PARTICIPANT).requireNoSelection();
		verify(participantController, times(2)).getAllEvents(); // This will get called by participantAdded and when
																// window is activated, so in total 2 times.
	}

	// Test when participant is deleted from the event and is not a part of anymore
	// events
	// then it should be removed from Participant List and error label should be
	// reset
	@Test
	public void testParticipantDeleteFromLastEventShouldRemoveTheParticipantFromTheListAndResetTheErrorLabel() {
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		participant.addEvent(event);

		GuiActionRunner.execute(() -> {
			participantViewScreen.participantAdded(participant);
		});
		participantViewScreen.participantDeleted(participant);

		assertThat(window.list(LIST_PARTICIPANT).contents()).isEmpty();
		window.textBox(TXT_PARTICIPANT_ERROR).requireText(" ");
	}

	// Test when participant is deleted then screen should reset
	@Test
	public void testWhenParticipantDeletedThenScreenShouldReset() {
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		participant.addEvent(event);

		GuiActionRunner.execute(() -> {
			participantViewScreen.participantAdded(participant);
		});
		participantViewScreen.participantDeleted(participant);

		await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> window.textBox(TXT_PARTICIPANT_NAME).requireText(""));
		window.textBox(TXT_EVENT_ID).requireText("");
		window.textBox(TXT_PARTICIPANT_ID).requireText("");
		window.textBox(TXT_PARTICIPANT_NAME).requireText("");
		window.textBox(TXT_PARTICIPANT_EMAIL).requireText("");
		window.button(JButtonMatcher.withText(BTN_UPDATE_PARTICIPANT)).requireDisabled();
		window.button(JButtonMatcher.withText(BTN_DELETE_PARTICIPANT)).requireDisabled();
		window.button(JButtonMatcher.withText(BTN_ADD_PARTICIPANT)).requireDisabled();
		window.list(LIST_PARTICIPANT).requireNoSelection();
		verify(participantController, times(3)).getAllEvents(); // This will get called by participantAdded,
																// participantDeleted and one time when window is
																// activated, so in total 3 times.
	}

	// Test when participant is deleted from the event but is a part of more than 1
	// event
	// then it should not be removed from Participant List.
	@Test
	public void testParticipantDeleteFromEventWhenItIsPartOfMultipleEventsThenParticipantShouldNotBeRemoved() {
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		EventModel event1 = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		EventModel event2 = new EventModel(EVENT_ID_2, EVENT_NAME_2, EVENT_DATE_2, EVENT_LOCATION_2);
		participant.addEvent(event1);
		participant.addEvent(event2);

		GuiActionRunner.execute(() -> {
			participantViewScreen.participantAdded(participant);
			participant.removeEvent(event2);
		});
		participantViewScreen.participantUpdated(participant);

		assertThat(window.list(LIST_PARTICIPANT).contents()).containsOnly(getDisplayString(participant));
	}

	// Test when participant is deleted from the event but is a part of more than 1
	// event
	// then event List should be updated for that specific participant
	@Test
	public void testParticipantDeleteFromEventWhenItIsPartOfMultipleEventsThenEventListShouldBeUpdated() {
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		EventModel event1 = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		EventModel event2 = new EventModel(EVENT_ID_2, EVENT_NAME_2, EVENT_DATE_2, EVENT_LOCATION_2);
		participant.addEvent(event1);
		participant.addEvent(event2);

		GuiActionRunner.execute(() -> {
			participantViewScreen.participantAdded(participant);
			participant.removeEvent(event2);
		});
		participantViewScreen.participantUpdated(participant);
		window.list(LIST_PARTICIPANT).clickItem(0);

		assertThat(window.list(LIST_EVENT).contents()).containsOnly(getDisplayString(event1));
	}

	// Test updating event in the list and resetting the error label
	@Test
	public void testParticipantUpdateShouldUpdateTheParticipantFromTheListAndResetTheErrorLabel() {
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		participant.addEvent(event);

		participantViewScreen.participantAdded(participant);
		participant.setParticipantName(PARTICIPANT_NAME_2); // updated participant Name
		participantViewScreen.participantUpdated(participant);

		assertThat(window.list(LIST_PARTICIPANT).contents()).containsExactly(getDisplayString(participant));
		window.textBox(TXT_PARTICIPANT_ERROR).requireText(" ");
	}

	// Test when participant is updated then screen should reset
	@Test
	public void testWhenParticipantUpdatedThenScreenShouldReset() {
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		participant.addEvent(event);

		participantViewScreen.participantAdded(participant);
		participant.setParticipantName(PARTICIPANT_NAME_2); // updated participant Name
		participantViewScreen.participantUpdated(participant);

		await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> window.textBox(TXT_PARTICIPANT_NAME).requireText(""));
		window.textBox(TXT_EVENT_ID).requireText("");
		window.textBox(TXT_PARTICIPANT_ID).requireText("");
		window.textBox(TXT_PARTICIPANT_NAME).requireText("");
		window.textBox(TXT_PARTICIPANT_EMAIL).requireText("");
		window.button(JButtonMatcher.withText(BTN_UPDATE_PARTICIPANT)).requireDisabled();
		window.button(JButtonMatcher.withText(BTN_DELETE_PARTICIPANT)).requireDisabled();
		window.button(JButtonMatcher.withText(BTN_ADD_PARTICIPANT)).requireDisabled();
		window.list(LIST_PARTICIPANT).requireNoSelection();
		verify(participantController, times(3)).getAllEvents(); // This will get called by participantAdded,
																// participantUpdated and one time when window is
																// activated, so in total 3 times.
	}

	// Test participant is not updated when it is not found
	@Test
	public void testParticipantUpdateShouldNotUpdateWhenParticipantNotFound() {
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		ParticipantModel updatedParticipant = new ParticipantModel(PARTICIPANT_ID_2, PARTICIPANT_NAME_2,
				PARTICIPANT_EMAIL_2);
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		participant.addEvent(event);
		updatedParticipant.addEvent(event);

		participantViewScreen.participantAdded(participant);
		participantViewScreen.participantUpdated(updatedParticipant);

		assertThat(window.list(LIST_PARTICIPANT).contents()).containsExactly(getDisplayString(participant));
	}

	// Test Add Participant Button is calling controller method
	@Test
	public void testAddParticipantButtonShouldDelegateToParticipantControllerAddParticipant() {
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		EventModel event = new EventModel();
		event.setEventId(EVENT_ID);
		setFieldValues(String.valueOf(EVENT_ID), PARTICIPANT_NAME, PARTICIPANT_EMAIL);

		window.button(JButtonMatcher.withText(BTN_ADD_PARTICIPANT)).click();

		await().atMost(10, TimeUnit.SECONDS)
				.untilAsserted(() -> verify(participantController).addParticipant(participant, event));
	}

	// Test Delete Participant Button is calling controller method
	@Test
	public void testDeleteParticipantButtonShouldDelegateToParticipantControllerDeleteParticipant() {
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		participant.addEvent(event);

		GuiActionRunner.execute(() -> {
			participantViewScreen.participantAdded(participant);
		});
		window.list(LIST_PARTICIPANT).selectItem(0);
		window.list(LIST_EVENT).selectItem(0);
		window.button(JButtonMatcher.withText(BTN_DELETE_PARTICIPANT)).click();

		await().atMost(10, TimeUnit.SECONDS)
				.untilAsserted(() -> verify(participantController).deleteParticipant(participant, event));
	}

	// Test Update Participant Button is calling controller method
	@Test
	public void testUpdateParticipantButtonShouldDelegateToParticipantControllerUpdateParticipant() {
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		participant.addEvent(event);

		GuiActionRunner.execute(() -> {
			participantViewScreen.participantAdded(participant);
		});
		window.list(LIST_PARTICIPANT).selectItem(0);
		window.textBox(TXT_PARTICIPANT_NAME).setText(PARTICIPANT_NAME_2);
		window.button(JButtonMatcher.withText(BTN_UPDATE_PARTICIPANT)).click();
		participant.setParticipantName(PARTICIPANT_NAME_2);

		await().atMost(10, TimeUnit.SECONDS)
				.untilAsserted(() -> verify(participantController).updateParticipant(participant));
	}

	// Test when Participant is selected from List it should auto populate text
	// fields and
	// enable Update Button
	@Test
	public void testParticipantSelectionFromListShouldPopulateFieldsAndEnableUpdateButton() {
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		participant.addEvent(event);

		GuiActionRunner.execute(() -> {
			participantViewScreen.participantAdded(participant);
		});
		window.list(LIST_PARTICIPANT).selectItem(0);

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
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		participant.addEvent(event);

		GuiActionRunner.execute(() -> {
			participantViewScreen.participantAdded(participant);
		});
		window.list(LIST_PARTICIPANT).selectItem(0);
		window.list(LIST_EVENT).selectItem(0);

		window.button(JButtonMatcher.withText(BTN_DELETE_PARTICIPANT)).requireEnabled();
	}
	
	// Test when Participant is selected but Event is deSelected from List then it should disable
	// Delete Button
	@Test
	public void testDeleteButtonDisabledWhenParticipantSelectedButEventDeselectedFromList() {
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		participant.addEvent(event);

		GuiActionRunner.execute(() -> {
			participantViewScreen.participantAdded(participant);
		});
		window.list(LIST_PARTICIPANT).selectItem(0);
		window.list(LIST_EVENT).selectItem(0);
		window.list(LIST_EVENT).clearSelection();

		window.button(JButtonMatcher.withText(BTN_DELETE_PARTICIPANT)).requireDisabled();
	}

	// Test when Participant is deselected from List then it should disable
	// Update Button
	@Test
	public void testParticipantSelectionFromListShouldDisableUpdateButton() {
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		participant.addEvent(event);

		GuiActionRunner.execute(() -> {
			participantViewScreen.participantAdded(participant);
		});
		window.list(LIST_PARTICIPANT).selectItem(0);
		window.list(LIST_PARTICIPANT).clearSelection();

		window.button(JButtonMatcher.withText(BTN_UPDATE_PARTICIPANT)).requireDisabled();
	}
	
	// Test when Refresh button is clicked then Participant Screen should fully reset
	@Test
	public void testWhenRefreshButtonClickedThenParticipantScreenShouldFullyReset() {
		ParticipantModel participant = new ParticipantModel(PARTICIPANT_ID, PARTICIPANT_NAME, PARTICIPANT_EMAIL);
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		participant.addEvent(event);

		GuiActionRunner.execute(() -> {
			participantViewScreen.participantAdded(participant);
		});
		window.list(LIST_PARTICIPANT).selectItem(0);
		window.list(LIST_EVENT).selectItem(0);
		window.button(JButtonMatcher.withText(BTN_REFRESH_SCREEN)).click();

		await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> window.textBox(TXT_PARTICIPANT_NAME).requireText(""));
		window.textBox(TXT_EVENT_ID).requireText("");
		window.textBox(TXT_PARTICIPANT_ID).requireText("");
		window.textBox(TXT_PARTICIPANT_NAME).requireText("");
		window.textBox(TXT_PARTICIPANT_EMAIL).requireText("");
		window.button(JButtonMatcher.withText(BTN_UPDATE_PARTICIPANT)).requireDisabled();
		window.button(JButtonMatcher.withText(BTN_DELETE_PARTICIPANT)).requireDisabled();
		window.button(JButtonMatcher.withText(BTN_ADD_PARTICIPANT)).requireDisabled();
		verify(participantController, times(3)).getAllEvents(); // This will get called by participantAdded,
																// one time when window is
																// activated and also when Refresh button is clicked, so in total 3 times.
	}

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
