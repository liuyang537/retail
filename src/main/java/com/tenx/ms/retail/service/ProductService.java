package com.tenx.ms.retail.service;

import com.tenx.ms.retail.domain.OrderedProduct;
import com.tenx.ms.retail.domain.Product;
import com.tenx.ms.retail.domain.Store;
import com.tenx.ms.retail.repository.OrderedProductRepository;
import com.tenx.ms.retail.repository.ProductRepository;
import com.tenx.ms.retail.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;

@Service
public class ProductService {

    private StoreRepository storeRepository;
    private ProductRepository productRepository;
    private OrderedProductRepository orderedProductRepository;

    @Autowired
    public void setProductRepository(ProductRepository productRepository){ this.productRepository = productRepository; }

    @Autowired
    public void setOrderedProductRepository(OrderedProductRepository orderedProductRepository){
        this.orderedProductRepository = orderedProductRepository;
    }

    @Autowired
    public void setStoreRepository(StoreRepository storeRepository){
        this.storeRepository = storeRepository;
    }

    public List<Product> list(long store_id){
        return productRepository.findByStore_id(store_id);
    }

    public Product findByNameAndStore_id(String name, long store_id){
        List<Product> products = productRepository.findByNameAndStore_id(name, store_id);
        return (products.size() == 0)? null:products.get(0);
    }

    public Product findBySkuAndStore_id(String sku, long store_id){
        List<Product> products = productRepository.findBySkuAndStore_id(sku, store_id);
        return (products.size() == 0)? null:products.get(0);
    }

    public Product findByIDAndStore_id(long product_id, long store_id){
        List<Product> products = productRepository.findByIDAndStore_id(product_id, store_id);
        return (products.size() == 0)? null:products.get(0);
    }

    public Long save(Product product){
        Long product_id = productRepository.save(product).getProduct_id();
        Store store = storeRepository.findOne(product.getStore_id());
        Set<Long> product_ids = store.getProduct_ids();
        product_ids.add(product_id);
        store.setProduct_ids(product_ids);
        storeRepository.save(store);
        return product_id;
    }

    public void delete(long product_id, long store_id){

        List<OrderedProduct> orderedProducts = orderedProductRepository.findByProduct_id(product_id);
        for(OrderedProduct op : orderedProducts ){
            orderedProductRepository.delete(op.getId());
        }

        Store store = storeRepository.findOne(store_id);
        Set<Long> product_ids = store.getProduct_ids();
        product_ids.remove(product_id);

        productRepository.delete(product_id);
    }
}
