/**
 * Unit tests for the Event Management View Screen in the Event Management Application.
 * 
 * This class tests the functionality and behavior of the Event Management UI, including event addition, update, deletion, and event list management. It uses AssertJ-Swing for GUI testing, Mockito for mocking dependencies, and Awaitility to handle asynchronous operations.
 *
 * Key functionalities tested:
 * 1. Initial state and visibility of UI components (labels, text fields, buttons, etc.).
 * 2. Enabling and disabling buttons (Add, Update, Delete) based on input validation and event selection.
 * 3. Event addition, update, and deletion, and ensuring proper interaction with the EventController.
 * 4. Verifying correct population of text fields when an event is selected from the list.
 * 5. Error handling: checking that the error message is displayed correctly when invalid input is provided.
 * 6. Interaction with the Participant Management screen and verifying the "Participant Screen" button functionality.
 * 7. Resetting UI components when actions are completed, such as after adding or deleting an event.
 * 8. Ensuring the UI reflects changes when events are added, updated, or deleted, and that the error label is reset accordingly.
 * 
 * The tests ensure that the Event Management View Screen performs as expected under various scenarios, validating both functionality and user experience.
 */

package com.mycompany.eventmanagementapp.view.screen;

import org.junit.Test;
import org.mockito.Mock;
import java.time.LocalDate;
import org.junit.runner.RunWith;
import javax.swing.DefaultListModel;
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
import com.mycompany.eventmanagementapp.controller.EventController;

@RunWith(GUITestRunner.class)
public class EventManagementViewScreenTest extends AssertJSwingJUnitTestCase {

	private EventManagementViewScreen eventViewScreen;

	private FrameFixture window;

	@Mock
	private EventController eventController;

	@Mock
	private ParticipantManagementViewScreen participantViewScreen;

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

	private static final String BTN_ADD_EVENT = "Add Event";

	private static final String BTN_UPDATE_EVENT = "Update Event";

	private static final String BTN_DELETE_EVENT = "Delete Event";

	private static final String BTN_PARTICIPANT_SCREEN = "Participant Screen";

	private static final String BTN_REFRESH_SCREEN = "Refresh";

	private static final String LBL_EVENT_ID = "Event ID:";

	private static final String LBL_EVENT_NAME = "Event Name:";

	private static final String LBL_EVENT_LOCATION = "Event Location:";

	private static final String LBL_EVENT_DATE = "Event Date (YYYY-MM-DD):";

	private static final String TXT_EVENT_ID = "txtEventId";

	private static final String TXT_EVENT_NAME = "txtEventName";

	private static final String TXT_EVENT_LOCATION = "txtEventLocation";

	private static final String TXT_EVENT_DATE = "txtEventDate";

	private static final String TXT_EVENT_ERROR = "lblError";

	private static final String LIST_EVENT = "eventList";

	@Override
	protected void onSetUp() {
		closeable = MockitoAnnotations.openMocks(this);
		GuiActionRunner.execute(() -> {
			eventViewScreen = new EventManagementViewScreen();
			eventViewScreen.setEventController(eventController);
			eventViewScreen.setParticipantView(participantViewScreen);
			return eventViewScreen;
		});
		window = new FrameFixture(robot(), eventViewScreen);
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
		window.label(JLabelMatcher.withText(LBL_EVENT_ID)).requireVisible();
		window.textBox(TXT_EVENT_ID).requireVisible().requireNotEditable();
		// for EventName label and textbox
		window.label(JLabelMatcher.withText(LBL_EVENT_NAME)).requireVisible();
		window.textBox(TXT_EVENT_NAME).requireVisible().requireEnabled();
		// for EventLocation label and textbox
		window.label(JLabelMatcher.withText(LBL_EVENT_LOCATION)).requireVisible();
		window.textBox(TXT_EVENT_LOCATION).requireVisible().requireEnabled();
		// for EventDate label and textbox
		window.label(JLabelMatcher.withText(LBL_EVENT_DATE)).requireVisible();
		window.textBox(TXT_EVENT_DATE).requireVisible().requireEnabled();

		// Event List
		window.list(LIST_EVENT).requireVisible();

		// Buttons
		window.button(JButtonMatcher.withText(BTN_ADD_EVENT)).requireVisible().requireDisabled();
		window.button(JButtonMatcher.withText(BTN_UPDATE_EVENT)).requireVisible().requireDisabled();
		window.button(JButtonMatcher.withText(BTN_DELETE_EVENT)).requireVisible().requireDisabled();
		window.button(JButtonMatcher.withText(BTN_PARTICIPANT_SCREEN)).requireVisible().requireEnabled();
		window.button(JButtonMatcher.withText(BTN_REFRESH_SCREEN)).requireVisible().requireEnabled();

		// Error Text
		window.textBox(TXT_EVENT_ERROR).requireVisible().requireNotEditable().requireText(" ");
	}

