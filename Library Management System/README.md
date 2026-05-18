# 📚 Library Management System

A feature-rich, production-quality Library Management System built with **Java Swing + MySQL + Maven**.

---

## Features

| Feature | Details |
|---------|---------|
| **User Accounts** | Admin + Member roles, SHA-256 password hashing |
| **Book Catalog** | Browse, search, add, delete with normalized DB |
| **Google Books API** | Enter an ISBN → title & author auto-filled |
| **Overdue Fines** | Calculated via `java.time` — ₹5/day after due date |
| **Recommendations** | Genre-based suggestions from borrow history |
| **Security** | 100% `PreparedStatement`, `try-with-resources` everywhere |
| **Normalization** | Separate `authors` table — book stores `author_id` FK |

---

## Quick Start

### 1. Prerequisites

- Java 17+
- Maven 3.8+
- MySQL 8.0+

### 2. Database Setup

```sql
-- In MySQL Workbench or mysql CLI:
source schema.sql
```

This creates `library_db` with all tables and seeds:
- Admin account: **admin / admin123**
- 5 sample books across different genres

### 3. Configure DB Credentials

Edit `src/main/java/com/library/db/DatabaseManager.java`:

```java
private static final String USER     = "root";
private static final String PASSWORD = "your_password";  // ← change this
```

### 4. Build & Run

```bash
# Compile and package fat JAR
mvn clean package -q

# Run
java -jar target/library-management-system-1.0.0.jar
```

Or run directly via Maven:
```bash
mvn compile exec:java -Dexec.mainClass="com.library.Main"
```

---

## Project Structure

```
src/main/java/com/library/
├── Main.java                        # Entry point
├── db/DatabaseManager.java          # MySQL connection manager
├── model/                           # POJOs (User, Author, Book, BorrowRecord, Fine)
├── dao/                             # Data access (SQL via PreparedStatement)
├── service/                         # Business logic
│   ├── AuthService.java             # Login, SHA-256 hashing
│   ├── BookService.java             # Book CRUD + author normalization
│   ├── BorrowService.java           # Borrow/Return + fine calculation ← TIER 1
│   ├── RecommendationService.java   # Genre-based recommendations
│   └── GoogleBooksService.java      # ISBN → Title/Author API lookup ← TIER 2
└── ui/                              # Swing GUI
    ├── LoginDialog.java
    ├── MainFrame.java               # Tabbed host frame
    └── panels/
        ├── BookPanel.java           # Browse, search, add (with ISBN lookup), delete
        ├── BorrowPanel.java         # Borrow + return with fine display
        ├── AccountPanel.java        # Profile + borrow history
        ├── UserPanel.java           # Admin: manage users
        └── RecommendationPanel.java # Personalized recommendations
```

---

## Security Notes

| Concern | Solution |
|---------|---------|
| **SQL Injection** | Every query uses `PreparedStatement` — user input never concatenated into SQL |
| **Connection Leaks** | All `Connection`, `PreparedStatement`, `ResultSet` opened in `try-with-resources` |
| **Password Storage** | SHA-256 hex digest — plain text never stored or logged |

---

## Database Schema (3NF)

```
authors (author_id PK, name UNIQUE)
books   (book_id PK, isbn, title, author_id FK → authors, genre, total_copies, avail_copies)
users   (user_id PK, username UNIQUE, password_hash, full_name, email, role ENUM)
borrow_records (record_id PK, user_id FK, book_id FK, borrow_date, due_date, return_date)
fines   (fine_id PK, record_id FK UNIQUE, amount DECIMAL, paid BOOLEAN)
```

**Author is NOT stored in the books table** — it lives in the `authors` table and is referenced by FK. This is Basic Normalization (3NF).

---

## Tier 1 — Overdue Fine Calculation

```java
// In BorrowService.java
long overdueDays = ChronoUnit.DAYS.between(record.getDueDate(), returnDate);
double fine = (overdueDays > 0) ? overdueDays * FINE_RATE_PER_DAY : 0.0;
```

- Fine rate: **₹5.00/day** (configurable constant `BorrowService.FINE_RATE_PER_DAY`)
- Loan period: **14 days** (configurable constant `BorrowService.LOAN_DAYS`)
- Fine is stored in the `fines` table and shown in the Return dialog

---

## Tier 2 — Google Books API

```java
// In GoogleBooksService.java
URL url = new URL("https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbn);
HttpURLConnection conn = (HttpURLConnection) url.openConnection();
// ... read JSON response and parse with org.json
```

In the **Add Book** dialog:
1. Enter an ISBN (e.g., `9780439708180`)
2. Click **🔍 Lookup ISBN**
3. Title and Author fields are auto-filled from the Google Books API

---

## Default Login

| Username | Password | Role |
|----------|----------|------|
| `admin`  | `admin123` | ADMIN |

Create member accounts from the **👥 Users** tab (admin only).
