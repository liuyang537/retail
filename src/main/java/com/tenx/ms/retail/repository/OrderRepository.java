package com.tenx.ms.retail.repository;

import com.tenx.ms.retail.domain.Order;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long>{

    @Query("SELECT o from Order o where o.store_id = :store_id")
    public List<Order> findByStore_id(@Param("store_id") long store_id);

}
