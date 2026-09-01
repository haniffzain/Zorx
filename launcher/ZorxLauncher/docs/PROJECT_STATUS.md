# Project Status

Build-verified on development after Phase 6G on 21 August 2026. The updated APK installs and launches in Waydroid without an observed startup crash; automated visual tab traversal remains unavailable on this host.

## Build

GitHub Actions verifies unit tests and `assembleDebug` with Android Gradle Plugin
8.8.2, Gradle 8.10.2, JDK 17 and compileSdk 35, then publishes the debug APK.

## Completed

- Runtime, spatial engine, repository and event pipeline.
- Desktop surface, scene, compositor and window painter.
- Native freeform launch and synthetic/native task registration path.
- Focus, drag, bottom-right resize and edge/corner snap UI.
- Spatial minimize, maximize/restore and taskbar restore.
- Coalesced native bounds synchronization for move events.
- Start Menu and App Drawer integration with stable dismissal behavior.
- Phase 4C: persistent centralized typography controls and live shell updates.
- Phase 4D: independent application/taskbar icon sizing and centered responsive floating App Drawer.
- Phase 4E: persistent resolution-aware display scale, effective workspace metrics and display preview.
- Phase 4F: persistent live Theme Engine with ZorxColors compatibility tokens and initial presets.
- Phase 5A: persistent Widget Engine foundation and theme-aware Clock widget.
- Phase 5B: Widget Edit Mode with logical grid drag/snap and persistent layout lock.
- Phase 5C: selected-widget toolbar, logical resize, duplicate/remove and collision-safe placement.
- Phase 5D: Widget Library v1 — Calendar, System Monitor, Network and Quick Controls registered with shared grid/persistence/theme support. Runtime data availability remains platform-dependent and visual testing is deferred until after Phase 6.
- Phase 5E: Weather and Media provider abstractions plus persistent per-instance Notes widget configuration. Weather/media providers remain unconfigured until platform integration.
- Phase 6A: four persistent virtual workspaces with logical window membership and compositor visibility. Widgets remain global in this foundation phase; runtime visual testing is deferred until Phase 6E.
- Phase 6B: persistent multi-monitor topology data model with per-display scale, work area, logical primary and widget display migration. Waydroid currently exposes one display; host arrangement and cross-display movement remain future work.
- Phase 6C: persistent window location model (workspace, display, logical bounds, state and z-order), workspace/display move actions and centralized logical-to-native coordinate conversion. Logical moves preserve task identity and use the existing native bounds bridge when more than one native display is available.
- Phase 6D: compact taskbar workspace switcher, current-workspace/current-display running-window filtering, focused/minimized states and taskbar-item move actions.
- Phase 6E: static stability pass, lifecycle/listener cleanup and build verification. Live Waydroid runtime verification could not run on this host because neither Waydroid nor ADB is installed and the available WSL distribution is stopped.
- Phase 6F: centralized desktop wallpaper system with built-in Zorx gradient, user document URI, solid color, five rendering modes, live desktop layer and persisted all/current-workspace assignments.
- Phase 6G — Tabbed Display Settings UI — Complete. The floating settings window uses fixed title, tab and footer regions with `DISPLAY`, `SCALE`, `APPEARANCE` and `WALLPAPER` content panels.

## Partial

- Native discovery/promotion depends on platform visibility.
- Native close and exact task cleanup are implemented but still require privileged
  Waydroid/device verification.
- External native task removal is reconciled by task ID after two consecutive
  missing-task observations; privileged runtime verification remains pending.
- New launches snapshot the package's existing task IDs and promote only a newly
  created task. Launch requests use Android's multiple-task/document flags, while a
  process-wide launch guard prevents ambiguous concurrent launches of one package.

## Native lifecycle update

- Desktop window objects now retain their Android task identity independently of
  their stable presentation ID.
- Synthetic-to-native promotion updates the matching desktop object through an
  explicit event, so bounds updates and close operations target the promoted task.
- Maximize/restore use the same native bounds synchronization path as move/resize.
- Close calls the native task backend and unregisters the exact task. A late native
  discovery after a synthetic window was closed removes the orphaned native task.
- Compositor rendering is sorted by z-index and taskbar state refreshes on focus.
- Periodic reconciliation removes desktop/registry state when Android closes a task
  externally. Two consecutive misses avoid reacting to transient task visibility.

## Phase 7A full-edge resize and work area

- Window hit testing supports all four edges and four corners while preserving the
  titlebar controls and drag region.
- Resize keeps the opposite edge fixed, enforces a 240×180 minimum and clamps every
  result to the usable desktop area above the taskbar.
- Titlebar dragging is constrained to the same work area, and edge/corner snap uses
  work-area bounds instead of the full surface.
- Geometry is platform-independent and covered by unit tests for handle detection,
  minimum size, work-area clamping and opposite-edge behavior.

## Phase 7B snap chooser foundation

- The desktop context menu exposes a focused-window Snap submenu with left/right
  halves and all four quarter placements.
- `SnapLayoutEngine` owns deterministic, taskbar-aware slot geometry and preserves
  remainder pixels on odd-sized work areas without gaps or overlap.
- Chooser actions flow through `SpatialEngine.moveObject`, so native bounds
  synchronization uses the same lifecycle pipeline as drag and resize.

## Phase 7C multi-window arrangements

- The focused-window context menu exposes an Arrange submenu for two columns,
  three columns and a main-left/two-right composition.
