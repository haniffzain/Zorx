# Zorx Development Roadmap

Status verified on development at b14b199.

## Completed foundation

- Android 15/API 35 launcher and Recents integration.
- Native freeform launch.
- Desktop surface, scene, compositor and window painter.
- Spatial repository and event pipeline.
- Focus, drag, bottom-right resize and edge/corner snap UI.
- Spatial minimize, maximize/restore and taskbar restore.
- Start Menu search handoff, App Drawer filtering and stable dismissal.
- Coalesced move/resize bounds forwarding to native tasks.

## Current milestone: Native Window Lifecycle Unification

- Use task ID as authoritative window identity.
- Synchronize move, resize, snap, maximize and restore through one bounds pipeline.
- Implement native close and registry cleanup.
- Reconcile tasks changed or closed outside Zorx.
- Support multiple tasks from the same package.
- Render by z-index and refresh taskbar focus state.
- Add lifecycle and interaction tests.

## Later

- Full-edge resize and work-area constraints.
- Snap layout chooser and multi-window arrangements.
- Desktop grid, widgets, icons and persistence.
- File Manager, Terminal, Settings and notifications.
- Virtual desktops and multi-monitor support.
