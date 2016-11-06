package com.tenx.ms.retail.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "orderedproducts", uniqueConstraints={
        @UniqueConstraint(columnNames = {"order_id", "store_id", "product_id"})
})
public class OrderedProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private Order order;

    @NotNull(message = "Store can not be null")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "store_id")
    private Store store;

    @NotNull(message = "Product ID can not be null")
    @Column(name = "product_id")
    private Long product_id;

    @NotNull(message = "Amount can not be null")
    @Column(name = "amount")
    private long amount;

    private long boAmount;

    protected OrderedProduct() {
    }

    public OrderedProduct(Long product_id, long amount) {
        this.product_id = product_id;
        this.amount = amount;
    }

    public OrderedProduct(Order order, Store store, Long product_id, long amount, long boAmount) {
        this.order = order;
        this.store = store;
        this.product_id = product_id;
        this.amount = amount;
        this.boAmount = boAmount;
    }

    public long getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public Long getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Long product_id) {
        this.product_id = product_id;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getBoAmount() {
        return boAmount;
    }

    public void setBoAmount(long boAmount) {
        this.boAmount = boAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderedProduct)) return false;

        OrderedProduct that = (OrderedProduct) o;

        if (!getOrder().equals(that.getOrder())) return false;
        if (!getStore().equals(that.getStore())) return false;
        return getProduct_id().equals(that.getProduct_id());

    }

    @Override
    public int hashCode() {
        int result = getOrder().hashCode();
        result = 31 * result + getStore().hashCode();
        result = 31 * result + getProduct_id().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "OrderedProduct{" +
                "id=" + id +
                ", order=" + order +
                ", store=" + store +
                ", product_id=" + product_id +
                ", amount=" + amount +
                ", boAmount=" + boAmount +
                '}';
    }
}
