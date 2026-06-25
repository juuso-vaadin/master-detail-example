package com.example.application.views.rolemanagement;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.masterdetaillayout.MasterDetailLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.*;

import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Minimal Example")
@Route("minimal-example")
@Menu(icon = LineAwesomeIconUrl.BUG_SOLID)
public class MinimalExample extends Div {

    private MasterDetailLayout masterDetailLayout;

    public MinimalExample() {
        setHeightFull();
        createMasterDetailLayout();
    }

    private void createMasterDetailLayout() {
        masterDetailLayout = new MasterDetailLayout();
        masterDetailLayout.addBackdropClickListener(e -> hideDetail());
        masterDetailLayout.addDetailEscapePressListener(e -> hideDetail());
        masterDetailLayout.setOverlayContainment(MasterDetailLayout.OverlayContainment.PAGE);
        masterDetailLayout.setMasterSize("560px");
        masterDetailLayout.setExpandDetail(true);
        
        add(masterDetailLayout);
        
        createMasterSection();
    }

    private void createMasterSection() {
        Div masterLayout = new Div();
        
        Button showDetailButton = new Button("Show Detail");
        showDetailButton.addClickListener(e -> showDetail());
        
        masterLayout.add(showDetailButton);
        masterDetailLayout.setMaster(masterLayout);
    }

    private void showDetail() {
        Div detailContent = createDetailContent();
        masterDetailLayout.setDetail(detailContent);
    }

    private Div createDetailContent() {
        Div content = new Div();
        
        H2 title = new H2("Detail Grid");
        title.addClassNames(FontSize.LARGE, Margin.Bottom.MEDIUM);
        
        Grid<String> grid = createDataGrid();
        
        content.add(title, grid);
        return content;
    }

    private Grid<String> createDataGrid() {
        Grid<String> grid = new Grid<>();
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addClassNames(Width.FULL);
        grid.setAllRowsVisible(true);

        grid.addColumn(item -> item)
                .setHeader("Column 1")
                .setAutoWidth(true);

        grid.addColumn(item -> "Long value for the " + item)
                .setHeader("Column 2")
                .setAutoWidth(true);

        grid.addColumn(item -> "Even longer value here in the " + item)
                .setHeader("Column 3")
                .setAutoWidth(true);

        grid.addColumn(item -> "How long can these values be? You'll find out on the " + item)
                .setHeader("Column 4")
                .setAutoWidth(true);

        grid.setItems("Row 1", "Row 2", "Row 3", "Row 4", "Row 5");

        return grid;
    }

    private void hideDetail() {
        masterDetailLayout.setDetail(null);
    }
}
