package com.tenx.ms.retail.repository;

import com.tenx.ms.retail.domain.Store;
import org.springframework.stereotype.Repository;

import java.util.*;
import org.springframework.data.repository.CrudRepository;

@Repository
public interface StoreRepository extends CrudRepository<Store, Long> {

    List<Store> findByName(String name);

}