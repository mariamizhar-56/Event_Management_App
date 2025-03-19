package com.mycompany.eventmanagementapp.dbconfigurations;

import org.junit.ClassRule;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.containers.MySQLContainer;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class TestContainerConfig implements DatabaseConfiguration {

	private static StandardServiceRegistry registry;
	
	private static final String HIBERNATE_XML = "hibernate-IT.cfg.xml";

	@SuppressWarnings("resource")
	@ClassRule
	public static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.28"))
			.withDatabaseName("test").withUsername("test").withPassword("test");

	@Override
	public void StartDatabaseConnection() {
		// Using Test Containers (Eclipse environment)
		mysqlContainer.start();
		System.setProperty("ENVIRONMENT", "testWithEclipes");
	}

	@Override
	public StandardServiceRegistry getServiceRegistry() {
		// Configure Hibernate to use Test Containers MySQL
		registry = new StandardServiceRegistryBuilder().configure(HIBERNATE_XML)
				.applySetting("hibernate.connection.url", mysqlContainer.getJdbcUrl())
				.applySetting("hibernate.connection.username", mysqlContainer.getUsername())
				.applySetting("hibernate.connection.password", mysqlContainer.getPassword()).build();

		return registry;
	}

	@Override
	public MySQLContainer<?> GetMySQLContainer() {
		return mysqlContainer;
	}
}
