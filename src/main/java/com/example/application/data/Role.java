package com.example.application.data;

import java.time.LocalDate;

/**
 * Data model for a role assignment.
 */
public class Role {
    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer utilizationRate; // Percentage (0-100)
    private String reason;
    private boolean isHeadOffice;
    private boolean isTeamLead;
    private boolean isSelected;

    // Constructors
    public Role() {}

    public Role(String name, LocalDate startDate, LocalDate endDate, 
                Integer utilizationRate, String reason) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.utilizationRate = utilizationRate;
        this.reason = reason;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getUtilizationRate() {
        return utilizationRate;
    }

    public void setUtilizationRate(Integer utilizationRate) {
        this.utilizationRate = utilizationRate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isHeadOffice() {
        return isHeadOffice;
    }

    public void setHeadOffice(boolean headOffice) {
        isHeadOffice = headOffice;
    }

    public boolean isTeamLead() {
        return isTeamLead;
    }

    public void setTeamLead(boolean teamLead) {
        isTeamLead = teamLead;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    // Helper method for date range display
    public String getDateRange() {
        if (startDate == null) return "";
        String start = startDate.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        if (endDate == null) {
            return start + " -";
        }
        String end = endDate.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        return start + " - " + end;
    }
}
