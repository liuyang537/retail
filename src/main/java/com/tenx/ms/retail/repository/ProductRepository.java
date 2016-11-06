package com.tenx.ms.retail.repository;

import com.tenx.ms.retail.domain.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {

    @Query("SELECT p from Product p where p.name = :name and p.store_id = :store_id")
    public List<Product> findByNameAndStore_id(@Param("name") String name, @Param("store_id") long store_id);

    @Query("SELECT p from Product p where p.sku = :sku and p.store_id = :store_id")
    public List<Product> findBySkuAndStore_id(@Param("sku") String sku, @Param("store_id") long store_id);

    @Query("SELECT p from Product p where p.store_id = :store_id")
    public List<Product> findByStore_id(@Param("store_id") long store_id);

    @Query("SELECT p from Product p where p.product_id = :product_id and p.store_id = :store_id")
    public List<Product> findByIDAndStore_id(@Param("product_id") long product_id, @Param("store_id") long store_id);
}
