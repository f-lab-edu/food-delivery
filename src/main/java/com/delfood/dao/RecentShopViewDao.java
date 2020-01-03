package com.delfood.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class RecentShopViewDao {

    private static final String RECENT_SHOP_VIEW_KEY = "recent:view:shop:";

    // 최대 조회 가능한 매장수
    private static final int MAX_LIST_SIZE = 10;

    @Autowired
    private RedisTemplate<String, Long> shopRedisTemplate;

    /**
     * 방문한 매장 정보를 추가한다.
     *
     * @param memberId 회원 아이디
     * @param shopId 매장 아이디
     */
    public void add(String memberId, Long shopId) {
        ZSetOperations<String, Long> zSetOperations = shopRedisTemplate.opsForZSet();
        zSetOperations.add(RECENT_SHOP_VIEW_KEY + memberId, shopId, System.currentTimeMillis());
        zSetOperations.removeRange(RECENT_SHOP_VIEW_KEY + memberId, MAX_LIST_SIZE + 1, -1);
    }

    /**
     * 최근 조회한 매장목록을 조회한다.
     * 키에 해당하는 매장목록이 없는 경우 빈 리스트 반환
     *
     * @param memberId 회원 아이디
     * @return 최근 조회한 매장 목록
     */
    public List<Long> getRecentShopView(String memberId) {
        return new ArrayList<>(Objects.requireNonNull(
                shopRedisTemplate.opsForZSet().reverseRange(RECENT_SHOP_VIEW_KEY + memberId, 0, -1)));
    }
}
