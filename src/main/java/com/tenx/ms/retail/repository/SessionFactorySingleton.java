package com.tenx.ms.retail.repository;

import com.tenx.ms.retail.entity.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.inject.Singleton;

@Service
@Singleton
public class SessionFactorySingleton {

    private SessionFactory factory;

    @Bean
    public SessionFactory getSessionFactory() {
        if(factory == null) {
            try {
                factory = new Configuration().
                        configure("hibernate.cfg.xml").
                        addPackage("com.tenx.retail"). //add package if used.
                        addAnnotatedClass(Store.class).
                        addAnnotatedClass(Product.class).
                        addAnnotatedClass(Stock.class).
                        addAnnotatedClass(Order.class).
                        addAnnotatedClass(OrderedProduct.class).
                        buildSessionFactory();
            } catch (Throwable ex) {
                System.err.println("Failed to create sessionFactory object." + ex);
                throw new ExceptionInInitializerError(ex);
            }
        }
        return factory;
    }
}
