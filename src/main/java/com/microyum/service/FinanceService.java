package com.microyum.service;

import com.microyum.common.http.BaseResponseDTO;
import com.microyum.dto.AssetAllocationDto;
import com.microyum.dto.TraderAccountDto;

import java.util.Map;

public interface FinanceService {

    BaseResponseDTO traderAccountOverview(int page, int limit, String trader);

    boolean traderAccountCreate(TraderAccountDto dto);

    boolean traderAccountUpdate(TraderAccountDto dto);

    boolean traderAccountDelete(Long id);

    Map<Long, String> traderAccountByUid(Long uid);

    BaseResponseDTO assetAllocationOverview(int page, int limit, String trader, String stock);

    boolean assetAllocationCreate(AssetAllocationDto dto);

    boolean assetAllocationUpdate(AssetAllocationDto dto);

    boolean assetAllocationDelete(Long id);
}
