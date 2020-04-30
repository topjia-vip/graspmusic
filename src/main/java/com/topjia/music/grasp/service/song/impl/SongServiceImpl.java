package com.topjia.music.grasp.service.song.impl;

import com.alibaba.fastjson.*;
import com.topjia.music.grasp.base.BaseParamsAndValues;
import com.topjia.music.grasp.entity.Lyric;
import com.topjia.music.grasp.entity.RequestHeader;
import com.topjia.music.grasp.entity.singer.Singer;
import com.topjia.music.grasp.entity.song.Song;
import com.topjia.music.grasp.service.song.SongService;
import com.topjia.music.grasp.utils.HttpDelegate;
import org.apache.http.NameValuePair;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author wjh
 * @date 2020-01-04 14:15
 */
@Service
public class SongServiceImpl implements SongService {
    @Override
    public String getSongPurl(String songMid) throws Exception {
        return getSongPurlByQQYY(songMid);
    }

    @Override
    public Lyric getLyric(String songmid) throws Exception {
        Lyric lyric = getSongLyric(songmid);
        return lyric;
    }

    @Override
    public HashMap<String, Object> getSongDetail(String songId, String songMid) throws Exception {
        return getSongDetailByQQYY(songId, songMid);
    }

    @Override
    public List<String> getSongDetailLyric(String songId) throws Exception {
        return getSongDetailLyricByQQYY(songId);
    }

    private List<String> getSongDetailLyricByQQYY(String songId) throws Exception {
        String url = "https://c.y.qq.com/lyric/fcgi-bin/fcg_query_lyric_yqq.fcg";
        RequestHeader header = new RequestHeader("c.y.qq.com", "https://c.y.qq.com/");
        Object[] params = new Object[]{
                BaseParamsAndValues.G_TK,
                BaseParamsAndValues.IN_CHAR_SET,
                BaseParamsAndValues.OUT_CHAR_SET,
                BaseParamsAndValues.FORMAT,
                BaseParamsAndValues.NOTICE,
                "musicid",
                "nobase64",
                "platform",
                "hostUin",
                "needNewCode",
                "loginUin",
        };
        Object[] values = new Object[]{
                BaseParamsAndValues.G_TK_VALUE,
                BaseParamsAndValues.IN_CHAR_SET_VALUE,
                BaseParamsAndValues.OUT_CHAR_SET_VALUE,
                BaseParamsAndValues.FORMAT_VALUE,
                BaseParamsAndValues.NOTICE_VALUE,
                songId,
                1,
                "yqq.json",
                "0",
                "0",
                "0",
        };
        List<NameValuePair> paramsList = HttpDelegate.getParams(params, values);
        JSONObject o = (JSONObject) HttpDelegate.sendGet(url, paramsList, header);
        ArrayList<String> strings = new ArrayList<>();
        String lyric = o.getString("lyric");
        strings.add(lyric);
        return strings;
    }

