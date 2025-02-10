package com.mycompany.eventmanagementapp.controller.utils;

import java.time.LocalDate;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

	// Validates that the Integer Id is greater than zero
	// Throws a ValidationException if the Id(Integer) is invalid
	/*
	 * public static boolean validateId(Integer id, String fieldName) {
	 * LOGGER.debug("Validating Id: {}", id);
	 * 
	 * if (id <= 0) { LOGGER.error("Validation failed: {} is less than 1",
	 * fieldName); throw new ValidationException(fieldName +
	 * " must be greater than zero"); }
	 * 
	 * LOGGER.debug("Validation passed for {}: {}", fieldName, id); return true; }
	 */

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
	
	//Validate Email format
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