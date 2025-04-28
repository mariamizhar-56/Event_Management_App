/**
 * EventManagementApp is the main entry point for the Event Management Swing Application.
 * This application allows users to manage events and participants, including the ability to:
 * - Add, update, and delete events
 * - Add, update, and delete participants
 * - Link participants to events
 * 
 * The application is built using Java Swing for the user interface and Hibernate for database interaction.
 * It uses picocli for command-line argument parsing and logging with Apache Log4j for error reporting.
 * 
 * The core components include:
 * - EventController: Manages business logic related to event operations.
 * - ParticipantController: Manages business logic related to participant operations.
 * - EventManagementViewScreen: The UI screen for managing events.
 * - ParticipantManagementViewScreen: The UI screen for managing participants.
 * - EventMySqlRepository: Handles database interactions related to events.
 * - ParticipantMySqlRepository: Handles database interactions related to participants.
 * 
 * The application supports different environments, such as:
 * - Default environment: Uses the default Hibernate configuration.
 * - Test environment: Configured for testing purposes with a separate Hibernate configuration file.
 * 
 * The program is initialized in a thread-safe manner using the EventQueue to ensure that Swing operations are executed on the Event Dispatch Thread (EDT).
 * 
 * Command-line options:
 * - --mysql-DB_URL: URL for the MySQL database.
 * - --mysql-user: MySQL database username.
 * - --mysql-pass: MySQL database password.
 * 
 * Logging:
 * - The application logs errors using Apache Log4j for debugging and tracking issues during runtime.
 */

package com.mycompany.eventmanagementapp;

import picocli.CommandLine;
import java.awt.EventQueue;
import javax.swing.UIManager;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;
import org.hibernate.SessionFactory;
import java.util.concurrent.Callable;
import org.apache.logging.log4j.Logger;
import org.hibernate.boot.MetadataSources;
import org.apache.logging.log4j.LogManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import com.mycompany.eventmanagementapp.controller.EventController;
import com.mycompany.eventmanagementapp.controller.ParticipantController;
import com.mycompany.eventmanagementapp.repository.mysql.EventMySqlRepository;
import com.mycompany.eventmanagementapp.view.screen.EventManagementViewScreen;
import com.mycompany.eventmanagementapp.repository.mysql.ParticipantMySqlRepository;
import com.mycompany.eventmanagementapp.view.screen.ParticipantManagementViewScreen;

// Main class for the Event Management Swing Application
@Command(mixinStandardHelpOptions = true)
public class EventManagementApp implements Callable<Void> {

	/**
	 * The database URL to connect to the MySQL database. This is set via the
	 * command line option "--mysql-DB_URL".
	 */
	@Option(names = { "--mysql-DB_URL" }, description = "mysql DB_URL ")
	private String url = "jdbc:mysql://localhost:3306/event_management_app";

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