# RavenOS Recents & Freeform Integration Checkpoint

## Platform

- Android 15
- API Level 35
- Build type: userdebug
- Raven package: com.raven.launcher
- Raven appId observed: 10210
- Development branch: development

## Raven Recents Integration

RavenOS successfully replaces the default Android Recents component with:

com.raven.launcher/com.raven.launcher.recents.RecentsActivity

Runtime verification:

mRecentsUid=10210
mRecentsComponent=ComponentInfo{com.raven.launcher/com.raven.launcher.recents.RecentsActivity}

## Raven Recents Overlay

Overlay package:

com.raven.overlay.recents

The overlay overrides:

android:string/config_recentsComponentName

Runtime value:

com.raven.launcher/com.raven.launcher.recents.RecentsActivity

The Raven overlay successfully takes precedence over PixelConfigOverlayCommon.

## Privileged Permissions

RavenLauncher successfully receives:

android.permission.REAL_GET_TASKS: granted=true
android.permission.START_TASKS_FROM_RECENTS: granted=true
android.permission.MANAGE_ACTIVITY_TASKS: granted=true

MANAGE_ACTIVITY_TASKS uses the signature|recents protection level.

Making RavenLauncher the active Android Recents implementation allows Raven to receive this permission.

## Original Freeform Failure

RavenWindowManager previously reached WindowContainerTransaction.applyTransaction()
but Android rejected the transaction with:

SecurityException:
Permission Denial: applyTransaction() requires android.permission.MANAGE_ACTIVITY_TASKS

The required MANAGE_ACTIVITY_TASKS permission is now granted.

## Verified Architecture

RavenRecentsOverlay
        |
        v
config_recentsComponentName
        |
        v
Raven RecentsActivity
        |
        v
mRecentsUid = Raven UID (10210)
        |
        v
MANAGE_ACTIVITY_TASKS = granted
        |
        v
WindowContainerTransaction access

## Next Development Step

Retest RavenWindowManager.moveTaskToFreeform().

Verify:

1. WindowContainerTransaction.applyTransaction() succeeds.
2. MANAGE_ACTIVITY_TASKS SecurityException is gone.
3. Target task changes to FREEFORM windowing mode.
4. Raven-defined task bounds are applied.
5. Multiple Android applications can operate as independent desktop windows.
6. Window focus, resize, move, minimize, maximize and close behavior can then be implemented.

## Git Checkpoint

Previous implementation checkpoint:

80e55fd - Add Raven recents integration and freeform window support
