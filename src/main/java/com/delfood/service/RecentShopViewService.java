package com.delfood.service;

import com.delfood.dao.RecentShopViewDao;
import com.delfood.dto.ShopDTO;
import com.delfood.mapper.ShopMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecentShopViewService {

    @Autowired
    RecentShopViewDao recentShopViewDao;

    @Autowired
    ShopMapper shopMapper;

    public void add(String memberId, Long shopId) {
        recentShopViewDao.add(memberId, shopId);
    }

    public List<ShopDTO> getRecentShopView(String memberId) {
        List<Long> shopIdList = recentShopViewDao.getRecentShopView(memberId);
        return shopMapper.findByIdList(shopIdList);
    }
}
