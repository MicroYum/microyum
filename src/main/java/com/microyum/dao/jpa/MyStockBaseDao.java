package com.microyum.dao.jpa;

import com.microyum.model.stock.MyStockBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MyStockBaseDao extends JpaRepository<MyStockBase, Long> {

    @Query(value = "update my_stock_base set observe = false, last_update_time = now() where id = ?1", nativeQuery = true)
    void deleteById(Long id);

    MyStockBase findByStockCode(String stockCode);
}
