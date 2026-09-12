# Statement Document

## Problem statement

Managing personal finances can be tough. Many people struggle with apps that're hard to use full of ads or require cloud subscriptions. Most users just want a private and fast way to track their daily spending. They want to avoid overspending without having to give their data to third-party services. The truth is, there aren't tools that make this easy, especially not ones that work offline and keep everything private.

## Scope of the project

WalletMate CLI is built with the essentials in mind. It runs entirely through the command line, no interface no extra bells and whistles.

**In-Scope:**

- Set or change a monthly spending limit.

- Record expenses with a date, description and category.

- Show a real-time ASCII dashboard with progress bars total spent and how much you can still spend each day.

- Break down expenses by category for the current month.

- Save data locally using CSV file-no databases, no external services.

**Out-of-Scope:**

- No graphical user interfaces.

- No support for currencies or live exchange rates.

- No cloud sync or sharing across devices.

- No tracking of investments or financial portfolios.

## Target users

- **Students & Young Professionals**: People on budgets like those with a monthly allowance or a first job, who need strict control over their spending.

- **Terminal Enthusiasts / Developers**: Users who already spend most of their time in the command line and prefer small fast tools over big GUI apps.

- **Privacy-Conscious Individuals**: People who don't want to trust their financial data to online services-they want full control and local storage.

## High-level features

1. **Interactive CLI Menu Loop**: A easy-to-navigate text menu that lets users switch between functions quickly.

2. **Monthly Budget Configuration**: Users can set a fixed budget for a year and month.

3. **Expense. Categorization**: Log each transaction. Assign it to a category like food, transport or entertainment.

4. **Budget Breach Prevention**: The app checks every transaction, against the limit. Throws a custom `BudgetExceededException` if the budget is exceeded.

5. **ASCII Financial Dashboard**: A terminal display showing progress bars, budget status (SAFE/WARNING/EXCEEDED) and daily spending limits.

6. **CSV Local Storage**: Data is. Loaded automatically from `expenses.csv` and `budgets.csv`-no extra setup, no dependencies.