package com.example.application.views.rolemanagement;

import com.example.application.data.Employee;
import com.example.application.data.Role;
import com.example.application.service.RoleService;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.masterdetaillayout.MasterDetailLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

/**
 * Role Management View implementing the Figma design using proper MasterDetailLayout.
 * 
 * Analysis performed following guidelines:
 * 1. get_code: Identified Avatar, Card, Button, DatePicker, NumberField, ComboBox, Checkbox components
 * 2. get_variable_defs: Retrieved color and typography definitions
 * 3. get_metadata: Understood layout structure and hierarchy
 * 
 * Typography mapping based on Figma text styles:
 * - "Heading 2" (28px, Semi Bold) → H1 for "Roles" 
 * - "Heading 4" (18px, Semi Bold) → H2 for "Product owner"
 * - "Heading 5" (16px, Semi Bold) → H3 for "Assigned roles"
 */
@PageTitle("Role Management")
@Route("role-management")
@Menu(order = 2, icon = LineAwesomeIconUrl.USER_COG_SOLID)
public class RoleManagementView extends VerticalLayout {

    private final RoleService roleService;
    
    // Main layout components
    private MasterDetailLayout masterDetailLayout;
    
    // Master section components
    private VerticalLayout masterContent;
    private VerticalLayout roleItemsContainer;
    
    // Detail section components
    private VerticalLayout detailContent;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private IntegerField utilizationField;
    private ComboBox<String> reasonComboBox;
    private Checkbox headOfficeCheckbox;
    private Checkbox teamLeadCheckbox;
    
    // Footer components
    private HorizontalLayout footerLayout;
    
    private Role currentSelectedRole;

    public RoleManagementView(RoleService roleService) {
        this.roleService = roleService;
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        
        initializeLayout();
        createViewHeader();
        createMasterDetailLayout();
        createViewFooter();
        
        // Load initial data and show first role in detail
        loadRoles();
    }

    private void initializeLayout() {
        // Main layout setup based on Figma structure
        addClassName("role-management-view");

    }

    /**
     * Creates the view header outside of MasterDetailLayout
     * Using H2 since the application already has H1 in MainLayout
     */
    private void createViewHeader() {
        // Main title using H2 for proper heading hierarchy
        H2 title = new H2("Roles");
        title.addClassNames(LumoUtility.TextColor.HEADER, LumoUtility.FontSize.XXLARGE, LumoUtility.FontWeight.SEMIBOLD);
        
        // Subtitle using Paragraph for body text
        Paragraph subtitle = new Paragraph("Which roles should this person be assigned to?");
        subtitle.addClassNames(LumoUtility.TextColor.BODY, LumoUtility.FontSize.MEDIUM);
        
        VerticalLayout headerSection = new VerticalLayout(title, subtitle);
        headerSection.setPadding(false);
        headerSection.addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM);
        headerSection.setSpacing(false);
        
