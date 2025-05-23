package com.mycompany.eventmanagementapp.dbconfigurations;

import static org.awaitility.Awaitility.await;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.testcontainers.containers.MySQLContainer;

public class MavenContainerConfig implements DatabaseConfiguration {

	private static final String JDBC_URL = "jdbc:mysql://localhost:3306/event_management_app";
	private static final String DB_USERNAME = "test";
	private static final String DB_PASSWORD = "test";

	private static final int MAX_RETRIES = 10;
	private static final long RETRY_DELAY_SECONDS = 10;

	private static final String HIBERNATE_XML = "hibernate-IT.cfg.xml";

	private StandardServiceRegistry registry;

	@Override
	public void StartDatabaseConnection() {
		int attempt = 0;
		while (attempt < MAX_RETRIES) {
			try {
				// Wait until MySQL is reachable and metadata is accessible
				await().atMost(RETRY_DELAY_SECONDS, TimeUnit.SECONDS).pollInterval(2, TimeUnit.SECONDS).until(() -> {
					try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USERNAME, DB_PASSWORD)) {
						if (conn.isValid(2)) {
							try (ResultSet rs = conn.getMetaData().getTables(null, null, null,
									new String[] { "TABLE" })) {
								return rs != null;
							}
						}
						return false;
					} catch (SQLException e) {
						System.out.println("Waiting for DB readiness: " + e.getMessage());
						return false;
					}
				});

				// Hibernate registry build
				registry = new StandardServiceRegistryBuilder().configure(HIBERNATE_XML)
						.applySetting("hibernate.connection.url", JDBC_URL)
						.applySetting("hibernate.connection.username", DB_USERNAME)
						.applySetting("hibernate.connection.password", DB_PASSWORD).build();

				System.out.println("✅ Connected to MySQL successfully and Hibernate registry initialized.");
				break;

			} catch (Exception e) {
				attempt++;
				System.out.println("❌ Attempt " + attempt + " failed: " + e.getMessage());
				if (attempt < MAX_RETRIES) {
					await().atMost(RETRY_DELAY_SECONDS, TimeUnit.SECONDS);
				}
			}
		}
	}

	@Override
	public StandardServiceRegistry getServiceRegistry() {
		return registry;
	}

	@Override
	public MySQLContainer<?> GetMySQLContainer() {
		return null;
	}
}
