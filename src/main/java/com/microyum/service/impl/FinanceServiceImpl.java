package com.microyum.service.impl;

import com.microyum.dao.MyFinanceDao;
import com.microyum.dto.TraderAccountDto;
import com.microyum.service.FinanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class FinanceServiceImpl implements FinanceService {

    @Autowired
    private MyFinanceDao myFinanceDao;

    @Override
    public List<TraderAccountDto> traderAccountOverview(int page, int limit, String trader) {

        int start = (page - 1) * limit;
        return myFinanceDao.traderAccountOverview(start, limit, trader);
    }

    @Override
    public boolean traderAccountCreate(TraderAccountDto dto) {

        try {
            int result = myFinanceDao.traderAccountCreate(dto);
            if (result != 1) {
                log.info("Create trader account error, result = " + result);
                return false;
            }
        } catch (Exception e) {
            log.error("Create trader account error.", e);
            return false;
        }

        return true;
    }

    @Override
    public boolean traderAccountUpdate(TraderAccountDto dto) {

        try {
            int result = myFinanceDao.traderAccountUpdate(dto);
            if (result != 1) {
                log.info("Update trader account error, result = " + result);
                return false;
            }
        } catch (Exception e) {
            log.error("Update trader account error.", e);
            return false;
        }

        return true;
    }

    @Override
    public boolean traderAccountDelete(Long id) {

        try {
            int result = myFinanceDao.traderAccountDelete(id);
            if (result != 1) {
                log.info("Delete trader account error, result = " + result);
                return false;
            }
        } catch (Exception e) {
            log.error("Delete trader account error.", e);
            return false;
        }

        return true;
    }
}
