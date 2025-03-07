/**
 * Utility class providing common validation functions used throughout the Event Management Application.
 * This class includes static methods to validate:
 * - Strings (to ensure they are not null or empty)
 * - Dates (to ensure they are not null and not in the past)
 * - Email addresses (to ensure they are not null, not empty, and in a valid format)
 *
 * The class uses regular expressions for email validation and throws a custom ValidationException
 * with appropriate error messages when validation fails.
 *
 * Logging is provided for each validation to track the success or failure of each operation.
 * The class is designed to be stateless, and all methods are static. Therefore, it cannot be instantiated.
 *
 * Key validation methods:
 * - validateString: Validates that a given string is not null or empty.
 * - validateDate: Validates that a given date is not null and is not in the past.
 * - validateEmail: Validates that a given email string matches the correct email format.
 *
 * Exceptions:
 * - ValidationException: Thrown if any validation fails, with a descriptive error message.
 *
 * Dependencies:
 * - Logger: Uses Log4j for logging validation steps and errors.
 */

package com.mycompany.eventmanagementapp.controller.utils;

import java.time.LocalDate;
import java.util.regex.Pattern;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

// Utility class for common validation functions (e.g., string validation, Integer validation, date validation, email validation)
public class ValidationConfigurations {

	private static final Logger LOGGER = LogManager.getLogger(ValidationConfigurations.class);
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

	// Private constructor to prevent instantiation since this is a utility class
	private ValidationConfigurations() {
	}

	// Validates that a required string is not null or empty
	// Throws a ValidationException if the string is invalid
	public static boolean validateString(String string, String fieldName) {
		LOGGER.debug("Validating string for field: {}", fieldName);

		if (string == null || string.trim().isEmpty()) {
			LOGGER.error("Validation failed: {} is null or empty", fieldName);
			throw new ValidationException(fieldName + " is required and cannot be null or empty");
		}

		LOGGER.debug("Validation passed for field: {}", fieldName);
		return true;
	}

	// Validates that the date is not null and is not in the past
	// Throws a ValidationException if the date is invalid
	public static boolean validateDate(LocalDate date) {
		LOGGER.debug("Validating date: {}", date);

		if (date == null) {
			LOGGER.error("Validation failed: Date is null");
			throw new ValidationException("Date is required and cannot be null");
		}

		if (date.isBefore(LocalDate.now())) {
			LOGGER.error("Validation failed: Date {} is in the past", date);
			throw new ValidationException("Date cannot be in the past");
		}

		LOGGER.debug("Validation passed for date: {}", date);
		return true;
	}

	// Validate Email format
	public static boolean validateEmail(String email) {
		LOGGER.debug("Validating Email format");

		if (email == null || email.trim().isEmpty()) {
			LOGGER.error("Validation failed: Email is null or empty");
			throw new ValidationException("Email cannot be null or empty");
		}

		if (!EMAIL_PATTERN.matcher(email).matches()) {
			LOGGER.error("Validation failed: Email format is not correct.");
			throw new ValidationException("Invalid email format.");
		}

		LOGGER.debug("Validation passed for email: {}", email);
		return true;
	}
}