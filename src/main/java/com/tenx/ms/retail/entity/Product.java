package com.tenx.ms.retail.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name="PRODUCTS", uniqueConstraints={
        @UniqueConstraint(columnNames = {"name", "store_id"}),
        @UniqueConstraint(columnNames = {"sku", "store_id"})
})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "product_id")
    private long product_id;


    @NotNull
    @Column(name = "store_id")
    private Long store_id;

    @NotNull(message = "Product name can not be null")
    @Size(max =50)
    @Column(name = "name", length = 50)
    private String name;

    @Size(max = 255)
    @Column(name = "description", length = 255)
    private String description ;

    @NotNull(message = "Product SKU can not be null")
    @Size(min = 5, max = 10)
    @Column(name = "sku", length = 10)
    private String sku;

    @NotNull(message = "Product price can not be null")
    @Digits(integer = 15, fraction = 2, message = "The value of price can not exceed fraction 2")
    @Column(name = "price", columnDefinition="Decimal(10,2)")
    private double price;

    @JsonIgnore
    @OneToMany( fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "product")
    private Set<Stock> stocks;

    @JsonIgnore
    @ElementCollection
    @CollectionTable(name = "products_orderedproducts", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "orderedproduct_ids")
    private Set<Long> orderedProduct_ids;

    public Product() { }

    public Product(long product_id, Long store_id, String name, String description, String sku, double price, Set<Stock> stocks, Set<Long> orderedProduct_ids) {
        this.product_id = product_id;
        this.store_id = store_id;
        this.name = name;
        this.description = description;
        this.sku = sku;
        this.price = price;
        this.stocks = stocks;
        this.orderedProduct_ids = orderedProduct_ids;
    }

    public boolean invalidPrice(){

        if (this.price == 0) {
            return false;
        }

        final double epsilon = Math.pow(10.0, (-3));

        double multiplier = Math.pow(10, 2);
        double check = this.price * multiplier;
        long checkLong = (long) Math.abs(check);
        check = checkLong / multiplier;

        double e = Math.abs(this.price - check);

        return e >= epsilon;
    }

    public boolean invalidSku(){
        if( this.sku.length() < 5 || this.sku.length() > 10){ return true; }
        for(int i = 0; i < this.sku.length(); i++){
            Character c = this.sku.charAt(i);
            if(!Character.isDigit(c) && !Character.isLetter(c) ){
                return true;
            }
        }
        return false;
    }

    public long getProduct_id() {
        return product_id;
    }

    public Long getStore_id() {
        return store_id;
    }

    public void setStore_id(Long store_id) {
        this.store_id = store_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Set<Stock> getStocks() {
        return stocks;
    }

    public void setStocks(Set<Stock> stocks) {
        this.stocks = stocks;
    }

    public Set<Long> getOrderedProduct_ids() {
        return orderedProduct_ids;
    }

    public void setOrderedProduct_ids(Set<Long> orderedProduct_ids) {
        this.orderedProduct_ids = orderedProduct_ids;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;

        Product product = (Product) o;

        if (getProduct_id() != product.getProduct_id()) return false;
        if (Double.compare(product.getPrice(), getPrice()) != 0) return false;
        if (!getStore_id().equals(product.getStore_id())) return false;
        if (!getName().equals(product.getName())) return false;
        if (!getDescription().equals(product.getDescription())) return false;
        return getSku().equals(product.getSku());

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (getProduct_id() ^ (getProduct_id() >>> 32));
        result = 31 * result + getStore_id().hashCode();
        result = 31 * result + getName().hashCode();
        result = 31 * result + getDescription().hashCode();
        result = 31 * result + getSku().hashCode();
        temp = Double.doubleToLongBits(getPrice());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Product{" +
                "product_id=" + product_id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", sku='" + sku + '\'' +
                ", price=" + price +
                '}';
    }
}
