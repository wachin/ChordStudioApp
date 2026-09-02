# Copilot instructions for ChordStudioApp

## Project overview

ChordStudioApp is a single-module Android application (`:app`) written in Kotlin with Jetpack Compose and Material 3. The app opens plain-text chord sheets, detects chord-heavy lines, transposes chord roots and slash-bass notes by semitones, and renders the result with configurable monospaced fonts.

The project targets Android SDK 34, supports API 24+, uses Java/Kotlin JVM target 11, and is built with the Gradle wrapper. The package and namespace are `com.wachin.chordstudio`.

## Build, test, and lint

Run commands from the repository root with `./gradlew`:

```bash
# Build the debug APK
./gradlew app:assembleDebug

# Run all local JVM unit tests
./gradlew app:testDebugUnitTest

# Run one local test class or method
./gradlew app:testDebugUnitTest --tests "com.wachin.chordstudio.ExampleUnitTest"
./gradlew app:testDebugUnitTest --tests "com.wachin.chordstudio.ExampleUnitTest.addition_isCorrect"

# Run Android instrumentation tests on a connected device or emulator
./gradlew app:connectedDebugAndroidTest

# Run lint
./gradlew app:lint
```

The debug APK is written under `app/build/outputs/apk/debug/`. Instrumentation tests use `AndroidJUnit4` and require a connected/emulated Android device.

## Architecture and important data flow

- `MainActivity` owns Android integration: edge-to-edge setup, Compose theme setup, the `OpenDocument` launcher for `text/plain`, file reading, and copying selected custom font files into the app's external `fonts` directory.
- `ChordStudioApp` in `MainActivity.kt` contains the primary Compose screen and its UI state. `originalText` is the source used for retransposition; `displayedText` is the rendered/editable text. Semitone and enharmonic-preference changes recompute `displayedText` from `originalText`.
- `ChordStudio` is the pure transposition engine. `transposeText` processes lines, only changes lines identified as chord lines, and leaves other lyrics unchanged. `transposeChord` handles chord quality/extensions and slash chords. Preserve this separation when changing UI behavior so parsing/transposition remains independently testable.
- `FontSettingsDialog` provides font selection, custom-font management, size, bold, italic, preview, and apply/cancel behavior. `FontPreferencesManager` persists the applied settings in a Preferences DataStore named `font_settings`; changes should continue to flow through `FontSettings`.
- Android resources hold the manifest/theme/string configuration and launcher assets. The editable launcher artwork is in `design/chordstudio-app-icon.svg`; installed adaptive and legacy launcher resources live under `app/src/main/res/`.

## Repository-specific conventions

- Keep production Kotlin in package `com.wachin.chordstudio` under `app/src/main/java/com/wachin/chordstudio/`; local JVM tests belong under `app/src/test`, and device tests under `app/src/androidTest`.
- The user-facing UI is currently Spanish. Preserve the existing Spanish labels and terminology when adding or changing visible text; add reusable Android UI strings to `app/src/main/res/values/strings.xml` when appropriate.
- Compose UI is built directly with Material 3 components and an app-level `darkColorScheme()` in `MainActivity`. Match existing state-driven Compose patterns (`remember`, `LaunchedEffect`, `rememberCoroutineScope`) rather than introducing another UI framework or state layer.
- Chord recognition intentionally uses the regular expression and majority-of-tokens heuristic in `ChordStudio`. If chord syntax changes, update both detection and transposition behavior and add focused unit coverage for roots, qualities, slash chords, enharmonic output, and non-chord lyric lines.
- Transposition always derives from the untransposed source (`originalText`) instead of repeatedly transposing already-rendered text. Preserve this invariant when editing controls or adding new transpose actions.
- Font settings are persisted asynchronously through `FontPreferencesManager`; use its `Flow<FontSettings>` and suspend save method rather than adding a second persistence mechanism. Custom font files are loaded from the app-specific external `fonts` directory and should be treated as optional/failable resources with the existing fallback to `FontFamily.Monospace`.
- Dependency versions are centralized in `gradle/libs.versions.toml`; use version-catalog aliases in `app/build.gradle.kts` instead of hard-coding new dependency versions.
- Launcher icon changes must account for both adaptive resources and density-specific legacy PNGs. Follow `docs/launcher-icon-guide.md` and verify appearance on a real launcher when changing icon artwork or safe-area scaling.

