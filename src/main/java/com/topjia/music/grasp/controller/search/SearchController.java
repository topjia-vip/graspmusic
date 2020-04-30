package com.topjia.music.grasp.controller.search;

import com.topjia.music.grasp.entity.search.SmartSearch;
import com.topjia.music.grasp.service.search.SearchService;
import javafx.geometry.Pos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wjh
 * @date 2019-12-20 15:02
 */
@CrossOrigin
@RestController
@RequestMapping("/search")
public class SearchController {
    @Autowired
    private SearchService searchService;

    @PostMapping("/smartsearch")
    public Object smartSearch(String key) throws Exception {
        HashMap<String, Object> result = new HashMap<>();
        SmartSearch res = searchService.smartSearch(key);
        result.put("code", 0);
        result.put("data", res);
        return result;
    }

    /**
     * 单曲搜索
     */
    @PostMapping("/searchsong")
    public Object searchSong(String key, Integer num, Integer page) throws Exception {
        HashMap<String, Object> result = new HashMap<>();
        Map<String, Object> res = searchService.searchSong(key, num, page);
        result.put("code", 0);
        result.put("data", res);
        return result;
    }

    /**
     * 专辑搜索
     */
    @PostMapping("/searchalbum")
    public Object searchAlbum(String key, Integer num, Integer page) throws Exception {
        HashMap<String, Object> result = new HashMap<>();
        Map<String, Object> res = searchService.searchAlbum(key, num, page);
        result.put("code", 0);
        result.put("data", res);
        return result;
    }

    /**
     * 歌单搜索
     */
    @PostMapping("/searchsonglist")
    public Object searchSongList(String key, Integer num, Integer page) throws Exception {
        HashMap<String, Object> result = new HashMap<>();
        Map<String, Object> res = searchService.searchSongList(key, num, page);
        result.put("code", 0);
        result.put("data", res);
        return result;
    }

    /**
     * 歌词搜索
     */
    @PostMapping("/searchlyric")
    public Object searchLyric(String key, Integer num, Integer page) throws Exception {
        HashMap<String, Object> result = new HashMap<>();
        Map<String, Object> res = searchService.searchLyric(key, num, page);
        result.put("code", 0);
        result.put("data", res);
        return result;
    }
}
