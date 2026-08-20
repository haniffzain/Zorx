# Changelog

## 0.5

- Native launcher
- Desktop interface foundation
- Taskbar
- Native Recents
- Native Freeform launch
- Android 15 support
- Recents overlay integration
- Privileged task-management integration required for desktop task control

## 15 August 2026 — Development branch documentation update

### Verified

- Native Android Recents provider integration is working on the development branch.
- Native Android applications can be launched in FREEFORM windowing mode.
- Android 15 / API 35 is the active target.
- The Android `taskId`-based resize path through `IActivityTaskManager.resizeTask(...)` has been identified and tested as the working direction for native task resizing.
- A taskbar running-window restore issue was fixed in commit `86a28d1`.
- The taskbar restore fix is treated as a focused behavior fix; the complete taskbar/window-state system is still under development.
- A concrete `StartMenuView` implementation is present, including header, search field, pinned applications and menu entries.
- A concrete `WindowPainter` implementation is present for the desktop window shell, including title bar, focus border, title and window controls.
- Current menu/window typography uses explicit Android text sizing; a final custom Zorx font resource is not yet verified.

### UI / Design work

- Desktop menu/application-menu work has progressed from design direction to a verified implementation foundation.
- Typography/font work has progressed to explicit UI text sizing, while the final font system remains planned.
- Menu, taskbar, window chrome and typography continue to be treated as one coherent desktop UI layer.

## Upcoming

- Complete desktop menu behavior and application search/filter.
- Full application drawer integration.
- Window movement and resize UI integration.
- Window focus and z-order integration.
- Maximize / restore / minimize behavior wiring.
- Taskbar window-state synchronization beyond the verified restore fix.
- Snap Layout.
- Final typography tokens and custom font resource strategy.
- Multi Desktop.
