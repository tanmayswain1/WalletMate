# WalletMate CLI. Personal Expense & Monthly Budget Planner

## Overview of the project

WalletMate CLI is a fully terminal-based Java application made for personal financial management. It lets users set a monthly budget, record expenses with categories and see how much they are spending right away. By using basic Java and CSV files to save data it gives a quick, offline and private way to track money without needing complicated databases or visual interfaces.

## Features

- **Monthly Total Budgeting**: Set a limit for the month (like 2026-09).

- **Categorized Expense Tracking**: Keep track of every expense with the date, category, amount and note.

- **Smart Budget Enforcement**: Automatically checks how much is left and stops any expense that goes over the limit by using a BudgetExceededException`.

- ** Financial Dashboard**: Shows how much of the budget is used with ASCII bars (`[#####-----] 50%`) real-time status labels (` `WARNING` `EXCEEDED`). Calculates how much can be spent each day safely.

- **Category Breakdown**: See how much money was spent in areas like Food, Transport and more on the dashboard.

- **Automated CSV Persistence**:. Saves all the expense records and budget settings directly to `data/expenses.csv` and `data/budgets.csv` using basic Java file tools.

## Technologies/tools used

- **Language**: Java (JDK 11 or newer)

- **Data Storage**: CSV (Comma Separated Values) files

- **Core Libraries**: `java.io` (BufferedReader, BufferedWriter, FileWriter, FileReader) `java.time` (LocalDate, YearMonth) `java.util` (Scanner, Collections API).

- **Environment**: Any normal Command Line Interface (Terminal, Command Prompt, PowerShell).

## Steps to. Run the project

1. **Prerequisites**: Make sure Java Development Kit (JDK) is installed.

Check by typing `java -version` and `javac -version` in the terminal.

2. **Open Terminal**: Go to the folder of the project (`WalletMate/`).

3. **Compile the Source Code**:

Use this command to turn the Java files into the `bin/` folder:

```bash

javac -encoding UTF-8 -d bin src/com/expense/exception/BudgetExceededException.java src/com/expense/model/Budget.java src/com/expense/model/Expense.java src/com/expense/service/FileStorageService.java src/com/expense/service/ExpenseService.java src/com/expense/Main.java

```

4. **Run the Application**:

Start the program with:

```bash

java -cp bin com.expense.Main

```

## Instructions for testing

To check the app works well follow these steps in the menu:

1. **Set a Budget**:

Choose option `1`.

Type the month (like `2026-09`) and set a total budget of `10000`.

2. **Add Expenses**:

Pick option `2`.

Enter date `2026-09-12` category `Food` amount `1500` and a note.

Do this again for `Transport` (like `2000`).

3. **Check Dashboard Numbers**:

Option `3`.

Make sure the total spent is `3500` the progress bar shows `35%` and the category list matches the expenses.

4. **Check Budget Rules (Error Check)**:

Choose option `2` again.

Try adding an expense of `8000` (this would go over the budget).

Make sure the app catches this and shows the `Budget exceeded!` message, without crashing.

5. **See All Records**:

Pick option `4` to check that all the expenses added are shown in a table.

## Screenshots (Terminal Output Example)

```text

============================================

WalletMate CLI. Personal Budget Tracker

============================================

1) Set / Update Monthly Budget

2) Add Expense

3) View Dashboard

4) List All Expenses

5) Exit

Choose (1-5): 3

========== Budget Dashboard ==========

-----------------------------------------

Month : 2026-09

Budget : Rs.10000.00

Spent : Rs.3500.00

Left : Rs.6500.00

Status : [#######-------------] SAFE 35%

Daily spend : Rs.342.11

Where the money went:

Food Rs. 1500.00 [###-----------------] 15%

Transport Rs. 2000.00 [####----------------] 20%

-----------------------------------------

``