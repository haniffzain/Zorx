# Zorx Master Architecture

## Vision

Zorx is designed as a desktop platform rather than a launcher.

---

# High Level Architecture

Zorx

├── Core

├── Runtime

├── Desktop

├── Rendering

└── Android Platform

---

## Core

Responsible for:

- Spatial Engine
- Event Bus
- Commands
- Repository

---

## Runtime

Responsible for:

- Desktop Runtime
- Session
- Future Services

---

## Desktop

Responsible for:

- Desktop Surface
- Desktop Scene
- Taskbar
- Start Menu
- Widgets

---

## Rendering

Responsible for:

- Desktop Compositor
- Window Painter
- Future GPU renderer

---

## Platform

Responsible for:

- Android integration
- Window backend
- Activity lifecycle