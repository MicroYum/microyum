package com.microyum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlbumRequestDto {

    private Long id;
    private Long userId;
    private String summary;
    private String cover;
    private List<String> paths;
}
