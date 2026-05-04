package com.example.application.views.masterdetail;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.masterdetaillayout.MasterDetailLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.*;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.time.LocalDate;
import java.util.List;

@PageTitle("Four-Level Example")
@Route("four-level-example")
@Menu(icon = LineAwesomeIconUrl.LAYER_GROUP_SOLID)
public class FourLevelExample extends Div {

    // --------------- Data model ---------------

    record Task(int id, String name, String description, String status, LocalDate date) {}

    record Project(int id, String name, String description, String status, LocalDate date, List<Task> tasks) {}

    private static final List<Project> PROJECTS = List.of(
        new Project(1, "Website Relaunch", "Redesign the public-facing website", "In Progress", LocalDate.of(2026, 6, 1), List.of(
            new Task(1, "Wireframes", "Create initial wireframes for all pages", "Done", LocalDate.of(2026, 3, 10)),
            new Task(2, "Frontend build", "Implement designs in React", "In Progress", LocalDate.of(2026, 5, 15)),
            new Task(3, "SEO audit", "Review and fix SEO issues on new pages", "Open", LocalDate.of(2026, 5, 30))
        )),
        new Project(2, "Mobile App v2", "Second major release of the mobile application", "Planning", LocalDate.of(2026, 9, 1), List.of(
            new Task(4, "Requirements workshop", "Gather requirements from stakeholders", "Done", LocalDate.of(2026, 2, 20)),
            new Task(5, "Architecture design", "Define new app architecture", "In Progress", LocalDate.of(2026, 4, 30)),
            new Task(6, "Push notifications", "Implement push notification service", "Open", LocalDate.of(2026, 7, 15))
        )),
        new Project(3, "Data Migration", "Migrate legacy database to new platform", "On Hold", LocalDate.of(2026, 12, 1), List.of(
            new Task(7, "Schema mapping", "Map old schema to new target schema", "Done", LocalDate.of(2026, 1, 15)),
            new Task(8, "ETL scripts", "Write ETL scripts for all entity types", "Open", LocalDate.of(2026, 10, 1)),
            new Task(9, "Validation suite", "Build automated data validation tests", "Open", LocalDate.of(2026, 11, 1))
        ))
    );

    // --------------- Layout components ---------------

    private final MasterDetailLayout level1Mdl = new MasterDetailLayout();
    private final MasterDetailLayout level2Mdl = new MasterDetailLayout();
    private final MasterDetailLayout level3Mdl = new MasterDetailLayout();

    // Level 2
    private final H3 level2Title = new H3();
    private final Grid<Task> taskGrid = new Grid<>(Task.class, false);

    // Level 3 (read-only form)
    private final H3 level3Title = new H3();
    private final TextField roName = new TextField("Name");
    private final TextField roDescription = new TextField("Description");
    private final TextField roStatus = new TextField("Status");
    private final DatePicker roDate = new DatePicker("Date");

    // Level 4 (edit form)
    private final TextField editName = new TextField("Name");
    private final TextField editDescription = new TextField("Description");
    private final TextField editStatus = new TextField("Status");
    private final DatePicker editDate = new DatePicker("Date");

    // --------------- Constructor ---------------

    public FourLevelExample() {
        setHeightFull();

        configureMdls();
        buildLevel1();
        buildLevel2();
        buildLevel3();

        add(level1Mdl);
    }

    // --------------- MDL configuration ---------------

    private void configureMdls() {
        level1Mdl.setSizeFull();
        level1Mdl.setContainment(MasterDetailLayout.Containment.VIEWPORT);
        level1Mdl.setForceOverlay(true);
        level1Mdl.setDetailMinSize("1000px");

        level2Mdl.setSizeFull();
        level2Mdl.setDetailMinSize("100%");

        level3Mdl.setSizeFull();
        level3Mdl.setDetailMinSize("90%");
    }

    // --------------- Level 1: Project grid ---------------

    private void buildLevel1() {
        Grid<Project> projectGrid = new Grid<>(Project.class, false);
        projectGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        
        projectGrid.setSizeFull();

        projectGrid.addColumn(Project::name).setHeader("Name").setFlexGrow(1);
        projectGrid.addColumn(Project::description).setHeader("Description").setFlexGrow(2);
        projectGrid.addColumn(Project::status).setHeader("Status").setAutoWidth(true);
        projectGrid.addColumn(Project::date).setHeader("Due Date").setAutoWidth(true);
        projectGrid.addComponentColumn(project -> {
            Button btn = new Button("View Tasks");
            btn.addThemeVariants(ButtonVariant.LUMO_SMALL);
            btn.addClickListener(e -> showLevel2(project));
            return btn;
        }).setAutoWidth(true).setFlexGrow(0);

        projectGrid.setItems(PROJECTS);

        VerticalLayout master = new VerticalLayout(header("Projects", null), projectGrid);
        master.setSizeFull();
        master.setPadding(false);
        master.setSpacing(false);

        level1Mdl.setMaster(master);
    }

