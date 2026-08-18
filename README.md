# NODO

Add this library to your FTC `TeamCode` module. Do not fork the repo.

## Gradle

In `TeamCode/build.gradle`:

```gradle
repositories {
    mavenCentral()
}

dependencies {
    implementation 'com.nonodo:non-odo:1.0.0'
}
```

Then sync Gradle. Use `NODOChassis` and the `NODO*` commands from your OpModes.

## Privacy ping

The first time the library initializes on a Control Hub, it sends one anonymous ping (`library` and `version` only) to `nodo-usage-tracker` so we can see how many teams use NODO. It does not collect team number, robot ID, IP address, or other personal data. Later autonomous and teleop runs do not ping again.

To disable it, set this before constructing `NODOChassis` or calling Blocks `initializeDrive`:

```java
com.nonodo.UsageTracker.enabled = false;
```
