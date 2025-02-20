package com.project.nupibe.domain.member.dto.response;

import lombok.Builder;

import java.util.List;

public class MypageResponseDTO{
    @Builder
    public record MypageDTO (
        String email,
        String nickname,
        String profile
    ){}

    @Builder
    public record MemberStoreDTO (
            Long storeId,
            String name,
            String location,
            String storePic
    ){}

    @Builder
    public record MypageStoresDTO (
            List<MemberStoreDTO> bookmarkedStores
    ){}

    @Builder
    public record MemberRouteDTO(
            Long routeId,
            String name,
            String location,
            String routePic
    ) {}

    @Builder
    public record MypageRoutesDTO(
            List<MemberRouteDTO> routes
    ) {}



}
