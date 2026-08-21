# Zorx Master Architecture

Zorx is an Android desktop platform built around native applications, a desktop
spatial model and a task-aware shell.

## Layers

- UI: DesktopActivity, StartMenuView, AppDrawerView and TaskbarView.
- Desktop: DesktopSurface, DesktopScene and WindowInteractionController.
- Runtime: DesktopRuntime and NativeTaskSynchronizer.
- Spatial: SpatialEngine and DesktopObjectRepository.
- Rendering: DesktopCompositor and WindowPainter.
- Window model: ZorxWindowManager and WindowRegistry.
- Android platform: AppManager, AndroidWindowBackend and ActivityTaskManager.

Commands enter through controllers and mutate SpatialEngine. Events drive redraw,
taskbar refresh and native bounds synchronization. App launch enters through
AppManager and ZorxWindowService; WindowOpenedEvent bridges the task registry into
the spatial desktop.

Native task state and spatial state are separate and only partially synchronized.
The next milestone defines task-based identity and one lifecycle contract without
changing stable Start Menu or App Drawer dismissal behavior.

## Workspace and display location

Phase 6C adds `ZorxWindowLocationManager` without duplicating `DesktopObject`.
It maps the authoritative window/task identity to a workspace, a logical display,
a display-local logical rectangle, state and z-order. `ZorxDisplayCoordinates`
converts between display-local logical bounds and native physical task bounds;
the global topology remains the owner of display positions and work areas.

Workspace switching is compositor visibility, not task destruction. A display
move uses `SpatialEngine.moveObject` only when Android exposes multiple native
displays, preserving `NativeTaskSynchronizer` as the native bounds bridge. On
single-display Waydroid the selected display remains a persisted Zorx logical
location until a multi-display host is available.

## Taskbar workspace/display integration

`TaskbarWorkspaceDisplayModel` owns the persisted taskbar display policy and
active-display resolution. The Phase 6D view is a single primary taskbar: it
filters running objects by active workspace plus the policy display scope, while
the workspace switcher reacts through the workspace listener. Future `PER_DISPLAY`
and `MIRRORED` policies reuse this model without changing window ownership.

## Runtime lifecycle guardrails

Phase 6E makes destruction explicit at the asynchronous boundaries. A detached
`WidgetHost` unregisters its retained theme listener and cancels its periodic
clock refresh. `DesktopRuntime.destroy()` disposes `NativeTaskSynchronizer`,
which clears coalesced delayed bounds callbacks before the desktop event listener
is left behind. This prevents callbacks from applying native task bounds after an
activity/surface lifecycle has ended.
