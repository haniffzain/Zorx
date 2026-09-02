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

Native task state and spatial state remain separate by responsibility but share an
explicit identity and lifecycle contract. Promotion records the authoritative task
ID, geometry operations use one synchronization path, native close cleans exact
state and reconciliation handles external removal. Stable Start Menu and App Drawer
dismissal behavior remains unchanged.

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

## Desktop wallpaper layer

`WallpaperView` is inserted before `WidgetHost` in the desktop frame, making it
the non-interactive lowest layer. `ZorxWallpaperManager` owns the wallpaper
model, persisted scope/assignment and decoded source-bitmap cache; neither the
activity nor views read wallpaper preferences directly. Wallpaper assignment is
keyed by workspace and optional display identity, allowing later per-display
surfaces without conflating wallpaper scaling with shell UI scale.

This is a static wallpaper renderer, not Android Live Wallpaper. `WallpaperView`
listens to both manager and workspace changes; `WallpaperSelectionPolicy` owns
global/workspace precedence, and the bitmap cache re-decodes when a larger target
is requested. A future live implementation requires an Android `WallpaperService`
engine and explicit manifest/provider lifecycle integration.

## Tabbed settings surface

Phase 6G keeps `ZorxSettingsActivity` as the only display, appearance and
wallpaper settings owner. A fixed window shell surrounds four replaceable tab
panels: Display, Scale, Appearance and Wallpaper. Each panel may own its own
scroll container, while the draggable titlebar, tab navigation and action footer
remain outside scrolling content. Existing desktop entry points select a tab
through `EXTRA_SECTION`; they do not create parallel settings activities.

All controls continue to write through `ZorxShellSettingsStore`,
`ZorxThemeManager` or `ZorxWallpaperManager`, preserving the existing live
listener notifications and persistence boundaries.

## Window organization and desktop grid

The interaction controller delegates pure geometry to resize/work-area and snap
engines, then sends final bounds through `SpatialEngine`. Multi-window arrangements
select visible, non-minimized active-workspace objects by z-order and use the same
path, preserving native synchronization.

`GridEngine` is the shared responsive desktop-placement authority. It divides the
usable area above the taskbar into 12 columns and eight rows, distributes remainder
pixels deterministically and exposes bounds, snapping, collision and first-free
placement. Legacy four-column widget records map to three columns per unit.

`DesktopShortcutHost` renders persisted launcher-component shortcuts on that grid
between widgets and the window surface. `DesktopShortcutStore` owns component and
placement persistence; launches remain delegated to `AppManager`, so shortcuts use
the same freeform task lifecycle as the drawer and Start Menu.

`DesktopPlacementPolicy` is the cross-object occupancy boundary. Widget operations
translate legacy four-column records into shared-grid spans before checking shortcut
reservations; shortcut operations reserve visible widget spans. Reconciliation is
ordered and deterministic, moving conflicting shortcuts to the first free cell
without rewriting backward-compatible widget persistence.

`DesktopGridSettingsStore` owns the selected density profile. Both desktop hosts
derive their `DesktopGridSpec` from it, preventing visual and collision geometry
from drifting apart. Profiles retain 12 columns for legacy widget compatibility;
row count, gap and padding may vary, and shortcut reconciliation handles reductions.

Every widget and shortcut record also carries `workspaceId` and `displayId`.
`DesktopLayoutScopeResolver` selects the active logical workspace and current
primary surface; both hosts filter rendering and occupancy to that exact pair while
stores retain records from inactive scopes. Legacy records decode into Workspace 1
and `primary`. Physical per-display view creation remains a later topology concern.

`DesktopLayoutMobilityManager` coordinates cross-workspace copy/move. Its pure
planner receives source objects, destination reservations and grid limits, then
produces a complete placement map or no plan. Persistence begins only after a full
plan exists. Widget identities are regenerated for copy and retained for move;
shortcut component identity remains scope-local. Display targets remain gated on
real per-display desktop surfaces.

Preflight is all-or-nothing, but persistence spans separate SharedPreferences stores;
it is not a crash-atomic transaction. Recovery/journaling is future work and must not
be inferred from a successful capacity plan or CI build.
