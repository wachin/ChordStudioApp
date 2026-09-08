# ChordStudioApp

ChordStudioApp is an Android app for opening plain text chord sheets and transposing the chords by semitones while keeping the song text readable.

The app is built with Kotlin, Jetpack Compose, and Gradle in Android Studio.

## Features

- Open a plain text song or chord file from the device
- Detect chord lines automatically
- Transpose chords up or down by semitones
- Choose whether enharmonic notes prefer sharps or flats
- Adjust the display font and use custom fonts

---

## Quick Start (experienced developers)

```bash
# 1) Get the code
git clone <repository-url>
cd ChordStudioApp

# 2) Point Gradle at your Android SDK
cp local.properties.example local.properties
#    then edit local.properties:
#    sdk.dir=/home/<your-user>/Android/Sdk

# 3) Build and test (first run downloads Gradle + dependencies)
./gradlew assembleDebug
./gradlew testDebugUnitTest

# APK output:
# app/build/outputs/apk/debug/app-debug.apk
```

If you are new to Android development on Linux, follow the full step-by-step guide below.

---

## Requirements (Linux, deb-based distros)

This guide targets **Debian, Ubuntu, MX Linux and any other Debian-based distribution**
(they all use the `.deb` package system and the `apt` commands shown here).

- A Debian-based Linux distribution (MX Linux, Ubuntu, Debian, Linux Mint, etc.)
- Internet connection (the first build downloads Gradle and dependencies)
- At least 8 GB of free disk space
- **8 GB of RAM is enough to build the app.** The Android emulator is another story:
  it needs a powerful computer (16 GB RAM recommended). With 8 GB of RAM,
  we recommend testing on a **real Android phone** instead (see
  [Run on a real device](#6-run-on-a-real-android-device-recommended)).
- JDK 17 or newer (JDK 21 is what this project uses and verifies with)
- Android SDK with **Android API 36** platform (the project's `compileSdk`), matching
  Build-Tools, and Platform-Tools (`adb`)

### Debian packages you may need (`apt`)

One-line installs (copy and paste):

Without the emulator (enough to build the app and test on a real phone):

```bash
sudo apt install git openjdk-21-jdk usbutils curl wget unzip
```

With the emulator (adds the KVM virtualization stack; only for capable computers):

```bash
sudo apt install git openjdk-21-jdk usbutils curl wget unzip cpu-checker qemu-kvm libvirt-daemon-system libvirt-clients bridge-utils
```

> - If `openjdk-21-jdk` is not available in your distro version, `openjdk-17-jdk`
>   also works (JDK 17 or newer is required).

Details:

| Package | Needed for | When |
|---|---|---|
| `git` | Cloning the repository, committing and pushing changes | **Required** |
| `openjdk-21-jdk` | Java 21 (alternative to Android Studio's bundled JDK) | Only if you don't use Android Studio's bundled JDK |
| `usbutils` | Provides `lsusb`, to check the phone is detected at USB level when `adb devices` shows nothing | Recommended (for USB troubleshooting) |
| `cpu-checker` | The `kvm-ok` diagnostic command | Only if you plan to use the emulator (optional) |
| `qemu-kvm`, `libvirt-daemon-system`, `libvirt-clients`, `bridge-utils` | KVM virtualization stack for the emulator | Only if you plan to use the emulator (optional) |
| `curl`, `wget`, `unzip` | Downloading/extracting files from the terminal (e.g. the Android Studio tarball) | Optional convenience |

Notes for people coming from other tools:

- You do **not** need the Debian packages `adb` or `fastboot`: the `adb` you use comes
  from the SDK's **Platform-Tools** (`~/Android/Sdk/platform-tools`), which is kept in
  sync with the rest of the SDK. Mixing a system `adb` with the SDK one can cause
  version mismatches.
- If you ever installed `apktool` (an APK reverse-engineering tool), Debian pulled in
  several packages named like `android-framework-res`, `aapt`, `android-lib*` and
  `libsmali-java`. Those belong to apktool and have **nothing to do** with developing
  this app — they are not dependencies and you don't need to install anything from them.
- Gradle itself is **not** installed as a package: the project's Gradle Wrapper
  (`./gradlew`) downloads and manages its own Gradle.
- `udev` (USB device management) is already installed by default on any
  Debian-based desktop; no action needed for the phone to appear in `adb devices`
  beyond enabling USB debugging on the phone.

---

## 1. Install Android Studio

1. Download the Linux version (`.tar.gz`) from the official page:
   👉 <https://developer.android.com/studio>

2. Open a terminal in your Downloads folder and extract it:

   ```bash
   cd ~/Downloads
   tar -xvzf android-studio-*.tar.gz
   ```

   This creates a folder named `android-studio`. (You can also right-click the
   archive and choose "Extract Here".)

3. Move it to `/opt` (a common system location for this kind of software):

   ```bash
   sudo mv android-studio /opt/
   ```

4. Give your user ownership of the folder so the IDE can update itself
   (otherwise Android Studio will complain it cannot write to `/opt/android-studio`):

   ```bash
   sudo chown -R $USER:$USER /opt/android-studio
   ```

   Verify with `ls -ld /opt/android-studio` — it should show your username as owner.

5. Launch Android Studio:

   ```bash
   /opt/android-studio/bin/studio
   ```

6. On first launch:

   - Select **Standard**
   - Accept all licenses
   - Wait while it downloads the Android SDK and its tools

### KDE KWallet / keyring prompt (KDE-based systems)

On KDE-based systems (for example MX Linux with KDE), Android Studio may ask to
create a **KDE wallet** (`kdewallet`) to store credentials (Google account,
passwords, tokens...). **This is normal and not an error.**

- Choose **Classic, encrypted file with Blowfish** (the simple option, recommended
  for most users) and create a password.
- The **GPG encryption** option is only for advanced users who already use GPG.
- If you press **Cancel**, the IDE will still start, but it will ask you to log in
  again more often and won't store credentials securely.
- You can change or delete the wallet later from the KWallet management tools.

---

## 2. Install the JDK (Java)

Gradle 9.5 (used by this project) requires **Java 17 or newer**; **JDK 21** works
perfectly. You have two options:

### Option A (recommended): use the JDK bundled with Android Studio

Android Studio ships its own JDK (JetBrains Runtime 21) at:

```text
/opt/android-studio/jbr
```

Nothing to install.

### Option B: install OpenJDK 21 from the system repositories

```bash
sudo apt update
sudo apt install openjdk-21-jdk
```

### Verify

```bash
java -version
```

Expected output (version 21.x):

```text
openjdk version "21.0.x" ...
OpenJDK Runtime Environment ...
```

> **Note:** If you build from a terminal and `java` is not found, point
> `JAVA_HOME` at Android Studio's bundled JDK:
>
> ```bash
> export JAVA_HOME=/opt/android-studio/jbr
> export PATH=$JAVA_HOME/bin:$PATH
> ```

---

## 3. Android SDK components (platform, build-tools, platform-tools)

The SDK is downloaded when you complete Android Studio's **Standard** setup, but the
project needs specific components.

### Install them from the SDK Manager

In Android Studio go to **Settings → Languages & Frameworks → Android SDK** and make
sure these are installed:

| Component | Why |
|---|---|
| **Android SDK Platform 36** | Required by the project's `compileSdk = 36` |
| **Build-Tools 36.0.0** | Build tools matching AGP |
| **Android SDK Platform-Tools** | Provides the `adb` command (needed to test on a phone) |
| **Android SDK Platform 34** (optional) | Only if you later lower `compileSdk`/`targetSdk` |

Tick **"Accept License"** for each package (or accept licenses when prompted).

> **Tip:** Once the licenses are accepted, the Android Gradle Plugin will
> **automatically download** any missing platform or build-tools the next time you
> build. Accepting the licenses is the only essential part.

### Verify from the terminal

```bash
ls ~/Android/Sdk/platforms
#   android-34  android-36.1

ls ~/Android/Sdk/build-tools
#   36.0.0

adb version
#   Android Debug Bridge version 1.0.x ...
```

---

## 4. Configure `local.properties` (SDK path)

This file tells Gradle where your Android SDK is. It contains a
**machine-specific path**, so it is **ignored by Git** (see `.gitignore`).

```bash
cd ChordStudioApp
cp local.properties.example local.properties
```

Then edit `local.properties` so it points to your SDK:

```text
sdk.dir=/home/<your-user>/Android/Sdk
```

Verify:

```bash
cat local.properties
#   sdk.dir=/home/<your-user>/Android/Sdk
```

### 4.1 Optional: environment variables (`ANDROID_HOME` and `PATH`)

Besides (or instead of) `local.properties`, you can tell your whole system where the
SDK is by adding this to the end of `~/.bashrc`:

```bash
export ANDROID_HOME="$HOME/Android/Sdk"
export PATH="$PATH:$ANDROID_HOME/platform-tools"
export PATH="$PATH:$ANDROID_HOME/cmdline-tools/latest/bin"
```

What each line does:

- `ANDROID_HOME` — the standard variable that many Android tools read to find the SDK
  (if `local.properties` exists, it takes precedence)
- `platform-tools` in `PATH` — lets you run `adb` from any terminal
- `cmdline-tools/latest/bin` — lets you run `sdkmanager` and `avdmanager` from any
  terminal. It only takes effect after installing **Android SDK Command-line Tools
  (latest)** in the SDK Manager (**Settings → Languages & Frameworks → Android SDK →
  SDK Tools tab**); if it is not installed, the line is simply ignored until then.

Apply the changes and verify:

```bash
source ~/.bashrc

echo $ANDROID_HOME   # /home/<your-user>/Android/Sdk
adb version          # works from any folder
```

---

## 5. Build the app

The repository includes the **Gradle Wrapper**, so **you don't need to install
Gradle yourself**. The first build downloads Gradle 9.5 and all dependencies —
this takes a few minutes and needs internet.

```bash
cd ChordStudioApp
./gradlew assembleDebug
```

On success you'll see:

```text
BUILD SUCCESSFUL
```

The debug APK is written to:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Run the local unit tests with:

```bash
./gradlew testDebugUnitTest
```

You can also build from Android Studio with **Build → Assemble Project**.

---

## 6. Run on a real Android device (recommended)

Testing on a real phone is the recommended way — it needs no powerful computer
and no emulator.

### 6.1 Prepare the phone (only once)

1. On the phone go to **Settings → About phone**
2. Tap **"Build number"** **7 times** until you see *"You are now a developer!"*
3. Go to **Settings → Developer options**
4. Enable **USB debugging**

### 6.2 Connect and verify

1. Connect the phone to the computer with a USB cable
2. On the phone accept the *"Allow USB debugging?"* prompt (tick
   "Always allow from this computer")
3. Check the computer sees it:

   ```bash
   adb devices
   ```

   Expected output (your device with `device` status):

   ```text
   List of devices attached
   R58N1234567    device
   ```

   > If it shows `unauthorized`, accept the prompt on the phone and run
   > `adb devices` again. If nothing appears, enable USB debugging, try another
   > cable/USB port, and confirm the phone shows up at USB level with
   > `lsusb` (from the `usbutils` package).

### 6.3 Install the app

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

The **ChordStudioApp** icon will appear on the phone. Alternatively, press
**Run ▶** in Android Studio and choose your phone as the target device.

> **Tip:** The app opens plain-text chord sheets from the phone (the "Open"
> button), so to try it, download or create a `.txt` file with lyrics and chords
> on the phone.

---

## 7. Emulator (optional — only on a capable computer)

> **Do you actually need the emulator? Probably not.** The emulator runs a whole
> virtual phone on your computer and consumes a lot of RAM and CPU. With **8 GB of
> RAM** it is not recommended — the app will be slow and the system may run out of
> memory. Testing on a **real device** (section 6) is the better choice.
>
> Follow this section only if you have a capable computer (16 GB RAM or more
> recommended).

### 7.1 Enable KVM (hardware acceleration)

1. Check that your CPU supports virtualization:

   ```bash
   egrep -c '(vmx|svm)' /proc/cpuinfo
   ```

   A number greater than 0 means it is compatible.

2. Install the virtualization packages:

   ```bash
   sudo apt update
   sudo apt install cpu-checker qemu-kvm libvirt-daemon-system libvirt-clients bridge-utils
   ```

3. Verify KVM is available:

   ```bash
   kvm-ok
   ```

   Expected output:

   ```text
   KVM acceleration can be used
   ```

4. Add your user to the `kvm` and `libvirt` groups:

   ```bash
   sudo usermod -aG kvm $USER
   sudo usermod -aG libvirt $USER
   ```

   👉 **Reboot the system** after this.

5. Verify the groups and the KVM device:

   ```bash
   groups
   #   should include: kvm libvirt

   ls -l /dev/kvm
   ```

### 7.2 Create a virtual device (AVD)

1. Open Android Studio
2. Go to **Device Manager**
3. Click **Create device** (for example, a Pixel)
4. Choose a system image (it downloads automatically; pick a lightweight one such
   as API 34/35)
5. Start the emulator

Thanks to KVM, the emulator will be fast — if your computer can handle it.

---

## 8. Desktop shortcut (optional)

To avoid opening Android Studio from the terminal every time, create a launcher:

```bash
gedit ~/.local/share/applications/android-studio.desktop
```

Content:

```ini
[Desktop Entry]
Version=1.0
Type=Application
Name=Android Studio
Exec=/opt/android-studio/bin/studio
Icon=/opt/android-studio/bin/studio.png
Categories=Development;IDE;
Terminal=false
```

---

## ✅ Verification checklist

After following all the steps, this is what a working environment looks like:

| Component | How to verify | Expected state |
|---|---|---|
| **JDK 21** (system and Android Studio's) | `java -version` | `openjdk version "21.0.x"` |
| **SDK platform android-34 and android-36.1** | `ls ~/Android/Sdk/platforms` | `android-34` and `android-36.1` present |
| **Build-tools 36.0.0** | `ls ~/Android/Sdk/build-tools` | `36.0.0` present |
| **Platform-tools (adb)** | `adb version` | `Android Debug Bridge version 1.0.x` |
| **Licenses accepted** | `ls ~/Android/Sdk/licenses` | `android-sdk-license` (and/or others) present |
| **local.properties** | `cat local.properties` | `sdk.dir=/home/<your-user>/Android/Sdk` |
| **ANDROID_HOME** (optional) | `echo $ANDROID_HOME` | `/home/<your-user>/Android/Sdk` |
| **KVM** (emulator only) | `ls /dev/kvm` and `groups` | `/dev/kvm` exists and `groups` includes `kvm` |
| **The project builds** | `./gradlew assembleDebug` | `BUILD SUCCESSFUL` |
| **Tests pass** | `./gradlew testDebugUnitTest` | `BUILD SUCCESSFUL` |
| **APK generated** | `ls app/build/outputs/apk/debug/` | `app-debug.apk` present |

---

## Troubleshooting

| Problem | Solution |
|---|---|
| Build fails with "SDK location not found" | Create `local.properties` with `sdk.dir=/home/<your-user>/Android/Sdk` (section 4) |
| Build fails asking to accept licenses | Accept the licenses in the SDK Manager (section 3) or run `sdkmanager --licenses` |
| `adb devices` shows `unauthorized` | Accept the USB debugging prompt on the phone and run `adb devices` again |
| `adb devices` shows nothing | Enable USB debugging, try another cable/USB port; verify the phone is detected with `lsusb` |
| Build is slow / out of memory | The project already sets `org.gradle.jvmargs=-Xmx2048m` in `gradle.properties`; close other programs while building |
| Emulator is extremely slow | Your computer likely lacks RAM/CPU for the emulator; use a real device (section 6) |

---

## Project Structure

Important locations:

- `app/` : Android application module
- `app/src/main/java/com/wachin/chordstudio/` : Kotlin source code
- `app/src/main/res/` : Android resources
- `design/` : editable design assets such as the SVG app icon
- `docs/` : project documentation

## Development Handoff

The current UI work is implemented in
[`MainActivity.kt`](./app/src/main/java/com/wachin/chordstudio/MainActivity.kt).
It includes separate reading/editing modes, focus and keyboard control,
independent content scrolling, and a compact controls bar.

The next planned work is documented in
[`ROADMAP.md`](./ROADMAP.md). Before making changes, check the working tree and
run the unit tests. If the build fails with an SDK-location error, configure
`local.properties` as described in section 4; that file is intentionally ignored
by Git.

## Icon Notes

Launcher icon documentation is here:

- [docs/launcher-icon-guide.md](./docs/launcher-icon-guide.md)

This guide explains:

- how the launcher icon is structured
- which files Android actually uses
- how the icon was tuned after testing on a real phone
- where to make future icon adjustments