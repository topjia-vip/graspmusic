package com.topjia.music.grasp.service.search.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.topjia.music.grasp.base.BaseParamsAndValues;
import com.topjia.music.grasp.entity.RequestHeader;
import com.topjia.music.grasp.entity.album.Album;
import com.topjia.music.grasp.entity.disst.Disst;
import com.topjia.music.grasp.entity.search.SmartSearch;
import com.topjia.music.grasp.entity.singer.Singer;
import com.topjia.music.grasp.entity.song.Song;
import com.topjia.music.grasp.service.search.SearchService;
import com.topjia.music.grasp.utils.HttpDelegate;
import org.apache.http.NameValuePair;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sun.rmi.runtime.Log;

import java.util.*;

/**
 * @author wjh
 * @date 2019-12-20 15:03
 */
@Service
public class SearchServiceImpl implements SearchService {
    @Override
    public SmartSearch smartSearch(String key) throws Exception {
        return smartSearchByQQYY(key);
    }

    @Override
    public Map<String, Object> searchSong(String key, Integer num, Integer page) throws Exception {
        return searchSongByQQYY(key, num, page);
    }

    @Override
    public Map<String, Object> searchAlbum(String key, Integer num, Integer page) throws Exception {
        return searchAlbumByQQYY(key, num, page);
    }

    @Override
    public Map<String, Object> searchSongList(String key, Integer num, Integer page) throws Exception {
        return searchSongListByQQYY(key, num, page);
    }

    @Override
    public Map<String, Object> searchLyric(String key, Integer num, Integer page) throws Exception {
        return searchLyricByQQYY(key, num, page);
    }

    private Map<String, Object> searchLyricByQQYY(String key, Integer num, Integer page) throws Exception {
        String url = "https://c.y.qq.com/soso/fcgi-bin/client_search_cp";
        RequestHeader header = new RequestHeader("c.y.qq.com", "https://y.qq.com/portal/search.html");
        Object[] params = new Object[]{
                BaseParamsAndValues.G_TK,
                BaseParamsAndValues.IN_CHAR_SET,
                BaseParamsAndValues.OUT_CHAR_SET,
                BaseParamsAndValues.FORMAT,
                BaseParamsAndValues.NOTICE,
                "w",
                "p",
                "n",
                "catZhida",
                "t",
                "aggr",
                "lossless",
                "sem",
                "remoteplace",
                "needNewCode",
                "platform",
                "hostUin",
                "loginUin",
        };
        Object[] values = new Object[]{
                BaseParamsAndValues.G_TK_VALUE,
                BaseParamsAndValues.IN_CHAR_SET_VALUE,
                BaseParamsAndValues.OUT_CHAR_SET_VALUE,
                BaseParamsAndValues.FORMAT_VALUE,
                BaseParamsAndValues.NOTICE_VALUE,
                key,
                page,
                num,
                1,
                "7",
                "0",
                "0",
                "1",
                "txt.yqq.lyric",
                "0",
                "yqq.json",
                "0",
                "0",
        };
        List<NameValuePair> paramsList = HttpDelegate.getParams(params, values);
        JSONObject getRes = (JSONObject) HttpDelegate.sendGet(url, paramsList, header);
        return handleSearchLyricData(getRes);
    }

    private Map<String, Object> handleSearchLyricData(JSONObject getRes) {
        HashMap<String, Object> res = new HashMap<>();
        String message = getRes.getString("message");
        if (message.equals("engine")) {
            res.put("message", "no_results");
            res.put("totalnum", 0);
        } else if (message.equals("system error")) {
            res.put("message", "QQ音乐服务器错误");
            res.put("totalnum", 0);
        } else {
            JSONObject lyric = getRes.getJSONObject("data").getJSONObject("lyric");
            res.put("curnum", lyric.getInteger("curnum"));
            res.put("curpage", lyric.getInteger("curpage"));
            res.put("totalnum", lyric.getInteger("totalnum"));
            JSONArray lyricList = lyric.getJSONArray("list");
            res.put("showData", lyricList);
            // 处理歌词结果的歌曲信息
            ArrayList<Song> songs = new ArrayList<>();
            lyricList.forEach(item -> {
                JSONObject songJSON = (JSONObject) item;
                Song song = new Song();
                song.setId(songJSON.getString("songid"));
                song.setMid(songJSON.getString("songmid"));
                song.setName(songJSON.getString("songname"));
                JSONArray singers = songJSON.getJSONArray("singer");
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
                String album = songJSON.getString("albumname");
                if (!StringUtils.isEmpty(album)) {
                    song.setAlbum(album);
                    song.setImage("https://y.gtimg.cn/music/photo_new/T002R500x500M000" + songJSON.getString("albummid") + ".jpg?max_age=2592000");
                }
                song.setDuration(songJSON.getString("interval"));
                songs.add(song);
            });
            res.put("songs", songs);
        }
        return res;
    }

