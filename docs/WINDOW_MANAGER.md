# Zorx Window Manager

Updated through Phase 7D on 1 September 2026.

## Current lifecycle

1. AppManager launches an activity with freeform bounds and windowing mode 5.
2. A synthetic presentation ID is registered while native discovery is pending.
3. WindowOpenedEvent causes DesktopRuntime to create a DesktopObject.
4. AppManager snapshots existing package tasks, discovers only the new task and
   promotes the matching window to its authoritative native task ID.
5. Drag, resize and snap emit DesktopMovedEvent.
6. NativeTaskSynchronizer coalesces updates at 32 ms and resizes the native task.
7. Maximize and restore use the same bounds pipeline; close removes the native
   task and exact registry/spatial state.
8. Periodic reconciliation removes tasks closed outside Zorx after two misses.

## Verified

- Native freeform launch and initial bounds.
- Synthetic-to-native task promotion when discovery succeeds.
- Focus, constrained title-bar drag and all-edge/corner resize.
- Spatial minimize, taskbar restore, maximize/restore and snap arrangements.
- Native bounds forwarding for all geometry-changing operations.
- Distinct multi-instance identity, z-index rendering and taskbar focus refresh.

## Partial or pending

- Native minimize/focus operations are not implemented.
- Privileged native close and reconciliation still require Waydroid/device smoke
  verification even though their code paths and unit/build checks are complete.

## Next milestone

Desktop icons: launchable, persistent shortcuts placed through the shared grid
without regressing window lifecycle or widget behavior.
