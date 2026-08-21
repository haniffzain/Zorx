# Project Status

Verified on development after Phase 4F on 21 August 2026.

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

## Partial

- Native discovery/promotion depends on platform visibility.
- Native sync covers move events but not maximize/restore state events.
- Taskbar refresh covers open/state/remove but not focus events.
- zIndex is tracked but compositor order is not sorted by it.

## Current milestone

Phase 4 visual personalization is complete in code and build-verified. Runtime visual testing is intentionally deferred until after Phase 6.
