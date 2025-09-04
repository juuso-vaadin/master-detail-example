package com.example.application.data;

public class Person {
    private String firstName;
    private String lastName;
    private Long numericValue;
    private String textValue;

    public Person() {}

    public Person(String firstName, String lastName, Long numericValue, String textValue) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.numericValue = numericValue;
        this.textValue = textValue;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getNumericValue() {
        return numericValue;
    }

    public void setNumericValue(Long numericValue) {
        this.numericValue = numericValue;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
