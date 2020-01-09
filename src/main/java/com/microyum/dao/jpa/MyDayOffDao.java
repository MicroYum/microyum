package com.microyum.dao.jpa;

import com.microyum.model.common.MyDayOff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MyDayOffDao extends JpaRepository<MyDayOff, Long> {

    @Query(value = "select * from my_day_off where trade_date = ?1", nativeQuery = true)
    MyDayOff findByTradeDate(String date);
}