    private HashMap<String, Object> getSongDetailByQQYY(String songId, String songMid) throws Exception {
        String url = "https://u.y.qq.com/cgi-bin/musicu.fcg";
        String data = "{\"comm\":{\"ct\":24,\"cv\":0},\"songinfo\":{\"method\":\"get_song_detail_yqq\",\"param\":{\"song_type\":0,\"song_mid\":\"" + songMid + "\",\"song_id\":" + songId + "},\"module\":\"music.pf_song_detail_svr\"}}";
        JSONObject parse = JSONObject.parseObject(data);
        RequestHeader header = new RequestHeader("u.y.qq.com", "https://u.y.qq.com/");
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
                JSON.toJSON(parse),
        };
        List<NameValuePair> paramsList = HttpDelegate.getParams(params, values);
        JSONObject getRes = (JSONObject) HttpDelegate.sendGet(url, paramsList, header);
        return handleSongData(getRes);
    }

    private HashMap<String, Object> handleSongData(JSONObject getRes) {
        // 处理基本信息
        JSONObject info = getRes.getJSONObject("songinfo").getJSONObject("data").getJSONObject("info");
        HashMap<String, Object> res = new HashMap<>();
        // 唱片公司
        JSONObject company = info.getJSONObject("company");
        ArrayList<String> companyList = new ArrayList<>();
        if (company != null) {
            JSONArray companyContent = company.getJSONArray("content");
            for (Object item : companyContent) {
                JSONObject i = (JSONObject) item;
                companyList.add(i.getString("value"));
            }
        } else {
            companyList.add("暂无");
        }
        res.put("company", companyList);

        // 歌曲语种
        JSONObject lan = info.getJSONObject("lan");
        ArrayList<String> lanList = new ArrayList<>();
        if (lan != null) {
            JSONArray lanContent = lan.getJSONArray("content");
            for (Object item : lanContent) {
                JSONObject i = (JSONObject) item;
                lanList.add(i.getString("value"));
            }
        } else {
            lanList.add("暂无");
        }
        res.put("lan", lanList);

        // 歌曲发行时间
        JSONObject pub_time = info.getJSONObject("pub_time");
        ArrayList<String> time = new ArrayList<>();
        if (pub_time != null) {
            JSONArray timeContent = pub_time.getJSONArray("content");
            for (Object item : timeContent) {
                JSONObject i = (JSONObject) item;
                time.add(i.getString("value"));
            }
        } else {
            time.add("暂无");
        }
        res.put("pub_time", time);

        // 歌曲流派
        JSONObject genre = info.getJSONObject("genre");
        ArrayList<String> genreList = new ArrayList<>();
        if (genre != null) {
            JSONArray genreContent = genre.getJSONArray("content");
            for (Object item : genreContent) {
                JSONObject i = (JSONObject) item;
                genreList.add(i.getString("value"));
            }
        } else {
            genreList.add("暂无");
        }
        res.put("genre", genreList);

        // 封装歌曲信息
        JSONObject songInfo = getRes.getJSONObject("songinfo").getJSONObject("data").getJSONObject("track_info");
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
        if (StringUtils.isEmpty(song.getAlbum())) {
            res.put("album", "暂无");
        } else {
            res.put("album", Arrays.asList(song.getAlbum()));
        }
        res.put("song", song);
        return res;
    }

    private Lyric getSongLyric(String songmid) throws Exception {
        Lyric lyric;
        String url = "https://c.y.qq.com/lyric/fcgi-bin/fcg_query_lyric_new.fcg";
        RequestHeader header = new RequestHeader("c.y.qq.com", "https://c.y.qq.com/");
        Object[] params = new Object[]{
                BaseParamsAndValues.G_TK,
                BaseParamsAndValues.IN_CHAR_SET,
                BaseParamsAndValues.OUT_CHAR_SET,
                BaseParamsAndValues.FORMAT,
                BaseParamsAndValues.NOTICE,
                "songmid",
                "platform",
                "hostUin",
                "needNewCode",
                "categoryId",
                "pcachetime",
        };
        Object[] values = new Object[]{
                BaseParamsAndValues.G_TK_VALUE,
                BaseParamsAndValues.IN_CHAR_SET_VALUE,
                BaseParamsAndValues.OUT_CHAR_SET_VALUE,
                BaseParamsAndValues.FORMAT_VALUE,
                BaseParamsAndValues.NOTICE_VALUE,
                songmid,
                "yqq",
                "0",
                "0",
                "10000000",
                new Date(),
        };
        List<NameValuePair> paramsList = HttpDelegate.getParams(params, values);
        JSONObject o = (JSONObject) HttpDelegate.sendGet(url, paramsList, header);
        lyric = new Lyric();
        lyric.setLyric(o.getString("lyric"));
        String trans = o.getString("trans");
        if (!StringUtils.isEmpty(trans)) {
            lyric.setTran(trans);
        }
        return lyric;
    }

    private String getSongPurlByQQYY(String songMid) throws Exception {
        String url = "https://u.y.qq.com/cgi-bin/musicu.fcg";
        String data = "{\"req_0\":{\"module\":\"vkey.GetVkeyServer\",\"method\":\"CgiGetVkey\",\"param\":{\"guid\":\"7275231575\",\"songmid\":[\"" + songMid + "\"],\"songtype\":[0],\"uin\":\"1256957450\",\"loginflag\":1,\"platform\":\"20\"}}}";
        JSONObject parse = JSONObject.parseObject(data);
        RequestHeader header = new RequestHeader("u.y.qq.com", "https://u.y.qq.com/");
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
                JSON.toJSON(parse),
        };
        List<NameValuePair> paramsList = HttpDelegate.getParams(params, values);
        JSONObject getRes = (JSONObject) HttpDelegate.sendGet(url, paramsList, header);
        return handleData(getRes);
    }

    private String handleData(JSONObject getRes) {
        // 封装音乐
        JSONObject o = (JSONObject) getRes.getJSONObject("req_0").getJSONObject("data").getJSONArray("midurlinfo").get(0);
        if (StringUtils.isEmpty(o.getString("purl"))) {
            return "";
        }
        return "http://123.184.152.16/amobile.music.tc.qq.com/" + o.getString("purl");
    }
}
