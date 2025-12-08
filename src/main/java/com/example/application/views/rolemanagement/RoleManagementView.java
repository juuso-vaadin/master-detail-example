package com.example.application.views.rolemanagement;

import com.example.application.components.GridItemLayout;
import com.example.application.components.GridVariant;
import com.example.application.components.MasterDetailLayoutVariant;
import com.example.application.data.Employee;
import com.example.application.data.Role;
import com.example.application.service.RoleService;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
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
import com.vaadin.flow.theme.lumo.LumoIcon;
import com.vaadin.flow.theme.lumo.LumoUtility.*;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

/**
 * Role Management View implementing the Figma design using proper MasterDetailLayout.
 * <p>
 * Analysis performed following guidelines:
 * 1. get_code: Identified Avatar, Card, Button, DatePicker, NumberField, ComboBox, Checkbox components
 * 2. get_variable_defs: Retrieved color and typography definitions
 * 3. get_metadata: Understood layout structure and hierarchy
 * <p>
 * Typography mapping based on Figma text styles:
 * - "Heading 2" (28px, Semi Bold) → H1 for "Roles"
 * - "Heading 4" (18px, Semi Bold) → H2 for "Product owner"
 * - "Heading 5" (16px, Semi Bold) → H3 for "Assigned roles"
 */
@PageTitle("Role Management")
@Route("role-management")
@Menu(order = 2, icon = LineAwesomeIconUrl.USER_COG_SOLID)
public class RoleManagementView extends Main {

    // Layout dimension constants
    private static final String MASTER_SIZE = "560px";
    private static final String DETAIL_MIN_SIZE = "460px";
    private static final String NESTED_DETAIL_MIN_SIZE = "300px";
    private static final String INFO_CARD_WIDTH = "300px";

    // Notification messages
    private static final String MSG_ADD_ROLE = "Add role functionality would be implemented here";
    private static final String MSG_CHANGES_CANCELLED = "Changes cancelled";
    private static final String MSG_ROLE_SAVED = "Role saved successfully";
    private static final String MSG_ROLE_SAVE_FAILED = "Failed to save role: ";
    private static final String MSG_LOAD_FAILED = "Failed to load roles";

    // Accessibility labels
    private static final String ARIA_ANALYTICS = "View analytics";
    private static final String ARIA_SETTINGS = "Role settings";

    private final RoleService roleService;

    // Main layout components
    private MasterDetailLayout masterDetailLayout;
    private MasterDetailLayout nestedMasterDetailLayout;

    // Master section components
    private Div masterLayout;
    private com.vaadin.flow.component.grid.Grid<Role> grid;

    // Form field components
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private IntegerField utilizationField;
    private ComboBox<String> reasonComboBox;
    private Checkbox headOfficeCheckbox;
    private Checkbox teamLeadCheckbox;

    // Detail components - stored references to avoid brittle access
    private H2 roleDetailTitle;

    // Footer components
    private Footer footer;

    public RoleManagementView(RoleService roleService) {
        this.roleService = roleService;

        initStyles();
        createHeader();
        createMasterDetailLayout();
        createViewFooter();

        loadRoles();
    }

    private void initStyles() {
        addClassNames(Display.FLEX, FlexDirection.COLUMN, Height.FULL, Width.FULL);
    }

    /**
     * Creates the view header outside of MasterDetailLayout.
     * Uses H2 since the application already has H1 in MainLayout.
     */
    private void createHeader() {
        H2 title = new H2("Roles");
        Paragraph subtitle = new Paragraph("Which roles should this person be assigned to?");

        Div header = new Div(title, subtitle);
        header.addClassNames(Display.FLEX, FlexDirection.COLUMN, Padding.Horizontal.LARGE, Padding.Vertical.MEDIUM);
        add(header);
    }

