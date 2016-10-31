package com.tenx.ms.retail.repository;

import com.tenx.ms.retail.entity.Order;
import com.tenx.ms.retail.entity.OrderStatus;
import com.tenx.ms.retail.entity.OrderedProduct;
import com.tenx.ms.retail.entity.Store;
import org.hibernate.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import java.time.LocalDate;
import java.util.*;
import java.lang.*;

public class OrderRepository {

    private SessionFactorySingleton sfs;
    private OrderedProductRepository opr;

    public OrderRepository() {
        ApplicationContext ctx1 = new AnnotationConfigApplicationContext(SessionFactorySingleton.class);
        this.sfs = ctx1.getBean(SessionFactorySingleton.class);
        ApplicationContext ctx2 = new AnnotationConfigApplicationContext(OrderedProductRepository.class);
        this.opr = ctx2.getBean(OrderedProductRepository.class);
    }

    public OrderStatus createOrder(Long store_id, LocalDate order_date, List<OrderedProduct> orderedProducts, String first_name, String last_name, String email, String phone) {
        OrderStatus orderStatus = new OrderStatus();
        Session session = sfs.getSessionFactory().openSession();
        Transaction tx = null;
        Store store = session.get(Store.class, store_id);
        if(store == null){
            orderStatus.setStatus("FAILED");
            List<String> details = new ArrayList<>();
            details.add("No such store existed");
            orderStatus.setDetails(details);
            return orderStatus;
        }
        Long order_id = null;
        try{
            tx = session.beginTransaction();
            Order order = new Order();
            order.setStore_id(store_id);
            order.setOrder_date(order_date);
            order.setFirst_name(first_name);
            order.setLast_name(last_name);
            order.setStatus("PROCESSING");
            order.setEmail(email);
            order.setPhone(phone);
            order_id = (Long) session.save(order);
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }finally {
            session.close();
        }
        orderStatus = addOrderedProducts(order_id, store_id, orderedProducts);
        return orderStatus;
    }

    public OrderStatus addOrderedProducts(Long order_id, Long store_id, List<OrderedProduct> ops){
        Session session = sfs.getSessionFactory().openSession();
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrder_id(order_id);
        List<String> details = new ArrayList<>();
        boolean packing = true;
        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            Order order = session.get(Order.class, order_id);
            List<OrderedProduct> orderedProducts = new ArrayList<OrderedProduct>();
            for( OrderedProduct op : ops){
                OrderedProduct orderedProduct = opr.createOrderedProduct(order_id, store_id, op.getProduct_id(), op.getAmount());
                if(orderedProduct == null){
                    details.add("Product " + op.getProduct_id() + " is not in store " + store_id + ".");
                }
                else{
                    orderedProducts.add(orderedProduct);
                    details.add("Product " + orderedProduct.getProduct_id() + " of quantity " + orderedProduct.getAmount() + " is packaged!");
                    if(orderedProduct.getBoAmount() > 0) {
                        packing = false;
                        details.add("Product " + orderedProduct.getProduct_id() + " of quantity " + orderedProduct.getBoAmount() + " is backordered!");
                    }
                }
            }
            order.setOrderedProducts(orderedProducts);
            session.update(order);
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }finally {
            session.close();
        }
        orderStatus.setStatus( packing? "PACKING":"ORDERED");
        orderStatus.setDetails(details);
        return orderStatus;
    }

    public void deleteOrder(Long order_id) {
        Session session = sfs.getSessionFactory().openSession();
        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            Order order = (Order) session.get(Order.class, order_id);
            if( order != null){session.delete(order);}
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }finally {
            session.close();
        }
        return;
    }
}