	// Test Add Button is enabled when eventName, location and date is not empty
	@Test
	public void testWhenEventNameLocationAndDateIsNonEmptyThenAddButtonShouldBeEnabled() {
		// Setup & Exercise
		setFieldValues(EVENT_NAME_1, EVENT_LOCATION_1, EVENT_DATE_1.toString());

		// Verify
		window.button(JButtonMatcher.withText(BTN_ADD_EVENT)).requireEnabled();
	}

	// Test Add Button is disabled when eventName is empty
	@Test
	public void testWhenEventNameIsEmptyThenAddButtonShouldBeDisabled() {
		// Setup & Exercise
		setFieldValues("", EVENT_LOCATION_1, EVENT_DATE_1.toString());

		// Verify
		window.button(JButtonMatcher.withText(BTN_ADD_EVENT)).requireDisabled();
	}

	// Test Add Button is disabled when eventLocation is empty
	@Test
	public void testWhenEventLocationIsEmptyThenAddButtonShouldBeDisabled() {
		// Setup & Exercise
		setFieldValues(EVENT_NAME_1, "", EVENT_DATE_1.toString());

		// Verify
		window.button(JButtonMatcher.withText(BTN_ADD_EVENT)).requireDisabled();
	}

	// Test Add Button is disabled when eventDate is empty
	@Test
	public void testWhenEventDateIsEmptyThenAddButtonShouldBeDisabled() {
		// Setup & Exercise
		setFieldValues(EVENT_NAME_1, EVENT_LOCATION_1, "");

		// Verify
		window.button(JButtonMatcher.withText(BTN_ADD_EVENT)).requireDisabled();
	}

	// Test Add Button is disabled when eventDate has incorrect format
	@Test
	public void testWhenEventDateHasIncorrectFormatThenAddButtonShouldBeDisabled() {
		// Setup & Exercise
		setFieldValues(EVENT_NAME_1, EVENT_LOCATION_1, EVENT_DATE_INVALID);

		// Verify
		window.button(JButtonMatcher.withText("Add Event")).requireDisabled();
	}

	// Test enabling Update and Delete buttons when event is selected
	@Test
	public void testDeleteSelectedAndUpdateSelectedButtonShouldBeEnabledOnlyWhenEventIsSelected() {
		// Setup
		GuiActionRunner.execute(() -> eventViewScreen.getEventListModel()
				.addElement(new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1)));

		// Exercise & Verify
		window.list(LIST_EVENT).selectItem(0);
		JButtonFixture deleteButton = window.button(JButtonMatcher.withText(BTN_DELETE_EVENT));
		JButtonFixture updateButton = window.button(JButtonMatcher.withText(BTN_UPDATE_EVENT));
		deleteButton.requireEnabled();
		updateButton.requireEnabled();

