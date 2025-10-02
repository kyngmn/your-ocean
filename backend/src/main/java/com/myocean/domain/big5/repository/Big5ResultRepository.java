package com.myocean.domain.big5.repository;

import com.myocean.domain.big5.entity.Big5Result;
import com.myocean.domain.big5.enums.Big5SourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Big5ResultRepository extends JpaRepository<Big5Result, Long> {

    List<Big5Result> findByUserIdAndSourceType(Integer userId, Big5SourceType sourceType);

    // 성능 최적화: 특정 사용자의 모든 Big5 결과를 효율적으로 삭제
    void deleteByUserId(Integer userId);
}