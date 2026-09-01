# Zorx Architecture

Updated through Phase 7D on 1 September 2026.

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
Task promotion retains a stable presentation ID while assigning the authoritative
native task ID. Move, resize, snap, maximize and restore use one bounds pipeline;
native close and periodic external-task reconciliation clean both registry and
spatial state. Multiple tasks from one package are discovered independently.

DesktopSurface owns touch input and redraw scheduling. WindowInteractionController
implements focus, full-edge/corner resize, constrained drag, minimize,
maximize/restore, close, snap and group arrangements. The renderer package is
mostly placeholder code; active painting
is performed by DesktopCompositor and WindowPainter.

`GridEngine` owns taskbar-aware 12-column desktop placement geometry. Widgets keep
their legacy four-column persisted coordinates and map each unit to three shared
grid columns, allowing desktop icons to adopt the same collision model next.

Desktop and taskbar touches currently dismiss the App Drawer and Start Menu. This
stable behavior must remain unchanged during window lifecycle work.