    /**
     * Creates the MasterDetailLayout component
     */
    private void createMasterDetailLayout() {
        masterDetailLayout = new MasterDetailLayout();
        masterDetailLayout.addClassNames(Flex.ONE);
        masterDetailLayout.getElement().getThemeList().add(MasterDetailLayoutVariant.NO_BORDER);
        masterDetailLayout.getElement().getThemeList().add(MasterDetailLayoutVariant.SHOW_PLACEHOLDER);
        masterDetailLayout.setContainment(MasterDetailLayout.Containment.VIEWPORT);
        masterDetailLayout.setMasterSize(MASTER_SIZE);
        masterDetailLayout.setDetailMinSize(DETAIL_MIN_SIZE);
        add(masterDetailLayout);

        createMasterSection();
        setupMasterDetailEventListeners();
    }

    /**
     * Creates the view footer outside MasterDetailLayout
     */
    private void createViewFooter() {
        Button cancel = new Button("Cancel");
        cancel.addClickListener(e -> handleCancel());

        Button save = new Button("Save and close");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(e -> handleSave());

        footer = new Footer(cancel, save);
        footer.addClassNames(Background.CONTRAST_5, Display.FLEX, Gap.SMALL, JustifyContent.END,
                Padding.Horizontal.LARGE, Padding.Vertical.SMALL);
        add(footer);
    }

    /**
     * Creates the master section following Figma design:
     * 1. Static header with employee card
     * 2. Toolbar with "Assigned roles" title and "Add role" button
     * 3. Grid displaying selectable role items
     */
    private void createMasterSection() {
        masterLayout = new Div();
        masterLayout.addClassNames(BoxSizing.BORDER, Display.FLEX, FlexDirection.COLUMN, Height.FULL,
                Padding.Horizontal.LARGE, Width.FULL);
        masterDetailLayout.setMaster(masterLayout);

        createEmployeeCard();
        createToolbar();
        createGrid();
    }

    /**
     * Sets up event listeners for the MasterDetailLayout
     */
    private void setupMasterDetailEventListeners() {
        masterDetailLayout.addBackdropClickListener(e -> hideDetail());
        masterDetailLayout.addDetailEscapePressListener(e -> hideDetail());
    }

    /**
     * Creates the employee header card
     */
    private void createEmployeeCard() {
        Employee employee = roleService.getCurrentEmployee();

        Avatar avatar = new Avatar(employee.getFullName());
        avatar.setAbbreviation(employee.getInitials());

        H3 employeeName = new H3(employee.getFullName());
        employeeName.addClassNames(FontSize.LARGE);

        Span personalInfo = new Span("Personal no " + employee.getPersonalNumber());
        personalInfo.addClassNames(TextColor.SECONDARY);

        Div employeeInfo = new Div(employeeName, personalInfo);
        employeeInfo.addClassNames(Display.FLEX, Flex.ONE, FlexDirection.COLUMN);

        Span statusBadge = new Span(employee.getStatus());
        statusBadge.addClassNames(FontSize.SMALL, FontWeight.MEDIUM);
        statusBadge.getElement().getThemeList().add("badge success");

        Div employeeCard = new Div(avatar, employeeInfo, statusBadge);
        employeeCard.addClassNames(AlignItems.CENTER, Background.BASE, Border.ALL, BorderRadius.MEDIUM, BoxSizing.BORDER,
                Display.FLEX, Gap.MEDIUM, Padding.MEDIUM, Width.FULL);
        masterLayout.add(employeeCard);
    }

    /**
     * Creates toolbar with "Assigned roles" heading and "Add role" button
     */
    private void createToolbar() {
        H3 h3 = new H3("Assigned roles");
        h3.addClassNames(FontSize.MEDIUM);

        Button addRole = new Button("Add role");
        addRole.addClickListener(e -> handleAddRole());

        Div toolbar = new Div(h3, addRole);
        toolbar.addClassNames(AlignItems.CENTER, Display.FLEX, JustifyContent.BETWEEN, Padding.Bottom.SMALL,
                Padding.Top.LARGE, Width.FULL);
        masterLayout.add(toolbar);
    }

