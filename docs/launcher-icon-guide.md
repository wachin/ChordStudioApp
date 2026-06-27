# Launcher Icon Guide

This document explains the launcher icon work done for `ChordStudioApp`, why it was needed, and where to modify it in the future.

## Goal

The project needed a professional custom app icon that:

- works as the Android launcher icon
- is editable as SVG
- behaves correctly after installation on real devices
- remains understandable for future developers

## Source Design

The editable source icon is:

- [design/chordstudio-app-icon.svg](/home/wachin/AndroidStudioProjects/ChordStudioApp/design/chordstudio-app-icon.svg)

This file is the design source that should be edited in Inkscape when the icon artwork needs visual changes.

Important:

- Android does not use this SVG file directly as the launcher icon.
- The SVG is only the source artwork.
- Android uses generated resources under `app/src/main/res/`.

## Android Files That Control the Installed App Icon

The manifest points to these launcher resources:

- [app/src/main/AndroidManifest.xml](/home/wachin/AndroidStudioProjects/ChordStudioApp/app/src/main/AndroidManifest.xml:8)

Key attributes:

- `android:icon="@mipmap/ic_launcher"`
- `android:roundIcon="@mipmap/ic_launcher_round"`

These resources are currently backed by:

- [app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml](/home/wachin/AndroidStudioProjects/ChordStudioApp/app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml:1)
- [app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml](/home/wachin/AndroidStudioProjects/ChordStudioApp/app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml:1)
- [app/src/main/res/drawable/ic_launcher_background.xml](/home/wachin/AndroidStudioProjects/ChordStudioApp/app/src/main/res/drawable/ic_launcher_background.xml:1)
- [app/src/main/res/drawable/ic_launcher_foreground.xml](/home/wachin/AndroidStudioProjects/ChordStudioApp/app/src/main/res/drawable/ic_launcher_foreground.xml:1)
- [app/src/main/res/drawable/ic_launcher_monochrome.xml](/home/wachin/AndroidStudioProjects/ChordStudioApp/app/src/main/res/drawable/ic_launcher_monochrome.xml:1)
- [app/src/main/res/drawable-nodpi/ic_launcher_foreground_art.png](/home/wachin/AndroidStudioProjects/ChordStudioApp/app/src/main/res/drawable-nodpi/ic_launcher_foreground_art.png)

Legacy launcher PNGs for pre-adaptive behavior are here:

- `app/src/main/res/mipmap-mdpi/`
- `app/src/main/res/mipmap-hdpi/`
- `app/src/main/res/mipmap-xhdpi/`
- `app/src/main/res/mipmap-xxhdpi/`
- `app/src/main/res/mipmap-xxxhdpi/`

## What Was Changed

### 1. Custom icon design added

A custom icon SVG was created and stored in `design/chordstudio-app-icon.svg`.

### 2. Android launcher resources were replaced

The default Android Studio launcher icon assets were replaced with resources based on the custom icon.

This included:

- replacing old default foreground/background launcher resources
- generating new PNG launcher assets for multiple densities
- adding a monochrome adaptive icon resource
- removing the old conflicting `ic_launcher*.webp` files

### 3. Adaptive icon tuning was needed after testing on a real phone

After installation, the icon looked cropped on the device launcher.

Reason:

- many launchers apply their own adaptive icon mask
- the original foreground artwork was too large for the visible safe area
- Samsung-style launchers may crop more aggressively than expected

### 4. Foreground artwork was reduced multiple times

The adaptive foreground icon was scaled down several times to fit better inside the launcher mask.

The file that was tuned for this is:

- [app/src/main/res/drawable-nodpi/ic_launcher_foreground_art.png](/home/wachin/AndroidStudioProjects/ChordStudioApp/app/src/main/res/drawable-nodpi/ic_launcher_foreground_art.png)

The monochrome layer was reduced to match:

- [app/src/main/res/drawable/ic_launcher_monochrome.xml](/home/wachin/AndroidStudioProjects/ChordStudioApp/app/src/main/res/drawable/ic_launcher_monochrome.xml:1)

## Current Status

Current result:

- the icon is much improved on the tested phone
- the full design is almost visible
- there is still a small amount of clipping, so the current result is close but not yet perfect

Current uncommitted tuning at the time of writing:

- `app/src/main/res/drawable-nodpi/ic_launcher_foreground_art.png`
- `app/src/main/res/drawable/ic_launcher_monochrome.xml`

These are the main files to review before the next commit.

## Screenshots Used During Tuning

Device screenshots used to evaluate launcher behavior:

- [8vo/Screenshot_20260626_161102_ChordStudioApp_Android.jpg](/home/wachin/AndroidStudioProjects/ChordStudioApp/8vo/Screenshot_20260626_161102_ChordStudioApp_Android.jpg)
- [8vo/Screenshot_20260626_161103_ChordStudioApp_Android.jpg](/home/wachin/AndroidStudioProjects/ChordStudioApp/8vo/Screenshot_20260626_161103_ChordStudioApp_Android.jpg)

