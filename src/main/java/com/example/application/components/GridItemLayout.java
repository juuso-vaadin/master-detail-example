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

    private Span topRightBadge;
    private Span rightInfo1;
    private Span rightInfo2;

    public GridItemLayout(Role role) {
        initStyles();
        initLeftLayout(role);
        initRightLayout(role);
    }

    private void initStyles() {
        addClassNames(Display.FLEX, Gap.SMALL, JustifyContent.BETWEEN);
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

        Div leftLayout = new Div(this.titleLayout, this.subtitle1, this.subtitle2);
        leftLayout.addClassNames(Display.FLEX, FlexDirection.COLUMN, Gap.XSMALL);
        add(leftLayout);
    }

    private void initRightLayout(Role role) {
        this.topRightBadge = new Span();
        if (role.isHeadOffice()) {
            this.topRightBadge.setText("Head office");
            this.topRightBadge.getElement().getThemeList().add("badge contrast");
            this.topRightBadge.setVisible(true);
        } else {
            this.topRightBadge.setVisible(false);
        }

        this.rightInfo1 = new Span(role.getUtilizationRate() != null ? role.getUtilizationRate() + "%" : "");
        this.rightInfo1.addClassNames(FontSize.XSMALL, TextColor.SECONDARY);

        this.rightInfo2 = new Span(getRoleStatus(role));
        this.rightInfo2.addClassNames(FontSize.XSMALL, TextColor.SECONDARY);

        Div rightLayout = new Div(this.topRightBadge, this.rightInfo1, this.rightInfo2);
        rightLayout.addClassNames(AlignItems.END, Display.FLEX, FlexDirection.COLUMN, Gap.XSMALL);
        add(rightLayout);
    }

    private String getRoleStatus(Role role) {
        if (role.getEndDate() == null) {
            return "Ongoing";
        }
        if (role.getEndDate().isAfter(java.time.LocalDate.now())) {
            return "Active";
        }
        return "Completed";
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

    public Span getTopRightBadge() {
        return this.topRightBadge;
    }

    public void setRightInfo1(String text) {
        this.rightInfo1.setText(text);
    }

    public void setRightInfo2(String text) {
        this.rightInfo2.setText(text);
    }

}
