package com.microyum.controller;

import com.microyum.common.http.BaseResponseDTO;
import com.microyum.dto.TagBindingDto;
import com.microyum.dto.TagDto;
import com.microyum.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TagController {

    @Autowired
    private TagService tagService;

    @PostMapping(value = "/tag/create")
    public BaseResponseDTO createTag(TagDto tagDto) {

        return tagService.createTag(tagDto);
    }

    @PostMapping(value = "/tag/update")
    public BaseResponseDTO updateTag(TagDto tagDto) {

        return tagService.updateTag(tagDto);
    }

    @GetMapping(value = "/tag/{id}/delete")
    public BaseResponseDTO deleteTag(@PathVariable("id") Long id) {

        return tagService.deleteTag(id);
    }

    @GetMapping(value = "/tag/overview", produces = "application/json")
    public BaseResponseDTO tagOverview(int page, int limit, String name) {

        return tagService.tagOverview(page, limit, name);
    }

    /**
     * Tag 绑定
     *
     * @return
     */
    @PostMapping(value = "/tag/binding")
    public BaseResponseDTO tagBinding(TagBindingDto dto) {

        return tagService.tagBinding(dto);
    }

    /**
     * Tag解绑
     *
     * @return
     */
    @PostMapping(value = "/tag/unbinding")
    public BaseResponseDTO tagUnbinding(TagBindingDto dto) {
        return tagService.tagUnbinding(dto);
    }

    @GetMapping(value = "/tag/{tagId}/stocks")
    public BaseResponseDTO getStockListByTag(@PathVariable("tagId") Long tagId) {
        return null;
    }
}
