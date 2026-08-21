# Project Status

Verified on development after Phase 6C on 21 August 2026.

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

## Partial

- Native discovery/promotion depends on platform visibility.
- Native sync covers move events but not maximize/restore state events.
- Taskbar refresh covers open/state/remove but not focus events.
- zIndex is tracked but compositor order is not sorted by it.

## Current milestone

Phase 6C is build-verified. Runtime visual testing, including real multi-display task movement, is intentionally deferred until Phase 6E.

## Phase 6C coordinate and platform notes

- `SpatialEngine` continues to own the single in-memory window object and native Android task identity.
- `ZorxWindowLocationManager` persists a display-local logical rectangle; `ZorxDisplayCoordinates` is the only Phase 6C boundary that converts it to native physical task pixels.
- Moving a maximized window targets the new display work area. Minimized windows retain their mapping without forced restore.
- Current Waydroid exposure is normally one Android display. In that configuration, a move-to-display updates only the logical Zorx mapping; it does not fabricate a host-level native move. Workspace moves are immediately reflected by compositor visibility.
