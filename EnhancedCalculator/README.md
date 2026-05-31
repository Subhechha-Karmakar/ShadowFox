# 🧮 Enhanced Calculator — BigDecimal Precision

> A feature-rich Java calculator built with `BigDecimal` for exact arithmetic — **no floating-point surprises**.  
> `0.1 + 0.2` actually equals `0.3`. ✅

---

## ✨ Features

| Category | Details |
|---|---|
| **Basic Arithmetic** | Addition, Subtraction, Multiplication, Division |
| **Scientific** | Square Root, Exponentiation (integer & fractional) |
| **Unit Conversions** | Temperature (°C ↔ °F ↔ K) and Currency (USD, EUR, GBP, INR) |
| **Expression Parser** | Full BODMAS/PEMDAS support with parentheses, `^`, `sqrt()` |
| **Two Interfaces** | Console-based CLI **and** Swing GUI with dark theme |

---

## 🏗️ Project Structure

```
EnhancedCalculator/
├── Calculator.java          # Console-based calculator (main entry point for CLI)
├── CalculatorGUI.java       # Swing GUI calculator (main entry point for GUI)
├── Operations.java          # Basic arithmetic: add, subtract, multiply, divide
├── Scientific.java          # Square root & exponentiation logic
├── Conversions.java         # Temperature & currency conversions
├── ExpressionParser.java    # Recursive descent parser (BODMAS/PEMDAS)
└── internshipProjCalculator.iml  # IntelliJ IDEA module file
```

---

## 📋 Prerequisites

| Requirement | Minimum Version |
|---|---|
| **Java JDK** | 8 or higher |

That's it — **no database, no external libraries, no build tools required**.  
This is a standalone Java application that uses only the Java Standard Library (`java.math`, `javax.swing`).

---

## 🚀 How to Run

### Option A — Using IntelliJ IDEA (Recommended)

1. **Open the project**  
   `File → Open → select the EnhancedCalculator folder`

2. **Configure JDK**  
   `File → Project Structure → Project → SDK → select JDK 8+`

3. **Run the Console version**  
   Right-click `Calculator.java` → `Run 'Calculator.main()'`

4. **Run the GUI version**  
   Right-click `CalculatorGUI.java` → `Run 'CalculatorGUI.main()'`

---

### Option B — Using the Command Line

```bash
# 1. Navigate to the project directory
cd path/to/EnhancedCalculator

# 2. Compile all Java files
javac *.java

# 3a. Run the Console Calculator
java Calculator

# 3b. Run the GUI Calculator
java CalculatorGUI
```

---

## 💾 Database Setup

### **There is no database.**

This project is a **self-contained, in-memory calculator**. All data (currency rates, conversion constants) is defined as `BigDecimal` constants directly in the source code.

| What you might expect | What actually happens |
|---|---|
| Currency exchange rates from a DB | Hardcoded as `BigDecimal` constants in `Conversions.java` |
| Calculation history in a DB | Not persisted — results are displayed and discarded |
| User preferences in a DB | Not applicable — the app resets on every launch |

**No database driver, no connection string, no schema, no migrations.** Just compile and run.

> 💡 **Want to add a database later?** You could add SQLite (via `java.sql`) to persist calculation history. No additional setup beyond adding the JDBC driver JAR to the classpath would be needed.

---

## 🖥️ Usage Guide

### Console Mode (`Calculator.java`)

```
==================================================
       CONSOLE-BASED CALCULATOR (BigDecimal)
       Precision You Can Trust: 0.1 + 0.2 = 0.3
==================================================

--- Main Menu ---
1. Basic Arithmetic
2. Scientific Calculations
3. Unit Conversions
4. Expression Parser (BODMAS/PEMDAS)
5. Exit
Choose an option (1-5):
```

### GUI Mode (`CalculatorGUI.java`)

A dark-themed Swing application with three tabs:

- **⌨ Basic** — Number pad with expression input field; supports typing or clicking  
- **ƒ Scientific** — Square root and power calculations  
- **⇄ Conversions** — Temperature and currency converters  

Type expressions like `5 + 3 * 2` or `sqrt(16)` and press **Enter** or **=**.

---

## 🧩 Architecture & Design Decisions

### Why BigDecimal?

Standard `double` arithmetic produces errors like `0.1 + 0.2 = 0.30000000000000004`.  
`BigDecimal` with `MathContext.DECIMAL128` (34-digit precision) eliminates this entirely.

### Expression Parser Grammar

The recursive descent parser follows this formal grammar:

```
expression → term (('+' | '-') term)*
term       → factor (('*' | '/') factor)*
factor     → unary ('^' factor)?          // right-associative
unary      → '-' unary | primary
primary    → NUMBER | '(' expression ')' | 'sqrt(' expression ')'
```

**Examples:**
- `5 + 3 * 2` → `11` (not `16`, because `*` binds tighter than `+`)
- `2 ^ 3 ^ 2` → `512` (right-associative: `2^(3^2) = 2^9`)
- `sqrt(16) + 1` → `5`

### Currency Conversion Strategy

Rates are stored relative to USD. Conversion uses a **two-step pivot**:
1. Source currency → USD
2. USD → Target currency

Rates are constructed with `new BigDecimal("0.92")` (String constructor) to avoid the double precision trap where `new BigDecimal(0.92)` ≠ `0.92` exactly.

---

## 🛠️ Troubleshooting

| Problem | Solution |
|---|---|
| `javac` not found | Install JDK 8+ and add `JAVA_HOME/bin` to your system `PATH` |
| `java Calculator` throws `NoClassDefFoundError` | Make sure you compiled **all** `.java` files: `javac *.java` |
| GUI looks different on macOS/Linux | The app forces the cross-platform L&F via `UIManager.setCrossPlatformLookAndFeelClassName()` |
| Division returns limited decimals | Division is capped at 10 decimal places (`DIVISION_SCALE = 10`) by design |

---

## 📄 License

This project was developed as part of the **ShadowFox Internship Program**.

---

<p align="center">
  <i>Built with ☕ Java — no frameworks, no databases, just math done right.</i>
</p>
