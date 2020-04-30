package com.topjia.music.grasp.service.search;

import com.topjia.music.grasp.entity.search.SmartSearch;

import java.util.Map;

/**
 * @author wjh
 * @date 2019-12-20 15:03
 */
public interface SearchService {
    SmartSearch smartSearch(String key) throws Exception;

    Map<String, Object> searchSong(String key, Integer num, Integer page) throws Exception;

    Map<String, Object> searchAlbum(String key, Integer num, Integer page) throws Exception;

    Map<String, Object> searchSongList(String key, Integer num, Integer page) throws Exception;

    Map<String, Object> searchLyric(String key, Integer num, Integer page) throws Exception;
}
