# Banking Management System

A desktop Banking Management System built with **Java Swing**. It provides a modern UI for managing customers, accounts, transactions, loans, cards, employees, and branches.

## Features

- **Login authentication**
- **Customer management** — add, edit, search, delete
- **Account management** — create and manage bank accounts
- **Transaction management** — deposits, withdrawals, transfers
- **Loan management** — track and manage loans
- **Card management** — issue and manage debit/credit cards
- **Employee management** — staff records
- **Branch management** — branches and accounts per branch
- **Collapsible animated sidebar** with profile picture and live clock
- **MySQL database** integration for persistent data storage

## Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Java |
| UI | Java Swing |
| Look & Feel | FlatLaf (macOS Light theme) |
| Icons | Ikonli + FontAwesome 5 |
| Database | MySQL |
| JDBC Driver | mysql-connector-j 9.7.0 |

## Project Structure

```
BankingManagementSystem/
├── src/
│   ├── Main.java                          # Application entry point
│   ├── resources/
│   │   └── ansir.jpg                      # Profile picture
│   ├── system/bankingapp/
│   │   ├── frontend/                      # UI pages (Login, Customer, Account, etc.)
│   │   ├── mainpages/                     # Main dashboard (AdminPage)
│   │   ├── backend/                       # Database operations
│   │   └── model/                         # Data models
│   ├── uifactory/                         # Reusable UI components (SideBar)
│   └── utils/                             # Database connection, avatar loader
├── library/                               # External JAR dependencies
└── BankingManagementSystem.iml            # IntelliJ module config
```

## Prerequisites

- **JDK 17+** (project configured with OpenJDK 25)
- **IntelliJ IDEA** (recommended) or any Java IDE
- **MySQL Server** running locally on port `3306`
- Database named `banking_management_system`

## Database Setup

1. Start MySQL server.
2. Create the database:

```sql
CREATE DATABASE banking_management_system;
```

3. Update credentials in `src/utils/DatabaseConnection.java` if needed:

```java
private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/banking_management_system";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "";
```

## How to Run

1. Open the project in **IntelliJ IDEA**.
2. Ensure all JARs in the `library/` folder are on the classpath (configured in `.iml`).
3. Run **`Main.java`** (recommended entry point).

```
src/Main.java → Run
```

4. The **Login Page** will open. Enter credentials and click **Login**.

> Do not run `SideBar.java` directly — it redirects to the login page. Always use `Main.java`.

## Login Credentials

| Username | Password | Full Name |
|----------|----------|-----------|
| `ansir` | `admin123` | Ansir Ali |
| `fatima` | `manager123` | Fatima Noor |
| `bilal` | `teller123` | Bilal Ahmed |

All users have access to every page after login.

## Dependencies

All dependencies are included in the `library/` folder:

| Library | Version |
|---------|---------|
| flatlaf | 3.7.1 |
| mysql-connector-j | 9.7.0 |
| ikonli-core | 12.3.1 |
| ikonli-fontawesome5-pack | 12.3.1 |
| ikonli-swing | 12.3.1 |

## Screens

1. **Login Page** — username/password with profile picture
2. **Dashboard** — sidebar navigation with all menu items
3. **Management Pages** — CRUD tables and forms for each module

## Author

**Ansir Ali** — Banking Management System Project
