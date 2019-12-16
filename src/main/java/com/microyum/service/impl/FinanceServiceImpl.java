package com.microyum.service.impl;

import com.microyum.common.enums.CurrencyEnum;
import com.microyum.common.enums.FinanceTypeEnum;
import com.microyum.common.http.BaseResponseDTO;
import com.microyum.common.http.HttpStatus;
import com.microyum.dao.MyFinanceDao;
import com.microyum.dto.AssetAllocationDto;
import com.microyum.dto.TraderAccountDto;
import com.microyum.model.MyFinanceTraderAccount;
import com.microyum.service.FinanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FinanceServiceImpl implements FinanceService {

    @Autowired
    private MyFinanceDao myFinanceDao;

    @Override
    public BaseResponseDTO traderAccountOverview(int page, int limit, String trader) {

        int start = (page - 1) * limit;
        List<TraderAccountDto> dtos = myFinanceDao.traderAccountOverview(start, limit, trader);
        BaseResponseDTO responseDTO = new BaseResponseDTO(HttpStatus.OK_LAYUI, dtos);
        responseDTO.setCount(myFinanceDao.traderAccountOverviewCount(trader));
        return responseDTO;
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

    @Override
    public Map<Long, String> traderAccountByUid(Long uid) {

        List<MyFinanceTraderAccount> traderAccountList = myFinanceDao.traderAccountByUid(uid);
        return traderAccountList.stream().collect(Collectors.toMap(MyFinanceTraderAccount::getId, MyFinanceTraderAccount::getTrader));
    }

    @Override
    public BaseResponseDTO assetAllocationOverview(int page, int limit, String trader, String stock) {

        int start = (page - 1) * limit;

        List<AssetAllocationDto> dtos = myFinanceDao.assetAllocationOverview(start, limit, trader, stock);

        BaseResponseDTO responseDTO = new BaseResponseDTO(HttpStatus.OK_LAYUI, dtos);
        responseDTO.setCount(myFinanceDao.assetAllocationOverviewCount(trader, stock));

        return responseDTO;
    }

    @Override
    public boolean assetAllocationCreate(AssetAllocationDto dto) {

        dto.setAssetType(FinanceTypeEnum.of(Integer.parseInt(dto.getAssetType())).getName());
        dto.setCurrency(CurrencyEnum.of(Integer.parseInt(dto.getCurrency())).getName());

        try {
            int result = myFinanceDao.assetAllocationCreate(dto);
            if (result != 1) {
                log.info("Create asset allocation error, result = " + result);
                return false;
            }
        } catch (Exception e) {
            log.error("Create asset allocation error.", e);
            return false;
        }

        return true;
    }

    @Override
    public boolean assetAllocationUpdate(AssetAllocationDto dto) {

        try {
            int result = myFinanceDao.assetAllocationUpdate(dto);
            if (result != 1) {
                log.info("Update asset allocation error, result = " + result);
                return false;
            }
        } catch (Exception e) {
            log.error("Update asset allocation error.", e);
            return false;
        }

        return true;
    }

    @Override
    public boolean assetAllocationDelete(Long id) {

        try {
            int result = myFinanceDao.assetAllocationDelete(id);
            if (result != 1) {
                log.info("Delete asset allocation error, result = " + result);
                return false;
            }
        } catch (Exception e) {
            log.error("Delete asset allocation error.", e);
            return false;
        }

        return true;
    }
}
