package com.tenx.ms.retail.service;

import com.tenx.ms.retail.domain.Order;
import com.tenx.ms.retail.domain.OrderStatus;
import com.tenx.ms.retail.domain.OrderedProduct;
import com.tenx.ms.retail.domain.Store;
import com.tenx.ms.retail.repository.OrderRepository;
import com.tenx.ms.retail.repository.OrderedProductRepository;
import com.tenx.ms.retail.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class OrderService {

    private OrderRepository orderRepository;
    private StoreRepository storeRepository;

    @Autowired
    private OrderedProductService orderedProductService;

    @Autowired
    public void setOrderRepository(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }

    @Autowired
    public void setStoreRepository(StoreRepository storeRepository){
        this.storeRepository = storeRepository;
    }

    public OrderStatus create(long store_id, LocalDate order_date, String first_name, String last_name, String email, String phone, List<OrderedProduct> ops){
        Order order = orderRepository.save(new Order(store_id, order_date, first_name, last_name,  email, phone));

        Store store = storeRepository.findOne(store_id);
        Set<Long> order_ids = store.getOrder_ids();
        order_ids.add(order.getOrder_id());
        store.setOrder_ids(order_ids);
        storeRepository.save(store);

        return setOrderedProduct(order, store, ops);
    }

    public OrderStatus setOrderedProduct(Order order, Store store, List<OrderedProduct> ops){
        OrderStatus orderStatus = new OrderStatus();
        boolean packing = true;
        orderStatus.setOrder_id(order.getOrder_id());
        List<String> details = new ArrayList<>();
        List<OrderedProduct> orderedProducts = new ArrayList<>();
        for( OrderedProduct op : ops){
            OrderedProduct orderedProduct = orderedProductService.create(order, store, op.getProduct_id(), op.getAmount());
            if(orderedProduct == null){
                packing = false;
                details.add("Product id " + op.getProduct_id() + " is not in store " + store.getStore_id() + ".");
            }
            else{
                orderedProducts.add(orderedProduct);
                details.add("Product id " + orderedProduct.getProduct_id() + " of quantity " + orderedProduct.getAmount() + " is packaged!");
                if(orderedProduct.getBoAmount() > 0) {
                    packing = false;
                    details.add("Product id " + orderedProduct.getProduct_id() + " of quantity " + orderedProduct.getBoAmount() + " is backordered!");
                }
            }
        }
        orderStatus.setStatus( packing? "PACKING":"ORDERED");
        orderStatus.setDetails(details);
        order.setOrderedProducts(orderedProducts);
        for(OrderedProduct op : orderedProducts){
            System.out.println(op.toString());
        }
        order.setStatus(orderStatus.getStatus());
        orderRepository.save(order);
        return orderStatus;
    }
}
