# Zorx Window Manager

Verified on development at b14b199.

## Current lifecycle

1. AppManager launches an activity with freeform bounds and windowing mode 5.
2. A synthetic negative task ID is registered as a ZorxWindow.
3. WindowOpenedEvent causes DesktopRuntime to create a DesktopObject.
4. AppManager polls for the native task and promotes the registry task ID.
5. Drag, resize and snap emit DesktopMovedEvent.
6. NativeTaskSynchronizer coalesces updates at 32 ms and resizes the native task.
7. Minimize, restore, maximize and close currently update the spatial UI model.

## Verified

- Native freeform launch and initial bounds.
- Synthetic-to-native task promotion when discovery succeeds.
- Focus, title-bar drag, bottom-right resize and edge/corner snap.
- Spatial minimize, taskbar restore and maximize/restore.
- Native bounds forwarding for DesktopMovedEvent.

## Partial or pending

- Close removes only the desktop object, not the native task or registry entry.
- Maximize/restore emit state events, so native bounds are not synchronized.
- ZorxWindow and DesktopObject state can diverge.
- Externally closed native tasks are not reconciled.
- Package lookup is ambiguous for multiple tasks from one application.
- Desktop IDs retain synthetic IDs after registry promotion.
- Native minimize/focus operations are not implemented.

## Next milestone

Native Window Lifecycle Unification: one task-based identity, explicit native/spatial
mapping, native close and reconciliation, and one bounds path for move, resize,
snap, maximize and restore.
