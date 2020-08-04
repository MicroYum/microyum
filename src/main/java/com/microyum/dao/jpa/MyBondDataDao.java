package com.microyum.dao.jpa;

import com.microyum.model.bond.MyBondData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface MyBondDataDao extends JpaRepository<MyBondData, Long> {

    MyBondData findByAreaAndBondCodeAndTradeDate(String area, String bondCode, Date tradeDate);
}
