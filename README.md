# 🎓 Advanced Student Management System

A Java Swing-based desktop application to manage student information with multi-role login (Admin, Teacher, Student), attendance, CGPA, fee tracking, dark mode, and CSV export functionality.

## ✅ Features

- 🔐 Role-based Login System:
  - **Admin**: Manage all student records, export data, update fee and attendance
  - **Teacher**: Update attendance, CGPA, and fee status
  - **Student**: View personal details, update email

- 📋 Student Management:
  - Add, edit, delete student data (Admin only)
  - Filter by department, year, and fee status
  - Real-time search by name, roll number, or email

- 🌙 Dark Mode:
  - Toggle between light and dark UI themes on login screen

- 📤 CSV Export:
  - Export student data to CSV (Admin only)

## 🧪 Test Login Credentials

| Role     | Username   | Password     |
|----------|------------|--------------|
| Admin    | `admin`    | `admin123`   |
| Teacher  | *Any name* | `teacher123` |
| Student  | *Exact name* | `student123` |

> Example Student Names: `Akila`, `Ramesh`, `Sowmya`, `Gokul`, etc.

---

## 🚀 Getting Started

### Prerequisites

- Java JDK 8 or higher
- Any Java IDE (IntelliJ, Eclipse) or terminal

### Run the Application

```bash
javac AdvancedStudentManagementSystem.java
java AdvancedStudentManagementSystem
