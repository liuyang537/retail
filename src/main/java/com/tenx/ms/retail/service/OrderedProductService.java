package com.tenx.ms.retail.service;

import com.tenx.ms.retail.domain.*;
import com.tenx.ms.retail.repository.OrderedProductRepository;
import com.tenx.ms.retail.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class OrderedProductService {

    private OrderedProductRepository orderedProductRepository;
    private StoreRepository storeRepository;

    @Autowired
    private StockService stockService;

    @Autowired
    private ProductService productService;

    @Autowired
    public void setOrderedProductRepository(OrderedProductRepository orderedProductRepository){
        this.orderedProductRepository = orderedProductRepository;
    }

    @Autowired
    public void setStoreRepository(StoreRepository storeRepository){
        this.storeRepository = storeRepository;
    }

    public OrderedProduct create(Order order, Store store, long product_id, long amount){
        Product product = productService.findByIDAndStore_id(product_id, store.getStore_id());

        if(store == null){
            return null;
        }

        if(product == null){
            return null;
        }

        Stock stock = stockService.get(product, store);
        long boAmount = 0;
        if((stock.getCount() - amount) >= 0){
            stock.setCount(stock.getCount() - amount);
        }
        else{
            boAmount = amount - stock.getCount();
            stock.setCount(0);
        }
        stockService.set(stock);

        OrderedProduct orderedProduct = orderedProductRepository.save(new OrderedProduct(order, store, product_id, amount-boAmount, boAmount));

        Set<Long> orderedProduct_ids = product.getOrderedProduct_ids();
        orderedProduct_ids.add(orderedProduct.getId());
        product.setOrderedProduct_ids(orderedProduct_ids);
        productService.save(product);

//        System.out.println("3.14159B");
//        System.out.println(orderedProduct.toString());

        return orderedProduct;
    }
}
