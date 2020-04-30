package com.topjia.music.grasp.controller.disst;

import com.topjia.music.grasp.entity.disst.Disst;
import com.topjia.music.grasp.entity.disst_category_group.DisstCategoryGroup;
import com.topjia.music.grasp.entity.song.Song;
import com.topjia.music.grasp.service.disst.DisstService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 歌单
 *
 * @author wjh
 * @date 2019-12-19 17:00
 */
@CrossOrigin
@RestController
@RequestMapping("/disst")
public class DisstController {
    @Autowired
    private DisstService disstService;

    /**
     * 获取歌单的分类标签
     */
    @PostMapping("/disstcategorygroup")
    public Object disstCategoryGroup() throws Exception {
        HashMap<String, Object> result = new HashMap<>();
        List<DisstCategoryGroup> categoryGroups = disstService.getDisstCategoryGroup();
        result.put("code", 0);
        result.put("categoryGroups", categoryGroups);
        return result;
    }

    /**
     * 获取标签下的歌单列表
     *
     * @param categoryId 分类id
     * @param sortId     排序id
     */
    @PostMapping("/disstlist")
    public Object disstList(Integer categoryId, Integer sortId) throws Exception {
        HashMap<String, Object> result = new HashMap<>();
        Map<String, Object> dissts = disstService.getDisstList(categoryId, sortId);
        result.put("code", 0);
        result.put("data", dissts);
        return result;
    }


    /**
     * 查看某个歌单的详情信息
     *
     * @param disstId 歌单id
     */
    @PostMapping("/disstDetail")
    public Object disstDetail(String disstId) throws Exception {
        HashMap<String, Object> result = new HashMap<>();
        Disst disst = disstService.getDisstDetail(disstId);
        result.put("code", 0);
        result.put("disst", disst);
        return result;
    }

    /**
     * 歌单详情的歌曲列表
     *
     * @param disstId 歌单id
     */
    @PostMapping("/disstsonglist")
    public Object disstSongList(String disstId) throws Exception {
        HashMap<String, Object> result = new HashMap<>();
        List<Song> songs = disstService.getDisstSongList(disstId);
        result.put("code", 0);
        result.put("songs", songs);
        return result;
    }
}
