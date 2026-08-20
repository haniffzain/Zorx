# ZorxLauncher Code Guidelines

- Route desktop changes through SpatialEngine or an explicit lifecycle service.
- Keep hidden Android API integration inside the platform backend.
- Use task IDs or typed identities, not package names or encoded mutable metadata.
- Define spatial, registry and native effects for each lifecycle operation.
- Use one bounds path for move, resize, snap, maximize and restore.
- Close native, registry and spatial state coherently.
- Sort render input when z-order matters.
- Test spatial transitions and privileged native operations.
- Preserve stable Start Menu and App Drawer dismissal behavior.
- Run ./gradlew assembleDebug before proposing a commit.
- Commit, push and PR actions require separate authorization.
