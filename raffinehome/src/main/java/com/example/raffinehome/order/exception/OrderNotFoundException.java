package com.example.raffinehome.order.exception;

public class OrderNotFoundException extends RuntimeException {
    private final Integer orderId;

    public OrderNotFoundException(Integer orderId) {
        super("注文ID " + orderId + " が見つかりません。");
        this.orderId = orderId;
    }

    public Integer getOrderId() {
        return orderId;
    }
}