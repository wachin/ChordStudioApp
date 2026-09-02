# ChordStudioApp

ChordStudioApp is an Android app for opening plain text chord sheets and transposing the chords by semitones while keeping the song text readable.

The app is built with Kotlin, Jetpack Compose, and Gradle in Android Studio.

## Features

- Open a plain text song or chord file from the device
- Detect chord lines automatically
- Transpose chords up or down by semitones
- Choose whether enharmonic notes prefer sharps or flats
- Adjust the display font and use custom fonts

## Requirements

To build this project, you need:

- Android Studio
- JDK 17 (Android Studio's bundled JDK is recommended)
- Android SDK with Android API 34 installed
- Android SDK Build-Tools installed through Android Studio
- Internet access the first time Gradle downloads dependencies

The repository includes the Gradle Wrapper, so no system Gradle installation is
required. Do not commit `local.properties`: it contains a machine-specific SDK
path. Use [`local.properties.example`](./local.properties.example) as a template
when a local SDK path must be configured manually.

## Get The Project

You can get the project in any of these ways:

- clone the repository
- download the repository as a ZIP file
- fork the repository and then clone your fork

Example clone command:

```bash
git clone <repository-url>
```

## Open In Android Studio

1. Open Android Studio.
2. Choose `Open`.
3. Select the `ChordStudioApp` project folder.
4. Wait for Gradle sync to finish.

If Android Studio asks to trust the project or configure the SDK, accept those steps first.
In `Settings > Languages & Frameworks > Android SDK`, install Android API 34
and the matching SDK Build-Tools if they are not already available.

If Android Studio cannot find the SDK, either configure its SDK location in the
IDE or create `local.properties` in the project root:

```text
sdk.dir=/absolute/path/to/Android/Sdk
```

For example, a typical Linux installation uses
`/home/<user>/Android/Sdk`; Windows and macOS use different paths.

## Build The APK

After Gradle sync completes:

1. In Android Studio, open the menu `Build`.
2. Click `Assemble Project`.

Android Studio will build the debug APK.

The same build can be run from a terminal:

```bash
./gradlew assembleDebug
```

On Windows, use `gradlew.bat assembleDebug`.

Run the local unit tests with:

```bash
./gradlew testDebugUnitTest
```

## APK Output Location

The generated debug APK will be created at:

```text
AndroidStudioProjects/ChordStudioApp/app/build/outputs/apk/debug/
```

Typical file:

```text
app-debug.apk
```

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
`local.properties` as described above; that file is intentionally ignored by
Git.

## Icon Notes

Launcher icon documentation is here:

- [docs/launcher-icon-guide.md](/home/wachin/AndroidStudioProjects/ChordStudioApp/docs/launcher-icon-guide.md)

This guide explains:

- how the launcher icon is structured
- which files Android actually uses
- how the icon was tuned after testing on a real phone
- where to make future icon adjustments
