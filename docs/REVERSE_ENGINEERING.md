# Android 15 Reverse Engineering Notes

Confirmed

ActivityOptions

android.activity.windowingMode

WINDOWING_MODE_FREEFORM = 5

--------------------------------------------------

Permissions

MANAGE_ACTIVITY_TASKS

START_TASKS_FROM_RECENTS

REAL_GET_TASKS

--------------------------------------------------

Framework

ActivityTaskManager

IActivityTaskManager

ServiceManager

activity_task

--------------------------------------------------

Confirmed

Native freeform launch works.

Desktop remains alive behind freeform applications.

Recents replacement successful.

--------------------------------------------------

Investigated

resizeTask()

TRANSACTION_resizeTask = 47

Reflection blocked by hidden API restrictions.

Binder service accessible.

Pending implementation through native framework integration.
