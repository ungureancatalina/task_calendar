## About the Project

**Task Calendar** is a Java-based desktop application designed to help users organize their tasks efficiently. It provides a calendar interface for scheduling, viewing, and managing tasks.

The application uses a database for persistent storage and supports user authentication, allowing personalized task tracking. Each task can have additional notes and details for better organization.

---

## Technologies Used

| Tool             | Purpose                                |
|------------------|----------------------------------------|
| Java             | Core programming language              |
| JavaFX           | Graphical user interface               |
| SQLite           | Embedded database                      |
| JDBC             | Database connectivity                  |
| SceneBuilder     | UI layout editor (optional)            |

---

## Features

- Visual calendar for scheduling tasks
- Persistent task storage using SQLite
- User login system for personal task management
- Add, edit, and delete tasks
- Add detailed notes for each task
- Clean and intuitive interface built with JavaFX

---

## Project Structure

```
task_calendar/
├── controller/        -> JavaFX controllers for UI interaction
├── database/          -> SQLite database file and DB access logic
├── model/             -> Task and user data classes
├── service/           -> Business logic
├── utils/             -> Helper methods (e.g. date, input checks)
├── resources/         -> FXML layouts, icons
└── Main.java          -> Entry point
```

---

## How to Run

### Requirements

- Java 17+
- JavaFX SDK (set in your IDE)
- An IDE like IntelliJ IDEA or VS Code

### Steps

1. Clone the repository:
```bash
git clone https://github.com/ungureancatalina/task_calendar
cd task_calendar
```

2. Make sure JavaFX is configured in your IDE:
   - Add it to libraries/dependencies
   - Set VM options:
     ```
     --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml
     ```

3. Run `Main.java` to launch the application.
