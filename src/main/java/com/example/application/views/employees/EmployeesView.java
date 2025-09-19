package com.example.application.views.employees;

import com.example.application.data.Employee;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Employee detail view implementing the Figma design following Vaadin Flow best practices.
 * 
 * Implementation based on Figma components:
 * - data-name="Avatar" → Avatar.class
 * - data-name="Text field" → TextField.class  
 * - data-name="Date Picker" → DatePicker.class
 * - data-name="Checkbox" → Checkbox.class
 * - data-name="Radio button" → RadioButtonGroup.class
 * - data-name="Button (primary)" → Button with LUMO_PRIMARY theme
 * - data-name="Button" → Button with default styling
 */
@PageTitle("Employees")
@Route("employees")
@Menu(order = 3, icon = LineAwesomeIconUrl.USER_SOLID)
public class EmployeesView extends VerticalLayout {

    private Employee currentEmployee;
    
    // Form components following Figma structure
    private Avatar avatar;
    private Span employeeType;
    private Span employeeName;
    private Span lastChangedLabel;
    private Span lastChangedValue;
    
    // Personal info form fields
    private TextField lastNameField;
    private TextField firstNameField;
    private TextField phoneField;
    private TextField emailField;
    private DatePicker dateOfBirthPicker;
    private DatePicker startDatePicker;
    
    // Address form fields
    private TextField streetField;
    private TextField unitNumberField;
    private TextField cityField;
    private TextField stateField;
    private TextField zipCodeField;
    
    // Notification preferences
    private CheckboxGroup<String> sendToCheckboxGroup;
    private RadioButtonGroup<String> promotionalMessagesRadioGroup;
    
    // Action buttons
    private Button saveChangesButton;
    private Button cancelButton;

    public EmployeesView() {
        setSizeFull();
        addClassNames(LumoUtility.Padding.LARGE);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.START);
        
