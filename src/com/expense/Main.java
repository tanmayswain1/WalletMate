package com.expense;

import com.expense.model.Budget;
import com.expense.model.Expense;
import com.expense.service.ExpenseService;
import com.expense.service.FileStorageService;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.YearMonth;
import java.util.List;
import java.util.Scanner;

/**
 * WalletMate CLI — entry point.
 * Presents a numbered menu and delegates to service classes.
 */
public class Main {

    private static Scanner          scanner;
    private static FileStorageService storage;
    private static ExpenseService     expenseService;

    public static void main(String[] args) {
        // Use UTF-8 for all output
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        scanner        = new Scanner(System.in, StandardCharsets.UTF_8);
        storage        = new FileStorageService();
        expenseService = new ExpenseService(storage);

        printBanner();

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            System.out.println();
            switch (choice) {
                case "1": setBudget();    break;
                case "2": addExpense();   break;
                case "3": viewDashboard();break;
                case "4": listExpenses(); break;
                case "5":
                    System.out.println("  Goodbye! Stay financially healthy.");
                    running = false;
                    break;
                default:
                    System.out.println("  Invalid option. Please enter a number from 1 to 5.");
            }
        }
        scanner.close();
    }

    // ──────────────────────────── UI helpers ──────────────────────────────────

    private static void printBanner() {
        System.out.println("============================================");
        System.out.println("   WalletMate CLI - Personal Budget Tracker ");
        System.out.println("============================================");
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("  1) Set / Update Monthly Budget");
        System.out.println("  2) Add Expense");
        System.out.println("  3) View Dashboard");
        System.out.println("  4) List All Expenses");
        System.out.println("  5) Exit");
        System.out.print("  Choose (1-5): ");
    }

    // ──────────────────────────── option handlers ─────────────────────────────

    private static void setBudget() {
        try {
            System.out.print("  Enter month (YYYY-MM, e.g. 2026-09): ");
            String monthStr = scanner.nextLine().trim();
            YearMonth month = YearMonth.parse(monthStr);

            System.out.print("  Enter total budget for " + monthStr + " (Rs.): ");
            double limit = Double.parseDouble(scanner.nextLine().trim());

            if (limit <= 0) {
                System.out.println("  Budget must be a positive number.");
                return;
            }

            Budget budget = new Budget(month, limit);
            storage.saveOrUpdateBudget(budget);
            System.out.printf("  Budget of Rs.%.2f set for %s.%n", limit, monthStr);

        } catch (Exception e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    private static void addExpense() {
        try {
            System.out.print("  Enter date (YYYY-MM-DD): ");
            String dateStr = scanner.nextLine().trim();

            System.out.print("  Enter category (e.g. Food, Transport, Bills): ");
            String category = scanner.nextLine().trim();

            System.out.print("  Enter amount (Rs.): ");
            double amount = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("  Enter description (press Enter to skip): ");
            String description = scanner.nextLine().trim();

            Expense expense = new Expense(dateStr, category, amount, description);
            expenseService.recordExpense(expense);
            System.out.printf("  Rs.%.2f spent on %s recorded successfully.%n", amount, category);

        } catch (Exception e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    private static void viewDashboard() {
        try {
            List<Budget> budgets = storage.loadAllBudgets();
            if (budgets.isEmpty()) {
                System.out.println("  No budgets found. Use option 1 to set one.");
                return;
            }
            System.out.println("  ========== Budget Dashboard ==========");
            for (Budget b : budgets) {
                expenseService.printBudgetStatus(b);
            }
        } catch (Exception e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    private static void listExpenses() {
        try {
            System.out.println("  ========== All Expenses ==========");
            expenseService.listAllExpenses();
        } catch (Exception e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }
}
