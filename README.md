# Zorx

> Development status updated through Phase 7G on 1 September 2026.

Zorx is a modern desktop environment for Android built directly on AOSP.

Unlike traditional Android launchers, Zorx provides a desktop experience with native freeform windows, multitasking, taskbar integration, native Recents integration and a modular path toward desktop-class services.

---

## Current Features

Verified on the `development` branch:

- Native Android Launcher
- Desktop Workspace foundation
- Native Freeform Window Launch
- Native Android Recents Replacement
- Taskbar integration
- Application Drawer foundation
- Active Application Tracking
- Android 15 / API 35 targeting
- Privileged task-management integration required for native desktop window control

### UI / Design Direction

- Desktop menu / application menu interaction is part of the Zorx desktop UI direction.
- Typography and font changes are treated as part of the Zorx design system.
- Menu and typography work should be documented separately from platform capabilities so visual design decisions are not confused with implemented system functions.

---

## Android Version

Currently targeting:

**Android 15 (AOSP)**

API Level: **35**

---

## Project Structure

```text
launcher/
Desktop UI and Launcher

docs/
Architecture, checkpoints and project documentation

ZorxRecentsOverlay/
Android Recents overlay integration

branding/
Logos and artwork

screenshots/
Project screenshots
```

---

## Current Progress

✔ Desktop Environment

✔ Native Freeform Launch

✔ Native Recents Replacement

✔ Android 15 Compatibility

✔ Required Recents/task-management permissions

✔ Native task resize path identified and tested through Android framework interfaces

✔ Native task identity, close and external reconciliation

✔ Full-edge/corner window dragging and resizing

✔ Snap chooser and multi-window arrangements

✔ Four logical workspaces and display-topology foundation

✔ Responsive desktop grid and persistent widgets

✔ Persistent launchable desktop shortcuts

✔ Shared widget/shortcut collision-aware placement

✔ Persistent Comfortable/Compact desktop grid profiles

✔ Static wallpaper: gradient, solid color and persisted document images

🚧 Android Live Wallpaper service/rendering

🚧 Final menu implementation

🚧 Final typography/font system

---

## Technical Checkpoints

### Recents

Zorx currently uses its own Recents activity as the Android Recents provider on the development branch. The Recents overlay changes the system `config_recentsComponentName` to the Zorx/Zorx launcher implementation. Required privileged task-management permissions have been verified. See [`docs/RECENTS_CHECKPOINT.md`](docs/RECENTS_CHECKPOINT.md).

### Freeform Windows

Native Android applications have been successfully launched in Android freeform mode. The Android framework `IActivityTaskManager.resizeTask(int taskId, Rect bounds, int flags)` path has been identified as the proven direction for native task resizing. See [`docs/FREEFORM_RESIZE_CHECKPOINT.md`](docs/FREEFORM_RESIZE_CHECKPOINT.md).

---

## Current Development Direction

The active engineering work continues from `development` on the
`codex/continue-window-lifecycle` branch.

The near-term objective is to turn the verified native Android capabilities into a coherent desktop windowing experience:

1. Per-workspace and per-display desktop object layouts
2. Waydroid smoke testing for privileged task operations
3. File Manager, Terminal and notification services

---

## Roadmap

### Version 0.5

- Desktop
- Launcher
- Taskbar
- Native Freeform
- Native Recents
- Android 15 support

### Version 0.6

- Window Resize
- Window Move
- Window Focus

### Version 0.7

- File Manager
- Terminal
- Settings

### Version 0.8

- Snap Layout
- Virtual Desktop

### Version 0.9

- Multi Monitor

### Version 1.0

- Stable Desktop Operating Environment

---

## Documentation

- [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md)
- [`docs/ANDROID15.md`](docs/ANDROID15.md)
- [`docs/RECENTS_CHECKPOINT.md`](docs/RECENTS_CHECKPOINT.md)
- [`docs/FREEFORM_RESIZE_CHECKPOINT.md`](docs/FREEFORM_RESIZE_CHECKPOINT.md)
- [`docs/WINDOW_MANAGER.md`](docs/WINDOW_MANAGER.md)
- [`docs/CHANGELOG.md`](docs/CHANGELOG.md)
- [`docs/ROADMAP.md`](docs/ROADMAP.md)

Documentation should describe verified implementation, active experiments and UI/design decisions separately.

---

Author

Mohd Haniff
Zorx Project
