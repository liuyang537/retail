package com.tenx.ms.retail.repository;

import com.tenx.ms.retail.entity.Order;
import com.tenx.ms.retail.entity.OrderedProduct;
import com.tenx.ms.retail.entity.Product;
import com.tenx.ms.retail.entity.Store;
import org.hibernate.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class OrderedProductRepository {

    private SessionFactorySingleton sfs;
    private StockRepository tr;

    public OrderedProductRepository() {
        ApplicationContext ctx1 = new AnnotationConfigApplicationContext(SessionFactorySingleton.class);
        this.sfs = ctx1.getBean(SessionFactorySingleton.class);
        ApplicationContext ctx2 = new AnnotationConfigApplicationContext(StockRepository.class);
        this.tr = ctx2.getBean(StockRepository.class);
    }

    public OrderedProduct createOrderedProduct(Long order_id, Long store_id, Long product_id, long count){
        Session session = sfs.getSessionFactory().openSession();
        Transaction tx = null;
        OrderedProduct orderedProduct = null;
        Order order = session.get(Order.class, order_id);
        Store store = session.get(Store.class, store_id);
        Product product = session.get(Product.class, product_id);
        long boAmount =  tr.updateQuantity(store_id, product_id, count*(-1), false);
        if(order == null || store == null || product == null || boAmount < 0){
            return null;
        }
        try {
            tx = session.beginTransaction();
            orderedProduct = new OrderedProduct();
            orderedProduct.setOrder(order);
            orderedProduct.setStore(store);
            orderedProduct.setProduct_id(product_id);
            orderedProduct.setAmount(count - boAmount);
            orderedProduct.setBoAmount(boAmount);
            session.save(orderedProduct);
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }finally {
            session.close();
        }
        return orderedProduct;
    }
}
