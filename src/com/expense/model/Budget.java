package com.expense.model;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * Holds a single monthly budget entry.
 * One budget covers the entire month regardless of category.
 */
public class Budget {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM");

    private final YearMonth month;
    private final double    limit;

    public Budget(YearMonth month, double limit) {
        this.month = month;
        this.limit = limit;
    }

    public YearMonth getMonth() { return month; }
    public double    getLimit() { return limit;  }

    /** Converts to a CSV row:  month,limit */
    public String toCsv() {
        return month.format(FMT) + "," + String.format("%.2f", limit);
    }

    /** Parses from a CSV row:  month,limit */
    public static Budget fromCsv(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length < 2) {
            throw new IllegalArgumentException("Bad budget CSV line: " + line);
        }
        YearMonth ym  = YearMonth.parse(parts[0].trim(), FMT);
        double    lim = Double.parseDouble(parts[1].trim());
        return new Budget(ym, lim);
    }

    @Override
    public String toString() {
        return "Budget[month=" + month + ", limit=" + limit + "]";
    }
}
