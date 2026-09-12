package com.expense.service;

import com.expense.exception.BudgetExceededException;
import com.expense.model.Budget;
import com.expense.model.Expense;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Core business logic layer — validates expenses, computes analytics,
 * and renders the ASCII budget dashboard to the console.
 */
public class ExpenseService {

    private static final int BAR_WIDTH = 20;
    private static final DateTimeFormatter YM_FMT = DateTimeFormatter.ofPattern("yyyy-MM");

    private final FileStorageService storage;

    public ExpenseService(FileStorageService storage) {
        this.storage = storage;
    }

    // ──────────────────────────── record expense ──────────────────────────────

    /**
     * Checks the monthly budget before saving an expense.
     * Throws BudgetExceededException if the total for the month would go over.
     */
    public void recordExpense(Expense expense) throws IOException {
        YearMonth month = YearMonth.from(expense.getDate());
        Optional<Budget> found = storage.findBudget(month);

        if (found.isEmpty()) {
            throw new IllegalArgumentException(
                "No budget set for " + month.format(YM_FMT) +
                ". Use option 1 to create one first.");
        }

        Budget budget      = found.get();
        double spentSoFar  = totalSpentInMonth(month);
        double afterThis   = spentSoFar + expense.getAmount();

        if (afterThis > budget.getLimit()) {
            double remaining = budget.getLimit() - spentSoFar;
            throw new BudgetExceededException(
                "Budget exceeded! Remaining: Rs." + String.format("%.2f", remaining) +
                " | Expense: Rs." + String.format("%.2f", expense.getAmount()));
        }

        storage.saveExpense(expense);
    }

    // ──────────────────────────── dashboard ──────────────────────────────────

    /**
     * Prints the full dashboard for one budget month:
     * overall progress bar, daily safe spend, and per-category breakdown.
     */
    public void printBudgetStatus(Budget budget) throws IOException {
        YearMonth month   = budget.getMonth();
        double    limit   = budget.getLimit();
        double    spent   = totalSpentInMonth(month);
        double    left    = limit - spent;
        double    pct     = (limit == 0) ? 0.0 : (spent / limit) * 100.0;
        double    daily   = dailySafeSpend(month, left);

        System.out.println("  -----------------------------------------");
        System.out.println("  Month  : " + month.format(YM_FMT));
        System.out.printf ("  Budget : Rs.%.2f%n", limit);
        System.out.printf ("  Spent  : Rs.%.2f%n", spent);
        System.out.printf ("  Left   : Rs.%.2f%n", left);
        System.out.printf ("  Status : %s %s %.0f%%%n", bar(pct), tag(spent, limit), pct);
        System.out.printf ("  Daily safe spend : Rs.%.2f%n", daily);

        Map<String, Double> cats = categoryTotals(month);
        if (!cats.isEmpty()) {
            System.out.println();
            System.out.println("  Where the money went:");
            for (Map.Entry<String, Double> e : cats.entrySet()) {
                double catAmt = e.getValue();
                double catPct = (limit == 0) ? 0.0 : (catAmt / limit) * 100.0;
                System.out.printf("   %-15s Rs.%8.2f  %s %.0f%%%n",
                        e.getKey(), catAmt, bar(catPct), catPct);
            }
        }
        System.out.println("  -----------------------------------------");
    }

    // ──────────────────────────── list all expenses ───────────────────────────

    /** Prints every recorded expense in a simple table. */
    public void listAllExpenses() throws IOException {
        List<Expense> all = storage.loadAllExpenses();
        if (all.isEmpty()) {
            System.out.println("  No expenses recorded yet.");
            return;
        }
        System.out.println();
        System.out.printf("  %-12s %-16s %-12s %s%n", "Date", "Category", "Amount", "Description");
        System.out.println("  " + "-".repeat(62));
        for (Expense e : all) {
            System.out.printf("  %-12s %-16s Rs.%7.2f   %s%n",
                    e.getDate(), e.getCategory(), e.getAmount(), e.getDescription());
        }
        System.out.println();
    }

    // ──────────────────────────── private helpers ─────────────────────────────

    private double totalSpentInMonth(YearMonth month) throws IOException {
        return storage.loadAllExpenses().stream()
                .filter(e -> YearMonth.from(e.getDate()).equals(month))
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    private Map<String, Double> categoryTotals(YearMonth month) throws IOException {
        Map<String, Double> map = new LinkedHashMap<>();
        for (Expense e : storage.loadAllExpenses()) {
            if (YearMonth.from(e.getDate()).equals(month)) {
                map.merge(e.getCategory(), e.getAmount(), Double::sum);
            }
        }
        return map;
    }

    /** Builds an ASCII progress bar like [########------------]. */
    private String bar(double pct) {
        int filled = (int) Math.round(Math.min(pct, 100.0) / 100.0 * BAR_WIDTH);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < BAR_WIDTH; i++) {
            sb.append(i < filled ? '#' : '-');
        }
        return sb.append(']').toString();
    }

    /** Returns SAFE, WARNING (less than 10% left), or EXCEEDED. */
    private String tag(double spent, double limit) {
        if (spent > limit)                  return "EXCEEDED";
        if ((limit - spent) < 0.1 * limit)  return "WARNING";
        return "SAFE";
    }

    /** Remaining budget divided by days left in the month. */
    private double dailySafeSpend(YearMonth month, double remaining) {
        if (remaining <= 0) return 0.0;
        LocalDate today      = LocalDate.now();
        LocalDate endOfMonth = month.atEndOfMonth();
        if (today.isAfter(endOfMonth)) return 0.0;
        long daysLeft = ChronoUnit.DAYS.between(today, endOfMonth) + 1;
        return remaining / daysLeft;
    }
}
