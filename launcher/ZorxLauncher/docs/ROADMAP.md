# ZorxLauncher Roadmap

## Verified foundation

- Runtime, spatial engine, repository and event bus.
- Desktop surface, scene, compositor and window painter.
- Freeform launch, synthetic registration and native promotion attempt.
- Focus, drag, bottom-right resize, minimize, maximize/restore and snap UI.
- Taskbar running-window controls, Start Menu and App Drawer integration.

## Active milestone

- Virtual workspace foundation: logical workspace membership, visibility and workspace switching.
- Multi-monitor topology foundation: logical display arrangement, per-display scale and work areas.
- Phase 6C complete: persistent window workspace/display membership, logical bounds conversion, work-area clamping and desktop context actions for moving the highest-z window.
- Phase 6D complete: taskbar workspace switcher, workspace/display-aware running-window filtering, item move menu and overflow scrolling.
- Phase 6E stability pass complete for build/static checks: widget theme-listener and native bounds callback cleanup. Live Waydroid test matrix remains pending a Waydroid-capable host.
- Phase 6F complete: centralized desktop wallpaper layer, persistent workspace/display-ready assignment model, image picker, solid color and layout modes.
- Phase 6G complete: responsive floating tabbed settings window with fixed navigation/footer, per-tab scrolling and direct desktop context routing.

- Unify native and spatial lifecycle using task identity.
- Synchronize maximize/restore and close with native tasks.
- Reconcile externally removed tasks.
- Correct render z-order and taskbar focus refresh.
- Add unit and integration coverage.

## Later

- Full-edge resize and work-area constraints.
- Snap layout UI and multi-window workflows.
- Desktop grid, widgets, icons and persistence.
- Run the Phase 6E Waydroid smoke-test matrix, including logcat and cross-display native task movement where hardware exposure permits.
- Add slideshow/dynamic/live wallpaper providers only after live wallpaper rendering and persistence are validated in Waydroid.
