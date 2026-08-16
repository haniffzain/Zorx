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

### UI / Design work recorded

- Desktop menu/application-menu interaction is now part of the documented UI direction.
- Typography and font changes are recorded as part of the LumaOS design system work.
- These UI/design items remain separate from verified platform capabilities until corresponding implementation is confirmed in source/resources.

## Upcoming

- Window Resize UI integration
- Window Drag
- Window Focus
- Maximize / Restore / Minimize behavior completion
- Taskbar window-state synchronization beyond the verified restore fix
- Final desktop menu implementation
- Final typography/font system
- Snap Layout
- Multi Desktop
