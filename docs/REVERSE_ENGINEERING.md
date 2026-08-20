# Android 15 Reverse Engineering Notes

## 20 August 2026 implementation status

The reflected IActivityTaskManager.resizeTask path is implemented. It still depends
on privileged deployment, hidden APIs and task visibility. Synthetic fallback,
native close, external reconciliation and maximize/restore sync remain.

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
