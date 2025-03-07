/**
 * This class defines the step implementation for the Cucumber BDD (Behavior Driven Development) tests 
 * for the Event Management Application. It contains various Given, When, Then steps that simulate 
 * user interactions with the Event and Participant views of the application through the GUI.
 *
 * The class uses AssertJ-Swing to interact with the GUI components, such as buttons, text fields, 
 * and lists, ensuring that the application behaves correctly as specified in the feature files.
 * Additionally, the test steps include validation for creating, updating, and removing events and 
 * participants, as well as handling errors when invalid data is provided.
 *
 * The database interactions are handled using test data defined in the DBSteps class, and Docker 
 * test containers are used to run the application and database in isolated environments for testing.
 * 
 * The main objectives of this class are:
 * - Test user interaction with Event and Participant views.
 * - Validate correct data entry and UI behavior in the application.
 */

package com.mycompany.eventmanagementapp.bdd.steps;

import java.util.Map;
import java.util.List;
import javax.swing.JFrame;
import java.time.LocalDate;
import io.cucumber.java.After;
import java.util.regex.Pattern;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Given;
import org.assertj.swing.core.Robot;
import java.util.concurrent.TimeUnit;
import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import static org.awaitility.Awaitility.await;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import static org.assertj.core.api.Assertions.assertThat;
import static com.mycompany.eventmanagementapp.bdd.steps.DBSteps.dbURL;
import static com.mycompany.eventmanagementapp.bdd.steps.DBSteps.DB_USER;
import static com.mycompany.eventmanagementapp.bdd.steps.DBSteps.DB_PASS;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

public class EventManagementAppSteps {

	private FrameFixture eventManagementAppWindow;

	private Robot robot = BasicRobot.robotWithCurrentAwtHierarchy();

	private static final String LIST_PARTICIPANT = "participantList";

	private static final String LIST_EVENT_PARTICIPANT = "eventListForParticipant";

	private static final String LIST_EVENT = "eventList";

	private static final String LBL_ERROR = "lblError";

	private static final String TXT_EVENT_ID = "txtEventId";

	private static final String TXT_EVENT_NAME = "txtEventName";

	private static final String TXT_EVENT_LOCATION = "txtEventLocation";

	private static final String TXT_EVENT_DATE = "txtEventDate";

	private static final long EVENT_ID_INVALID = 9;

	private static final String EVENT_NAME_NEW = "New Event Name";

	private static final LocalDate EVENT_DATE_NEW = LocalDate.now().plusDays(10);

	private static final LocalDate EVENT_DATE_PAST = LocalDate.now().minusDays(10);

	private static final String EVENT_LOCATION_NEW = "New Event Location";

	private static final String PARTICIPANT_NAME_NEW = "New Participant";

	private static final String PARTICIPANT_EMAIL_NEW = "newparticipant@gmail.com";

	private static final String TXT_PARTICIPANT_ID = "txtParticipantId";

	private static final String TXT_PARTICIPANT_NAME = "txtParticipantName";

	private static final String TXT_PARTICIPANT_EMAIL = "txtParticipantEmail";

	@After
	public void tearDown() {
		// Ensure any open windows are closed after each test
		if (eventManagementAppWindow != null)
			eventManagementAppWindow.cleanUp();
	}

	/*
	 * The Event View Section.
	 */

