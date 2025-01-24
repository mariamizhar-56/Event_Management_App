package com.mycompany.eventmanagementapp.view.screen;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollPane;

public class EventManagementViewScreen extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtIdtextbox;
	private JTextField txtNametextbox;
	private JTextField txtDatetextbox;
	private JTextField txtLocationtextbox;
	private JTextField txtListtextbox;
	private JButton button;
	private JList list;
	private JButton btnNewButton_1;
	private JLabel lblNewLabel_4;
	private JLabel lblNewLabel_5;
	private JLabel lblNewLabel_6;
	private JScrollPane scrollPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EventManagementViewScreen frame = new EventManagementViewScreen();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public EventManagementViewScreen() {
		setTitle("Event View");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 0, 0, 0 };
		gbl_contentPane.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_contentPane.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);

		JLabel lblNewLabel = new JLabel("Id");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		contentPane.add(lblNewLabel, gbc_lblNewLabel);

		txtIdtextbox = new JTextField();
		txtIdtextbox.setName("IdTextBox");
		GridBagConstraints gbc_txtIdtextbox = new GridBagConstraints();
		gbc_txtIdtextbox.insets = new Insets(0, 0, 5, 0);
		gbc_txtIdtextbox.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtIdtextbox.gridx = 1;
		gbc_txtIdtextbox.gridy = 0;
		contentPane.add(txtIdtextbox, gbc_txtIdtextbox);
		txtIdtextbox.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("Name");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 1;
		contentPane.add(lblNewLabel_1, gbc_lblNewLabel_1);

		txtNametextbox = new JTextField();
		txtNametextbox.setName("NameTextBox");
		GridBagConstraints gbc_txtNametextbox = new GridBagConstraints();
		gbc_txtNametextbox.insets = new Insets(0, 0, 5, 0);
		gbc_txtNametextbox.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtNametextbox.gridx = 1;
		gbc_txtNametextbox.gridy = 1;
		contentPane.add(txtNametextbox, gbc_txtNametextbox);
		txtNametextbox.setColumns(10);

		JLabel lblNewLabel_2 = new JLabel("Date");
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 2;
		contentPane.add(lblNewLabel_2, gbc_lblNewLabel_2);

		txtDatetextbox = new JTextField();
		txtDatetextbox.setName("DateTextBox");
		GridBagConstraints gbc_txtDatetextbox = new GridBagConstraints();
		gbc_txtDatetextbox.insets = new Insets(0, 0, 5, 0);
		gbc_txtDatetextbox.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtDatetextbox.gridx = 1;
		gbc_txtDatetextbox.gridy = 2;
		contentPane.add(txtDatetextbox, gbc_txtDatetextbox);
		txtDatetextbox.setColumns(10);

		JLabel lblNewLabel_3 = new JLabel("Location");
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_3.gridx = 0;
		gbc_lblNewLabel_3.gridy = 3;
		contentPane.add(lblNewLabel_3, gbc_lblNewLabel_3);

		txtLocationtextbox = new JTextField();
		txtLocationtextbox.setName("LocationTextBox");
		GridBagConstraints gbc_txtLocationtextbox = new GridBagConstraints();
		gbc_txtLocationtextbox.insets = new Insets(0, 0, 5, 0);
		gbc_txtLocationtextbox.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtLocationtextbox.gridx = 1;
		gbc_txtLocationtextbox.gridy = 3;
		contentPane.add(txtLocationtextbox, gbc_txtLocationtextbox);
		txtLocationtextbox.setColumns(10);

		JButton AddButton = new JButton("Add");
		GridBagConstraints gbc_AddButton = new GridBagConstraints();
		gbc_AddButton.insets = new Insets(0, 0, 5, 0);
		gbc_AddButton.gridwidth = 2;
		gbc_AddButton.gridx = 0;
		gbc_AddButton.gridy = 4;
		contentPane.add(AddButton, gbc_AddButton);
		
		JButton UpdateButton = new JButton("Update");
		GridBagConstraints gbc_UpdateButton = new GridBagConstraints();
		gbc_UpdateButton.insets = new Insets(0, 0, 5, 0);
		gbc_UpdateButton.gridwidth = 2;
		gbc_UpdateButton.gridx = 0;
		gbc_UpdateButton.gridy = 5;
		contentPane.add(UpdateButton, gbc_UpdateButton);
		
		scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridwidth = 2;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 6;
		contentPane.add(scrollPane, gbc_scrollPane);

		list = new JList();
		scrollPane.setViewportView(list);
		list.setName("List<Participant>");
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		btnNewButton_1 = new JButton("Delete Event");
		GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
		gbc_btnNewButton_1.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton_1.gridwidth = 2;
		gbc_btnNewButton_1.gridx = 0;
		gbc_btnNewButton_1.gridy = 7;
		contentPane.add(btnNewButton_1, gbc_btnNewButton_1);

		lblNewLabel_4 = new JLabel("");
		lblNewLabel_4.setName("ErrorLabel1");
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_4.gridwidth = 2;
		gbc_lblNewLabel_4.gridx = 0;
		gbc_lblNewLabel_4.gridy = 8;
		contentPane.add(lblNewLabel_4, gbc_lblNewLabel_4);
		
		lblNewLabel_5 = new JLabel("");
		lblNewLabel_5.setName("ErrorLabel2");
		GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
		gbc_lblNewLabel_5.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_5.gridwidth = 2;
		gbc_lblNewLabel_5.gridx = 0;
		gbc_lblNewLabel_5.gridy = 9;
		contentPane.add(lblNewLabel_5, gbc_lblNewLabel_5);
		
		lblNewLabel_6 = new JLabel("");
		lblNewLabel_6.setName("ErrorLabel3");
		GridBagConstraints gbc_lblNewLabel_6 = new GridBagConstraints();
		gbc_lblNewLabel_6.gridwidth = 2;
		gbc_lblNewLabel_6.gridx = 0;
		gbc_lblNewLabel_6.gridy = 10;
		contentPane.add(lblNewLabel_6, gbc_lblNewLabel_6);

	}

}
