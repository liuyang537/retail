package com.tenx.ms.retail.repository;

import com.tenx.ms.retail.entity.Product;
import com.tenx.ms.retail.entity.Store;
import org.hibernate.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;
import java.util.*;

public class StoreRepository {

    private SessionFactorySingleton sfs;

    public StoreRepository() {
        ApplicationContext ctx =
                new AnnotationConfigApplicationContext(SessionFactorySingleton.class);
        this.sfs = ctx.getBean(SessionFactorySingleton.class);
    }

    public Long addStore(String name){
        Session session = sfs.getSessionFactory().openSession();
        Transaction tx = null;
        Long store_id = null;
        try{
            tx = session.beginTransaction();
            Store store = new Store();
            store.setName(name);
            store_id = (Long) session.save(store);
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }finally {
            session.close();
        }
        return store_id;
    }

    public List<Store> listAllStores(){
        Session session = sfs.getSessionFactory().openSession();
        Transaction tx = null;
        List<Store> stores = null;
        try{
            tx = session.beginTransaction();
            stores = session.createQuery("from Store").list();
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }finally {
            session.close();
        }
        return stores;
    }

    public Store findAStore(String name){
        Session session = sfs.getSessionFactory().openSession();
        Transaction tx = null;
        Store store = null;
        try{
            tx = session.beginTransaction();
            Query q = session.createQuery("from Store s where s.name = :name");
            q.setParameter("name", name);
            if(q.list().size()!=0){ store = (Store)q.list().get(0); }
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }finally {
            session.close();
        }
        return store;
    }

    public Store findAStore(Long store_id){
        Session session = sfs.getSessionFactory().openSession();
        Transaction tx = null;
        Store store = null;
        try{
            tx = session.beginTransaction();
            store = (Store) session.get(Store.class, store_id);
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }finally {
            session.close();
        }
        return store;
    }

    public void deleteStore(Long store_id){
        Session session = sfs.getSessionFactory().openSession();
        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            Store store = (Store) session.get(Store.class, store_id);
            if(store == null){ tx.commit(); return; }
            Query q = session.createQuery("select p from Product p where store_id = :store_id");
            q.setParameter("store_id", store_id);
            List<Product> products = q.list();
            for(Product p : products){
                session.delete(p);
            }
            session.delete(store);
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }finally {
            session.close();
        }
    }
}