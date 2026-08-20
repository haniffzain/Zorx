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
