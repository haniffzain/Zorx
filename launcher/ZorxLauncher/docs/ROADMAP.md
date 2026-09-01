# ZorxLauncher Roadmap

## Verified foundation

- Runtime, spatial engine, repository and event bus.
- Desktop surface, scene, compositor and window painter.
- Freeform launch, synthetic registration and native promotion attempt.
- Focus, constrained drag, full-edge/corner resize, minimize, maximize/restore and
  snap/arrangement UI.
- Taskbar running-window controls, Start Menu and App Drawer integration.

## Active milestone

- Virtual workspace foundation: logical workspace membership, visibility and workspace switching.
- Multi-monitor topology foundation: logical display arrangement, per-display scale and work areas.
- Phase 6C complete: persistent window workspace/display membership, logical bounds conversion, work-area clamping and desktop context actions for moving the highest-z window.
- Phase 6D complete: taskbar workspace switcher, workspace/display-aware running-window filtering, item move menu and overflow scrolling.
- Phase 6E stability pass complete for build/static checks: widget theme-listener and native bounds callback cleanup. Live Waydroid test matrix remains pending a Waydroid-capable host.
- Phase 6F complete: centralized desktop wallpaper layer, persistent workspace/display-ready assignment model, image picker, solid color and layout modes.
- Phase 6F audit complete: corrected workspace redraw, scope precedence and
  target-aware image caching. Android Live Wallpaper remains a separate future phase.
- Phase 6G complete: responsive floating tabbed settings window with fixed navigation/footer, per-tab scrolling and direct desktop context routing.

- Native/spatial task identity, maximize/restore bounds synchronization and native
  close are implemented; privileged runtime verification remains.
- External task reconciliation and new-task-aware multi-instance discovery are
  implemented; privileged runtime verification remains.
- Add unit and integration coverage.

## Later

- Phase 7A complete: full-edge/corner resize, minimum sizing and taskbar-aware
  work-area constraints with unit-tested geometry.
- Phase 7B foundation complete: focused-window Snap chooser for half and quarter
  slots with unit-tested work-area geometry.
- Phase 7C complete: two-column, three-column and main-plus-stack group arrangements
  for visible windows in the active workspace.
- Phase 7D complete: configurable 12-column Desktop Grid Engine with responsive
  geometry, collision/placement utilities and backward-compatible widget mapping.
- Phase 7E complete: persistent launchable desktop shortcuts with add, drag/snap,
  collision rejection and removal interactions.
- Phase 7F complete: shared widget/shortcut occupancy, cross-object collision
  rejection and deterministic migration of existing shortcut conflicts.
- Phase 7G complete: persistent Comfortable/Compact grid profiles applied to both
  widget and shortcut geometry through the desktop View menu.
- Phase 7H: workspace/display-scoped desktop layouts.
- Run the Phase 6E Waydroid smoke-test matrix, including logcat and cross-display native task movement where hardware exposure permits.
- Implement `WallpaperService` integration, provider selection and lifecycle before
  claiming Android Live Wallpaper support; then validate it in Waydroid/device.
