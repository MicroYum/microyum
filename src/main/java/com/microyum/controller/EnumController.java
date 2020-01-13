package com.microyum.controller;

import com.google.common.collect.Lists;
import com.microyum.common.enums.*;
import com.microyum.common.http.BaseResponseDTO;
import com.microyum.dto.EnumDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/enum")
public class EnumController {

    @GetMapping(value = "/finance/type", produces = "application/json")
    public BaseResponseDTO financeTypeEnum() {

        List<EnumDto> dtoList = Lists.newArrayList();
        for (FinanceTypeEnum item : FinanceTypeEnum.values()) {
            dtoList.add(new EnumDto(item.getCode(), item.getName()));
        }

        return BaseResponseDTO.OK(dtoList);
    }

    @GetMapping(value = "/currency", produces = "application/json")
    public BaseResponseDTO currencyEnum() {

        List<EnumDto> dtoList = Lists.newArrayList();
        for (CurrencyEnum item : CurrencyEnum.values()) {
            dtoList.add(new EnumDto(item.getCode(), item.getName()));
        }

        return BaseResponseDTO.OK(dtoList);
    }

    @GetMapping(value = "/stock/strategy", produces = "application/json")
    public BaseResponseDTO stockStrategy() {

        List<EnumDto> dtoList = Lists.newArrayList();
        for (StockStrategyEnum item : StockStrategyEnum.values()) {
            dtoList.add(new EnumDto(item.getCode(), item.getName()));
        }

        return BaseResponseDTO.OK(dtoList);
    }

    @GetMapping(value = "/stock/type", produces = "application/json")
    public BaseResponseDTO stockType() {

        List<EnumDto> dtoList = Lists.newArrayList();
        for (StockTypeEnum item : StockTypeEnum.values()) {
            dtoList.add(new EnumDto(item.getCode(), item.getName()));
        }

        return BaseResponseDTO.OK(dtoList);
    }

    @GetMapping(value = "/tag/category", produces = "application/json")
    public BaseResponseDTO tagCategory() {

        List<EnumDto> dtoList = Lists.newArrayList();
        for (TagCategoryEnum item : TagCategoryEnum.values()) {
            dtoList.add(new EnumDto(item.getCode(), item.getName()));
        }

        return BaseResponseDTO.OK(dtoList);
    }
}
