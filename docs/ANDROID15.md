# Android 15 Integration

Verified on development at b14b199.

## Confirmed

- Launcher replacement, desktop activity, Zorx Recents and overlay.
- Intended privileged permissions: START_TASKS_FROM_RECENTS,
  MANAGE_ACTIVITY_TASKS and REAL_GET_TASKS.
- ActivityOptions launch bounds and freeform windowing mode 5.
- Task discovery through ActivityManager.getRunningTasks in the target image.
- Bounds calls through IActivityTaskManager.resizeTask.
- Drag/resize/snap bounds forwarding after native task promotion.

## Limitations

- The backend relies on hidden/reflected APIs and privileged deployment.
- Normal launcher contexts may not expose running tasks.
- Synthetic IDs remain when native discovery fails.
- Native close, native minimize, external reconciliation and multi-instance identity
  are incomplete.
- Maximize/restore currently change spatial bounds without native synchronization.
- Android Gradle Plugin 8.5.0 warns that compileSdk 35 is newer than its tested
  compileSdk 34 support.

