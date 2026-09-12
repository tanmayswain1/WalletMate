package com.expense.exception;

/**
 * Thrown when a recorded expense would push total monthly spending
 * beyond the configured monthly budget limit.
 */
public class BudgetExceededException extends RuntimeException {

    public BudgetExceededException(String message) {
        super(message);
    }
}
