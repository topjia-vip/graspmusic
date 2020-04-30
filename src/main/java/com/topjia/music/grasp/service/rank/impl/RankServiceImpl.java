package com.topjia.music.grasp.service.rank.impl;

import com.alibaba.fastjson.*;
import com.topjia.music.grasp.base.BaseParamsAndValues;
import com.topjia.music.grasp.entity.song.Song;
import com.topjia.music.grasp.entity.singer.Singer;
import com.topjia.music.grasp.entity.top.Top;
import com.topjia.music.grasp.entity.top.TopGroup;
import com.topjia.music.grasp.service.rank.RankService;
import com.topjia.music.grasp.utils.HttpDelegate;
import org.apache.http.NameValuePair;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author wjh
 * @date 2019-12-11 19:59
 */
@Service
public class RankServiceImpl implements RankService {
    @Override
    public List<TopGroup> getTopListInfo() throws Exception {
        return getTopListInfoByQQYY();
    }

    @Override
    public Map<String, Object> getTopDetail(Integer topId, String period) throws Exception {
        return getTopDetailByQQYY(topId, period);
    }

    private Map<String, Object> getTopDetailByQQYY(Integer topId, String period) throws Exception {
        String url = "https://u.y.qq.com/cgi-bin/musicu.fcg";
        String data = "{\"detail\":{\"module\":\"musicToplist.ToplistInfoServer\",\"method\":\"GetDetail\",\"param\":{\"topId\":" + topId + ",\"offset\":0,\"num\":300,\"period\":\"" + period + "\"}},\"comm\":{\"ct\":24,\"cv\":0}}";
        JSONObject jsonObject = JSON.parseObject(data);
        Object[] params = new Object[]{
                "-",
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
                "recom5538819072610848",
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
        return handleTopDetailData(getRes);
    }

    private Map<String, Object> handleTopDetailData(JSONObject getRes) {
        JSONObject topDetailObj = getRes.getJSONObject("detail").getJSONObject("data");
        JSONObject jsonTop = topDetailObj.getJSONObject("data");
        HashMap<String, Object> res = new HashMap<>();
        // 处理排行耪详细信息
        Top top = new Top();
        top.setPeriod(jsonTop.getString("period"));
        top.setHeadPicUrl(jsonTop.getString("headPicUrl"));
        top.setFrontPicUrl(jsonTop.getString("frontPicUrl"));
        top.setTopId(jsonTop.getInteger("topId"));
        top.setTopTitle(jsonTop.getString("title"));
        top.setUpdateTips(jsonTop.getString("updateTips"));
        top.setTopHistory(jsonTop.getJSONObject("history"));
        top.setUpdateTime(jsonTop.getString("updateTime"));
        top.setSong(jsonTop.getJSONArray("song"));
        top.setTitleDetail(jsonTop.getString("titleDetail"));
        top.setTitleShare(jsonTop.getString("titleShare"));
        top.setIntro(jsonTop.getString("intro"));
        top.setListenNum(jsonTop.getInteger("listenNum"));
        res.put("topDetail", top);
        // 处理排行榜歌曲
        JSONArray detailObjJSONObject = topDetailObj.getJSONArray("songInfoList");
        ArrayList<Song> songs = new ArrayList<>();
        for (Object o1 : detailObjJSONObject) {
            JSONObject songObj = (JSONObject) o1;
            Song song = new Song();
            song.setId(songObj.getString("id"));
            song.setMid(songObj.getString("mid"));
            JSONArray singers = songObj.getJSONArray("singer");
            ArrayList<Singer> singerArrayList = new ArrayList<>();
            for (Object singero : singers) {
                JSONObject singerObj = (JSONObject) singero;
                Singer singer = new Singer();
                singer.setSingerId(singerObj.getInteger("id"));
                singer.setSingerMid(singerObj.getString("mid"));
                singer.setSingerName(singerObj.getString("name"));
                singer.setSingerPic("https://y.gtimg.cn/music/photo_new/T001R300x300M000" + singerObj.getString("mid") + ".jpg?max_age=2592000");
                singerArrayList.add(singer);
            }
            song.setSingers(singerArrayList);
            song.setName(songObj.getString("name"));
            String album = songObj.getJSONObject("album").getString("name");
            if (!StringUtils.isEmpty(album)) {
                song.setAlbum(album);
                song.setImage("https://y.gtimg.cn/music/photo_new/T002R500x500M000" + songObj.getJSONObject("album").getString("mid") + ".jpg?max_age=2592000");
            }
            song.setDuration(songObj.getString("interval"));
            song.setTitle(songObj.getString("title"));
            song.setSubTitle(songObj.getString("subtitle"));
            songs.add(song);
        }
        res.put("top_song_list", songs);
        return res;
    }

    private List<TopGroup> getTopListInfoByQQYY() throws Exception {
        String url = "https://u.y.qq.com/cgi-bin/musicu.fcg";
        String data = "{\"comm\":{\"ct\":24},\"toplist\":{\"module\":\"musicToplist.ToplistInfoServer\",\"method\":\"GetAll\",\"param\":{}}}";
        JSONObject jsonObject = JSON.parseObject(data);
        Object[] params = new Object[]{
                "-",
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
                "recom5538819072610848",
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

        return handleTopListInfoData(getRes);
    }

    private List<TopGroup> handleTopListInfoData(JSONObject getRes) {
        // 处理排行榜数据
        JSONArray jsonArray = getRes.getJSONObject("toplist").getJSONObject("data").getJSONArray("group");
        ArrayList<TopGroup> topGroups = new ArrayList<>();
        jsonArray.forEach((o) -> {
            JSONObject jsonTopGroup = (JSONObject) o;
            TopGroup topGroup = new TopGroup();
            topGroup.setGroupId(jsonTopGroup.getString("groupId"));
            topGroup.setGroupName(jsonTopGroup.getString("groupName"));
            JSONArray toplist = jsonTopGroup.getJSONArray("toplist");
            ArrayList<Top> tops = new ArrayList<>();
            toplist.forEach((topObj) -> {
                JSONObject jsonTop = (JSONObject) topObj;
                if (!jsonTop.getInteger("topId").equals(201)) {
                    Top top = new Top();
                    top.setPeriod(jsonTop.getString("period"));
                    top.setHeadPicUrl(jsonTop.getString("headPicUrl"));
                    top.setFrontPicUrl(jsonTop.getString("frontPicUrl"));
                    top.setTopId(jsonTop.getInteger("topId"));
                    top.setTopTitle(jsonTop.getString("title"));
                    top.setUpdateTips(jsonTop.getString("updateTips"));
                    top.setTopHistory(jsonTop.getJSONObject("history"));
                    tops.add(top);
                    topGroup.setTopList(tops);
                }
            });
            topGroups.add(topGroup);
        });
        return topGroups;
    }
}
