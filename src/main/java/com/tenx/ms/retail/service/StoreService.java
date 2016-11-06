package com.tenx.ms.retail.service;

import com.tenx.ms.retail.domain.Order;
import com.tenx.ms.retail.domain.Product;
import com.tenx.ms.retail.domain.Store;
import com.tenx.ms.retail.repository.OrderRepository;
import com.tenx.ms.retail.repository.ProductRepository;
import com.tenx.ms.retail.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StoreService {

    private StoreRepository storeRepository;
    private ProductRepository productRepository;
    private OrderRepository orderRepository;

    @Autowired
    public void setStoreRepository(StoreRepository storeRepository){
        this.storeRepository = storeRepository;
    }

    @Autowired
    public void setProductRepository(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    @Autowired
    public void setOrderRepository(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }

    public Iterable<Store> listAllStores(){
        return storeRepository.findAll();
    }

    public Store findByID(Long store_id){
        return storeRepository.findOne(store_id);
    }

    public Store findByName(String name){
        List<Store> stores = storeRepository.findByName(name);
        return (stores.size() == 0)? null:stores.get(0);
    }

    public Long save(Store store){
        return  storeRepository.save(store).getStore_id();
    }

    public void delete(long store_id){

        List<Product> products = productRepository.findByStore_id(store_id);
        for(Product p : products){
            productRepository.delete(p.getProduct_id());
        }

        List<Order> orders = orderRepository.findByStore_id(store_id);
        for(Order o : orders){
            orderRepository.delete(o.getOrder_id());
        }

        storeRepository.delete(store_id);
    }
}
