package com.example.raffinehome.order.exception;

public class OrderCancellationException extends RuntimeException {
    private final Integer orderId;
    private final String currentStatus;

    public OrderCancellationException(Integer orderId, String currentStatus) {
        super("注文ID " + orderId + " は現在の状態（" + currentStatus + "）のためキャンセルできません。");
        this.orderId = orderId;
        this.currentStatus = currentStatus;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }
}