package com.topjia.music.grasp.service.singer.impl;

import com.alibaba.fastjson.*;
import com.topjia.music.grasp.base.BaseParamsAndValues;
import com.topjia.music.grasp.entity.RequestHeader;
import com.topjia.music.grasp.entity.singer.Singer;
import com.topjia.music.grasp.entity.singer.SingerTag;
import com.topjia.music.grasp.entity.song.Song;
import com.topjia.music.grasp.service.singer.SingerService;
import com.topjia.music.grasp.utils.HttpDelegate;
import org.apache.http.NameValuePair;
import org.dom4j.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author wjh
 * @date 2019-12-10 20:48
 */
@Service
public class SingerServiceImpl implements SingerService {

    @Override
    public HashMap<String, Object> getSingerList(Integer area, Integer sex, Integer genre, Integer index, Integer sin, Integer cur_page) throws Exception {
        return getSingers(area, sex, genre, index, sin, cur_page);
    }

    @Override
    public HashMap<String, Object> getSingerDetail(String singerMid) throws Exception {
        return getSingerDetailByQQYY(singerMid);
    }

    private HashMap<String, Object> getSingerDetailByQQYY(String singerMid) throws Exception {
        // 获取当前歌手的简介
        HashMap<String, Object> res = new HashMap<>();
        String url = "https://c.y.qq.com/splcloud/fcgi-bin/fcg_get_singer_desc.fcg";
        RequestHeader header = new RequestHeader("c.y.qq.com", "https://y.qq.com/");
        Object[] infoParams = new Object[]{
                "singermid",
                "r",
                "utf8",
                "outCharset",
                "format",
        };
        Object[] infoValues = new Object[]{
                singerMid,
                new Date(),
                1,
                "utf-8",
                "xml",
        };
        List<NameValuePair> infoParamsList = HttpDelegate.getParams(infoParams, infoValues);
        String xml = (String) HttpDelegate.sendGet(url, infoParamsList, header);
        // 解析xml 获取歌手的信息
        HashMap<String, Object> desc = resolveXML(xml);
        res.put("singerInfo", desc);

        // 获取歌手歌曲列表
        String songUrl = "https://u.y.qq.com/cgi-bin/musicu.fcg";
        String data = "{\"comm\":{\"ct\":24,\"cv\":0},\"singerSongList\":{\"method\":\"GetSingerSongList\",\"param\":{\"order\":1,\"singerMid\":\"" + singerMid + "\",\"begin\":0,\"num\":100},\"module\":\"musichall.song_list_server\"}}";
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
        JSONObject getRes = (JSONObject) HttpDelegate.sendGet(songUrl, paramsList, null);
        // 处理数据
        List<Song> songList = handleSongList(getRes);
        res.put("songList", songList);
        res.put("total", getRes.getJSONObject("singerSongList").getJSONObject("data").getInteger("totalNum"));
        return res;
    }

    // 处理歌手的歌曲列表
    private List<Song> handleSongList(JSONObject getRes) {
        JSONArray jsonArray = getRes.getJSONObject("singerSongList").getJSONObject("data").getJSONArray("songList");
        ArrayList<Song> songs = new ArrayList<>();
        jsonArray.forEach(item -> {
            JSONObject songObj = (JSONObject) item;
            JSONObject songInfo = songObj.getJSONObject("songInfo");
            Song song = new Song();
            song.setId(songInfo.getString("id"));
            song.setMid(songInfo.getString("mid"));
            song.setName(songInfo.getString("name"));
            JSONArray singers = songInfo.getJSONArray("singer");
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
            String album = songInfo.getJSONObject("album").getString("name");
            if (!StringUtils.isEmpty(album)) {
                song.setAlbum(album);
                song.setImage("https://y.gtimg.cn/music/photo_new/T002R500x500M000" + songInfo.getJSONObject("album").getString("mid") + ".jpg?max_age=2592000");
            }
            song.setDuration(songInfo.getString("interval"));
            song.setTitle(songInfo.getString("title"));
            song.setSubTitle(songInfo.getString("subtitle"));
            songs.add(song);
        });
        return songs;
    }

