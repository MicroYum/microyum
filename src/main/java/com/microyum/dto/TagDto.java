package com.microyum.dto;

import lombok.Data;

import java.util.List;

@Data
public class TagDto {

    private Long id;
    private String name;
    private Integer category;
    private Long items;
    private List<Long> entityIds;
}
