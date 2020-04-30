package com.topjia.music.grasp.service.singer;

import java.util.HashMap;

/**
 * @author wjh
 * @date 2019-12-10 20:48
 */
public interface SingerService {
    HashMap<String, Object> getSingerList(Integer area, Integer sex, Integer genre, Integer index, Integer sin, Integer cur_page) throws Exception;

    HashMap<String, Object> getSingerDetail(String singerMid) throws Exception;
}
