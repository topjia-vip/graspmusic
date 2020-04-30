package com.topjia.music.grasp.service.recommend.impl;

import com.alibaba.fastjson.*;
import com.topjia.music.grasp.base.BaseParamsAndValues;
import com.topjia.music.grasp.entity.disst.Disst;
import com.topjia.music.grasp.service.recommend.ChangeDisstListService;
import com.topjia.music.grasp.utils.HttpDelegate;
import org.apache.http.NameValuePair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wjh
 * @date 2019-12-08 18:38
 */
@Service
public class ChangeDisstListServiceImpl implements ChangeDisstListService {
    @Override
    public List<Disst> changeDisstList(Integer categoryId) throws Exception {
        return getDisstLsit(categoryId);
    }

    private List<Disst> getDisstLsit(Integer categoryId) throws Exception {
        String url = "https://u.y.qq.com/cgi-bin/musicu.fcg";
        String data;
        if (categoryId == 1) {
            data = "{\"comm\":{\"ct\":24},\"recomPlaylist\":{\"method\":\"get_hot_recommend\",\"param\":{\"async\":1,\"cmd\":2},\"module\":\"playlist.HotRecommendServer\"}}";
        } else {
            data = "{\"comm\":{\"ct\":24},\"playlist\":{\"method\":\"get_playlist_by_category\",\"param\":{\"id\":" + categoryId + ",\"curPage\":1,\"size\":40,\"order\":5,\"titleid\":3317},\"module\":\"playlist.PlayListPlazaServer\"}}";
        }
        JSONObject jsonObject = JSON.parseObject(data);
        Object[] params = new Object[]{
                BaseParamsAndValues.G_TK,
                BaseParamsAndValues.IN_CHAR_SET,
                BaseParamsAndValues.OUT_CHAR_SET,
                BaseParamsAndValues.FORMAT,
                BaseParamsAndValues.NOTICE,
                "loginUin",
                "hostUin",
                "platform",
                "needNewCode",
                "data",
        };
        Object[] values = new Object[]{
                BaseParamsAndValues.G_TK_VALUE,
                BaseParamsAndValues.IN_CHAR_SET_VALUE,
                BaseParamsAndValues.OUT_CHAR_SET_VALUE,
                BaseParamsAndValues.FORMAT_VALUE,
                BaseParamsAndValues.NOTICE_VALUE,
                "0",
                "0",
                "yqq.json",
                "0",
                JSON.toJSON(jsonObject),
        };
        List<NameValuePair> paramsList = HttpDelegate.getParams(params, values);
        JSONObject getRes = (JSONObject) HttpDelegate.sendGet(url, paramsList, null);
        // 处理数据
        return handleResData(getRes, categoryId);
    }

    private List<Disst> handleResData(JSONObject getRes, Integer categoryId) {
        ArrayList<Disst> dissts = new ArrayList<>();
        if (categoryId == 1) {
            JSONArray recomDisstlistArray = getRes.getJSONObject("recomPlaylist").getJSONObject("data").getJSONArray("v_hot");
            for (Object o1 : recomDisstlistArray) {
                Disst disst = new Disst();
                JSONObject dissObj = (JSONObject) o1;
                disst.setDisstId(dissObj.getString("content_id"));
                disst.setDisstLogo(dissObj.getString("cover"));
                disst.setNickname(dissObj.getString("username"));
                disst.setVisitnum(dissObj.getInteger("listen_num"));
                disst.setDisstName(dissObj.getString("title"));
                dissts.add(disst);
            }
        } else {
            JSONArray disstlistArray = getRes.getJSONObject("playlist").getJSONObject("data").getJSONArray("v_playlist");
            for (Object o : disstlistArray) {
                Disst disst = new Disst();
                JSONObject dissObj = (JSONObject) o;
                disst.setDisstId(dissObj.getString("tid"));
                disst.setDisstLogo(dissObj.getString("cover_url_medium"));
                disst.setNickname(dissObj.getJSONObject("creator_info").getString("nick"));
                disst.setVisitnum(dissObj.getInteger("access_num"));
                disst.setDisstName(dissObj.getString("title"));
                dissts.add(disst);
            }
        }
        return dissts;
    }


}
