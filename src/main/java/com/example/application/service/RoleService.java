package com.example.application.service;

import com.example.application.data.Employee;
import com.example.application.data.Role;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Service for managing role and employee data.
 */
@Service
public class RoleService {

    private Employee currentEmployee;
    private List<Role> availableRoles;

    public RoleService() {
        currentEmployee = new Employee("Altan", "Sadik", "42786", "Active");
        availableRoles = generateSampleRoles();
    }

    private List<Role> generateSampleRoles() {
        List<Role> roles = new ArrayList<>();
        Random random = new Random(42); // Fixed seed for consistent data

        String[] roleNames = {
            "Product Owner", "Scrum Master", "UX Designer", "UI Designer",
            "Frontend Developer", "Backend Developer", "Full Stack Developer",
            "DevOps Engineer", "QA Engineer", "Test Automation Engineer",
            "Business Analyst", "Data Analyst", "Data Scientist", "ML Engineer",
            "Solution Architect", "Technical Lead", "Engineering Manager", "Project Manager",
            "Product Manager", "Marketing Manager", "Sales Manager", "HR Manager",
            "Software Engineer", "Senior Developer", "Junior Developer", "Intern",
            "UX Researcher", "Content Writer", "Graphic Designer", "Brand Manager"
        };

        String[] reasons = {
            "Excellent performance", "Good employee", "Team leadership skills",
            "Technical expertise", "Project requirements", "Strong communication",
            "Problem-solving abilities", "Innovation and creativity", "Reliable team member",
            "Strategic thinking", "Client satisfaction", "Process improvement"
        };

        for (int i = 0; i < 100; i++) {
            Role role = new Role();
            role.setId((long) (i + 1));

            // Role name with occasional suffix
            String baseName = roleNames[i % roleNames.length];
            if (i >= roleNames.length) {
                role.setName(baseName + " " + (i / roleNames.length + 1));
            } else {
                role.setName(baseName);
            }

            // Random start date in the past 3 years
            LocalDate startDate = LocalDate.now()
                .minusDays(random.nextInt(365 * 3));
            role.setStartDate(startDate);

            // Create varied end date scenarios to showcase all statuses
            double endDateType = random.nextDouble();
            if (endDateType < 0.33) {
                // 33% ongoing (no end date)
                role.setEndDate(null);
            } else if (endDateType < 0.66) {
                // 33% active (future end date)
                LocalDate futureEndDate = LocalDate.now().plusDays(random.nextInt(730));
                role.setEndDate(futureEndDate);
            } else {
                // 34% completed (past end date)
                LocalDate pastEndDate = startDate.plusDays(random.nextInt(365));
                if (pastEndDate.isAfter(LocalDate.now())) {
                    pastEndDate = LocalDate.now().minusDays(random.nextInt(365));
                }
                role.setEndDate(pastEndDate);
            }

            // Utilization rate between 10% and 100%
            role.setUtilizationRate(10 + random.nextInt(91));

            // Random reason
            role.setReason(reasons[random.nextInt(reasons.length)]);

            // All are head office for demonstration purposes
            role.setHeadOffice(true);

            // All are team leads for demonstration purposes
            role.setTeamLead(true);

            roles.add(role);
        }

        // Set first role as selected initially
        if (!roles.isEmpty()) {
            roles.get(0).setSelected(true);
        }

        return roles;
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