These screenshots show how much of the icon is visible after installation on the phone launcher.

## Where To Modify Things

### If the visual design itself should change

Edit:

- [design/chordstudio-app-icon.svg](/home/wachin/AndroidStudioProjects/ChordStudioApp/design/chordstudio-app-icon.svg)

Examples:

- color changes
- shape changes
- composition changes
- node/ring size changes

### If the installed icon is being cropped by a launcher

Adjust:

- [app/src/main/res/drawable-nodpi/ic_launcher_foreground_art.png](/home/wachin/AndroidStudioProjects/ChordStudioApp/app/src/main/res/drawable-nodpi/ic_launcher_foreground_art.png)
- [app/src/main/res/drawable/ic_launcher_monochrome.xml](/home/wachin/AndroidStudioProjects/ChordStudioApp/app/src/main/res/drawable/ic_launcher_monochrome.xml:1)

In practice:

- reduce the visible icon size inside the adaptive foreground canvas
- keep the icon centered
- keep monochrome scaling consistent with the normal icon

## Final Tuning For This Project

For the current state of this project, the most likely final fix is not a redesign.

It only needs a very small reduction of the adaptive foreground artwork.

Edit these two things:

### 1. Main adaptive foreground image

File:

- [app/src/main/res/drawable-nodpi/ic_launcher_foreground_art.png](/home/wachin/AndroidStudioProjects/ChordStudioApp/app/src/main/res/drawable-nodpi/ic_launcher_foreground_art.png)

What to do:

- make the visible symbol slightly smaller
- keep it centered on the transparent canvas
- do not change the canvas size

Practical target:

- reduce the visible artwork by a very small amount, around 2% to 4%

Reason:

- the phone screenshot shows only a tiny remaining crop near the top-right edge
- the composition is already acceptable
- a small scale reduction should be enough

### 2. Monochrome adaptive icon scale

File:

- [app/src/main/res/drawable/ic_launcher_monochrome.xml](/home/wachin/AndroidStudioProjects/ChordStudioApp/app/src/main/res/drawable/ic_launcher_monochrome.xml:1)

What to edit:

- the `android:scaleX` value
- the `android:scaleY` value

Current values:

- `0.455`
- `0.455`

What to do:

- lower both values slightly and keep them equal

Practical target:

- try `0.445` first
- if needed, try `0.44`

Reason:

- the monochrome version should match the same safe margin as the normal icon

## Smallest Safe Workflow

If you want the smallest possible adjustment, do this:

1. Make the foreground PNG just a little smaller.
2. Change monochrome scale from `0.455` to `0.445`.
3. Build and reinstall the app.
4. Check the launcher again.

If it still clips a little:

1. keep the design unchanged
2. reduce the foreground artwork one more tiny step
3. change monochrome scale to `0.44`

Do not change these unless you want a real redesign:

- [design/chordstudio-app-icon.svg](/home/wachin/AndroidStudioProjects/ChordStudioApp/design/chordstudio-app-icon.svg)
- [app/src/main/res/drawable/ic_launcher_background.xml](/home/wachin/AndroidStudioProjects/ChordStudioApp/app/src/main/res/drawable/ic_launcher_background.xml:1)

### If the adaptive icon background should change

Edit:

- [app/src/main/res/drawable/ic_launcher_background.xml](/home/wachin/AndroidStudioProjects/ChordStudioApp/app/src/main/res/drawable/ic_launcher_background.xml:1)

### If legacy launcher icons need regeneration

Regenerate the PNG files in:

- `app/src/main/res/mipmap-mdpi/`
- `app/src/main/res/mipmap-hdpi/`
- `app/src/main/res/mipmap-xhdpi/`
- `app/src/main/res/mipmap-xxhdpi/`
- `app/src/main/res/mipmap-xxxhdpi/`

## Recommended Workflow For Future Icon Changes

1. Edit the master icon in `design/chordstudio-app-icon.svg`.
2. Regenerate the launcher assets used by Android.
3. Rebuild the app.
4. Install it on a real device.
5. Check the icon on the launcher, not only inside Android Studio.
6. If it looks cropped, reduce the adaptive foreground artwork and monochrome scale slightly.
7. Rebuild and retest.

## Practical Advice

- Do not assume the icon preview in Android Studio matches the real launcher.
- Real-device testing is required.
- Adaptive icons should use conservative margins.
- If clipping continues, the best long-term fix is to simplify the adaptive icon into:
  - a stable background layer
  - a smaller foreground symbol with more empty margin

## Suggested Next Cleanup

Before the next commit, review whether:

- the current foreground PNG scale is final
- the monochrome scale is final
- `design/chordstudio-app-icon.svg` should also be committed if it was edited during the tuning process
