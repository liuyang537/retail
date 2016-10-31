package com.tenx.ms.retail.entity;

import org.hibernate.validator.constraints.Email;
import java.time.LocalDate;
import java.util.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "ORDERS")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "order_id")
    private long order_id;

//    @ManyToOne//(cascade = CascadeType.ALL)
    @Column(name = "store_id")
    private Long store_id;

    @NotNull
    @Column(name = "order_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDate order_date;

    @NotNull
    @Column(name = "status")
    private String status;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order")
    private List<OrderedProduct> orderedProducts;

    @NotNull
    @Size(max = 30)
    @Column(name = "first_name", length = 30)
    private String first_name;

    @Size(max = 50)
    @Column(name = "last_name", length = 50)
    private String last_name;

    @NotNull
    @Size(max = 30)
    @Email
    @Column(name = "email", length = 30)
    private String email;

    @NotNull
    @Size(max = 15)
    @Column(name = "phone", length = 15)
    private String phone;

    public Order() {
    }

    public Order(long order_id, Long store_id, LocalDate order_date, String status, String first_name, String last_name, String email, String phone) {
        this.order_id = order_id;
        this.store_id = store_id;
        this.order_date = order_date;
        this.status = status;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.phone = phone;
    }

    public boolean invalidEmail(){
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            return true;
        }
        return false;
    }

    public boolean invalidPhone(){

        //validate phone numbers of format "1234567890"
        if (this.phone.matches("\\d{10}")){ return false; }
            //validating phone number with -, . or spaces
        else if(this.phone.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}")){ return false; }
            //validating phone number with extension length from 3 to 5
        else if(this.phone.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}")){ return false; }
            //validating phone number where area code is in braces ()
        else if(this.phone.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}")){ return false; }
            //return false if nothing matches the input
        else{ return true; }
    }

    public long getOrder_id() {
        return order_id;
    }

    public Long getStore_id() {
        return store_id;
    }

    public void setOrder_id(long order_id) {
        this.order_id = order_id;
    }

    public void setStore_id(Long store_id) {
        this.store_id = store_id;
    }

    public LocalDate getOrder_date() {
        return order_date;
    }

    public void setOrder_date(LocalDate order_date) {
        this.order_date = order_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrderedProduct> getOrderedProducts() {
        return orderedProducts;
    }

    public void setOrderedProducts(List<OrderedProduct> orderedProducts) {
        this.orderedProducts = orderedProducts;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;

        Order order = (Order) o;

        if (getOrder_id() != order.getOrder_id()) return false;
        if (!getStore_id().equals(order.getStore_id())) return false;
        if (!getFirst_name().equals(order.getFirst_name())) return false;
        if (!getLast_name().equals(order.getLast_name())) return false;
        if (!getEmail().equals(order.getEmail())) return false;
        return getPhone().equals(order.getPhone());

    }

    @Override
    public int hashCode() {
        int result = (int) (getOrder_id() ^ (getOrder_id() >>> 32));
        result = 31 * result + getStore_id().hashCode();
        result = 31 * result + getFirst_name().hashCode();
        result = 31 * result + getLast_name().hashCode();
        result = 31 * result + getEmail().hashCode();
        result = 31 * result + getPhone().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Order{" +
                "order_id=" + order_id +
                ", store_id=" + store_id +
                ", order_date=" + order_date +
                ", status='" + status + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}