package com.project.nupibe.domain.store.dto.response;

import lombok.Builder;
import java.util.List;

public class HomeResponseDTO {
    @Builder
    public record GetHomeResponseDTO(
            List<groupNameDTO> groupList,
            List<regionDTO> regions
    ) {}

    @Builder
    public record groupNameDTO(
            String groupName
    ) {}

    @Builder
    public record regionDTO(
            int regionId,
            String regionName
    ){};

    @Builder
    public record entertainmentDTO(
            List<categoryDTO> category,
            String sort,
            List<storeDTO> stores
    ) {};

    @Builder
    public record categoryDTO(
            int cateogoryId,
            String category,
            boolean selected
    ) {};

    @Builder
    public record storeDTO(
            Long storeId,
            String storeName,
            String storePic,
            String storePlace,
            int isFavor
    ) {};
}
