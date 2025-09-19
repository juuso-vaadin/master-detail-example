# Figma to Vaadin Implementation Guidelines

## Overview
This document provides comprehensive guidelines for accurately translating Figma designs to well-structured Vaadin Flow code, emphasizing proper component usage and semantic correctness over implementation speed. Accurate, maintainable code is more valuable than quick implementation. The generated UI should utilize services and data classes that exists in the project or mock new if needed. Generated UI is not expected to be functional. Avoid creating logic and focus on accuracy of generating the user interface design provided via Figma MCP.


## Required Implementation Workflow
Create TODOs based on these steps.

### Step 1. ALWAYS Start with `get_code`
- Contains the most detailed component information
- Check `data-name` attribute to get the type of the component
- Review component description for identification of correct Vaadin component
- Identify theme/variant hints
- Text styles and typography information (font size, weight, line height)

### Step 2. Use `get_metadata` for Structure and identification of components
- Review component `name` attribute for identification of correct Vaadin component
- Plan component hierarchy and relationships
- Analyze node IDs and relationships
- Identify layout patterns and nesting

### Step 3: Component Research (MANDATORY - No Implementation Without This)
**For EACH component identified in Steps 1-2:**

#### 3.1 Component Discovery
- Use `search_vaadin_docs` to find relevant components
- Record `file_path` for each component found

#### 3.2 Complete Documentation Review (MANDATORY)
**For each component, call `get_full_document` with the file_path:** - REQUIRED before implementation
- TextField → `get_full_document("components/text-field/index-flow.md")`
- DatePicker → `get_full_document("components/date-picker/index-flow.md")`
- Button → `get_full_document("components/button/index-flow.md")`
- etc.

#### 3.3 Examine Java class directly
- Use `semantic_search` to find the component Java file (e.g., "Button.java", "TextField.java")
- Use `read_file` to examine the actual Java class implementation
- Look for available constructors, methods, and theme variants
- Check for interfaces like `HasLabel`, `HasValue`, `HasSize` to understand capabilities
- Identify all available `addThemeVariants()` options
- Review setter methods for proper configuration approaches

#### 3.4 Implementation Planning
- Document available theme variants
- Note component-specific features
- Identify any limitations or gaps
- Plan component configuration approach

❌ NEVER implement without completing full documentation review
❌ WORKFLOW VIOLATIONS = REJECTION
⚠️ Search results are previews only - not sufficient for implementation

### Step 4: Implement user interface
- Implement using proper Vaadin components and custom elements already available in the project, not generic HTML
- Apply correct themes and variants
- Ignore visual styling of elements
- Use Lumo Utilities to configure layouts, paddings, borders, background colors etc.
- Ensure semantic correctness
- Determine correct heading levels based on text styles
- Accessibility attributes should be included where needed

### Step 5: Don't run tests
- Do not run any commands in terminal
- Do not open browser or take screenshots


## Lumo Theme Mapping Guidelines

### Examples of Figma → Lumo Color Mappings:
```java
// Colors
"Semantic colors/Primary" → "var(--lumo-primary-color)" or LumoUtility.Background.PRIMARY
"Semantic colors/Primary, Text" → "var(--lumo-primary-text-color)" or LumoUtility.TextColor.PRIMARY
"Header Text" → "var(--lumo-header-text-color)" or LumoUtility.TextColor.HEADER
"Body Text" → "var(--lumo-body-text-color)" or LumoUtility.TextColor.BODY

// Typography
"Typography/Font-family" → "var(--lumo-font-family)"
"Typography/Font-size-m" → "var(--lumo-font-size-m)"
```

### Implementation Examples:
```java
// Using Lumo Utility Classes (preferred when available)
title.addClassNames(LumoUtility.TextColor.HEADER);
card.addClassNames(LumoUtility.Background.CONTRAST_5);
```

### Alternative 1 using CSS styles
- Add custom classname to element
```java
span.addClassName("secondary-text");
```

