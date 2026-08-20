# Code Guidelines

## Naming

Use meaningful class names.

Example:

SpatialEngine

DesktopRuntime

DesktopScene

---

## Architecture

UI must not directly modify the engine.

Always use:

Command

↓

Engine

↓

Event

↓

Renderer

---

## Git

Every feature must:

1. Build successfully
2. Pass tests
3. Be committed separately

---

## Formatting

- Keep methods short
- One responsibility per class
- Avoid Android dependencies in core modules