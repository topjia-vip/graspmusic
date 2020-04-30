package com.topjia.music.grasp.service.rank;

import com.topjia.music.grasp.entity.top.TopGroup;

import java.util.List;
import java.util.Map;

/**
 * @author wjh
 * @date 2019-12-11 19:59
 */
public interface RankService {
    List<TopGroup> getTopListInfo() throws Exception;

    Map<String, Object> getTopDetail(Integer topId, String period) throws Exception;
}
