# Task Manager (Android App)

Task Manager is an Android application designed to help users manage daily tasks efficiently with a focused, clean workflow.

## Features

### Top App Bar
- Displays the app title
- Settings icon to navigate to the Settings screen

---

## Main Screens

### Task Manager
The main screen uses a **Tab Layout** with three tabs:

#### 1. Task Tab
- Displays the **current task** for the day
- Shows:
  - Task title
  - Task description
  - From–To time
- Actions:
  - **Add Report**
    - Attach files (images, etc.)
    - Add final text report for the task
  - **Mark as Done**
    - Marks the current task as completed
    - Automatically loads the next task of the day
    - If no tasks remain, shows a completed screen with the all the tasks

---

#### 2. All Tab
- Shows **all tasks for the current day**
- Tasks are displayed in a scrollable list
- Completed tasks:
  - Greyed out
  - Moved to the bottom

---

#### 3. Calendar Tab
- Displays a calendar for:
  - Selecting a date shows all tasks for that day
- Floating Action Button (**+**):
  - Create or edit tasks
  - Enter title, description, and time details

---

## Settings
- Color theme customization
- Import user data
- Export user data

---

## Tech Stack
- Android
- Kotlin
- Jetpack Compose
- Material Design

---

## Status
- Completed
