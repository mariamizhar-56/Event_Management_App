package com.mycompany.eventmanagementapp.dbconfigurations;

public class DBConfigSetup {

	public static DatabaseConfiguration databaseConfig;

	public static DatabaseConfiguration getDatabaseConfig() {
		String dbServer = System.getProperty("db.server");
		if (dbServer == null) {
			// To run on eclipse
			databaseConfig = new TestContainerConfig();
//			databaseConfig = new MavenContainerConfig();

		} else if (dbServer.equals("mvn")) {
			// To run using mvn command always use mvn to start docker container without
			// test containers
			databaseConfig = new MavenContainerConfig();
		}

		return databaseConfig;
	}
}
