package com.tenx.ms.retail.repository;

import com.tenx.ms.retail.domain.OrderedProduct;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import javax.sql.rowset.CachedRowSet;
import java.util.List;

@Repository
public interface OrderedProductRepository extends CrudRepository<OrderedProduct, Long>{

    @Query("SELECT op from OrderedProduct op where op.product_id = :product_id")
    public List<OrderedProduct> findByProduct_id(@Param("product_id") long product_id);

}
