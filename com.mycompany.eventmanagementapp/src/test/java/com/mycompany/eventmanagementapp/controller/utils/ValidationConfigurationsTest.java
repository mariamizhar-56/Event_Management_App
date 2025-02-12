package com.mycompany.eventmanagementapp.controller.utils;

import java.time.LocalDate;
import org.junit.Test;
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