    private Map<String, Object> searchSongListByQQYY(String key, Integer num, Integer page) throws Exception {
        String url = "https://c.y.qq.com/soso/fcgi-bin/client_music_search_songlist";
        RequestHeader header = new RequestHeader("c.y.qq.com", "https://y.qq.com/portal/search.html");
        Object[] params = new Object[]{
                BaseParamsAndValues.G_TK,
                BaseParamsAndValues.IN_CHAR_SET,
                BaseParamsAndValues.OUT_CHAR_SET,
                BaseParamsAndValues.FORMAT,
                BaseParamsAndValues.NOTICE,
                "query",
                "page_no",
                "num_per_page",
                "remoteplace",
                "needNewCode",
                "platform",
                "hostUin",
                "loginUin",
        };
        Object[] values = new Object[]{
                BaseParamsAndValues.G_TK_VALUE,
                BaseParamsAndValues.IN_CHAR_SET_VALUE,
                BaseParamsAndValues.OUT_CHAR_SET_VALUE,
                BaseParamsAndValues.FORMAT_VALUE,
                BaseParamsAndValues.NOTICE_VALUE,
                key,
                page - 1,
                num,
                "txt.yqq.playlist",
                "0",
                "yqq.json",
                "0",
                "0",
        };
        List<NameValuePair> paramsList = HttpDelegate.getParams(params, values);
        JSONObject getRes = (JSONObject) HttpDelegate.sendGet(url, paramsList, header);
        return handleSearchSongListData(getRes);
    }

    private Map<String, Object> handleSearchSongListData(JSONObject getRes) {
        HashMap<String, Object> res = new HashMap<>();
        String message = getRes.getString("message");
        if (message.equals("engine")) {
            res.put("message", "no_results");
            res.put("totalnum", 0);
        } else if (message.equals("system error")) {
            res.put("message", "QQ音乐服务器错误");
            res.put("totalnum", 0);
        } else {
            JSONObject songList = getRes.getJSONObject("data");
            res.put("curnum", songList.getInteger("num_per_page"));
            res.put("curpage", songList.getInteger("page_no") + 1);
            res.put("totalnum", songList.getInteger("sum"));
            // 处理歌单
            JSONArray songListJSONArray = songList.getJSONArray("list");
            ArrayList<Disst> dissts = new ArrayList<>();
            songListJSONArray.forEach(songListObj -> {
                JSONObject disstJSON = (JSONObject) songListObj;
                Disst disst = new Disst();
                disst.setDisstId(disstJSON.getString("dissid"));
                disst.setDisstLogo(disstJSON.getString("imgurl"));
                disst.setDesc(disstJSON.getString("introduction"));
                disst.setSongNum(disstJSON.getInteger("song_count"));
                disst.setDisstName(disstJSON.getString("dissname"));
                disst.setNickname(disstJSON.getJSONObject("creator").getString("name"));
                disst.setVisitnum(disstJSON.getInteger("listennum"));
                dissts.add(disst);
            });
            res.put("dissts", dissts);
        }
        return res;
    }

