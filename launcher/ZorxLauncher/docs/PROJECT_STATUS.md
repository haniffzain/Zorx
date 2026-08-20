# Project Status

Verified on development at b14b199 on 20 August 2026.

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

## Partial

- Native discovery/promotion depends on platform visibility.
- Native sync covers move events but not maximize/restore state events.
- Taskbar refresh covers open/state/remove but not focus events.
- zIndex is tracked but compositor order is not sorted by it.

## Current milestone

Native Window Lifecycle Unification: task identity, native close, external task
reconciliation, complete bounds/state synchronization and lifecycle tests.
