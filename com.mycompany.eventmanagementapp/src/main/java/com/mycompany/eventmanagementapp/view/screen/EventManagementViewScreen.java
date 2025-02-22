package com.mycompany.eventmanagementapp.view.screen;

import com.mycompany.eventmanagementapp.controller.EventController;
import com.mycompany.eventmanagementapp.model.EventModel;
import com.mycompany.eventmanagementapp.view.EventManagementView;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class EventManagementViewScreen extends JFrame implements EventManagementView {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtEventName, txtEventLocation, txtEventDate, txtEventId;
	private JButton btnAddEvent, btnUpdateEvent, btnDeleteEvent, btnParticipantScreen, btnRefresh;
	private JList<EventModel> eventList;
	private DefaultListModel<EventModel> eventListModel;
	private JTextArea lblError;
	private transient EventController eventController;
	private ParticipantManagementViewScreen participantManagementView;

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
				eventController.getAllEvents();

			}
		});

		setTitle("Event Management Screen");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 750, 500);
		setResizable(false);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);

		// **Event Input Fields**
		JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
		txtEventId = new JTextField();
		txtEventId.setName("txtEventId");
		txtEventId.setEditable(false);
		txtEventName = new JTextField();
		txtEventName.setName("txtEventName");
		txtEventLocation = new JTextField();
		txtEventLocation.setName("txtEventLocation");
		txtEventDate = new JTextField();
		txtEventDate.setName("txtEventDate");

		inputPanel.add(new JLabel("Event ID:"));
		inputPanel.add(txtEventId);
		inputPanel.add(new JLabel("Event Name:"));
		inputPanel.add(txtEventName);
		inputPanel.add(new JLabel("Event Location:"));
		inputPanel.add(txtEventLocation);
		inputPanel.add(new JLabel("Event Date (YYYY-MM-DD):"));
		inputPanel.add(txtEventDate);

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		gbc.gridy = 0;
		contentPane.add(inputPanel, gbc);

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

		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weighty = 1.0;
		contentPane.add(scrollPane, gbc);

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

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weighty = 0;
		contentPane.add(buttonPanel, gbc);

		// **Error Label (Now Using JTextArea for Multi-line Wrapping)**
		lblError = new JTextArea();
		lblError.setName("lblError");
		lblError.setText(" ");
		lblError.setForeground(Color.RED);
		lblError.setEditable(false);
		lblError.setWrapStyleWord(true);
		lblError.setLineWrap(true);
		lblError.setOpaque(false);
		lblError.setFocusable(false);
		lblError.setBorder(null);

		// **Fix Height and Width of Error Label**
		lblError.setPreferredSize(new Dimension(650, 40));
		lblError.setMinimumSize(new Dimension(650, 40));
		lblError.setMaximumSize(new Dimension(650, 40));

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		gbc.gridy = 3;
		contentPane.add(lblError, gbc);

		// **Button Actions**
		btnAddEvent.addActionListener(e -> new Thread(() -> {
			addEvent();
		}).start());
		btnUpdateEvent.addActionListener(e -> new Thread(() -> {
			updateEvent();
		}).start());
		btnDeleteEvent.addActionListener(e -> new Thread(() -> {
			deleteEvent();
		}).start());
		btnParticipantScreen.addActionListener(e -> openParticipantScreen());
		btnRefresh.addActionListener(e -> RefreshScreen());
		

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
		EventModel event = new EventModel(-1, txtEventName.getText().trim(),
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
		lblError.setText(" ");
		clearFieldsAndButtons();
	}
	
	private void RefreshScreen() {
		lblError.setText(" ");
		clearFieldsAndButtons();
		eventController.getAllEvents();
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
		 eventListModel.addAll(events);

		//events.stream().forEach(eventListModel::addElement);
	}

	@Override
	public void eventAdded(EventModel event) {
		SwingUtilities.invokeLater(() -> {
			eventListModel.addElement(event);
			lblError.setText(" ");
		});
		clearFieldsAndButtons();
		eventList.clearSelection();
	}

	@Override
	public void showError(String message, EventModel event) {
		SwingUtilities.invokeLater(() -> {
			lblError.setText(message + ": " + event);
			eventController.getAllEvents();
		});
	}

	@Override
	public void eventDeleted(EventModel event) {
		SwingUtilities.invokeLater(() -> {
			eventListModel.removeElement(event);
			lblError.setText(" ");
		});

		// eventListModel.removeElement(event);
		clearFieldsAndButtons();
		eventList.clearSelection();
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
			lblError.setText(" ");
		});
		// eventList.repaint();
		clearFieldsAndButtons();
		eventList.clearSelection();
	}

	public void setParticipantView(ParticipantManagementViewScreen participantView) {
		this.participantManagementView = participantView;

	}

	private String getDisplayString(EventModel event) {
		return event.getEventId() + " | " + event.getEventName() + " | " + event.getEventLocation() + " | "
				+ event.getEventDate();
	}
}
