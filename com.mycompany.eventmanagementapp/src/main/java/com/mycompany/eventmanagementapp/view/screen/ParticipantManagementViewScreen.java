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
import java.util.List;
import java.util.regex.Pattern;

public class ParticipantManagementViewScreen extends JFrame implements ParticipantManagementView {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtParticipantId, txtParticipantName, txtParticipantEmail, txtEventId;
	private JButton btnAddParticipant, btnUpdateParticipant, btnDeleteParticipant, btnEventScreen;
	private JList<ParticipantModel> participantList;
	private DefaultListModel<ParticipantModel> participantListModel;
	private JList<EventModel> eventListForParticipant;
	private DefaultListModel<EventModel> eventListModel;
	private JTextArea lblError;
	private ParticipantController participantController;
	private EventManagementViewScreen eventManagementView;
	
	public void setParticipantController(ParticipantController participantController) {
		this.participantController = participantController;
	}
	public ParticipantController getParticipantController() {
		return this.participantController;
	}

	public ParticipantManagementViewScreen() {
		setTitle("Participant Management Screen");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 500);
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
		txtParticipantId.setEditable(false);
		txtEventId = new JTextField();
		txtParticipantName = new JTextField();
		txtParticipantEmail = new JTextField();

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
		JScrollPane participantScrollPane = new JScrollPane(participantList);

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
		JScrollPane eventScrollPane = new JScrollPane(eventListForParticipant);

		gbc.gridx = 1;
		contentPane.add(eventScrollPane, gbc);

		// **Buttons Panel**
		JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 5, 5));
		btnAddParticipant = new JButton("Add Participant");
		btnUpdateParticipant = new JButton("Update Participant");
		btnDeleteParticipant = new JButton("Delete Participant");
		btnEventScreen = new JButton("Event Screen");

		btnAddParticipant.setEnabled(false);
		btnUpdateParticipant.setEnabled(false);
		btnDeleteParticipant.setEnabled(false);

		buttonPanel.add(btnAddParticipant);
		buttonPanel.add(btnUpdateParticipant);
		buttonPanel.add(btnDeleteParticipant);
		buttonPanel.add(btnEventScreen);

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weighty = 0;
		contentPane.add(buttonPanel, gbc);

		// **Error Label (Fixed Size)**
		lblError = new JTextArea();
		lblError.setForeground(Color.RED);
		lblError.setEditable(false);
		lblError.setWrapStyleWord(true);
		lblError.setLineWrap(true);
		lblError.setOpaque(false);
		lblError.setFocusable(false);
		lblError.setBorder(null);
		lblError.setPreferredSize(new Dimension(500, 40));
		lblError.setMinimumSize(new Dimension(500, 40));
		lblError.setMaximumSize(new Dimension(500, 40));

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		gbc.gridy = 3;
		contentPane.add(lblError, gbc);

		// **Button Actions**
		btnAddParticipant.addActionListener(e -> new Thread(() -> {addParticipant();}).start());
		btnUpdateParticipant.addActionListener(e -> new Thread(() -> {updateParticipant();}).start());
		btnDeleteParticipant.addActionListener(e -> new Thread(() -> {deleteParticipant();}).start());
		btnEventScreen.addActionListener(e -> openEventScreen());

		participantList.addListSelectionListener(e -> updateSelection());

		// **Enable Add Button Only When Fields Are Filled**
		DocumentListener docListener = new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				toggleAddButton();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				toggleAddButton();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				toggleAddButton();
			}
		};

		txtParticipantName.getDocument().addDocumentListener(docListener);
		txtParticipantEmail.getDocument().addDocumentListener(docListener);
		txtEventId.getDocument().addDocumentListener(docListener);
	}

	private void toggleAddButton() {
		boolean isParticipantNameFilled = !txtParticipantName.getText().trim().isEmpty();
		boolean isParticipantEmailFilled = !txtParticipantEmail.getText().trim().isEmpty();
		boolean isEventIdFilled = !txtEventId.getText().trim().isEmpty() && isValidNumber(txtEventId.getText().trim());

		btnAddParticipant.setEnabled(isParticipantNameFilled && isParticipantEmailFilled && isEventIdFilled);
	}

	private boolean isValidNumber(String text) {
		return Pattern.matches("\\d+", text);
	}

	private void addParticipant() {
		try {
			EventModel event = new EventModel();
			event.setEventId(Long.parseLong(txtEventId.getText().trim()));

			ParticipantModel participant = new ParticipantModel(txtParticipantName.getText().trim(),
					txtParticipantEmail.getText().trim());

			participantController.addParticipant(participant, event);
		} catch (Exception e) {
			lblError.setText("Error: " + e.getMessage());
		}
	}

	private void updateParticipant() {
		try {
			Long participantId = Long.parseLong(txtParticipantId.getText());
			ParticipantModel participant = new ParticipantModel(participantId, txtParticipantName.getText().trim(),
					txtParticipantEmail.getText().trim());

			participantController.updateParticipant(participant);
		} catch (Exception e) {
			lblError.setText("Error: " + e.getMessage());
		}
	}

	private void deleteParticipant() {
		try {
			ParticipantModel participant = participantList.getSelectedValue();
			EventModel selectedEvent = eventListForParticipant.getSelectedValue();

			if (participant == null || selectedEvent == null) {
				lblError.setText("Error: Select a participant and an event.");
				return;
			}

			participantController.deleteParticipant(participant, selectedEvent);
		} catch (Exception e) {
			lblError.setText("Error: " + e.getMessage());
		}
	}

	private void openEventScreen() {
		eventManagementView.clearEventListModel();
		eventManagementView.getEventController().getAllEvents();
		eventManagementView.setVisible(true);
    	this.dispose();
    	lblError.setText(" ");
        clearFields();
	}

	private void clearFields() {
		SwingUtilities.invokeLater(() -> {
			txtParticipantId.setText("");
			txtParticipantName.setText("");
			txtParticipantEmail.setText("");
			txtEventId.setText("");
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
			btnDeleteParticipant.setEnabled(true);
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
			participantController.getAllParticipants();
			participantController.getAllEvents();
			lblError.setText(" ");
		});
		clearFields();
	}

	@Override
	public void participantDeleted(ParticipantModel participant) {
		SwingUtilities.invokeLater(() -> {
			participantController.getAllParticipants();
			participantController.getAllEvents();
			lblError.setText(" ");
		});
		clearFields();
	}

	@Override
	public void participantUpdated(ParticipantModel participant) {
		SwingUtilities.invokeLater(() -> {
			participantController.getAllParticipants();
			participantController.getAllEvents();
			lblError.setText(" ");
		});
		clearFields();
	}

	@Override
	public void showError(String message, ParticipantModel participant) {
		SwingUtilities.invokeLater(() -> lblError.setText(message + ": " + participant));
	}
	
	public void setEventView(EventManagementViewScreen eventView) {
		this.eventManagementView = eventView;

	}
	
	public void clearEventListModel() {
		eventListModel.clear();
	}
	
	public void clearParticipantListModel() {
		participantListModel.clear();
	}
}
