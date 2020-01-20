package com.microyum.dao.jpa;

import com.microyum.model.stock.MyStockData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface MyStockDataDao extends JpaRepository<MyStockData, Long> {

    @Query(value = "select min(t.trade_date) trade_date from my_stock_data t where area = ?1 and stock_code = ?2", nativeQuery = true)
    Date findMinTradeDateByStock(String area, String stockCode);

    @Query(value = "select * from my_stock_data where area = ?1 and stock_code = ?2 order by trade_date", nativeQuery = true)
    List<MyStockData> findByAreaAndStockCode(String area, String stockCode);

    List<MyStockData> findByAreaAndStockCodeAndTradeDate(String area, String stockCode, Date tradeDate);
}
