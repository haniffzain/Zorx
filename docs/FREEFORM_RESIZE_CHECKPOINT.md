# ZorxOS Freeform Resize Checkpoint

## Status

Zorx Launcher is successfully registered as the Android Recents provider.

Verified:

- Zorx Recents UID is active.
- Zorx RecentsActivity is the configured recents component.
- START_TASKS_FROM_RECENTS granted.
- MANAGE_ACTIVITY_TASKS granted.
- REAL_GET_TASKS granted.
- Applications can be launched directly in Android freeform mode.
- Android Settings successfully launches as a native freeform task.

## Native Freeform Test

Settings task was verified as:

- Package: com.android.settings
- Windowing mode: freeform
- supportsMultiWindow: true
- isResizeable: true

Initial bounds:

    Rect(300, 150 - 1800, 1250)

Native resize command:

    adb shell cmd activity task resize 44 500 250 1700 1050

Result:

    Rect(500, 250 - 1700, 1050)

The resize completed successfully and WindowManager generated a CHANGE transition.

## Android 15 Framework Findings

ActivityTaskManager exposes:

    ActivityTaskManager.getService()

which returns:

    IActivityTaskManager

IActivityTaskManager contains:

    resizeTask(int taskId, Rect bounds, int flags)

Framework signature:

    (ILandroid/graphics/Rect;I)V

The third parameter is treated as flags.

## Current Architecture

Zorx Launcher
    |
    +-- launches Android applications directly in FREEFORM mode
    |
    +-- Zorx Recents provider
    |
    +-- privileged task-management permissions
    |
    +-- native Android freeform tasks
            |
            +-- taskId
            +-- bounds
            +-- resize capability

## Next Development Step

Implement ZorxWindowManager task movement/resizing using the Android
ActivityTaskManager / IActivityTaskManager resizeTask path.

Target features:

1. Move native freeform windows.
2. Resize native freeform windows.
3. Drag window title bar.
4. Resize from window edges/corners.
5. Maximize.
6. Restore.
7. Minimize/taskbar integration.

## Important

Do not return to the previous TaskInfo.getToken() reflection approach.

The previous WindowContainerToken/WCT experiment failed because hidden
TaskInfo token access was blocked at runtime.

The proven working path is taskId-based native task resizing.
