package com.tenx.ms.retail.repository;

import com.tenx.ms.retail.entity.Stock;
import com.tenx.ms.retail.entity.Product;
import com.tenx.ms.retail.entity.Store;
import org.hibernate.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class StockRepository {

    private SessionFactorySingleton sfs;

    public StockRepository() {
        ApplicationContext ctx =
                new AnnotationConfigApplicationContext(SessionFactorySingleton.class);
        this.sfs = ctx.getBean(SessionFactorySingleton.class);
    }

    /*
    0: success
    -1: no such store
    -2: no such product in this store
    -3: can not update stock to a negative number
    positive number: backordered amount
     */
    public long updateQuantity(Long store_id, Long product_id, long count, boolean isCurrAmount){
        Session session = sfs.getSessionFactory().openSession();
        Transaction tx = null;
        Stock stock = null;
        try{
            tx = session.beginTransaction();
            Store store = session.get(Store.class, store_id);
            if(store == null){ return -1; }
            Product product = null;
            Query q = session.createQuery("select p from Product p where store_id = :store_id and product_id = :product_id");
            q.setParameter("store_id", store_id);
            q.setParameter("product_id", product_id);
            if(q.list().size()!=0){ product = (Product)q.list().get(0); }
            if( product == null){ return -2; }
            Query q1 = session.createQuery("select s from Stock s where store_id = :store_id and product_id = :product_id");
            q1.setParameter("store_id", store_id);
            q1.setParameter("product_id", product_id);

            if(q1.list().size() != 0) {//if the stock existed
                stock = (Stock) q1.list().get(0);
            }
            else{// if the stock has not been established
                stock = new Stock();
                stock.setProduct(product);
                stock.setStore(store);
                stock.setCount(0);
            }

            if(isCurrAmount){//if updating the current stock
                if(count >= 0){//if the current amount is non negative
                    stock.setCount(count);
                    session.save(stock);
                    tx.commit();
                    return 0;
                }
                else{//if the current amount is negative
                    tx.commit();
                    return -3;
                }
            }
            else{
                Long t = stock.getCount()+count;
                if( t >= 0) {//if an ordered amount does not exceed the stock amount
                    stock.setCount(t);
                    session.save(stock);
                    tx.commit();
                    return 0;
                }
                else{//if an ordered amount exceed the stock amount
                    stock.setCount(0);
                    session.save(stock);
                    tx.commit();
                    return Math.abs(t);
                }

            }
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }finally {
            session.close();
        }
        return 0;
    }

    public Stock getQuantity(Long store_id, Long product_id){
        Session session = sfs.getSessionFactory().openSession();
        Transaction tx = null;
        Stock stock = null;
        try{
            tx = session.beginTransaction();
            Store store = session.get(Store.class, store_id);
            Product product = null;
            Query q = session.createQuery("select p from Product p where store_id = :store_id and p.product_id = :product_id");
            q.setParameter("store_id", store_id);
            q.setParameter("product_id", product_id);
            if(q.list().size()!=0){ product = (Product)q.list().get(0); }
            if(store == null || product == null){ return stock; }
            Query q1 = session.createQuery("select s from Stock s where store_id = :store_id and product_id = :product_id");
            q1.setParameter("store_id", store_id);
            q1.setParameter("product_id", product_id);
            if(q1.list().size() != 0){
                stock = (Stock)q1.list().get(0);
            }
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }finally {
            session.close();
        }
        return stock;
    }

    public void deleteStock(Long store_id, Long product_id){
        Session session = sfs.getSessionFactory().openSession();
        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            Store store = session.get(Store.class, store_id);
            Product product = null;
            Query q = session.createQuery("select p from Product p inner join p.stores as s where s.store_id = :store_id and p.product_id = :product_id");
            q.setParameter("store_id", store_id);
            q.setParameter("product_id", product_id);
            if(q.list().size()!=0){ product = (Product)q.list().get(0); }
            if(store == null || product == null){ return; }
            Query q1 = session.createQuery("select s from Stock s inner join s.store t inner join s.product p where t.store_id = :store_id and p.product_id = :product_id");
            q1.setParameter("store_id", store_id);
            q1.setParameter("product_id", product_id);
            if(q1.list().size() != 0){
                Stock stock = (Stock)q1.list().get(0);
                session.delete(stock);
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
