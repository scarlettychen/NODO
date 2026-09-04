---
title: Installation
layout: default
nav_order: 2
---

# Installing NODO
#### Pick Java or Blocks/OnBot Java

<div class="language-toggle" data-language-group="install-main" markdown="1">

<div data-language="Java/OnBot Java" markdown="1">

## Android Studio Installation Guide

NODO is distributed as an Android library via Gradle (JitPack). Follow these steps to add it to your FTC project.

## 1. Add the Repository

Open your FTC Android Studio project and find the **root** `build.gradle` file (the whole project, not TeamCode).

In the `allprojects { repositories { ... } }` block, add JitPack:

```gradle
allprojects {
    repositories {
        mavenCentral()
        google()

        maven { url 'https://jitpack.io' }
    }
}
```

## 2. Add the dependency

Open **TeamCode** `build.gradle` and add:

```gradle
implementation 'com.github.scarlettychen:NODO:v1.0.0-beta.1'
```

Example `dependencies` block:

```gradle
dependencies {
    implementation project(':FtcRobotController')
    implementation 'org.ftclib.ftclib:core:2.1.1'

    implementation 'com.github.scarlettychen:NODO:v1.0.0-beta.1'
}
```

## 3. Sync Gradle

Click **Sync Now** when Android Studio prompts you. NODO classes under `com.nonodo` will resolve against your existing FTC SDK.

</div>

<div data-language="Blocks/OnBot Java" markdown="1">

## Blocks / OnBot Java Installation Guide

## 1. Download the JAR

Go to the [NODO GitHub Releases](https://github.com/scarlettychen/NODO/releases/latest) page and download **`nodo-1.0.0-beta.1.jar`** (attached to the release).

## 2. Open OnBot Java

On the Robot Controller, open **OnBot Java** (Manage from the driver station, or use the RC web interface).

## 3. Upload the library

1. Click **Upload**
2. Select **`nodo-1.0.0-beta.1.jar`** (~43 KB thin JAR — only `com.nonodo` classes)
3. Click **Build Everything**

Do **not** upload an AAR from JitPack, a fat/shadow JAR, or the FTC SDK. Use the release `.jar` only.

![OnBot Java upload screen]({{ '/images/OnBotJava.png' | relative_url }})

When the build succeeds, NODO blocks appear under **NODO Init** and **NODO Run** in FTC Blocks.

### If upload says classes already exist in FtcRobotController

OnBot rejects a library when **any** class in the JAR can already be loaded from the installed Robot Controller app. That is **not** “SDK classes inside the JAR” — our release JAR is thin and only contains `com.nonodo.*`.

Usual cause: this robot already has NODO baked in from **Android Studio** (`implementation 'com.github.scarlettychen:NODO:...'` in TeamCode). Pick **one** install path per robot:

| Path | What to do |
|------|------------|
| **Blocks / OnBot only** | Remove the Gradle NODO dependency, rebuild/reinstall the RC app (or use a stock REV/FTC RC), then upload the thin JAR |
| **Android Studio only** | Keep the Gradle dependency; do **not** also upload the JAR on OnBot |

Also delete any older `nodo*.jar` under **ExternalLibraries** on the RC before re-uploading.

</div>

</div>

### You are now ready to start!

<a href="{{ '/quickstart/' | relative_url }}" class="btn btn-primary">Go to Quickstart</a>
