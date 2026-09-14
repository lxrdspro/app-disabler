# App Disabler

A lightweight Android application that uses Shizuku to manage installed applications across Android users/profiles without requiring root access.

## Features

- ✅ Detect Shizuku installation and permission status
- ✅ Discover and manage multiple Android users/profiles (Owner, Guest, Work Profile, Secure Folder, etc.)
- ✅ List packages per user with automatic access validation
- ✅ Search packages with case-insensitive filtering
- ✅ Filter by app type (All apps, Third-party, System)
- ✅ Bulk enable/disable packages with progress tracking
- ✅ Safety protection for critical packages
- ✅ Graceful error handling for inaccessible users
- ✅ Background thread processing for smooth UI

## Target Device

- **Device:** Samsung Galaxy S20 SM-G981V
- **OS:** Android 13
- **UI:** One UI 5.1
- **Shizuku:** Version 13.x
- **Root:** Not required

## Requirements

- **JDK:** Java 17+
- **Android SDK:** API 35 (Android 15)
- **Gradle:** 8.7
- **AGP:** 8.6.1

### Before Running

1. **Install Shizuku** on your device from https://github.com/RikkaApps/Shizuku
2. **Start Shizuku** service (requires ADB access initially)
3. **Grant permission** when prompted by App Disabler

## Build Instructions

### 1. Clone Repository

```bash
git clone https://github.com/lxrdspro/app-disabler.git
cd app-disabler
```

### 2. Build Debug APK

```bash
./gradlew clean assembleDebug
```

The debug APK will be located at:
```
app/build/outputs/apk/debug/app-debug.apk
```

### 3. Install on Device

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 4. Run Application

```bash
adb shell am start -n com.example.appdisabler/.MainActivity
```

## Build Release APK

```bash
./gradlew assembleRelease
```

**Note:** Release builds require signing configuration. Create `keystore.properties` in the project root with your signing credentials.

## Project Structure

```
app-disabler/
├── .github/
│   └── workflows/
│       └── android-build.yml
├── app/
│   ├── build.gradle
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/
│           │   └── com/example/appdisabler/
│           │       └── MainActivity.java
│           └── res/
│               ├── layout/
│               │   ├── activity_main.xml
│               │   └── package_item.xml
│               └── values/
│                   ├── strings.xml
│                   └── styles.xml
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── build.gradle
├── settings.gradle
├── gradle.properties
├── gradlew
├── gradlew.bat
└── README.md
```

## Dependencies

- **Shizuku API:** 13.1.5
- **Shizuku Provider:** 13.1.5
- **AndroidX AppCompat:** 1.6.1
- **Material Design:** 1.9.0

## Usage

### 1. Detect Shizuku Status

The app automatically checks:
- Is Shizuku installed?
- Is Shizuku service running?
- Does this app have Shizuku permission?

### 2. Select User/Profile

Use the dropdown to switch between discovered users:
- User 0 — Owner
- User 10 — Guest
- User 12 — Work Profile
- User 95 — Dual Apps
- User 150 — Secure Folder

### 3. Search & Filter

- **Search:** Type package names (case-insensitive)
- **Filter:** Select app type (All, Third-party, System)

### 4. Manage Packages

- **Select All:** Check all visible packages
- **Clear:** Uncheck all selections
- **Enable Selected:** Re-enable disabled packages
- **Disable Selected:** Disable selected packages

### 5. Monitor Progress

Real-time progress displays:
```
Processing 8 of 23...
```

Final result:
```
Completed — Disabled: 21 | Failed: 2
```

## Protected Packages

The following packages **cannot** be disabled via bulk operations:

- `com.example.appdisabler` (this app)
- `moe.shizuku.privileged.api` (Shizuku)

Attempting to disable these will show: *"Cannot disable protected package"*

## Error Handling

### Shizuku Not Installed

```
Shizuku: Not installed
```

**Solution:** Install from https://github.com/RikkaApps/Shizuku

### Shizuku Not Running

```
Shizuku: Not running
```

**Solution:** Start Shizuku service via ADB or the Shizuku app

### Permission Denied

```
Shizuku: Permission denied - Request permission
```

**Solution:** Grant permission when prompted, or restart the app

### User Inaccessible

```
User 10 is inaccessible: Shell does not have permission to access this user.
```

**Reason:** Android/Samsung Knox restricts access to certain profiles (e.g., Secure Folder)

**Note:** This is expected behavior and does not indicate an error.

## Shizuku Commands Used

The app executes these Shizuku commands:

```bash
# Discover users
cmd user list

# List packages for a specific user
pm list packages --user USER_ID

# Disable package for a user
pm disable-user --user USER_ID PACKAGE_NAME

# Enable package for a user
pm enable --user USER_ID PACKAGE_NAME
```

## Limitations

1. **No Root Required:** App uses only Shizuku API (requires device owner or ADB access initially)
2. **Samsung Knox Restrictions:** Some profiles (e.g., Secure Folder) may be inaccessible
3. **System Packages:** Disabling critical system packages may cause device instability
4. **One UI Specific:** Tested on Samsung One UI; behavior on stock Android may vary
5. **No Uninstall:** Only disables packages, does not uninstall

## Troubleshooting

### App Crashes

**Log output:**
```bash
adb logcat | grep appdisabler
```

### Build Fails

**Check JDK version:**
```bash
java -version
```

Must be Java 17+.

**Check Gradle version:**
```bash
./gradlew --version
```

Must be 8.7.

### APK Installation Fails

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Use `-r` flag to replace existing installation.

## Development

### Modify Package Protection

Edit `MainActivity.java`:
```java
private static final String PROTECTED_PACKAGE = "com.example.appdisabler";
private static final String PROTECTED_SHIZUKU = "moe.shizuku.privileged.api";
```

### Change Target Device

Edit `app/build.gradle`:
```gradle
compileSdk = 35
targetSdk = 35
minSdk = 23
```

### Customize UI

Edit layouts:
- `app/src/main/res/layout/activity_main.xml`
- `app/src/main/res/layout/package_item.xml`

## GitHub Actions

The project includes a CI/CD workflow (`.github/workflows/android-build.yml`) that:

1. Runs on every push to `main`
2. Sets up JDK 17
3. Installs Android SDK
4. Builds debug APK
5. Uploads APK as artifact

To enable the workflow:

1. Ensure `.github/workflows/android-build.yml` exists
2. GitHub Actions will run automatically on push

## License

This project is provided as-is for educational and personal use.

## Support

- **Shizuku Documentation:** https://github.com/RikkaApps/Shizuku
- **Android Developer:** https://developer.android.com
- **Issues:** Report via GitHub Issues

---

**Version:** 1.1  
**Updated:** 2026-09-14  
**Target Android:** 13-15
