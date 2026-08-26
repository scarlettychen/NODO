---
title: Language toggle authoring
layout: default
nav_order: 99
nav_exclude: true
---

# Language toggle authoring

Site-wide **Java** / **FTC Blocks** toggle lives in the header. Preference is stored in `localStorage` (`nodo-docs-lang`, default `java`) and applies on every docs page.

## Author pattern

Wrap alternate instructions in a `.language-toggle` group. Put Java in `data-language="java"` and Blocks in `data-language="blocks"`. Use `markdown="1"` so Kramdown still processes nested Markdown/code fences.

```html
<div class="language-toggle" data-language-group="kF-example" markdown="1">

<div data-language="java" markdown="1">

Construct the drive with your `kF`:

```java
NODODrive drive = new NODODrive(
    hardwareMap,
    NODODriveType.MECANUM,
    0.03
);
```

</div>

<div data-language="blocks" markdown="1">

Call **initializeMecanumDrive** (or **initializeTankDrive**) once in init. Default `kF` is `0.03`.

</div>

</div>
```

## Rules

- Do **not** use `<details>` or other collapsibles for language switching.
- Only one language panel is visible at a time (CSS + `html[data-docs-lang]`).
- Optional local buttons: `{% include language_toggle.html %}` inside a group (usually unnecessary; the header toggle is enough).
- Shared prose outside `.language-toggle` is always visible.
