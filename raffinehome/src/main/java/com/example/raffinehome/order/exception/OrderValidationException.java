package com.example.raffinehome.order.exception;

import java.util.List;

public class OrderValidationException extends RuntimeException {
    private final Integer orderId;
    private final List<String> validationErrors;

    public OrderValidationException(Integer orderId, List<String> validationErrors) {
        super("注文ID " + orderId + " のバリデーションエラー: " + String.join(", ", validationErrors));
        this.orderId = orderId;
        this.validationErrors = validationErrors;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }
}