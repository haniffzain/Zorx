# Zorx

> Development status verified at b14b199 on 20 August 2026.

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

🚧 Window Resize UI integration

🚧 Window Dragging

🚧 Snap Layout

🚧 Multi Desktop

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

The `development` branch is the active engineering branch for the newest desktop work.

The near-term objective is to turn the verified native Android capabilities into a coherent desktop windowing experience:

1. Window movement
2. Window resize UI
3. Drag handles / title-bar interaction
4. Maximize / restore / minimize
5. Taskbar state synchronization
6. Menu and application-launcher interaction
7. Consistent typography and font system
8. Snap layouts and multi-window organization

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