        createViewContent();
        loadSampleData();
    }

    /**
     * Creates the main view content based on Figma "Employee view" structure
     */
    private void createViewContent() {
        // Main container with max width like in Figma
        VerticalLayout mainContainer = new VerticalLayout();
        mainContainer.setMaxWidth("960px");
        mainContainer.setWidthFull(); 
        mainContainer.setPadding(false);
        mainContainer.setSpacing(true);
        mainContainer.addClassNames(LumoUtility.Gap.LARGE);
        
        // View header based on Figma data-name="view header"
        HorizontalLayout viewHeader = createViewHeader();
        
        // Card container based on Figma data-name="Card"
        Div card = createEmployeeCard();
        
        mainContainer.add(viewHeader, card);
        add(mainContainer);
    }

    /**
     * Creates the view header with avatar and employee info
     * Based on Figma data-name="view header"
     */
    private HorizontalLayout createViewHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        header.addClassNames(LumoUtility.Gap.MEDIUM);
        
        // Avatar based on Figma data-name="Avatar"
        avatar = new Avatar();
        avatar.setWidth("48px");  // Based on Figma metadata: width="48"
        avatar.setHeight("48px"); // Based on Figma metadata: height="48"
        
        // View title section based on Figma data-name="view title"
        VerticalLayout viewTitle = new VerticalLayout();
        viewTitle.setPadding(false);
        viewTitle.setSpacing(false);
        viewTitle.setFlexGrow(1);
        
        employeeType = new Span("Employee");
        employeeType.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL);
        
        employeeName = new Span();
        employeeName.addClassNames(LumoUtility.TextColor.HEADER, LumoUtility.FontSize.XLARGE, LumoUtility.FontWeight.SEMIBOLD);
        
        viewTitle.add(employeeType, employeeName);
        
        // Last changed section based on Figma data-name="changed"
        VerticalLayout lastChangedSection = new VerticalLayout();
        lastChangedSection.setPadding(false);
        lastChangedSection.setSpacing(false);
        lastChangedSection.setAlignItems(Alignment.END);
        
        lastChangedLabel = new Span("Last changed");
        lastChangedLabel.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.XSMALL);
        
        lastChangedValue = new Span();
        lastChangedValue.addClassNames(LumoUtility.TextColor.BODY, LumoUtility.FontSize.MEDIUM);
        
        lastChangedSection.add(lastChangedLabel, lastChangedValue);
        
        header.add(avatar, viewTitle, lastChangedSection);
        return header;
    }

    /**
     * Creates the employee card with form content
     * Based on Figma data-name="Card"
     */
    private Div createEmployeeCard() {
        Div card = new Div();
        card.addClassNames(
            LumoUtility.Background.BASE,
            LumoUtility.BorderRadius.LARGE,
            LumoUtility.BoxShadow.SMALL,
            LumoUtility.Border.ALL,
            LumoUtility.BorderColor.CONTRAST_10
        );
        card.setWidthFull();
        
        // Card content based on Figma data-name="card content"
        VerticalLayout cardContent = new VerticalLayout();
        cardContent.setPadding(true);
        cardContent.setSpacing(true);
        cardContent.addClassNames(LumoUtility.Gap.XLARGE);
        
        // Create form sections
        VerticalLayout personalInfoForm = createPersonalInfoForm();
        VerticalLayout addressForm = createAddressForm();
        VerticalLayout notificationPreferencesForm = createNotificationPreferencesForm();
        
        cardContent.add(personalInfoForm, addressForm, notificationPreferencesForm);
        
        // Actions section based on Figma data-name="Actions"
        HorizontalLayout actionsSection = createActionsSection();
        
        card.add(cardContent, actionsSection);
        return card;
    }

    /**
     * Creates the personal info form section
     * Based on Figma data-name="form" with "Personal info" heading
     */
    private VerticalLayout createPersonalInfoForm() {
        VerticalLayout form = new VerticalLayout();
        form.setPadding(false);
        form.setSpacing(true);
        form.addClassNames(LumoUtility.Gap.MEDIUM);
        
        // Form subheading based on Figma data-name="form subheading"
        H3 heading = new H3("Personal info");
        heading.addClassNames(LumoUtility.TextColor.HEADER, LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        
        // Form rows based on Figma data-name="form row"
        HorizontalLayout firstRow = new HorizontalLayout();
        firstRow.setWidthFull();
        firstRow.addClassNames(LumoUtility.Gap.LARGE);
        
        // Text fields based on Figma data-name="Text field"
        lastNameField = new TextField("Last name");
        lastNameField.setWidthFull();
        
        firstNameField = new TextField("First name");
        firstNameField.setWidthFull();
        
        firstRow.add(lastNameField, firstNameField);
        
        HorizontalLayout secondRow = new HorizontalLayout();
        secondRow.setWidthFull();
        secondRow.addClassNames(LumoUtility.Gap.LARGE);
        
        phoneField = new TextField("Phone");
        phoneField.setWidthFull();
        
        emailField = new TextField("Email");
        emailField.setWidthFull();
        
        secondRow.add(phoneField, emailField);
        
        HorizontalLayout thirdRow = new HorizontalLayout();
        thirdRow.setWidthFull();
        thirdRow.addClassNames(LumoUtility.Gap.LARGE);
        
        // Date pickers based on Figma data-name="Date Picker"
        dateOfBirthPicker = new DatePicker("Date of Birth");
        dateOfBirthPicker.setWidthFull();
        
        startDatePicker = new DatePicker("Start date");
        startDatePicker.setWidthFull();
        
        thirdRow.add(dateOfBirthPicker, startDatePicker);
        
        form.add(heading, firstRow, secondRow, thirdRow);
        return form;
    }

    /**
     * Creates the address form section
     * Based on Figma data-name="form" with "Address" heading
     */
    private VerticalLayout createAddressForm() {
        VerticalLayout form = new VerticalLayout();
        form.setPadding(false);
        form.setSpacing(true);
        form.addClassNames(LumoUtility.Gap.MEDIUM);
        
        // Form subheading based on Figma data-name="form subheading"
        H3 heading = new H3("Address");
        heading.addClassNames(LumoUtility.TextColor.HEADER, LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        
        // Form rows based on Figma data-name="form row"
        HorizontalLayout firstRow = new HorizontalLayout();
        firstRow.setWidthFull();
        firstRow.addClassNames(LumoUtility.Gap.LARGE);
        
        streetField = new TextField("Street");
        streetField.setWidthFull();
        
        unitNumberField = new TextField("Unit Number / P.O. Box");
        unitNumberField.setWidth("200px"); // Based on Figma metadata: width="200"
        
        firstRow.add(streetField, unitNumberField);
        firstRow.setFlexGrow(1, streetField);
        
        HorizontalLayout secondRow = new HorizontalLayout();
        secondRow.setWidthFull();
        secondRow.addClassNames(LumoUtility.Gap.LARGE);
        
        cityField = new TextField("City");
        cityField.setWidthFull();
        
        stateField = new TextField("State");
        stateField.setWidthFull();
        
        zipCodeField = new TextField("Zip Code");
        zipCodeField.setWidthFull();
        
        secondRow.add(cityField, stateField, zipCodeField);
        
        form.add(heading, firstRow, secondRow);
        return form;
    }

    /**
     * Creates the notification preferences form section
     * Based on Figma data-name="form" with "Notification preferences" heading
     */
    private VerticalLayout createNotificationPreferencesForm() {
        VerticalLayout form = new VerticalLayout();
        form.setPadding(false);
        form.setSpacing(true);
        form.addClassNames(LumoUtility.Gap.MEDIUM);
        
        // Form subheading based on Figma data-name="form subheading"
        H3 heading = new H3("Notification preferences");
        heading.addClassNames(LumoUtility.TextColor.HEADER, LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        
        // Form row based on Figma data-name="form row"
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.addClassNames(LumoUtility.Gap.LARGE);
        
        // Checkbox group based on Figma data-name="Checkbox group (horizontal)"
        sendToCheckboxGroup = new CheckboxGroup<>("Send to");
        sendToCheckboxGroup.setItems("Email", "SMS");
        sendToCheckboxGroup.setWidthFull();
        
        // Radio button group based on Figma data-name="Radio button group (horizontal)"
        promotionalMessagesRadioGroup = new RadioButtonGroup<>("Promotional messages");
        promotionalMessagesRadioGroup.setItems("Allow", "Don't allow");
        promotionalMessagesRadioGroup.setWidthFull();
        
        row.add(sendToCheckboxGroup, promotionalMessagesRadioGroup);
        
        form.add(heading, row);
        return form;
    }

    /**
     * Creates the actions section with buttons
     * Based on Figma data-name="Actions"
     */
    private HorizontalLayout createActionsSection() {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setWidthFull();
        actions.setPadding(true);
        actions.addClassNames(
            LumoUtility.Gap.MEDIUM,
            LumoUtility.Border.TOP,
            LumoUtility.BorderColor.CONTRAST_10
        );
        
        // Primary button based on Figma data-name="Button (primary)"
        saveChangesButton = new Button("Save Changes");
        saveChangesButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveChangesButton.addClickListener(e -> saveChanges());
        
        // Secondary button based on Figma data-name="Button"
        cancelButton = new Button("Cancel");
        cancelButton.addClickListener(e -> cancelChanges());
        
        actions.add(saveChangesButton, cancelButton);
        return actions;
    }

    /**
     * Loads sample data matching the Figma design
     */
    private void loadSampleData() {
        currentEmployee = new Employee();
        currentEmployee.setFirstName("Lisa");
        currentEmployee.setLastName("Jackson");
        currentEmployee.setPhone("+555 453456065");
        currentEmployee.setEmail("lisa.jackson@example.com");
        currentEmployee.setDateOfBirth(LocalDate.of(1998, 6, 22));
        currentEmployee.setStartDate(LocalDate.of(2023, 11, 8));
        currentEmployee.setStreet("N. Example Road");
        currentEmployee.setUnitNumber("15");
        currentEmployee.setCity("Fort Example");
        currentEmployee.setState("Arizona");
        currentEmployee.setZipCode("12345");
        currentEmployee.setEmailNotifications(false);
        currentEmployee.setSmsNotifications(true);
        currentEmployee.setAllowPromotionalMessages(true);
        currentEmployee.setLastChanged(LocalDateTime.of(2024, 5, 30, 12, 48));
        
        updateUI();
    }

    /**
     * Updates the UI with current employee data
     */
    private void updateUI() {
        if (currentEmployee == null) {
            return;
        }
        
        // Update header
        avatar.setName(currentEmployee.getFullName());
        avatar.setAbbreviation(currentEmployee.getInitials());
        
        employeeName.setText(currentEmployee.getFullName());
        lastChangedValue.setText(currentEmployee.getLastChangedFormatted());
        
        // Update form fields
        lastNameField.setValue(currentEmployee.getLastName() != null ? currentEmployee.getLastName() : "");
        firstNameField.setValue(currentEmployee.getFirstName() != null ? currentEmployee.getFirstName() : "");
        phoneField.setValue(currentEmployee.getPhone() != null ? currentEmployee.getPhone() : "");
        emailField.setValue(currentEmployee.getEmail() != null ? currentEmployee.getEmail() : "");
        dateOfBirthPicker.setValue(currentEmployee.getDateOfBirth());
        startDatePicker.setValue(currentEmployee.getStartDate());
        
        streetField.setValue(currentEmployee.getStreet() != null ? currentEmployee.getStreet() : "");
        unitNumberField.setValue(currentEmployee.getUnitNumber() != null ? currentEmployee.getUnitNumber() : "");
        cityField.setValue(currentEmployee.getCity() != null ? currentEmployee.getCity() : "");
        stateField.setValue(currentEmployee.getState() != null ? currentEmployee.getState() : "");
        zipCodeField.setValue(currentEmployee.getZipCode() != null ? currentEmployee.getZipCode() : "");
        
        // Update notification preferences
        Set<String> selectedNotifications = new java.util.HashSet<>();
        if (currentEmployee.isEmailNotifications()) {
            selectedNotifications.add("Email");
        }
        if (currentEmployee.isSmsNotifications()) {
            selectedNotifications.add("SMS");
        }
        sendToCheckboxGroup.setValue(selectedNotifications);
        
        promotionalMessagesRadioGroup.setValue(currentEmployee.isAllowPromotionalMessages() ? "Allow" : "Don't allow");
    }

    /**
     * Handles save changes action
     */
    private void saveChanges() {
        if (currentEmployee == null) {
            return;
        }
        
        // Update employee with form values
        currentEmployee.setLastName(lastNameField.getValue());
        currentEmployee.setFirstName(firstNameField.getValue());
        currentEmployee.setPhone(phoneField.getValue());
        currentEmployee.setEmail(emailField.getValue());
        currentEmployee.setDateOfBirth(dateOfBirthPicker.getValue());
        currentEmployee.setStartDate(startDatePicker.getValue());
        
        currentEmployee.setStreet(streetField.getValue());
        currentEmployee.setUnitNumber(unitNumberField.getValue());
        currentEmployee.setCity(cityField.getValue());
        currentEmployee.setState(stateField.getValue());
        currentEmployee.setZipCode(zipCodeField.getValue());
        
        Set<String> selectedNotifications = sendToCheckboxGroup.getValue();
        currentEmployee.setEmailNotifications(selectedNotifications.contains("Email"));
        currentEmployee.setSmsNotifications(selectedNotifications.contains("SMS"));
        
        currentEmployee.setAllowPromotionalMessages("Allow".equals(promotionalMessagesRadioGroup.getValue()));
        currentEmployee.setLastChanged(LocalDateTime.now());
        
        // Update UI to reflect changes
        updateUI();
        
        // Show success notification
        com.vaadin.flow.component.notification.Notification.show("Changes saved successfully", 3000,
                com.vaadin.flow.component.notification.Notification.Position.BOTTOM_START);
    }

    /**
     * Handles cancel action
     */
    private void cancelChanges() {
        // Reload data to revert changes
        updateUI();
        
        com.vaadin.flow.component.notification.Notification.show("Changes cancelled", 3000,
                com.vaadin.flow.component.notification.Notification.Position.BOTTOM_START);
    }
}
