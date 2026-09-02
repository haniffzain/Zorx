# Zorx Development Roadmap

Status updated through Phase 7I on `codex/continue-window-lifecycle`.

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

## Completed shared placement policy

- Widgets and shortcuts now reserve cells through one placement policy.
- Add, drag, widget resize and duplication reject cross-object collisions.
- Existing shortcut collisions are deterministically migrated to the next free cell
  while legacy widget records retain their four-column persisted format.

## Completed configurable grid profiles

- Persistent Comfortable (12×8) and Compact (12×10) profiles control shared grid
  rows, gaps and padding from the desktop View menu.
- Both widget and shortcut hosts resolve the same profile; shortcut reconciliation
  migrates cells that no longer fit after a profile change.

## Completed scoped desktop layouts

- Widget and shortcut records persist workspace/display identity.
- Rendering and collision ownership are isolated to the active scope, and switching
  workspaces refreshes both desktop hosts immediately.
- Legacy records migrate to Workspace 1 / primary display.

## Completed workspace layout mobility

- Desktop Layout menu copies or moves the active widget/shortcut layout to another
  workspace after a complete collision/capacity preflight.
- Widget relocation stays compatible with legacy column boundaries; copied Notes
  retain their text configuration.
- Failure leaves both source and destination collections unchanged.

## Current milestone: Physical Display Surfaces

- Bind display scope to physical per-display surfaces when Android exposes them.
- Extend layout mobility from workspace targets to real display targets.

## Later
- Android Live Wallpaper service/provider integration and device validation.
- File Manager, Terminal, Settings and notifications.
- Physical multi-monitor rendering and privileged runtime verification.
