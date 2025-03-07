/**
 * Unit tests for the ValidationConfigurations class in the Event Management Application.
 * 
 * This class tests the validation logic for various data types, ensuring that the validation rules defined in
 * the ValidationConfigurations class are correctly enforced. The tests cover string, date, and email validation
 * to ensure that they meet the required formats and constraints. Invalid inputs are expected to throw appropriate
 * exceptions with informative error messages.
 * 
 * Key validation methods tested:
 * 1. validateString(String value, String fieldName) - Ensures the string is not empty or null.
 * 2. validateDate(LocalDate date) - Ensures the date is not null and not in the past.
 * 3. validateEmail(String email) - Ensures the email is not null or empty and follows the correct format.
 * 
 * The tests check both valid inputs (which should pass the validation) and invalid inputs (which should trigger
 * exceptions). Specifically, the following scenarios are covered:
 * - Non-empty strings, valid dates, and valid emails pass validation.
 * - Empty or null strings, null dates, past dates, empty or null emails, and incorrectly formatted emails trigger
 *   ValidationException with appropriate error messages.
 * 
 * The tests use JUnit for defining and running the tests, and AssertJ for fluent assertions to verify expected results.
 * 
 * This test class ensures the reliability and correctness of the validation logic, contributing to the robustness
 * of the Event Management Application.
 */

package com.mycompany.eventmanagementapp.controller.utils;

import org.junit.Test;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.*;

public class ValidationConfigurationsTest {
	
	public static final String VALID_STRING = "Valid String";
	
	public static final String EMPTY_STRING = "";
	
	public static final String NULL_STRING = null;
	
	public static final LocalDate VALID_DATE = LocalDate.now().plusDays(10);
	
	public static final LocalDate PAST_DATE = LocalDate.now().minusDays(10);

	public static final String VALID_EMAIL = "John@gmail.com";
	
	public static final String INVALID_EMAIL = "John@gmail";
	
	public static final String EMPTY_EMAIL = "";
	
	public static final String NULL_EMAIL = null;

	// Test for validating a non-empty string
	@Test
	public void testValidateStringWhenStringisNotEmpty() {
		boolean isValidString = ValidationConfigurations.validateString(VALID_STRING, "String");
		assertThat(isValidString).isTrue();
	}

	// Test for validating an empty string (expected to throw ValidationException)
	@Test
	public void testValidateStringWhenStringisEmpty() {
		assertThatThrownBy(() -> ValidationConfigurations.validateString(EMPTY_STRING, "String"))
				.isInstanceOf(ValidationException.class).hasMessage("String is required and cannot be null or empty");
	}

	// Test for validating a null string (expected to throw ValidationException)
	@Test
	public void testValidateStringWhenStringisNull() {
		assertThatThrownBy(() -> ValidationConfigurations.validateString(NULL_STRING, "String"))
				.isInstanceOf(ValidationException.class).hasMessage("String is required and cannot be null or empty");
	}

	// Test for validating date when it is not in past
	@Test
	public void testIsValidDateWhenDateIsValid() {
		boolean isValidDate = ValidationConfigurations.validateDate(VALID_DATE);
		assertThat(isValidDate).isTrue();
	}

	// Test for validating a null date (expected to throw ValidationException)
	@Test
	public void testValidateDateWhenDateIsNull() {
		assertThatThrownBy(() -> ValidationConfigurations.validateDate(null)).isInstanceOf(ValidationException.class)
				.hasMessage("Date is required and cannot be null");
	}

	// Test for validating date when it is in past (expected to throw
	// ValidationException)
	@Test
	public void testValidateDateWhenDateIsInPast() {
		assertThatThrownBy(() -> ValidationConfigurations.validateDate(PAST_DATE))
				.isInstanceOf(ValidationException.class).hasMessage("Date cannot be in the past");
	}

	// Test for validating a valid email
	@Test
	public void testValidateEmailWhenEmailIsValid() {
		boolean isValidEmail = ValidationConfigurations.validateEmail(VALID_EMAIL);
		assertThat(isValidEmail).isTrue();
	}

	// Test for validating an empty email (expected to throw ValidationException)
	@Test
	public void testValidateEmailWhenEmailIsEmpty() {
		assertThatThrownBy(() -> ValidationConfigurations.validateEmail(EMPTY_EMAIL)).isInstanceOf(ValidationException.class)
				.hasMessage("Email cannot be null or empty");
	}

	// Test for validating a null email (expected to throw ValidationException)
	@Test
	public void testValidateEmailWhenEmailIsNull() {
		assertThatThrownBy(() -> ValidationConfigurations.validateEmail(null)).isInstanceOf(ValidationException.class)
				.hasMessage("Email cannot be null or empty");
	}

	// Test for validating a invalid format email (expected to throw
	// ValidationException)
	@Test
	public void testValidateEmailWhenEmailHasInvalidFormat() {
		assertThatThrownBy(() -> ValidationConfigurations.validateEmail(INVALID_EMAIL))
				.isInstanceOf(ValidationException.class).hasMessage("Invalid email format.");
	}
}
