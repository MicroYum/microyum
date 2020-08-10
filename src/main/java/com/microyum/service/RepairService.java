package com.microyum.service;

/**
 * @author syaka.hong
 */
public interface RepairService {

    void repairAllStrategyData();

    void repairStrategyData(String area, String stockCode);

    void repairChgAndPercent();
}
