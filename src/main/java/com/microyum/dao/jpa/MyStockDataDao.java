package com.microyum.dao.jpa;

import com.microyum.model.stock.MyStockData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyStockDataDao extends JpaRepository<MyStockData, Long> {
}
