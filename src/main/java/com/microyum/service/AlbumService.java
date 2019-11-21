package com.microyum.service;

import com.microyum.common.http.BaseResponseDTO;
import com.microyum.dto.AlbumRequestDTO;
import com.microyum.model.MyAlbumDetail;

public interface AlbumService {

    BaseResponseDTO listActiveAlbum(int pageNo, int pageSize);

    BaseResponseDTO findAlbumDetailById(Long id);

    BaseResponseDTO saveAlbum(AlbumRequestDTO dto);
}
