package com.tenx.ms.retail.entity;

import java.util.List;

public class OrderStatus {
    private Long order_id;
    private String status;
    private List<String> details;

    public OrderStatus() {
    }

    public OrderStatus(Long order_id, String status, List<String> details) {
        this.order_id = order_id;
        this.status = status;
        this.details = details;
    }

    public Long getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Long order_id) {
        this.order_id = order_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "OrderStatus{" +
                "order_id=" + order_id +
                ", status='" + status + '\'' +
                ", details=" + details +
                '}';
    }
}