    /**
     * Creates and configures the grid for displaying roles with single selection mode
     */
    private void createGrid() {
        grid = new Grid<>();
        grid.addClassNames("border-x-0");
        grid.addThemeName(GridVariant.ROW_HEIGHT_FULL);
        grid.getStyle().set("--vaadin-grid-cell-padding", "var(--lumo-space-s)");
        grid.setSelectionMode(Grid.SelectionMode.MULTI);

        grid.addComponentColumn(this::createItem);
        grid.addComponentColumn(this::createButtons).setAutoWidth(true).setFlexGrow(0);

        grid.addSelectionListener(e -> {
            if (e.getFirstSelectedItem().isPresent()) {
                showRoleDetail(e.getFirstSelectedItem().get());
            }
        });

        masterLayout.add(grid);
    }

    /**
     * Loads available roles from the service and displays them in the grid
     */
    private void loadRoles() {
        try {
            grid.setItems(roleService.getAvailableRoles());
        } catch (Exception e) {
            Notification.show(MSG_LOAD_FAILED + ": " + e.getMessage());
        }
    }

    /**
     * Creates a role item component for grid display
     */
    private Div createItem(Role role) {
        return new GridItemLayout(role);
    }

    /**
     * Creates action buttons (analytics and settings) for each role item
     */
    private Div createButtons(Role role) {
        Button btn1 = new Button(LumoIcon.BAR_CHART.create());
        btn1.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        btn1.setAriaLabel(ARIA_ANALYTICS);
        btn1.setTooltipText(ARIA_ANALYTICS);

        Button btn2 = new Button(LumoIcon.COG.create());
        btn2.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        btn2.setAriaLabel(ARIA_SETTINGS);
        btn2.setTooltipText(ARIA_SETTINGS);

        Div buttons = new Div(btn1, btn2);
        buttons.addClassNames(Display.FLEX, Gap.XSMALL);
        return buttons;
    }

    /**
     * Refreshes the visual selection state of role cards
     */
    private void refreshRoleSelection() {
        grid.getDataProvider().refreshAll();
    }

    /**
     * Shows the detail form for the selected role
     */
    private void showRoleDetail(Role role) {
        if (nestedMasterDetailLayout == null) {
            createNestedMasterDetailLayout();
        }

        // Update form with role data
        populateDetailForm(role);
        masterDetailLayout.setDetail(nestedMasterDetailLayout);
    }

    /**
     * Creates form fields for role editing
     */
    private VerticalLayout createFormFields() {
        VerticalLayout fields = new VerticalLayout();
        fields.setPadding(false);

        HorizontalLayout dateFields = new HorizontalLayout();
        dateFields.setWidthFull();

        startDatePicker = new DatePicker("Start");
        startDatePicker.setWidthFull();

        endDatePicker = new DatePicker("End");
        endDatePicker.setWidthFull();

        dateFields.add(startDatePicker, endDatePicker);

        utilizationField = new IntegerField("Utilisation rate");
        utilizationField.setWidthFull();
        utilizationField.setMin(0);
        utilizationField.setMax(100);
        utilizationField.setStepButtonsVisible(true);

        reasonComboBox = new ComboBox<>("Reason");
        reasonComboBox.setWidthFull();
        reasonComboBox.setItems(roleService.getAvailableReasons());

        HorizontalLayout checkboxGroup = new HorizontalLayout();
        checkboxGroup.setSpacing(true);

        headOfficeCheckbox = new Checkbox("Head office");
        teamLeadCheckbox = new Checkbox("Team lead");

        checkboxGroup.add(headOfficeCheckbox, teamLeadCheckbox);

        fields.add(dateFields, utilizationField, reasonComboBox, checkboxGroup);
        return fields;
    }

    /**
     * Creates the info card displayed alongside the form
     */
    private VerticalLayout createInfoCard() {
        VerticalLayout card = new VerticalLayout();
        card.setPadding(true);
        card.addClassNames(Background.CONTRAST_5, BorderRadius.MEDIUM);

        H3 cardTitle = new H3("Role info");
        cardTitle.addClassNames(TextColor.HEADER, FontSize.LARGE, FontWeight.SEMIBOLD);

        Paragraph cardContent = new Paragraph(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                        "Phasellus tellus dui, fringilla nec dictum at, pellentesque sed leo. " +
                        "Donec tellus tellus, ultricies non risus volutpat, gravida luctus ante."
        );
        cardContent.addClassNames(TextColor.BODY);

        Button showMoreButton = new Button("Show more");
        showMoreButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        showMoreButton.addClickListener(e -> showNestedDetail());

        card.add(cardTitle, cardContent, showMoreButton);
        return card;
    }

