package com.example.application.service;

import com.example.application.data.Employee;
import com.example.application.data.Role;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Service for managing role and employee data.
 */
@Service
public class RoleService {

    private Employee currentEmployee;
    private List<Role> availableRoles;

    public RoleService() {
        // Initialize with sample data
        currentEmployee = new Employee("Altan", "Sadik", "42786", "Active");
        
        availableRoles = Arrays.asList(
            new Role("Product owner", 
                     LocalDate.of(2022, 1, 20), 
                     LocalDate.of(2024, 1, 20), 
                     50, 
                     "Good employee"),
            new Role("UX Designer", 
                     LocalDate.of(2024, 1, 20), 
                     null, 
                     30, 
                     "Great design skills")
        );
        
        // Set first role as selected initially
        availableRoles.get(0).setSelected(true);
        availableRoles.get(0).setTeamLead(true); // Based on Figma design
    }

    public Employee getCurrentEmployee() {
        return currentEmployee;
    }

    public List<Role> getAvailableRoles() {
        return availableRoles;
    }

    public Role getSelectedRole() {
        return availableRoles.stream()
                .filter(Role::isSelected)
                .findFirst()
                .orElse(null);
    }

    public void selectRole(Role role) {
        // Deselect all roles first
        availableRoles.forEach(r -> r.setSelected(false));
        // Select the chosen role
        role.setSelected(true);
    }

    public void saveRole(Role role) {
        // In a real application, this would save to a database
        System.out.println("Saving role: " + role.getName());
    }

    public List<String> getAvailableReasons() {
        return Arrays.asList(
            "Good employee",
            "Excellent performance",
            "Team leadership skills",
            "Technical expertise",
            "Project requirements"
        );
    }
}
