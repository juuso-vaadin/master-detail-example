package com.example.application.components;

import com.example.application.data.Role;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.theme.lumo.LumoUtility.*;

public class GridItemLayout extends Div {

    private Div titleLayout;
    private Span title;
    private Span titleBadge;
    private Span subtitle1;
    private Span subtitle2;

    public GridItemLayout(Role role) {
        initStyles();
        initLeftLayout(role);
    }

    private void initStyles() {
        addClassNames(Display.FLEX, FlexDirection.COLUMN, Gap.XSMALL);
    }

    private void initLeftLayout(Role role) {
        this.title = new Span(role.getName());
        this.title.addClassNames(FontSize.SMALL, FontWeight.MEDIUM);

        this.titleBadge = new Span();
        if (role.isTeamLead()) {
            this.titleBadge.setText("Team lead");
            this.titleBadge.getElement().getThemeList().add("badge");
            this.titleBadge.setVisible(true);
        } else {
            this.titleBadge.setVisible(false);
        }

        this.titleLayout = new Div(this.title, this.titleBadge);
        this.titleLayout.addClassNames(AlignItems.CENTER, Display.FLEX, Gap.SMALL);

        this.subtitle1 = new Span(role.getDateRange());
        this.subtitle1.addClassNames(FontSize.XSMALL, TextColor.SECONDARY);

        this.subtitle2 = new Span(role.getReason() != null ? role.getReason() : "");
        this.subtitle2.addClassNames(FontSize.XSMALL, TextColor.SECONDARY);

        add(this.titleLayout, this.subtitle1, this.subtitle2);
    }

    public void setTitle(String text) {
        this.title.setText(text);
    }

    public Span getTitleBadge(String text) {
        return this.titleBadge;
    }

    public Div getTitleLayout() {
        return this.titleLayout;
    }

    public void setSubtitle1(String text) {
        this.subtitle1.setText(text);
    }

    public void setSubtitle2(String text) {
        this.subtitle2.setText(text);
    }

}
