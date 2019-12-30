package com.microyum.dao.jpa;

import com.microyum.model.stock.MyStockData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface MyStockDataDao extends JpaRepository<MyStockData, Long> {

    @Query(value = "select min(t.trade_date) trade_date from my_stock_data t where area = ?1 and stock_code = ?2", nativeQuery = true)
    Date findMinTradeDateByStock(String area, String stockCode);
}
