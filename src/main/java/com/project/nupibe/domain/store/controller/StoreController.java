package com.project.nupibe.domain.store.controller;

import com.project.nupibe.domain.store.dto.response.StoreResponseDTO;
import com.project.nupibe.domain.store.service.StoreCommandService;
import com.project.nupibe.domain.store.service.StoreQueryService;
import com.project.nupibe.global.apiPayload.CustomResponse;
import com.project.nupibe.global.config.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
@Tag(name = "장소 API")
public class StoreController {
    private final StoreQueryService storeQueryService;
    private final StoreCommandService storeCommandService;
    private final SecurityUtil securityUtil;

    @GetMapping("{storeId}/detail")
    @Operation(method = "GET", summary = "장소 단일 조회(detail) API", description = "장소 상세페이지 조회입니다.")
    public CustomResponse<StoreResponseDTO.StoreDetailResponseDTO> getStoreDetail(
            @PathVariable("storeId") Long storeId) {

        Long memberId = securityUtil.getMemberIdFromTokens();
        StoreResponseDTO.StoreDetailResponseDTO responseDTO = storeQueryService.getStoreDetail(storeId, memberId);

        return CustomResponse.onSuccess(responseDTO);
    }

    @GetMapping("{storeId}/routes")
    @Operation(method = "GET", summary = "특정 장소가 포함된 경로 조회", description = "storeId를 사용하여 해당 장소가 포함된 경로들을 조회하는 기능입니다."
    )
    public CustomResponse<StoreResponseDTO.MemberRouteListDTO> getRoutesByStoreId(@PathVariable("storeId") Long storeId) {
        StoreResponseDTO.MemberRouteListDTO routes = storeCommandService.getRoutesByStoreId(storeId);
        return CustomResponse.onSuccess(routes);
    }

    @GetMapping("/{storeId}/images")
    @Operation(method = "GET", summary = "특정 가게의 이미지들 조회", description = "상세페이지 조회 내 사진 탭 기능입니다.")
    public CustomResponse<StoreResponseDTO.StoreImagesDTO> getTabImages(@PathVariable("storeId") Long storeId) {
        StoreResponseDTO.StoreImagesDTO storeImagesDTO = storeQueryService.getTabImages(storeId);
        return CustomResponse.onSuccess(storeImagesDTO);

    }

    @PostMapping("{storeId}/bookmark")
    @Operation(method = "POST", summary = "장소 북마크 API", description = "장소 조회 시 북마크 버튼을 클릭시 작동하는 기능입니다..")
    public CustomResponse<StoreResponseDTO.savedDTO> bookmarkStore( @PathVariable("storeId") Long storeId) {
        Long memberId = securityUtil.getMemberIdFromTokens();

        StoreResponseDTO.savedDTO saved = storeCommandService.bookmarkStore(memberId, storeId);
        return CustomResponse.onSuccess(saved);
    }
    @PostMapping("{storeId}/like")
    @Operation(method = "POST", summary = "장소 좋아요 API", description = "장소 조회 시 좋아요 버튼을 클릭시 작동하는 기능입니다.")
    public CustomResponse<StoreResponseDTO.savedDTO> likeStore( @PathVariable("storeId") Long storeId) {
        Long memberId = securityUtil.getMemberIdFromTokens();
        StoreResponseDTO.savedDTO saved = storeCommandService.likeStore(memberId, storeId);
        return CustomResponse.onSuccess(saved);
    }

    @GetMapping("/{storeId}/preview")
    @Operation(method = "GET", summary = "장소 단일 조회(preview) API", description = "지도 마커 클릭 시 나오는 장소 단일 조회입니다.")
    public CustomResponse<StoreResponseDTO.StorePreviewDTO> getStorePreview(
            @PathVariable("storeId") Long storeId
    ){
        StoreResponseDTO.StorePreviewDTO result = storeQueryService.getStorePreview(storeId);
        return CustomResponse.onSuccess(HttpStatus.OK, result);
    }

    @GetMapping("/around")
    @Operation(method = "GET", summary = "내 주변 장소 조회 API", description = "현재 위치 주변에 있는 장소들을 조회하는 API입니다.")
    @Parameters({
            @Parameter(name = "cursor", description = "커서 값, 처음이면 0"),
            @Parameter(name = "query", description = "쿼리 거리순, 북마크순, 추천순")
    })
    public CustomResponse<StoreResponseDTO.StorePageDTO> getStores(
            @RequestParam (value="query", defaultValue = "거리순") String query,
            @RequestParam(value="cursor", defaultValue = "0") Long cursor,
            @RequestParam(value = "offset", defaultValue = "8") int offset,
            @RequestParam(value="latitude", required = true) float lat,
            @RequestParam(value="longitude", required = true) float lng
    ){
        StoreResponseDTO.StorePageDTO result = storeQueryService.getStores(query, lat, lng, cursor, offset);
        return CustomResponse.onSuccess(HttpStatus.OK, result);
    }

    @GetMapping("/search")
    @Operation(method = "GET", summary = "장소 검색 조회 API", description = "키워드 검색을 통한 장소 조회 API입니다.")
    @Parameters({
            @Parameter(name = "cursor", description = "커서 값, 처음이면 0"),
            @Parameter(name = "query", description = "쿼리 거리순, 북마크순, 추천순")
    })
    public CustomResponse<StoreResponseDTO.StorePageDTO> getStoresWithQuery(
            @RequestParam (value="query", defaultValue = "거리순") String query,
            @RequestParam (value="search") String search,
            @RequestParam(value="cursor", defaultValue = "0") Long cursor,
            @RequestParam(value = "offset", defaultValue = "8") int offset,
            @RequestParam(value="latitude",required = false) Float lat,
            @RequestParam(value="longitude",required = false) Float lng
    ){
        float latitude = (lat != null) ? lat : 0.0f;
        float longitude = (lng != null) ? lng : 0.0f;
        StoreResponseDTO.StorePageDTO result = storeQueryService.getStoresWithQuery(query, latitude, longitude, search, cursor, offset);
        return CustomResponse.onSuccess(HttpStatus.OK, result);
    }
}

