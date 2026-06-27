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
- The Android SDK configured by Android Studio
- Internet access the first time Gradle downloads dependencies

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

## Build The APK

After Gradle sync completes:

1. In Android Studio, open the menu `Build`.
2. Click `Assemble Project`.

Android Studio will build the debug APK.

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

## Icon Notes

Launcher icon documentation is here:

- [docs/launcher-icon-guide.md](/home/wachin/AndroidStudioProjects/ChordStudioApp/docs/launcher-icon-guide.md)

This guide explains:

- how the launcher icon is structured
- which files Android actually uses
- how the icon was tuned after testing on a real phone
- where to make future icon adjustments