    /**
     * Populates the detail form with role data
     */
    private void populateDetailForm(Role role) {
        if (role == null) return;

        if (roleDetailTitle != null) {
            roleDetailTitle.setText(role.getName());
        }

        startDatePicker.setValue(role.getStartDate());
        endDatePicker.setValue(role.getEndDate());
        utilizationField.setValue(role.getUtilizationRate());
        reasonComboBox.setValue(role.getReason());
        headOfficeCheckbox.setValue(role.isHeadOffice());
        teamLeadCheckbox.setValue(role.isTeamLead());
    }

    /**
     * Hides the detail panel and clears the grid selection
     */
    private void hideDetail() {
        masterDetailLayout.setDetail(null);
        grid.asSingleSelect().clear();
        refreshRoleSelection();
    }

    /**
     * Creates a close button with consistent styling
     */
    private Button createCloseButton(Runnable clickHandler) {
        Button closeButton = new Button(new Icon(VaadinIcon.CLOSE));
        closeButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
        closeButton.addClickListener(e -> clickHandler.run());
        closeButton.getStyle().set("margin-left", "auto");
        return closeButton;
    }

    /**
     * Creates the nested MasterDetailLayout that contains the form as master.
     * This layout is shown in the main layout's detail area, creating a nested structure
     * that allows for additional detail panels within the role editing form.
     */
    private void createNestedMasterDetailLayout() {
        nestedMasterDetailLayout = new MasterDetailLayout();
        nestedMasterDetailLayout.addClassNames(Height.FULL, Padding.Bottom.MEDIUM, Padding.Right.MEDIUM, Width.FULL);
        nestedMasterDetailLayout.getElement().getThemeList().add(MasterDetailLayoutVariant.NO_BORDER);
        nestedMasterDetailLayout.setDetailMinSize(NESTED_DETAIL_MIN_SIZE);
        nestedMasterDetailLayout.setId("nested-master-detail");
        nestedMasterDetailLayout.setOverlayMode(MasterDetailLayout.OverlayMode.STACK);

        VerticalLayout masterContent = new VerticalLayout();
        masterContent.setSizeFull();
        masterContent.setPadding(true);
        masterContent.setSpacing(false);
        masterContent.addClassNames(Border.ALL, BorderRadius.MEDIUM, BorderColor.CONTRAST_10);

        roleDetailTitle = new H2("Product owner");
        roleDetailTitle.addClassNames(TextColor.HEADER, FontSize.LARGE, FontWeight.SEMIBOLD);

        Button closeButton = createCloseButton(this::hideDetail);

        HorizontalLayout detailHeader = new HorizontalLayout(roleDetailTitle, closeButton);
        detailHeader.setWidthFull();
        detailHeader.setAlignItems(HorizontalLayout.Alignment.CENTER);

        HorizontalLayout formContent = new HorizontalLayout();
        formContent.setSizeFull();
        formContent.setSpacing(true);
        formContent.setWrap(true);
        formContent.addClassNames(AlignContent.START, AlignItems.START);

        VerticalLayout formFields = createFormFields();
        formFields.setWidth("fit-content");
        formFields.addClassName(Flex.AUTO);

        VerticalLayout infoCard = createInfoCard();
        infoCard.setWidth(INFO_CARD_WIDTH);
        infoCard.addClassName(Flex.AUTO);

        formContent.add(formFields, infoCard);
        masterContent.add(detailHeader, formContent);
        nestedMasterDetailLayout.setMaster(masterContent);

        nestedMasterDetailLayout.addBackdropClickListener(e -> hideNestedDetail());
        nestedMasterDetailLayout.addDetailEscapePressListener(e -> hideNestedDetail());
    }

