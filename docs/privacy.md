---
title: Telemetry & Privacy
layout: default
nav_order: 7
nav_exclude: true
---

# Telemetry & Privacy

When you construct `NODOChassis` / `NODOTankDrive` (or call Blocks drive init) on a Control Hub, NODO may send **one anonymous usage ping** per install.

The ping includes only:

- library name
- library version

It does **not** include team number, robot name, OpMode names, or match data.

## Opt out

Set this **before** creating the drive:

```java
com.nonodo.UsageTracker.enabled = false;
```

Then construct `NODODrive` / `NODOChassis` / `NODOTankDrive` as usual.