    private Map<String, Object> searchAlbumByQQYY(String key, Integer num, Integer page) throws Exception {
        String url = "https://c.y.qq.com/soso/fcgi-bin/client_search_cp";
        RequestHeader header = new RequestHeader("c.y.qq.com", "https://y.qq.com/portal/search.html");
        Object[] params = new Object[]{
                BaseParamsAndValues.G_TK,
                BaseParamsAndValues.IN_CHAR_SET,
                BaseParamsAndValues.OUT_CHAR_SET,
                BaseParamsAndValues.FORMAT,
                BaseParamsAndValues.NOTICE,
                "w",
                "p",
                "n",
                "catZhida",
                "t",
                "aggr",
                "lossless",
                "sem",
                "remoteplace",
                "needNewCode",
                "platform",
                "hostUin",
                "loginUin",
        };
        Object[] values = new Object[]{
                BaseParamsAndValues.G_TK_VALUE,
                BaseParamsAndValues.IN_CHAR_SET_VALUE,
                BaseParamsAndValues.OUT_CHAR_SET_VALUE,
                BaseParamsAndValues.FORMAT_VALUE,
                BaseParamsAndValues.NOTICE_VALUE,
                key,
                page,
                num,
                1,
                "8",
                "0",
                "0",
                "10",
                "txt.yqq.album",
                "0",
                "yqq.json",
                "0",
                "0",
        };
        List<NameValuePair> paramsList = HttpDelegate.getParams(params, values);
        JSONObject getRes = (JSONObject) HttpDelegate.sendGet(url, paramsList, header);
        return handleSearchAlbumData(getRes);
    }

    private Map<String, Object> handleSearchAlbumData(JSONObject getRes) {
        HashMap<String, Object> res = new HashMap<>();
        String message = getRes.getString("message");
        if (message.equals("no results")) {
            res.put("message", "no_results");
            res.put("totalnum", 0);
        } else if (message.equals("system error")) {
            res.put("message", "QQ音乐服务器错误");
            res.put("totalnum", 0);
        } else {
            JSONObject album = getRes.getJSONObject("data").getJSONObject("album");
            res.put("curnum", album.getInteger("curnum"));
            res.put("curpage", album.getInteger("curpage"));
            res.put("totalnum", album.getInteger("totalnum"));
            JSONArray albumJSONArray = album.getJSONArray("list");
            res.put("dataShowList", albumJSONArray);
            // 处理专辑信息
            ArrayList<Album> albums = new ArrayList<>();
            albumJSONArray.forEach(item -> {
                JSONObject albumJSON = (JSONObject) item;
                Album album1 = new Album();
                album1.setId(albumJSON.getInteger("albumID"));
                album1.setMid(albumJSON.getString("albumMID"));
                album1.setName(albumJSON.getString("albumName"));
                album1.setPubTime(albumJSON.getString("publicTime"));
                album1.setImage("http://y.gtimg.cn/music/photo_new/T002R500x500M0000" + album1.getMid() + "_1.jpg");
                album1.setCurrentSongNum(albumJSON.getInteger("song_count"));
                JSONArray singer_list = albumJSON.getJSONArray("singer_list");
                ArrayList<Singer> singers = new ArrayList<>();
                singer_list.forEach(singerObj -> {
                    JSONObject singerJson = (JSONObject) singerObj;
                    Singer singer = new Singer();
                    singer.setSingerId(singerJson.getInteger("id"));
                    singer.setSingerMid(singerJson.getString("mid"));
                    singer.setSingerName(singerJson.getString("name"));
                    singer.setSingerPic("https://y.gtimg.cn/music/photo_new/T001R300x300M000" + singerJson.getString("mid") + ".jpg?max_age=2592000");
                    singers.add(singer);
                });
                album1.setSingers(singers);
                albums.add(album1);
            });
            res.put("albums", albums);
            res.put("message", "success");
        }
        return res;
    }

