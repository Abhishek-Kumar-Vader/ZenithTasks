
# ZenithTasks

ZenithTasks is a modern Android application designed to help users manage their tasks efficiently. Built using Kotlin and Android Jetpack components, it emphasizes modular architecture, scalability, and maintainability.

## 🧭 Project Overview

ZenithTasks aims to provide a seamless task management experience with features like task creation, editing, deletion, and categorization. The app leverages modern Android development practices to ensure a robust and user-friendly interface.

## ✨ Features

- **Task Management**: Create, edit, and delete tasks with ease.
- **Categorization**: Organize tasks into categories for better clarity.
- **Reminders**: Set reminders to stay on top of your tasks.
- **Search Functionality**: Quickly find tasks using the search feature.
- **User-Friendly Interface**: Intuitive UI/UX for a seamless experience.

## 🏗️ Architecture

The project follows a modular architecture to enhance scalability and maintainability. Key architectural components include:

- **MVVM Pattern**: Separates concerns for better code management.
- **Jetpack Components**: Utilizes LiveData, ViewModel, Room, and Navigation components.
- **Dependency Injection**: Implemented using Dagger/Hilt for efficient dependency management.
- **Modularization**: Divides the app into distinct modules for features, core functionalities, and utilities.

## 🛠️ Tech Stack

- **Language**: Kotlin
- **Build System**: Gradle
- **Database**: Room
- **UI**: Jetpack Compose / XML (based on implementation)
- **Dependency Injection**: Dagger / Hilt
- **Architecture Components**: LiveData, ViewModel, Navigation

## 🚀 Getting Started

### Prerequisites

- Android Studio Arctic Fox or later
- Kotlin 1.5+
- Gradle 7.0+
- Android SDK 21+

### Installation

1. **Clone the repository**:

   ```bash
   git clone https://github.com/Abhishek-Kumar-Vader/ZenithTasks.git
   ```

2. **Open in Android Studio**:

   - Open Android Studio.
   - Click on `Open an existing project`.
   - Navigate to the cloned repository and select it.

3. **Build the project**:

   - Let Gradle sync and build the project.
   - Run the app on an emulator or physical device.

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

## 🧪 Testing

- **Unit Tests**: Located in the `test/` directories of respective modules.
- **Instrumentation Tests**: Located in the `androidTest/` directories.

To run tests:

```bash
./gradlew test
./gradlew connectedAndroidTest
```

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
