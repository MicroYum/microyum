package com.microyum.dao.jpa;

import com.microyum.model.stock.MyStockDataDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyStockDataDetailDao extends JpaRepository<MyStockDataDetail, Long> {
}