	@When("The Event View is shown")
	public void the_Event_View_is_shown() {
		// Launch the EventManagementApp and wait for the UI to become idle
		application("com.mycompany.eventmanagementapp.EventManagementApp")
				.withArgs("--mysql-DB_URL=" + dbURL, "--mysql-user=" + DB_USER, "--mysql-pass=" + DB_PASS).start();
		robot.waitForIdle();

		// Find the "Event Management View Screen" window and assign it to the
		// eventViewWindow fixture
		eventManagementAppWindow = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
			@Override
			protected boolean isMatching(JFrame frame) {
				return "Event Management Screen".equals(frame.getTitle()) && frame.isShowing();
			}
		}).using(robot);
	}

	@Given("The user provides event data in the text fields")
	public void the_user_provides_event_data_in_the_text_fields() {
		eventManagementAppWindow.textBox(TXT_EVENT_NAME).enterText(EVENT_NAME_NEW);
		eventManagementAppWindow.textBox(TXT_EVENT_LOCATION).enterText(EVENT_LOCATION_NEW);
		eventManagementAppWindow.textBox(TXT_EVENT_DATE).enterText(EVENT_DATE_NEW.toString());
	}

	@Then("The list contains the new event")
	public void the_list_contains_the_new_event() {
		await().atMost(5, TimeUnit.SECONDS)
				.untilAsserted(() -> assertThat(eventManagementAppWindow.list(LIST_EVENT).contents()).anySatisfy(
						e -> assertThat(e).contains(EVENT_NAME_NEW, EVENT_LOCATION_NEW, EVENT_DATE_NEW.toString())));
	}

	@Then("The Event view list contains an element with the following values")
	public void the_Event_view_list_contains_an_element_with_the_following_values(List<List<String>> values) {
		values.forEach(v -> await().atMost(5, TimeUnit.SECONDS)
				.untilAsserted(() -> assertThat(eventManagementAppWindow.list().contents())
						.anySatisfy(e -> assertThat(e).contains(v.get(0), v.get(1), v.get(2)))));
	}

	@Given("The user provides event data in the text fields, specifying past event date")
	public void the_user_provides_event_data_in_the_text_fields_specifying_past_event_date() {
		eventManagementAppWindow.textBox(TXT_EVENT_NAME).enterText(EVENT_NAME_NEW);
		eventManagementAppWindow.textBox(TXT_EVENT_LOCATION).enterText(EVENT_LOCATION_NEW);
		eventManagementAppWindow.textBox(TXT_EVENT_DATE).enterText(EVENT_DATE_PAST.toString());
	}

	@Then("An error is shown containing the information of the event")
	public void an_error_is_shown_containing_the_information_of_the_event() {
		await().atMost(5, TimeUnit.SECONDS)
				.untilAsserted(() -> assertThat(eventManagementAppWindow.textBox(LBL_ERROR).text()).isNotEmpty()
						.doesNotContainOnlyWhitespaces());
	}

	@Given("The user selects an event from the list")
	public void the_user_selects_an_event_from_the_list() {
		eventManagementAppWindow.list(LIST_EVENT).selectItem(Pattern.compile(".*" + DBSteps.EVENT_NAME_1 + ".*"));
	}

	@Then("The event is removed from the list")
	public void the_event_is_removed_from_the_list() {
		await().atMost(5, TimeUnit.SECONDS)
				.untilAsserted(() -> assertThat(eventManagementAppWindow.list(LIST_EVENT).contents())
						.noneMatch(e -> e.contains(DBSteps.EVENT_NAME_1)));
	}

	@When("The selected event is populated in textfields")
	public void the_selected_event_is_populated_in_textfields() {
		eventManagementAppWindow.textBox(TXT_EVENT_ID).requireText(String.valueOf(DBSteps.EVENT_ID));
		eventManagementAppWindow.textBox(TXT_EVENT_NAME).requireText(DBSteps.EVENT_NAME_1);
		eventManagementAppWindow.textBox(TXT_EVENT_LOCATION).requireText(DBSteps.EVENT_LOCATION_1);
		eventManagementAppWindow.textBox(TXT_EVENT_DATE).requireText(DBSteps.EVENT_DATE_1.toString());
	}

	@When("The user provides Updated event data in the text fields")
	public void the_user_provides_Updated_event_data_in_the_text_fields() {
		eventManagementAppWindow.textBox(TXT_EVENT_NAME).setText(EVENT_NAME_NEW);
	}

	@Then("The list contains the updated event")
	public void the_list_contains_the_updated_event() {
		assertThat(eventManagementAppWindow.list(LIST_EVENT).contents()).anySatisfy(
				e -> assertThat(e).contains(EVENT_NAME_NEW, DBSteps.EVENT_LOCATION_1, DBSteps.EVENT_DATE_1.toString()));
	}

	@When("The user provides Updated event data in the text fields, specifying past event date")
	public void the_user_provides_Updated_event_data_in_the_text_fields_specifying_past_event_date() {
		eventManagementAppWindow.textBox(TXT_EVENT_DATE).setText(EVENT_DATE_PAST.toString());
	}

	/*
	 * The Participant View Section.
	 */

	@When("The Participant View is shown")
	public void the_Participant_View_is_shown() {
		the_Event_View_is_shown();
		eventManagementAppWindow.button("Participant Screen").click();

		eventManagementAppWindow = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
			@Override
			protected boolean isMatching(JFrame frame) {
				return "Participant Management Screen".equals(frame.getTitle()) && frame.isShowing();
			}
		}).using(robot);
	}

	@Given("The user provides participant data in the text fields")
	public void the_user_provides_participant_data_in_the_text_fields() {
		eventManagementAppWindow.textBox(TXT_EVENT_ID).enterText(String.valueOf(DBSteps.EVENT_ID));
		eventManagementAppWindow.textBox(TXT_PARTICIPANT_NAME).enterText(PARTICIPANT_NAME_NEW);
		eventManagementAppWindow.textBox(TXT_PARTICIPANT_EMAIL).enterText(PARTICIPANT_EMAIL_NEW);
	}

	@Then("The Participant list contains the new participant")
	public void the_Participant_list_contains_the_new_participant() {
		await().atMost(5, TimeUnit.SECONDS)
				.untilAsserted(() -> assertThat(eventManagementAppWindow.list(LIST_PARTICIPANT).contents())
						.anySatisfy(e -> assertThat(e).contains(PARTICIPANT_NAME_NEW, PARTICIPANT_EMAIL_NEW)));
	}

	@Given("The user provides participant data in the text fields, specifying invalid event Id")
	public void the_user_provides_participant_data_in_the_text_fields_specifying_invalid_event_Id() {
		eventManagementAppWindow.textBox(TXT_EVENT_ID).enterText(String.valueOf(EVENT_ID_INVALID));
		eventManagementAppWindow.textBox(TXT_PARTICIPANT_NAME).enterText(PARTICIPANT_NAME_NEW);
		eventManagementAppWindow.textBox(TXT_PARTICIPANT_EMAIL).enterText(PARTICIPANT_EMAIL_NEW);
	}

	@Then("An error is shown containing the information of the participant")
	public void an_error_is_shown_containing_the_information_of_the_participant() {
		await().atMost(5, TimeUnit.SECONDS)
				.untilAsserted(() -> assertThat(eventManagementAppWindow.textBox(LBL_ERROR).text()).isNotEmpty()
						.doesNotContainOnlyWhitespaces());
	}

	@When("The user selects a participant which only associated to one Event from the Participant list")
	public void the_user_selects_a_participant_which_only_associated_to_one_Event_from_the_Participant_list() {
		eventManagementAppWindow.list(LIST_PARTICIPANT)
				.selectItem(Pattern.compile(".*" + DBSteps.PARTICIPANT_NAME_2 + ".*"));
	}

	@When("The user selects an event from the Event list")
	public void the_user_selects_an_event_from_the_Event_list() {
		eventManagementAppWindow.list(LIST_EVENT_PARTICIPANT)
				.selectItem(Pattern.compile(".*" + DBSteps.EVENT_NAME_1 + ".*"));
	}

	@Then("The participant is removed from the Participant list")
	public void the_participant_is_removed_from_the_list() {
		await().atMost(5, TimeUnit.SECONDS)
				.untilAsserted(() -> assertThat(eventManagementAppWindow.list(LIST_PARTICIPANT).contents())
						.noneMatch(e -> e.contains(DBSteps.PARTICIPANT_NAME_2)));
	}

	@When("The user selects a participant which associated to more than one Event from the Participant list")
	public void the_user_selects_a_participant_which_associated_to_more_than_one_Event_from_the_Participant_list() {
		eventManagementAppWindow.list(LIST_PARTICIPANT)
				.selectItem(Pattern.compile(".*" + DBSteps.PARTICIPANT_NAME + ".*"));
	}

	@Then("The participant is removed from that Event but stays in Participant list")
	public void the_participant_is_removed_from_that_Event_but_stays_in_Participant_list() {
		await().atMost(5, TimeUnit.SECONDS)
				.untilAsserted(() -> assertThat(eventManagementAppWindow.list(LIST_PARTICIPANT).contents())
						.anySatisfy(e -> assertThat(e).contains(DBSteps.PARTICIPANT_NAME)));
	}

	@When("The user selects a participant from the Participant list")
	public void the_user_selects_a_participant_from_the_list() {
		eventManagementAppWindow.list(LIST_PARTICIPANT)
				.selectItem(Pattern.compile(".*" + DBSteps.PARTICIPANT_NAME_2 + ".*"));
	}

	@When("The selected participant is populated in text fields")
	public void the_selected_participant_is_populated_in_text_fields() {
		eventManagementAppWindow.textBox(TXT_PARTICIPANT_ID).requireText(String.valueOf(DBSteps.PARTICIPANT_ID_2));
		eventManagementAppWindow.textBox(TXT_PARTICIPANT_NAME).requireText(DBSteps.PARTICIPANT_NAME_2);
		eventManagementAppWindow.textBox(TXT_PARTICIPANT_EMAIL).requireText(DBSteps.PARTICIPANT_EMAIL_2);
	}

	@When("The user provides Updated participant data in the text fields")
	public void the_user_provides_Updated_participant_data_in_the_text_fields() {
		eventManagementAppWindow.textBox(TXT_PARTICIPANT_NAME).setText(PARTICIPANT_NAME_NEW);
	}

	@Then("The Participant list contains the updated Participant")
	public void the_Participant_list_contains_the_updated_Participant() {
		assertThat(eventManagementAppWindow.list(LIST_PARTICIPANT).contents())
				.anySatisfy(e -> assertThat(e).contains(PARTICIPANT_NAME_NEW, DBSteps.PARTICIPANT_EMAIL_2));
	}

	@When("The user provides Updated participant data in the text fields specifying new email")
	public void the_user_provides_Updated_participant_data_in_the_text_fields_specifying_new_email() {
		eventManagementAppWindow.textBox(TXT_PARTICIPANT_EMAIL).setText(PARTICIPANT_EMAIL_NEW);
	}

	@Then("The Participant list contains an element with the following values")
	public void the_participant_list_contains_an_element_with_the_following_values(List<List<String>> values) {
		values.forEach(v -> await().atMost(5, TimeUnit.SECONDS)
				.untilAsserted(() -> assertThat(eventManagementAppWindow.list(LIST_PARTICIPANT).contents())
						.anySatisfy(e -> assertThat(e).contains(v.get(0), v.get(1)))));
	}

	@Then("The Event list contains an element with the following values")
	public void the_event_list_contains_an_element_with_the_following_values(List<List<String>> values) {
		values.forEach(v -> await().atMost(5, TimeUnit.SECONDS)
				.untilAsserted(() -> assertThat(eventManagementAppWindow.list(LIST_EVENT_PARTICIPANT).contents())
						.anySatisfy(e -> assertThat(e).contains(v.get(0), v.get(1), v.get(2)))));
	}

	/*
	 * Common Section.
	 */

	@Then("An error is shown containing the following values")
	public void an_error_is_shown_containing_the_following_values(List<List<String>> values) {
		await().atMost(5, TimeUnit.SECONDS).untilAsserted(
				() -> assertThat(eventManagementAppWindow.textBox(LBL_ERROR).text()).contains(values.get(0)));
	}

	@When("The user enters the following values in the text fields")
	public void the_user_enters_the_following_values_in_the_text_fields(List<Map<String, String>> values) {
		values.stream().flatMap(m -> m.entrySet().stream()).forEach(e -> {
			if (e.getKey().contains(TXT_EVENT_NAME)) {
				eventManagementAppWindow.textBox(e.getKey()).setText("");
				eventManagementAppWindow.textBox(e.getKey()).enterText(e.getValue());
			} else if (e.getKey().contains(TXT_EVENT_LOCATION)) {
				eventManagementAppWindow.textBox(e.getKey()).setText("");
				eventManagementAppWindow.textBox(e.getKey()).enterText(e.getValue());
			} else if ((e.getKey().contains(TXT_EVENT_DATE))) {
				eventManagementAppWindow.textBox(e.getKey()).setText("");
				eventManagementAppWindow.textBox(e.getKey()).enterText(e.getValue());
			} else if ((e.getKey().contains(TXT_PARTICIPANT_NAME))) {
				eventManagementAppWindow.textBox(e.getKey()).setText("");
				eventManagementAppWindow.textBox(e.getKey()).enterText(e.getValue());
			} else if ((e.getKey().contains(TXT_PARTICIPANT_EMAIL))) {
				eventManagementAppWindow.textBox(e.getKey()).setText("");
				eventManagementAppWindow.textBox(e.getKey()).enterText(e.getValue());
			} else if ((e.getKey().contains(TXT_EVENT_ID))) {
				eventManagementAppWindow.textBox(e.getKey()).setText("");
				eventManagementAppWindow.textBox(e.getKey()).enterText(e.getValue());
			}
		});
	}

	@Then("All values are populated")
	public void all_values_are_populated(List<Map<String, String>> values) {
		values.stream().flatMap(m -> m.entrySet().stream()).forEach(e -> {
			if (e.getKey().contains(TXT_EVENT_NAME)) {
				await().atMost(5, TimeUnit.SECONDS).untilAsserted(
						() -> assertThat(eventManagementAppWindow.textBox(e.getKey()).text()).isEqualTo(e.getValue()));
			} else if (e.getKey().contains(TXT_EVENT_LOCATION)) {
				assertThat(eventManagementAppWindow.textBox(e.getKey()).text()).isEqualTo(e.getValue());
			} else if ((e.getKey().contains(TXT_EVENT_DATE))) {
				assertThat(eventManagementAppWindow.textBox(e.getKey()).text()).isEqualTo(e.getValue());
			} else if ((e.getKey().contains(TXT_EVENT_ID))) {
				assertThat(eventManagementAppWindow.textBox(e.getKey()).text()).isEqualTo(e.getValue());
			} else if ((e.getKey().contains(TXT_PARTICIPANT_NAME))) {
				assertThat(eventManagementAppWindow.textBox(e.getKey()).text()).isEqualTo(e.getValue());
			} else if ((e.getKey().contains(TXT_PARTICIPANT_EMAIL))) {
				assertThat(eventManagementAppWindow.textBox(e.getKey()).text()).isEqualTo(e.getValue());
			}
		});
	}

	@When("The user clicks the {string} button")
	public void the_user_clicks_the_button(String buttonText) {
		eventManagementAppWindow.button(JButtonMatcher.withText(buttonText)).click();
	}
}
