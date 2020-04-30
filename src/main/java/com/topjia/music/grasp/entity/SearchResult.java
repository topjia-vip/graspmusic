package com.topjia.music.grasp.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * @author wjh
 * @date 2019-11-30 22:43
 */
@Data
public class SearchResult {
    private JSONObject songs;
    private JSONObject zhida;
}
