package com.microyum.service;

import com.microyum.dto.TraderAccountDto;

import java.util.List;

public interface FinanceService {

    List<TraderAccountDto> traderAccountOverview(int page, int limit, String trader);

    boolean traderAccountCreate(TraderAccountDto dto);

    boolean traderAccountUpdate(TraderAccountDto dto);

    boolean traderAccountDelete(Long id);
}
