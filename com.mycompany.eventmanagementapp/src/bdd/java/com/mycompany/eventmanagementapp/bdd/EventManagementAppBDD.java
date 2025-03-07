/**
 * EventManagementAppBDD class is the test runner for the Cucumber BDD tests in the Event Management App project.
 * It is responsible for running the Cucumber feature files and executing the defined scenarios.
 * 
 * Key functionalities:
 * - Uses the Cucumber JUnit runner to execute feature files and step definitions.
 * - Configures the test environment by setting the path for feature files (`src/bdd/resources`).
 * - Ensures proper thread management during UI tests by installing the `FailOnThreadViolationRepaintManager`.
 * - Runs the tests with monochrome output for better readability.
 */

package com.mycompany.eventmanagementapp.bdd;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/bdd/resources", monochrome = true)
public class EventManagementAppBDD {
	@BeforeClass
	public static void setUpOnce() {
		FailOnThreadViolationRepaintManager.install();
	}
}