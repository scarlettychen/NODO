---
title: Language toggle authoring
layout: default
nav_order: 99
nav_exclude: true
---

# Language toggle authoring

Each `.language-toggle` group gets a switcher **automatically** at the top of that section. Buttons are always labeled **Java** / **Blocks** (from each panel's `data-language`). Choosing either one updates **every** toggle on the page, and the preference is stored in `localStorage` (`nodo-docs-lang`) so other docs pages stay in sync.

## Author pattern

Wrap alternate instructions in a `.language-toggle` group. Set `data-language` on each panel to the label you want on the button (for example `Java/OnBot Java` or `Blocks`). Use `markdown="1"` so Kramdown still processes nested Markdown/code fences.

```html
<div class="language-toggle" data-language-group="kF-example" markdown="1">

<div data-language="Java/OnBot Java" markdown="1">

Construct the drive with your `kF`:

```java
NODODrive drive = new NODODrive(
    hardwareMap,
    NODODriveType.MECANUM,
    0.03
);
```

</div>

<div data-language="Blocks" markdown="1">

Call **initializeMecanumDrive** (or **initializeTankDrive**) once in init. Default `kF` is `0.03`.

</div>

</div>
```

## Rules

- Do **not** use `<details>` or other collapsibles for language switching.
- Only one language panel is visible at a time (CSS + JS).
- Do **not** add manual toggle buttons — `assets/js/language-toggle.js` builds them from `data-language`.
- Shared prose outside `.language-toggle` is always visible.
- Labels containing `block` (case-insensitive) map to the Blocks preference; everything else maps to Java.
