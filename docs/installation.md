---
title: Installation
layout: default
nav_order: 2
---

# Installing NODO
#### Pick Java or Blocks/OnBot Java

<div class="language-toggle" data-language-group="some-unique-id" markdown="1">

<div data-language="Java/OnBot Java" markdown="1">

## Android Studio Installation Guide

NODO is distributed as an AAR Android library via Gradle. Follow these two quick steps to add it to your FTC project.

## 1. Add the Repository
Open your FTC Android Studio project and find the **root** 'build.gradle' file (the one for the whole project, not the TeamCode one).

Scroll down to the 'repositories' block inside 'allprojects' and add JitPack;

```java
allprojects {
    repositories {
        mavenCentral()
        google() 
        
        // Add this!
        maven { url 'https://jitpack.io' }
    }
}
```

## 2. Edit build.gradle
In your Android Studio project, navigate to 'build.gradle' (Module :TeamCode) in Grade Scripts.

Scroll down to the 'dependencies' block, and add this: 'implementation 'com.nonodo:non-odo:1.0.0''

You should now have something that looks like this: 

```java
dependencies {
    implementation project(':FtcRobotController')
    implementation 'org.ftclib.ftclib:core:2.1.1'
    
    // NODO implementation!
    implementation 'com.nonodo:non-odo:1.0.0'
}

```

## 2. Sync Gradle
After modify the file, a banner will pop up at the top of Android Studio prompting you to Sync Now. Click it to download the library. You are now good to go!

</div>

<div data-language="Blocks/OnBot Java" markdown="1">

## Blocks/OnBotJava Installation Guide

## 1. Go to the NODO Github Release Page

Linked [here](https://github.com/scarlettychen/NODO).

## 2. Under releases, download nodo-v1.0.0.jar.
add pics

## 3. Open the Robot Controller's OnBot Java.
add pics
## 4. Click **Upload**
add pics
## 5. Upload the NODO `.jar`
add pics
## 6. Click**Build Everything**
add pics

</div>

### You are now ready to start!

</div>

<a href="quickstart.html">
  <button>Click me</button>
</a>












