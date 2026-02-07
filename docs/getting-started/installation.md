# Installation

## Maven Central

Pix is available on Maven Central Repository.

### Using Gradle (Recommended)

Add to your **app-level** `build.gradle.kts`:

```kotlin
dependencies {
    implementation("io.ak1.pix:piximagepicker:1.6.8")
}
```

Make sure your project has `mavenCentral()` in repositories:

```kotlin
repositories {
    mavenCentral()
}
```

### Using Gradle (Groovy)

For projects using Groovy syntax:

```groovy
dependencies {
    implementation 'io.ak1.pix:piximagepicker:1.6.8'
}
```

### Using Maven

```xml
<dependency>
  <groupId>io.ak1.pix</groupId>
  <artifactId>piximagepicker</artifactId>
  <version>1.6.8</version>
  <type>pom</type>
</dependency>
```

### Using Ivy

```xml
<dependency org='io.ak1.pix' name='piximagepicker' rev='1.6.8'>
  <artifact name='pix' ext='pom'></artifact>
</dependency>
```

## Requirements

- **Minimum SDK**: API 16 (Android 4.1)
- **Target SDK**: API 34+ (Recommended)
- **Kotlin**: 1.9.0+
- **AndroidX**: Latest version

## Permissions

Pix requires the following permissions in your `AndroidManifest.xml`:

```xml
<!-- Required for camera functionality -->
<uses-permission android:name="android.permission.CAMERA" />

<!-- Required for storage access (API 32+) -->
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
<uses-permission android:name="android.permission.READ_MEDIA_VIDEO" />

<!-- For older Android versions -->
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```

## ProGuard/R8 Configuration

No special ProGuard configuration is needed. Pix is designed to work seamlessly with R8 and ProGuard.

## Next Steps

After installation, check out the [Quick Start Guide](quick-start.md) to integrate Pix into your app.