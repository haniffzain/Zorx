# Zorx Architecture v1

Status: in development. Updated through Phase 7D on 1 September 2026.

AppManager launches and registers ZorxWindow through ZorxWindowService.
WindowOpenedEvent lets DesktopRuntime create a DesktopObject in SpatialEngine.
DesktopSurface sends input to WindowInteractionController. SpatialEngine events drive
rendering, taskbar refresh and, for move events, NativeTaskSynchronizer.

Implemented: freeform launch, synthetic registration, authoritative native task
promotion, window painting, focus, constrained drag, full-edge resize, minimize,
maximize/restore, native close, taskbar activation, external reconciliation,
multi-instance identity, snap/group arrangements and responsive desktop grid.

Next: persistent launchable desktop icons using `GridEngine`, followed by expanded
grid persistence and runtime verification of privileged operations. Stable Start
Menu and App Drawer behavior must be preserved.