- Target the classname in CSS. Use styles.css unless there is component or view specific stylesheets that are more appropriate.
- Whenever possible use existing CSS custom properties instead of defining new values.
```css
.secondary-text {
    color: var(--lumo-secondary-text-color);
    font-size: var(--lumo-font-size-s)
}
```

### Alternative 2 using LumoUtilities
```java
// Proper way to configure component styles
span.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL);

// ❌ INCORRECT way to configure component styles
span.getStyle().set("color", "rgba(27,43,65,0.69)").set("font-size", "15px").set("line-height", "1.34");
```


## Quality Standards

### Accuracy Over Speed
- Take time to understand the design properly
- Read ALL metadata available through Figma MCP before implementing
- Verify component choice against documentation

### Semantic Correctness
- Ensure semantic correctness
- Use proper Vaadin components, not generic HTML
- Follow Vaadin component APIs and patterns
- Preserve component semantics and accessibility

### Code style quidelines
- Avoid creating tiny wrapper methods that only delegate to another method without adding logic
- Inline the call or generalize into one reusable method with parameters.

### Follow Vaadin Patterns
```java
// Proper way to configure components is to use component API's when available
textField.setReadOnly(true);

// ❌ INCORRECT ways to configure components is to use getComponent()
textField.getElement().setAttribute("readonly", "true");
button.getElement().getStyle().set("background", "transparent");

// Proper way to set component theme variants
button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

// Proper way to set styles using Lumo Utility classes
layout.addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM);

// Proper ways to set sizing
layout.setSizeFull();
layout.setWidth("600px");
layout.setHeight("50%");

// ❌ INCORRECT way to set sizing
layout.getStyle().set("width", "600px");

// Proper way to set space around layout, always use padding
layout.addClassName(LumoUtility.Padding.Bottom.MEDIUM);

// ❌ INCORRECT way to set space around layout, never use margin
layout.getStyle().set("margin-bottom", "36px");

// Proper way to set component size is to first use available size variants
avatar.addThemeVariants(AvatarVariant.LUMO_LARGE);

// ❌ INCORRECT way to set component size
avatar.getStyle().set("--vaadin-avatar-size", "48px");

// Proper accessibility
iconButton.setAriaLabel("Close");

// Proper way to set input field label if component implements HasLabel
input.setLabel("Label");

// ❌ INCORRECT way to set input field label
Span label = new Span("Label");
VerticalLayout.add(label, input);

// Proper way to set border
layout.addClassNames("LumoUtility.Border.TOP", "LumoUtility.BorderColor.CONTRAST_10");

// ❌ INCORRECT way to set border
layout.getStyle().set("border-top", "1px solid var(--lumo-contrast-10pct)");
```

### When to Ask for Clarification

Ask when:
- Multiple Vaadin components could fit the visual design
- Figma component name doesn't clearly map to a Vaadin component
- Uncertain about theme variants or styling approach
- Need clarification on interaction patterns or data binding

**Always ask**: "Should this be a [ComponentA] or [ComponentB]? The Figma shows [description]"


## Examples of common Figma → Vaadin Mappings

### Vaadin Components:
- `Button` → `Button.class`
- `Button (tertiary, icon-only)` → `Button` + `ButtonVariant.LUMO_TERTIARY` + `ButtonVariant.LUMO_ICON`
- `Text Field` → `TextField.class`
- `Grid` → `Grid.class`
- `Message List` → `MessageList.class`
- `Avatar` → `Avatar.class`
- `Card` → `Card.class` (since v24.8)
- etc.

### Vaadin Layouts:
- Vertical auto layout → `VerticalLayout`
- Vertical auto layout with wrapping → `VerticalLayout` + `setWrap(true)`
- Horizontal auto layout → `HorizontalLayout`
- Layout → `FlexLayout` + `addClassNames(LumoUtility.FlexDirection.ROW, LumoUtility.AlignItems.BASELINE)`
- Master-Detail Layout → `MasterDetailLayout.class` (with feature flag)
- etc.

### Generic HTML elements
- `Text layer` -> `com.vaadin.flow.component.html.Span`
- `Heading 3` -> `com.vaadin.flow.component.html.H3;`
- etc.
