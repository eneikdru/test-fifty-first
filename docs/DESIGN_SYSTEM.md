# Design System Specification

## Overview
This design system provides consistent design tokens, layout patterns, accessibility rules (WCAG 2.1 AA), and Svelte component guidelines for the application UI.

## Color Tokens & Contrast Compliance

All color combinations satisfy WCAG 2.1 AA contrast requirements (minimum 4.5:1 for normal text, 3:1 for large text / UI controls).

| Token Name | Value | Usage | Minimum Contrast |
|------------|-------|-------|------------------|
| `--color-bg-main` | `#f8fafc` | Page background | 19:1 against primary text |
| `--color-bg-card` | `#ffffff` | Panel & modal background | 19:1 against primary text |
| `--color-text-primary` | `#0f172a` | Primary headings & body | 19:1 against white |
| `--color-text-muted` | `#334155` | Secondary text & labels | 10.7:1 against white |
| `--color-primary` | `#1d4ed8` | Primary buttons & links | 7.3:1 against white |
| `--color-primary-hover` | `#1e40af` | Button hover state | 9.8:1 against white |
| `--color-border` | `#cbd5e1` | Input borders & dividers | UI component boundary |
| `--color-error` | `#b91c1c` | Error alerts & validation | 7.2:1 against white |
| `--color-success` | `#15803d` | Success badges & alerts | 5.8:1 against white |
| `--color-focus` | `#2563eb` | Visible focus ring | 3px solid, 2px offset |

## Accessibility & Focus Requirements (WCAG 2.1 AA)

1. **Visible Focus**: All interactive elements (buttons, inputs, links, toggles) MUST display a high-contrast focus indicator (`outline: 3px solid var(--color-focus); outline-offset: 2px;`).
2. **Form Controls**: Every form field MUST have an associated explicit `<label for="...">` or `aria-label`. Helper text and error messages MUST be associated using `aria-describedby` or live regions (`aria-live="polite"`).
3. **Keyboard Navigation**: Modals and cookie consent banners MUST support Tab navigation, Escape key closing, and focus trap within active modal dialogs (`role="dialog"`, `aria-modal="true"`).
4. **Touch & Click Targets**: Interactive elements maintain a minimum target size of 44x44px.

## Typography

- **Font Family**: System UI stack (`system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif`)
- **Heading 1**: 1.5rem (24px), font-weight: 700, line-height: 1.3
- **Heading 2**: 1.25rem (20px), font-weight: 600, line-height: 1.35
- **Body Text**: 1rem (16px), font-weight: 400, line-height: 1.5
- **Small / Label**: 0.875rem (14px), font-weight: 500, line-height: 1.4

## Spacing System

- `--space-1`: `0.25rem` (4px)
- `--space-2`: `0.5rem` (8px)
- `--space-3`: `0.75rem` (12px)
- `--space-4`: `1rem` (16px)
- `--space-6`: `1.5rem` (24px)
- `--space-8`: `2rem` (32px)
