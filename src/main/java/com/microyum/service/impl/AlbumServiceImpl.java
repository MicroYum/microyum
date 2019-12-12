package com.microyum.service.impl;

import com.google.common.collect.Maps;
import com.microyum.common.Constants;
import com.microyum.common.http.BaseResponseDTO;
import com.microyum.common.http.HttpStatus;
import com.microyum.dao.MyAlbumDao;
import com.microyum.dao.MyAlbumDetailDao;
import com.microyum.dto.AlbumRequestDto;
import com.microyum.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AlbumServiceImpl implements AlbumService {

    @Autowired
    private MyAlbumDao albumDao;
    @Autowired
    private MyAlbumDetailDao albumDetailDao;

    @Override
    public BaseResponseDTO listActiveAlbum(int pageNo, int pageSize) {
        return null;
    }

    @Override
    public BaseResponseDTO findAlbumDetailById(Long id) {

        List<Map<String, String>> listAlbumDetail = albumDetailDao.findAlbumDetailById(id);

        return new BaseResponseDTO(HttpStatus.OK, listAlbumDetail);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, RuntimeException.class})
    public BaseResponseDTO saveAlbum(AlbumRequestDto dto) {

        Long albumId = albumDao.findIdBySummary(dto.getSummary());
        if (albumId == null) {
            Map<String, Object> mapAlbum = Maps.newHashMap();
            mapAlbum.put("userId", 1L);
            mapAlbum.put("summary", dto.getSummary());
            mapAlbum.put("cover", dto.getCover());
            mapAlbum.put("status", Constants.BLOG_STATUS_ACTIVE);
            albumDao.save(mapAlbum);

            albumId = albumDao.findIdBySummary(dto.getSummary());
            if (albumId == null) {
                return new BaseResponseDTO(HttpStatus.ERROR_IN_DATABASE);
            }
        }

        Map<String, Object>[] maps = new HashMap[dto.getPaths().size()];
        for (int i = 0; i < dto.getPaths().size(); i++) {
            Map<String, Object> map = Maps.newHashMap();
            map.put("albumId", albumId);
            map.put("path", dto.getPaths().get(i));
            map.put("status", Constants.BLOG_STATUS_ACTIVE);
            maps[i] = map;
        }
        albumDetailDao.batchSave(maps);

        return new BaseResponseDTO(HttpStatus.OK);
    }
}
