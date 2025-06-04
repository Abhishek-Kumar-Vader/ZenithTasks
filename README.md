
# ZenithTasks

ZenithTasks is a modern Android application designed to help users manage their tasks efficiently. Built using Kotlin and Android Jetpack components, it emphasizes modular architecture, scalability, and maintainability.

## 🧭 Project Overview

ZenithTasks aims to provide a seamless task management experience with features like task creation, editing, deletion, and robust organization. The app leverages modern Android development practices to ensure a robust and user-friendly interface.

## ✨ Features

-   **Comprehensive Task Management**: Create, edit, and delete tasks with ease.
-   **Task Status Management**: Organize tasks into distinct statuses like To-Do, In-Progress, Done, Cancelled, and Archived.
-   **Priority Levels**: Assign priorities (Urgent, High, Medium, Low) to tasks for effective prioritization.
-   **Advanced Search Functionality**: Quickly find tasks using the search feature, with added support for priority filtering.
-   **User-Friendly Interface**: Intuitive UI/UX for a seamless experience.

## 🏗️ Architecture

The project follows a modular architecture to enhance scalability and maintainability. Key architectural components include:

-   **MVVM Pattern**: Separates concerns for better code management.
-   **Jetpack Components**: Utilizes LiveData/Flow, ViewModel, Room, and Navigation components.
-   **Dependency Injection**: Implemented using Hilt for efficient dependency management.
-   **Modularization**: Divides the app into distinct modules for features, core functionalities, and utilities. (Ensure this matches your actual module setup. If `category` and `domain` are not distinct modules yet, consider adjusting the `Project Structure` section accordingly).

## 🛠️ Tech Stack

-   **Language**: Kotlin
-   **Build System**: Gradle
-   **Database**: Room
-   **UI**: Jetpack Compose
-   **Dependency Injection**: Hilt
-   **Architecture Components**: LiveData, ViewModel, Navigation

## 🚀 Getting Started

### Prerequisites

-   Android Studio Arctic Fox or later
-   Kotlin 1.5+
-   Gradle 7.0+
-   Android SDK 21+

### Installation

1.  **Clone the repository**:

    ```bash
    git clone [https://github.com/Abhishek-Kumar-Vader/ZenithTasks.git](https://github.com/Abhishek-Kumar-Vader/ZenithTasks.git)
    ```

2.  **Open in Android Studio**:

    -   Open Android Studio.
    -   Click on `Open an existing project`.
    -   Navigate to the cloned repository and select it.

3.  **Build the project**:

    -   Let Gradle sync and build the project.
    -   Run the app on an emulator or physical device.


## 📁 Project Structure

```
ZenithTasks/
├── app/                # Main application module
├── core/               # Core functionalities and utilities
├── features/           # Feature-specific modules
│   ├── task/           # Task management feature
│   └── category/       # Category management feature
├── data/               # Data layer (Room database, repositories)
├── domain/             # Business logic and use cases
└── utils/              # Utility classes and extensions
```
*(**Note:** Please verify this project structure against your actual file system and adjust if some modules like `core`, `category`, or `domain` are not fully implemented or are named differently.)*

## 🤝 Contributing

Contributions are welcome! If you have any ideas, suggestions, or bug reports, please open an issue or submit a pull request

## 🧪 Testing

- **Unit Tests**: Located in the `test/` directories of respective modules.
- **Instrumentation Tests**: Located in the `androidTest/` directories.

To run tests:

```bash
./gradlew test
./gradlew connectedAndroidTest
```
