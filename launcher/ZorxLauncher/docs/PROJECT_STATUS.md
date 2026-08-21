# Project Status

Build-verified on development after Phase 6E on 21 August 2026. Runtime Waydroid verification is pending an available Waydroid session.

## Build

./gradlew assembleDebug succeeds. Android Gradle Plugin 8.5.0 emits a compileSdk
35 compatibility warning.

## Completed

- Runtime, spatial engine, repository and event pipeline.
- Desktop surface, scene, compositor and window painter.
- Native freeform launch and synthetic/native task registration path.
- Focus, drag, bottom-right resize and edge/corner snap UI.
- Spatial minimize, maximize/restore and taskbar restore.
- Coalesced native bounds synchronization for move events.
- Start Menu and App Drawer integration with stable dismissal behavior.
- Phase 4C: persistent centralized typography controls and live shell updates.
- Phase 4D: independent application/taskbar icon sizing and centered responsive floating App Drawer.
- Phase 4E: persistent resolution-aware display scale, effective workspace metrics and display preview.
- Phase 4F: persistent live Theme Engine with ZorxColors compatibility tokens and initial presets.
- Phase 5A: persistent Widget Engine foundation and theme-aware Clock widget.
- Phase 5B: Widget Edit Mode with logical grid drag/snap and persistent layout lock.
- Phase 5C: selected-widget toolbar, logical resize, duplicate/remove and collision-safe placement.
- Phase 5D: Widget Library v1 — Calendar, System Monitor, Network and Quick Controls registered with shared grid/persistence/theme support. Runtime data availability remains platform-dependent and visual testing is deferred until after Phase 6.
- Phase 5E: Weather and Media provider abstractions plus persistent per-instance Notes widget configuration. Weather/media providers remain unconfigured until platform integration.
- Phase 6A: four persistent virtual workspaces with logical window membership and compositor visibility. Widgets remain global in this foundation phase; runtime visual testing is deferred until Phase 6E.
- Phase 6B: persistent multi-monitor topology data model with per-display scale, work area, logical primary and widget display migration. Waydroid currently exposes one display; host arrangement and cross-display movement remain future work.
- Phase 6C: persistent window location model (workspace, display, logical bounds, state and z-order), workspace/display move actions and centralized logical-to-native coordinate conversion. Logical moves preserve task identity and use the existing native bounds bridge when more than one native display is available.
- Phase 6D: compact taskbar workspace switcher, current-workspace/current-display running-window filtering, focused/minimized states and taskbar-item move actions.
- Phase 6E: static stability pass, lifecycle/listener cleanup and build verification. Live Waydroid runtime verification could not run on this host because neither Waydroid nor ADB is installed and the available WSL distribution is stopped.

## Partial

- Native discovery/promotion depends on platform visibility.
- Native sync covers move events but not maximize/restore state events.
- Taskbar refresh covers open/state/remove but not focus events.
- zIndex is tracked but compositor order is not sorted by it.

## Current milestone

Phase 6E is build-verified. Runtime visual testing remains pending a Waydroid-capable host; it has not been represented as completed.

## Phase 6C coordinate and platform notes

- `SpatialEngine` continues to own the single in-memory window object and native Android task identity.
- `ZorxWindowLocationManager` persists a display-local logical rectangle; `ZorxDisplayCoordinates` is the only Phase 6C boundary that converts it to native physical task pixels.
- Moving a maximized window targets the new display work area. Minimized windows retain their mapping without forced restore.
- Current Waydroid exposure is normally one Android display. In that configuration, a move-to-display updates only the logical Zorx mapping; it does not fabricate a host-level native move. Workspace moves are immediately reflected by compositor visibility.

## Phase 6D taskbar behavior

- The taskbar displays workspace buttons 1–4: active uses the accent, a workspace with windows uses a subtle surface/border indicator, and empty workspaces are muted.
- The running strip is filtered to the active workspace and display scope. Default persisted policy is `PRIMARY_ONLY`; `PER_DISPLAY` and `MIRRORED` are represented in the model for later multi-taskbar rendering.
- Active display resolves from the focused window's persisted display location, otherwise the logical primary display. The single visible Phase 6D taskbar remains the primary taskbar.
- Long-pressing a running-window item opens desktop-style Move to Workspace / Move to Display actions. Horizontal scrolling prevents running items from overlapping in narrow taskbars.

## Phase 6E verification and fixes

### Completed on this host

- Branch/remote synchronization, diff check and a clean debug APK build.
- Static audit of workspace, display, taskbar, widget and runtime listener lifecycles.
- `WidgetHost` now unregisters its Theme Engine listener on detachment and stops its clock runnable.
- `NativeTaskSynchronizer` now cancels coalesced delayed native-bounds work when `DesktopRuntime` is destroyed, preventing stale callbacks against a disposed desktop.

### Pending live Waydroid verification

- Workspace switching, task/taskbar interactions, App Drawer/Start Menu dismissal, widget editing, persistence after restart and logcat inspection.
- Real native multi-display task movement and scale conversion. The current Windows environment has no `waydroid` or `adb` executable; WSL Ubuntu is stopped.
