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
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoIcon;
import com.vaadin.flow.theme.lumo.LumoUtility.*;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Role Management (Alt)")
@Route("role-management-alt")
@Menu(icon = LineAwesomeIconUrl.USER_COG_SOLID)
public class RoleManagementViewAlt extends Main {

    // Layout dimension constants
    private static final String MASTER_SIZE = "560px";
    private static final String NESTED_DETAIL_MIN_SIZE = "300px";

    // Notification messages
    private static final String MSG_ADD_ROLE = "Add role functionality would be implemented here";
    private static final String MSG_CHANGES_CANCELLED = "Changes cancelled";
    private static final String MSG_ROLE_SAVED = "Role saved successfully";
    private static final String MSG_ROLE_SAVE_FAILED = "Failed to save role: ";
    private static final String MSG_LOAD_FAILED = "Failed to load roles";

    // Accessibility labels
    private static final String ARIA_ANALYTICS = "View analytics";
    private static final String ARIA_SETTINGS = "Role settings";
    private static final String ARIA_SELECTION_MODE = "Grid selection mode";

    // Selection mode options
    private static final String SELECTION_MULTI = "Multi-select";
    private static final String SELECTION_SINGLE = "Single-select";

    private final RoleService roleService;

    // Main layout components
    private MasterDetailLayout masterDetailLayout;
    private MasterDetailLayout nestedMasterDetailLayout;

    // Master section components
    private Div masterLayout;
    private Grid<Role> grid;
    private Role activeRole;

    // Form field components
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private IntegerField utilizationField;
    private ComboBox<String> reasonComboBox;
    private Checkbox headOfficeCheckbox;
    private Checkbox teamLeadCheckbox;

    // Detail components
    private H2 detailTitle;

    // Footer components
    private Footer footer;
    
    // Action buttons
    private Button removeButton;

    public RoleManagementViewAlt(RoleService roleService) {
        this.roleService = roleService;

        initStyles();
        createHeader();
        createMasterDetailLayout();
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
        masterDetailLayout.addBackdropClickListener(e -> hideDetail());
        masterDetailLayout.addDetailEscapePressListener(e -> hideDetail());
        masterDetailLayout.getElement().getThemeList().add(MasterDetailLayoutVariant.NO_BORDER);
        masterDetailLayout.getElement().getThemeList().add(MasterDetailLayoutVariant.SHOW_PLACEHOLDER);
        masterDetailLayout.setContainment(MasterDetailLayout.Containment.VIEWPORT);
        masterDetailLayout.setMasterSize(MASTER_SIZE);
        //masterDetailLayout.setDetailMinSize(DETAIL_MIN_SIZE);
        add(masterDetailLayout);

        createMasterSection();

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
        h3.addClassNames(FontSize.MEDIUM, Margin.End.AUTO);

        Button addRole = new Button("Add role");
        addRole.addClickListener(e -> handleAddRole());
        
        removeButton = new Button(new Icon(VaadinIcon.TRASH));
        removeButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        removeButton.setAriaLabel("Remove selected roles");
        removeButton.setTooltipText("Remove selected roles");
        removeButton.setEnabled(false);
        removeButton.addClickListener(e -> handleRemove());

        Button selectionModeButton = new Button(LumoIcon.COG.create());
        selectionModeButton.setAriaLabel(ARIA_SELECTION_MODE);
        selectionModeButton.setTooltipText(ARIA_SELECTION_MODE);

        RadioButtonGroup<String> selectionModeGroup = new RadioButtonGroup<>(ARIA_SELECTION_MODE);
        selectionModeGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        selectionModeGroup.setItems(SELECTION_MULTI, SELECTION_SINGLE);
        selectionModeGroup.setValue(SELECTION_MULTI);
        selectionModeGroup.addValueChangeListener(e -> {
            if (SELECTION_SINGLE.equals(e.getValue())) {
                grid.setSelectionMode(Grid.SelectionMode.SINGLE);
            } else {
                grid.setSelectionMode(Grid.SelectionMode.MULTI);
            }
            grid.deselectAll();
            attachSelectionListener();
        });

        Popover selectionModePopover = new Popover(selectionModeGroup);
        selectionModePopover.setTarget(selectionModeButton);

        Div toolbar = new Div(h3, addRole, removeButton, selectionModeButton);
        toolbar.addClassNames(AlignItems.CENTER, Border.BOTTOM, Display.FLEX, Gap.XSMALL, Padding.Bottom.SMALL,
                Padding.Top.LARGE, Width.FULL);
        masterLayout.add(toolbar);
    }