    // --------------- Level 2: Task grid ---------------

    private void buildLevel2() {
        taskGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        taskGrid.addClassNames("row-hover");
        taskGrid.setSizeFull();

        taskGrid.addColumn(Task::name).setHeader("Name").setFlexGrow(1);
        taskGrid.addColumn(Task::description).setHeader("Description").setFlexGrow(2);
        taskGrid.addColumn(Task::status).setHeader("Status").setAutoWidth(true);
        taskGrid.addColumn(Task::date).setHeader("Due Date").setAutoWidth(true);

        taskGrid.asSingleSelect().addValueChangeListener(e -> {
            Task selected = e.getValue();
            if (selected != null) {
                showLevel3(selected);
            }
        });

        Button closeBtn = new Button("Close", click -> level1Mdl.setDetail(null));
        closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        VerticalLayout master = new VerticalLayout(header(level2Title, closeBtn), taskGrid);
        master.setSizeFull();
        master.setPadding(false);
        master.setSpacing(false);

        level2Mdl.setMaster(master);
    }

    // --------------- Level 3: Read-only task form ---------------

    private void buildLevel3() {
        roName.setReadOnly(true);
        roName.setWidthFull();
        roDescription.setReadOnly(true);
        roDescription.setWidthFull();
        roStatus.setReadOnly(true);
        roStatus.setWidthFull();
        roDate.setReadOnly(true);
        roDate.setWidthFull();

        Button editBtn = new Button("Edit");
        editBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button closeBtn = new Button("Close", click -> level2Mdl.setDetail(null));
        closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout footer = new HorizontalLayout(editBtn);
        footer.addClassNames(Padding.MEDIUM);

        VerticalLayout master = new VerticalLayout(
            header(level3Title, closeBtn),
            new VerticalLayout(roName, roDescription, roStatus, roDate),
            footer
        );
        master.setSizeFull();
        master.setPadding(false);
        master.setSpacing(false);

        editBtn.addClickListener(e -> showLevel4());

        level3Mdl.setMaster(master);
    }

    // --------------- Level 4: Editable task form ---------------

    private final VerticalLayout editPanelHolder = buildLevel4();

    private VerticalLayout buildLevel4() {
        editName.setWidthFull();
        editDescription.setWidthFull();
        editStatus.setWidthFull();
        editDate.setWidthFull();

        Button saveBtn = new Button("Save");
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveBtn.addClickListener(e -> {
            Notification.show("Task \"" + editName.getValue() + "\" saved.");
            level3Mdl.setDetail(null);
        });

        Button cancelBtn = new Button("Cancel");
        cancelBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelBtn.addClickListener(e -> level3Mdl.setDetail(null));

        Button closeBtn = new Button("Close", click -> level3Mdl.setDetail(null));
        closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout footer = new HorizontalLayout(saveBtn, cancelBtn);
        footer.addClassNames(Padding.MEDIUM, Gap.SMALL);

        VerticalLayout editPanel = new VerticalLayout(
            header("Edit Task", closeBtn),
            new VerticalLayout(editName, editDescription, editStatus, editDate),
            footer
        );
        editPanel.setSizeFull();
        editPanel.setPadding(false);
        editPanel.setSpacing(false);

        return editPanel;
    }

    // --------------- Show/hide helpers ---------------

    private void showLevel2(Project project) {
        level2Title.setText(project.name());
        taskGrid.setItems(project.tasks());
        taskGrid.deselectAll();
        level3Mdl.setDetail(null);
        level1Mdl.setDetail(level2Mdl);
    }

    private void showLevel3(Task task) {
        level3Title.setText(task.name());
        roName.setValue(task.name());
        roDescription.setValue(task.description());
        roStatus.setValue(task.status());
        roDate.setValue(task.date());
        level3Mdl.setDetail(null);
        level2Mdl.setDetail(level3Mdl);
    }

    private void showLevel4() {
        editName.setValue(roName.getValue());
        editDescription.setValue(roDescription.getValue());
        editStatus.setValue(roStatus.getValue());
        editDate.setValue(roDate.getValue());
        level3Mdl.setDetail(editPanelHolder);
    }

    // --------------- Header helpers ---------------

    private HorizontalLayout header(String title, Button actionButton) {
        return header(new H3(title), actionButton);
    }

    private HorizontalLayout header(H3 titleComponent, Button actionButton) {
        titleComponent.addClassNames(Margin.NONE, Flex.ONE);
        HorizontalLayout row = new HorizontalLayout(titleComponent);
        row.addClassNames(Padding.MEDIUM, AlignItems.CENTER, Width.FULL);
        if (actionButton != null) {
            row.add(actionButton);
        }
        return row;
    }
}
