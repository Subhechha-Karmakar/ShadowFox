# 🎓 Student Information System

A modern, dark-themed **desktop application** built with Java Swing for managing student records. Features a premium UI with sidebar navigation, real-time GPA calculation, analytics charts, and PDF report export.

---

## 📸 Features

| Feature | Description |
|---|---|
| **Add / Edit Students** | Full-featured form with real-time GPA preview, input validation, and personal details |
| **Records Table** | Sortable, searchable table displaying all student data |
| **Analytics Dashboard** | JavaFX-powered pie chart showing pass/fail distribution and class statistics |
| **PDF Export** | Professionally formatted PDF reports via iText 7 (landscape A4, styled tables) |
| **Dark Theme** | Premium GitHub-inspired dark mode with Nimbus Look & Feel |
| **Live GPA Preview** | Auto-calculated GPA (4.0 scale) updates as you type marks |
| **Input Validation** | Numeric, decimal, phone, email, and DOB format filters |

---

## 🏗️ Architecture

The project follows the **MVC (Model-View-Controller)** pattern:

```
src/com/studentmgmt/
├── App.java                        # Entry point — installs theme, launches UI
├── model/
│   └── Student.java                # Data model with GPA calculation
├── controller/
│   └── StudentController.java      # Business logic & in-memory CRUD
├── view/
│   ├── StudentView.java            # Main window (sidebar + card layout)
│   ├── StudentFormPanel.java       # Add/Edit form with validation
│   ├── StudentTablePanel.java      # Records table view
│   ├── ChartPanel.java             # Analytics card with stats
│   ├── PieChartPanel.java          # JavaFX pie chart (pass/fail)
│   ├── MainFrame.java              # Frame utilities
│   └── ThemeManager.java           # Dark theme colors, fonts & component factory
└── util/
    ├── PdfExporter.java            # iText 7 PDF generation
    └── ValidationUtils.java        # Input filters & validators
```

---

## 💾 Data Storage (No External Database Required)

> **This application uses in-memory storage — no database setup is needed.**

All student records are stored in a `java.util.ArrayList<Student>` inside `StudentController.java`. Data persists only for the duration of the application session.

**What this means:**
- ✅ **No database installation** (no MySQL, PostgreSQL, SQLite, etc.)
- ✅ **No connection strings** or configuration files to edit
- ✅ **No schema migration** or table creation needed
- ✅ **Zero setup** — just compile and run
- ⚠️ **Data is not persisted** — all records are lost when the application is closed

---

## ⚙️ Prerequisites

| Requirement | Version | Notes |
|---|---|---|
| **Java JDK** | 11 or higher | JDK 17+ recommended. Must include `javac` and `java` on your `PATH` |
| **Maven** | 3.6+ | Only needed if building with Maven (see Option A below) |
| **JavaFX** | 17.0.9 | Automatically downloaded by Maven; bundled manually by `build.ps1` |

### Verify Java Installation

```powershell
java -version
javac -version
```

If these commands fail, [download and install the JDK](https://adoptium.net/) and add its `bin` directory to your system `PATH`.

---

## 🚀 How to Build & Run

### Option A: Using the PowerShell Build Script (Recommended — No Maven Required)

The included `build.ps1` script handles everything automatically: downloads dependencies, compiles, and launches the app.

```powershell
# Navigate to the project root
cd "Student Information System"

# Run the build script
.\build.ps1
```

**What it does:**
1. Downloads iText 7 and SLF4J JARs to `lib/` (if not already present)
2. Compiles all `.java` source files to `out/`
3. Launches the application

> **Note:** The script downloads JARs from Maven Central on first run. An internet connection is required the first time only.

---

### Option B: Using Maven

```powershell
# Navigate to the project root
cd "Student Information System"

# Compile and package into a fat JAR
mvn clean package

# Run the application
java -jar target/student-info-system-1.0-SNAPSHOT.jar
```

Or run directly without packaging:

```powershell
mvn compile exec:java -Dexec.mainClass="com.studentmgmt.App"
```

---

### Option C: Using IntelliJ IDEA

1. Open the project folder in IntelliJ IDEA
2. IntelliJ will auto-detect the `pom.xml` and import dependencies
3. Right-click `App.java` → **Run 'App.main()'**

---

## 📁 Project Structure

```
Student Information System/
├── pom.xml              # Maven build configuration
├── build.ps1            # PowerShell build & run script (standalone)
├── README.md            # This file
├── src/                 # Java source code (com.studentmgmt.*)
├── lib/                 # Auto-downloaded JARs (created by build.ps1)
├── out/                 # Compiled classes (created by build.ps1)
├── target/              # Maven build output
└── .idea/               # IntelliJ IDEA project files
```

---

## 📦 Dependencies

| Library | Version | Purpose |
|---|---|---|
| **iText 7** (kernel, layout, io, commons) | 7.2.5 | PDF report generation |
| **JavaFX** (controls, swing, graphics, fxml) | 17.0.9 | Pie chart in the Analytics dashboard |
| **SLF4J Simple** | 2.0.9 | Logging facade (quiets iText warnings) |

All dependencies are managed by Maven (`pom.xml`) or auto-downloaded by `build.ps1`.

---

## 🎨 Usage Guide

### Adding a Student
1. Click **"Add / Edit"** in the sidebar
2. Fill in required fields (marked with `*`): Student ID, Full Name, Age, Course
3. Optionally fill in: DOB, Gender, Mobile, Email
4. Enter marks (0–100) for all 5 subjects — GPA updates live as you type
5. Click **"➕ Add Student"**

### Viewing & Searching Records
1. Click **"Records"** in the sidebar
2. Use the search bar to filter by name, ID, or course
3. Select a row and click **"Edit"** or **"Delete"**

### Exporting to PDF
1. Add at least one student record
2. Click **"Export PDF"** in the sidebar
3. Choose a save location — a styled PDF report will be generated

### Analytics
1. Click **"Analytics"** in the sidebar
2. View the pass/fail pie chart and class statistics

---

## 🔧 Troubleshooting

| Problem | Solution |
|---|---|
| `javac` not found | Install JDK 11+ and add `JAVA_HOME/bin` to your system `PATH` |
| `build.ps1` cannot be loaded (execution policy) | Run `Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy RemoteSigned` in PowerShell |
| Maven `mvn` not found | Install [Apache Maven](https://maven.apache.org/download.cgi) and add to `PATH` |
| JavaFX module errors | Ensure you're using JDK 11+. JavaFX is fetched automatically via Maven/build script |
| Blank chart panel | JavaFX requires a compatible JDK. Try JDK 17 with the bundled JavaFX dependencies |

---

## 📝 Grading Scale

The system uses the following mark-to-GPA conversion (4.0 scale):

| Marks Range | Grade Point | Letter Grade |
|---|---|---|
| 90–100 | 4.0 | A |
| 85–89 | 3.7 | A |
| 80–84 | 3.3 | A- |
| 75–79 | 3.0 | B+ |
| 70–74 | 2.7 | B |
| 65–69 | 2.3 | B- |
| 60–64 | 2.0 | C+ |
| 55–59 | 1.7 | C |
| 50–54 | 1.3 | C- |
| 45–49 | 1.0 | D |
| 40–44 | 0.7 | F |
| 0–39 | 0.0 | F |

**Passing threshold:** GPA ≥ 2.0

---

## 📄 License

This project was developed as part of the **ShadowFox** internship program.
