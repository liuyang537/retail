package com.tenx.ms.retail.repository;

import com.tenx.ms.retail.entity.OrderedProduct;
import com.tenx.ms.retail.entity.Product;
import com.tenx.ms.retail.entity.Store;
import org.hibernate.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import java.util.*;

public class ProductRepository {

    private SessionFactorySingleton sfs;

    public ProductRepository() {
        ApplicationContext ctx =
                new AnnotationConfigApplicationContext(SessionFactorySingleton.class);
        this.sfs = ctx.getBean(SessionFactorySingleton.class);
    }

    public Long addProduct(String name, String description, double price, String sku, Long store_id){
        Session session = sfs.getSessionFactory().openSession();
        Store s = session.get(Store.class, store_id);
        if(s == null){ return (long)-1; }
        Transaction tx = null;
        Long product_id = null;
        try{
            tx = session.beginTransaction();
            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setSku(sku);
            product.setStore_id(store_id);
            product_id = (Long) session.save(product);
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }finally {
            session.close();
        }
        return product_id;
    }

    public List<Product> listAllProducts(Long store_id){
        Session session = sfs.getSessionFactory().openSession();
        Transaction tx = null;
        List<Product> products = null;
        try{
            tx = session.beginTransaction();
            Query q = session.createQuery("select p from Product p where store_id = :store_id");
            q.setParameter("store_id", store_id);
            products = (List<Product>)q.list();
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }finally {
            session.close();
        }
        return products;
    }

    public Product findAProduct(Long store_id, Long product_id){
        Session session = sfs.getSessionFactory().openSession();
        Transaction tx = null;
        Product product = null;
        try{
            tx = session.beginTransaction();
            Query q = session.createQuery("select p from Product p where store_id = :store_id and product_id = :product_id");
            q.setParameter("store_id", store_id);
            q.setParameter("product_id", product_id);
            if(q.list().size()!=0){
                product = (Product)q.list().get(0);
            }
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }finally {
            session.close();
        }
        return product;
    }

    public Product findAProduct(Long store_id, String name){
        Session session = sfs.getSessionFactory().openSession();
        Transaction tx = null;
        Product product = null;
        try{
            tx = session.beginTransaction();
            Query q = session.createQuery("select p from Product p where store_id = :store_id and name = :name");
            q.setParameter("store_id", store_id);
            q.setParameter("name", name);
            if(q.list().size()!=0){
                product = (Product)q.list().get(0);
            }
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }finally {
            session.close();
        }
        return product;
    }

    public Product findSku(Long store_id, String sku){
        Session session = sfs.getSessionFactory().openSession();
        Transaction tx = null;
        Product product = null;
        try{
            tx = session.beginTransaction();
            Query q = session.createQuery("select p from Product p where store_id = :store_id and sku = :sku");
            q.setParameter("store_id", store_id);
            q.setParameter("sku", sku);
            if(q.list().size()!=0){
                product = (Product)q.list().get(0);
            }
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }finally {
            session.close();
        }
        return product;
    }

    public void deleteProducts(Long store_id){
        Session session = sfs.getSessionFactory().openSession();
        Transaction tx = null;
        Product product = null;
        try{
            tx = session.beginTransaction();
            Query q = session.createQuery("select p from Product p where store_id = :store_id");
            q.setParameter("store_id", store_id);
            List<Product> products = q.list();
            for(Product p : products){
                Query q1 = session.createQuery("select op from OrderedProduct op where store_id = :store_id");
                q1.setParameter("store_id", store_id);
                List<OrderedProduct> ops = q1.list();
                for(OrderedProduct op : ops){
                    session.delete(op);
                }
                session.delete(p);
            }
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }finally {
            session.close();
        }
        return;
    }

    public void deleteProduct(Long store_id, Long product_id){
        Session session = sfs.getSessionFactory().openSession();
        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            Query q = session.createQuery("select p from Product p where store_id = :store_id and product_id = :product_id");
            q.setParameter("store_id", store_id);
            q.setParameter("product_id", product_id);
            if(q.list().size()!=0) {
                Product product = (Product) q.list().get(0);
                Query q1 = session.createQuery("select op from OrderedProduct op where product_id = :product_id");
                q1.setParameter("product_id", product_id);
                List<OrderedProduct> ops = q1.list();
                for(OrderedProduct op : ops){
                    session.delete(op);
                }
                session.delete(product);
            }
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

