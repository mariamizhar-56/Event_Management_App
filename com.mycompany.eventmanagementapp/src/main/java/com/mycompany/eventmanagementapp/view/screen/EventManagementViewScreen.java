/**
 * EventManagementViewScreen is a graphical user interface (GUI) class that implements the 
 * EventManagementView interface in the Event Management application. This class provides the 
 * user interface for managing events, allowing users to add, update, delete, and refresh events.
 * It also provides a visual representation of all events and handles user interaction with the event list.
 * <p>
 * The class is responsible for the following actions:
 * - Displaying the list of events.
 * - Handling the addition, update, and deletion of events.
 * - Enabling or disabling buttons based on user inputs and event selection.
 * - Displaying error messages related to event management.
 * - Providing navigation to the participant management screen.
 * 
 * The class uses Java Swing components to build the interface, including JTextFields for event data 
 * input, JList for displaying the event list, and buttons for performing actions like adding, updating, 
 * deleting events, and refreshing the event list.
 * <p>
 * This class communicates with an EventController to handle the business logic associated with 
 * event management operations and updates the view based on user actions and system responses.
 * <p>
 * The following methods are provided:
 * - showAllEvents: Displays a list of all events.
 * - eventAdded: Notifies the view that an event has been successfully added.
 * - showError: Displays an error message related to an event.
 * - eventDeleted: Notifies the view that an event has been successfully deleted.
 * - eventUpdated: Notifies the view that an event has been successfully updated.
 * - setParticipantView: Sets the participant management view for navigation.
 */

package com.mycompany.eventmanagementapp.view.screen;

import java.awt.*;
import javax.swing.*;
import java.util.List;
import java.time.LocalDate;
import java.awt.event.KeyEvent;
import java.util.regex.Pattern;
import java.awt.event.KeyAdapter;
import java.awt.event.WindowEvent;
import java.util.stream.IntStream;
import java.awt.event.WindowAdapter;
import javax.swing.border.EmptyBorder;

import com.mycompany.eventmanagementapp.model.EventModel;
import com.mycompany.eventmanagementapp.view.EventManagementView;
import com.mycompany.eventmanagementapp.controller.EventController;

public class EventManagementViewScreen extends JFrame implements EventManagementView {

	private static final long serialVersionUID = 1L;

	private JPanel contentPaneEventScreen;

	private JTextField txtEventName;

	private JTextField txtEventLocation;

	private JTextField txtEventDate;

	private JTextField txtEventId;

	private JButton btnAddEvent;

	private JButton btnUpdateEvent;

	private JButton btnDeleteEvent;

	private JButton btnParticipantScreen;

	private JButton btnRefresh;

	private JList<EventModel> eventList;

	private DefaultListModel<EventModel> eventListModel;

	private JTextArea lblErrorEvent;

	private transient EventController eventController;

	private ParticipantManagementViewScreen participantManagementView;
	
	private static final long DEFAULT_EVENT_ID = -1;

	DefaultListModel<EventModel> getEventListModel() {
		return eventListModel;
	}

	public void setEventController(EventController eventController) {
		this.eventController = eventController;
	}

