package com.microyum.service;

import com.microyum.common.http.BaseResponseDTO;
import com.microyum.dto.TagBindingDto;
import com.microyum.dto.TagDto;

public interface TagService {

    BaseResponseDTO tagOverview(int page, int limit, String name);

    BaseResponseDTO createTag(TagDto tagDto);

    BaseResponseDTO updateTag(TagDto tagDto);

    BaseResponseDTO deleteTag(Long id);

    BaseResponseDTO tagBinding(TagBindingDto tagBindingDto);

    BaseResponseDTO tagUnbinding(TagBindingDto tagBindingDto);
}
