package com.myocean.domain.user.service;

import com.myocean.domain.gamemanagement.enums.GameType;
import com.myocean.domain.user.dto.response.GameCountResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserGameCountService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String GAME_COUNT_KEY_PREFIX = "game_count:";

    //게임 카운트 증가
    public void incrementGameCount(Integer userId, GameType gameType) {
        String key = GAME_COUNT_KEY_PREFIX + userId;
        redisTemplate.opsForHash().increment(key, gameType.name(), 1);
    }

    public Map<String, Integer> getGameCounts(Integer userId) {
        String key = GAME_COUNT_KEY_PREFIX + userId;

        // Redis 연결 테스트
        try {
            redisTemplate.opsForValue().set("test_key", "test_value");
            String testValue = (String) redisTemplate.opsForValue().get("test_key");
        } catch (Exception e) {
        }

        Map<Object, Object> rawCounts = redisTemplate.opsForHash().entries(key);
        Map<String, Integer> gameCounts = new HashMap<>();

        // 모든 게임 타입 초기화
        for (GameType gameType : GameType.values()) {
            Object count = rawCounts.get(gameType.name());
            gameCounts.put(gameType.name(), count != null ? ((Number) count).intValue() : 0);
        }
        return gameCounts;
    }

    // 유저의 게임 카운트 조회
    public GameCountResponse getGameCountResponse(Integer userId) {
        Map<String, Integer> counts = getGameCounts(userId);

        return new GameCountResponse(
                counts.get("GNG"),
                counts.get("UG"),
                counts.get("BART")
        );
    }

    //유저의 게임 카운트 삭제 (PersonaProfile 생성 후)
    public void clearGameCounts(Integer userId) {
        String key = GAME_COUNT_KEY_PREFIX + userId;
        redisTemplate.delete(key);
    }

    //모든 게임이 최소 3회 이상인지 확인
    public boolean hasEnoughGameCounts(Integer userId) {
        Map<String, Integer> counts = getGameCounts(userId);

        for (GameType gameType : GameType.values()) {
            if (counts.get(gameType.name()) < 3) {
                return false;
            }
        }
        return true;
    }
}