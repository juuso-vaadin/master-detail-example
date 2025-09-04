# Figma to Vaadin Implementation Guidelines

## Overview
This document provides comprehensive guidelines for accurately translating Figma designs to well-structured Vaadin code, emphasizing proper component usage and semantic correctness over implementation speed.

## Primary Analysis Order

### 1. ALWAYS Start with `get_code`
- Contains the most detailed component information
- Provides `data-name` attributes with component hints
- Shows theme variants and component structure
- Reveals implementation-specific metadata

### 2. Use `get_metadata` for Structure
- Understand component hierarchy
- Analyze node IDs and relationships
- Identify layout patterns and nesting

### 3. `get_image` for Final Verification Only
- **Never** use as primary analysis tool
- Only for visual confirmation after implementation
- Avoid visual interpretation without metadata analysis

## Typography and Heading Level Guidelines

### Text Style Analysis:
Analyze Figma text properties to determine appropriate HTML heading levels:

### Figma Text Style → Vaadin Component Mapping:
```java
// Large titles (28px, Semi Bold, weight: 600)
"Heading 2: Font(size: 28, weight: 600)" → H1 component
H1 mainTitle = new H1("Roles");

// Section titles (18px, Semi Bold, weight: 600) 
"Heading 4: Font(size: 18, weight: 600)" → H2 component
H2 sectionTitle = new H2("Product owner");

// Subsection titles (16px, Semi Bold, weight: 600)
"Heading 5: Font(size: 16, weight: 600)" → H3 component
H3 subsectionTitle = new H3("Assigned roles");

// Body text (16px, Regular, weight: 400)
"Normal body text: Font(size: 16, weight: 400)" → Paragraph or Span
Paragraph description = new Paragraph("Which roles should this person be assigned to?");

// Field labels (14px, Medium, weight: 500)
"Field label: Font(size: 14, weight: 500)" → Native field labels or Span
datePicker.setLabel("Start"); // Use component's built-in label
```

### Context-Based Heading Levels:
- **H1**: Page/view main title (typically largest text on the page)
- **H2**: Major section headings within the view
- **H3**: Subsection headings, toolbar titles
- **H4-H6**: Further subdivisions as needed

