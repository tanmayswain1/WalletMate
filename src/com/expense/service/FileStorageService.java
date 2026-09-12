package com.expense.service;

import com.expense.model.Budget;
import com.expense.model.Expense;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles all reading and writing of data/expenses.csv and data/budgets.csv.
 * Files are created automatically on first run if they do not exist.
 */
public class FileStorageService {

    private static final String EXPENSES_FILE = "data/expenses.csv";
    private static final String BUDGETS_FILE  = "data/budgets.csv";

    private static final String EXPENSE_HEADER = "date,category,amount,description";
    private static final String BUDGET_HEADER  = "month,limit";

    public FileStorageService() {
        initFile(EXPENSES_FILE, EXPENSE_HEADER);
        initFile(BUDGETS_FILE,  BUDGET_HEADER);
    }

    // ──────────────────────────── internal helpers ────────────────────────────

    /** Creates a file with a header row only if the file does not already exist. */
    private void initFile(String filePath, String header) {
        Path p = Paths.get(filePath);
        if (!Files.exists(p)) {
            try {
                if (p.getParent() != null) {
                    Files.createDirectories(p.getParent());
                }
                Files.writeString(p, header + System.lineSeparator(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException("Cannot create file: " + filePath + " — " + e.getMessage(), e);
            }
        }
    }

    // ──────────────────────────── expenses ────────────────────────────────────

    /** Appends a single expense row to expenses.csv. */
    public void saveExpense(Expense expense) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(EXPENSES_FILE, StandardCharsets.UTF_8, true))) {
            bw.write(expense.toCsv());
            bw.newLine();
        }
    }

    /** Returns every expense stored in expenses.csv. */
    public List<Expense> loadAllExpenses() throws IOException {
        List<Expense> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new FileReader(EXPENSES_FILE, StandardCharsets.UTF_8))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    list.add(Expense.fromCsv(trimmed));
                }
            }
        }
        return list;
    }

    // ──────────────────────────── budgets ─────────────────────────────────────

    /**
     * Saves a new budget for a month, or replaces an existing one for the same month.
     * Only one budget is allowed per month.
     */
    public void saveOrUpdateBudget(Budget incoming) throws IOException {
        List<Budget> all = loadAllBudgets();
        boolean found = false;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getMonth().equals(incoming.getMonth())) {
                all.set(i, incoming);
                found = true;
                break;
            }
        }
        if (!found) {
            all.add(incoming);
        }

        // Overwrite the file with the updated list
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(BUDGETS_FILE, StandardCharsets.UTF_8, false))) {
            bw.write(BUDGET_HEADER);
            bw.newLine();
            for (Budget b : all) {
                bw.write(b.toCsv());
                bw.newLine();
            }
        }
    }

    /** Returns every budget stored in budgets.csv. */
    public List<Budget> loadAllBudgets() throws IOException {
        List<Budget> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new FileReader(BUDGETS_FILE, StandardCharsets.UTF_8))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    list.add(Budget.fromCsv(trimmed));
                }
            }
        }
        return list;
    }

    /** Finds the budget for a given month, or returns empty if none is set. */
    public Optional<Budget> findBudget(YearMonth month) throws IOException {
        return loadAllBudgets().stream()
                .filter(b -> b.getMonth().equals(month))
                .findFirst();
    }
}
