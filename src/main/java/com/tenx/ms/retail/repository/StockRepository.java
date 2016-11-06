package com.tenx.ms.retail.repository;

import com.tenx.ms.retail.domain.Product;
import com.tenx.ms.retail.domain.Stock;
import com.tenx.ms.retail.domain.Store;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends CrudRepository<Stock, Long> {

    @Query("SELECT s from Stock s where s.product = :product and s.store = :store")
    public List<Stock> findByProductAndStore(@Param("product") Product product, @Param("store") Store store);

}
