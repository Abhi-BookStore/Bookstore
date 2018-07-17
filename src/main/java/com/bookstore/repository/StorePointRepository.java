package com.bookstore.repository;

import com.bookstore.domain.StorePoint;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorePointRepository extends CrudRepository<StorePoint, Long> {

    public StorePoint findByUserId(Long userId);
}
