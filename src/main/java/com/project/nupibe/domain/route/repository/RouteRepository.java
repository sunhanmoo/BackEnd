package com.project.nupibe.domain.route.repository;

import com.project.nupibe.domain.member.entity.Member;
import com.project.nupibe.domain.route.entity.Route;
import com.project.nupibe.domain.store.entity.Store;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route,Long> {

    // 날짜 기준으로 경로 조회
    List<Route> findByDateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);

    // 특정 날짜 범위 내의 경로가 있는 날짜 조회
    @Query("SELECT r.date FROM Route r WHERE r.date BETWEEN :startDate AND :endDate AND r.member.id = :memberId")
    List<LocalDateTime> findDatesBetweenAndMemberId(LocalDateTime startDate, LocalDateTime endDate, Long memberId);

    @Query("SELECT r FROM Route r WHERE r.member = :member")
    List<Route> findByMember(@Param("member") Member member);

    @Query(
            value = "SELECT * FROM route r WHERE LOWER(r.route_name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(r.category) LIKE LOWER(CONCAT('%', :query, '%'))",
            nativeQuery = true
    )
    Slice<Route> findByQuery(Pageable pageable, @Param("query") String query);
}


