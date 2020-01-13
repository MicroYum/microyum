package com.microyum.dto;

import lombok.Data;

import java.util.List;

@Data
public class TagDto {

    private Long id;
    private String name;
    private String category;
    private Long items;
    private String entityIds;
    private String lastUpdateTime;
}
