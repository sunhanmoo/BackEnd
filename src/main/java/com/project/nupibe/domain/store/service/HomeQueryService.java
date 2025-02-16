package com.project.nupibe.domain.store.service;

import com.project.nupibe.domain.member.entity.Member;
import com.project.nupibe.domain.member.exception.code.MemberErrorCode;
import com.project.nupibe.domain.member.exception.handler.MemberException;
import com.project.nupibe.domain.member.repository.MemberRepository;
import com.project.nupibe.domain.member.repository.MemberRouteRepository;
import com.project.nupibe.domain.member.repository.MemberStoreRepository;
import com.project.nupibe.domain.region.entity.Region;
import com.project.nupibe.domain.region.repository.RegionRepository;
import com.project.nupibe.domain.route.entity.Route;
import com.project.nupibe.domain.route.repository.RouteRepository;
import com.project.nupibe.domain.route.repository.RouteStoreRepository;
import com.project.nupibe.domain.store.converter.HomeConverter;
import com.project.nupibe.domain.store.dto.response.HomeResponseDTO;
import com.project.nupibe.domain.store.entity.Store;
import com.project.nupibe.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class HomeQueryService {
    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;
    private final RegionRepository regionRepository;
    private final RouteRepository routeRepository;
    private final MemberRouteRepository memberRouteRepository;
    private final RouteStoreRepository routeStoreRepository;
    private final MemberStoreRepository memberStoreRepository;

    public HomeResponseDTO.GetHomeResponseDTO getHome(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));

        List<String> groupNames = storeRepository.findAllGroup();
        List<HomeResponseDTO.groupNameDTO> groupList = HomeConverter.toGroupName(groupNames);

        List<HomeResponseDTO.regionDTO> regions = new ArrayList<>();
        for(Region region : regionRepository.findAll()) {
            int id = region.getId();
            String name = region.getName();
            HomeResponseDTO.regionDTO place = HomeConverter.toRegionDTO(id, name);
            regions.add(place);
        }

        return HomeConverter.toGetHome(groupList, regions);
    }

    public HomeResponseDTO.entertainmentDTO getEntertainment(Long memberId, double latitude, double longitude, int selected, String sort) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));

        List<String> categories = List.of("전체", "소품샵", "굿즈샵", "맛집", "카페", "테마카페", "팝업", "전시", "클래스");
        HomeResponseDTO.categoryDTO category = HomeConverter.toCategoryDTO(categories, selected);

        int sortId = 0;
        switch (sort) {
            case "default": sortId = 1; break;
            case "bookmark": sortId = 2; break;
            case "recommend": sortId = 3; break;
        }

        List<Store> stores = getStoreByConditions(categories, selected, sortId, latitude, longitude);

        List<Boolean> isFavors = new ArrayList<>();
        for(Store store : stores) {
            boolean isFavor = memberStoreRepository.existsByMemberIdAndStoreId(member.getId(), store.getId());
            isFavors.add(isFavor);
        }
        List<HomeResponseDTO.storeDTO> storeList = HomeConverter.toStoreDTO(isFavors, stores);

        return HomeConverter.toEntertainmentDTO(category, sort, storeList);
    }

    public HomeResponseDTO.groupStoreDTO getRegionStore(Long memberId, Long regionId, double latitude, double longitude, int selected, String sort) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));

        List<String> categories = List.of("전체", "소품샵", "굿즈샵", "맛집", "카페", "테마카페", "팝업", "전시", "클래스");
        HomeResponseDTO.categoryDTO category = HomeConverter.toCategoryDTO(categories, selected);

        int sortId = 0;
        switch (sort) {
            case "default": sortId = 1; break;
            case "bookmark": sortId = 2; break;
            case "recommend": sortId = 3; break;
        }

        List<Store> stores = getStoreByConditionsAndRegion(categories, regionId, selected, sortId, latitude, longitude);

        List<Boolean> isFavors = new ArrayList<>();
        for(Store store : stores) {
            boolean isFavor = memberStoreRepository.existsByMemberIdAndStoreId(member.getId(), store.getId());
            isFavors.add(isFavor);
        }
        List<HomeResponseDTO.storeDTO> storeList = HomeConverter.toStoreDTO(isFavors, stores);
        return HomeConverter.toRegionStoreDTO(storeList);
    }

    private List<Store> getStoreByConditions(List<String> categories, int selected, int sortId, double latitude, double longitude) {
        if(selected == 0) {
            switch(sortId) {
                case 1: return storeRepository.findAllOrderDistance(latitude, longitude);
                case 2: return storeRepository.findAllOrderBookmark();
                case 3: return storeRepository.findAllOrderRecommend();
            }
        }
        else {
            switch(sortId) {
                case 1: return storeRepository.findCategoryOrderDistance(categories.get(selected), latitude, longitude);
                case 2: return storeRepository.findCategoryOrderBookmark(categories.get(selected));
                case 3: return storeRepository.findCategoryOrderRecommend(categories.get(selected));
            }
        }
        return new ArrayList<Store>();
    }

    private List<Store> getStoreByConditionsAndRegion(List<String> categories, long regionId, int selected, int sortId, double latitude, double longitude) {
        if(selected == 0) {
            switch(sortId) {
                case 1: return storeRepository.findAllOrderDistanceAndRegion(latitude, longitude, regionId);
                case 2: return storeRepository.findAllOrderBookmarkAndRegion(regionId);
                case 3: return storeRepository.findAllOrderRecommendAndRegion(regionId);
            }
        }
        else {
            switch(sortId) {
                case 1: return storeRepository.findCategoryOrderDistanceAndRegion(categories.get(selected), latitude, longitude, regionId);
                case 2: return storeRepository.findCategoryOrderBookmarkAndRegion(categories.get(selected), regionId);
                case 3: return storeRepository.findCategoryOrderRecommendAndRegion(categories.get(selected), regionId);
            }
        }
        return new ArrayList<Store>();
    }

    public HomeResponseDTO.myRouteDTO getRoute(Long memberId, String routeType) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));

        List<Route> routes = new ArrayList<>();
        switch (routeType) {
            case "created" :
                routes = routeRepository.findByMember(member);
                break;
            case "saved" :
                routes = memberRouteRepository.findByMember(member);
        }

        List<String> images = new ArrayList<>();
        for(Route route : routes) {
            String pic = routeStoreRepository.findFirstImage(route);
            images.add(pic);
        }

        return HomeConverter.toMyRouteDTO(routes, images);
    }

    public HomeResponseDTO.groupStoreDTO getGroupStore(Long memberId, String groupName) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));

        List<Store> stores = storeRepository.findByGroupName(groupName);
        List<Boolean> isFavors = new ArrayList<>();
        for(Store store : stores) {
            boolean isFavor = memberStoreRepository.existsByMemberIdAndStoreId(member.getId(), store.getId());
            isFavors.add(isFavor);
        }
        return HomeConverter.toGroupStoreDTO(stores, isFavors);
    }
}
