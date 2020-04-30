package com.topjia.music.grasp.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 过滤歌手
 *
 * @author wjh
 * @date 2019-11-30 20:39
 */
public class SingerFilterUtil {
    public static String SingerFilter(JSONArray singers) {
        if (singers.isEmpty()) {
            return "";
        }
        String res = "";
        for (int i = 0; i < singers.size(); i++) {
            JSONObject o = (JSONObject) singers.get(i);
            if (i == singers.size() - 1) {
                res += o.getString("name");
            } else {
                res += o.getString("name") + "/";
            }
        }
        return res;
    }
}