    /**
     * Creates and configures the grid for displaying roles with single selection mode
     */
    private void createGrid() {
        grid = new Grid<>();
        grid.addThemeVariants(com.vaadin.flow.component.grid.GridVariant.LUMO_NO_BORDER);
        grid.addThemeName(GridVariant.ROW_HEIGHT_FULL);
        grid.getStyle().set("--vaadin-grid-cell-padding", "var(--lumo-space-s)");
        grid.setSelectionMode(Grid.SelectionMode.MULTI);

        grid.addComponentColumn(this::renderRoleItem);
        grid.addComponentColumn(this::renderActions).setAutoWidth(true).setFlexGrow(0);

        attachSelectionListener();

        masterLayout.add(grid);
    }

    /**
     * Attaches or re-attaches the selection listener to the grid.
     * This is needed when the selection mode changes dynamically.
     */
    private void attachSelectionListener() {
        grid.addItemClickListener(e -> {
            Role previousActive = activeRole;
            activeRole = e.getItem();
            if (previousActive != null) {
                grid.getDataProvider().refreshItem(previousActive);
            }
            grid.getDataProvider().refreshItem(activeRole);
            showDetail(activeRole);
        });
        grid.setPartNameGenerator(role -> role.equals(activeRole) ? "active" : "");
        
        // Update remove button state based on selection
        grid.addSelectionListener(e -> {
            removeButton.setEnabled(!e.getAllSelectedItems().isEmpty());
        });
    }

