package com.mycompany.eventmanagementapp.dbconfigurations;

public class DBConfigSetup {
	
	public static DatabaseConfiguration databaseConfig;
	
	public static DatabaseConfiguration getDatabaseConfig() {
		String runningServer = System.getProperty("surefire.test.class.path");
		
		if (runningServer == null) {
			databaseConfig = new TestContainerConfig();
		} else {
			databaseConfig = new MavenContainerConfig();
		}
		
		return databaseConfig;
	}
}
