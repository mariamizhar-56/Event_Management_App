package com.mycompany.eventmanagementapp.dbconfigurations;

public class DBConfigSetup {

	public static DatabaseConfiguration databaseConfig;

	public static DatabaseConfiguration getDatabaseConfig() {
		String dbServer = System.getProperty("db.server");
		if ("mvn".equalsIgnoreCase(dbServer)) {
			// To run on eclipse
			databaseConfig = new MavenContainerConfig();

//			databaseConfig = new MavenContainerConfig();

		} else {
			// To run using mvn command always use mvn to start docker container without
			databaseConfig = new TestContainerConfig();
		}

		return databaseConfig;
	}
}