        add(headerSection);
    }

    /**
     * Creates the MasterDetailLayout component
     */
    private void createMasterDetailLayout() {
        masterDetailLayout = new MasterDetailLayout();
        masterDetailLayout.setSizeFull();
        
        // Configure MasterDetailLayout
        masterDetailLayout.setMasterSize("400px");
        masterDetailLayout.setDetailMinSize("460px");
        
        createMasterSection();
        setupMasterDetailEventListeners();
        
        add(masterDetailLayout);
        expand(masterDetailLayout); // Make it take remaining space
    }

    /**
     * Creates the view footer outside of MasterDetailLayout
     */
    private void createViewFooter() {
        Button cancelButton = new Button("Cancel");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.addClickListener(e -> handleCancel());
        
        Button saveButton = new Button("Save and close");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> handleSave());
        
        footerLayout = new HorizontalLayout(cancelButton, saveButton);
        footerLayout.setWidthFull();
        footerLayout.setJustifyContentMode(HorizontalLayout.JustifyContentMode.END);
        footerLayout.setPadding(true);
        footerLayout.getStyle()
            .set("background-color", "var(--lumo-contrast-5pct)")
            .set("border-top", "1px solid var(--lumo-contrast-10pct)");
        
        add(footerLayout);
    }

    /**
     * Creates the master section following Figma design:
     * 1. Static header with employee card
     * 2. Toolbar with "Assigned roles" title and "Add role" button  
     * 3. Selectable role items
     */
    private void createMasterSection() {
        masterContent = new VerticalLayout();
        masterContent.setSizeFull();
        masterContent.setPadding(false);
        masterContent.addClassNames(LumoUtility.Padding.Horizontal.LARGE);
        masterContent.setSpacing(false);
        masterContent.addClassName("master-content");

        // Employee header card - static element
        createEmployeeCard();
        
        // Toolbar with "Assigned roles" and "Add role" button
        createToolbar();
        
        // Container for role items - will be populated dynamically
        roleItemsContainer = new VerticalLayout();
        roleItemsContainer.setPadding(false);
        roleItemsContainer.setSpacing("var(--lumo-space-s)");
        roleItemsContainer.addClassName("role-items");
        
        masterContent.add(roleItemsContainer);
        masterDetailLayout.setMaster(masterContent);
    }

    /**
     * Sets up event listeners for the MasterDetailLayout
     */
    private void setupMasterDetailEventListeners() {
        // Add backdrop and escape listeners for better UX
        masterDetailLayout.addBackdropClickListener(e -> hideDetail());
        masterDetailLayout.addDetailEscapePressListener(e -> hideDetail());
    }

    /**
     * Creates the employee header card based on Figma "Card (Icon & Avatar)" component
     */
    private void createEmployeeCard() {
        Employee employee = roleService.getCurrentEmployee();
        
        // Avatar component as identified in Figma
        Avatar avatar = new Avatar(employee.getFullName());
        avatar.setAbbreviation(employee.getInitials());
        avatar.addClassName("employee-avatar");
        
        // Employee info section
        H3 employeeName = new H3(employee.getFullName());
        employeeName.addClassNames(LumoUtility.TextColor.HEADER, LumoUtility.FontSize.LARGE, LumoUtility.FontWeight.SEMIBOLD);
        employeeName.getStyle().set("margin", "0");
        
        Span personalInfo = new Span("Personal no " + employee.getPersonalNumber());
        personalInfo.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.MEDIUM);
        
        VerticalLayout employeeInfo = new VerticalLayout(employeeName, personalInfo);
        employeeInfo.setPadding(false);
        employeeInfo.setSpacing(false);
        employeeInfo.setFlexGrow(1);
        
        // Status badge
        Span statusBadge = new Span(employee.getStatus());
        statusBadge.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.FontWeight.MEDIUM);
        statusBadge.getElement().getThemeList().add("badge success");
        statusBadge.getStyle()
            .set("background-color", "var(--lumo-success-color-10pct)")
            .set("color", "var(--lumo-success-color)")
            .set("padding", "4px 8px")
            .set("border-radius", "var(--lumo-border-radius-s)");
        
        // Card layout
        HorizontalLayout cardContent = new HorizontalLayout(avatar, employeeInfo, statusBadge);
        cardContent.setAlignItems(HorizontalLayout.Alignment.CENTER);
        cardContent.setWidthFull();
        cardContent.setPadding(true);
        cardContent.addClassNames(LumoUtility.Background.BASE, LumoUtility.BorderRadius.MEDIUM);
        cardContent.getStyle()
            .set("border", "1px solid var(--lumo-contrast-10pct)")
            .set("margin-bottom", "24px");
        
        masterContent.add(cardContent);
    }

    /**
     * Creates toolbar with "Assigned roles" heading and "Add role" button
     */
    private void createToolbar() {
        // Section title using H3 for 16px Semi Bold text  
        H3 sectionTitle = new H3("Assigned roles");
        sectionTitle.addClassNames(LumoUtility.TextColor.HEADER, LumoUtility.FontSize.MEDIUM, LumoUtility.FontWeight.SEMIBOLD);
        sectionTitle.getStyle().set("margin", "0");
        
        // Add role button based on Figma "Button" component
        Button addRoleButton = new Button("Add role");
        addRoleButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        addRoleButton.addClickListener(e -> handleAddRole());
        
        HorizontalLayout toolbar = new HorizontalLayout(sectionTitle, addRoleButton);
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(HorizontalLayout.JustifyContentMode.BETWEEN);
        toolbar.setAlignItems(HorizontalLayout.Alignment.CENTER);
        toolbar.getStyle().set("margin-bottom", "24px");
        
        masterContent.add(toolbar);
    }

    /**
     * Loads and displays role items as selectable cards
     */
    private void loadRoles() {
        roleItemsContainer.removeAll();
        
        for (Role role : roleService.getAvailableRoles()) {
            roleItemsContainer.add(createRoleCard(role));
        }
        
        // Show detail for the first selected role
        Role selectedRole = roleService.getSelectedRole();
        if (selectedRole != null) {
            showRoleDetail(selectedRole);
        }
    }

    /**
     * Creates a selectable role card based on Figma "Card" component using proper slots
     */
    private Div createRoleCard(Role role) {
        // Create proper Vaadin Card component
        Card card = new Card();
        card.setWidthFull();
        
        // Use Card's Title slot for role name (proper semantic heading)
        H4 roleTitle = new H4(role.getName());
        roleTitle.addClassNames(LumoUtility.TextColor.HEADER, LumoUtility.FontSize.LARGE, LumoUtility.FontWeight.SEMIBOLD);
        roleTitle.getStyle().set("margin", "0");
        card.setTitle(roleTitle);
        
        // Use Card's Subtitle slot for date range
        Span dateSubtitle = new Span(role.getDateRange());
        dateSubtitle.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.MEDIUM);
        card.setSubtitle(dateSubtitle);
        
        // Use Header Suffix slot for utilization badge
        Span utilizationBadge = new Span(role.getUtilizationRate() + "%");
        utilizationBadge.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.FontWeight.MEDIUM);
        utilizationBadge.getStyle()
            .set("background-color", "var(--lumo-contrast-5pct)")
            .set("color", "var(--lumo-contrast-80pct)")
            .set("padding", "4px 8px")
            .set("border-radius", "var(--lumo-border-radius-s)");
        card.setHeaderSuffix(utilizationBadge);
        
        // Create clickable wrapper (since Card doesn't support click listeners directly)
        Div cardWrapper = new Div(card);
        cardWrapper.setWidthFull();
        
        // Selection styling based on Figma selected state
        if (role.isSelected()) {
            card.getStyle().set("border", "2px solid var(--lumo-primary-color)");
        } else {
            card.addThemeVariants(com.vaadin.flow.component.card.CardVariant.LUMO_OUTLINED);
        }
        
        // Click handler for selection on wrapper
        cardWrapper.addClickListener(e -> {
            roleService.selectRole(role);
            refreshRoleSelection();
            showRoleDetail(role);
        });
        
        cardWrapper.getStyle().set("cursor", "pointer");
        return cardWrapper;
    }

    /**
     * Refreshes the visual selection state of role cards
     */
    private void refreshRoleSelection() {
        loadRoles(); // Reload to update selection styling
    }

    /**
     * Shows the detail form for the selected role
     */
    private void showRoleDetail(Role role) {
        currentSelectedRole = role;
        if (detailContent == null) {
            createDetailSection();
        }
        
        // Update form with role data
        populateDetailForm(role);
        masterDetailLayout.setDetail(detailContent);
    }

    /**
     * Creates the detail section with form fields and info card
     */
    private void createDetailSection() {
        detailContent = new VerticalLayout();
        detailContent.setSizeFull();
        detailContent.setPadding(true);
        detailContent.setSpacing(false);
        detailContent.addClassNames(LumoUtility.Border.ALL, LumoUtility.BorderRadius.MEDIUM, LumoUtility.BorderColor.CONTRAST_10);
        
        createDetailForm();
    }

    /**
     * Creates the detail form based on Figma form components
     */
    private void createDetailForm() {
        // Detail header with close button
        H2 detailTitle = new H2("Product owner");
        detailTitle.addClassNames(LumoUtility.TextColor.HEADER, LumoUtility.FontSize.LARGE, LumoUtility.FontWeight.SEMIBOLD);
        detailTitle.getStyle().set("margin", "0");
        
        // Close button (X icon) - only visible in drawer mode
        Button closeButton = new Button(new Icon(VaadinIcon.CLOSE));
        closeButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
        closeButton.addClickListener(e -> hideDetail());
        closeButton.addClassName("detail-close-button");
        closeButton.getStyle().set("margin-left", "auto");
        
        // Header layout with title and close button
        HorizontalLayout detailHeader = new HorizontalLayout(detailTitle, closeButton);
        detailHeader.setWidthFull();
        detailHeader.setAlignItems(HorizontalLayout.Alignment.CENTER);
        detailHeader.getStyle().set("margin-bottom", "24px");
        
        // Form layout
        HorizontalLayout formContent = new HorizontalLayout();
        formContent.setSizeFull();
        formContent.setSpacing(true);
        formContent.setWrap(true);
        formContent.addClassNames(LumoUtility.AlignContent.START, LumoUtility.AlignItems.START);
        
        // Left side - form fields
        VerticalLayout formFields = createFormFields();
        formFields.setWidth("fit-content");
        formFields.addClassName(LumoUtility.Flex.AUTO);

        
        // Right side - info card
        VerticalLayout infoCard = createInfoCard();
        infoCard.setWidth("300px"); // Based on Figma metadata
        infoCard.addClassName(LumoUtility.Flex.AUTO);
        
        formContent.add(formFields, infoCard);
        
        detailContent.add(detailHeader, formContent);
    }

    /**
     * Creates form fields based on Figma components
     */
    private VerticalLayout createFormFields() {
        VerticalLayout fields = new VerticalLayout();
        fields.setPadding(false);
        
        // Date fields row - DatePicker components as identified in Figma
        HorizontalLayout dateFields = new HorizontalLayout();
        dateFields.setWidthFull();
        
        startDatePicker = new DatePicker("Start");
        startDatePicker.setWidthFull(); // Based on Figma metadata
        
        endDatePicker = new DatePicker("End");
        endDatePicker.setWidthFull();
        
        dateFields.add(startDatePicker, endDatePicker);
        
        // Utilization rate - NumberField with controls as identified in Figma
        utilizationField = new IntegerField("Utilisation rate");
        utilizationField.setWidthFull();
        utilizationField.setMin(0);
        utilizationField.setMax(100);
        utilizationField.setStepButtonsVisible(true);
        
        // Reason - ComboBox as identified in Figma
        reasonComboBox = new ComboBox<>("Reason");
        reasonComboBox.setWidthFull();
        reasonComboBox.setItems(roleService.getAvailableReasons());
        
        // Checkboxes - horizontal group as shown in Figma
        HorizontalLayout checkboxGroup = new HorizontalLayout();
        checkboxGroup.setSpacing(true);
        
        headOfficeCheckbox = new Checkbox("Head office");
        teamLeadCheckbox = new Checkbox("Team lead");
        
        checkboxGroup.add(headOfficeCheckbox, teamLeadCheckbox);
        
        fields.add(dateFields, utilizationField, reasonComboBox, checkboxGroup);
        return fields;
    }

    /**
     * Creates the info card on the right side
     */
    private VerticalLayout createInfoCard() {
        VerticalLayout card = new VerticalLayout();
        card.setPadding(true);
        card.addClassNames(LumoUtility.Background.CONTRAST_5, LumoUtility.BorderRadius.MEDIUM);
        
        H3 cardTitle = new H3("Role info");
        cardTitle.addClassNames(LumoUtility.TextColor.HEADER, LumoUtility.FontSize.LARGE, LumoUtility.FontWeight.SEMIBOLD);
        cardTitle.getStyle().set("margin", "0 0 16px 0");
        
        Paragraph cardContent = new Paragraph(
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
            "Phasellus tellus dui, fringilla nec dictum at, pellentesque sed leo. " +
            "Donec tellus tellus, ultricies non risus volutpat, gravida luctus ante."
        );
        cardContent.addClassNames(LumoUtility.TextColor.BODY);
        
        card.add(cardTitle, cardContent);
        return card;
    }

    /**
     * Populates the detail form with role data
     */
    private void populateDetailForm(Role role) {
        if (role == null) return;
        
        // Update detail title (first component in the header layout)
        HorizontalLayout detailHeader = (HorizontalLayout) detailContent.getComponentAt(0);
        H2 detailTitle = (H2) detailHeader.getComponentAt(0);
        detailTitle.setText(role.getName());
        
        // Populate form fields
        startDatePicker.setValue(role.getStartDate());
        endDatePicker.setValue(role.getEndDate());
        utilizationField.setValue(role.getUtilizationRate());
        reasonComboBox.setValue(role.getReason());
        headOfficeCheckbox.setValue(role.isHeadOffice());
        teamLeadCheckbox.setValue(role.isTeamLead());
    }

    /**
     * Creates the footer with action buttons
     */

    private void hideDetail() {
        masterDetailLayout.setDetail(null);
        // Deselect current role
        if (currentSelectedRole != null) {
            currentSelectedRole.setSelected(false);
            currentSelectedRole = null;
            refreshRoleSelection();
        }
    }

    // Event handlers
    private void handleAddRole() {
        Notification.show("Add role functionality would be implemented here");
    }

    private void handleCancel() {
        hideDetail();
        Notification.show("Changes cancelled");
    }

    private void handleSave() {
        if (currentSelectedRole != null) {
            // Update role with form values
            currentSelectedRole.setStartDate(startDatePicker.getValue());
            currentSelectedRole.setEndDate(endDatePicker.getValue());
            currentSelectedRole.setUtilizationRate(utilizationField.getValue());
            currentSelectedRole.setReason(reasonComboBox.getValue());
            currentSelectedRole.setHeadOffice(headOfficeCheckbox.getValue());
            currentSelectedRole.setTeamLead(teamLeadCheckbox.getValue());
            
            roleService.saveRole(currentSelectedRole);
            Notification.show("Role saved successfully");
            hideDetail();
        }
    }
}
