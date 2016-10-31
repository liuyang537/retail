package com.tenx.ms.retail.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "STOCKS", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"product_id", "store_id"})
})
public class Stock {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne//(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne//(cascade = CascadeType.ALL)
    @JoinColumn(name = "store_id")
    private Store store;

    @NotNull
    @Column(name = "count")
    private long count;

    public Stock() {
    }

    public Stock(long id, Product product, Store store, long count) {
        this.id = id;
        this.product = product;
        this.store = store;
        this.count = count;
    }

    public Stock(Product product, Store store, long count) {
        this.product = product;
        this.store = store;
        this.count = count;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
