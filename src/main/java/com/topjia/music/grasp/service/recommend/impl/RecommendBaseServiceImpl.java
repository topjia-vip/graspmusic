package com.topjia.music.grasp.service.recommend.impl;

import com.alibaba.fastjson.*;
import com.topjia.music.grasp.base.BaseParamsAndValues;
import com.topjia.music.grasp.entity.disst.Disst;
import com.topjia.music.grasp.entity.*;
import com.topjia.music.grasp.entity.recommend.RecommendPic;
import com.topjia.music.grasp.entity.recommend.RecommendSongListCategory;
import com.topjia.music.grasp.entity.singer.Singer;
import com.topjia.music.grasp.entity.song.Song;
import com.topjia.music.grasp.entity.song.SongComment;
import com.topjia.music.grasp.service.recommend.RecommendBaseService;
import com.topjia.music.grasp.utils.HttpDelegate;
import org.apache.http.NameValuePair;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sun.rmi.runtime.Log;

import java.util.*;

/**
 * @author wjh
 * @date 2019-12-08 14:28
 */
@Service
public class RecommendBaseServiceImpl implements RecommendBaseService {
    /**
     * 获取推荐页信息
     */
    @Override
    public Object GetRecommendBase() throws Exception {
        return GetRecommendBaseRes();
    }

    private Object GetRecommendBaseRes() throws Exception {
        String url = "https://u.y.qq.com/cgi-bin/musicu.fcg";
        String data = "{\"comm\":{\"ct\":24},\"category\":{\"method\":\"get_hot_category\",\"param\":{\"qq\":\"\"},\"module\":\"music.web_category_svr\"},\"recomPlaylist\":{\"method\":\"get_hot_recommend\",\"param\":{\"async\":1,\"cmd\":2},\"module\":\"playlist.HotRecommendServer\"},\"new_song\":{\"module\":\"newsong.NewSongServer\",\"method\":\"get_new_song_info\",\"param\":{\"type\":5}},\"focus\":{\"module\":\"QQMusic.MusichallServer\",\"method\":\"GetFocus\",\"param\":{}}}";
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
        // 处理合并数据
        // 推荐歌单分类
        JSONArray categoryArray = getRes.getJSONObject("category").getJSONObject("data").getJSONArray("category");
        Object o = categoryArray.get(0);
        JSONArray items = ((JSONObject) o).getJSONArray("items");
        HashMap<String, Object> res = new HashMap<>();
        ArrayList<RecommendSongListCategory> recommendSongListCategories = new ArrayList<>();
        for (int i = 3; i < 8; i++) {
            JSONObject categoryObj = (JSONObject) items.get(i);
            RecommendSongListCategory recommendSongListCategory = new RecommendSongListCategory();
            recommendSongListCategory.setCategoryId(categoryObj.getString("item_id"));
            recommendSongListCategory.setCategoryName(categoryObj.getString("item_name"));
            recommendSongListCategories.add(recommendSongListCategory);
        }
        res.put("category", recommendSongListCategories);
        // 处理轮播图
        JSONArray focusArray = getRes.getJSONObject("focus").getJSONObject("data").getJSONArray("content");
        ArrayList<RecommendPic> recommendPics = new ArrayList<>();
        for (int i = 0; i < focusArray.size(); i++) {
            JSONObject obj = (JSONObject) focusArray.get(i);
            String jump_info = obj.getJSONObject("jump_info").getString("url");
            if (!jump_info.contains("https")) {
                jump_info = "https://y.qq.com/n/yqq/album/" + jump_info + ".html";
            }
            // 重新封装结果返回给前端
            RecommendPic recommendPic = new RecommendPic();
            recommendPic.setJumpInfo(jump_info);
            String pic_info = obj.getJSONObject("pic_info").getString("url");
            recommendPic.setPicInfo(pic_info);
            recommendPics.add(recommendPic);
        }
        res.put("focus", recommendPics);
        // 处理推荐歌单列表
        JSONArray recomDisstlistArray = getRes.getJSONObject("recomPlaylist").getJSONObject("data").getJSONArray("v_hot");
        ArrayList<Disst> dissts = new ArrayList<>();
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
        // 处理分类
        res.put("dissts", dissts);
        JSONArray lans = getRes.getJSONObject("new_song").getJSONObject("data").getJSONArray("lanlist");
        HashMap<String, Object> lanAndSongs = new HashMap<>();
        ArrayList<Lan> lanLists = new ArrayList<>();
        for (Object lano : lans) {
            JSONObject lanObj = (JSONObject) lano;
            Lan lan = new Lan();
            lan.setLanName(lanObj.getString("lan"));
            lan.setLanType(lanObj.getString("type"));
            lanLists.add(lan);
        }
        res.put("lanList", lanLists);
        //处理歌曲
        JSONArray songlist = getRes.getJSONObject("new_song").getJSONObject("data").getJSONArray("songlist");
        ArrayList<Song> songs = new ArrayList<>();
        for (Object o1 : songlist) {
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
        res.put("new_song", songs);
        return res;
    }

    private Song getSongDetail(String songId) throws Exception {
        String url = "https://u.y.qq.com/cgi-bin/musicu.fcg";
        String data = "{\"comm\":{\"ct\":24,\"cv\":0},\"songinfo\":{\"method\":\"get_song_detail_yqq\",\"param\":{\"song_type\":0,\"song_mid\":\"\",\"song_id\":" + songId + "},\"module\":\"music.pf_song_detail_svr\"}}";
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
        JSONObject songObj = getRes.getJSONObject("songinfo").getJSONObject("data").getJSONObject("track_info");
        Song song = new Song();
        song.setId(songObj.getString("id"));
        song.setMid(songObj.getString("mid"));
        JSONArray singers = songObj.getJSONArray("singer");
        ArrayList<Singer> singerArrayList = new ArrayList<>();
        for (Object singero : singers) {
            JSONObject singerObj = (JSONObject) singero;
            Singer singer = new Singer();
            singer.setSingerMid(singerObj.getString("mid"));
            singer.setSingerName(singerObj.getString("name"));
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
        return song;
    }

    private List<SongComment> getSongComment(String songId) throws Exception {
        String url = "https://c.y.qq.com/base/fcgi-bin/fcg_global_comment_h5.fcg";
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
                "reqtype",
                "biztype",
                "topid",
                "cmd",
                "needmusiccrit",
                "pagenum",
                "pagesize",
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
                "2",
                "1",
                songId,
                "8",
                "0",
                "0",
                "25",
        };
        List<NameValuePair> paramsList = HttpDelegate.getParams(params, values);
        JSONObject getRes = (JSONObject) HttpDelegate.sendGet(url, paramsList, null);
        JSONArray jsonArray = getRes.getJSONObject("hot_comment").getJSONArray("commentlist");
        ArrayList<SongComment> songComments = new ArrayList<>();
        for (Object o : jsonArray) {
            JSONObject commentObj = (JSONObject) o;
            SongComment songComment = new SongComment();
            songComment.setContent(commentObj.getString("rootcommentcontent"));
            songComment.setNick(commentObj.getString("nick"));
            songComments.add(songComment);
        }
        return songComments;
    }
}
