# Zorx Architecture v1

Status: in development. Verified baseline: development at b14b199.

AppManager launches and registers ZorxWindow through ZorxWindowService.
WindowOpenedEvent lets DesktopRuntime create a DesktopObject in SpatialEngine.
DesktopSurface sends input to WindowInteractionController. SpatialEngine events drive
rendering, taskbar refresh and, for move events, NativeTaskSynchronizer.

Implemented: freeform launch, synthetic registration, native task promotion attempt,
window painting, focus, drag, resize, minimize, maximize/restore, snap, taskbar
activation and native bounds forwarding for move events.

Required: authoritative task identity, native close, external task reconciliation,
native maximize/restore synchronization, multi-instance support, z-index rendering
and lifecycle tests. Stable Start Menu and App Drawer behavior must be preserved.
