package com.microyum.service.impl;

import com.microyum.common.http.BaseResponseDTO;
import com.microyum.common.http.HttpStatus;
import com.microyum.dao.MyStockDao;
import com.microyum.dto.StockLatestDataDTO;
import com.microyum.model.MyStockBase;
import com.microyum.model.MyStockData;
import com.microyum.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockServiceImpl implements StockService {

    @Autowired
    private MyStockDao stockDao;

    @Override
    public BaseResponseDTO referStockList(int pageNo, int pageSize, String stock) {

        List<MyStockBase> listStock = stockDao.referStockList(pageNo, pageSize, stock);

        BaseResponseDTO responseDTO = new BaseResponseDTO(HttpStatus.OK_LAYUI, listStock);
        responseDTO.setCount(stockDao.countAllStockBase(stock));

        return responseDTO;
    }

    @Override
    public BaseResponseDTO referStockDetail(String stockCode, String startDate, String endDate) {

        List<MyStockData> stockDaoList = stockDao.referStockData(stockCode, startDate, endDate);
        return new BaseResponseDTO(HttpStatus.OK, stockDaoList);
    }

    @Override
    public BaseResponseDTO referStockTradeDayDetail(String stockCode) {

        StockLatestDataDTO latestDataDTO = stockDao.referLatestStockData(stockCode);

        return new BaseResponseDTO(HttpStatus.OK, latestDataDTO);
    }
}