### Implementation Notes:
- Consider semantic hierarchy, not just visual appearance
- Maintain consistent heading progression (don't skip levels)
- Use component labels for form fields when available
- Apply Lumo text color tokens for proper theming

## Lumo Color Mapping Guidelines

### Color Analysis Workflow:
1. **Extract Colors**: Use `get_variable_defs` to get Figma color definitions
2. **Match Lumo Tokens**: Map colors to appropriate Lumo design tokens
3. **Use CSS Custom Properties**: Prefer Lumo CSS variables over hardcoded values
4. **Reference Documentation**: https://vaadin.com/docs/latest/styling/lumo/lumo-style-properties/color

### Common Figma → Lumo Color Mappings:
```java
// Text Colors
"#192434" → "var(--lumo-header-text-color)" or LumoUtility.TextColor.HEADER
"#192739f0" → "var(--lumo-body-text-color)" or LumoUtility.TextColor.BODY
"#005fdb" → "var(--lumo-primary-text-color)" or LumoUtility.TextColor.PRIMARY

// Background Colors
"#193b670d" → "var(--lumo-contrast-5pct)" or LumoUtility.Background.CONTRAST_5
"#1a38601a" → "var(--lumo-contrast-10pct)" or LumoUtility.Background.CONTRAST_10
"#006af5" → "var(--lumo-primary-color)" or LumoUtility.Background.PRIMARY

// Brand Colors
"rgba(25,59,103,0.05)" → "var(--lumo-primary-color-10pct)"
```

### Implementation Examples:
```java
// Using CSS Custom Properties
component.getStyle().set("color", "var(--lumo-primary-text-color)");
component.getStyle().set("background-color", "var(--lumo-contrast-5pct)");

// Using Lumo Utility Classes (preferred when available)
title.addClassNames(LumoUtility.TextColor.HEADER);
card.addClassNames(LumoUtility.Background.CONTRAST_5);
```

## Component Selection Priority

### 1. Use ONLY Vaadin Components
```java
// ✅ CORRECT: Proper Vaadin component
Button closeButton = new Button(VaadinIcon.CLOSE.create());
closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);

// ❌ AVOID: Generic HTML for interactive elements
Div clickableDiv = new Div();
clickableDiv.add(VaadinIcon.CLOSE.create());
clickableDiv.addClickListener(...);
```

### 2. Search Vaadin Documentation First
- Always verify component exists in Vaadin before implementing
- Use `search_vaadin_docs` to find correct components
- Check available themes, variants, and APIs

### 3. When Uncertain About Component Choice
```java
// TODO: Verify if Button is the correct Vaadin component for this design
// Figma shows: "Button (tertiary, icon-only)" - should this be Button or IconButton?
```

**Always ask**: "Should this be a [ComponentA] or [ComponentB]? The Figma shows [description]"

## Metadata Analysis Requirements

Before writing ANY code, analyze:

### From `get_code` Output:
- Component names (e.g., "Button", "Grid", "MessageList")
- `data-name` attributes (e.g., `data-name="Button (tertiary, icon-only)"`)
- Theme and variant information
- CSS classes that hint at Vaadin themes
- Text styles and typography information (font size, weight, line height)

### From `get_metadata` Output:
- Node IDs and hierarchical structure
- Layer organization and nesting
- Component relationships

### From `get_variable_defs` Output:
- Color variable definitions and values
- Design token mappings
- Theme-specific color schemes

#### Typography Analysis:
Identify heading levels based on Figma text styles:
- Analyze font size, weight, and styling information
- Map to appropriate HTML heading levels (H1, H2, H3, etc.)
- Consider context and hierarchy within the design

### Color and Style Analysis:
When encountering color values:
1. Use `get_variable_defs` to retrieve Figma color definitions
2. Map colors to Lumo design tokens when possible
3. Reference: https://vaadin.com/docs/latest/styling/lumo/lumo-style-properties/color
4. Prefer Lumo CSS custom properties over hardcoded values

### Required Mapping:
```markdown
Figma Component Name → Vaadin Component
- "Button" → Button.class
- "Grid" → Grid.class
- "Message List" → MessageList.class
- "Avatar" → Avatar.class
```

## Quality Standards

### Accuracy Over Speed
- Take time to understand the design properly
- Read ALL metadata before implementing
- Verify component choice against documentation

### Semantic Correctness
- Use proper Vaadin components, not generic HTML
- Follow Vaadin component APIs and patterns
- Preserve component semantics and accessibility

### Follow Vaadin Patterns
```java
// ✅ Proper theme usage
button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

// ✅ Proper sizing
layout.setSizeFull();

// ✅ Proper accessibility
button.setAriaLabel("Close");
```

### Preserve Accessibility
- Include aria-labels for interactive elements
- Use proper semantic structure
- Follow Vaadin accessibility guidelines

## When to Ask for Clarification

Ask when:
- Multiple Vaadin components could fit the visual design
- Figma component name doesn't clearly map to a Vaadin component
- Uncertain about theme variants or styling approach
- Need clarification on interaction patterns or data binding
- The `data-name` attribute is ambiguous

## Required Implementation Workflow

### Step 1: Extract and Analyze
```markdown
1. Call `get_code` - Extract ALL component metadata
2. Read `data-name` attributes carefully
3. Note component hierarchy and structure
4. Identify theme/variant hints
5. Analyze text styles for proper heading levels
6. Call `get_variable_defs` for color and design token information
```

### Step 2: Research and Verify
```markdown
1. Search Vaadin documentation for mentioned components
2. Verify component exists and supports required features
3. Check available themes and variants
4. Review component API documentation
5. Map Figma colors to Lumo design tokens
```

### Step 3: Map and Plan
```markdown
1. Map Figma component names to exact Vaadin equivalents
2. Plan component hierarchy and relationships
3. Identify required themes, variants, and properties
4. Map colors to appropriate Lumo CSS custom properties
5. Determine correct heading levels based on text styles
6. Note any custom styling requirements
```

### Step 4: Implement with Verification
```markdown
1. Implement using proper Vaadin components
2. Apply correct themes and variants
3. Add proper accessibility attributes
4. Call `get_image` for final visual verification only
```

## Code Review Checklist

### ❌ Avoid These Patterns:
```java
// Generic HTML for interactive elements
Div button = new Div();
Span clickable = new Span();

// Ignoring available Vaadin themes
button.getElement().getStyle().set("background", "transparent");

// Missing accessibility
// No aria-label or semantic meaning
```

### ✅ Prefer These Patterns:
```java
// Proper Vaadin components
Button button = new Button();
Grid<Person> grid = new Grid<>(Person.class);

// Using Vaadin themes
button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

// Proper accessibility
button.setAriaLabel("Close dialog");

// Proper heading levels based on Figma text styles
H1 mainTitle = new H1("Roles");  // For 28px Semi Bold text
H2 sectionTitle = new H2("Product owner");  // For 18px Semi Bold text

// Using Lumo design tokens for colors
component.getStyle().set("color", "var(--lumo-primary-text-color)");
component.addClassNames(LumoUtility.TextColor.PRIMARY);
```

## Common Figma → Vaadin Mappings

### Interactive Elements:
- `"Button"` → `Button.class`
- `"Button (tertiary, icon-only)"` → `Button` + `ButtonVariant.LUMO_TERTIARY` + `ButtonVariant.LUMO_ICON`

### Layout Components:
- `"Grid"` → `Grid.class`
- `"Master-Detail Layout"` → `MasterDetailLayout.class` (with feature flag)

### Data Display:
- `"Message List"` → `MessageList.class`
- `"Avatar"` → `Avatar.class`

### Containers:
- Generic containers → `VerticalLayout`, `HorizontalLayout`, `Div` (non-interactive only)

## Error Prevention

### Critical Implementation Rules:
1. **NEVER** use generic HTML (Div, Span) for interactive elements
2. **ALWAYS** verify component choice against Vaadin documentation
3. **READ** the `data-name` attributes - they contain component type hints
4. **ASK** when uncertain rather than guess
5. **COMMENT** when making assumptions about component choice

### Quality Assurance:
- Every interactive element should be a proper Vaadin component
- All themes and variants should use Vaadin APIs
- Accessibility attributes should be included where needed
- Implementation should match the semantic intent, not just visual appearance

## Example Implementation Review

### Before (Incorrect):
```java
Div closeButton = new Div();
closeButton.add(VaadinIcon.CLOSE.create());
closeButton.getElement().getStyle().set("cursor", "pointer");
closeButton.addClickListener(e -> {});
```

### After (Correct):
```java
// Based on Figma data-name="Button (tertiary, icon-only)"
Button closeButton = new Button(VaadinIcon.CLOSE.create());
closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
closeButton.setAriaLabel("Close");
closeButton.addClickListener(e -> {});
```

## Summary

The key to accurate Figma-to-Vaadin translation is:
1. **Metadata First**: Always analyze `get_code` output before visual interpretation
2. **Component Verification**: Search Vaadin docs before implementing any UI element
3. **Semantic Accuracy**: Use proper Vaadin components that match the design intent
4. **Ask When Uncertain**: Better to clarify than implement incorrectly
5. **Quality Over Speed**: Accurate, maintainable code is more valuable than quick implementation