    private Map<String, Object> searchSongByQQYY(String key, Integer num, Integer page) throws Exception {
        String url = "https://c.y.qq.com/soso/fcgi-bin/client_search_cp";
        RequestHeader header = new RequestHeader("c.y.qq.com", "https://y.qq.com/portal/search.html");
        Object[] params = new Object[]{
                BaseParamsAndValues.G_TK,
                BaseParamsAndValues.IN_CHAR_SET,
                BaseParamsAndValues.OUT_CHAR_SET,
                BaseParamsAndValues.FORMAT,
                BaseParamsAndValues.NOTICE,
                "w",
                "p",
                "n",
                "catZhida",
                "zhidaqu",
                "t",
                "flag",
                "ie",
                "sem",
                "remoteplace",
                "needNewCode",
                "platform",
                "uin",
        };
        Object[] values = new Object[]{
                BaseParamsAndValues.G_TK_VALUE,
                BaseParamsAndValues.IN_CHAR_SET_VALUE,
                BaseParamsAndValues.OUT_CHAR_SET_VALUE,
                BaseParamsAndValues.FORMAT_VALUE,
                BaseParamsAndValues.NOTICE_VALUE,
                key,
                page,
                num,
                1,
                "1",
                "0",
                "1",
                "utf-8",
                "1",
                "txt.yqq.song",
                "0",
                "0",
                "yqq.json",
        };
        List<NameValuePair> paramsList = HttpDelegate.getParams(params, values);
        JSONObject getRes = (JSONObject) HttpDelegate.sendGet(url, paramsList, header);
        return handleSearchSongData(getRes);
    }

    private Map<String, Object> handleSearchSongData(JSONObject getRes) {
        HashMap<String, Object> res = new HashMap<>();
        String message = getRes.getString("message");
        if (message.equals("no results")) {
            res.put("message", "no_results");
            res.put("totalnum", 0);
        } else if (message.equals("system error")) {
            res.put("message", "QQ音乐服务器错误");
            res.put("totalnum", 0);
        } else {
            JSONObject songInfo = getRes.getJSONObject("data").getJSONObject("song");
            // 处理基本数据
            res.put("curnum", songInfo.getInteger("curnum"));
            res.put("curpage", songInfo.getInteger("curpage"));
            res.put("totalnum", songInfo.getInteger("totalnum"));
            // 处理歌曲和高亮字体
            JSONArray songArr = songInfo.getJSONArray("list");
            ArrayList<Song> songs = new ArrayList<>();
            ArrayList<Object> hiLights = new ArrayList<>();
            songArr.forEach(item -> {
                HashMap<String, Object> hiLight = new HashMap<>();
                // 歌曲
                Song song = new Song();
                JSONObject songJSON = (JSONObject) item;
                song.setId(songJSON.getString("songid"));
                song.setMid(songJSON.getString("songmid"));
                song.setName(songJSON.getString("songname"));
                JSONArray singers = songJSON.getJSONArray("singer");
                ArrayList<Singer> singerArrayList = new ArrayList<>();
                ArrayList<HashMap<String, String>> singerHiLight = new ArrayList<>();
                for (Object singero : singers) {
                    JSONObject singerObj = (JSONObject) singero;
                    Singer singer = new Singer();
                    HashMap<String, String> singerNameHiLight = new HashMap<>();
                    singer.setSingerId(singerObj.getInteger("id"));
                    singer.setSingerMid(singerObj.getString("mid"));
                    singer.setSingerName(singerObj.getString("name"));
                    singer.setSingerPic("https://y.gtimg.cn/music/photo_new/T001R300x300M000" + singerObj.getString("mid") + ".jpg?max_age=2592000");
                    singerArrayList.add(singer);
                    singerNameHiLight.put("singer_name_hiLight", singerObj.getString("name_hilight"));
                    singerNameHiLight.put("singer_name", singer.getSingerName());
                    singerHiLight.add(singerNameHiLight);
                }
                song.setSingers(singerArrayList);
                String album = songJSON.getString("albumname");
                if (!StringUtils.isEmpty(album)) {
                    song.setAlbum(album);
                    song.setImage("https://y.gtimg.cn/music/photo_new/T002R500x500M000" + songJSON.getString("albummid") + ".jpg?max_age=2592000");
                }
                song.setDuration(songJSON.getString("interval"));
                songs.add(song);
                // 高亮
                hiLight.put("singer_hilight", singerHiLight);
                hiLight.put("songname_hilight", songJSON.getString("songname_hilight"));
                hiLight.put("albumname_hilight", songJSON.getString("albumname_hilight"));
                hiLight.put("song_image", song.getImage());
                hiLight.put("song_name", song.getName());
                hiLight.put("song_album", song.getAlbum());
                hiLights.add(hiLight);
            });
            res.put("songList", songs);
            res.put("hiLights", hiLights);
            // 处理直达歌手
            JSONObject zhida = getRes.getJSONObject("data").getJSONObject("zhida");
            if (zhida != null) {
                JSONObject zhida_singer = zhida.getJSONObject("zhida_singer");
                if (zhida_singer != null) {
                    HashMap<String, Object> zhidaMap = new HashMap<>();
                    zhidaMap.put("mvNum", zhida_singer.getString("mvNum"));
                    zhidaMap.put("albumNum", zhida_singer.getString("albumNum"));
                    zhidaMap.put("songNum", zhida_singer.getString("songNum"));
                    Singer singer = new Singer();
                    singer.setSingerId(zhida_singer.getInteger("singerID"));
                    singer.setSingerName(zhida_singer.getString("singerName"));
                    singer.setSingerMid(zhida_singer.getString("singerMID"));
                    singer.setSingerPic("https://y.gtimg.cn/music/photo_new/T001R300x300M000" + zhida_singer.getString("singerMID") + ".jpg?max_age=2592000");
                    zhidaMap.put("singer", singer);
                    res.put("zhida", zhidaMap);
                } else {
                    res.put("zhida", null);
                }
            } else {
                res.put("zhida", null);
            }
            res.put("message", "success");
        }
        return res;
    }