    /**
     * Creates the view footer outside MasterDetailLayout
     */
    private Footer createFooter() {
        Button cancel = new Button("Cancel");
        cancel.addClickListener(e -> handleCancel());

        Button save = new Button("Save and close");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(e -> handleSave());

        footer = new Footer(cancel, save);
        footer.addClassNames(Display.FLEX, Flex.GROW, Gap.SMALL, JustifyContent.END, AlignItems.END, Padding.Top.SMALL);
        return footer;
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
     * Renders a role item component for grid display
     */
    private Div renderRoleItem(Role role) {
        return new GridItemLayout(role);
    }

    /**
     * Renders action buttons (analytics and settings) for each role item
     */
    private Div renderActions(Role role) {
        Button btn1 = new Button(LumoIcon.BAR_CHART.create());
        btn1.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        btn1.setAriaLabel(ARIA_ANALYTICS);
        btn1.setTooltipText(ARIA_ANALYTICS);

        Div buttons = new Div(btn1);
        buttons.addClassNames(Display.FLEX, Gap.XSMALL);
        return buttons;
    }

    /**
     * Shows the detail form for the selected role
     */
    private void showDetail(Role role) {
        if (nestedMasterDetailLayout == null) {
            createNestedMasterDetailLayout();
        }

        populateDetailForm(role);
        masterDetailLayout.setDetail(nestedMasterDetailLayout);
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

        detailTitle = new H2("Product owner");
        detailTitle.addClassNames(FontSize.LARGE);

        Button closeButton = createCloseButton(this::hideDetail);

        Div header = new Div(detailTitle, closeButton);
        header.addClassNames(AlignItems.CENTER, Display.FLEX, Width.FULL);

        Div form = new Div(createFormFields(), createInfoCard());
        form.addClassNames(BoxSizing.BORDER, Display.FLEX, FlexDirection.COLUMN, Gap.MEDIUM, MaxHeight.FULL,
                Overflow.AUTO);

        Div masterLayout = new Div(header, form, createFooter());
        masterLayout.addClassNames(Border.ALL, BorderRadius.MEDIUM, BoxSizing.BORDER, Display.FLEX,
                FlexDirection.COLUMN, Height.FULL, Padding.MEDIUM, Width.FULL);
        nestedMasterDetailLayout.setMaster(masterLayout);

        nestedMasterDetailLayout.addBackdropClickListener(e -> hideNestedDetail());
        nestedMasterDetailLayout.addDetailEscapePressListener(e -> hideNestedDetail());
    }

    /**
     * Creates a close button with consistent styling
     */
    private Button createCloseButton(Runnable clickHandler) {
        Button button = new Button(new Icon(VaadinIcon.CLOSE), e -> clickHandler.run());
        button.addClassNames(Margin.Start.AUTO);
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return button;
    }

    /**
     * Creates form fields for role editing
     */
    private Div createFormFields() {
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

        Div fields = new Div(dateFields, utilizationField, reasonComboBox, checkboxGroup);
        fields.addClassNames(Display.FLEX, FlexDirection.COLUMN);
        return fields;
    }

    /**
     * Creates the info card displayed alongside the form
     */
    private Div createInfoCard() {
        H3 title = new H3("Role info");
        title.addClassNames(FontSize.LARGE);

        Paragraph content = new Paragraph(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                        "Phasellus tellus dui, fringilla nec dictum at, pellentesque sed leo. " +
                        "Donec tellus tellus, ultricies non risus volutpat, gravida luctus ante."
        );

        Button button = new Button("Show more", e -> showNestedDetail());
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Div card = new Div(title, content, button);
        card.addClassNames(AlignItems.START, Background.CONTRAST_5, BorderRadius.MEDIUM, BoxSizing.BORDER, Display.FLEX,
                Flex.AUTO, FlexDirection.COLUMN, Padding.MEDIUM);
        return card;
    }

    /**
     * Populates the detail form with role data
     */
    private void populateDetailForm(Role role) {
        if (role == null) return;

        if (detailTitle != null) {
            detailTitle.setText(role.getName());
        }

        startDatePicker.setValue(role.getStartDate());
        endDatePicker.setValue(role.getEndDate());
        utilizationField.setValue(role.getUtilizationRate());
        reasonComboBox.setValue(role.getReason());
        headOfficeCheckbox.setValue(role.isHeadOffice());
        teamLeadCheckbox.setValue(role.isTeamLead());
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
     * Hides the detail panel and clears the grid selection
     */
    private void hideDetail() {
        masterDetailLayout.setDetail(null);
        if (activeRole != null) {
            Role previousActive = activeRole;
            activeRole = null;
            grid.getDataProvider().refreshItem(previousActive);
        }
    }

    /**
     * Handles the "Add role" button click event
     */
    private void handleAddRole() {
        Notification.show(MSG_ADD_ROLE);
    }
    
    /**
     * Handles the "Remove" button click event.
     * Shows a notification with employee status and selected role information.
     */
    private void handleRemove() {
        Employee employee = roleService.getCurrentEmployee();
        var selectedRoles = grid.getSelectedItems();
        
        if (selectedRoles.isEmpty()) {
            return;
        }
        
        String message = String.format("Selected %d role(s) for removal: %s",
                selectedRoles.size(),
                selectedRoles.stream().map(Role::getName).reduce((a, b) -> a + ", " + b).orElse(""));
        
        Notification.show(message);
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
        if (activeRole == null) {
            return;
        }

        if (!validateForm()) {
            return;
        }

        try {
            activeRole.setStartDate(startDatePicker.getValue());
            activeRole.setEndDate(endDatePicker.getValue());

            // Handle nullable Integer field with default value
            Integer utilizationValue = utilizationField.getValue();
            activeRole.setUtilizationRate(utilizationValue != null ? utilizationValue : 0);

            activeRole.setReason(reasonComboBox.getValue());
            activeRole.setHeadOffice(headOfficeCheckbox.getValue());
            activeRole.setTeamLead(teamLeadCheckbox.getValue());

            roleService.saveRole(activeRole);
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
