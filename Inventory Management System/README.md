# 🛒 Inventory Management System | Project Zone

A **Java Swing** desktop Point-of-Sale (POS) application built for **ShadowFox**. It enables shopkeepers to browse a product catalog across three categories — **Cosmetics**, **Grocery**, and **Cold Drinks** — select quantities, and generate itemized customer bills with automatic tax calculation.

---

## 📑 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Prerequisites](#-prerequisites)
- [How to Run](#-how-to-run)
  - [Option A — IntelliJ IDEA (Recommended)](#option-a--intellij-idea-recommended)
  - [Option B — Command Line](#option-b--command-line)
  - [Option C — Pre-built JAR](#option-c--pre-built-jar)
- [Data Storage & "Database" Setup](#-data-storage--database-setup)
- [Product Catalog](#-product-catalog)
- [Tax Rates](#-tax-rates)
- [Application Walkthrough](#-application-walkthrough)
- [Contributing](#-contributing)
- [License](#-license)

---

## ✨ Features

| Feature                  | Description                                                                                       |
| ------------------------ | ------------------------------------------------------------------------------------------------- |
| **Product Catalog**      | 100+ products across Cosmetics (37), Grocery (60), and Cold Drinks (34) — all with preset prices |
| **Quantity Selection**   | Per-product spinner controls with live total recalculation                                        |
| **Tax Calculation**      | Automatic category-wise tax: Cosmetics 5%, Grocery 10%, Cold Drinks 5%                           |
| **Bill Generation**      | Generates a formatted receipt with bill number, customer details, itemized list, and grand total  |
| **Bill Search**          | Look up previously generated bills by bill number (within the same session)                       |
| **Customer Management**  | Captures customer name and phone number per bill                                                  |
| **Clear & Reset**        | One-click reset of all fields and selections                                                      |

---

## 🛠 Tech Stack

| Layer      | Technology                      |
| ---------- | ------------------------------- |
| Language   | **Java 11+** (up to JDK 24)    |
| UI         | **Java Swing** (javax.swing)    |
| Build/IDE  | **IntelliJ IDEA**               |
| Storage    | **In-memory** (no external DB)  |

---

## 📁 Project Structure

```
Inventory Management System/
├── .idea/                              # IntelliJ IDEA project settings
│   └── libraries/                      # Library configurations
├── files/
│   ├── InventoryPOS-source/
│   │   └── src/
│   │       └── pos/                    # 📦 Main source package
│   │           ├── InventoryApp.java   # Entry point — Swing UI & main()
│   │           ├── BillManager.java    # Singleton — catalog, prices, tax, billing logic
│   │           ├── Bill.java           # Bill data model
│   │           └── Product.java        # Product data model
│   ├── InventoryPOS-source.zip         # Source archive
│   └── InventoryPOS.jar                # Pre-built executable JAR
├── out/                                # Compiled output (auto-generated)
├── Inventory Management System.iml     # IntelliJ module descriptor
├── files.zip                           # Project archive
└── README.md                           # ← You are here
```

---

## 📋 Prerequisites

- **Java Development Kit (JDK) 11 or higher**
  - The project is configured for JDK 24 but will compile and run on JDK 11+.
  - Download: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [Adoptium](https://adoptium.net/)
  
- Verify your Java installation:
  ```bash
  java -version
  javac -version
  ```

> **Note:** No database server, build tool (Maven/Gradle), or additional libraries are required.

---

## 🚀 How to Run

### Option A — IntelliJ IDEA (Recommended)

1. **Open the project:**
   - Launch IntelliJ IDEA
   - Go to `File → Open` and select the `Inventory Management System` folder

2. **Configure the JDK** (if prompted):
   - Go to `File → Project Structure → Project`
   - Set **Project SDK** to JDK 11 or higher
   - Set **Language Level** to 11 or higher

3. **Run the application:**
   - Open `files/InventoryPOS-source/src/pos/InventoryApp.java`
   - Right-click inside the file → **Run 'InventoryApp.main()'**
   - Or click the green ▶ play button next to the `main()` method

---

### Option B — Command Line

Navigate to the project root and compile + run manually:

```bash
# 1. Navigate to the source directory
cd "Inventory Management System"

# 2. Create an output directory
mkdir -p compiled

# 3. Compile all source files
javac -d compiled files/InventoryPOS-source/src/pos/*.java

# 4. Run the application
java -cp compiled pos.InventoryApp
```

**Windows (PowerShell):**
```powershell
# 1. Navigate to the project
cd "Inventory Management System"

# 2. Create output directory
New-Item -ItemType Directory -Force -Path compiled

# 3. Compile
javac -d compiled files\InventoryPOS-source\src\pos\*.java

# 4. Run
java -cp compiled pos.InventoryApp
```

---

### Option C — Pre-built JAR

A pre-built JAR is included for convenience:

```bash
cd "Inventory Management System/files"
java -jar InventoryPOS.jar
```

> **Note:** The JAR requires a compatible JRE to be installed on your system.

---

## 💾 Data Storage & "Database" Setup

### ⚠️ No External Database Required

This application does **not** use any external database (no MySQL, PostgreSQL, SQLite, etc.). All data is stored **in-memory** using Java collections:

| Data                | Storage Mechanism                                       | Location in Code                                                         |
| ------------------- | ------------------------------------------------------- | ------------------------------------------------------------------------ |
| **Product Catalog** | `List<String>` (immutable)                              | `BillManager.COSMETICS`, `BillManager.GROCERY`, `BillManager.COLD_DRINKS` |
| **Product Prices**  | `Map<String, Double>` (immutable)                       | `BillManager.PRICES`                                                      |
| **Tax Rates**       | `static final double` constants                         | `BillManager.COSMETIC_TAX_RATE`, etc.                                     |
| **Generated Bills** | `Map<Integer, Bill>` (runtime only)                     | `BillManager.bills`                                                       |
| **Bill Counter**    | `int` starting at ~4000 (randomized)                    | `BillManager.nextBillNumber`                                              |

### What This Means

- **No database installation** or configuration is needed.
- **No connection strings**, credentials, or environment variables to set up.
- **Bills are session-scoped** — they exist only while the application is running. Closing the app clears all generated bills.
- **Product data is hardcoded** — to add/remove products or change prices, edit the `BillManager.java` source file directly.

### Adding or Modifying Products

To modify the product catalog, edit [BillManager.java](files/InventoryPOS-source/src/pos/BillManager.java):

1. **Add a product name** to the appropriate category list (`COSMETICS`, `GROCERY`, or `COLD_DRINKS`)
2. **Add the price** entry in the `PRICES` static block
3. **Recompile** and run

Example — adding a new cold drink:
```java
// In COLD_DRINKS list, add:
"New Drink Name"

// In PRICES static block, add:
m.put("New Drink Name", 45.0);
```

---

## 🏷 Product Catalog

The application includes **131 products** across three categories:

| Category        | Count | Examples                                                       |
| --------------- | ----- | -------------------------------------------------------------- |
| **Cosmetics**   | 37    | Face Wash, Sunscreen, Shampoo, Lipstick, Nail Polish           |
| **Grocery**     | 60    | Rice, Dal, Oil, Spices, Noodles, Dairy, Fruits & Vegetables    |
| **Cold Drinks** | 34    | Coca-Cola, Pepsi, Sprite, Red Bull, Juices, Lassi, Water       |

Prices range from **₹12** (Yippee noodles) to **₹850** (Olive oil).

---

## 💰 Tax Rates

| Category    | Tax Rate |
| ----------- | -------- |
| Cosmetics   | 5%       |
| Grocery     | 10%      |
| Cold Drinks | 5%       |

Taxes are calculated on the subtotal of each category and added to the grand total.

---

## 🖥 Application Walkthrough

1. **Launch** → The application opens a full-featured POS window
2. **Enter Customer Details** → Fill in Customer Name and Phone Number (both required)
3. **Select Products** → Use the spinner controls in each category column to set quantities
4. **View Totals** → Category totals and taxes auto-update as you change quantities (or click "Total")
5. **Generate Bill** → Click "Generate Bill" to create a formatted receipt in the billing area
6. **Search Bills** → Enter a bill number and click "Search" to retrieve a previously generated bill
7. **Clear** → Reset all fields to start a new transaction
8. **Exit / Logout** → Close the application

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit your changes (`git commit -m 'Add your feature'`)
4. Push to the branch (`git push origin feature/your-feature`)
5. Open a Pull Request

---

## 📄 License

This project is developed as part of the **ShadowFox Internship Program**.

---

<p align="center">
  Made with ☕ and Java Swing
</p>
