package com.topjia.music.grasp.service.disst.impl;

import com.alibaba.fastjson.*;
import com.topjia.music.grasp.base.BaseParamsAndValues;
import com.topjia.music.grasp.entity.RequestHeader;
import com.topjia.music.grasp.entity.disst.Disst;
import com.topjia.music.grasp.entity.disst.Tag;
import com.topjia.music.grasp.entity.disst_category_group.DisstCategory;
import com.topjia.music.grasp.entity.disst_category_group.DisstCategoryGroup;
import com.topjia.music.grasp.entity.singer.Singer;
import com.topjia.music.grasp.entity.song.Song;
import com.topjia.music.grasp.service.disst.DisstService;
import com.topjia.music.grasp.utils.HttpDelegate;
import org.apache.http.NameValuePair;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author wjh
 * @date 2019-12-19 17:05
 */
@Service
public class DisstServiceImpl implements DisstService {
    @Override
    public List<DisstCategoryGroup> getDisstCategoryGroup() throws Exception {
        return getDisstCategoryGroupByQQYY();
    }

    @Override
    public Map<String, Object> getDisstList(Integer categoryId, Integer sortId) throws Exception {
        return getDisstListByQQYY(categoryId, sortId);
    }

    @Override
    public List<Song> getDisstSongList(String disstId) throws Exception {
        JSONObject getRes = getDisstDetailByQQYY(disstId);
        return getSongs(getRes);
    }

    @Override
    public Disst getDisstDetail(String disstId) throws Exception {
        JSONObject getRes = getDisstDetailByQQYY(disstId);
        return getDisst(getRes);
    }

    private Map<String, Object> getDisstListByQQYY(Integer categoryId, Integer sortId) throws Exception {
        String url = "https://c.y.qq.com/splcloud/fcgi-bin/fcg_get_diss_by_tag.fcg";
        RequestHeader header = new RequestHeader("c.y.qq.com", "https://c.y.qq.com/");
        Object[] params = new Object[]{
                BaseParamsAndValues.G_TK,
                BaseParamsAndValues.IN_CHAR_SET,
                BaseParamsAndValues.OUT_CHAR_SET,
                BaseParamsAndValues.FORMAT,
                BaseParamsAndValues.NOTICE,
                "platform",
                "hostUin",
                "sin",
                "ein",
                "sortId",
                "needNewCode",
                "categoryId",
                "rnd",
        };
        Object[] values = new Object[]{
                BaseParamsAndValues.G_TK_VALUE,
                BaseParamsAndValues.IN_CHAR_SET_VALUE,
                BaseParamsAndValues.OUT_CHAR_SET_VALUE,
                BaseParamsAndValues.FORMAT_VALUE,
                BaseParamsAndValues.NOTICE_VALUE,
                "yqq.json",
                "0",
                "0",
                "300",
                sortId,
                "0",
                categoryId,
                Math.random(),
        };
        List<NameValuePair> paramsList = HttpDelegate.getParams(params, values);
        // 处理请求结果
        JSONObject getRest = (JSONObject) HttpDelegate.sendGet(url, paramsList, header);
        return handleDisstListData(getRest);
    }

    private Map<String, Object> handleDisstListData(JSONObject getRest) {
        HashMap<String, Object> res = new HashMap<>();
        res.put("total", getRest.getJSONObject("data").getInteger("ein") + 1);
        JSONArray dissts = getRest.getJSONObject("data").getJSONArray("list");
        ArrayList<Disst> dissts1 = new ArrayList<>();
        dissts.forEach(disstObj -> {
            JSONObject disstJSON = (JSONObject) disstObj;
            Disst disst = new Disst();
            disst.setDisstId(disstJSON.getString("dissid"));
            disst.setDisstLogo(disstJSON.getString("imgurl"));
            disst.setDisstName(disstJSON.getString("dissname"));
            disst.setNickname(disstJSON.getJSONObject("creator").getString("name"));
            disst.setVisitnum(disstJSON.getInteger("listennum"));
            dissts1.add(disst);
        });
        res.put("dissts", dissts1);
        return res;
    }

