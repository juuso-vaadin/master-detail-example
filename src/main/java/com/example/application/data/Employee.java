package com.example.application.data;

/**
 * Data model for employee information.
 */
public class Employee {
    private Long id;
    private String firstName;
    private String lastName;
    private String personalNumber;
    private String status; // e.g., "Active"
    private String avatarUrl;
    private String initials;

    // Constructors
    public Employee() {}

    public Employee(String firstName, String lastName, String personalNumber, String status) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalNumber = personalNumber;
        this.status = status;
        this.initials = generateInitials(firstName, lastName);
    }

    private String generateInitials(String firstName, String lastName) {
        StringBuilder initials = new StringBuilder();
        if (firstName != null && !firstName.isEmpty()) {
            initials.append(firstName.charAt(0));
        }
        if (lastName != null && !lastName.isEmpty()) {
            initials.append(lastName.charAt(0));
        }
        return initials.toString().toUpperCase();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        this.initials = generateInitials(firstName, lastName);
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        this.initials = generateInitials(firstName, lastName);
    }

    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }

    public String getPersonalNumber() {
        return personalNumber;
    }

    public void setPersonalNumber(String personalNumber) {
        this.personalNumber = personalNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }
}
