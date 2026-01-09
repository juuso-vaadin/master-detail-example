package com.example.application.views.masterdetail;

import com.example.application.data.Message;
import com.example.application.data.Person;
import com.example.application.service.DataService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.masterdetaillayout.MasterDetailLayout;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@PageTitle("Progress Demo")
@Route("progress-demo")
@Menu(icon = LineAwesomeIconUrl.FILE)
public class ProgressDemoView extends MasterDetailLayout {

    private final DataService dataService;
    private Grid<Person> grid;
    private MessageList messageList;
    private MasterDetailLayout nestedMasterDetailLayout;
    private VerticalLayout personFormContent;
    private VerticalLayout messageDetailContent;

    // Form fields
    private TextField firstNameField;
    private TextField lastNameField;
    private TextField textValueField;
    private IntegerField numericValueField;

    public ProgressDemoView(DataService dataService) {
        this.dataService = dataService;
        setSizeFull();
        addClassName("mdl-progress");

        setMasterMinSize("320px");
        setDetailMinSize("320px");

        createMasterSection();
        // Detail section will be created when a person is selected
        setupSelectionListener();
    }

    /**
     * Creates the master section with Grid component
     * Based on Figma data-name="Grid"
     */
    private void createMasterSection() {
        // Create Grid component as identified in Figma metadata
        grid = new Grid<>(Person.class, false);

        // Add columns matching the Figma design structure
        grid.addColumn(Person::getFirstName).setHeader("Text").setFlexGrow(1);
        grid.addColumn(Person::getLastName).setHeader("Text").setFlexGrow(1);
        grid.addColumn(person -> String.format("%,d", person.getNumericValue()))
                .setHeader("Numeric")
                .setFlexGrow(1)
                .getElement().getStyle().set("text-align", "right");
        grid.addColumn(Person::getTextValue).setHeader("Text").setFlexGrow(1);

        // Set data from service
        grid.setItems(dataService.getAllPeople());
        grid.setSizeFull();

        // Apply styling
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        RadioButtonGroup<String> splitRatio = new RadioButtonGroup<>("Split Ratio");
        splitRatio.setItems("50/25/25", "33/33/33");
        splitRatio.setValue("50/25/25");
        splitRatio.addClassName(LumoUtility.Padding.Horizontal.MEDIUM);

        // Add value change listener to handle classname changes
        splitRatio.addValueChangeListener(event -> {
            String selectedValue = event.getValue();
            // Remove any existing equal-split classname
            removeClassName("equal-split");

            // Apply equal-split classname for 30/30/30 selection
            if ("33/33/33".equals(selectedValue)) {
                addClassName("equal-split");
            }
        });

        RadioButtonGroup<String> overlayMode = new RadioButtonGroup<>("Overlay mode");
        overlayMode.setItems("Drawer", "Stack");
        overlayMode.setValue("Drawer");
        overlayMode.addClassName(LumoUtility.Padding.Horizontal.MEDIUM);

        overlayMode.addValueChangeListener(event -> {
            String selectedValue = event.getValue();
            setOverlayMode(MasterDetailLayout.OverlayMode.DRAWER);
            nestedMasterDetailLayout.setOverlayMode(MasterDetailLayout.OverlayMode.DRAWER);

            if ("Stack".equals(selectedValue)) {
                setOverlayMode(MasterDetailLayout.OverlayMode.STACK);
                nestedMasterDetailLayout.setOverlayMode(MasterDetailLayout.OverlayMode.STACK);
            }
        });

        // Create master container and set it
        VerticalLayout masterLayout = new VerticalLayout(new HorizontalLayout(splitRatio, overlayMode), grid);
        masterLayout.setSizeFull();
        masterLayout.setPadding(false);
        masterLayout.setSpacing(false);

        setMaster(masterLayout);
    }

    /**
     * Sets up the selection listener to show/hide detail area
     */
    private void setupSelectionListener() {
        grid.asSingleSelect().addValueChangeListener(event -> {
            Person selectedPerson = event.getValue();
            if (selectedPerson != null) {
                showDetailSection(selectedPerson);
            } else {
                hideDetailSection();
            }
        });

        // Add backdrop click listener to hide detail area
        addBackdropClickListener(event -> {
            grid.deselectAll();
        });

        // Add escape key listener to hide detail area
        addDetailEscapePressListener(event -> {
            grid.deselectAll();
        });
    }

    /**
     * Shows the detail section with nested Master Detail Layout for the selected person
     */
    private void showDetailSection(Person selectedPerson) {
        if (nestedMasterDetailLayout == null) {
            createNestedMasterDetailLayout();
        }

        // Update the form fields with selected person data
        populatePersonForm(selectedPerson);

        setDetail(nestedMasterDetailLayout);
    }

    /**
     * Hides the detail section
     */
    private void hideDetailSection() {
        setDetail(null);
    }

    /**
     * Creates the nested Master Detail Layout structure
     */
    private void createNestedMasterDetailLayout() {
        nestedMasterDetailLayout = new MasterDetailLayout();
        nestedMasterDetailLayout.setSizeFull();
        nestedMasterDetailLayout.setDetailMinSize("320px");

        // Create the person form as master of nested layout
        createPersonForm();
        nestedMasterDetailLayout.setMaster(personFormContent);

        // Set up event listeners for nested layout
        nestedMasterDetailLayout.addBackdropClickListener(e -> hideNestedDetail());
        nestedMasterDetailLayout.addDetailEscapePressListener(e -> hideNestedDetail());
    }

