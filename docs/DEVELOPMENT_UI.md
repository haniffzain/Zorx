# Zorx Development UI Notes

## 16 August 2026 — Verified implementation update

This document records UI implementation and design decisions on the `development` branch separately from low-level Android platform checkpoints.

## Desktop Menu

The development branch now contains a concrete `StartMenuView` implementation rather than only a design concept.

### Verified implementation

- Zorx start/menu surface is implemented as a `ScrollView`-based desktop UI component.
- Menu header displays `Zorx`.
- Application search field is present.
- Pinned applications are loaded through `PinnedAppManager`.
- Installed applications are resolved through `AppManager`.
- Pinned application icons can launch their associated applications.
- Menu items currently include Applications, Files, Settings and Search.
- The Applications entry supports an application-list callback.
- Menu styling uses the existing `ZorxColors` and `ZorxRadius` design resources.

This is a **verified menu foundation**, not a claim that the complete desktop menu is finished. Search behavior, full application browsing, menu state management and final desktop integration remain development work.

## Window UI

The development branch also contains a `WindowPainter` implementation for the Zorx window shell.

### Verified implementation

- Desktop objects are painted as window shells.
- Window body and title bar are rendered.
- Focused windows receive distinct border treatment.
- Window titles are rendered.
- Minimize, maximize and close controls are rendered.
- A basic content label is rendered.
- Rendering uses clipping/rejection through `Canvas.quickReject` for off-screen windows.

This provides a concrete visual window-shell foundation. It does not yet prove that all window controls are fully wired to task-manager operations.

## Typography and Font

Typography is treated as a first-class part of the Zorx design system.

### Verified

The current UI code contains explicit text sizing for menu and window elements, including menu headers, search text, pinned-app labels, menu items and window title/content text.

### Not yet verified

- A final custom Zorx font resource has not been established in the verified source examined during this update.
- Final font family selection, weights, fallback behavior and centralized typography tokens remain implementation tasks.

Therefore the project should distinguish **current typography implementation** from the **future Zorx font system**.

## Relationship to Desktop Windowing

```text
Android 15 / AOSP
        ↓
Native Recents + privileged task management
        ↓
Native Freeform tasks
        ↓
Zorx Window / Desktop Objects
        ↓
Window Shell + Taskbar + Start Menu
        ↓
Typography + Design System
```

## Current UI Status

### Verified platform/UI foundation

- Native Recents provider
- Recents overlay integration
- Native freeform launch
- Native task-management permissions
- Android 15 / API 35 target
- Taskbar running-window restore fix
- Start menu foundation
- Window shell painter foundation

### Active UI work

- Complete desktop menu behavior
- Application search/filter behavior
- Full application drawer integration
- Window movement interaction
- Window resize interaction
- Window focus and z-order integration
- Maximize / restore / minimize behavior wiring
- Taskbar window-state synchronization beyond restore
- Snap layout UI
- Final typography/font system

## Rule

A UI item is marked **verified** only when the corresponding implementation is present in the `development` branch. Design ideas remain separate from implementation status until source/resource evidence exists.
