/*
 * package com.mycompany.eventmanagementapp.controller.utils;
 * 
 * import java.time.LocalDate; import java.time.format.DateTimeParseException;
 * import java.util.regex.Pattern;
 * 
 * public class ValidationConfigurationsImpl implements ValidationConfigurations
 * {
 * 
 * private static final Pattern EMAIL_PATTERN =
 * Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
 * 
 * @Override public int validateEventId(int eventId) { if (eventId <= 0) { throw
 * new IllegalArgumentException("Event ID must be greater than zero."); } return
 * eventId; }
 * 
 * @Override public String validateEventName(String name) { if (name == null ||
 * name.trim().isEmpty()) { throw new
 * IllegalArgumentException("Event name cannot be null or empty."); } return
 * name.trim(); }
 * 
 * @Override public String validateEventLocation(String location) { if (location
 * == null || location.trim().isEmpty()) { throw new
 * IllegalArgumentException("Event location cannot be null or empty."); } return
 * location.trim(); }
 * 
 * @Override public String validateEventDate(String date) { if (date == null) {
 * throw new IllegalArgumentException("Event date cannot be null."); } try {
 * LocalDate.parse(date); return date; } catch (DateTimeParseException e) {
 * throw new
 * IllegalArgumentException("Invalid date format. Expected format: YYYY-MM-DD."
 * ); } }
 * 
 * 
 * @Override public String validateParticipantName(String name) { if (name ==
 * null || name.trim().isEmpty()) { throw new
 * IllegalArgumentException("Participant name cannot be null or empty."); }
 * return name.trim(); }
 * 
 * @Override public String validateParticipantEmail(String email) { if (email ==
 * null || email.trim().isEmpty()) { throw new
 * IllegalArgumentException("Participant email cannot be null or empty."); } if
 * (!EMAIL_PATTERN.matcher(email).matches()) { throw new
 * IllegalArgumentException("Invalid email format."); } return email.trim(); }
 * 
 * @Override public int validateParticipantEventId(int eventId) { if (eventId <=
 * 0) { throw new
 * IllegalArgumentException("Event ID for participant must be greater than zero."
 * ); } return eventId; } }
 */