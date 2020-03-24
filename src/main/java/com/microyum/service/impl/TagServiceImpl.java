package com.microyum.service.impl;

import com.google.common.collect.Lists;
import com.microyum.common.enums.TagCategoryEnum;
import com.microyum.common.http.BaseResponseDTO;
import com.microyum.common.http.HttpStatus;
import com.microyum.common.util.StringUtils;
import com.microyum.dao.jdbc.MyTagJdbcDao;
import com.microyum.dao.jpa.MyTagBindingDao;
import com.microyum.dao.jpa.MyTagDao;
import com.microyum.dto.TagBindingDto;
import com.microyum.dto.TagDto;
import com.microyum.model.common.MyTag;
import com.microyum.model.common.MyTagBinding;
import com.microyum.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private MyTagJdbcDao tagJdbcDao;
    @Autowired
    private MyTagDao tagDao;
    @Autowired
    private MyTagBindingDao tagBindingDao;

    @Override
    public BaseResponseDTO tagOverview(int page, int limit, String name) {

        int start = (page - 1) * limit;
        long count;
        List<TagDto> tagList;
        if (StringUtils.isNotBlank(name)) {
            tagList = tagJdbcDao.findByTagNamePaging(start, limit, name);
            count = tagJdbcDao.findByTagNameCount(name);
        } else {
            tagList = tagJdbcDao.findByTagNamePaging(start, limit);
            count = tagJdbcDao.findByTagNameCount();
        }

        BaseResponseDTO responseDTO = new BaseResponseDTO(HttpStatus.OK_LAYUI, tagList);
        responseDTO.setCount(count);

        return responseDTO;
    }

    @Transactional
    @Override
    public BaseResponseDTO createTag(TagDto tagDto) {

        MyTag tag = new MyTag();
        tag.setName(tagDto.getName());
        tag.setCategory(TagCategoryEnum.of(Integer.valueOf(tagDto.getCategory())).getName());
        tag.setStatus(1);
        if (StringUtils.isBlank(tagDto.getEntityIds())) {
            tag.setItems(0L);
        } else {
            tag.setItems(Long.valueOf(tagDto.getEntityIds().split(",").length));
        }

        tag = tagDao.save(tag);
        saveTagBinding(tagDto, tag);

        return new BaseResponseDTO(HttpStatus.OK);
    }

    @Transactional
    @Override
    public BaseResponseDTO updateTag(TagDto tagDto) {

        tagBindingDao.deleteByTagId(tagDto.getId());

        MyTag tag = new MyTag();
        tag.setId(tagDto.getId());
        tag.setName(tagDto.getName());
        tag.setCategory(TagCategoryEnum.of(Integer.valueOf(tagDto.getCategory())).getName());
        if (StringUtils.isBlank(tagDto.getEntityIds())) {
            tag.setItems(0L);
        } else {
            tag.setItems(Long.valueOf(tagDto.getEntityIds().split(",").length));
        }
        tag.setStatus(1);
        tagDao.save(tag);

        saveTagBinding(tagDto, tag);

        return new BaseResponseDTO(HttpStatus.OK);
    }

    @Transactional
    @Override
    public BaseResponseDTO deleteTag(Long id) {

        tagDao.deleteById(id);
        tagBindingDao.deleteByTagId(id);
        return new BaseResponseDTO(HttpStatus.OK);
    }

    @Transactional
    @Override
    public BaseResponseDTO tagBinding(TagBindingDto dto) {

        MyTagBinding tagBinding = new MyTagBinding();
        BeanUtils.copyProperties(dto, tagBinding);
        tagBindingDao.save(tagBinding);

        Long items = tagBindingDao.findCountByTagId(dto.getTagId());

        MyTag tag = tagDao.findByTagId(dto.getTagId());
        tag.setItems(items);

        tagDao.save(tag);

        return new BaseResponseDTO(HttpStatus.OK);
    }

    @Transactional
    @Override
    public BaseResponseDTO tagUnbinding(TagBindingDto dto) {

        tagBindingDao.deleteEntity(dto.getTagId(), dto.getEntityId(), dto.getCategory());

        Long items = tagBindingDao.findCountByTagId(dto.getTagId());

        MyTag tag = tagDao.findByTagId(dto.getTagId());
        tag.setItems(items);

        tagDao.save(tag);

        return new BaseResponseDTO(HttpStatus.OK);
    }

    @Override
    public BaseResponseDTO findEntityByTagId(Long id) {

        List<String> tagNames = tagJdbcDao.findEntityNameByTagId(id);
        return new BaseResponseDTO(HttpStatus.OK, tagNames);
    }

    private void saveTagBinding(TagDto tagDto, MyTag tag) {

        if (StringUtils.isBlank(tagDto.getEntityIds()) || tagDto.getEntityIds().split(",").length == 0) {
            return;
        }

        List<MyTagBinding> tagBindings = Lists.newArrayList();
        for (String entityId : tagDto.getEntityIds().split(",")) {
            MyTagBinding tagBinding = new MyTagBinding();
            tagBinding.setTagId(tag.getId());
            tagBinding.setEntityId(Long.valueOf(entityId));
            tagBinding.setCategory(Integer.valueOf(tagDto.getCategory()));
            tagBindings.add(tagBinding);
        }

        tagBindingDao.saveAll(tagBindings);
    }
}
