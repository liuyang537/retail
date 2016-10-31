package com.tenx.ms.retail.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="STORES")
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "store_id")
    private long store_id;

    @NotNull
    @Column(name = "name", unique = true)
    private String name;

    @JsonIgnore
    @ElementCollection
    @CollectionTable(name = "stores_products", joinColumns = @JoinColumn(name = "store_id"))
    @Column(name = "product_ids")
    private Set<Long> product_ids;

    @JsonIgnore
    @OneToMany( fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "store")
    private Set<OrderedProduct> orderedProducts;

    @JsonIgnore
    @OneToMany( fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "store")
    private Set<Stock> stocks;

    @JsonIgnore
    @ElementCollection
    @CollectionTable(name = "stores_orders", joinColumns = @JoinColumn(name = "store_id"))
    @Column(name = "order_ids")
    private Set<Long> order_ids;

    public Store() {
    }

    public Store(long store_id, String name, Set<Long> product_ids, Set<Stock> stocks, Set<Long> order_ids) {
        this.store_id = store_id;
        this.name = name;
        this.product_ids = product_ids;
        this.stocks = stocks;
        this.order_ids = order_ids;
    }

    public long getStore_id() {
        return store_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Long> getProduct_ids() {
        return product_ids;
    }

    public void setProduct_ids(Set<Long> product_ids) {
        this.product_ids = product_ids;
    }

    public Set<Long> getOrder_ids() {
        return order_ids;
    }

    public void setOrder_ids(Set<Long> order_ids) {
        this.order_ids = order_ids;
    }

    public Set<Stock> getStocks() {
        return stocks;
    }

    public void setStocks(Set<Stock> stocks) {
        this.stocks = stocks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Store)) return false;

        Store store = (Store) o;

        if (getStore_id() != store.getStore_id()) return false;
        return getName().equals(store.getName());

    }

    @Override
    public int hashCode() {
        int result = (int) (getStore_id() ^ (getStore_id() >>> 32));
        result = 31 * result + getName().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Store{" +
                "store_id=" + store_id +
                ", name='" + name + '\'' +
                '}';
    }
}
