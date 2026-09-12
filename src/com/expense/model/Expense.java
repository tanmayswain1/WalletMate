package com.expense.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents one spending transaction with a date, category,
 * amount, and an optional description.
 */
public class Expense {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final LocalDate date;
    private final String    category;
    private final double    amount;
    private final String    description;

    public Expense(String dateStr, String category, double amount, String description) {
        this.date        = LocalDate.parse(dateStr.trim(), FMT);
        this.category    = category.trim();
        this.amount      = amount;
        this.description = description == null ? "" : description.trim();
    }

    public LocalDate getDate()        { return date;        }
    public String    getCategory()    { return category;    }
    public double    getAmount()      { return amount;      }
    public String    getDescription() { return description; }

    /** Converts to a CSV row:  date,category,amount,description */
    public String toCsv() {
        // Replace commas inside description so the CSV stays well-formed
        String safeDesc = description.replace(",", ";");
        return date.format(FMT) + "," + category + "," +
               String.format("%.2f", amount) + "," + safeDesc;
    }

    /** Parses from a CSV row:  date,category,amount,description */
    public static Expense fromCsv(String line) {
        // Limit to 4 tokens so commas in the description are kept together
        String[] parts = line.split(",", 4);
        if (parts.length < 4) {
            throw new IllegalArgumentException("Bad expense CSV line: " + line);
        }
        return new Expense(parts[0], parts[1], Double.parseDouble(parts[2].trim()), parts[3]);
    }

    @Override
    public String toString() {
        return "Expense[" + date + ", " + category + ", " + amount + ", " + description + "]";
    }
}