    /**
     * Shows the nested detail panel with placeholder content
     */
    private void showNestedDetail() {
        VerticalLayout nestedDetailContent = createNestedDetailContent();
        nestedMasterDetailLayout.setDetail(nestedDetailContent);
    }

    /**
     * Creates the content for the nested detail panel
     */
    private VerticalLayout createNestedDetailContent() {
        VerticalLayout nestedDetailContent = new VerticalLayout();
        nestedDetailContent.setSizeFull();
        nestedDetailContent.setPadding(true);
        nestedDetailContent.addClassName(Background.CONTRAST_5);

        H3 nestedTitle = new H3("Additional Role Information");
        nestedTitle.addClassNames(TextColor.HEADER, FontSize.LARGE, FontWeight.SEMIBOLD);

        Button nestedCloseButton = createCloseButton(this::hideNestedDetail);

        HorizontalLayout nestedHeader = new HorizontalLayout(nestedTitle, nestedCloseButton);
        nestedHeader.setWidthFull();
        nestedHeader.setAlignItems(HorizontalLayout.Alignment.CENTER);

        Paragraph nestedContent = new Paragraph(
                "This is a nested detail panel showing additional information about the selected role. " +
                        "This demonstrates how Master-Detail Layout can be nested to create multi-level navigation. " +
                        "You can add more detailed information, charts, tables, or any other relevant content here."
        );
        nestedContent.addClassNames(TextColor.BODY);

        nestedDetailContent.add(nestedHeader, nestedContent);
        return nestedDetailContent;
    }

    /**
     * Hides the nested detail panel
     */
    private void hideNestedDetail() {
        if (nestedMasterDetailLayout != null) {
            nestedMasterDetailLayout.setDetail(null);
        }
    }

    /**
     * Handles the "Add role" button click event
     */
    private void handleAddRole() {
        Notification.show(MSG_ADD_ROLE);
    }

    /**
     * Handles the "Cancel" button click event, closing the detail view without saving
     */
    private void handleCancel() {
        hideDetail();
        Notification.show(MSG_CHANGES_CANCELLED);
    }

    /**
     * Handles the "Save and close" button click event.
     * Validates form data and saves the selected role to the service.
     */
    private void handleSave() {
        Role selectedRole = grid.asSingleSelect().getValue();
        if (selectedRole == null) {
            return;
        }

        if (!validateForm()) {
            return;
        }

        try {
            selectedRole.setStartDate(startDatePicker.getValue());
            selectedRole.setEndDate(endDatePicker.getValue());

            // Handle nullable Integer field with default value
            Integer utilizationValue = utilizationField.getValue();
            selectedRole.setUtilizationRate(utilizationValue != null ? utilizationValue : 0);

            selectedRole.setReason(reasonComboBox.getValue());
            selectedRole.setHeadOffice(headOfficeCheckbox.getValue());
            selectedRole.setTeamLead(teamLeadCheckbox.getValue());

            roleService.saveRole(selectedRole);
            Notification.show(MSG_ROLE_SAVED);
            hideDetail();
        } catch (Exception e) {
            Notification.show(MSG_ROLE_SAVE_FAILED + e.getMessage());
        }
    }

    /**
     * Validates the form fields including required fields, date ranges, and utilization rate bounds.
     *
     * @return true if all validations pass, false otherwise
     */
    private boolean validateForm() {
        if (startDatePicker.getValue() == null) {
            Notification.show("Start date is required");
            startDatePicker.focus();
            return false;
        }

        if (endDatePicker.getValue() == null) {
            Notification.show("End date is required");
            endDatePicker.focus();
            return false;
        }

        if (endDatePicker.getValue().isBefore(startDatePicker.getValue())) {
            Notification.show("End date must be after start date");
            endDatePicker.focus();
            return false;
        }

        Integer utilization = utilizationField.getValue();
        if (utilization != null && (utilization < 0 || utilization > 100)) {
            Notification.show("Utilization rate must be between 0 and 100");
            utilizationField.focus();
            return false;
        }

        return true;
    }
}