    private List<DisstCategoryGroup> getDisstCategoryGroupByQQYY() throws Exception {
        String url = "https://c.y.qq.com/splcloud/fcgi-bin/fcg_get_diss_tag_conf.fcg";
        RequestHeader header = new RequestHeader("c.y.qq.com", "https://y.qq.com/portal/playlist.html");
        Object[] params = new Object[]{
                BaseParamsAndValues.G_TK,
                BaseParamsAndValues.IN_CHAR_SET,
                BaseParamsAndValues.OUT_CHAR_SET,
                BaseParamsAndValues.FORMAT,
                BaseParamsAndValues.NOTICE,
                "platform",
                "loginUin",
                "hostUin",
                "needNewCode",
        };
        Object[] values = new Object[]{
                BaseParamsAndValues.G_TK_VALUE,
                BaseParamsAndValues.IN_CHAR_SET_VALUE,
                BaseParamsAndValues.OUT_CHAR_SET_VALUE,
                BaseParamsAndValues.FORMAT_VALUE,
                BaseParamsAndValues.NOTICE_VALUE,
                "yqq.json",
                "0",
                "0",
                "0",
        };
        List<NameValuePair> paramsList = HttpDelegate.getParams(params, values);
        JSONObject getRest = (JSONObject) HttpDelegate.sendGet(url, paramsList, header);

        return handleDisstCategoryGroupData(getRest);
    }

    private List<DisstCategoryGroup> handleDisstCategoryGroupData(JSONObject getRest) {
        JSONArray categories = getRest.getJSONObject("data").getJSONArray("categories");
        ArrayList<DisstCategoryGroup> disstCategoryGroups = new ArrayList<>();
        categories.forEach(groupObj -> {
            JSONObject group = (JSONObject) groupObj;
            DisstCategoryGroup disstCategoryGroup = new DisstCategoryGroup();
            disstCategoryGroup.setCategoryGroupName(group.getString("categoryGroupName"));
            JSONArray items = group.getJSONArray("items");
            ArrayList<DisstCategory> disstCategories = new ArrayList<>();
            items.forEach(categoryObj -> {
                JSONObject category = (JSONObject) categoryObj;
                DisstCategory disstCategory = new DisstCategory();
                disstCategory.setCategoryId(category.getInteger("categoryId"));
                disstCategory.setCategoryName(category.getString("categoryName"));
                disstCategories.add(disstCategory);
            });
            disstCategoryGroup.setCategories(disstCategories);
            disstCategoryGroups.add(disstCategoryGroup);
        });
        return disstCategoryGroups;
    }

    private Disst getDisst(JSONObject getRes) {
        JSONObject cdlist = (JSONObject) getRes.getJSONArray("cdlist").get(0);
        Disst disst = new Disst();
        disst.setDisstId(cdlist.getString("disstid"));
        disst.setDisstName(cdlist.getString("dissname"));
        disst.setVisitnum(cdlist.getInteger("visitnum"));
        disst.setNickname(cdlist.getString("nickname"));
        disst.setDisstLogo(cdlist.getString("logo"));
        disst.setDesc(cdlist.getString("desc"));
        JSONArray tags = cdlist.getJSONArray("tags");
        ArrayList<Tag> tagArrayList = new ArrayList<>();
        tags.forEach(tagObj -> {
            JSONObject tagJSON = (JSONObject) tagObj;
            Tag tag = new Tag();
            tag.setName(tagJSON.getString("name"));
            tag.setId(tagJSON.getInteger("id"));
            tagArrayList.add(tag);
        });
        disst.setTags(tagArrayList);
        disst.setSonglist(getSongs(getRes));
        return disst;
    }

    private JSONObject getDisstDetailByQQYY(String disstId) throws Exception {
        String url = "https://c.y.qq.com/qzone/fcg-bin/fcg_ucc_getcdinfo_byids_cp.fcg";
        RequestHeader header = new RequestHeader("c.y.qq.com", "https://c.y.qq.com/");
        Object[] params = new Object[]{
                BaseParamsAndValues.G_TK,
                BaseParamsAndValues.IN_CHAR_SET,
                BaseParamsAndValues.OUT_CHAR_SET,
                BaseParamsAndValues.FORMAT,
                BaseParamsAndValues.NOTICE,
                "type",
                "json",
                "utf8",
                "onlysong",
                "new_format",
                "disstid",
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
                "1",
                "1",
                "1",
                "0",
                "1",
                disstId,
                "0",
                "0",
                "yqq.json",
                "0",
        };
        List<NameValuePair> paramsList = HttpDelegate.getParams(params, values);
        JSONObject getRes = (JSONObject) HttpDelegate.sendGet(url, paramsList, header);
        return getRes;
    }

    private List<Song> getSongs(JSONObject getRes) {
        JSONObject cdlist = (JSONObject) getRes.getJSONArray("cdlist").get(0);
        JSONArray songlist = cdlist.getJSONArray("songlist");
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
        return songs;
    }
}
