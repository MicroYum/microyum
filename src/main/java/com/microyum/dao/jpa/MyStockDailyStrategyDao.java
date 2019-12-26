package com.microyum.dao.jpa;

import com.microyum.model.stock.MyStockDailyStrategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MyStockDailyStrategyDao extends JpaRepository<MyStockDailyStrategy, Long> {


    @Query(value = " select * from my_stock_daily_strategy where stock_code = ?1 and date_format(trade_date, '%Y-%m-%d') = ?2", nativeQuery = true)
    MyStockDailyStrategy findByStockCodeAndTradeDate(String stockCode, String tradeDate);
}
