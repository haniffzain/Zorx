# ZorxOS Recents & Freeform Integration Checkpoint

## 20 August 2026 follow-up

The taskId resize path is now invoked by AndroidWindowBackend and coalesced move
bounds are forwarded after task promotion. Native close, maximize/restore sync,
external task reconciliation and multi-instance identity remain pending.

## Platform

- Android 15
- API Level 35
- Build type: userdebug
- Zorx package: com.zorx.launcher
- Zorx appId observed: 10210
- Development branch: development

## Zorx Recents Integration

ZorxOS successfully replaces the default Android Recents component with:

com.zorx.launcher/com.zorx.launcher.recents.RecentsActivity

Runtime verification:

mRecentsUid=10210
mRecentsComponent=ComponentInfo{com.zorx.launcher/com.zorx.launcher.recents.RecentsActivity}

## Zorx Recents Overlay

Overlay package:

com.zorx.overlay.recents

The overlay overrides:

android:string/config_recentsComponentName

Runtime value:

com.zorx.launcher/com.zorx.launcher.recents.RecentsActivity

The Zorx overlay successfully takes precedence over PixelConfigOverlayCommon.

## Privileged Permissions

ZorxLauncher successfully receives:

android.permission.REAL_GET_TASKS: granted=true
android.permission.START_TASKS_FROM_RECENTS: granted=true
android.permission.MANAGE_ACTIVITY_TASKS: granted=true

MANAGE_ACTIVITY_TASKS uses the signature|recents protection level.

Making ZorxLauncher the active Android Recents implementation allows Zorx to receive this permission.

## Original Freeform Failure

ZorxWindowManager previously reached WindowContainerTransaction.applyTransaction()
but Android rejected the transaction with:

SecurityException:
Permission Denial: applyTransaction() requires android.permission.MANAGE_ACTIVITY_TASKS

The required MANAGE_ACTIVITY_TASKS permission is now granted.

## Verified Architecture

ZorxRecentsOverlay
        |
        v
config_recentsComponentName
        |
        v
Zorx RecentsActivity
        |
        v
mRecentsUid = Zorx UID (10210)
        |
        v
MANAGE_ACTIVITY_TASKS = granted
        |
        v
WindowContainerTransaction access

## Next Development Step

Retest ZorxWindowManager.moveTaskToFreeform().

Verify:

1. WindowContainerTransaction.applyTransaction() succeeds.
2. MANAGE_ACTIVITY_TASKS SecurityException is gone.
3. Target task changes to FREEFORM windowing mode.
4. Zorx-defined task bounds are applied.
5. Multiple Android applications can operate as independent desktop windows.
6. Window focus, resize, move, minimize, maximize and close behavior can then be implemented.

## Git Checkpoint

Previous implementation checkpoint:

80e55fd - Add Zorx recents integration and freeform window support
