package com.microyum.service;

import com.microyum.common.http.BaseResponseDTO;
import com.microyum.dto.AlbumRequestDto;

public interface AlbumService {

    BaseResponseDTO listActiveAlbum(int pageNo, int pageSize);

    BaseResponseDTO findAlbumDetailById(Long id);

    BaseResponseDTO saveAlbum(AlbumRequestDto dto);
}
