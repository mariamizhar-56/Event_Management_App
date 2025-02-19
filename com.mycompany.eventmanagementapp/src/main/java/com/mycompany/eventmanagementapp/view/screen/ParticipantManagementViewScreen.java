package com.mycompany.eventmanagementapp.view.screen;

import com.mycompany.eventmanagementapp.controller.EventController;
import com.mycompany.eventmanagementapp.controller.ParticipantController;
import com.mycompany.eventmanagementapp.model.EventModel;
import com.mycompany.eventmanagementapp.model.ParticipantModel;
import com.mycompany.eventmanagementapp.view.ParticipantManagementView;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class ParticipantManagementViewScreen extends JFrame implements ParticipantManagementView {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtParticipantId, txtParticipantName, txtParticipantEmail, txtEventId;
	private JButton btnAddParticipant, btnUpdateParticipant, btnDeleteParticipant, btnEventScreen, btnRefresh;
	private JList<ParticipantModel> participantList;
	private DefaultListModel<ParticipantModel> participantListModel;
	private JList<EventModel> eventListForParticipant;
	private DefaultListModel<EventModel> eventListModel;
	private JTextArea lblError;
	private ParticipantController participantController;
	private EventManagementViewScreen eventManagementView;

	public DefaultListModel<ParticipantModel> getParticipantListModel() {
		return participantListModel;
	}

	/*
	 * public DefaultListModel<EventModel> getParticipantEventListModel() { return
	 * eventListModel; }
	 */

	public void setParticipantController(ParticipantController participantController) {
		this.participantController = participantController;
	}

	public ParticipantManagementViewScreen() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent e) {
				eventListModel.removeAllElements();
				participantListModel.removeAllElements();
				participantController.getAllEvents();
				participantController.getAllParticipants();
			}
		});

		setTitle("Participant Management Screen");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 750, 500);
		setResizable(false);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);

		// **Participant Input Fields**
		JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
		txtParticipantId = new JTextField();
		txtParticipantId.setName("txtParticipantId");
		txtParticipantId.setEditable(false);
		txtEventId = new JTextField();
		txtEventId.setName("txtEventId");
		txtParticipantName = new JTextField();
		txtParticipantName.setName("txtParticipantName");
		txtParticipantEmail = new JTextField();
		txtParticipantEmail.setName("txtParticipantEmail");

		inputPanel.add(new JLabel("Participant ID:"));
		inputPanel.add(txtParticipantId);
		inputPanel.add(new JLabel("Event ID:"));
		inputPanel.add(txtEventId);
		inputPanel.add(new JLabel("Participant Name:"));
		inputPanel.add(txtParticipantName);
		inputPanel.add(new JLabel("Participant Email:"));
		inputPanel.add(txtParticipantEmail);

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		gbc.gridy = 0;
		contentPane.add(inputPanel, gbc);

		// **Participant List**
		participantListModel = new DefaultListModel<>();
		participantList = new JList<>(participantListModel);
		participantList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		participantList.setName("participantList");
		JScrollPane participantScrollPane = new JScrollPane(participantList);
		participantScrollPane.setPreferredSize(new Dimension(350, 150));

		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weighty = 1.0;
		contentPane.add(participantScrollPane, gbc);

		// **Nested Event List for Selected Participant**
		eventListModel = new DefaultListModel<>();
		eventListForParticipant = new JList<>(eventListModel);
		eventListForParticipant.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		eventListForParticipant.setName("eventListForParticipant");
		JScrollPane eventScrollPane = new JScrollPane(eventListForParticipant);
		eventScrollPane.setPreferredSize(new Dimension(350, 150));

		gbc.gridx = 1;
		contentPane.add(eventScrollPane, gbc);

		// **Buttons Panel**
		JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 5, 5));
		btnAddParticipant = new JButton("Add Participant");
		btnAddParticipant.setName("Add Participant");
		btnUpdateParticipant = new JButton("Update Participant");
		btnUpdateParticipant.setName("Update Participant");
		btnDeleteParticipant = new JButton("Delete Participant");
		btnDeleteParticipant.setName("Delete Participant");
		btnRefresh = new JButton("Refresh");
		btnRefresh.setName("Refresh");
		btnEventScreen = new JButton("Event Screen");
		btnEventScreen.setName("Event Screen");

		btnAddParticipant.setEnabled(false);
		btnUpdateParticipant.setEnabled(false);
		btnDeleteParticipant.setEnabled(false);

		buttonPanel.add(btnAddParticipant);
		buttonPanel.add(btnUpdateParticipant);
		buttonPanel.add(btnDeleteParticipant);
		buttonPanel.add(btnRefresh);
		buttonPanel.add(btnEventScreen);

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weighty = 0;
		contentPane.add(buttonPanel, gbc);

		// **Error Label (Fixed Size)**
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
		lblError.setPreferredSize(new Dimension(650, 40));
		lblError.setMinimumSize(new Dimension(650, 40));
		lblError.setMaximumSize(new Dimension(650, 40));

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		gbc.gridy = 3;
		contentPane.add(lblError, gbc);

		// **Button Actions**
		btnAddParticipant.addActionListener(e -> new Thread(() -> {
			addParticipant();
		}).start());
		btnUpdateParticipant.addActionListener(e -> new Thread(() -> {
			updateParticipant();
		}).start());
		btnDeleteParticipant.addActionListener(e -> new Thread(() -> {
			deleteParticipant();
		}).start());
		btnEventScreen.addActionListener(e -> openEventScreen());
		btnRefresh.addActionListener(e -> RefreshScreen());

		participantList.addListSelectionListener(e -> updateSelection());

		eventListForParticipant.addListSelectionListener(e -> updateEventSelection());

		// **Enable Add and Update Button Only When Fields Are Filled**
		KeyAdapter btnEnabler = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				toggleAddButton();
				toggleUpdateButton();
			}
		};

		txtParticipantName.addKeyListener(btnEnabler);
		txtParticipantEmail.addKeyListener(btnEnabler);
		txtEventId.addKeyListener(btnEnabler);
	}

	private void toggleAddButton() {
		boolean isParticipantNameFilled = !txtParticipantName.getText().trim().isEmpty();
		boolean isParticipantEmailFilled = !txtParticipantEmail.getText().trim().isEmpty();
		boolean isEventIdFilled = !txtEventId.getText().trim().isEmpty() && isValidNumber(txtEventId.getText().trim());

		btnAddParticipant.setEnabled(isParticipantNameFilled && isParticipantEmailFilled && isEventIdFilled);
	}

	private void toggleUpdateButton() {
		boolean isParticipantNameFilled = !txtParticipantName.getText().trim().isEmpty();
		boolean isParticipantEmailFilled = !txtParticipantEmail.getText().trim().isEmpty();
		boolean isParticipantSelected = !participantList.isSelectionEmpty();

		btnUpdateParticipant.setEnabled(isParticipantNameFilled && isParticipantEmailFilled && isParticipantSelected);
	}

	private boolean isValidNumber(String text) {
		return Pattern.matches("\\d+", text);
	}

	private void addParticipant() {
		EventModel event = new EventModel();
		event.setEventId(Long.parseLong(txtEventId.getText().trim()));

		ParticipantModel participant = new ParticipantModel(txtParticipantName.getText().trim(),
				txtParticipantEmail.getText().trim());

		participantController.addParticipant(participant, event);
	}

	private void updateParticipant() {
		ParticipantModel selectedParticipant = participantList.getSelectedValue();
		selectedParticipant.setParticipantName(txtParticipantName.getText().trim());

		participantController.updateParticipant(selectedParticipant);
	}

	private void deleteParticipant() {
		ParticipantModel participant = participantList.getSelectedValue();
		EventModel selectedEvent = eventListForParticipant.getSelectedValue();

		participantController.deleteParticipant(participant, selectedEvent);
	}

	private void openEventScreen() {
		eventManagementView.setVisible(true);
		this.dispose();
		lblError.setText(" ");
		clearFieldsAndButtons();
	}

	private void RefreshScreen() {
		lblError.setText(" ");
		clearFieldsAndButtons();
		participantController.getAllParticipants();
		participantController.getAllEvents();
	}
	
	private void clearFieldsAndButtons() {
		SwingUtilities.invokeLater(() -> {
			txtParticipantId.setText("");
			txtParticipantName.setText("");
			txtParticipantEmail.setText("");
			txtEventId.setText("");
			btnAddParticipant.setEnabled(false);
			btnUpdateParticipant.setEnabled(false);
			btnDeleteParticipant.setEnabled(false);
		});
	}

	private void updateSelection() {
		ParticipantModel selectedParticipant = participantList.getSelectedValue();
		if (selectedParticipant != null) {
			txtParticipantId.setText(String.valueOf(selectedParticipant.getParticipantId()));
			txtParticipantName.setText(selectedParticipant.getParticipantName());
			txtParticipantEmail.setText(selectedParticipant.getParticipantEmail());

			eventListModel.clear();
			eventListModel.addAll(selectedParticipant.getEvents());
			btnUpdateParticipant.setEnabled(true);
			// btnDeleteParticipant.setEnabled(true);
		} else {
			btnUpdateParticipant.setEnabled(false);
		}
	}

	private void updateEventSelection() {
		EventModel selectedEvent = eventListForParticipant.getSelectedValue();
		ParticipantModel selectedParticipant = participantList.getSelectedValue();
		if (selectedParticipant != null && selectedEvent != null) {
			btnDeleteParticipant.setEnabled(true);
		} else {
			btnDeleteParticipant.setEnabled(false);
		}
	}

	// **Implementing All Required Methods from ParticipantManagementView**
	@Override
	public void showAllParticipants(List<ParticipantModel> participants) {
		participantListModel.clear();
		participantListModel.addAll(participants);
	}

	@Override
	public void showAllEvents(List<EventModel> events) {
		eventListModel.clear();
		eventListModel.addAll(events);
	}

	@Override
	public void participantAdded(ParticipantModel participant) {
		SwingUtilities.invokeLater(() -> {
			participantListModel.addElement(participant);
			// participantController.getAllParticipants();
			// participantController.getAllEvents();
			lblError.setText(" ");
			participantController.getAllEvents();
		});
		clearFieldsAndButtons();
	}

	@Override
	public void participantDeleted(ParticipantModel participant) {
		SwingUtilities.invokeLater(() -> {
			participantListModel.removeElement(participant);

			// participantController.getAllParticipants();
			// participantController.getAllEvents();
			lblError.setText(" ");
			participantController.getAllEvents();
			participantList.clearSelection();
		});
		clearFieldsAndButtons();
	}

	@Override
	public void participantUpdated(ParticipantModel participant) {
		SwingUtilities.invokeLater(() -> {
			int index = IntStream.range(0, participantListModel.size()).filter(
					i -> (participantListModel.get(i).getParticipantId()).equals(participant.getParticipantId()))
					.findFirst().orElse(-1);

			if (index == -1)
				return;
			participantListModel.set(index, participant);
			// lblError.setText(" ");
			// participantController.getAllParticipants();
			lblError.setText(" ");
			participantController.getAllEvents();
			participantList.clearSelection();
		});

		clearFieldsAndButtons();
	}

	@Override
	public void showError(String message, ParticipantModel participant) {
		SwingUtilities.invokeLater(() -> lblError.setText(message + ": " + participant));
	}

	public void setEventView(EventManagementViewScreen eventView) {
		this.eventManagementView = eventView;

	}
}
