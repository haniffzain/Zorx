# Changelog

## 1 September 2026 — Phases 7A–7D

- Unified native window lifecycle around promoted task IDs, including exact close,
  external removal reconciliation and distinct tasks from the same package.
- Routed drag, resize, snap, maximize and restore through one native bounds path;
  added z-index rendering and taskbar focus refresh.
- Upgraded to Android Gradle Plugin 8.8.2 and Gradle 8.10.2 for supported API 35
  builds, with GitHub Actions unit-test/APK verification on JDK 17.
- Added taskbar-aware full-edge/corner resize and work-area constraints (Phase 7A).
- Added half/quarter snap chooser geometry (Phase 7B).
- Added two-column, three-column and main-plus-stack arrangements (Phase 7C).
- Added the responsive 12-column desktop grid, collision/placement utilities and
  backward-compatible widget mapping (Phase 7D).
- Added persistent launchable desktop shortcuts with grid-aware add, drag/snap,
  collision rejection and removal (Phase 7E).
- Added a shared placement policy for widget/shortcut collision ownership and
  deterministic shortcut migration while preserving legacy widget records (Phase 7F).
- Audited the wallpaper subsystem: fixed workspace-switch redraw, made scope
  selection authoritative and made bitmap caching target-size aware. Android Live
  Wallpaper remains unimplemented and is not implied by “live desktop layer”.
- Added persistent Comfortable/Compact grid profiles shared by widgets and desktop
  shortcuts, with a checked desktop View menu and deterministic migration (Phase 7G).
- Scoped widget and shortcut visibility, occupancy and persistence by workspace and
  display. Legacy records migrate deterministically to Workspace 1 / primary display
  without deleting records in inactive scopes (Phase 7H).
- Added preflighted copy/move of complete widget/shortcut layouts between workspaces,
  including collision relocation, note configuration copy and no-write failure when
  the destination cannot fit the complete layout (Phase 7I).
- Next: physical multi-display surfaces and display-to-display mobility.

## 20 August 2026 — development at b14b199

- Verified desktop runtime/compositor, focus, drag, bottom-right resize, snap,
  spatial minimize/maximize/restore and taskbar restore.
- Verified coalesced native bounds forwarding for DesktopMovedEvent.
- Verified stable Start Menu/App Drawer dismissal and successful assembleDebug.
- Pending: native close, maximize/restore sync, authoritative task identity,
  external reconciliation, multi-instance support, z-index rendering and tests.
- Build warning: Android Gradle Plugin 8.5.0 is tested through compileSdk 34 while
  the project uses compileSdk 35.

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