    /**
     * Creates the person form content for the nested master section
     */
    private void createPersonForm() {
        // Create header with close button
        H2 formTitle = new H2("Person Details");
        formTitle.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.Margin.NONE);
        formTitle.getStyle().set("font-weight", "600");

        // Close button for nested master (closes entire nested MDL)
        Button closeFormButton = new Button(VaadinIcon.CLOSE.create());
        closeFormButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
        closeFormButton.setAriaLabel("Close person details");
        closeFormButton.addClickListener(e -> {
            // Clear selection to hide entire nested detail
            grid.deselectAll();
        });

        HorizontalLayout formHeader = new HorizontalLayout(formTitle, closeFormButton);
        formHeader.setWidthFull();
        formHeader.setJustifyContentMode(HorizontalLayout.JustifyContentMode.BETWEEN);
        formHeader.setAlignItems(HorizontalLayout.Alignment.CENTER);
        formHeader.addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM);

        // Create form fields
        firstNameField = new TextField("First Name");
        firstNameField.setReadOnly(true);
        firstNameField.setWidthFull();

        lastNameField = new TextField("Last Name");
        lastNameField.setReadOnly(true);
        lastNameField.setWidthFull();

        textValueField = new TextField("Text Value");
        textValueField.setReadOnly(true);
        textValueField.setWidthFull();

        numericValueField = new IntegerField("Numeric Value");
        numericValueField.setReadOnly(true);
        numericValueField.setWidthFull();

        // Show Messages button to trigger nested detail
        Button showMessagesButton = new Button("Show Messages");
        showMessagesButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        showMessagesButton.addClickListener(e -> showNestedDetail());

        // Form layout
        VerticalLayout formFields = new VerticalLayout(
                firstNameField, lastNameField, textValueField, numericValueField, showMessagesButton
        );
        formFields.setPadding(true);
        formFields.setSpacing(true);

        personFormContent = new VerticalLayout(formHeader, formFields);
        personFormContent.setSizeFull();
        personFormContent.setPadding(false);
        personFormContent.setSpacing(false);
        personFormContent.expand(formFields);
    }

    /**
     * Creates the message detail content for the nested detail section
     */
    private void createMessageDetailContent() {
        // Create header with close button
        H2 messageTitle = new H2("Messages");
        messageTitle.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.Margin.NONE);
        messageTitle.getStyle().set("font-weight", "600");

        // Close button for nested detail (closes only message list)
        Button closeMessageButton = new Button(VaadinIcon.CLOSE.create());
        closeMessageButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
        closeMessageButton.setAriaLabel("Close messages");
        closeMessageButton.addClickListener(e -> hideNestedDetail());

        HorizontalLayout messageHeader = new HorizontalLayout(messageTitle, closeMessageButton);
        messageHeader.setWidthFull();
        messageHeader.setJustifyContentMode(HorizontalLayout.JustifyContentMode.BETWEEN);
        messageHeader.setAlignItems(HorizontalLayout.Alignment.CENTER);
        messageHeader.addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM);

        // Create MessageList component
        messageList = new MessageList();

        messageDetailContent = new VerticalLayout(messageHeader, messageList);
        messageDetailContent.setSizeFull();
        messageDetailContent.setPadding(false);
        messageDetailContent.setSpacing(false);
        messageDetailContent.expand(messageList);

        // Apply styling to match Figma design background
        messageDetailContent.getElement().getStyle()
                .set("background-color", "var(--lumo-contrast-5pct)");
    }

    /**
     * Shows the nested detail (message list)
     */
    private void showNestedDetail() {
        if (messageDetailContent == null) {
            createMessageDetailContent();
        }
        updateMessageList();
        nestedMasterDetailLayout.setDetail(messageDetailContent);
    }

    /**
     * Hides the nested detail (message list)
     */
    private void hideNestedDetail() {
        if (nestedMasterDetailLayout != null) {
            nestedMasterDetailLayout.setDetail(null);
        }
    }

    /**
     * Populates the person form with selected person data
     */
    private void populatePersonForm(Person selectedPerson) {
        if (selectedPerson != null) {
            firstNameField.setValue(selectedPerson.getFirstName());
            lastNameField.setValue(selectedPerson.getLastName());
            textValueField.setValue(selectedPerson.getTextValue());
            numericValueField.setValue(selectedPerson.getNumericValue().intValue());
        }
    }

    /**
     * Updates the MessageList with current messages
     */
    private void updateMessageList() {
        if (messageList != null) {
            List<Message> messages = dataService.getAllMessages();
            List<MessageListItem> messageItems = messages.stream()
                    .map(msg -> {
                        MessageListItem item = new MessageListItem(
                                msg.getContent(),
                                msg.getTimestamp().toInstant(ZoneOffset.UTC),
                                msg.getSenderName()
                        );
                        if (msg.getAvatarUrl() != null) {
                            item.setUserImage(msg.getAvatarUrl());
                        }
                        item.setUserColorIndex(msg.getUserColorIndex());
                        return item;
                    })
                    .collect(Collectors.toList());

            messageList.setItems(messageItems);
        }
    }

}
