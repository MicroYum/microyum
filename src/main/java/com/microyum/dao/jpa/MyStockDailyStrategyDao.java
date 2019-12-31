package com.microyum.dao.jpa;

import com.microyum.model.stock.MyStockDailyStrategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MyStockDailyStrategyDao extends JpaRepository<MyStockDailyStrategy, Long> {


    @Query(value = " select * from my_stock_daily_strategy where area = ?1 and stock_code = ?2 and date_format(trade_date, '%Y-%m-%d') = ?2", nativeQuery = true)
    MyStockDailyStrategy findByStockAndTradeDate(String area, String stockCode, String tradeDate);
}
