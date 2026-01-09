package com.example.application.views.masterdetail;

import com.example.application.data.Message;
import com.example.application.data.Person;
import com.example.application.service.DataService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.masterdetaillayout.MasterDetailLayout;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation following Figma-to-Vaadin guidelines using proper MasterDetailLayout.
 * <p>
 * Analysis performed:
 * 1. get_code: Identified Grid, MessageList, and Button (tertiary, icon-only) components
 * 2. get_metadata: Understood layout structure and component hierarchy
 * 3. search_vaadin_docs: Verified proper Vaadin components and APIs including MasterDetailLayout
 * 4. get_image: Final visual verification
 */
@PageTitle("Master Detail View")
@Route("")
@Menu(icon = LineAwesomeIconUrl.FILE)
public class MasterDetailView extends MasterDetailLayout {

    private final DataService dataService;
    private Grid<Person> grid;
    private MessageList messageList;
    private VerticalLayout detailContent;

    public MasterDetailView(DataService dataService) {
        this.dataService = dataService;
        setSizeFull();
        setOverlayMode(MasterDetailLayout.OverlayMode.DRAWER);

        // Configure the MasterDetailLayout
        setMasterMinSize("450px");
        setDetailSize("640px");

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

        // Apply styling to match Figma design
        grid.addClassNames(LumoUtility.Background.BASE);

        // Create master container and set it
        VerticalLayout masterLayout = new VerticalLayout(grid);
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
     * Shows the detail section with MessageList for the selected person
     */
    private void showDetailSection(Person selectedPerson) {
        if (detailContent == null) {
            createDetailSection();
        }

        // Update the detail content based on selected person
        // For now, we'll show all messages, but this could be filtered by person
        updateMessageList();

        setDetail(detailContent);
    }

    /**
     * Hides the detail section
     */
    private void hideDetailSection() {
        setDetail(null);
    }

    /**
     * Creates the detail section with MessageList component
     * Based on Figma data-name="Message List" and "Button (tertiary, icon-only)"
     */
    private void createDetailSection() {
        // Create header with proper Vaadin components
        H2 title = new H2("Messages");
        title.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.Margin.NONE);
        title.getStyle().set("font-weight", "600");

        // Create Button component based on Figma data-name="Button (tertiary, icon-only)"
        Button closeButton = new Button(VaadinIcon.CLOSE.create());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
        closeButton.setAriaLabel("Close");
        closeButton.addClickListener(e -> {
            // Clear selection to hide detail
            grid.deselectAll();
        });

        HorizontalLayout header = new HorizontalLayout(title, closeButton);
        header.setWidthFull();
        header.setJustifyContentMode(HorizontalLayout.JustifyContentMode.BETWEEN);
        header.setAlignItems(HorizontalLayout.Alignment.CENTER);
        header.addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM);

        // Create MessageList component as identified in Figma metadata
        messageList = new MessageList();

        // Create detail container
        detailContent = new VerticalLayout(header, messageList);
        detailContent.setSizeFull();
        detailContent.setPadding(false);
        detailContent.setSpacing(false);
        detailContent.expand(messageList);

        // Apply styling to match Figma design background
        detailContent.getElement().getStyle()
                .set("background-color", "var(--lumo-contrast-5pct)");
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