- Arrangements select the highest-z visible non-minimized windows from the active
  workspace and reject layouts when too few windows are available.
- Group geometry preserves odd remainder pixels and routes every bounds update
  through `SpatialEngine`, retaining native synchronization.

## Phase 7D desktop grid engine

- `GridEngine` provides a configurable 12-column, eight-row desktop grid with
  deterministic remainder distribution, responsive cell sizing, gaps and padding.
- The engine exposes span bounds, nearest-cell snapping, bounds validation,
  collision detection and first-available placement.
- Existing widget persistence remains backward compatible: each legacy 4-column
  widget unit maps to three desktop-grid columns while rendering and drag snapping
  now use the shared responsive grid above the taskbar.

## Current milestone

Phase 6E is build-verified. Runtime visual testing remains pending a Waydroid-capable host; it has not been represented as completed.

## Phase 6F wallpaper system

- `ZorxWallpaperManager` is the sole wallpaper persistence/loading and listener authority. Wallpaper renders in `WallpaperView`, below widgets and desktop windows, without hit testing.
- Supported sources: built-in Zorx gradient, persisted user image URI, and solid color. User images use `ACTION_OPEN_DOCUMENT` with persisted read access where the provider supports it; missing/revoked/decode-failed images fall back to the selected solid color.
- Supported modes: `FILL`, `FIT`, `STRETCH`, `CENTER`, and `TILE`. Bitmap decoding first reads dimensions, then uses sampling against current target bounds and retains only the current source bitmap cache.
- Scope is persisted as all workspaces or current workspace. Assignment keys already include workspace plus optional display identity, preparing per-display wallpaper without inventing additional Waydroid display surfaces.
- Slideshow, dynamic/live wallpapers and physical multi-display rendering are intentionally deferred. Runtime picker/render testing is pending Waydroid/ADB availability.

## Phase 6G tabbed settings UI

- The existing `ZorxSettingsActivity` remains the single settings surface. Its compact floating window is centered at a responsive 840dp by 620dp target, constrained to the current display, draggable, maximizable and styled by the current window-corner preference and Theme Engine tokens.
- Tabs are `DISPLAY`, `SCALE`, `APPEARANCE` and `WALLPAPER`. Only the selected tab is mounted. The titlebar, tab strip and `Reset` / `Apply` / `Close` footer stay fixed; scrolling is confined to the selected content panel when required.
- `DISPLAY` contains the topology preview and compact monitor cards. `SCALE` contains display/UI scale and physical/effective resolution. `APPEARANCE` uses responsive two-column groups for shell size, icons, typography, shape and theme. `WALLPAPER` keeps its preview, source, five modes and workspace scope in a compact layout.
- Desktop `Display Settings`, `Personalization` and `Change Wallpaper` actions route to `DISPLAY`, `APPEARANCE` and `WALLPAPER` respectively through the existing activity intent extra. The active tab survives activity state recreation.
- Display scale, shell metrics, icon sizing, typography, shapes, theme and wallpaper retain their existing live persistence paths. Appearance is the only tab expected to need internal vertical scrolling at the default window size; no whole-window scrolling is used.
- Waydroid accepted the rebuilt APK and launched Zorx without an observed startup crash. Direct tab mappings are statically verified at their desktop call sites; full visual tab switching and persistence interaction remain a manual smoke test because the host denies non-root access to the Waydroid activity shell.

## Phase 6C coordinate and platform notes

- `SpatialEngine` continues to own the single in-memory window object and native Android task identity.
- `ZorxWindowLocationManager` persists a display-local logical rectangle; `ZorxDisplayCoordinates` is the only Phase 6C boundary that converts it to native physical task pixels.
- Moving a maximized window targets the new display work area. Minimized windows retain their mapping without forced restore.
- Current Waydroid exposure is normally one Android display. In that configuration, a move-to-display updates only the logical Zorx mapping; it does not fabricate a host-level native move. Workspace moves are immediately reflected by compositor visibility.

## Phase 6D taskbar behavior

- The taskbar displays workspace buttons 1–4: active uses the accent, a workspace with windows uses a subtle surface/border indicator, and empty workspaces are muted.
- The running strip is filtered to the active workspace and display scope. Default persisted policy is `PRIMARY_ONLY`; `PER_DISPLAY` and `MIRRORED` are represented in the model for later multi-taskbar rendering.
- Active display resolves from the focused window's persisted display location, otherwise the logical primary display. The single visible Phase 6D taskbar remains the primary taskbar.
- Long-pressing a running-window item opens desktop-style Move to Workspace / Move to Display actions. Horizontal scrolling prevents running items from overlapping in narrow taskbars.

## Phase 6E verification and fixes

### Completed on this host

- Branch/remote synchronization, diff check and a clean debug APK build.
- Static audit of workspace, display, taskbar, widget and runtime listener lifecycles.
- `WidgetHost` now unregisters its Theme Engine listener on detachment and stops its clock runnable.
- `NativeTaskSynchronizer` now cancels coalesced delayed native-bounds work when `DesktopRuntime` is destroyed, preventing stale callbacks against a disposed desktop.

### Pending live Waydroid verification

- Workspace switching, task/taskbar interactions, App Drawer/Start Menu dismissal, widget editing, persistence after restart and logcat inspection.
- Real native multi-display task movement and scale conversion. The current Windows environment has no `waydroid` or `adb` executable; WSL Ubuntu is stopped.