    private SmartSearch smartSearchByQQYY(String key) throws Exception {
        String url = "https://c.y.qq.com/splcloud/fcgi-bin/smartbox_new.fcg";
        RequestHeader header = new RequestHeader("c.y.qq.com", "https://y.qq.com/");
        Object[] params = new Object[]{
                BaseParamsAndValues.G_TK,
                BaseParamsAndValues.IN_CHAR_SET,
                BaseParamsAndValues.OUT_CHAR_SET,
                BaseParamsAndValues.FORMAT,
                BaseParamsAndValues.NOTICE,
                "key",
                "loginUin",
                "hostUin",
                "platform",
                "needNewCode",
        };
        Object[] values = new Object[]{
                BaseParamsAndValues.G_TK_VALUE,
                BaseParamsAndValues.IN_CHAR_SET_VALUE,
                BaseParamsAndValues.OUT_CHAR_SET_VALUE,
                BaseParamsAndValues.FORMAT_VALUE,
                BaseParamsAndValues.NOTICE_VALUE,
                key,
                "0",
                "0",
                "yqq.json",
                "0",
        };
        List<NameValuePair> paramsList = HttpDelegate.getParams(params, values);
        JSONObject getRes = (JSONObject) HttpDelegate.sendGet(url, paramsList, header);
        return handleSmartSearchData(getRes);
    }

    private SmartSearch handleSmartSearchData(JSONObject getRes) {
        // 处理歌手
        JSONArray singerArr = getRes.getJSONObject("data").getJSONObject("singer").getJSONArray("itemlist");
        SmartSearch smartSearch = new SmartSearch();
        ArrayList<Singer> singers = new ArrayList<>();
        singerArr.forEach(singerObj -> {
            JSONObject singerJSON = (JSONObject) singerObj;
            Singer singer = new Singer();
            singer.setSingerId(singerJSON.getInteger("id"));
            singer.setSingerMid(singerJSON.getString("mid"));
            singer.setSingerName(singerJSON.getString("name"));
            singer.setSingerPic("https://y.gtimg.cn/music/photo_new/T001R300x300M000" + singerJSON.getString("mid") + ".jpg?max_age=2592000");
            singers.add(singer);
        });
        smartSearch.setSingers(singers);

        JSONArray songArr = getRes.getJSONObject("data").getJSONObject("song").getJSONArray("itemlist");
        ArrayList<Song> songs = new ArrayList<>();
        songArr.forEach(songObj -> {
            JSONObject songJSON = (JSONObject) songObj;
            Song song = new Song();
            song.setId(songJSON.getString("id"));
            song.setMid(songJSON.getString("mid"));
            song.setName(songJSON.getString("name"));
            Singer singer = new Singer();
            singer.setSingerName(songJSON.getString("singer"));
            song.setSingers(Arrays.asList(singer));
            songs.add(song);
        });
        smartSearch.setSongs(songs);
        return smartSearch;
    }
}
