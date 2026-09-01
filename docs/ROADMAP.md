# Zorx Development Roadmap

Status updated through Phase 7D on `codex/continue-window-lifecycle`.

## Completed foundation

- Android 15/API 35 launcher and Recents integration.
- Native freeform launch.
- Desktop surface, scene, compositor and window painter.
- Spatial repository and event pipeline.
- Focus, constrained drag, full-edge/corner resize and snap/arrangement UI.
- Spatial minimize, maximize/restore and taskbar restore.
- Start Menu search handoff, App Drawer filtering and stable dismissal.
- Coalesced move/resize bounds forwarding to native tasks.

## Completed window lifecycle and organization

- Task ID is the authoritative native window identity after promotion.
- Move, resize, snap, maximize and restore share one native bounds pipeline.
- Native close, exact registry cleanup and external-task reconciliation are implemented.
- Multiple tasks from one package retain distinct identities.
- Rendering follows z-index and taskbar focus state refreshes with spatial focus.
- Full-edge resize, work-area constraints, snap choices and multi-window arrangements
  are covered by platform-independent unit tests.
- A responsive 12-column desktop grid now supplies span geometry, snapping,
  collision checks and backward-compatible widget placement.

## Completed desktop icons foundation

- Launchable shortcuts render on the shared grid and persist component identity and
  positions independently of taskbar pins.
- Add and drag placement resolves collisions through `GridEngine`; removal and
  malformed/uninstalled entry cleanup do not change widget persistence.

## Current milestone: Shared Grid Persistence

- Add persistent grid preferences and cross-object collision ownership.
- Migrate widgets and shortcuts through one placement repository while retaining
  backward compatibility with existing widget records.

## Later
- File Manager, Terminal, Settings and notifications.
- Physical multi-monitor rendering and privileged runtime verification.
