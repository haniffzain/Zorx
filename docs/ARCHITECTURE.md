# Zorx Architecture

Verified against development at b14b199 on 20 August 2026.

## Runtime topology

DesktopActivity owns DesktopSurface, AppDrawerView, StartMenuView and TaskbarView.
DesktopSurface owns DesktopRuntime, WindowInteractionController and DesktopScene.
DesktopRuntime owns SpatialEngine and NativeTaskSynchronizer. Rendering flows through
DesktopScene, DesktopCompositor and WindowPainter. Native bounds operations flow
through NativeTaskSynchronizer and AndroidWindowBackend.

## State ownership

- ZorxWindow and WindowRegistry track Android task identity and launch metadata.
- DesktopObject and SpatialEngine track bounds, focus, z-order and desktop state.
- ZorxEventBus bridges both layers.

WindowOpenedEvent creates a desktop object. DesktopMovedEvent is forwarded to
NativeTaskSynchronizer, which coalesces updates and calls AndroidWindowBackend.
This is not yet a single source of truth: native close, external task removal,
maximize/restore synchronization and multi-instance identity remain pending.

DesktopSurface owns touch input and redraw scheduling. WindowInteractionController
implements focus, drag, bottom-right resize, minimize, maximize/restore, close and
edge/corner snap. The renderer package is mostly placeholder code; active painting
is performed by DesktopCompositor and WindowPainter.

Desktop and taskbar touches currently dismiss the App Drawer and Start Menu. This
stable behavior must remain unchanged during window lifecycle work.