		window.list(LIST_EVENT).clearSelection();
		deleteButton.requireDisabled();
		updateButton.requireDisabled();
	}

	// Test update button is disabled when event is not selected and Event Id is
	// empty
	@Test
	public void testUpdateButtondisabledWhenListIsNotSelectedAndEventIdIsEmpty() {
		// Setup
		GuiActionRunner.execute(() -> eventViewScreen.getEventListModel()
				.addElement(new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1)));

		// Exercise
		window.list(LIST_EVENT).selectItem(0);
		window.list(LIST_EVENT).clearSelection();
		setFieldValues(EVENT_NAME_1, EVENT_LOCATION_1, EVENT_DATE_1.toString());

		// Verify
		JButtonFixture updateButton = window.button(JButtonMatcher.withText(BTN_UPDATE_EVENT));
		updateButton.requireDisabled();
	}

	// Test update button is enabled when event is selected and updated values are
	// entered in text fields
	@Test
	public void testUpdateButtonEnabledWhenListIsSelected() {
		// Setup
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		GuiActionRunner.execute(() -> {
			eventViewScreen.eventAdded(event);
		});

		// Exercise
		window.list(LIST_EVENT).selectItem(0);
		ResetFieldValues();
		setFieldValues(EVENT_NAME_1, EVENT_LOCATION_1, EVENT_DATE_1.toString());

		// Verify
		window.button(JButtonMatcher.withText(BTN_UPDATE_EVENT)).requireEnabled();
	}

	// Test populating text fields when event is selected from List
	@Test
	public void testPopulatingFieldsWhenEventIsSelected() {
		// Setup
		GuiActionRunner.execute(() -> eventViewScreen.getEventListModel()
				.addElement(new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1)));

		// Exercise
		window.list(LIST_EVENT).selectItem(0);

		// Verify
		window.textBox(TXT_EVENT_ID).requireText(String.valueOf(EVENT_ID));
		window.textBox(TXT_EVENT_NAME).requireText(EVENT_NAME_1);
		window.textBox(TXT_EVENT_DATE).requireText(EVENT_DATE_1.toString());
		window.textBox(TXT_EVENT_LOCATION).requireText(EVENT_LOCATION_1);
	}

	// Test Update Button is disabled when eventName is empty
	@Test
	public void testWhenEventNameIsBlankThenUpdateButtonShouldBeDisabled() {
		// Setup
		GuiActionRunner.execute(() -> eventViewScreen.getEventListModel()
				.addElement(new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1)));

		// Exercise
		window.list(LIST_EVENT).selectItem(0);

		// Verify
		JButtonFixture updateButton = window.button(JButtonMatcher.withText(BTN_UPDATE_EVENT));
		updateButton.requireEnabled();
		window.textBox(TXT_EVENT_NAME).setText("");
		window.textBox(TXT_EVENT_NAME).enterText("  ");
		updateButton.requireDisabled();
	}

	// Test Update Button is disabled when eventLocation is empty
	@Test
	public void testWhenEventLocationIsBlankThenUpdateButtonShouldBeDisabled() {
		// Setup
		GuiActionRunner.execute(() -> eventViewScreen.getEventListModel()
				.addElement(new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1)));

		// Exercise
		window.list(LIST_EVENT).selectItem(0);

		// Verify
		JButtonFixture updateButton = window.button(JButtonMatcher.withText(BTN_UPDATE_EVENT));
		updateButton.requireEnabled();
		window.textBox(TXT_EVENT_LOCATION).setText("");
		window.textBox(TXT_EVENT_LOCATION).enterText("  ");
		updateButton.requireDisabled();
	}

	// Test Update Button is disabled when eventDate is empty
	@Test
	public void testWhenEventDateIsBlankThenUpdateButtonShouldBeDisabled() {
		// Setup
		GuiActionRunner.execute(() -> eventViewScreen.getEventListModel()
				.addElement(new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1)));

		// Exercise
		window.list(LIST_EVENT).selectItem(0);

		// Verify
		JButtonFixture updateButton = window.button(JButtonMatcher.withText(BTN_UPDATE_EVENT));
		updateButton.requireEnabled();
		window.textBox(TXT_EVENT_DATE).setText("");
		window.textBox(TXT_EVENT_DATE).enterText("  ");
		updateButton.requireDisabled();
	}

	// Test Update Button is disabled when eventDate is not empty but incorrect
	// Format
	@Test
	public void testWhenEventDateIsInvalidThenUpdateButtonShouldBeDisabled() {
		// Setup
		GuiActionRunner.execute(() -> eventViewScreen.getEventListModel()
				.addElement(new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1)));

		// Exercise
		window.list(LIST_EVENT).selectItem(0);

		// Verify
		JButtonFixture updateButton = window.button(JButtonMatcher.withText(BTN_UPDATE_EVENT));
		updateButton.requireEnabled();
		window.textBox(TXT_EVENT_DATE).setText("");
		window.textBox(TXT_EVENT_DATE).enterText(EVENT_DATE_INVALID);
		updateButton.requireDisabled();
	}

	// Test when Participant Screen opens when "Participant Screen" button is
	// clicked
	@Test
	public void testParticipantScreenButtonShouldOpenParticipantScreen() {
		// Setup & Exercise
		window.button(JButtonMatcher.withText(BTN_PARTICIPANT_SCREEN)).click();

		// Verify
		verify(participantViewScreen).setVisible(true);
	}

	// Test displaying all events in the list
	@Test
	public void testShowAllEventsShouldAddEventsDescriptionsToTheEventList() {
		// Setup
		EventModel event1 = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		EventModel event2 = new EventModel(EVENT_ID_2, EVENT_NAME_2, EVENT_DATE_2, EVENT_LOCATION_2);

		// Exercise
		GuiActionRunner.execute(() -> eventViewScreen.showAllEvents(asList(event1, event2)));

		// Verify
		assertThat(window.list().contents()).containsExactly(getDisplayString(event1), getDisplayString(event2));
	}

	// Test when showError is called then it should show message on screen.
	@Test
	public void testShowErrorShouldShowTheMessageInErrorSection() {
		// Setup
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);

		// Exercise
		eventViewScreen.showError("Error Occurred", event);

		// Verify
		window.textBox(TXT_EVENT_ERROR).requireText("Error Occurred: " + event);
	}

	// Test adding event to the list and resetting error label
	@Test
	public void testEventAddedShouldAddEventToTheListAndResetTheErrorLabel() {
		// Setup
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);

		// Exercise
		eventViewScreen.eventAdded(event);

		// Verify
		assertThat(window.list().contents()).containsExactly(getDisplayString(event));
		window.textBox(TXT_EVENT_ERROR).requireText(" ");
	}

	// Test when event is added then screen is reset
	@Test
	public void testWhenEventAddedThenScreenIsReset() {
		// Setup
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);

		// Exercise
		eventViewScreen.eventAdded(event);

		// Verify
		await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> window.textBox(TXT_EVENT_NAME).requireText(""));
		window.textBox(TXT_EVENT_ID).requireText("");
		window.textBox(TXT_EVENT_NAME).requireText("");
		window.textBox(TXT_EVENT_LOCATION).requireText("");
		window.textBox(TXT_EVENT_DATE).requireText("");
		window.button(JButtonMatcher.withText(BTN_UPDATE_EVENT)).requireDisabled();
		window.button(JButtonMatcher.withText(BTN_DELETE_EVENT)).requireDisabled();
		window.button(JButtonMatcher.withText(BTN_ADD_EVENT)).requireDisabled();
	}

	// Test when event is deleted from the list then error label should be reset
	@Test
	public void testEventDeleteShouldRemoveTheEventFromTheListAndResetTheErrorLabel() {
		// Setup
		EventModel event1 = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		EventModel event2 = new EventModel(EVENT_ID_2, EVENT_NAME_2, EVENT_DATE_2, EVENT_LOCATION_2);
		GuiActionRunner.execute(() -> {
			eventViewScreen.eventAdded(event1);
			eventViewScreen.eventAdded(event2);
		});

		// Exercise
		eventViewScreen.eventDeleted(event1);

		// Verify
		assertThat(window.list().contents()).containsExactly(getDisplayString(event2));
		window.textBox(TXT_EVENT_ERROR).requireText(" ");
	}

	// Test when event is deleted then screen is reset
	@Test
	public void testWhenEventDeletedThenScreenIsReset() {
		// Setup
		EventModel event1 = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		EventModel event2 = new EventModel(EVENT_ID_2, EVENT_NAME_2, EVENT_DATE_2, EVENT_LOCATION_2);
		GuiActionRunner.execute(() -> {
			eventViewScreen.eventAdded(event1);
			eventViewScreen.eventAdded(event2);
		});

		// Exercise
		eventViewScreen.eventDeleted(event1);

		// Verify
		await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> window.textBox(TXT_EVENT_NAME).requireText(""));
		window.textBox(TXT_EVENT_ID).requireText("");
		window.textBox(TXT_EVENT_NAME).requireText("");
		window.textBox(TXT_EVENT_LOCATION).requireText("");
		window.textBox(TXT_EVENT_DATE).requireText("");
		window.button(JButtonMatcher.withText(BTN_UPDATE_EVENT)).requireDisabled();
		window.button(JButtonMatcher.withText(BTN_DELETE_EVENT)).requireDisabled();
		window.button(JButtonMatcher.withText(BTN_ADD_EVENT)).requireDisabled();
	}

	// Test updating event in the list and resetting the error label
	@Test
	public void testEventUpdateShouldUpdateTheCategoryFromTheListAndResetTheErrorLabel() {
		// Setup
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		eventViewScreen.eventAdded(event);
		EventModel updatedEvent = new EventModel(EVENT_ID, EVENT_NAME_2, EVENT_DATE_2, EVENT_LOCATION_2);

		// Exercise
		eventViewScreen.eventUpdated(updatedEvent);

		// Verify
		assertThat(window.list().contents()).containsExactly(getDisplayString(updatedEvent));
		window.textBox(TXT_EVENT_ERROR).requireText(" ");
	}

	// Test when event is updated then screen is reset
	@Test
	public void testWhenEventUpdatedThenScreenIsReset() {
		// Setup
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		eventViewScreen.eventAdded(event);
		EventModel updatedEvent = new EventModel(EVENT_ID, EVENT_NAME_2, EVENT_DATE_2, EVENT_LOCATION_2);

		// Exercise
		eventViewScreen.eventUpdated(updatedEvent);

		// Verify
		await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> window.textBox(TXT_EVENT_NAME).requireText(""));
		window.textBox(TXT_EVENT_ID).requireText("");
		window.textBox(TXT_EVENT_NAME).requireText("");
		window.textBox(TXT_EVENT_LOCATION).requireText("");
		window.textBox(TXT_EVENT_DATE).requireText("");
		window.button(JButtonMatcher.withText(BTN_UPDATE_EVENT)).requireDisabled();
		window.button(JButtonMatcher.withText(BTN_DELETE_EVENT)).requireDisabled();
		window.button(JButtonMatcher.withText(BTN_ADD_EVENT)).requireDisabled();
	}

	// Test event is not updated when it is not found
	@Test
	public void testEventUpdateShouldNotUpdateWhenEventNotFound() {
		// Setup
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		EventModel updatedEvent = new EventModel(EVENT_ID_2, EVENT_NAME_2, EVENT_DATE_2, EVENT_LOCATION_2);

		// Exercise
		eventViewScreen.eventAdded(event);
		eventViewScreen.eventUpdated(updatedEvent);

		// Verify
		assertThat(window.list().contents()).containsExactly(getDisplayString(event));
	}

	// Test Add Event Button is calling controller method
	@Test
	public void testAddEventButtonShouldDelegateToEventControllerAddEvent() {
		// Setup
		setFieldValues(EVENT_NAME_1, EVENT_LOCATION_1, EVENT_DATE_1.toString());

		// Exercise
		window.button(JButtonMatcher.withText(BTN_ADD_EVENT)).click();

		// Verify
		await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> verify(eventController)
				.addEvent(new EventModel(-1, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1)));
	}

	// Test Delete Event Button is calling controller method
	@Test
	public void testDeleteEventButtonShouldDelegateToEventControllerDeleteEvent() {
		// Setup
		EventModel event1 = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		EventModel event2 = new EventModel(EVENT_ID_2, EVENT_NAME_2, EVENT_DATE_2, EVENT_LOCATION_2);
		GuiActionRunner.execute(() -> {
			DefaultListModel<EventModel> eventListModel = eventViewScreen.getEventListModel();
			eventListModel.addElement(event1);
			eventListModel.addElement(event2);
		});

		// Exercise
		window.list(LIST_EVENT).selectItem(1);
		window.button(JButtonMatcher.withText(BTN_DELETE_EVENT)).click();

		// Verify
		await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> verify(eventController).deleteEvent(event2));
	}

	// Test Update Event Button is calling controller method
	@Test
	public void testUpdateEventButtonShouldDelegateToEventControllerUpdateEvent() {
		// Setup
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		GuiActionRunner.execute(() -> {
			DefaultListModel<EventModel> eventListModel = eventViewScreen.getEventListModel();
			eventListModel.addElement(event);
		});

		// Exercise
		window.list(LIST_EVENT).selectItem(0);
		window.textBox(TXT_EVENT_LOCATION).setText(EVENT_LOCATION_2);
		window.button(JButtonMatcher.withText(BTN_UPDATE_EVENT)).click();

		// Verify
		EventModel updatedEvent = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_2);
		await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> verify(eventController).updateEvent(updatedEvent));
	}

	// Test when Event is selected from List it should auto populate text fields and
	// enable buttons
	@Test
	public void testEventSelectionFromListShouldPopulateFieldsAndEnableButtons() {
		// Setup
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);
		GuiActionRunner.execute(() -> {
			DefaultListModel<EventModel> eventListModel = eventViewScreen.getEventListModel();
			eventListModel.addElement(event);
		});

		// Exercise
		window.list(LIST_EVENT).selectItem(0);

		// Verify
		await().atMost(10, TimeUnit.SECONDS)
				.untilAsserted(() -> window.textBox(TXT_EVENT_NAME).requireText(EVENT_NAME_1));
		window.textBox(TXT_EVENT_ID).requireText(String.valueOf(EVENT_ID));
		window.textBox(TXT_EVENT_NAME).requireText(EVENT_NAME_1);
		window.textBox(TXT_EVENT_LOCATION).requireText(EVENT_LOCATION_1);
		window.textBox(TXT_EVENT_DATE).requireText(EVENT_DATE_1.toString());
		window.button(JButtonMatcher.withText(BTN_UPDATE_EVENT)).requireEnabled();
		window.button(JButtonMatcher.withText(BTN_DELETE_EVENT)).requireEnabled();
		window.button(JButtonMatcher.withText(BTN_ADD_EVENT)).requireEnabled();
	}

	// Test when Refresh button is clicked then Event Screen should fully reset
	@Test
	public void testWhenRefreshButtonClickedThenEventScreenShouldFullyReset() {
		// Setup
		EventModel event = new EventModel(EVENT_ID, EVENT_NAME_1, EVENT_DATE_1, EVENT_LOCATION_1);

		// Exercise
		GuiActionRunner.execute(() -> {
			DefaultListModel<EventModel> eventListModel = eventViewScreen.getEventListModel();
			eventListModel.addElement(event);
		});
		window.list(LIST_EVENT).selectItem(0);
		window.button(JButtonMatcher.withText(BTN_REFRESH_SCREEN)).click();

		// Verify
		await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> window.textBox(TXT_EVENT_NAME).requireText(""));
		window.textBox(TXT_EVENT_ID).requireText("");
		window.textBox(TXT_EVENT_NAME).requireText("");
		window.textBox(TXT_EVENT_LOCATION).requireText("");
		window.textBox(TXT_EVENT_DATE).requireText("");
		window.textBox(TXT_EVENT_ERROR).requireText(" ");
		window.button(JButtonMatcher.withText(BTN_UPDATE_EVENT)).requireDisabled();
		window.button(JButtonMatcher.withText(BTN_DELETE_EVENT)).requireDisabled();
		window.button(JButtonMatcher.withText(BTN_ADD_EVENT)).requireDisabled();
		// one time it will be called by Window Activator and second time from Refresh
		// button. In total 2 times.
		verify(eventController, times(2)).getAllEvents();
	}

	//Helper Methods
	private void setFieldValues(String eventName, String eventLocation, String eventDate) {
		window.textBox(TXT_EVENT_NAME).enterText(eventName);
		window.textBox(TXT_EVENT_LOCATION).enterText(eventLocation);
		window.textBox(TXT_EVENT_DATE).enterText(eventDate);
	}

	private void ResetFieldValues() {
		window.textBox(TXT_EVENT_NAME).setText("");
		window.textBox(TXT_EVENT_LOCATION).setText("");
		window.textBox(TXT_EVENT_DATE).setText("");
	}

	private String getDisplayString(EventModel event) {
		return event.getEventId() + " | " + event.getEventName() + " | " + event.getEventLocation() + " | "
				+ event.getEventDate();
	}
}
