package com.example.application.views.empty;

import com.example.application.data.Person;
import com.example.application.data.Message;
import com.example.application.service.DataService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
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

@PageTitle("Master Detail Example")
@Route("")
@Menu(order = 0, icon = LineAwesomeIconUrl.FILE)
public class EmptyView extends HorizontalLayout {

    private final DataService dataService;
    private Grid<Person> grid;
    private MessageList messageList;

    public EmptyView(DataService dataService) {
        this.dataService = dataService;
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        
        createGridSection();
        createMessageSection();
    }

    private void createGridSection() {
        // Create the data grid
        grid = new Grid<>(Person.class, false);
        grid.addColumn(Person::getFirstName).setHeader("Text").setFlexGrow(1);
        grid.addColumn(Person::getLastName).setHeader("Text").setFlexGrow(1);
        grid.addColumn(person -> String.format("%,d", person.getNumericValue()))
            .setHeader("Numeric")
            .setFlexGrow(1)
            .getElement().getStyle().set("text-align", "right");
        grid.addColumn(Person::getTextValue).setHeader("Text").setFlexGrow(1);
        
        grid.setItems(dataService.getAllPeople());
        grid.setSizeFull();
        
        // Style the grid to match Figma design
        grid.addClassNames(LumoUtility.Background.BASE);
        grid.getElement().getStyle()
            .set("border-right", "1px solid var(--lumo-contrast-10pct)");

        VerticalLayout gridLayout = new VerticalLayout(grid);
        gridLayout.setSizeFull();
        gridLayout.setPadding(false);
        gridLayout.setSpacing(false);
        gridLayout.setWidth("800px");
        
        add(gridLayout);
    }

    private void createMessageSection() {
        // Create header with title and close button
        H2 title = new H2("Messages");
        title.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.Margin.NONE);
        title.getStyle().set("font-weight", "600");

        Button closeButton = new Button(VaadinIcon.CLOSE.create());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
        closeButton.setAriaLabel("Close");
        closeButton.addClickListener(e -> {
            // Close button functionality could be implemented here
        });

        HorizontalLayout header = new HorizontalLayout(title, closeButton);
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.setAlignItems(Alignment.CENTER);
        header.addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM);

        // Create message list
        messageList = new MessageList();
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

        VerticalLayout messageLayout = new VerticalLayout(header, messageList);
        messageLayout.setSizeFull();
        messageLayout.setPadding(false);
        messageLayout.setSpacing(false);
        messageLayout.setWidth("640px");
        messageLayout.expand(messageList);
        
        // Style the message section to match Figma design
        messageLayout.getElement().getStyle()
            .set("background-color", "var(--lumo-contrast-5pct)");

        add(messageLayout);
    }
}
