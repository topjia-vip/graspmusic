package com.topjia.music.grasp.service.recommend;

import com.topjia.music.grasp.entity.disst.Disst;

import java.util.List;

/**
 * @author wjh
 * @date 2019-12-08 18:38
 */
public interface ChangeDisstListService {
    List<Disst> changeDisstList(Integer categoryId) throws Exception;
}
