package com.microyum.controller;

import com.microyum.common.http.BaseResponseDTO;
import com.microyum.common.http.HttpStatus;
import com.microyum.dto.TraderAccountDto;
import com.microyum.service.FinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/finance")
public class FinanceController {

    @Autowired
    private FinanceService financeService;

    @RequestMapping(value = "/trader/account/overview", produces = "application/json")
    public BaseResponseDTO traderAccountOverview(int page, int limit, String trader) {

        return new BaseResponseDTO(HttpStatus.OK_LAYUI, financeService.traderAccountOverview(page, limit, trader));
    }

    @RequestMapping(value = "/trader/account/create", produces = "application/json")
    public BaseResponseDTO traderAccountCreate(TraderAccountDto dto) {

        boolean result = financeService.traderAccountCreate(dto);
        if (!result) {
            return new BaseResponseDTO(HttpStatus.ERROR_IN_DATABASE);
        }

        return new BaseResponseDTO(HttpStatus.OK);
    }

    @RequestMapping(value = "/trader/account/update", produces = "application/json")
    public BaseResponseDTO traderAccountUpdate(TraderAccountDto dto) {

        boolean result = financeService.traderAccountUpdate(dto);
        if (!result) {
            return new BaseResponseDTO(HttpStatus.ERROR_IN_DATABASE);
        }

        return new BaseResponseDTO(HttpStatus.OK);
    }

    @RequestMapping(value = "/trader/account/{id}/delete", produces = "application/json")
    public BaseResponseDTO traderAccountDelete(@PathVariable("id") Long id) {

        boolean result = financeService.traderAccountDelete(id);
        if (!result) {
            return new BaseResponseDTO(HttpStatus.ERROR_IN_DATABASE);
        }

        return new BaseResponseDTO(HttpStatus.OK);
    }
}
