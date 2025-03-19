package com.mycompany.eventmanagementapp.dbconfigurations;

import org.testcontainers.containers.MySQLContainer;
import org.hibernate.boot.registry.StandardServiceRegistry;

public interface DatabaseConfiguration {
	
	public void StartDatabaseConnection();

	public StandardServiceRegistry getServiceRegistry();
	
	public MySQLContainer<?> GetMySQLContainer();
}
