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

import java.util.List;

import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("List Object Page")
@Route("list-object-page")
@Menu(icon = LineAwesomeIconUrl.USER_COG_SOLID)
public class ListObjectPage extends Main {

    // Layout dimension constants
    private static final String MASTER_SIZE = "560px";
    private static final String NESTED_DETAIL_MIN_SIZE = "300px";

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

    public ListObjectPage(RoleService roleService) {
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
        
        removeButton = new Button(new Icon(VaadinIcon.TRASH));
        removeButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        removeButton.setAriaLabel("Remove selected roles");
        removeButton.setTooltipText("Remove selected roles");
        removeButton.setEnabled(false);

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
        cancel.addClickListener(e -> hideNestedDetail());

        Button save = new Button("Save and close");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(e -> hideNestedDetail());
        
        footer = new Footer(cancel, save);
        footer.addClassNames(Display.FLEX, Flex.GROW, Gap.SMALL, JustifyContent.END, AlignItems.END, Padding.Top.SMALL);
        return footer;
    }

    /**
     * Loads available roles from the service and displays them in the grid
     */
    private void loadRoles() {
        grid.setItems(roleService.getAvailableRoles());
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
        createNestedMasterDetailLayout();
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

        Div form = new Div(createSection(), createGridSection());
        form.addClassNames(BoxSizing.BORDER, Display.FLEX, FlexDirection.COLUMN, Gap.MEDIUM, MaxHeight.FULL,
                Overflow.AUTO);

        Div masterLayout = new Div(header, form);
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
     * Creates a section with header, key-value content, and divider following Figma design
     */
    private Div createSection() {
        Div section = new Div();
        section.addClassNames(Display.FLEX, FlexDirection.COLUMN, Gap.MEDIUM, Width.FULL);

        // Section header (Grid Toolbar)
        Div toolbar = createSectionToolbar();
        
        // Content with three columns of key-value pairs
        Div content = createSectionContent();
        
        // Divider
        Hr divider = new Hr();
        divider.addClassNames(Width.FULL);

        section.add(toolbar, content, divider);
        return section;
    }

    /**
     * Creates the section toolbar with heading, badges, and edit button
     */
    private Div createSectionToolbar() {
        // Left side: heading and badges
        H3 heading = new H3("Sektion 1");
        heading.addClassNames(FontSize.MEDIUM);

        Span badge1 = new Span("Abgerechnet");
        badge1.getElement().getThemeList().add("badge success");
        badge1.addClassNames(FontSize.SMALL, FontWeight.MEDIUM);

        Span badge2 = new Span("Folgeänderung vorhanden");
        badge2.getElement().getThemeList().add("badge");
        badge2.addClassNames(FontSize.SMALL, FontWeight.MEDIUM);
        badge2.getStyle().set("background", "var(--lumo-primary-color-10pct)");
        badge2.getStyle().set("color", "var(--lumo-primary-color)");

        Div badgeGroup = new Div(badge1, badge2);
        badgeGroup.addClassNames(Display.FLEX, Gap.MEDIUM);

        Div leftSide = new Div(heading, badgeGroup);
        leftSide.addClassNames(AlignItems.CENTER, Display.FLEX, Flex.ONE, Gap.MEDIUM);

        // Right side: edit button
        Button editButton = new Button("Bearbeiten");
        editButton.addClickListener(e -> showNestedDetail());

        Div rightSide = new Div(editButton);
        rightSide.addClassNames(Display.FLEX, Gap.MEDIUM, JustifyContent.END);

        Div toolbar = new Div(leftSide, rightSide);
        toolbar.addClassNames(Display.FLEX, Gap.MEDIUM, Width.FULL);
        return toolbar;
    }

    /**
     * Creates the section content with three columns of key-value facets
     */
    private Div createSectionContent() {
        Div content = new Div();
        content.addClassNames(Display.FLEX, Gap.Column.XLARGE, Width.FULL);

        // Create three columns
        content.add(createKeyValueColumn(), createKeyValueColumn(), createKeyValueColumn());
        return content;
    }

    /**
     * Creates a column of key-value facets
     */
    private Div createKeyValueColumn() {
        Div column = new Div();
        column.addClassNames(Display.FLEX, Flex.ONE, FlexDirection.COLUMN, Gap.SMALL);

        // Add three key-value pairs
        column.add(
                createKeyValueFacet("Text", "Wert"),
                createKeyValueFacet("Text", "Wert"),
                createKeyValueFacet("Text", "Wert")
        );
        return column;
    }

    /**
     * Creates a key-value facet (stacked layout)
     */
    private Div createKeyValueFacet(String key, String value) {
        Span keyLabel = new Span(key);
        keyLabel.addClassNames(TextColor.SECONDARY);

        Span valueLabel = new Span(value);
        valueLabel.addClassNames(FontWeight.SEMIBOLD, TextColor.BODY);

        Div facet = new Div(keyLabel, valueLabel);
        facet.addClassNames(Display.FLEX, FlexDirection.COLUMN, Width.FULL);
        return facet;
    }

    /**
     * Creates the grid section with toolbar and data grid following Figma design
     */
    private Div createGridSection() {
        Div section = new Div();
        section.addClassNames(Display.FLEX, FlexDirection.COLUMN, Gap.MEDIUM, Width.FULL);

        // Grid toolbar
        Div toolbar = createGridToolbar();
        
        // Grid with data
        Grid<GridItem> grid = createDataGrid();
        
        section.add(toolbar, grid);
        return section;
    }

    /**
     * Creates the grid toolbar with heading, search field, and add button
     */
    private Div createGridToolbar() {
        // Left side: heading
        H3 heading = new H3("Sektion 2");
        heading.addClassNames(FontSize.MEDIUM);

        Div leftSide = new Div(heading);
        leftSide.addClassNames(AlignItems.CENTER, Display.FLEX, Flex.ONE, Gap.MEDIUM);

        // Right side: search field and add button
        com.vaadin.flow.component.textfield.TextField searchField = new com.vaadin.flow.component.textfield.TextField();
        searchField.setPlaceholder("Suchen");
        searchField.setSuffixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setWidth("192px");

        Button addButton = new Button("Hinzufügen");

        Div rightSide = new Div(searchField, addButton);
        rightSide.addClassNames(Display.FLEX, Gap.MEDIUM, JustifyContent.END);

        Div toolbar = new Div(leftSide, rightSide);
        toolbar.addClassNames(Display.FLEX, Gap.MEDIUM, Width.FULL);
        return toolbar;
    }

    /**
     * Creates and configures the data grid
     */
    private Grid<GridItem> createDataGrid() {
        Grid<GridItem> grid = new Grid<>();
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addClassNames(Width.FULL);

        // Column 1: Gültig ab (Valid from)
        grid.addColumn(GridItem::getValidFrom)
                .setHeader("Gültig ab")
                .setSortable(true);

        // Column 2: Gültig bis (Valid to)
        grid.addColumn(GridItem::getValidTo)
                .setHeader("Gültig bis")
                .setSortable(true);

        // Column 3: Header 1
        grid.addColumn(GridItem::getHeader1)
                .setHeader("Header 1")
                .setSortable(true);

        // Column 4: Header 2
        grid.addColumn(GridItem::getHeader2)
                .setHeader("Header 2")
                .setSortable(true);

        // Column 5: Header 3 (right-aligned)
        grid.addColumn(GridItem::getHeader3)
                .setHeader("Header 3")
                .setSortable(true);

        // Column 6: Status with badges
        grid.addComponentColumn(this::createStatusBadges)
                .setHeader("Status")
                .setSortable(true)
                .setFlexGrow(2);

        // Column 7: Inline actions
        grid.addComponentColumn(this::createInlineActions)
                .setHeader("")
                .setAutoWidth(true)
                .setFlexGrow(0);

        // Set sample data
        grid.setItems(createSampleGridData());

        return grid;
    }

    /**
     * Creates status badges for grid items
     */
    private Div createStatusBadges(GridItem item) {
        Div badgeGroup = new Div();
        badgeGroup.addClassNames(Display.FLEX, Gap.MEDIUM, FlexWrap.WRAP);

        for (String status : item.getStatuses()) {
            Span badge = new Span(status);
            badge.getElement().getThemeList().add("badge");
            badge.addClassNames(FontSize.SMALL, FontWeight.MEDIUM);
            badge.getStyle().set("background", "var(--lumo-primary-color-10pct)");
            badge.getStyle().set("color", "var(--lumo-primary-color)");
            badgeGroup.add(badge);
        }

        return badgeGroup;
    }

    /**
     * Creates inline action buttons for grid items
     */
    private Button createInlineActions(GridItem item) {
        Button actionsButton = new Button(new Icon(VaadinIcon.ELLIPSIS_DOTS_V));
        actionsButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        actionsButton.setAriaLabel("More actions");
        return actionsButton;
    }

    /**
     * Creates sample data for the grid
     */
    private java.util.List<GridItem> createSampleGridData() {
        return java.util.Arrays.asList(
                new GridItem("TT.MM.JJJJ", "TT.MM.JJJJ", "TT.MM.JJJJ", "Wert", "Betrag €", 
                        java.util.Arrays.asList("Information", "Information", "Information", "Information")),
                new GridItem("TT.MM.JJJJ", "TT.MM.JJJJ", "TT.MM.JJJJ", "Wert", "Betrag €", 
                        java.util.Arrays.asList("Information", "Information", "Information", "Information")),
                new GridItem("TT.MM.JJJJ", "TT.MM.JJJJ", "TT.MM.JJJJ", "Wert", "Betrag €", 
                        java.util.Arrays.asList("Information", "Information", "Information", "Information"))
        );
    }

    /**
     * Grid item data class
     */
    private static class GridItem {
        private final String validFrom;
        private final String validTo;
        private final String header1;
        private final String header2;
        private final String header3;
        private final java.util.List<String> statuses;

        public GridItem(String validFrom, String validTo, String header1, String header2, String header3, java.util.List<String> statuses) {
            this.validFrom = validFrom;
            this.validTo = validTo;
            this.header1 = header1;
            this.header2 = header2;
            this.header3 = header3;
            this.statuses = statuses;
        }

        public String getValidFrom() { return validFrom; }
        public String getValidTo() { return validTo; }
        public String getHeader1() { return header1; }
        public String getHeader2() { return header2; }
        public String getHeader3() { return header3; }
        public java.util.List<String> getStatuses() { return statuses; }
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
     * Shows the nested detail panel with form content based on Figma design
     */
    private void showNestedDetail() {
        Div nestedDetailContent = createNestedDetailContent();
        nestedMasterDetailLayout.setDetail(nestedDetailContent);
    }

    /**
     * Creates the content for the nested detail panel - a form with two fieldsets
     */
    private Div createNestedDetailContent() {
        Div content = new Div();
        content.addClassNames(Border.ALL, BorderRadius.MEDIUM, BoxSizing.BORDER, Display.FLEX,
                FlexDirection.COLUMN, Height.FULL, Padding.MEDIUM, Width.FULL);

        // Header
        H3 nestedTitle = new H3("Form Title");
        nestedTitle.addClassNames(FontSize.LARGE);

        Button nestedCloseButton = createCloseButton(this::hideNestedDetail);

        Div header = new Div(nestedTitle, nestedCloseButton);
        header.addClassNames(AlignItems.CENTER, Display.FLEX, Width.FULL);

        // Form with two fieldsets
        Div form = createFormLayout();

        content.add(header, form);
        return content;
    }

    /**
     * Creates the form layout with two fieldsets side by side
     */
    private Div createFormLayout() {
        Div form = new Div();
        form.addClassNames(Display.FLEX, FlexDirection.COLUMN, Gap.MEDIUM, Width.FULL, Padding.MEDIUM);

        // Two-column fieldset layout
        Div fieldsets = new Div();
        fieldsets.addClassNames(Display.FLEX, Gap.XLARGE, Width.FULL);

        // Fieldset 1 (left column)
        Div fieldset1 = createFieldset1();
        
        // Fieldset 2 (right column)
        Div fieldset2 = createFieldset2();

        fieldsets.add(fieldset1, fieldset2);
        form.add(fieldsets);

        form.add(createFooter());
        
        return form;
    }

    /**
     * Creates the first fieldset with 6 text fields
     */
    private Div createFieldset1() {
        Div fieldset = new Div();
        fieldset.addClassNames(Display.FLEX, Flex.ONE, FlexDirection.COLUMN, Gap.MEDIUM);

        // Heading
        H3 title = new H3("Titel");
        title.addClassNames(FontSize.SMALL, FontWeight.BOLD, Padding.Top.SMALL);

        // First row: two fields side by side
        Div row1 = new Div();
        row1.addClassNames(Display.FLEX, Gap.MEDIUM, Width.FULL);
        row1.add(
                createTextField("Label", "Wert"),
                createTextField("Label", "Wert")
        );

        // Single fields
        com.vaadin.flow.component.textfield.TextField field3 = createTextField("Label", "Wert");
        com.vaadin.flow.component.textfield.TextField field4 = createTextField("Label", "Wert");

        // Last row: two fields side by side
        Div row2 = new Div();
        row2.addClassNames(Display.FLEX, Gap.MEDIUM, Width.FULL);
        row2.add(
                createTextField("Label", "Wert"),
                createTextField("Label", "Wert")
        );

        fieldset.add(title, row1, field3, field4, row2);
        return fieldset;
    }

    /**
     * Creates the second fieldset with 2 text fields
     */
    private Div createFieldset2() {
        Div fieldset = new Div();
        fieldset.addClassNames(Display.FLEX, Flex.ONE, FlexDirection.COLUMN, Gap.MEDIUM);

        // Heading
        H3 title = new H3("Titel");
        title.addClassNames(FontSize.SMALL, FontWeight.BOLD, Padding.Top.SMALL);

        // Two fields
        com.vaadin.flow.component.textfield.TextField field1 = createTextField("Label", "Wert");
        com.vaadin.flow.component.textfield.TextField field2 = createTextField("Label", "Wert");

        fieldset.add(title, field1, field2);
        return fieldset;
    }

    /**
     * Creates a text field with label and value
     */
    private com.vaadin.flow.component.textfield.TextField createTextField(String label, String value) {
        com.vaadin.flow.component.textfield.TextField textField = new com.vaadin.flow.component.textfield.TextField();
        textField.setLabel(label);
        textField.setValue(value);
        textField.setWidth("100%");
        return textField;
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

}