	public EventManagementViewScreen() {

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent e) {
				eventListModel.removeAllElements();
				getAllEvents();
			}
		});

		setTitle("Event Management Screen");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 750, 500);
		setResizable(false);

		contentPaneEventScreen = new JPanel();
		contentPaneEventScreen.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPaneEventScreen);
		contentPaneEventScreen.setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(5, 5, 5, 5);

		// **Event Input Fields**
		JPanel inputPanelEvent = new JPanel(new GridLayout(4, 2, 5, 5));
		txtEventId = new JTextField();
		txtEventId.setName("txtEventId");
		txtEventId.setEditable(false);
		txtEventName = new JTextField();
		txtEventName.setName("txtEventName");
		txtEventLocation = new JTextField();
		txtEventLocation.setName("txtEventLocation");
		txtEventDate = new JTextField();
		txtEventDate.setName("txtEventDate");

		inputPanelEvent.add(new JLabel("Event ID:"));
		inputPanelEvent.add(txtEventId);
		inputPanelEvent.add(new JLabel("Event Name:"));
		inputPanelEvent.add(txtEventName);
		inputPanelEvent.add(new JLabel("Event Location:"));
		inputPanelEvent.add(txtEventLocation);
		inputPanelEvent.add(new JLabel("Event Date (YYYY-MM-DD):"));
		inputPanelEvent.add(txtEventDate);

		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		contentPaneEventScreen.add(inputPanelEvent, gridBagConstraints);

		// **Scrollable Event List**
		eventListModel = new DefaultListModel<>();
		eventList = new JList<>(eventListModel);
		eventList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		eventList.setName("eventList");
		eventList.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				EventModel event = (EventModel) value;
				return super.getListCellRendererComponent(list, getDisplayString(event), index, isSelected,
						cellHasFocus);
			}
		});
		JScrollPane scrollPane = new JScrollPane(eventList);

		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.weighty = 1.0;
		contentPaneEventScreen.add(scrollPane, gridBagConstraints);

		// **Buttons Panel**
		JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 5, 5));
		btnAddEvent = new JButton("Add Event");
		btnAddEvent.setName("Add Event");
		btnUpdateEvent = new JButton("Update Event");
		btnUpdateEvent.setName("Update Event");
		btnDeleteEvent = new JButton("Delete Event");
		btnDeleteEvent.setName("Delete Event");
		btnRefresh = new JButton("Refresh");
		btnRefresh.setName("Refresh");
		btnParticipantScreen = new JButton("Participant Screen");
		btnParticipantScreen.setName("Participant Screen");

		btnAddEvent.setEnabled(false);
		btnUpdateEvent.setEnabled(false);
		btnDeleteEvent.setEnabled(false);

		buttonPanel.add(btnAddEvent);
		buttonPanel.add(btnUpdateEvent);
		buttonPanel.add(btnDeleteEvent);
		buttonPanel.add(btnRefresh);
		buttonPanel.add(btnParticipantScreen);

		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.weighty = 0;
		contentPaneEventScreen.add(buttonPanel, gridBagConstraints);

		// **Error Label (Now Using JTextArea for Multi-line Wrapping)**
		lblErrorEvent = new JTextArea();
		lblErrorEvent.setName("lblError");
		clearErrorLabel();
		lblErrorEvent.setForeground(Color.RED);
		lblErrorEvent.setEditable(false);
		lblErrorEvent.setWrapStyleWord(true);
		lblErrorEvent.setLineWrap(true);
		lblErrorEvent.setOpaque(false);
		lblErrorEvent.setFocusable(false);
		lblErrorEvent.setBorder(null);

		// **Fix Height and Width of Error Label**
		lblErrorEvent.setPreferredSize(new Dimension(650, 40));
		lblErrorEvent.setMinimumSize(new Dimension(650, 40));
		lblErrorEvent.setMaximumSize(new Dimension(650, 40));

		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		contentPaneEventScreen.add(lblErrorEvent, gridBagConstraints);

		// **Button Actions**
		btnAddEvent.addActionListener(e -> new Thread(this::addEvent).start());
		btnUpdateEvent.addActionListener(e -> new Thread((this::updateEvent)).start());
		btnDeleteEvent.addActionListener(e -> new Thread((this::deleteEvent)).start());
		btnParticipantScreen.addActionListener(e -> openParticipantScreen());
		btnRefresh.addActionListener(e -> refreshScreen());

		eventList.addListSelectionListener(e -> updateSelection());

		// **Enable Add and Update Button Only When Fields Are Filled**
		KeyAdapter btnEnabler = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				toggleAddButton();
				toggleUpdateButton();
			}
		};

		txtEventName.addKeyListener(btnEnabler);
		txtEventLocation.addKeyListener(btnEnabler);
		txtEventDate.addKeyListener(btnEnabler);
	}

	private void toggleAddButton() {
		boolean isEventNameFilled = !txtEventName.getText().trim().isEmpty();
		boolean isEventLocationFilled = !txtEventLocation.getText().trim().isEmpty();
		boolean isEventDateFilled = !txtEventDate.getText().trim().isEmpty()
				&& isValidDate(txtEventDate.getText().trim());

		btnAddEvent.setEnabled(isEventNameFilled && isEventLocationFilled && isEventDateFilled);
	}

	private void toggleUpdateButton() {
		boolean isEventNameFilled = !txtEventName.getText().trim().isEmpty();
		boolean isEventLocationFilled = !txtEventLocation.getText().trim().isEmpty();
		boolean isEventDateFilled = !txtEventDate.getText().trim().isEmpty()
				&& isValidDate(txtEventDate.getText().trim());
		boolean isEventSelected = !eventList.isSelectionEmpty();

		btnUpdateEvent.setEnabled(isEventSelected && isEventNameFilled && isEventLocationFilled && isEventDateFilled);
	}

	private boolean isValidDate(String date) {
		return Pattern.matches("^\\d{4}-\\d{2}-\\d{2}$", date);
	}

	private void addEvent() {
		EventModel event = new EventModel(DEFAULT_EVENT_ID, txtEventName.getText().trim(),
				LocalDate.parse(txtEventDate.getText().trim()), txtEventLocation.getText().trim());
		eventController.addEvent(event);
	}

	private void updateEvent() {
		Long eventId = Long.parseLong(txtEventId.getText());
		EventModel event = new EventModel(txtEventName.getText().trim(), LocalDate.parse(txtEventDate.getText().trim()),
				txtEventLocation.getText().trim());
		event.setEventId(eventId);
		eventController.updateEvent(event);
	}

	private void deleteEvent() {
		eventController.deleteEvent(eventList.getSelectedValue());
	}

	private void openParticipantScreen() {
		participantManagementView.setVisible(true);
		this.dispose();
		clearErrorLabel();
		clearFieldsAndButtons();
	}

	private void refreshScreen() {
		clearErrorLabel();
		clearFieldsAndButtons();
		getAllEvents();
	}

	private void updateSelection() {
		EventModel selectedEvent = eventList.getSelectedValue();
		if (selectedEvent != null) {
			txtEventId.setText(String.valueOf(selectedEvent.getEventId()));
			txtEventName.setText(selectedEvent.getEventName());
			txtEventLocation.setText(selectedEvent.getEventLocation());
			txtEventDate.setText(String.valueOf(selectedEvent.getEventDate()));
			btnAddEvent.setEnabled(true);
			btnUpdateEvent.setEnabled(true);
			btnDeleteEvent.setEnabled(true);
		} else {
			clearFieldsAndButtons();
		}
	}

	private void clearFieldsAndButtons() {
		SwingUtilities.invokeLater(() -> {
			txtEventId.setText("");
			txtEventName.setText("");
			txtEventLocation.setText("");
			txtEventDate.setText("");
			btnAddEvent.setEnabled(false);
			btnUpdateEvent.setEnabled(false);
			btnDeleteEvent.setEnabled(false);
		});
	}

	@Override
	public void showAllEvents(List<EventModel> events) {
		eventListModel.clear();
		events.stream().forEach(eventListModel::addElement);
	}

	@Override
	public void eventAdded(EventModel event) {
		SwingUtilities.invokeLater(() -> {
			eventListModel.addElement(event);
			clearErrorLabel();
			resetFormAndClearEventList();
		});
	}

	@Override
	public void showError(String message, EventModel event) {
		SwingUtilities.invokeLater(() -> {
			lblErrorEvent.setText(message + ": " + event);
			getAllEvents();
		});
	}

	@Override
	public void eventDeleted(EventModel event) {
		SwingUtilities.invokeLater(() -> {
			eventListModel.removeElement(event);
			clearErrorLabel();
			resetFormAndClearEventList();
		});
	}

	@Override
	public void eventUpdated(EventModel event) {
		SwingUtilities.invokeLater(() -> {
			int index = IntStream.range(0, eventListModel.size())
					.filter(i -> (eventListModel.get(i).getEventId()).equals(event.getEventId())).findFirst()
					.orElse(-1);

			if (index == -1)
				return;
			eventListModel.set(index, event);
			clearErrorLabel();
			resetFormAndClearEventList();
		});
	}

	public void setParticipantView(ParticipantManagementViewScreen participantView) {
		this.participantManagementView = participantView;

	}

	private String getDisplayString(EventModel event) {
		return event.getEventId() + " | " + event.getEventName() + " | " + event.getEventLocation() + " | "
				+ event.getEventDate();
	}
	
	private void resetFormAndClearEventList() {
		clearFieldsAndButtons();
		eventList.clearSelection();
	}
	
	private void clearErrorLabel() {
		lblErrorEvent.setText(" ");
	}
	
	private void getAllEvents() {
		eventController.getAllEvents();
	}
}