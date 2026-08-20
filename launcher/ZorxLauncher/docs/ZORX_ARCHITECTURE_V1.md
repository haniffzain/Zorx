# Zorx Architecture v1.0

**Project:** Zorx  
**Version:** Alpha 0.3  
**Status:** In Development  
**Author:** Mohd. Haniff & Contributors

---

# 1. Vision

Zorx is a modern desktop environment built on top of Android.

Its purpose is to transform Android from a mobile operating system into a productive desktop operating system while maintaining Android application compatibility.

Zorx focuses on:

- Native desktop experience
- Freeform multi-window
- Modular architecture
- Modern user interface
- Event-driven communication
- Future AI integration

Zorx is not intended to imitate existing Android desktop launchers. Instead, it aims to become a complete desktop platform.

---

# 2. Core Principles

Every design decision in Zorx follows these principles.

## Simplicity

Every component should have one clear responsibility.

## Performance

The desktop environment must remain responsive and lightweight.

## Modularity

Components communicate through services and events rather than directly.

## Scalability

The architecture should support future desktop features without major redesign.

## Consistency

User interface components should follow a unified design language.

## Extensibility

Future modules should be easy to add without changing the existing architecture.

---

# 3. System Architecture

```
                  Zorx Desktop

                       │

                TaskbarView
                       ▲
                       │
               TaskbarController
                       ▲
                       │
                ZorxEventBus
                       ▲
                       │
               ZorxWindowService
                       ▲
                       │
               ZorxWindowManager
                       ▲
                       │
                WindowRegistry
                       ▲
                       │
            AndroidWindowBackend
                       ▲
                       │
                   Android 15
```

---

# 4. Core Components

## WindowRegistry

Responsible for storing every active desktop window.

Responsibilities:

- Register windows
- Remove windows
- Search windows
- Return active window list

---

## ZorxWindowManager

Provides the central API for managing desktop windows.

Responsibilities:

- Register windows
- Remove windows
- Retrieve windows

---

## ZorxWindowService

Acts as the service layer between the desktop environment and the window manager.

Responsibilities:

- Register new windows
- Publish window events
- Coordinate desktop updates

---

## ZorxEventBus

Global event communication layer.

Responsibilities:

- Register listeners
- Remove listeners
- Publish events
- Decouple system components

---

## AndroidWindowBackend

Platform-specific backend responsible for Android window operations.

Responsibilities:

- Locate Android tasks
- Resize tasks
- Move windows
- Future Android integration

---

# 5. Desktop Components

Current desktop modules include:

- Desktop
- Taskbar
- Start Menu
- Widgets
- Settings
- Recent Applications

Future modules:

- Notification Center
- Virtual Desktop
- Widget Engine
- AI Assistant

---

# 6. Window Lifecycle

```
Launch Application

↓

Create ZorxWindow

↓

Register Window

↓

Store In Registry

↓

Publish WindowOpenedEvent

↓

Refresh Desktop

↓

Refresh Taskbar
```

---

# 7. Event Driven Architecture

All communication should follow the event system.

```
AppManager

↓

ZorxWindowService

↓

ZorxEventBus

↓

TaskbarController

↓

TaskbarView
```

Components should avoid direct dependencies whenever possible.

---

# 8. Development Workflow

Every feature follows the same process.

```
Architecture

↓

Implementation

↓

Build

↓

Testing

↓

Commit

↓

Push
```

No feature is considered complete until it successfully builds and is committed to Git.

---

# 9. Current Progress

Completed

- Native Freeform Window Support
- Window Registry
- Window Manager
- Window Service
- Event Bus
- Android Window Backend
- Taskbar Controller
- Event Driven UI
- Zorx Branding

In Progress

- Desktop Integration
- Running Window Synchronisation

Upcoming

- Live Taskbar
- Window Focus
- Window Minimize
- Window Maximize
- Snap Layout
- Widget Engine
- Notification Center
- AI Integration

---

# 10. Long-Term Vision

Zorx aims to become a complete Android desktop platform with:

- Native desktop experience
- Multi-window environment
- Beautiful widget system
- AI-powered productivity
- Plugin architecture
- Modern design language
- Open developer ecosystem

---

# Motto

**Illuminate Possibilities**

Zorx is designed to bring clarity, productivity and creativity to Android desktop computing.