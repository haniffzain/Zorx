# LumaOS Development UI Notes

## 15 August 2026

This document records UI work and design decisions on the `development` branch separately from low-level Android platform checkpoints.

## Desktop Menu

The LumaOS desktop requires a dedicated menu/application-launcher experience rather than relying on a conventional Android home-screen interaction model.

### Direction

- Menu interaction should fit the desktop workspace rather than behave like a phone launcher.
- Application discovery and launching should remain accessible from the taskbar/desktop environment.
- Menu visuals should follow the LumaOS design system and remain consistent with window chrome, taskbar and workspace surfaces.
- The menu is a UI workstream and must not be marked as fully implemented until the corresponding source/resources are verified.

## Typography and Font

Typography is treated as a first-class part of the LumaOS design system.

### Recorded work

- Font changes discussed during the 15 August design session are recorded as UI/design work.
- Final font selection, weights, sizes, fallback behavior and Android resource integration remain implementation tasks until verified in source/resources.
- Typography should remain consistent across menu, taskbar, window title bars, dialogs, settings and future desktop services.

## Relationship to Desktop Windowing

The UI work sits above the verified native Android capabilities:

```text
Android 15 / AOSP
        ↓
Native Recents + privileged task management
        ↓
Native Freeform tasks
        ↓
Luma Window Manager
        ↓
Taskbar / Menu / Window Chrome
        ↓
Typography + Design System
```

## Current Status

### Verified platform work

- Native Recents provider
- Recents overlay integration
- Native freeform launch
- Native task-management permissions
- Android 15 / API 35 target

### Active UI work

- Desktop menu
- Typography/font system
- Window movement UI
- Window resize UI
- Window chrome
- Taskbar window-state integration
- Snap layout UI

## Rule

Design ideas are recorded here as requirements or direction until implementation is verified. This prevents the project documentation from confusing a UI decision with completed code.