    // 解析xml，获取歌手信息
    private HashMap<String, Object> resolveXML(String xml) {
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(xml);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Element root = doc.getRootElement();// 指向根节点  <root>
        HashMap<String, Object> singerInfoMap = new HashMap<>();
        try {
            Element info = root.element("data").element("info");
            // 获取歌手简介
            String desc = info.element("desc").getText();
            if (desc != null) {
                singerInfoMap.put("desc", desc);
            }
            // 获取歌手基本信息
            List basicItems = info.element("basic").elements("item");
            List<String> baseInfo = new ArrayList<>();
            basicItems.forEach(item -> {
                Element itemEle = (Element) item;
                String key = itemEle.element("key").getText();
                String value = itemEle.element("value").getText();
                baseInfo.add(key + "：" + value);
            });
            singerInfoMap.put("basic", baseInfo);
            // 获取其他信息
            Element other = info.element("other");
            if (other != null) {
                List otherItems = other.elements("item");
                ArrayList<String> otherInfo = new ArrayList<>();
                otherItems.forEach(item -> {
                    Element itemEle = (Element) item;
                    String key = itemEle.element("key").getText();
                    String value = itemEle.element("value").getText();
                    otherInfo.add(key + "：" + value);
                });
                singerInfoMap.put("other", otherInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return singerInfoMap;
    }

    private HashMap<String, Object> getSingers(Integer area, Integer sex, Integer genre, Integer index, Integer sin, Integer cur_page) throws Exception {
        String url = "https://u.y.qq.com/cgi-bin/musicu.fcg";
        String data = "{\"comm\":{\"ct\":24,\"cv\":0},\"singerList\":{\"module\":\"Music.SingerListServer\",\"method\":\"get_singer_list\",\"param\":{\"area\":" + area + ",\"sex\":" + sex + ",\"genre\":" + genre + ",\"index\":" + index + ",\"sin\":" + sin + ",\"cur_page\":" + cur_page + "}}}";
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
        return handleData(getRes);
    }

    private HashMap<String, Object> handleData(JSONObject getRes) {
        JSONObject jsonObject = getRes.getJSONObject("singerList").getJSONObject("data");
        // 处理标签
        HashMap<String, Object> res = new HashMap<>();
        JSONObject tags = jsonObject.getJSONObject("tags");
        handleSingerTag(tags, Arrays.asList("area", "genre", "index", "sex"), res);
        // 处理歌手列表
        JSONArray singerList = jsonObject.getJSONArray("singerlist");
        // 处理当前点击的是什么
        res.put("area", jsonObject.getString("area"));
        res.put("genre", jsonObject.getString("genre"));
        res.put("index", jsonObject.getString("index"));
        res.put("sex", jsonObject.getString("sex"));
        handleSingerList(singerList, res);
        // 处理总数据
        res.put("total", jsonObject.getInteger("total"));
        return res;
    }

    private void handleSingerTag(JSONObject tags, List<String> types, HashMap<String, Object> res) {
        HashMap<String, ArrayList<SingerTag>> map = new HashMap<>();
        for (String type : types) {
            ArrayList<SingerTag> singerTags = new ArrayList<>();
            tags.getJSONArray(type).forEach((item) -> {
                JSONObject i = (JSONObject) item;
                SingerTag singerTag = new SingerTag();
                singerTag.setTagId(i.getString("id"));
                singerTag.setTagName(i.getString("name"));
                singerTags.add(singerTag);
            });
            map.put(type, singerTags);
        }
        res.put("tags", map);
    }

    private void handleSingerList(JSONArray singerList, HashMap<String, Object> res) {
        ArrayList<Singer> singers = new ArrayList<>();
        singerList.forEach((singerObj) -> {
            JSONObject singerJsonObj = (JSONObject) singerObj;
            Singer singer = new Singer();
            singer.setSingerMid(singerJsonObj.getString("singer_mid"));
            singer.setSingerName(singerJsonObj.getString("singer_name"));
            String singer_pic = singerJsonObj.getString("singer_pic");
            if (!StringUtils.isEmpty(singer_pic)) {
                singer.setSingerPic("https://y.gtimg.cn/music/photo_new/T001R300x300M000" + singerJsonObj.getString("singer_mid") + ".jpg?max_age=2592000");
            }
            singer.setSingerId(singerJsonObj.getInteger("singer_id"));
            singers.add(singer);
        });
        res.put("singerList", singers);
    }
}
