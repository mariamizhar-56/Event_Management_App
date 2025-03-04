package com.mycompany.eventmanagementapp;

import java.awt.EventQueue;
import java.util.concurrent.Callable;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import com.mycompany.eventmanagementapp.controller.EventController;
import com.mycompany.eventmanagementapp.controller.ParticipantController;
import com.mycompany.eventmanagementapp.repository.mysql.EventMySqlRepository;
import com.mycompany.eventmanagementapp.repository.mysql.ParticipantMySqlRepository;
import com.mycompany.eventmanagementapp.view.screen.EventManagementViewScreen;
import com.mycompany.eventmanagementapp.view.screen.ParticipantManagementViewScreen;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

// Main class for the Event Management Swing Application
@Command(mixinStandardHelpOptions = true)
public class EventManagementApp implements Callable<Void> {

	/**
	 * The database URL to connect to the MySQL database. This is set via the
	 * command line option "--mysql-DB_URL".
	 */
	@Option(names = { "--mysql-DB_URL" }, description = "mysql DB_URL ")
	private String url = "jdbc:mysql://localhost:3307/event_management_app";

	/**
	 * The username for the MySQL database. This is set via the command line option
	 * "--mysql-user".
	 */
	@Option(names = { "--mysql-user" }, description = "mysql user")
	private String user = "test";

	/**
	 * The password for the MySQL database. This is set via the command line option
	 * "--mysql-pass".
	 */
	@Option(names = { "--mysql-pass" }, description = "mysql pass")
	private String pass = "test";

	// Logger for logging errors and information.
	private static final Logger LOGGER = LogManager.getLogger(EventManagementApp.class);

	// Hibernate service registry for database configuration.
	private StandardServiceRegistry registry;

	public static void main(String[] args) {
		try {
			// Sets the look and feel of the UI to GTK, if available.
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			LOGGER.error("context", e);
		}
		// Initializes the application using picocli's CommandLine to parse command-line
		// arguments.
		new CommandLine(new EventManagementApp()).execute(args);
	}

	// Method invoked when the application is called.
	@Override
	public Void call() throws Exception {
		// Runs the UI-related code on the EventQueue thread to ensure thread safety in
		// Swing.
		EventQueue.invokeLater(() -> {
			try {
				// Fetch the current environment property to determine which configuration to
				// load.
				String environment = System.getProperty("ENVIRONMENT");

				// If the environment is set to "testWithEclipes", load the testing
				// configuration.
				if ("testWithEclipes".equals(environment)) {
					registry = new StandardServiceRegistryBuilder().configure("hibernate-IT.cfg.xml")
							.applySetting("hibernate.connection.url", url)
							.applySetting("hibernate.connection.username", user)
							.applySetting("hibernate.hbm2ddl.auto", "validate")
							.applySetting("hibernate.connection.password", pass).build();
				} else {
					// For other environments, load the default configuration.
					registry = new StandardServiceRegistryBuilder().configure().build();
				}

				// Sets up Hibernate's SessionFactory for managing database connections.
				MetadataSources metadataSources = new MetadataSources(registry);
				SessionFactory sessionFactory = metadataSources.buildMetadata().buildSessionFactory();

				// Initialize the Swing views for event and participant.
				EventManagementViewScreen eventView = new EventManagementViewScreen();
				ParticipantManagementViewScreen participantView = new ParticipantManagementViewScreen();

				// Initialize the repositories for interacting with the MySQL database.
				EventMySqlRepository eventRepository = new EventMySqlRepository(sessionFactory);
				ParticipantMySqlRepository participantRepository = new ParticipantMySqlRepository(sessionFactory);

				// Create controllers to handle user interactions and business logic.
				EventController eventController = new EventController(eventView, eventRepository);
				ParticipantController participantController = new ParticipantController(participantView,
						participantRepository, eventRepository);

				// Link views with their respective controllers.
				eventView.setEventController(eventController);
				eventView.setParticipantView(participantView);

				// Load all events and make the view visible to the user.
				eventController.getAllEvents();
				eventView.setVisible(true);

				// Link participant view to participant controller and load all participants.
				participantView.setParticipantController(participantController);
				participantController.getAllEvents();
				participantController.getAllParticipants();
				participantView.setEventView(eventView);

			} catch (Exception e) {
				// Logs any exceptions that occur during initialization.
				LOGGER.error("context", e);
			}
		});
		return null;
	}
}
