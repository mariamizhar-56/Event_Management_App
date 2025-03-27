package com.mycompany.eventmanagementapp.dbconfigurations;

import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.testcontainers.containers.MySQLContainer;

public class MavenContainerConfig implements DatabaseConfiguration {

	private static StandardServiceRegistry registry;
	
	private static final String HIBERNATE_XML = "hibernate-IT.cfg.xml";
	
	private static final String DB_URL = "jdbc:mysql://localhost:3307/event_management_app";
	
	private static final String DB_PASSWORD = "test";
	
	@Override
	public void StartDatabaseConnection() {}

	@Override
	public StandardServiceRegistry getServiceRegistry() {
		registry = new StandardServiceRegistryBuilder().configure(HIBERNATE_XML)
				.applySetting("hibernate.connection.url", DB_URL)
				.applySetting("hibernate.connection.password", DB_PASSWORD).build();
		
		return registry;
	}

	@Override
	public MySQLContainer<?> GetMySQLContainer() {
		return null;
	}
}
